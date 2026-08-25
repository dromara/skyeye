package com.skyeye.knowledge.embedding.client;

import com.skyeye.knowledge.classenum.AiPlatformEnum;
import com.skyeye.knowledge.embedding.EmbeddingConsts;
import com.skyeye.knowledge.entity.EmbedModel;
import com.skyeye.knowledge.exception.CustomException;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 向量客户端工厂
 */
public final class EmbeddingClientFactory {

    private static final Map<String, EmbeddingClient> CACHE = new ConcurrentHashMap<>();

    private EmbeddingClientFactory() {
    }

    public static EmbeddingClient getClient(EmbedModel model) {
        if (model == null || !StringUtils.hasText(model.getPlatform())) {
            throw new CustomException("向量模型配置不能为空");
        }
        AiPlatformEnum platform = AiPlatformEnum.getName(model.getPlatform());
        String modelName = resolveModel(model, platform);
        String cacheKey = platform.getKey() + "_" + nullToEmpty(model.getApiKey())
            + "_" + nullToEmpty(model.getSecretKey()) + "_" + modelName;
        EmbeddingClient cached = CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        EmbeddingClient created = create(platform, model, modelName);
        CACHE.put(cacheKey, created);
        return created;
    }

    private static EmbeddingClient create(AiPlatformEnum platform, EmbedModel model, String modelName) {
        switch (platform) {
            case TONG_YI:
                if (!StringUtils.hasText(model.getApiKey())) {
                    throw new CustomException("通义向量模型缺少 apiKey");
                }
                return new TongYiEmbeddingClient(model.getApiKey(), modelName);
            case YI_YAN:
                if (!StringUtils.hasText(model.getApiKey()) || !StringUtils.hasText(model.getSecretKey())) {
                    throw new CustomException("文心向量模型缺少 apiKey/secretKey");
                }
                return new YiYanEmbeddingClient(model.getApiKey(), model.getSecretKey(), modelName);
            case XUN_FEI:
            default:
                throw new CustomException("当前平台暂不支持向量化: " + platform.getValue());
        }
    }

    private static String resolveModel(EmbedModel model, AiPlatformEnum platform) {
        if (StringUtils.hasText(model.getModel())) {
            return model.getModel();
        }
        switch (platform) {
            case TONG_YI:
                return EmbeddingConsts.TONG_YI_DEFAULT_MODEL;
            case YI_YAN:
                return EmbeddingConsts.YI_YAN_DEFAULT_MODEL;
            default:
                return null;
        }
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
