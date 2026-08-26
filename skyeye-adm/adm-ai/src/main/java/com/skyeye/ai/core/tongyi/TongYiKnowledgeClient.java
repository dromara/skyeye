/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.core.tongyi;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.core.knowledge.AiKnowledgeClient;
import com.skyeye.ai.core.knowledge.AiKnowledgeConfig;
import com.skyeye.exception.CustomException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 通义百炼知识库：OpenAPI 上传；对话依赖应用控制台绑库。
 * apiKey=AccessKeyId，secretKey=AccessKeySecret，workspaceId=业务空间，knowledgeId=IndexId。
 */
public class TongYiKnowledgeClient implements AiKnowledgeClient {

    private static final OkHttpClient HTTP = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build();

    @Override
    public boolean useNativeAppKnowledge() {
        return true;
    }

    @Override
    public String uploadText(AiKnowledgeConfig config, String fileName, String content) {
        check(config);
        String workspaceId = StrUtil.blankToDefault(config.getWorkspaceId(), config.getAppId());
        String categoryId = StrUtil.blankToDefault(config.getCategoryId(), "default");
        try {
            JSONObject leaseReq = JSONUtil.createObj()
                .set("FileName", fileName)
                .set("Md5", DigestUtil.md5Hex(content))
                .set("SizeInBytes", content.getBytes(StandardCharsets.UTF_8).length)
                .set("CategoryType", "UNSTRUCTURED");
            JSONObject leaseResp = callOpenApi(config, workspaceId, "ApplyFileUploadLease",
                "/" + workspaceId + "/api/category/" + categoryId + "/fileUploadLease", leaseReq);
            JSONObject leaseData = leaseResp.getJSONObject("Data");
            if (leaseData == null) {
                throw new CustomException("通义申请上传租约失败: " + leaseResp);
            }
            String leaseId = leaseData.getStr("FileUploadLeaseId");
            JSONObject param = leaseData.getJSONObject("Param");
            String url = param == null ? null : param.getStr("Url");
            if (StrUtil.isBlank(url)) {
                throw new CustomException("通义上传租约缺少 Url");
            }
            Request.Builder put = new Request.Builder().url(url)
                .put(RequestBody.create(content.getBytes(StandardCharsets.UTF_8), MediaType.parse("text/plain")));
            JSONObject headers = param.getJSONObject("Headers");
            if (headers != null) {
                for (String key : headers.keySet()) {
                    put.header(key, headers.getStr(key));
                }
            }
            try (Response putResp = HTTP.newCall(put.build()).execute()) {
                if (!putResp.isSuccessful()) {
                    throw new CustomException("通义文件上传失败");
                }
            }
            JSONObject addResp = callOpenApi(config, workspaceId, "AddFile",
                "/" + workspaceId + "/api/file",
                JSONUtil.createObj().set("LeaseId", leaseId).set("Parser", "DASHSCOPE_DOCMIND").set("CategoryId", categoryId));
            String fileId = addResp.getJSONObject("Data") == null ? null : addResp.getJSONObject("Data").getStr("FileId");
            if (StrUtil.isBlank(fileId)) {
                throw new CustomException("通义 AddFile 未返回 FileId");
            }
            callOpenApi(config, workspaceId, "SubmitIndexAddDocumentsJob",
                "/" + workspaceId + "/index/add_documents_to_index",
                JSONUtil.createObj()
                    .set("IndexId", config.getKnowledgeId())
                    .set("SourceType", "DATA_CENTER_FILE")
                    .set("DocumentIds", JSONUtil.createArray().put(fileId)));
            return fileId;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("通义知识库上传失败: " + e.getMessage());
        }
    }

    @Override
    public String search(AiKnowledgeConfig config, String query, int topN) {
        return StrUtil.EMPTY;
    }

    private JSONObject callOpenApi(AiKnowledgeConfig config, String workspaceId, String action,
                                   String path, JSONObject body) throws Exception {
        String host = "bailian.cn-beijing.aliyuncs.com";
        String bodyStr = body == null ? "{}" : body.toString();
        String contentMd5 = Base64.getEncoder().encodeToString(DigestUtil.md5(bodyStr));
        String date = gmtDate();
        String stringToSign = "POST\napplication/json\n" + contentMd5 + "\napplication/json; charset=utf-8\n" + date + "\n" + path;
        String auth = "acs " + config.getApiKey() + ":" + hmacSha1(config.getSecretKey(), stringToSign);
        HttpResponse response = HttpRequest.post("https://" + host + path)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json; charset=utf-8")
            .header("Content-MD5", contentMd5)
            .header("Date", date)
            .header("Authorization", auth)
            .header("x-acs-action", action)
            .header("x-acs-version", "2023-12-29")
            .header("x-acs-signature-nonce", UUID.randomUUID().toString())
            .body(bodyStr).timeout(120000).execute();
        if (!response.isOk()) {
            throw new CustomException("通义 OpenAPI " + action + " 失败: " + response.body());
        }
        return JSONUtil.parseObj(response.body());
    }

    private String gmtDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date());
    }

    private String hmacSha1(String secret, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    private void check(AiKnowledgeConfig config) {
        if (config == null || StrUtil.isBlank(config.getApiKey()) || StrUtil.isBlank(config.getSecretKey())
            || StrUtil.isBlank(config.getKnowledgeId())
            || (StrUtil.isBlank(config.getWorkspaceId()) && StrUtil.isBlank(config.getAppId()))) {
            throw new CustomException("通义知识库请配置 AccessKey、Secret、业务空间 ID、IndexId");
        }
    }

}
