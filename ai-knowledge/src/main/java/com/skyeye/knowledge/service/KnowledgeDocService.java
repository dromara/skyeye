package com.skyeye.knowledge.service;

import com.skyeye.knowledge.entity.KnowledgeDoc;

import java.util.List;
import java.util.Map;

/**
 * AI知识库文档服务
 */
public interface KnowledgeDocService {

    KnowledgeDoc editDocument(KnowledgeDoc knowledgeDoc);

    void rebuildDocument(String docIds);

    void rebuildDocumentByKnowId(String knowId);

    void removeByKnowIds(List<String> knowIds);

    void deleteAllByKnowId(String knowId);

    void deleteDocByIds(String ids);

    List<Map<String, Object>> searchByKnowledge(List<String> knowIds, String queryText, Integer topNumber);

    List<Map<String, Object>> searchByKnowledge(List<String> knowIds, String queryText,
                                                Integer topNumber, Double similarity, String currentUserId);

    KnowledgeDoc selectById(String id);

    List<KnowledgeDoc> queryPageList(int page, int limit, String keyword, String knowledgeId);

    long count(String keyword, String knowledgeId);
}
