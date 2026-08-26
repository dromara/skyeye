/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.factory;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.ai.core.doubao.DouBaoChatClient;
import com.skyeye.ai.core.doubao.DouBaoKnowledgeClient;
import com.skyeye.ai.core.enums.AiPlatformEnum;
import com.skyeye.ai.core.knowledge.AiKnowledgeClient;
import com.skyeye.ai.core.qianfan.QianfanChatClient;
import com.skyeye.ai.core.qianfan.QianfanKnowledgeClient;
import com.skyeye.ai.core.tongyi.TongYiChatClient;
import com.skyeye.ai.core.tongyi.TongYiKnowledgeClient;
import com.skyeye.ai.core.xunfei.XunFeiChatClient;
import com.skyeye.ai.core.xunfei.XunFeiKnowledgeClient;
import com.skyeye.exception.CustomException;
import com.skyeye.key.entity.AiApiKey;

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
        String cacheKey = buildClientCacheKey(platform, appId, apiKey, secretKey, url);
        return Singleton.get(cacheKey, (Func0<Object>) () -> {
            switch (platform) {
                case YI_YAN:
                    // 文心走千帆 V2 HTTP/SSE，只需要 API Key，不再传 Secret Key
                    return buildYiYanChatModel(apiKey, url);
                case XUN_FEI:
                    return buildXunFeiClient(appId, apiKey, secretKey, url);
                case TONG_YI:
                    return buildTongYiChatClient(appId, apiKey);
                case DOU_BAO:
                    return buildDouBaoChatClient(appId, apiKey, url);
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
    public AiKnowledgeClient getKnowledgeClient(AiPlatformEnum platform) {
        String cacheKey = "knowledge_" + platform.getKey();
        return Singleton.get(cacheKey, (Func0<AiKnowledgeClient>) () -> {
            switch (platform) {
                case YI_YAN:
                    return new QianfanKnowledgeClient();
                case TONG_YI:
                    return new TongYiKnowledgeClient();
                case DOU_BAO:
                    return new DouBaoKnowledgeClient();
                case XUN_FEI:
                    return new XunFeiKnowledgeClient();
                default:
                    throw new IllegalArgumentException(StrUtil.format("未知平台({})", platform));
            }
        });
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
     * 不用 openai-java，避免 Kotlin 1.9 与项目里旧 kotlin-stdlib 冲突
     * （运行时缺 kotlin.enums.EnumEntriesKt）。
     */
    private static QianfanChatClient buildYiYanChatModel(String apiKey, String url) {
        return new QianfanChatClient(apiKey, url);
    }

    private static XunFeiChatClient buildXunFeiClient(String appId, String apiKey, String secretKey, String url) {
        return new XunFeiChatClient(appId, apiKey, secretKey, url);
    }

    /**
     * 通义走百炼「应用」接口，不是模型 Generation。
     * 配置里的 apiAppId 填应用管理中的应用 ID。
     */
    private static TongYiChatClient buildTongYiChatClient(String appId, String apiKey) {
        return new TongYiChatClient(appId, apiKey);
    }

    /**
     * 豆包走火山引擎方舟 OpenAI 兼容接口。
     * 配置里的 apiAppId 填接入点 ID（ep-xxxx）或模型名，url 可空。
     */
    private static DouBaoChatClient buildDouBaoChatClient(String appId, String apiKey, String url) {
        return new DouBaoChatClient(apiKey, url, appId);
    }

}
