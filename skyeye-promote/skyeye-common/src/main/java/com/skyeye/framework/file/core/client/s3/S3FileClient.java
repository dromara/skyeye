/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.framework.file.core.client.s3;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.skyeye.framework.file.core.client.AbstractFileClient;
import io.minio.*;
import io.minio.http.Method;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: S3FileClient
 * @Description: 基于 S3 协议的文件客户端，实现 MinIO、阿里云、腾讯云、七牛云、华为云等云服务
 * * <p>
 * * S3 协议的客户端，采用亚马逊提供的 software.amazon.awssdk.s3 库
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 12:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class S3FileClient extends AbstractFileClient<S3FileClientConfig> {

    private MinioClient client;

    public S3FileClient(String id, S3FileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
        normalizeEndpoint();
        // 补全 domain
        if (StrUtil.isEmpty(config.getDomain())) {
            config.setDomain(buildDomain());
        }
        // 初始化客户端
        MinioClient.Builder builder = MinioClient.builder()
            .endpoint(buildEndpointURL()) // Endpoint URL
            .credentials(config.getAccessKey(), config.getAccessSecret()); // 认证密钥
        String region = buildRegion();
        if (StrUtil.isNotBlank(region)) {
            builder.region(region);
        }
        client = builder.build();
        enableVirtualStyleEndpoint();
    }

    /**
     * 规范化 endpoint：去协议/路径，并修正火山 TOS 常见写法（缺 s3 段会导致 DNS 失败）
     */
    private void normalizeEndpoint() {
        String endpoint = StrUtil.trim(config.getEndpoint());
        if (StrUtil.isBlank(endpoint)) {
            return;
        }
        endpoint = endpoint.replaceAll("^https?://", "");
        int slashIdx = endpoint.indexOf('/');
        if (slashIdx > 0) {
            endpoint = endpoint.substring(0, slashIdx);
        }
        // 火山 TOS S3 协议域名必须是 tos-s3-{region}.volces.com，不是 tos-{region}.volces.com
        if (endpoint.contains(S3FileClientConfig.ENDPOINT_VOLCES)
            && endpoint.startsWith("tos-")
            && !endpoint.startsWith("tos-s3-")) {
            endpoint = "tos-s3-" + endpoint.substring("tos-".length());
        }
        config.setEndpoint(endpoint);
    }

    /**
     * 基于 endpoint 构建调用云服务的 URL 地址
     *
     * @return URI 地址
     */
    private String buildEndpointURL() {
        // 如果已经是 http 或者 https，则不进行拼接.主要适配 MinIO
        if (HttpUtil.isHttp(config.getEndpoint()) || HttpUtil.isHttps(config.getEndpoint())) {
            return config.getEndpoint();
        }
        return StrUtil.format("https://{}", config.getEndpoint());
    }

    /**
     * 基于 bucket + endpoint 构建访问的 Domain 地址
     *
     * @return Domain 地址
     */
    private String buildDomain() {
        // 如果已经是 http 或者 https，则不进行拼接.主要适配 MinIO
        if (HttpUtil.isHttp(config.getEndpoint()) || HttpUtil.isHttps(config.getEndpoint())) {
            return StrUtil.format("{}/{}", config.getEndpoint(), config.getBucket());
        }
        // 阿里云、腾讯云、华为云都适合。七牛云比较特殊，必须有自定义域名
        return StrUtil.format("https://{}.{}", config.getBucket(), config.getEndpoint());
    }

    /**
     * 基于 bucket 构建 region 地区
     *
     * @return region 地区
     */
    private String buildRegion() {
        // 阿里云必须有 region，否则会报错
        if (config.getEndpoint().contains(S3FileClientConfig.ENDPOINT_ALIYUN)) {
            return StrUtil.subBefore(config.getEndpoint(), '.', false)
                .replaceAll("-internal", "")// 去除内网 Endpoint 的后缀
                .replaceAll("https://", "");
        }
        // 腾讯云必须有 region，否则会报错
        if (config.getEndpoint().contains(S3FileClientConfig.ENDPOINT_TENCENT)) {
            return StrUtil.subAfter(config.getEndpoint(), "cos.", false)
                .replaceAll("." + S3FileClientConfig.ENDPOINT_TENCENT, ""); // 去除 Endpoint
        }
        // 火山 TOS 必须带 region，如 tos-s3-cn-beijing.volces.com -> cn-beijing
        if (config.getEndpoint().contains(S3FileClientConfig.ENDPOINT_VOLCES)) {
            String ep = config.getEndpoint().replaceAll("^https?://", "");
            if (ep.startsWith("tos-s3-")) {
                String region = StrUtil.subBetween(ep, "tos-s3-", ".");
                if (StrUtil.isNotBlank(region)) {
                    return region;
                }
            }
            return "cn-beijing";
        }
        return null;
    }

    /**
     * 开启 VirtualStyle 模式
     */
    private void enableVirtualStyleEndpoint() {
        if (StrUtil.containsAny(config.getEndpoint(),
            S3FileClientConfig.ENDPOINT_TENCENT, // 腾讯云 https://cloud.tencent.com/document/product/436/41284
            S3FileClientConfig.ENDPOINT_VOLCES)) { // 火山云 https://www.volcengine.com/docs/6349/1288493
            client.enableVirtualStyleEndpoint();
        }
    }

    @Override
    public String upload(byte[] content, String path, String type) throws Exception {
        // 执行上传
        client.putObject(PutObjectArgs.builder()
            .bucket(config.getBucket()) // bucket 必须传递
            .contentType(type)
            .object(path) // 相对路径作为 key
            .stream(new ByteArrayInputStream(content), content.length, -1) // 文件内容
            .build());
        // 拼接返回路径
        return config.getDomain() + "/" + path;
    }

    @Override
    public void delete(String path) throws Exception {
        client.removeObject(RemoveObjectArgs.builder()
            .bucket(config.getBucket()) // bucket 必须传递
            .object(path) // 相对路径作为 key
            .build());
    }

    @Override
    public byte[] getContent(String path) throws Exception {
        GetObjectResponse response = client.getObject(GetObjectArgs.builder()
            .bucket(config.getBucket()) // bucket 必须传递
            .object(path) // 相对路径作为 key
            .build());
        return IoUtil.readBytes(response);
    }

    @Override
    public FilePresignedUrlRespDTO getPresignedObjectUrl(String path) throws Exception {
        String uploadUrl = client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
            .method(Method.PUT)
            .bucket(config.getBucket())
            .object(path)
            .expiry(10, TimeUnit.MINUTES) // 过期时间（秒数）取值范围：1 秒 ~ 7 天
            .build()
        );
        return new FilePresignedUrlRespDTO(null, uploadUrl, config.getDomain() + "/" + path);
    }

}
