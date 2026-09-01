/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.doubao;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.core.knowledge.AiKnowledgeClient;
import com.skyeye.ai.core.knowledge.AiKnowledgeConfig;
import com.skyeye.ai.core.knowledge.AiKnowledgeUploadHelper;
import com.skyeye.exception.CustomException;
import okhttp3.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 豆包知识库客户端。
 * <p>
 * 官方 doc/add 支持 url / tos / lark。
 * <p>
 * 优先 add_type=url 并传入稳定 doc_id：同一 ID 重复上传会覆盖。
 * add_type=tos 时请求体里的 doc_id 无效，系统读 TOS 元数据 x-tos-meta-doc_id，
 * 不设元数据就会每导入一次产生一篇新文档。
 * <p>
 * AI 配置：apiKey=ark 对话 Key；secretKey=知识库 VIKING_API_KEY；platformKnowledgeId=resource_id。
 */
public class DouBaoKnowledgeClient implements AiKnowledgeClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String BASE = "https://api-knowledgebase.mlp.cn-beijing.volces.com";
    private static final long DOC_WAIT_MS = TimeUnit.MINUTES.toMillis(10);
    private static final OkHttpClient HTTP = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build();

    @Override
    public String uploadText(AiKnowledgeConfig config, String fileName, String content) {
        throw new CustomException("豆包知识库请先通过文件存储器上传，再使用 URL/TOS 导入");
    }

    @Override
    public String uploadText(AiKnowledgeConfig config, String fileName, String content,
                             String fileUrl, String tosPath, String platformDocName) {
        return addDoc(config, fileName, fileUrl, tosPath, platformDocName, true);
    }

    @Override
    public String uploadFile(AiKnowledgeConfig config, String fileName, String fileUrl, String tosPath) {
        return uploadFile(config, fileName, fileUrl, tosPath, null);
    }

    @Override
    public String uploadFile(AiKnowledgeConfig config, String fileName, String fileUrl, String tosPath, String docId) {
        return addDoc(config, fileName, fileUrl, tosPath, fileName, false, docId);
    }

    @Override
    public void deleteDoc(AiKnowledgeConfig config, String docId) {
        if (StrUtil.isBlank(docId)) {
            return;
        }
        List<String> ids = new ArrayList<>();
        ids.add(docId);
        deleteDocs(config, ids);
    }

    @Override
    public void deleteDocs(AiKnowledgeConfig config, List<String> docIds) {
        if (docIds == null || docIds.isEmpty()) {
            return;
        }
        check(config);
        // 接口一次最多删一批，超出则拆成多请求
        JSONArray batch = JSONUtil.createArray();
        for (String id : docIds) {
            if (StrUtil.isBlank(id)) {
                continue;
            }
            batch.add(id.trim());
            if (batch.size() >= 50) {
                postDeleteDocs(config, batch);
                batch = JSONUtil.createArray();
            }
        }
        if (!batch.isEmpty()) {
            postDeleteDocs(config, batch);
        }
    }

    private void postDeleteDocs(AiKnowledgeConfig config, JSONArray ids) {
        JSONObject body = JSONUtil.createObj().set("doc_ids", ids);
        fillKnowledgeId(body, config.getKnowledgeId());
        try {
            postJson(resolveVikingKey(config), "/api/knowledge/doc/delete", body);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("豆包知识库删除文档失败: " + e.getMessage());
        }
    }

    private String addDoc(AiKnowledgeConfig config, String fileName, String fileUrl, String tosPath,
                          String platformDocName, boolean forceTxt) {
        return addDoc(config, fileName, fileUrl, tosPath, platformDocName, forceTxt, null);
    }

    private String addDoc(AiKnowledgeConfig config, String fileName, String fileUrl, String tosPath,
                          String platformDocName, boolean forceTxt, String explicitDocId) {
        check(config);
        String vikingKey = resolveVikingKey(config);
        String knowledgeId = config.getKnowledgeId();
        String displayName = StrUtil.blankToDefault(platformDocName, fileName);
        // 稳定 ID：不拼 UUID，同一文档再次导入走覆盖
        String docId = sanitizeDocId(StrUtil.blankToDefault(explicitDocId, displayName));
        String docName = StrUtil.blankToDefault(displayName, docId + (forceTxt ? ".txt" : ""));
        if (forceTxt && !StrUtil.endWithIgnoreCase(docName, ".txt")) {
            docName = docName + ".txt";
        }
        try {
            // 优先 URL：请求体 doc_id 生效，同一 ID 覆盖。TOS 方式会忽略 body 里的 doc_id。
            JSONObject body = JSONUtil.createObj();
            fillKnowledgeId(body, knowledgeId);
            String docType = AiKnowledgeUploadHelper.resolveDocType(docName);
            if (forceTxt) {
                docType = "txt";
            }
            if (StrUtil.isNotBlank(fileUrl)) {
                body.set("add_type", "url")
                    .set("doc_id", docId)
                    .set("doc_name", docName)
                    .set("doc_type", docType)
                    .set("url", fileUrl);
            } else if (StrUtil.isNotBlank(tosPath)) {
                body.set("add_type", "tos")
                    .set("tos_path", StrUtil.removePrefix(tosPath.trim(), "tos://"))
                    .set("doc_id", docId)
                    .set("doc_name", docName)
                    .set("doc_type", docType);
            } else {
                throw new CustomException("豆包知识库需要文件存储器返回的 url 或 tosPath（请配置默认 S3/TOS 存储器）");
            }
            JSONObject json = postJson(vikingKey, "/api/knowledge/doc/add", body);
            String returnedDocId = resolveReturnedDocId(json, docId);
            waitDocReady(vikingKey, knowledgeId, returnedDocId);
            return returnedDocId;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("豆包知识库上传失败: " + e.getMessage());
        }
    }

    @Override
    public String uploadText(AiKnowledgeConfig config, String fileName, String content,
                             String fileUrl, String tosPath) {
        return uploadText(config, fileName, content, fileUrl, tosPath, null);
    }

    @Override
    public String search(AiKnowledgeConfig config, String query, int topN) {
        check(config);
        if (StrUtil.isBlank(query)) {
            return StrUtil.EMPTY;
        }
        try {
            JSONObject body = JSONUtil.createObj()
                .set("query", query)
                .set("limit", Math.max(topN, 1));
            fillKnowledgeId(body, config.getKnowledgeId());
            JSONObject json = postJson(resolveVikingKey(config), "/api/knowledge/collection/search_knowledge", body);
            return join(json, topN);
        } catch (Exception e) {
            return StrUtil.EMPTY;
        }
    }

    private String resolveReturnedDocId(JSONObject json, String fallback) {
        JSONObject data = json.getJSONObject("data");
        if (data == null) {
            return fallback;
        }
        return StrUtil.blankToDefault(data.getStr("doc_id"), fallback);
    }

    private void waitDocReady(String vikingKey, String knowledgeId, String docId) throws Exception {
        long deadline = System.currentTimeMillis() + DOC_WAIT_MS;
        while (System.currentTimeMillis() < deadline) {
            Integer status = getDocProcessStatus(vikingKey, knowledgeId, docId);
            if (status != null && status == 0) {
                return;
            }
            if (status != null && status == 1) {
                throw new CustomException("豆包知识库文档处理失败, doc_id=" + docId);
            }
            Thread.sleep(3000L);
        }
        throw new CustomException("豆包知识库文档处理超时, doc_id=" + docId);
    }

    private Integer getDocProcessStatus(String vikingKey, String knowledgeId, String docId) throws Exception {
        JSONObject body = JSONUtil.createObj().set("doc_id", docId);
        fillKnowledgeId(body, knowledgeId);
        Request request = new Request.Builder()
            .url(BASE + "/api/knowledge/doc/info")
            .header("Authorization", "Bearer " + vikingKey)
            .header("Content-Type", "application/json")
            .post(RequestBody.create(body.toString(), JSON)).build();
        try (Response response = HTTP.newCall(request).execute()) {
            String resp = response.body() == null ? "" : response.body().string();
            if (!response.isSuccessful()) {
                return null;
            }
            JSONObject json = JSONUtil.parseObj(resp);
            if (json.getInt("code", -1) != 0) {
                return null;
            }
            JSONObject data = json.getJSONObject("data");
            if (data == null) {
                return null;
            }
            if (data.containsKey("process_status")) {
                return data.getInt("process_status");
            }
            JSONObject statusObj = data.getJSONObject("status");
            if (statusObj != null && statusObj.containsKey("process_status")) {
                return statusObj.getInt("process_status");
            }
            String status = data.getStr("status");
            if (StrUtil.isNotBlank(status) && StrUtil.isNumeric(status)) {
                return Integer.parseInt(status);
            }
            return 0;
        }
    }

    private JSONObject postJson(String vikingKey, String path, JSONObject body) throws Exception {
        Request request = new Request.Builder()
            .url(BASE + path)
            .header("Authorization", "Bearer " + vikingKey)
            .header("Content-Type", "application/json")
            .post(RequestBody.create(body.toString().getBytes(StandardCharsets.UTF_8), JSON)).build();
        try (Response response = HTTP.newCall(request).execute()) {
            String resp = response.body() == null ? "" : response.body().string();
            if (!response.isSuccessful()) {
                throw new CustomException("豆包知识库调用失败[" + path + "]: " + resp);
            }
            JSONObject json = JSONUtil.parseObj(resp);
            if (json.getInt("code", 0) != 0) {
                throw new CustomException("豆包知识库调用失败[" + path + "]: " + resp);
            }
            return json;
        }
    }

    private void fillKnowledgeId(JSONObject body, String knowledgeId) {
        String id = StrUtil.trim(knowledgeId);
        if (StrUtil.startWithIgnoreCase(id, "kb-") || StrUtil.startWithIgnoreCase(id, "kb_")) {
            body.set("resource_id", id);
        } else {
            body.set("collection_name", id);
        }
    }

    private String resolveVikingKey(AiKnowledgeConfig config) {
        String key = StrUtil.trim(config.getSecretKey());
        if (StrUtil.isBlank(key)) {
            throw new CustomException("豆包知识库请在 AI 配置的 secretKey 填写知识库 API Key（非 ark- 对话 Key）");
        }
        if (StrUtil.startWithIgnoreCase(key, "ark-")) {
            throw new CustomException("secretKey 不能填方舟 ark- Key，请填知识库专用 API Key");
        }
        return key;
    }

    private void check(AiKnowledgeConfig config) {
        if (config == null || StrUtil.isBlank(config.getKnowledgeId())) {
            throw new CustomException("豆包知识库请配置平台知识库 ID（resource_id）");
        }
        resolveVikingKey(config);
    }

    private String sanitizeDocId(String fileName) {
        String id = StrUtil.blankToDefault(fileName, "doc").replaceAll("(?i)\\.txt$", "");
        id = id.replaceAll("[^A-Za-z0-9_]", "_");
        if (StrUtil.isBlank(id)) {
            id = "doc";
        }
        char first = id.charAt(0);
        if (!(Character.isLetter(first) || first == '_')) {
            id = "d_" + id;
        }
        if (id.length() > 128) {
            id = id.substring(0, 128);
        }
        return id;
    }

    private String join(JSONObject json, int topN) {
        Object data = json.get("data");
        JSONArray arr = null;
        if (data instanceof JSONObject) {
            arr = ((JSONObject) data).getJSONArray("result_list");
            if (arr == null) {
                arr = ((JSONObject) data).getJSONArray("points");
            }
        } else if (data instanceof JSONArray) {
            arr = (JSONArray) data;
        }
        if (arr == null) {
            return StrUtil.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Object item : arr) {
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

}
