/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.dao.CompanyTalkGroupDao;
import com.skyeye.eve.dao.CompanyTalkGroupUserDao;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroupUser;
import com.skyeye.eve.service.CompanyTalkGroupUserService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CompanyTalkGroupUserServiceImpl
 * @Description: 群组用户服务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/28 17:16
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "群组用户", groupName = "聊天模块")
public class CompanyTalkGroupUserServiceImpl extends SkyeyeBusinessServiceImpl<CompanyTalkGroupUserDao, CompanyTalkGroupUser> implements CompanyTalkGroupUserService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private JedisClientService jedisClientService;

    @Autowired
    private CompanyTalkGroupDao companyTalkGroupDao;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    public List<CompanyTalkGroupUser> selectByGroupId(String groupId) {
        QueryWrapper<CompanyTalkGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getGroupId), groupId);
        return list(queryWrapper);
    }

    @Override
    public List<String> selectMemberUserIdsByGroupIdWithCache(String groupId) {
        if (StrUtil.isBlank(groupId)) {
            return Collections.emptyList();
        }
        String cacheKey = buildMemberUserIdsCacheKey(groupId);
        List<String> userIds = redisCache.getList(cacheKey, key -> {
            List<String> loaded = loadMemberUserIds(groupId);
            return CollectionUtil.isEmpty(loaded) ? null : loaded;
        }, RedisConstants.ALL_USE_TIME, String.class);
        return userIds;
    }

    private String buildMemberUserIdsCacheKey(String groupId) {
        if (!tenantEnable) {
            return Constants.checkSysEveTalkGroupUserIdsByGroupId(groupId);
        }
        String tenantId = StrUtil.nullToEmpty(TenantContext.getTenantId());
        return Constants.checkSysEveTalkGroupUserIdsByGroupId(groupId + "_" + tenantId);
    }

    private List<String> loadMemberUserIds(String groupId) {
        String tenantId = tenantEnable ? StrUtil.nullToEmpty(TenantContext.getTenantId()) : StrUtil.EMPTY;
        List<Map<String, Object>> members = companyTalkGroupDao.queryGroupMemberByGroupId(groupId, tenantId);
        if (CollectionUtil.isEmpty(members)) {
            return Collections.emptyList();
        }
        return members.stream()
            .map(member -> member.get("id").toString())
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
    }

    private boolean isGroupMember(List<String> memberUserIds, String userId) {
        if (StrUtil.isBlank(userId) || CollectionUtil.isEmpty(memberUserIds)) {
            return false;
        }
        return memberUserIds.stream().anyMatch(memberUserId -> StrUtil.equals(memberUserId, userId));
    }

    @Override
    public void evictGroupMemberCache(String groupId) {
        if (StrUtil.isBlank(groupId)) {
            return;
        }
        jedisClientService.del(Constants.checkSysEveTalkGroupUserListByGroupId(groupId));
        jedisClientService.del(Constants.checkSysEveTalkGroupUserIdsByGroupId(groupId));
        if (tenantEnable) {
            String tenantId = StrUtil.nullToEmpty(TenantContext.getTenantId());
            jedisClientService.del(Constants.checkSysEveTalkGroupUserIdsByGroupId(groupId + "_" + tenantId));
        }
    }

    @Override
    public long countByGroupId(String groupId) {
        QueryWrapper<CompanyTalkGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getGroupId), groupId);
        return count(queryWrapper);
    }

    @Override
    public Map<String, String> batchCheckGroupUserIsExit(List<String> groupId, String userId) {
        if (CollectionUtil.isEmpty(groupId) || StrUtil.isBlank(userId)) {
            return new HashMap<>();
        }
        Map<String, String> map = new HashMap<>();
        for (String id : groupId) {
            if (isGroupMember(selectMemberUserIdsByGroupIdWithCache(id), userId)) {
                map.put(id, id);
            }
        }
        return map;
    }

    @Override
    public boolean checkGroupUserIsExit(String groupId, String userId) {
        if (StrUtil.isBlank(groupId) || StrUtil.isBlank(userId)) {
            return false;
        }
        return isGroupMember(selectMemberUserIdsByGroupIdWithCache(groupId), userId);
    }

    @Override
    public void deleteByGroupIdAndUserId(String groupId, String userId) {
        QueryWrapper<CompanyTalkGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getGroupId), groupId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getUserId), userId);
        remove(queryWrapper);
        evictGroupMemberCache(groupId);
    }
}
