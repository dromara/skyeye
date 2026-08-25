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
import com.skyeye.knowledge.dao.KnowledgeSegmentDao;
import com.skyeye.knowledge.entity.KnowledgeSegment;
import com.skyeye.knowledge.service.KnowledgeSegmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "AI知识库分段", groupName = "AI知识库", allowDynamicAttrKey = false)
public class KnowledgeSegmentServiceImpl extends SkyeyeBusinessServiceImpl<KnowledgeSegmentDao, KnowledgeSegment> implements KnowledgeSegmentService {

    @Override
    public void deleteByKnowledgeId(String knowledgeId) {
        QueryWrapper<KnowledgeSegment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeSegment::getKnowledgeId), knowledgeId);
        remove(queryWrapper);
    }

    @Override
    public void deleteByDocIds(List<String> docIds) {
        if (CollectionUtil.isEmpty(docIds)) {
            return;
        }
        QueryWrapper<KnowledgeSegment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(KnowledgeSegment::getDocId), docIds);
        remove(queryWrapper);
    }

    @Override
    public void saveForDoc(String knowledgeId, String docId, String docName, String content) {
        KnowledgeSegment segment = new KnowledgeSegment();
        segment.setKnowledgeId(knowledgeId);
        segment.setDocId(docId);
        segment.setDocName(docName);
        segment.setContent(content);
        segment.setSegmentIndex(0);
        createEntity(segment, StrUtil.EMPTY);
    }

    @Override
    public List<KnowledgeSegment> search(String knowledgeId, String keyword, int topN) {
        QueryWrapper<KnowledgeSegment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeSegment::getKnowledgeId), knowledgeId);
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(w -> w.like(MybatisPlusUtil.toColumns(KnowledgeSegment::getContent), keyword)
                .or()
                .like(MybatisPlusUtil.toColumns(KnowledgeSegment::getDocName), keyword));
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(KnowledgeSegment::getCreateTime));
        queryWrapper.last("LIMIT " + Math.max(topN, 1));
        return list(queryWrapper);
    }

}
