package com.skyeye.knowledge.service;

import com.skyeye.knowledge.entity.KnowledgeSegment;

import java.util.List;

/**
 * AI知识库分段服务
 */
public interface KnowledgeSegmentService {

    void deleteByDocIds(List<String> docIds);

    void deleteByKnowIds(List<String> knowIds);

    List<KnowledgeSegment> listByKnowIds(List<String> knowIds);

    void createEntities(List<KnowledgeSegment> segments);
}
