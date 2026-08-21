package com.skyeye.knowledge.classenum;

/**
 * AI知识库文档状态
 */
public enum KnowledgeDocStatusEnum {

    DRAFT("draft", "草稿"),
    BUILDING("building", "构建中"),
    COMPLETE("complete", "构建完成"),
    FAILED("failed", "构建失败");

    private final String key;
    private final String value;

    KnowledgeDocStatusEnum(String key, String value) {
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
