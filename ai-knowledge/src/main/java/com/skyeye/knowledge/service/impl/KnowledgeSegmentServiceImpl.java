package com.skyeye.knowledge.service.impl;

import com.skyeye.knowledge.entity.KnowledgeSegment;
import com.skyeye.knowledge.exception.CustomException;
import com.skyeye.knowledge.service.KnowledgeSegmentService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * AI知识库分段服务
 */
@Service
public class KnowledgeSegmentServiceImpl implements KnowledgeSegmentService {

    private static final String TODO = "知识库分段功能待实现，后续恢复";

    @Override
    public void deleteByDocIds(List<String> docIds) {
        throw new CustomException(TODO);
    }

    @Override
    public void deleteByKnowIds(List<String> knowIds) {
        throw new CustomException(TODO);
    }

    @Override
    public List<KnowledgeSegment> listByKnowIds(List<String> knowIds) {
        return Collections.emptyList();
    }

    @Override
    public void createEntities(List<KnowledgeSegment> segments) {
        throw new CustomException(TODO);
    }
}
