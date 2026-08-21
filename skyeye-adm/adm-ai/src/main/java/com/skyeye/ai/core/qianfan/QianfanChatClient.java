/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.qianfan;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.exception.CustomException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 千帆 V2 OpenAI 兼容对话客户端。
 * 官方文档：https://cloud.baidu.com/doc/qianfan-docs/s/nm9l6oc8e
 * <p>
 * 不用 openai-java，是因为它编译自 Kotlin 1.9，会要求 kotlin.enums.EnumEntriesKt，
 * 而项目里 OkHttp 4.9.3 带的 kotlin-stdlib 更旧，运行时会 NoClassDefFoundError。
 * 这里直接走 HTTP + SSE，鉴权只要 IAM API Key。
 */
public class QianfanChatClient {

    public static final String DEFAULT_BASE_URL = "https://qianfan.baidubce.com/v2/";

    /**
     * 模型对应的model值，请查看支持的模型列表：https://cloud.baidu.com/doc/qianfan-docs/s/7m95lyy43
     */
    public static final String DEFAULT_MODEL = "ernie-5.0-thinking-preview";

    /**
     * 看图提 Bug 时使用的视觉模型。
     * 支持的模型：https://cloud.baidu.com/doc/qianfan-docs/s/7m95lyy43
     */
    public static final String DEFAULT_VL_MODEL = "ernie-4.5-turbo-vl";

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build();

    private final String apiKey;
    private final String baseUrl;

    public QianfanChatClient(String apiKey, String url) {
        // apiKey：如何获取API Key请查看：https://console.bce.baidu.com/iam/#/iam/apikey/list
        this.apiKey = apiKey;
        String resolved = StrUtil.isBlank(url) ? DEFAULT_BASE_URL : url;
        this.baseUrl = resolved.endsWith("/") ? resolved : resolved + "/";
    }

    /**
     * 阻塞消费流式对话，直到结束、失败或超时。
     *
     * @param messages OpenAI 格式 messages：role + content
     * @param appId    多应用时的 appid Header，可空
     * @param listener 增量回调
     */
    public void streamChat(List<Map<String, String>> messages, String appId, StreamListener listener) {
        streamChat(messages, appId, null, listener);
    }

    /**
     * @param images 截图地址，可空；有值时走视觉模型，content 改为图文混排
     */
    public void streamChat(List<Map<String, String>> messages, String appId, List<String> images, StreamListener listener) {
        boolean vision = images != null && !images.isEmpty();
        JSONObject body = new JSONObject();
        body.set("model", vision ? DEFAULT_VL_MODEL : DEFAULT_MODEL);
        body.set("stream", true);
        body.set("messages", vision ? buildVisionMessages(messages, images) : messages);

        Request.Builder requestBuilder = new Request.Builder()
            .url(baseUrl + "chat/completions")
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .post(RequestBody.create(JSON_MEDIA_TYPE, body.toString()));
        if (StrUtil.isNotBlank(appId)) {
            requestBuilder.header("appid", appId);
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean ended = new AtomicBoolean(false);
        AtomicReference<String> errorMessage = new AtomicReference<String>();
        EventSource eventSource = EventSources.createFactory(HTTP_CLIENT)
            .newEventSource(requestBuilder.build(), new EventSourceListener() {
                @Override
                public void onEvent(EventSource source, String id, String type, String data) {
                    if (StrUtil.isBlank(data) || ended.get()) {
                        return;
                    }
                    if ("[DONE]".equals(data.trim())) {
                        completeQuietly(listener, ended, errorMessage);
                        return;
                    }
                    try {
                        JSONObject json = JSONUtil.parseObj(data);
                        if (json.containsKey("error")) {
                            errorMessage.set(readErrorMessage(json));
                            source.cancel();
                            return;
                        }
                        JSONArray choices = json.getJSONArray("choices");
                        if (choices == null || choices.isEmpty()) {
                            return;
                        }
                        JSONObject choice = choices.getJSONObject(0);
                        String piece = readDeltaContent(choice);
                        boolean end = StrUtil.isNotBlank(choice.getStr("finish_reason"));
                        if (StrUtil.isBlank(piece) && !end) {
                            return;
                        }
                        listener.onDelta(piece, end);
                        if (end) {
                            ended.set(true);
                        }
                    } catch (Exception e) {
                        errorMessage.set(e.getMessage());
                        source.cancel();
                    }
                }

                @Override
                public void onClosed(EventSource source) {
                    completeQuietly(listener, ended, errorMessage);
                    latch.countDown();
                }

                @Override
                public void onFailure(EventSource source, Throwable t, Response response) {
                    if (!ended.get() && errorMessage.get() == null) {
                        errorMessage.set(readFailureMessage(t, response));
                    }
                    ended.set(true);
                    latch.countDown();
                }
            });

        try {
            if (!latch.await(5, TimeUnit.MINUTES)) {
                eventSource.cancel();
                throw new CustomException("千帆流式响应超时");
            }
        } catch (InterruptedException e) {
            eventSource.cancel();
            Thread.currentThread().interrupt();
            throw new CustomException("千帆流式响应被中断");
        }

        String error = errorMessage.get();
        if (StrUtil.isNotBlank(error)) {
            listener.onError(error);
        }
    }

    /**
     * 千帆 V2 视觉消息：最后一条 user 的 content 改为 text + image_url 数组。
     */
    private JSONArray buildVisionMessages(List<Map<String, String>> messages, List<String> images) {
        JSONArray result = new JSONArray();
        int lastUserIndex = -1;
        if (messages != null) {
            for (int i = 0; i < messages.size(); i++) {
                if ("user".equals(messages.get(i).get("role"))) {
                    lastUserIndex = i;
                }
            }
        }
        if (messages != null) {
            for (int i = 0; i < messages.size(); i++) {
                Map<String, String> item = messages.get(i);
                JSONObject msg = new JSONObject();
                msg.set("role", item.get("role"));
                if (i == lastUserIndex) {
                    JSONArray content = new JSONArray();
                    JSONObject text = new JSONObject();
                    text.set("type", "text");
                    text.set("text", item.get("content"));
                    content.add(text);
                    for (String image : images) {
                        if (StrUtil.isBlank(image)) {
                            continue;
                        }
                        JSONObject imageUrl = new JSONObject();
                        imageUrl.set("url", image);
                        JSONObject imagePart = new JSONObject();
                        imagePart.set("type", "image_url");
                        imagePart.set("image_url", imageUrl);
                        content.add(imagePart);
                    }
                    msg.set("content", content);
                } else {
                    msg.set("content", item.get("content"));
                }
                result.add(msg);
            }
        }
        return result;
    }

    private void completeQuietly(StreamListener listener, AtomicBoolean ended, AtomicReference<String> errorMessage) {
        if (errorMessage.get() != null) {
            ended.set(true);
            return;
        }
        if (ended.compareAndSet(false, true)) {
            listener.onDelta(StrUtil.EMPTY, true);
        }
    }

    private String readDeltaContent(JSONObject choice) {
        JSONObject delta = choice.getJSONObject("delta");
        if (delta == null) {
            return StrUtil.EMPTY;
        }
        String content = delta.getStr("content");
        return content == null ? StrUtil.EMPTY : content;
    }

    private String readErrorMessage(JSONObject json) {
        Object error = json.get("error");
        if (error instanceof JSONObject) {
            String message = ((JSONObject) error).getStr("message");
            if (StrUtil.isNotBlank(message)) {
                return message;
            }
        }
        return json.toString();
    }

    private String readFailureMessage(Throwable t, Response response) {
        if (response != null && response.body() != null) {
            try {
                String body = response.body().string();
                if (StrUtil.isNotBlank(body)) {
                    return body;
                }
            } catch (Exception ignored) {
                // 读失败时退回异常信息
            }
        }
        return t == null ? "千帆流式调用失败" : t.getMessage();
    }

    public interface StreamListener {
        void onDelta(String content, boolean end);

        void onError(String message);
    }
}
