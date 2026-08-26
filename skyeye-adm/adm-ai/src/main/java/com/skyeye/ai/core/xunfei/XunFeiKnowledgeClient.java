/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.xunfei;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.core.knowledge.AiKnowledgeClient;
import com.skyeye.ai.core.knowledge.AiKnowledgeConfig;
import com.skyeye.exception.CustomException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 讯飞星火知识库（ChatDoc）文件上传。
 */
public class XunFeiKnowledgeClient implements AiKnowledgeClient {

    private static final OkHttpClient HTTP = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build();

    @Override
    public String uploadText(AiKnowledgeConfig config, String fileName, String content) {
        check(config);
        try {
            long ts = System.currentTimeMillis() / 1000;
            String signature = DigestUtil.md5Hex(
                StrUtil.blankToDefault(config.getSecretKey(), config.getApiKey()) + ts);
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                    RequestBody.create(content.getBytes(StandardCharsets.UTF_8), MediaType.parse("text/plain")))
                .addFormDataPart("fileType", "wiki")
                .addFormDataPart("repoIds", "[\"" + config.getKnowledgeId() + "\"]");
            Request request = new Request.Builder()
                .url("https://chatdoc.xfyun.cn/openapi/v1/file/upload")
                .header("appId", config.getAppId())
                .header("timestamp", String.valueOf(ts))
                .header("signature", signature)
                .post(builder.build()).build();
            try (Response response = HTTP.newCall(request).execute()) {
                String resp = response.body() == null ? "" : response.body().string();
                if (!response.isSuccessful()) {
                    throw new CustomException("讯飞知识库上传失败: " + resp);
                }
                JSONObject json = JSONUtil.parseObj(resp);
                JSONObject data = json.getJSONObject("data");
                if (data != null) {
                    return StrUtil.blankToDefault(data.getStr("fileId"), data.getStr("id"));
                }
                return json.getStr("fileId");
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("讯飞知识库上传失败: " + e.getMessage());
        }
    }

    @Override
    public String search(AiKnowledgeConfig config, String query, int topN) {
        return StrUtil.EMPTY;
    }

    private void check(AiKnowledgeConfig config) {
        if (config == null || StrUtil.isBlank(config.getAppId()) || StrUtil.isBlank(config.getApiKey())
            || StrUtil.isBlank(config.getKnowledgeId())) {
            throw new CustomException("讯飞知识库请配置 AppId、API Key 与 repo ID");
        }
    }

}
