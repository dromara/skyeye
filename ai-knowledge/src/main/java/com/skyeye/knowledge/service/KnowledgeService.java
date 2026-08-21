package com.skyeye.knowledge.service;

import com.skyeye.knowledge.entity.Knowledge;

import java.util.List;
import java.util.Map;

/**
 * AI知识库服务
 */
public interface KnowledgeService {

    Knowledge saveOrUpdate(Knowledge knowledge);

    Knowledge selectById(String id);

    List<Knowledge> queryList();

    List<Knowledge> queryPageList(int page, int limit, String keyword);

    long count(String keyword);

    void deleteById(String id);

    void rebuildKnowledge(String knowIds);

    List<Map<String, Object>> hitTest(String knowId, String queryText, Integer topNumber, Double similarity);

    List<Map<String, Object>> embeddingSearch(String knowIds, String queryText, Integer topNumber, Double similarity);
}
