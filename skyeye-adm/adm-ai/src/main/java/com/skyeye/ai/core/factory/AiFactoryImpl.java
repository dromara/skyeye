/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.factory;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.skyeye.ai.core.enums.AiPlatformEnum;
import com.skyeye.exception.CustomException;
import com.skyeye.key.entity.AiApiKey;
import io.github.briqt.spark4j.SparkClient;

/**
 * @ClassName: AiFactoryImpl
 * @Description: AI Model 模型工厂的实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/5 14:09
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AiFactoryImpl implements AiFactory {

    @Override
    public Object getOrCreateChatModel(AiPlatformEnum platform, String appId, String apiKey, String secretKey, String url) {
        String cacheKey = buildClientCacheKey(platform, apiKey, url);
        return Singleton.get(cacheKey, (Func0<Object>) () -> {
            switch (platform) {
                case YI_YAN:
                    // 文心走千帆 V2 OpenAI 兼容接口，只需要 API Key，不再传 Secret Key
                    return buildYiYanChatModel(apiKey, url);
                case XUN_FEI:
                    return buildXunFeiClient(appId, apiKey, secretKey);
                case TONG_YI:
                    return buildTongYiChatClient();
                default:
                    throw new IllegalArgumentException(StrUtil.format("未知平台({})", platform));
            }
        });
    }

    @Override
    public Object getDefaultChatModel(AiPlatformEnum platform, AiApiKey aiApiKey) {
        return getOrCreateChatModel(platform,
            aiApiKey.getApiAppId(),
            aiApiKey.getApiKey(),
            aiApiKey.getSecretKey(),
            aiApiKey.getUrl());
    }

    @Override
    public Object getDefaultImageModel(AiPlatformEnum platform) {
        switch (platform) {
            case YI_YAN:
            default:
                throw new CustomException(StrUtil.format("未知平台({})", platform));
        }
    }

    @Override
    public Object getOrCreateImageModel(AiPlatformEnum platform, String apiKey, String url) {
        switch (platform) {
            case YI_YAN:
            default:
                throw new CustomException(StrUtil.format("未知平台({})", platform));
        }
    }

    private static String buildClientCacheKey(Object... params) {
        if (ArrayUtil.isEmpty(params)) {
            throw new CustomException("请指定参数");
        }
        return StrUtil.format("{}", ArrayUtil.join(params, "_"));
    }

    // ========== 各平台客户端创建 ==========

    /**
     * 创建千帆文心客户端。
     * 官方文档：https://cloud.baidu.com/doc/qianfan-docs/s/nm9l6oc8e
     * <p>
     * 千帆已停用应用 AK/SK（OAuth）鉴权，改为 IAM API Key：
     * https://console.bce.baidu.com/iam/#/iam/apikey/list
     * <p>
     * 默认地址为 https://qianfan.baidubce.com/v2/ ；配置表 url 为空时用这个。
     * openai-java 会把路径拼在 baseUrl 后面，所以末尾必须带 /。
     */
    private static OpenAIClient buildYiYanChatModel(String apiKey, String url) {
        String baseUrl = StrUtil.isBlank(url) ? "https://qianfan.baidubce.com/v2/" : url;
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }
        return OpenAIOkHttpClient.builder()
            .apiKey(apiKey)
            .baseUrl(baseUrl)
            .build();
    }

    private static SparkClient buildXunFeiClient(String appId, String apiKey, String secretKey) {
        SparkClient xunFeiApi = new SparkClient();
        xunFeiApi.appid = appId;
        xunFeiApi.apiKey = apiKey;
        xunFeiApi.apiSecret = secretKey;
        return xunFeiApi;
    }


    private static Generation buildTongYiChatClient() {
        return new Generation();
    }

}
