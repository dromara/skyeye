package com.skyeye.knowledge.embedding.client;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.baidubce.qianfan.model.embedding.EmbeddingData;
import com.baidubce.qianfan.model.embedding.EmbeddingResponse;
import com.skyeye.knowledge.embedding.EmbeddingConsts;
import com.skyeye.knowledge.embedding.VectorUtils;
import com.skyeye.knowledge.exception.CustomException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文心一言向量客户端
 */
public class YiYanEmbeddingClient implements EmbeddingClient {

    private final Qianfan qianfan;
    private final String model;

    public YiYanEmbeddingClient(String apiKey, String secretKey, String model) {
        this.qianfan = new Qianfan(Auth.TYPE_OAUTH, apiKey, secretKey);
        this.model = StringUtils.hasText(model) ? model : EmbeddingConsts.YI_YAN_DEFAULT_MODEL;
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        if (CollectionUtils.isEmpty(texts)) {
            return Collections.emptyList();
        }
        try {
            List<float[]> vectors = new ArrayList<>(texts.size());
            for (int i = 0; i < texts.size(); i += EmbeddingConsts.EMBED_BATCH_SIZE) {
                List<String> batch = texts.subList(i, Math.min(i + EmbeddingConsts.EMBED_BATCH_SIZE, texts.size()));
                EmbeddingResponse response = qianfan.embedding()
                    .model(model)
                    .input(batch)
                    .execute();
                if (response == null || CollectionUtils.isEmpty(response.getData())) {
                    throw new CustomException("文心向量化返回为空");
                }
                float[][] ordered = new float[batch.size()][];
                for (EmbeddingData data : response.getData()) {
                    int index = data.getIndex();
                    if (index < 0 || index >= batch.size()) {
                        throw new CustomException("文心向量化返回 index 非法: " + index);
                    }
                    ordered[index] = VectorUtils.toFloatArray(data.getEmbedding());
                }
                for (int j = 0; j < batch.size(); j++) {
                    if (ordered[j] == null) {
                        throw new CustomException("文心向量化结果缺失，index=" + j);
                    }
                    vectors.add(ordered[j]);
                }
            }
            if (vectors.size() != texts.size()) {
                throw new CustomException("文心向量化结果数量不匹配");
            }
            return vectors;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("文心向量化失败: " + e.getMessage());
        }
    }
}
