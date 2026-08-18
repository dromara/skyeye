/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.llm;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName: LlmProperties
 * @Description: 大模型配置（通义 / 文心 / 讯飞），对齐 adm-ai 平台名
 */
@ConfigurationProperties(prefix = "skill.llm")
public class LlmProperties {

    private boolean enabled = true;

    /** TongYi / YiYan / XunFei */
    private String platform = "TongYi";

    private boolean fallbackToTemplate = true;

    private TongYi tongyi = new TongYi();

    private YiYan yiyan = new YiYan();

    private XunFei xunfei = new XunFei();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public boolean isFallbackToTemplate() {
        return fallbackToTemplate;
    }

    public void setFallbackToTemplate(boolean fallbackToTemplate) {
        this.fallbackToTemplate = fallbackToTemplate;
    }

    public TongYi getTongyi() {
        return tongyi;
    }

    public void setTongyi(TongYi tongyi) {
        this.tongyi = tongyi;
    }

    public YiYan getYiyan() {
        return yiyan;
    }

    public void setYiyan(YiYan yiyan) {
        this.yiyan = yiyan;
    }

    public XunFei getXunfei() {
        return xunfei;
    }

    public void setXunfei(XunFei xunfei) {
        this.xunfei = xunfei;
    }

    public static class TongYi {
        private String apiKey;
        private String model = "qwen-turbo";

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }

    public static class YiYan {
        private String apiKey;
        private String secretKey;
        private String model = "ERNIE-Speed-8K";

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }
    }

    public static class XunFei {
        private String appId;
        private String apiKey;
        private String secretKey;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }
}
