package com.skyeye.knowledge.classenum;

/**
 * AI知识库类型
 */
public enum KnowledgeTypeEnum {

    KNOWLEDGE("knowledge", "知识库"),
    MEMORY("memory", "记忆库");

    private final String key;
    private final String value;

    KnowledgeTypeEnum(String key, String value) {
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
