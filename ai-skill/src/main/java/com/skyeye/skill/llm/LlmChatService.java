/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.llm;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.auth.Auth;
import com.baidubce.qianfan.core.builder.ChatBuilder;
import com.baidubce.qianfan.model.chat.ChatResponse;
import com.skyeye.skill.exception.CustomException;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.listener.SparkConsoleListener;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkResponse;
import io.github.briqt.spark4j.model.response.SparkResponseUsage;
import okhttp3.Response;
import okhttp3.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName: LlmChatService
 * @Description: 同步调用通义 / 文心 / 讯飞，返回完整文本
 */
@Component
public class LlmChatService {

    private static final Logger log = LoggerFactory.getLogger(LlmChatService.class);

    @Autowired
    private LlmProperties llmProperties;

    public boolean isEnabled() {
        return llmProperties.isEnabled();
    }

    public boolean isFallbackToTemplate() {
        return llmProperties.isFallbackToTemplate();
    }

    public String currentPlatform() {
        String platform = llmProperties.getPlatform();
        return StringUtils.hasText(platform) ? platform.trim() : "TongYi";
    }

    public boolean hasApiKey() {
        String platform = currentPlatform();
        if ("YiYan".equalsIgnoreCase(platform)) {
            return StringUtils.hasText(llmProperties.getYiyan().getApiKey())
                && StringUtils.hasText(llmProperties.getYiyan().getSecretKey());
        }
        if ("XunFei".equalsIgnoreCase(platform)) {
            return StringUtils.hasText(llmProperties.getXunfei().getAppId())
                && StringUtils.hasText(llmProperties.getXunfei().getApiKey())
                && StringUtils.hasText(llmProperties.getXunfei().getSecretKey());
        }
        return StringUtils.hasText(llmProperties.getTongyi().getApiKey());
    }

    public String missingKeyHint() {
        String platform = currentPlatform();
        if ("YiYan".equalsIgnoreCase(platform)) {
            return "未配置 skill.llm.yiyan.api-key / secret-key";
        }
        if ("XunFei".equalsIgnoreCase(platform)) {
            return "未配置 skill.llm.xunfei.app-id / api-key / secret-key";
        }
        return "未配置 skill.llm.tongyi.api-key";
    }

    public String chat(String systemPrompt, String userInput) {
        String platform = currentPlatform();
        log.info("调用大模型 platform={}", platform);
        if ("YiYan".equalsIgnoreCase(platform)) {
            return chatYiYan(systemPrompt, userInput);
        }
        if ("XunFei".equalsIgnoreCase(platform)) {
            return chatXunFei(systemPrompt, userInput);
        }
        return chatTongYi(systemPrompt, userInput);
    }

    private String chatTongYi(String systemPrompt, String userInput) {
        String apiKey = llmProperties.getTongyi().getApiKey();
        String model = llmProperties.getTongyi().getModel();
        if (!StringUtils.hasText(model)) {
            model = "qwen-turbo";
        }
        List<Message> messages = new ArrayList<>();
        messages.add(Message.builder().role(Role.SYSTEM.getValue()).content(systemPrompt).build());
        messages.add(Message.builder().role(Role.USER.getValue()).content(userInput).build());
        GenerationParam param = GenerationParam.builder()
            .model(model)
            .messages(messages)
            .resultFormat(GenerationParam.ResultFormat.MESSAGE)
            .topP(0.3)
            .apiKey(apiKey)
            .build();
        try {
            GenerationResult result = new Generation().call(param);
            if (result == null || result.getOutput() == null || result.getOutput().getChoices() == null
                || result.getOutput().getChoices().isEmpty()
                || result.getOutput().getChoices().get(0).getMessage() == null) {
                throw new CustomException("通义千问返回为空");
            }
            return result.getOutput().getChoices().get(0).getMessage().getContent();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("通义千问调用失败: " + e.getMessage());
        }
    }

    private String chatYiYan(String systemPrompt, String userInput) {
        LlmProperties.YiYan yiyan = llmProperties.getYiyan();
        String model = StringUtils.hasText(yiyan.getModel()) ? yiyan.getModel() : "ERNIE-Speed-8K";
        try {
            Qianfan qianfan = new Qianfan(Auth.TYPE_OAUTH, yiyan.getApiKey(), yiyan.getSecretKey());
            ChatBuilder builder = qianfan.chatCompletion().model(model);
            if (StringUtils.hasText(systemPrompt)) {
                builder.system(systemPrompt);
            }
            ChatResponse response = builder.addMessage("user", userInput).execute();
            if (response == null || !StringUtils.hasText(response.getResult())) {
                throw new CustomException("文心一言返回为空");
            }
            return response.getResult();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("文心一言调用失败: " + e.getMessage());
        }
    }

    private String chatXunFei(String systemPrompt, String userInput) {
        LlmProperties.XunFei xunfei = llmProperties.getXunfei();
        SparkClient sparkClient = new SparkClient();
        sparkClient.appid = xunfei.getAppId();
        sparkClient.apiKey = xunfei.getApiKey();
        sparkClient.apiSecret = xunfei.getSecretKey();

        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent(systemPrompt));
        messages.add(SparkMessage.userContent(userInput));
        SparkRequest sparkRequest = SparkRequest.builder()
            .messages(messages)
            .maxTokens(2048)
            .temperature(0.3)
            .apiVersion(SparkApiVersion.V3_5)
            .build();

        final CountDownLatch latch = new CountDownLatch(1);
        final StringBuilder content = new StringBuilder();
        final AtomicReference<String> error = new AtomicReference<String>();
        sparkClient.chatStream(sparkRequest, new SparkConsoleListener() {
            @Override
            public void onMessage(String chunk, SparkResponseUsage usage, Integer status,
                                  SparkRequest request, SparkResponse response, WebSocket webSocket) {
                if (StringUtils.hasText(chunk)) {
                    content.append(chunk);
                }
                if (status != null && status == 2) {
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                error.set(t == null ? "讯飞星火调用失败" : t.getMessage());
                latch.countDown();
            }
        });
        try {
            if (!latch.await(90, TimeUnit.SECONDS)) {
                throw new CustomException("讯飞星火超时");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException("讯飞星火调用被中断");
        }
        if (error.get() != null) {
            throw new CustomException("讯飞星火调用失败: " + error.get());
        }
        if (content.length() == 0) {
            throw new CustomException("讯飞星火返回为空");
        }
        return content.toString();
    }
}
