/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.notice.dao.NoticeMiniSubscribeDao;
import com.skyeye.eve.notice.entity.NoticeMiniSubscribe;
import com.skyeye.eve.notice.service.NoticeMiniSubscribeService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "小程序订阅授权状态", groupName = "消息通知", manageShow = false)
public class NoticeMiniSubscribeServiceImpl
    extends SkyeyeBusinessServiceImpl<NoticeMiniSubscribeDao, NoticeMiniSubscribe>
    implements NoticeMiniSubscribeService {

    private static final Set<String> ALLOWED_STATUS =
        new HashSet<>(Arrays.asList("accept", "reject", "ban"));

    public static final String STATUS_ACCEPT = "accept";

    @Override
    public void upsertStatuses(String userId, List<Map<String, Object>> items) {
        if (StrUtil.isBlank(userId) || CollectionUtil.isEmpty(items)) {
            return;
        }
        String now = DateUtil.getTimeAndToString();
        for (Map<String, Object> item : items) {
            if (item == null) {
                continue;
            }
            String templateId = StrUtil.trim(MapUtil.getStr(item, "templateId"));
            String status = StrUtil.trim(MapUtil.getStr(item, "status"));
            if (StrUtil.isBlank(templateId) || !ALLOWED_STATUS.contains(status)) {
                continue;
            }
            NoticeMiniSubscribe exist = queryByUserAndTemplate(userId, templateId);
            if (exist == null) {
                NoticeMiniSubscribe row = new NoticeMiniSubscribe();
                row.setUserId(userId);
                row.setTemplateId(templateId);
                row.setStatus(status);
                row.setLastAuthTime(now);
                createEntity(row, userId);
            } else {
                UpdateWrapper<NoticeMiniSubscribe> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, exist.getId());
                updateWrapper.set(MybatisPlusUtil.toColumns(NoticeMiniSubscribe::getStatus), status);
                updateWrapper.set(MybatisPlusUtil.toColumns(NoticeMiniSubscribe::getLastAuthTime), now);
                update(updateWrapper);
            }
        }
    }

    @Override
    public Set<String> listAcceptedTemplateIds(String userId, Collection<String> templateIds) {
        Set<String> result = new HashSet<>();
        if (StrUtil.isBlank(userId) || CollectionUtil.isEmpty(templateIds)) {
            return result;
        }
        List<String> ids = templateIds.stream()
            .filter(StrUtil::isNotBlank)
            .map(String::trim)
            .distinct()
            .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return result;
        }
        QueryWrapper<NoticeMiniSubscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(NoticeMiniSubscribe::getUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(NoticeMiniSubscribe::getStatus), STATUS_ACCEPT);
        queryWrapper.in(MybatisPlusUtil.toColumns(NoticeMiniSubscribe::getTemplateId), ids);
        List<NoticeMiniSubscribe> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return result;
        }
        for (NoticeMiniSubscribe row : list) {
            if (row != null && StrUtil.isNotBlank(row.getTemplateId())) {
                result.add(row.getTemplateId());
            }
        }
        return result;
    }

    @Override
    public void consumeAccept(String userId, String templateId) {
        if (StrUtil.isBlank(userId) || StrUtil.isBlank(templateId)) {
            return;
        }
        UpdateWrapper<NoticeMiniSubscribe> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(NoticeMiniSubscribe::getUserId), userId.trim());
        updateWrapper.eq(MybatisPlusUtil.toColumns(NoticeMiniSubscribe::getTemplateId), templateId.trim());
        updateWrapper.eq(MybatisPlusUtil.toColumns(NoticeMiniSubscribe::getStatus), STATUS_ACCEPT);
        // 清空为已消耗，列表不再显示「已开启」
        updateWrapper.set(MybatisPlusUtil.toColumns(NoticeMiniSubscribe::getStatus), "consumed");
        update(updateWrapper);
    }

    private NoticeMiniSubscribe queryByUserAndTemplate(String userId, String templateId) {
        QueryWrapper<NoticeMiniSubscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(NoticeMiniSubscribe::getUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(NoticeMiniSubscribe::getTemplateId), templateId);
        return getOne(queryWrapper, false);
    }

}
