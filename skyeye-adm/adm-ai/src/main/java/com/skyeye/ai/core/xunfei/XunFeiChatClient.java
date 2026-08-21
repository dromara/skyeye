/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.xunfei;

import cn.hutool.core.util.StrUtil;
import com.skyeye.aiStreamModle.SparkListener;
import com.skyeye.exception.CustomException;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkResponse;
import io.github.briqt.spark4j.model.response.SparkResponseUsage;
import okhttp3.WebSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 讯飞星火流式对话客户端。
 * 鉴权需要 APPID + APIKey + APISecret。
 */
public class XunFeiChatClient {

    private static final int DEFAULT_MAX_TOKENS = 2048;

    private final SparkClient sparkClient;

    public XunFeiChatClient(String appId, String apiKey, String secretKey) {
        if (StrUtil.hasBlank(appId, apiKey, secretKey)) {
            throw new CustomException("讯飞配置缺少 APPID、API Key 或 API Secret");
        }
        SparkClient client = new SparkClient();
        client.appid = appId;
        client.apiKey = apiKey;
        client.apiSecret = secretKey;
        this.sparkClient = client;
    }

    /**
     * 阻塞消费流式对话，直到结束、失败或超时。
     *
     * @param messages    role + content，role 为 system / user / assistant
     * @param temperature 采样温度
     * @param listener    增量回调
     */
    public void streamChat(List<Map<String, String>> messages, double temperature, StreamListener listener) {
        SparkRequest sparkRequest = SparkRequest.builder()
            .messages(toSparkMessages(messages))
            .maxTokens(DEFAULT_MAX_TOKENS)
            .temperature(temperature)
            .apiVersion(SparkApiVersion.V3_5)
            .build();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean ended = new AtomicBoolean(false);
        AtomicReference<String> errorMessage = new AtomicReference<String>();
        sparkClient.chatStream(sparkRequest, new SparkListener() {
            @Override
            public void onMessage(String content, SparkResponseUsage usage, Integer status, SparkRequest request,
                                  SparkResponse response, WebSocket webSocket) {
                if (ended.get()) {
                    return;
                }
                String piece = content == null ? StrUtil.EMPTY : content;
                boolean end = status != null && status == 2;
                if (StrUtil.isBlank(piece) && !end) {
                    return;
                }
                listener.onDelta(piece, end);
                if (end) {
                    ended.set(true);
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response httpResponse) {
                if (ended.compareAndSet(false, true)) {
                    errorMessage.set(t == null ? "讯飞星火调用失败" : t.getMessage());
                    latch.countDown();
                }
            }
        });

        try {
            if (!latch.await(5, TimeUnit.MINUTES)) {
                throw new CustomException("讯飞星火流式响应超时");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException("讯飞星火流式响应被中断");
        }

        String error = errorMessage.get();
        if (StrUtil.isNotBlank(error)) {
            listener.onError(error);
        }
    }

    private List<SparkMessage> toSparkMessages(List<Map<String, String>> messages) {
        List<SparkMessage> result = new ArrayList<>();
        if (messages == null) {
            return result;
        }
        for (Map<String, String> item : messages) {
            if (item == null) {
                continue;
            }
            String role = item.get("role");
            String content = item.get("content");
            if (StrUtil.isBlank(content)) {
                continue;
            }
            if ("system".equals(role)) {
                result.add(SparkMessage.systemContent(content));
            } else if ("assistant".equals(role)) {
                result.add(SparkMessage.assistantContent(content));
            } else {
                result.add(SparkMessage.userContent(content));
            }
        }
        return result;
    }

    public interface StreamListener {
        void onDelta(String content, boolean end);

        void onError(String message);
    }
}
