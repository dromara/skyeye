package com.skyeye.knowledge.service.impl;

import com.skyeye.knowledge.entity.Knowledge;
import com.skyeye.knowledge.exception.CustomException;
import com.skyeye.knowledge.service.KnowledgeService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * AI知识库服务
 */
@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    private static final String TODO = "知识库功能待实现，后续恢复";

    @Override
    public Knowledge saveOrUpdate(Knowledge knowledge) {
        throw new CustomException(TODO);
    }

    @Override
    public Knowledge selectById(String id) {
        throw new CustomException(TODO);
    }

    @Override
    public List<Knowledge> queryList() {
        return Collections.emptyList();
    }

    @Override
    public List<Knowledge> queryPageList(int page, int limit, String keyword) {
        return Collections.emptyList();
    }

    @Override
    public long count(String keyword) {
        return 0;
    }

    @Override
    public void deleteById(String id) {
        throw new CustomException(TODO);
    }

    @Override
    public void rebuildKnowledge(String knowIds) {
        throw new CustomException(TODO);
    }

    @Override
    public List<Map<String, Object>> hitTest(String knowId, String queryText, Integer topNumber, Double similarity) {
        throw new CustomException(TODO);
    }

    @Override
    public List<Map<String, Object>> embeddingSearch(String knowIds, String queryText, Integer topNumber, Double similarity) {
        throw new CustomException(TODO);
    }
}
