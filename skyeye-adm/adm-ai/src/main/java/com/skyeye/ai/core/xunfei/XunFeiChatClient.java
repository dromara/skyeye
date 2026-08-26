/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.xunfei;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.aiStreamModle.SparkListener;
import com.skyeye.common.util.ImagesUtil;
import com.skyeye.exception.CustomException;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkResponse;
import io.github.briqt.spark4j.model.response.SparkResponseUsage;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 讯飞星火流式对话客户端。
 * <p>
 * 两种模式：
 * 1. WebSocket（默认）：鉴权 APPID + APIKey + APISecret，走 spark4j。
 * 2. HTTP 推理服务：在配置 url 后走 OpenAI 兼容 SSE，apiAppId 填 modelId，支持 reasoning_content 思考流。
 * 有截图时走图片理解 WebSocket：wss://spark-api.cn-huabei-1.xf-yun.com/v2.1/image
 */
public class XunFeiChatClient {

    private static final int DEFAULT_MAX_TOKENS = 2048;

    public static final String DEFAULT_HTTP_BASE_URL = "https://maas-api.cn-huabei-1.xf-yun.com/v1/chat/completions";

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    /**
     * 官方图片理解地址。文档：https://www.xfyun.cn/doc/spark/ImageUnderstanding.html
     */
    private static final String IMAGE_WS_URL = "wss://spark-api.cn-huabei-1.xf-yun.com/v2.1/image";

    /**
     * 高级版视觉模型；控制台需开通图片理解。未开通时会自动退回文字对话。
     */
    private static final String IMAGE_DOMAIN = "imagev3";

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build();

    private final SparkClient sparkClient;
    private final String appId;
    private final String apiKey;
    private final String apiSecret;
    private final boolean httpMode;
    private final String httpChatCompletionsUrl;
    private final String httpModelId;

    public XunFeiChatClient(String appId, String apiKey, String secretKey) {
        this(appId, apiKey, secretKey, null);
    }

    public XunFeiChatClient(String appId, String apiKey, String secretKey, String url) {
        this.httpMode = StrUtil.isNotBlank(url);
        this.httpChatCompletionsUrl = resolveHttpChatCompletionsUrl(url);
        this.httpModelId = appId;
        this.appId = appId;
        this.apiKey = apiKey;
        this.apiSecret = secretKey;
        if (httpMode) {
            if (StrUtil.isBlank(apiKey)) {
                throw new CustomException("讯飞 HTTP 推理配置缺少 API Key");
            }
            if (StrUtil.isBlank(appId)) {
                throw new CustomException("讯飞 HTTP 推理配置缺少 modelId，请填写 apiAppId");
            }
            this.sparkClient = null;
            return;
        }
        if (StrUtil.hasBlank(appId, apiKey, secretKey)) {
            throw new CustomException("讯飞配置缺少 APPID、API Key 或 API Secret");
        }
        SparkClient client = new SparkClient();
        client.appid = appId;
        client.apiKey = apiKey;
        client.apiSecret = secretKey;
        this.sparkClient = client;
    }

    public void streamChat(List<Map<String, String>> messages, double temperature, StreamListener listener) {
        streamChat(messages, temperature, null, listener);
    }

    /**
     * @param images 截图地址或 data URI，可空；有值时走图片理解，失败再退回文字星火
     */
    public void streamChat(List<Map<String, String>> messages, double temperature, List<String> images, StreamListener listener) {
        List<String> imageBase64 = loadImageBase64(images);
        if (httpMode && imageBase64.isEmpty()) {
            streamHttpChat(messages, temperature, listener);
            return;
        }
        if (imageBase64.isEmpty()) {
            streamTextChat(appendImageHint(messages, images), temperature, listener);
            return;
        }
        AtomicBoolean gotDelta = new AtomicBoolean(false);
        AtomicBoolean fallback = new AtomicBoolean(false);
        streamImageChat(messages, imageBase64, temperature, new StreamListener() {
            @Override
            public void onDelta(String content, boolean end) {
                if (StrUtil.isNotBlank(content)) {
                    gotDelta.set(true);
                }
                listener.onDelta(content, end);
            }

            @Override
            public void onError(String message) {
                if (gotDelta.get() || !fallback.compareAndSet(false, true)) {
                    listener.onError(message);
                    return;
                }
                streamTextChat(appendImageHint(messages, images), temperature, listener);
            }
        });
    }

    private void streamTextChat(List<Map<String, String>> messages, double temperature, StreamListener listener) {
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
                if (StrUtil.isEmpty(piece) && !end) {
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

        awaitLatch(latch, ended, errorMessage, listener, "讯飞星火流式响应超时", "讯飞星火流式响应被中断");
    }

    /**
     * 讯飞 MaaS HTTP 推理：OpenAI 兼容 SSE，思考过程在 delta.reasoning_content。
     */
    private void streamHttpChat(List<Map<String, String>> messages, double temperature, StreamListener listener) {
        JSONObject body = new JSONObject();
        body.set("model", httpModelId);
        body.set("stream", true);
        body.set("temperature", temperature);
        body.set("max_tokens", DEFAULT_MAX_TOKENS);
        body.set("messages", toHttpMessages(messages));
        body.set("enable_thinking", true);

        Request request = new Request.Builder()
            .url(httpChatCompletionsUrl)
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
                        completeHttpQuietly(listener, ended, errorMessage);
                        return;
                    }
                    try {
                        JSONObject json = JSONUtil.parseObj(data);
                        if (json.containsKey("error")) {
                            errorMessage.set(readHttpErrorMessage(json));
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
                    completeHttpQuietly(listener, ended, errorMessage);
                    latch.countDown();
                }

                @Override
                public void onFailure(EventSource source, Throwable t, Response response) {
                    if (!ended.get() && errorMessage.get() == null) {
                        errorMessage.set(readHttpFailureMessage(t, response));
                    }
                    ended.set(true);
                    latch.countDown();
                }
            });

        try {
            if (!latch.await(5, TimeUnit.MINUTES)) {
                eventSource.cancel();
                throw new CustomException("讯飞 HTTP 流式响应超时");
            }
        } catch (InterruptedException e) {
            eventSource.cancel();
            Thread.currentThread().interrupt();
            throw new CustomException("讯飞 HTTP 流式响应被中断");
        }

        String error = errorMessage.get();
        if (StrUtil.isNotBlank(error)) {
            listener.onError(error);
        }
    }

    private JSONArray toHttpMessages(List<Map<String, String>> messages) {
        if (messages == null || messages.isEmpty()) {
            return new JSONArray();
        }
        return JSONUtil.parseArray(JSONUtil.toJsonStr(messages));
    }

    private String resolveHttpChatCompletionsUrl(String url) {
        if (StrUtil.isBlank(url)) {
            return DEFAULT_HTTP_BASE_URL;
        }
        String trimmed = url.trim();
        if (trimmed.contains("chat/completions")) {
            return trimmed;
        }
        String base = trimmed.endsWith("/") ? trimmed : trimmed + "/";
        return base + "chat/completions";
    }

    private void completeHttpQuietly(StreamListener listener, AtomicBoolean ended, AtomicReference<String> errorMessage) {
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

    private String readHttpErrorMessage(JSONObject json) {
        Object error = json.get("error");
        if (error instanceof JSONObject) {
            String message = ((JSONObject) error).getStr("message");
            if (StrUtil.isNotBlank(message)) {
                return message;
            }
        }
        return json.toString();
    }

    private String readHttpFailureMessage(Throwable t, Response response) {
        if (response != null && response.body() != null) {
            try {
                String body = response.body().string();
                if (StrUtil.isNotBlank(body)) {
                    try {
                        JSONObject json = JSONUtil.parseObj(body);
                        String message = readHttpErrorMessage(json);
                        if (StrUtil.isNotBlank(message) && !message.equals(json.toString())) {
                            return message;
                        }
                    } catch (Exception ignored) {
                        return body;
                    }
                    return body;
                }
            } catch (Exception ignored) {
                // 读失败时退回异常信息
            }
        }
        return t == null ? "讯飞 HTTP 流式调用失败" : t.getMessage();
    }

    private void streamImageChat(List<Map<String, String>> messages, List<String> imageBase64,
                                 double temperature, StreamListener listener) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean ended = new AtomicBoolean(false);
        AtomicReference<String> errorMessage = new AtomicReference<String>();
        String authUrl;
        try {
            authUrl = assembleAuthUrl(IMAGE_WS_URL);
        } catch (Exception e) {
            listener.onError(StrUtil.blankToDefault(e.getMessage(), "讯飞图片理解鉴权失败"));
            return;
        }
        Request request = new Request.Builder().url(authUrl).build();
        WebSocket webSocket = HTTP_CLIENT.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket socket, Response response) {
                socket.send(buildImageRequestBody(messages, imageBase64, temperature));
            }

            @Override
            public void onMessage(WebSocket socket, String text) {
                if (ended.get() || StrUtil.isBlank(text)) {
                    return;
                }
                try {
                    JSONObject json = JSONUtil.parseObj(text);
                    JSONObject header = json.getJSONObject("header");
                    if (header != null && header.getInt("code", 0) != 0) {
                        errorMessage.set(StrUtil.blankToDefault(header.getStr("message"), "讯飞图片理解失败"));
                        if (ended.compareAndSet(false, true)) {
                            latch.countDown();
                        }
                        socket.close(1000, "");
                        return;
                    }
                    String piece = readImageDelta(json);
                    Integer status = header == null ? null : header.getInt("status");
                    boolean end = status != null && status == 2;
                    if (StrUtil.isEmpty(piece) && !end) {
                        return;
                    }
                    listener.onDelta(piece, end);
                    if (end) {
                        ended.set(true);
                        latch.countDown();
                        socket.close(1000, "");
                    }
                } catch (Exception e) {
                    errorMessage.set(StrUtil.blankToDefault(e.getMessage(), "讯飞图片理解解析失败"));
                    socket.close(1000, "");
                }
            }

            @Override
            public void onFailure(WebSocket socket, Throwable t, Response response) {
                if (ended.compareAndSet(false, true)) {
                    errorMessage.set(t == null ? "讯飞图片理解调用失败" : t.getMessage());
                    latch.countDown();
                }
            }

            @Override
            public void onClosed(WebSocket socket, int code, String reason) {
                if (ended.compareAndSet(false, true)) {
                    latch.countDown();
                }
            }
        });

        awaitLatch(latch, ended, errorMessage, listener, "讯飞图片理解超时", "讯飞图片理解被中断");
        webSocket.cancel();
    }

    private void awaitLatch(CountDownLatch latch, AtomicBoolean ended, AtomicReference<String> errorMessage,
                            StreamListener listener, String timeoutMessage, String interruptMessage) {
        try {
            if (!latch.await(5, TimeUnit.MINUTES)) {
                ended.set(true);
                throw new CustomException(timeoutMessage);
            }
        } catch (InterruptedException e) {
            ended.set(true);
            Thread.currentThread().interrupt();
            throw new CustomException(interruptMessage);
        }
        String error = errorMessage.get();
        if (StrUtil.isNotBlank(error)) {
            listener.onError(error);
        }
    }

    private String buildImageRequestBody(List<Map<String, String>> messages, List<String> imageBase64, double temperature) {
        JSONArray text = new JSONArray();
        String firstImage = imageBase64.get(0);
        JSONObject imageMsg = new JSONObject();
        imageMsg.set("role", "user");
        imageMsg.set("content", firstImage);
        imageMsg.set("content_type", "image");
        text.add(imageMsg);
        JSONObject userMsg = new JSONObject();
        userMsg.set("role", "user");
        userMsg.set("content", buildImagePrompt(messages, imageBase64.size()));
        userMsg.set("content_type", "text");
        text.add(userMsg);

        JSONObject payloadMessage = new JSONObject();
        payloadMessage.set("text", text);
        JSONObject payload = new JSONObject();
        payload.set("message", payloadMessage);

        JSONObject chat = new JSONObject();
        chat.set("domain", IMAGE_DOMAIN);
        chat.set("temperature", temperature <= 0 ? 0.5 : temperature);
        chat.set("max_tokens", DEFAULT_MAX_TOKENS);

        JSONObject parameter = new JSONObject();
        parameter.set("chat", chat);

        JSONObject header = new JSONObject();
        header.set("app_id", appId);

        JSONObject body = new JSONObject();
        body.set("header", header);
        body.set("parameter", parameter);
        body.set("payload", payload);
        return body.toString();
    }

    private String buildImagePrompt(List<Map<String, String>> messages, int imageCount) {
        StringBuilder sb = new StringBuilder();
        if (messages != null) {
            for (Map<String, String> item : messages) {
                if (item == null) {
                    continue;
                }
                String content = item.get("content");
                if (StrUtil.isBlank(content)) {
                    continue;
                }
                sb.append(content).append("\n");
            }
        }
        if (imageCount > 1) {
            sb.append("用户共上传 ").append(imageCount).append(" 张截图，讯飞图片理解本轮只能看第一张，请主要根据第一张分析。");
        }
        return sb.toString().trim();
    }

    private String readImageDelta(JSONObject json) {
        JSONObject payload = json.getJSONObject("payload");
        if (payload == null) {
            return StrUtil.EMPTY;
        }
        JSONObject choices = payload.getJSONObject("choices");
        if (choices == null) {
            return StrUtil.EMPTY;
        }
        JSONArray texts = choices.getJSONArray("text");
        if (texts == null || texts.isEmpty()) {
            return StrUtil.EMPTY;
        }
        JSONObject first = texts.getJSONObject(0);
        if (first == null) {
            return StrUtil.EMPTY;
        }
        String content = first.getStr("content");
        return content == null ? StrUtil.EMPTY : content;
    }

    private List<String> loadImageBase64(List<String> images) {
        List<String> result = new ArrayList<>();
        if (images == null) {
            return result;
        }
        for (String image : images) {
            if (StrUtil.isBlank(image) || result.size() >= 3) {
                continue;
            }
            String base64 = toImageBase64(image.trim());
            if (StrUtil.isNotBlank(base64)) {
                result.add(base64);
            }
        }
        return result;
    }

    private String toImageBase64(String source) {
        if (source.startsWith("data:")) {
            int comma = source.indexOf(',');
            return comma >= 0 ? source.substring(comma + 1) : "";
        }
        String encoded = ImagesUtil.urlToBase64(source);
        if (StrUtil.isBlank(encoded)) {
            return "";
        }
        if (encoded.startsWith("data:")) {
            int comma = encoded.indexOf(',');
            return comma >= 0 ? encoded.substring(comma + 1) : "";
        }
        return encoded;
    }

    private String assembleAuthUrl(String wsUrl) throws Exception {
        java.net.URI uri = java.net.URI.create(wsUrl);
        String host = uri.getHost();
        String path = uri.getRawPath();
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        String signatureOrigin = "host: " + host + "\n" + "date: " + date + "\n" + "GET " + path + " HTTP/1.1";
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = Base64.getEncoder().encodeToString(mac.doFinal(signatureOrigin.getBytes(StandardCharsets.UTF_8)));
        String authorizationOrigin = "api_key=\"" + apiKey + "\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"" + signature + "\"";
        String authorization = Base64.getEncoder().encodeToString(authorizationOrigin.getBytes(StandardCharsets.UTF_8));
        return wsUrl + "?authorization=" + URLEncoder.encode(authorization, "UTF-8")
            + "&date=" + URLEncoder.encode(date, "UTF-8")
            + "&host=" + URLEncoder.encode(host, "UTF-8");
    }

    private List<Map<String, String>> appendImageHint(List<Map<String, String>> messages, List<String> images) {
        if (images == null || images.isEmpty() || messages == null || messages.isEmpty()) {
            return messages;
        }
        Map<String, String> last = messages.get(messages.size() - 1);
        if (!"user".equals(last.get("role"))) {
            return messages;
        }
        StringBuilder sb = new StringBuilder(StrUtil.nullToEmpty(last.get("content")));
        sb.append("\n用户上传了截图，但未能调用讯飞图片理解，请尽量根据文字描述生成 Bug。");
        last.put("content", sb.toString());
        return messages;
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

        default void onReasoningDelta(String content, boolean end) {
        }
    }
}
