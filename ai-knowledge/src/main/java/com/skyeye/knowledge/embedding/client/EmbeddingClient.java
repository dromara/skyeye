package com.skyeye.knowledge.embedding.client;

import java.util.List;

/**
 * 向量模型客户端
 */
public interface EmbeddingClient {

    List<float[]> embed(List<String> texts);
}
