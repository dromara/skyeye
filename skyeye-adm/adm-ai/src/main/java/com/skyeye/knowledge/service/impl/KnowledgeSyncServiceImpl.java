/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.knowledge.dao.KnowledgeSyncDao;
import com.skyeye.knowledge.entity.KnowledgeSync;
import com.skyeye.knowledge.service.KnowledgeSyncService;
import com.skyeye.knowledge.util.KnowledgeTenantFilterHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "AI知识库同步配置", groupName = "AI知识库", allowDynamicAttrKey = false)
public class KnowledgeSyncServiceImpl extends SkyeyeBusinessServiceImpl<KnowledgeSyncDao, KnowledgeSync>
    implements KnowledgeSyncService {

    @Override
    public void saveList(String knowledgeId, List<KnowledgeSync> syncList) {
        // 配置是先删后插，前端不会带回水位/分片字段，按表名从旧记录拷贝，避免保存配置后增量水位和分片 ID 丢失
        Map<String, KnowledgeSync> oldByTable = new HashMap<>();
        List<KnowledgeSync> oldList = selectByKnowledgeId(knowledgeId);
        if (CollectionUtil.isNotEmpty(oldList)) {
            for (KnowledgeSync old : oldList) {
                if (StrUtil.isNotBlank(old.getTableName())) {
                    oldByTable.put(old.getTableName(), old);
                }
            }
        }
        deleteByKnowledgeId(knowledgeId);
        if (CollectionUtil.isEmpty(syncList)) {
            return;
        }
        for (KnowledgeSync sync : syncList) {
            sync.setId(null);
            sync.setKnowledgeId(knowledgeId);
            if (StrUtil.isBlank(sync.getTenantIsolation())) {
                sync.setTenantIsolation(TenantEnum.STRONG_ISOLATION.getKey());
            }
            if (KnowledgeTenantFilterHelper.needTenantColumn(sync.getTenantIsolation())) {
                if (StrUtil.isBlank(sync.getTenantField())) {
                    sync.setTenantField("tenant_id");
                }
            } else if (sync.getTenantField() == null) {
                sync.setTenantField(StrUtil.EMPTY);
            }
            KnowledgeSync old = oldByTable.get(sync.getTableName());
            if (old != null) {
                // 保留增量水位、已上传分片数、平台文档 ID
                if (StrUtil.isBlank(sync.getLastWatermark()) && StrUtil.isNotBlank(old.getLastWatermark())) {
                    sync.setLastWatermark(old.getLastWatermark());
                }
                if (sync.getTablePartCount() == null) {
                    sync.setTablePartCount(old.getTablePartCount());
                }
                if (StrUtil.isBlank(sync.getPartDocIds()) && StrUtil.isNotBlank(old.getPartDocIds())) {
                    sync.setPartDocIds(old.getPartDocIds());
                }
            }
        }
        createEntity(syncList, StrUtil.EMPTY);
    }

    @Override
    public List<KnowledgeSync> selectByKnowledgeId(String knowledgeId) {
        QueryWrapper<KnowledgeSync> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeSync::getKnowledgeId), knowledgeId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(KnowledgeSync::getCreateTime));
        return list(queryWrapper);
    }

    @Override
    public void deleteByKnowledgeId(String knowledgeId) {
        QueryWrapper<KnowledgeSync> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeSync::getKnowledgeId), knowledgeId);
        remove(queryWrapper);
    }

}
