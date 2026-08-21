/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.xunfei;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.aiStreamModle.SparkListener;
import com.skyeye.exception.CustomException;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkResponse;
import io.github.briqt.spark4j.model.response.SparkResponseUsage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

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
 * 鉴权需要 APPID + APIKey + APISecret。
 * 有截图时走图片理解 WebSocket：wss://spark-api.cn-huabei-1.xf-yun.com/v2.1/image
 */
public class XunFeiChatClient {

    private static final int DEFAULT_MAX_TOKENS = 2048;

    /**
     * 官方图片理解地址。文档：https://www.xfyun.cn/doc/spark/ImageUnderstanding.html
     */
    private static final String IMAGE_WS_URL = "wss://spark-api.cn-huabei-1.xf-yun.com/v2.1/image";

    /**
     * 高级版视觉模型；控制台需开通图片理解。未开通时会自动退回文字对话。
     */
    private static final String IMAGE_DOMAIN = "imagev3";

    private static final int MAX_IMAGE_BYTES = 4 * 1024 * 1024;

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build();

    private static final OkHttpClient IMAGE_FETCH_CLIENT = new OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build();

    private final SparkClient sparkClient;
    private final String appId;
    private final String apiKey;
    private final String apiSecret;

    public XunFeiChatClient(String appId, String apiKey, String secretKey) {
        if (StrUtil.hasBlank(appId, apiKey, secretKey)) {
            throw new CustomException("讯飞配置缺少 APPID、API Key 或 API Secret");
        }
        SparkClient client = new SparkClient();
        client.appid = appId;
        client.apiKey = apiKey;
        client.apiSecret = secretKey;
        this.sparkClient = client;
        this.appId = appId;
        this.apiKey = apiKey;
        this.apiSecret = secretKey;
    }

    public void streamChat(List<Map<String, String>> messages, double temperature, StreamListener listener) {
        streamChat(messages, temperature, null, listener);
    }

    /**
     * @param images 截图地址或 data URI，可空；有值时走图片理解，失败再退回文字星火
     */
    public void streamChat(List<Map<String, String>> messages, double temperature, List<String> images, StreamListener listener) {
        List<String> imageBase64 = loadImageBase64(images);
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

        awaitLatch(latch, ended, errorMessage, listener, "讯飞星火流式响应超时", "讯飞星火流式响应被中断");
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
                    if (StrUtil.isBlank(piece) && !end) {
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
        try {
            if (source.startsWith("data:")) {
                int comma = source.indexOf(',');
                return comma >= 0 ? source.substring(comma + 1) : "";
            }
            Request request = new Request.Builder().url(source).get().build();
            try (Response response = IMAGE_FETCH_CLIENT.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    return "";
                }
                byte[] bytes = response.body().bytes();
                if (bytes.length == 0 || bytes.length > MAX_IMAGE_BYTES) {
                    return "";
                }
                return Base64.getEncoder().encodeToString(bytes);
            }
        } catch (Exception e) {
            return "";
        }
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
    }
}
