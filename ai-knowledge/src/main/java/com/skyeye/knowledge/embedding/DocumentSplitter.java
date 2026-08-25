package com.skyeye.knowledge.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 文档分段器
 */
public class DocumentSplitter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final int maxSegment;
    private final int overlap;
    private final String separator;

    public DocumentSplitter(int maxSegment, int overlap) {
        this(maxSegment, overlap, null);
    }

    public DocumentSplitter(int maxSegment, int overlap, String separator) {
        this.maxSegment = Math.max(maxSegment, 50);
        this.overlap = Math.max(0, Math.min(overlap, this.maxSegment / 2));
        this.separator = separator;
    }

    public static DocumentSplitter fromMetadata(String docMetadata, String knowledgeMetadata) {
        int maxSegment = EmbeddingConsts.DEFAULT_SEGMENT_SIZE;
        int overlap = EmbeddingConsts.DEFAULT_OVERLAP_SIZE;
        String separator = null;
        JsonNode json = resolveSegmentConfig(docMetadata, knowledgeMetadata);
        if (json != null) {
            if (json.has(EmbeddingConsts.MAX_SEGMENT) && json.get(EmbeddingConsts.MAX_SEGMENT).asInt() > 0) {
                maxSegment = json.get(EmbeddingConsts.MAX_SEGMENT).asInt();
            }
            if (json.has(EmbeddingConsts.OVERLAP)) {
                double rate = json.get(EmbeddingConsts.OVERLAP).asDouble();
                if (rate >= 0) {
                    overlap = (int) (maxSegment * (rate / 100D));
                }
            }
            if (EmbeddingConsts.SEGMENT_STRATEGY_CUSTOM.equals(text(json, EmbeddingConsts.SEGMENT_STRATEGY))) {
                String splitChar = text(json, EmbeddingConsts.SEPARATOR);
                if (EmbeddingConsts.SEGMENT_STRATEGY_CUSTOM.equals(splitChar)) {
                    String custom = text(json, EmbeddingConsts.CUSTOM_SEPARATOR);
                    splitChar = StringUtils.hasText(custom) ? custom : "\n";
                }
                if (StringUtils.hasText(splitChar)) {
                    separator = splitChar.replace("\\n", "\n").replace("\\t", "\t").replace("\\r", "\r");
                }
            }
        }
        return new DocumentSplitter(maxSegment, overlap, separator);
    }

    private static JsonNode resolveSegmentConfig(String docMetadata, String knowledgeMetadata) {
        JsonNode docJson = parse(docMetadata);
        if (docJson != null && docJson.path(EmbeddingConsts.USE_KNOWLEDGE_DEFAULT).asBoolean(false)) {
            return parse(knowledgeMetadata);
        }
        if (docJson != null && docJson.has(EmbeddingConsts.SEGMENT_STRATEGY)) {
            return docJson;
        }
        JsonNode knowJson = parse(knowledgeMetadata);
        if (knowJson != null && knowJson.path(EmbeddingConsts.ENABLE_SEGMENT).asBoolean(false)) {
            return knowJson;
        }
        return docJson;
    }

    private static JsonNode parse(String metadata) {
        if (!StringUtils.hasText(metadata)) {
            return null;
        }
        try {
            return MAPPER.readTree(metadata);
        } catch (Exception e) {
            return null;
        }
    }

    private static String text(JsonNode json, String field) {
        JsonNode node = json.get(field);
        return node == null || node.isNull() ? null : node.asText();
    }

    public List<String> split(String text) {
        List<String> result = new ArrayList<>();
        if (!StringUtils.hasText(text)) {
            return result;
        }
        String content = text.trim();
        if (content.length() <= maxSegment) {
            result.add(content);
            return result;
        }
        if (StringUtils.hasText(separator)) {
            return splitBySeparator(content);
        }
        return splitRecursive(content);
    }

    private List<String> splitBySeparator(String content) {
        String[] parts = content.split(Pattern.quote(separator), -1);
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String part : parts) {
            String piece = part == null ? "" : part.trim();
            if (!StringUtils.hasText(piece)) {
                continue;
            }
            if (current.length() == 0) {
                current.append(piece);
            } else if (current.length() + separator.length() + piece.length() <= maxSegment) {
                current.append(separator).append(piece);
            } else {
                chunks.add(current.toString());
                current = new StringBuilder(piece);
            }
            if (current.length() > maxSegment) {
                chunks.addAll(splitRecursive(current.toString()));
                current = new StringBuilder();
            }
        }
        if (current.length() > 0) {
            chunks.add(current.toString());
        }
        return chunks;
    }

    private List<String> splitRecursive(String content) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < content.length()) {
            int end = Math.min(start + maxSegment, content.length());
            if (end < content.length()) {
                int breakPos = findBreak(content, start, end);
                if (breakPos > start) {
                    end = breakPos;
                }
            }
            String chunk = content.substring(start, end).trim();
            if (StringUtils.hasText(chunk)) {
                chunks.add(chunk);
            }
            if (end >= content.length()) {
                break;
            }
            start = Math.max(end - overlap, start + 1);
        }
        return chunks;
    }

    private int findBreak(String content, int start, int end) {
        String[] marks = {"\n\n", "\n", "。", "！", "？", ".", "!", "?", "；", ";", "，", ","};
        for (String mark : marks) {
            int idx = content.lastIndexOf(mark, end - 1);
            if (idx > start + maxSegment / 4) {
                return idx + mark.length();
            }
        }
        return end;
    }
}
