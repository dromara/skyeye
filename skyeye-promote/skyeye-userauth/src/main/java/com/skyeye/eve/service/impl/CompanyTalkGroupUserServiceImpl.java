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
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.dao.CompanyTalkGroupUserDao;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroupUser;
import com.skyeye.eve.service.CompanyTalkGroupUserService;
import com.skyeye.jedis.JedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
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
        String cacheKey = Constants.checkSysEveTalkGroupUserIdsByGroupId(groupId);
        List<String> userIds = redisCache.getList(cacheKey, key -> loadMemberUserIds(groupId),
            RedisConstants.ALL_USE_TIME, String.class);
        if (CollectionUtil.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return userIds;
    }

    private List<String> loadMemberUserIds(String groupId) {
        return selectByGroupId(groupId).stream()
            .map(CompanyTalkGroupUser::getUserId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
    }

    @Override
    public void evictGroupMemberCache(String groupId) {
        if (StrUtil.isBlank(groupId)) {
            return;
        }
        jedisClientService.del(Constants.checkSysEveTalkGroupUserListByGroupId(groupId));
        jedisClientService.del(Constants.checkSysEveTalkGroupUserIdsByGroupId(groupId));
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
            if (selectMemberUserIdsByGroupIdWithCache(id).contains(userId)) {
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
        return selectMemberUserIdsByGroupIdWithCache(groupId).contains(userId);
    }

    @Override
    public void deleteByGroupIdAndUserId(String groupId, String userId) {
        QueryWrapper<CompanyTalkGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getGroupId), groupId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getUserId), userId);
        remove(queryWrapper);
        evictGroupMemberCache(groupId);
    }

    @Override
    public List<CompanyTalkGroupUser> selectByUserId(String userId) {
        QueryWrapper<CompanyTalkGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getUserId), userId);
        return list(queryWrapper);
    }

    @Override
    public List<CompanyTalkGroupUser> selectByGroupIds(List<String> groupIds) {
        if (CollectionUtil.isEmpty(groupIds)) {
            return Collections.emptyList();
        }
        QueryWrapper<CompanyTalkGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getGroupId), groupIds);
        return list(queryWrapper);
    }

    @Override
    public void deleteByUserIdAndGroupIds(String userId, List<String> groupIds) {
        if (CollectionUtil.isEmpty(groupIds)) {
            return;
        }
        QueryWrapper<CompanyTalkGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getUserId), userId);
        queryWrapper.in(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getGroupId), groupIds);
        remove(queryWrapper);
        groupIds.forEach(this::evictGroupMemberCache);
    }
}
