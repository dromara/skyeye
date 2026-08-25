package com.skyeye.knowledge.embedding;

/**
 * 向量化常量
 */
public final class EmbeddingConsts {

    private EmbeddingConsts() {
    }

    public static final String META_KNOWLEDGE_ID = "knowledgeId";
    public static final String META_DOC_ID = "docId";
    public static final String META_DOC_NAME = "docName";
    public static final String META_CREATE_TIME = "createTime";

    public static final int DEFAULT_SEGMENT_SIZE = 500;
    public static final int DEFAULT_OVERLAP_SIZE = 50;
    public static final double DEFAULT_SIMILARITY = 0.5D;
    public static final int DEFAULT_TOP_NUMBER = 5;
    public static final int EMBED_BATCH_SIZE = 16;

    public static final String TONG_YI_DEFAULT_MODEL = "text-embedding-v3";
    public static final String YI_YAN_DEFAULT_MODEL = "Embedding-V1";

    public static final String ENABLE_SEGMENT = "enableSegment";
    public static final String SEGMENT_STRATEGY = "segmentStrategy";
    public static final String SEGMENT_STRATEGY_CUSTOM = "custom";
    public static final String MAX_SEGMENT = "maxSegment";
    public static final String OVERLAP = "overlap";
    public static final String SEPARATOR = "separator";
    public static final String CUSTOM_SEPARATOR = "customSeparator";
    public static final String USE_KNOWLEDGE_DEFAULT = "useKnowledgeDefault";
}
