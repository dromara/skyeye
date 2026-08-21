package com.skyeye.knowledge.service;

import com.skyeye.knowledge.entity.EmbedModel;

import java.util.List;

/**
 * 向量模型配置服务
 */
public interface EmbedModelService {

    EmbedModel saveOrUpdate(EmbedModel embedModel);

    EmbedModel selectById(String id);

    List<EmbedModel> queryList();

    List<EmbedModel> queryPageList(int page, int limit, String keyword);

    long count(String keyword);

    void deleteById(String id);
}
