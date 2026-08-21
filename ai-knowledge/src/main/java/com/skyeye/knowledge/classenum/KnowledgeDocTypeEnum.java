package com.skyeye.knowledge.classenum;

/**
 * AI知识库文档类型
 */
public enum KnowledgeDocTypeEnum {

    TEXT("text", "文本"),
    FILE("file", "文件"),
    WEB("web", "网页");

    private final String key;
    private final String value;

    KnowledgeDocTypeEnum(String key, String value) {
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
