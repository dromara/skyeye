package com.skyeye.knowledge.embedding.client;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.embeddings.TextEmbeddingResultItem;
import com.skyeye.knowledge.embedding.EmbeddingConsts;
import com.skyeye.knowledge.embedding.VectorUtils;
import com.skyeye.knowledge.exception.CustomException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 通义千问向量客户端
 */
public class TongYiEmbeddingClient implements EmbeddingClient {

    private final String apiKey;
    private final String model;

    public TongYiEmbeddingClient(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = StringUtils.hasText(model) ? model : EmbeddingConsts.TONG_YI_DEFAULT_MODEL;
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        if (CollectionUtils.isEmpty(texts)) {
            return Collections.emptyList();
        }
        try {
            TextEmbedding textEmbedding = new TextEmbedding();
            TextEmbeddingParam param = TextEmbeddingParam.builder()
                .model(model)
                .apiKey(apiKey)
                .texts(texts)
                .build();
            TextEmbeddingResult result = textEmbedding.call(param);
            if (result == null || result.getOutput() == null || CollectionUtils.isEmpty(result.getOutput().getEmbeddings())) {
                throw new CustomException("通义向量化返回为空");
            }
            List<TextEmbeddingResultItem> items = result.getOutput().getEmbeddings();
            float[][] ordered = new float[texts.size()][];
            for (TextEmbeddingResultItem item : items) {
                int index = item.getTextIndex() == null ? 0 : item.getTextIndex();
                ordered[index] = VectorUtils.toFloatArray(item.getEmbedding());
            }
            List<float[]> vectors = new ArrayList<>(texts.size());
            for (int i = 0; i < texts.size(); i++) {
                if (ordered[i] == null) {
                    throw new CustomException("通义向量化结果缺失，index=" + i);
                }
                vectors.add(ordered[i]);
            }
            return vectors;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("通义向量化失败: " + e.getMessage());
        }
    }
}
