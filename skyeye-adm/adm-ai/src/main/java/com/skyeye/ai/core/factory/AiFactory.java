/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.factory;

import com.skyeye.ai.core.enums.AiPlatformEnum;
import com.skyeye.ai.core.knowledge.AiKnowledgeClient;
import com.skyeye.key.entity.AiApiKey;

/**
 * @ClassName: AiFactory
 * @Description: AI Model 模型工厂的接口类
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/5 11:33
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AiFactory {

    /**
     * 基于指定配置，获得 ChatModel 对象
     * <p>
     * 如果不存在，则进行创建
     *
     * @param platform 平台
     * @param appId    APP ID
     * @param apiKey   API KEY
     * @param url      API URL
     * @return ChatModel 对象
     */
    Object getOrCreateChatModel(AiPlatformEnum platform, String appId, String apiKey, String secretKey, String url);

    /**
     * 基于默认配置，获得 ChatModel 对象
     * <p>
     * 默认配置，指的是在 application.yaml 配置文件中的 spring.ai 相关的配置
     *
     * @param platform 平台
     * @param aiApiKey
     * @return ChatModel 对象
     */
    Object getDefaultChatModel(AiPlatformEnum platform, AiApiKey aiApiKey);

    /**
     * 获得平台知识库客户端（上传/检索）
     */
    AiKnowledgeClient getKnowledgeClient(AiPlatformEnum platform);

    /**
     * 基于默认配置，获得 ImageModel 对象
     * <p>
     * 默认配置，指的是在 application.yaml 配置文件中的 spring.ai 相关的配置
     *
     * @param platform 平台
     * @return ImageModel 对象
     */
    Object getDefaultImageModel(AiPlatformEnum platform);

    /**
     * 基于指定配置，获得 ImageModel 对象
     * <p>
     * 如果不存在，则进行创建
     *
     * @param platform 平台
     * @param apiKey   API KEY
     * @param url      API URL
     * @return ImageModel 对象
     */
    Object getOrCreateImageModel(AiPlatformEnum platform, String apiKey, String url);

}
