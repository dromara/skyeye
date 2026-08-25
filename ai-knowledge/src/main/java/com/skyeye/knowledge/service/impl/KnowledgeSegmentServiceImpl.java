package com.skyeye.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skyeye.knowledge.dao.KnowledgeSegmentDao;
import com.skyeye.knowledge.entity.KnowledgeSegment;
import com.skyeye.knowledge.service.KnowledgeSegmentService;
import com.skyeye.knowledge.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * AI知识库分段服务
 */
@Service
public class KnowledgeSegmentServiceImpl implements KnowledgeSegmentService {

    private static final String LOCAL_USER = "local";

    @Autowired
    private KnowledgeSegmentDao knowledgeSegmentDao;

    @Override
    public void deleteByDocIds(List<String> docIds) {
        if (CollectionUtils.isEmpty(docIds)) {
            return;
        }
        knowledgeSegmentDao.delete(new LambdaQueryWrapper<KnowledgeSegment>().in(KnowledgeSegment::getDocId, docIds));
    }

    @Override
    public void deleteByKnowIds(List<String> knowIds) {
        if (CollectionUtils.isEmpty(knowIds)) {
            return;
        }
        knowledgeSegmentDao.delete(new LambdaQueryWrapper<KnowledgeSegment>().in(KnowledgeSegment::getKnowledgeId, knowIds));
    }

    @Override
    public List<KnowledgeSegment> listByKnowIds(List<String> knowIds) {
        if (CollectionUtils.isEmpty(knowIds)) {
            return Collections.emptyList();
        }
        return knowledgeSegmentDao.selectList(new LambdaQueryWrapper<KnowledgeSegment>()
            .in(KnowledgeSegment::getKnowledgeId, knowIds));
    }

    @Override
    public void createEntities(List<KnowledgeSegment> segments) {
        if (CollectionUtils.isEmpty(segments)) {
            return;
        }
        String now = IdUtil.now();
        for (KnowledgeSegment segment : segments) {
            if (!StringUtils.hasText(segment.getId())) {
                segment.setId(IdUtil.uuid());
            }
            if (!StringUtils.hasText(segment.getCreateId())) {
                segment.setCreateId(LOCAL_USER);
            }
            if (!StringUtils.hasText(segment.getCreateTime())) {
                segment.setCreateTime(now);
            }
            if (!StringUtils.hasText(segment.getLastUpdateId())) {
                segment.setLastUpdateId(LOCAL_USER);
            }
            if (!StringUtils.hasText(segment.getLastUpdateTime())) {
                segment.setLastUpdateTime(now);
            }
            knowledgeSegmentDao.insert(segment);
        }
    }
}
