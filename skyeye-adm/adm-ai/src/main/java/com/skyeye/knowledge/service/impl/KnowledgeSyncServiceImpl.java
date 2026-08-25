/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.knowledge.dao.KnowledgeSyncDao;
import com.skyeye.knowledge.entity.KnowledgeSync;
import com.skyeye.knowledge.service.KnowledgeSyncService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "AI知识库同步配置", groupName = "AI知识库", allowDynamicAttrKey = false)
public class KnowledgeSyncServiceImpl extends SkyeyeBusinessServiceImpl<KnowledgeSyncDao, KnowledgeSync>
    implements KnowledgeSyncService {

    @Override
    public void saveList(String knowledgeId, List<KnowledgeSync> syncList) {
        deleteByKnowledgeId(knowledgeId);
        if (CollectionUtil.isEmpty(syncList)) {
            return;
        }
        for (KnowledgeSync sync : syncList) {
            sync.setKnowledgeId(knowledgeId);
            if (StrUtil.isBlank(sync.getTenantField())) {
                sync.setTenantField("tenant_id");
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
