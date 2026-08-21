package com.skyeye.knowledge.classenum;

/**
 * AI知识库状态
 */
public enum KnowledgeStatusEnum {

    ENABLE("enable", "启用"),
    DISABLE("disable", "禁用");

    private final String key;
    private final String value;

    KnowledgeStatusEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
