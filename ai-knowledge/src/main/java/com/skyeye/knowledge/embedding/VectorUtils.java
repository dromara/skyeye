package com.skyeye.knowledge.embedding;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 向量工具
 */
public final class VectorUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<Number>> NUMBER_LIST = new TypeReference<List<Number>>() {
    };

    private VectorUtils() {
    }

    public static String toJson(float[] vector) {
        if (vector == null) {
            return "[]";
        }
        List<Float> list = new ArrayList<>(vector.length);
        for (float v : vector) {
            list.add(v);
        }
        try {
            return MAPPER.writeValueAsString(list);
        } catch (Exception e) {
            throw new IllegalStateException("向量序列化失败", e);
        }
    }

    public static float[] fromJson(String json) {
        if (!StringUtils.hasText(json)) {
            return new float[0];
        }
        try {
            List<Number> list = MAPPER.readValue(json, NUMBER_LIST);
            float[] vector = new float[list.size()];
            for (int i = 0; i < list.size(); i++) {
                vector[i] = list.get(i).floatValue();
            }
            return vector;
        } catch (Exception e) {
            return new float[0];
        }
    }

    public static double cosineSimilarity(float[] a, float[] b) {
        if (a == null || b == null || a.length == 0 || b.length == 0 || a.length != b.length) {
            return 0D;
        }
        double dot = 0D;
        double normA = 0D;
        double normB = 0D;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0D || normB == 0D) {
            return 0D;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public static float[] toFloatArray(Object embedding) {
        if (embedding == null) {
            return new float[0];
        }
        if (embedding instanceof float[]) {
            return (float[]) embedding;
        }
        if (embedding instanceof double[]) {
            double[] doubles = (double[]) embedding;
            float[] result = new float[doubles.length];
            for (int i = 0; i < doubles.length; i++) {
                result[i] = (float) doubles[i];
            }
            return result;
        }
        if (embedding instanceof List) {
            List<?> list = (List<?>) embedding;
            float[] result = new float[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Object item = list.get(i);
                result[i] = item instanceof Number ? ((Number) item).floatValue() : 0F;
            }
            return result;
        }
        throw new IllegalArgumentException("不支持的向量类型: " + embedding.getClass().getName());
    }

    public static List<String> emptyIfNull(List<String> list) {
        return list == null ? Collections.<String>emptyList() : list;
    }
}
