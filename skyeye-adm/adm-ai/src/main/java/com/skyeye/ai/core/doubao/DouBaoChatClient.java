/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.doubao;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.util.ImagesUtil;
import com.skyeye.exception.CustomException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 豆包（火山引擎方舟）OpenAI 兼容对话客户端。
 * 官方文档：https://www.volcengine.com/docs/82379/1298454
 * <p>
 * POST {base}/chat/completions，鉴权 Bearer ARK_API_KEY。
 * 配置里的 appId 填接入点 ID（ep-xxxx）或模型名；url 可空。
 * 不用 openai-java，避免 Kotlin 版本冲突，直接走 HTTP + SSE。
 */
public class DouBaoChatClient {

    public static final String DEFAULT_BASE_URL = "https://ark.cn-beijing.volces.com/api/v3/";

    /**
     * 未配置接入点时的默认模型。
     * doubao-seed-1-6-251015 已官方标记即将下线，迁移到 Seed 2.0-lite（支持文本和看图）。
     * 正式环境请在 AI 配置的 appId 填方舟接入点 ID（ep-xxxx）。
     */
    public static final String DEFAULT_MODEL = "doubao-seed-2-0-lite-260215";

    public static final String DEFAULT_VL_MODEL = "doubao-seed-2-0-lite-260215";

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build();

    private final String apiKey;
    private final String chatCompletionsUrl;
    private final String modelId;

    public DouBaoChatClient(String apiKey, String url, String modelId) {
        this.apiKey = apiKey;
        this.chatCompletionsUrl = resolveChatCompletionsUrl(url);
        this.modelId = StrUtil.blankToDefault(modelId, StrUtil.EMPTY);
    }

    public void streamChat(List<Map<String, String>> messages, StreamListener listener) {
        streamChat(messages, null, listener);
    }

    /**
     * @param images 截图地址，可空；有值时走图文混排
     */
    public void streamChat(List<Map<String, String>> messages, List<String> images, StreamListener listener) {
        List<String> sendableImages = toSendableImages(images);
        boolean vision = !sendableImages.isEmpty();
        JSONObject body = new JSONObject();
        body.set("model", resolveModel(vision));
        body.set("stream", true);
        body.set("messages", vision ? buildVisionMessages(messages, sendableImages) : messages);

        Request request = new Request.Builder()
            .url(chatCompletionsUrl)
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .post(RequestBody.create(JSON_MEDIA_TYPE, body.toString()))
            .build();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean ended = new AtomicBoolean(false);
        AtomicReference<String> errorMessage = new AtomicReference<String>();
        EventSource eventSource = EventSources.createFactory(HTTP_CLIENT)
            .newEventSource(request, new EventSourceListener() {
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
                        String reasoning = readDeltaReasoning(choice);
                        String piece = readDeltaContent(choice);
                        boolean end = StrUtil.isNotBlank(choice.getStr("finish_reason"));
                        if (!StrUtil.isEmpty(reasoning)) {
                            listener.onReasoningDelta(reasoning, false);
                        }
                        if (!StrUtil.isEmpty(piece) || end) {
                            listener.onDelta(piece, end);
                        }
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
                throw new CustomException("豆包流式响应超时");
            }
        } catch (InterruptedException e) {
            eventSource.cancel();
            Thread.currentThread().interrupt();
            throw new CustomException("豆包流式响应被中断");
        }

        String error = errorMessage.get();
        if (StrUtil.isNotBlank(error)) {
            listener.onError(error);
        }
    }

    private String resolveModel(boolean vision) {
        if (StrUtil.isNotBlank(modelId)) {
            return modelId;
        }
        return vision ? DEFAULT_VL_MODEL : DEFAULT_MODEL;
    }

    private String resolveChatCompletionsUrl(String url) {
        if (StrUtil.isBlank(url)) {
            return DEFAULT_BASE_URL + "chat/completions";
        }
        String trimmed = url.trim();
        if (trimmed.contains("chat/completions")) {
            return trimmed;
        }
        String base = trimmed.endsWith("/") ? trimmed : trimmed + "/";
        return base + "chat/completions";
    }

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

    private List<String> toSendableImages(List<String> images) {
        List<String> result = new ArrayList<>();
        if (images == null) {
            return result;
        }
        for (String image : images) {
            if (StrUtil.isBlank(image) || result.size() >= 3) {
                continue;
            }
            if (image.startsWith("data:")) {
                result.add(image);
                continue;
            }
            String encoded = ImagesUtil.urlToBase64(image.trim());
            if (StrUtil.isBlank(encoded)) {
                continue;
            }
            result.add(encoded.startsWith("data:") ? encoded : "data:image/png;base64," + encoded);
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

    private String readDeltaReasoning(JSONObject choice) {
        JSONObject delta = choice.getJSONObject("delta");
        if (delta == null) {
            return StrUtil.EMPTY;
        }
        String reasoning = delta.getStr("reasoning_content");
        return reasoning == null ? StrUtil.EMPTY : reasoning;
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
                if (StrUtil.isBlank(body)) {
                    return t == null ? "豆包流式调用失败" : t.getMessage();
                }
                try {
                    JSONObject json = JSONUtil.parseObj(body);
                    String message = readErrorMessage(json);
                    if (StrUtil.isNotBlank(message) && !message.equals(json.toString())) {
                        return message;
                    }
                    String err = json.getStr("message");
                    if (StrUtil.isNotBlank(err)) {
                        return err;
                    }
                } catch (Exception ignored) {
                    // 非 JSON 时直接返回原文
                }
                return body;
            } catch (Exception ignored) {
                // 读失败时退回异常信息
            }
        }
        return t == null ? "豆包流式调用失败" : t.getMessage();
    }

    public interface StreamListener {
        void onDelta(String content, boolean end);

        void onError(String message);

        default void onReasoningDelta(String content, boolean end) {
        }
    }
}
