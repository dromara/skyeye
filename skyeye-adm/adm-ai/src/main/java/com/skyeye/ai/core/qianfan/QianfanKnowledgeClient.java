/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.qianfan;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.core.knowledge.AiKnowledgeClient;
import com.skyeye.ai.core.knowledge.AiKnowledgeConfig;
import com.skyeye.exception.CustomException;
import okhttp3.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 文心/千帆知识库上传与检索。
 */
public class QianfanKnowledgeClient implements AiKnowledgeClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient HTTP = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build();

    @Override
    public String uploadText(AiKnowledgeConfig config, String fileName, String content) {
        check(config);
        try {
            JSONObject payload = JSONUtil.createObj()
                .set("id", config.getKnowledgeId())
                .set("source", JSONUtil.createObj().set("type", "file"))
                .set("contentFormat", "rawText")
                .set("processOption", JSONUtil.createObj().set("template", "default"));
            MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                    RequestBody.create(content.getBytes(StandardCharsets.UTF_8), MediaType.parse("text/plain")))
                .addFormDataPart("payload", payload.toString())
                .build();
            Request request = new Request.Builder()
                .url("https://qianfan.baidubce.com/v2/knowledgeBase?Action=UploadDocuments")
                .header("Authorization", "Bearer " + config.getApiKey())
                .post(body).build();
            try (Response response = HTTP.newCall(request).execute()) {
                String resp = response.body() == null ? "" : response.body().string();
                if (!response.isSuccessful()) {
                    throw new CustomException("文心知识库上传失败: " + resp);
                }
                JSONObject json = JSONUtil.parseObj(resp);
                return StrUtil.blankToDefault(json.getStr("id"), json.getStr("documentId"));
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("文心知识库上传失败: " + e.getMessage());
        }
    }

    @Override
    public String search(AiKnowledgeConfig config, String query, int topN) {
        check(config);
        if (StrUtil.isBlank(query)) {
            return StrUtil.EMPTY;
        }
        try {
            JSONObject body = JSONUtil.createObj()
                .set("query", JSONUtil.createArray().put(JSONUtil.createObj().set("type", "text").set("text", query)))
                .set("knowledgebase_ids", JSONUtil.createArray().put(config.getKnowledgeId()))
                .set("recall", JSONUtil.createObj().set("type", "hybrid").set("top_k", Math.max(topN, 1)))
                .set("rerank", JSONUtil.createObj().set("enable", true).set("top_n", Math.max(topN, 1)));
            Request request = new Request.Builder()
                .url("https://qianfan.baidubce.com/v2/knowledgebases/search")
                .header("Authorization", "Bearer " + config.getApiKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), JSON)).build();
            try (Response response = HTTP.newCall(request).execute()) {
                String resp = response.body() == null ? "" : response.body().string();
                if (!response.isSuccessful()) {
                    return StrUtil.EMPTY;
                }
                return joinChunks(JSONUtil.parseObj(resp), topN);
            }
        } catch (Exception e) {
            return StrUtil.EMPTY;
        }
    }

    private String joinChunks(JSONObject json, int topN) {
        Object chunks = json.get("chunks");
        if (!(chunks instanceof JSONArray)) {
            chunks = json.get("data");
        }
        if (!(chunks instanceof JSONArray)) {
            return StrUtil.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Object item : (JSONArray) chunks) {
            if (i >= topN) {
                break;
            }
            JSONObject row = JSONUtil.parseObj(item);
            String text = StrUtil.blankToDefault(row.getStr("content"), row.getStr("text"));
            if (StrUtil.isBlank(text)) {
                continue;
            }
            sb.append(++i).append(". ").append(text).append("\n\n");
        }
        return sb.toString().trim();
    }

    private void check(AiKnowledgeConfig config) {
        if (config == null || StrUtil.isBlank(config.getApiKey()) || StrUtil.isBlank(config.getKnowledgeId())) {
            throw new CustomException("文心知识库请配置 API Key 与知识库 ID");
        }
    }

}
