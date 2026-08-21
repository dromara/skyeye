package com.skyeye.knowledge.service.impl;

import com.skyeye.knowledge.entity.EmbedModel;
import com.skyeye.knowledge.exception.CustomException;
import com.skyeye.knowledge.service.EmbedModelService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 向量模型配置服务
 */
@Service
public class EmbedModelServiceImpl implements EmbedModelService {

    private static final String TODO = "向量模型功能待实现，后续恢复";

    @Override
    public EmbedModel saveOrUpdate(EmbedModel embedModel) {
        throw new CustomException(TODO);
    }

    @Override
    public EmbedModel selectById(String id) {
        throw new CustomException(TODO);
    }

    @Override
    public List<EmbedModel> queryList() {
        return Collections.emptyList();
    }

    @Override
    public List<EmbedModel> queryPageList(int page, int limit, String keyword) {
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
}
