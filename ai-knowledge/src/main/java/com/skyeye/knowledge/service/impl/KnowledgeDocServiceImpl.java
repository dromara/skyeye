package com.skyeye.knowledge.service.impl;

import com.skyeye.knowledge.entity.KnowledgeDoc;
import com.skyeye.knowledge.exception.CustomException;
import com.skyeye.knowledge.service.KnowledgeDocService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * AI知识库文档服务
 */
@Service
public class KnowledgeDocServiceImpl implements KnowledgeDocService {

    private static final String TODO = "知识库文档功能待实现，后续恢复";

    @Override
    public KnowledgeDoc editDocument(KnowledgeDoc knowledgeDoc) {
        throw new CustomException(TODO);
    }

    @Override
    public void rebuildDocument(String docIds) {
        throw new CustomException(TODO);
    }

    @Override
    public void rebuildDocumentByKnowId(String knowId) {
        throw new CustomException(TODO);
    }

    @Override
    public void removeByKnowIds(List<String> knowIds) {
        throw new CustomException(TODO);
    }

    @Override
    public void deleteAllByKnowId(String knowId) {
        throw new CustomException(TODO);
    }

    @Override
    public void deleteDocByIds(String ids) {
        throw new CustomException(TODO);
    }

    @Override
    public List<Map<String, Object>> searchByKnowledge(List<String> knowIds, String queryText, Integer topNumber) {
        throw new CustomException(TODO);
    }

    @Override
    public List<Map<String, Object>> searchByKnowledge(List<String> knowIds, String queryText,
                                                       Integer topNumber, Double similarity, String currentUserId) {
        throw new CustomException(TODO);
    }

    @Override
    public KnowledgeDoc selectById(String id) {
        throw new CustomException(TODO);
    }

    @Override
    public List<KnowledgeDoc> queryPageList(int page, int limit, String keyword, String knowledgeId) {
        return Collections.emptyList();
    }

    @Override
    public long count(String keyword, String knowledgeId) {
        return 0;
    }
}
