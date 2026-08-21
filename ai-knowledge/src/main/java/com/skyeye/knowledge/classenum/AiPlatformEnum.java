package com.skyeye.knowledge.classenum;

import com.skyeye.knowledge.exception.CustomException;
import org.springframework.util.StringUtils;

/**
 * AI 模型平台
 */
public enum AiPlatformEnum {

    YI_YAN("YiYan", "文心一言"),
    XUN_FEI("XunFei", "讯飞星火"),
    TONG_YI("TongYi", "通义千问");

    private final String key;
    private final String value;

    AiPlatformEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static AiPlatformEnum getName(String key) {
        if (!StringUtils.hasText(key)) {
            throw new CustomException("非法的AI平台状态");
        }
        for (AiPlatformEnum bean : values()) {
            if (key.equalsIgnoreCase(bean.key)) {
                return bean;
            }
        }
        throw new CustomException("非法的AI平台状态: " + key);
    }
}
