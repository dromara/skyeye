/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.framework.file.core.client.s3;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.framework.file.core.client.FileClientConfig;
import lombok.Data;

import javax.validation.Validator;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

/**
 * @ClassName: S3FileClientConfig
 * @Description: S3 文件客户端的配置类
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 11:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("S3 文件客户端的配置类")
public class S3FileClientConfig implements FileClientConfig {

    public static final String ENDPOINT_QINIU = "qiniucs.com";

    public static final String ENDPOINT_ALIYUN = "aliyuncs.com";

    public static final String ENDPOINT_TENCENT = "myqcloud.com";

    public static final String ENDPOINT_VOLCES = "volces.com"; // 火山云（字节）

    /**
     * 节点地址
     * 1. MinIO：https://www.iocoder.cn/Spring-Boot/MinIO 。例如说，http://127.0.0.1:9000
     * 2. 阿里云：https://help.aliyun.com/document_detail/31837.html
     * 3. 腾讯云：https://cloud.tencent.com/document/product/436/6224
     * 4. 七牛云：https://developer.qiniu.com/kodo/4088/s3-access-domainname
     * 5. 华为云：https://developer.huaweicloud.com/endpoint?OBS
     * 6. 火山 TOS（S3 协议）：https://www.volcengine.com/docs/6349/74822 ，如 tos-s3-cn-beijing.volces.com
     */
    @NotBlank(message = "节点地址", groups = {Config.class})
    private String endpoint;

    /**
     * 自定义域名
     * 1. MinIO：通过 Nginx 配置
     * 2. 阿里云：https://help.aliyun.com/document_detail/31836.html
     * 3. 腾讯云：https://cloud.tencent.com/document/product/436/11142
     * 4. 七牛云：https://developer.qiniu.com/kodo/8556/set-the-custom-source-domain-name
     * 5. 华为云：https://support.huaweicloud.com/usermanual-obs/obs_03_0032.html
     */
    @NotBlank(message = "自定义域名", groups = {Config.class})
    private String domain;

    @NotBlank(message = "存储 Bucket", groups = {Config.class})
    private String bucket;

    /**
     * 访问 Key
     * 1. MinIO：https://www.iocoder.cn/Spring-Boot/MinIO
     * 2. 阿里云：https://ram.console.aliyun.com/manage/ak
     * 3. 腾讯云：https://console.cloud.tencent.com/cam/capi
     * 4. 七牛云：https://portal.qiniu.com/user/key
     * 5. 华为云：https://support.huaweicloud.com/qs-obs/obs_qs_0005.html
     */
    @NotBlank(message = "访问 Key", groups = {Config.class})
    private String accessKey;

    @NotBlank(message = "访问 Secret", groups = {Config.class})
    private String accessSecret;

    /**
     * TODO 待补充
     *
     * @return
     */
    @SuppressWarnings("RedundantIfStatement")
    @AssertTrue(message = "domain 不能为空")
    @JsonIgnore
    public boolean isDomainValid() {
        // 如果是七牛，必须带有 domain
        if (StrUtil.contains(endpoint, ENDPOINT_QINIU) && StrUtil.isEmpty(domain)) {
            return false;
        }
        return true;
    }

    public interface Config {
    }

    @Override
    public void validate(Validator validator) {
        validator.validate(this, Config.class);
    }
}
