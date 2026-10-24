/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.agent.listconfig;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 按配置把原始行瘦身为 title / subtitle / id。
 */
public final class AiBizListRowMapper {

    private AiBizListRowMapper() {
    }

    public static Map<String, Object> slim(Map<String, Object> row, AiBizListFieldMapping mapping) {
        Map<String, Object> item = new LinkedHashMap<>();
        if (mapping == null) {
            mapping = new AiBizListFieldMapping();
        }
        String title = firstPath(row, mapping.getTitlePaths());
        item.put("title", StrUtil.blankToDefault(title, "未命名"));
        item.put("subtitle", buildSubtitle(row, mapping.getSubtitle()));
        String id = firstPath(row, mapping.getIdPaths());
        if (StrUtil.isNotBlank(id)) {
            item.put("id", id);
        }
        String odd = firstPath(row, mapping.getOddNumberPaths());
        if (StrUtil.isNotBlank(odd)) {
            item.put("oddNumber", odd);
        }
        return item;
    }

    private static String buildSubtitle(Map<String, Object> row, List<AiBizListFieldMapping.SubtitlePart> parts) {
        if (CollectionUtil.isEmpty(parts)) {
            return "";
        }
        List<String> segs = new ArrayList<>();
        for (AiBizListFieldMapping.SubtitlePart part : parts) {
            if (part == null) {
                continue;
            }
            String raw = firstPath(row, part.getPaths());
            if (StrUtil.isBlank(raw) && !part.isFenToYuan()) {
                continue;
            }
            String value = raw;
            if (part.isFenToYuan()) {
                value = fenToYuan(raw);
            } else if (part.getValueLabels() != null && !part.getValueLabels().isEmpty()) {
                value = part.getValueLabels().getOrDefault(raw, raw);
            }
            if (StrUtil.isBlank(value)) {
                continue;
            }
            if (StrUtil.isNotBlank(part.getLabel()) && !value.startsWith("¥")) {
                segs.add(part.getLabel() + "：" + value);
            } else {
                segs.add(value);
            }
        }
        return CollectionUtil.isEmpty(segs) ? "" : String.join(" · ", segs);
    }

    private static String fenToYuan(String fen) {
        if (StrUtil.isBlank(fen)) {
            return "";
        }
        try {
            BigDecimal yuan = new BigDecimal(fen).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            return "¥" + yuan.toPlainString();
        } catch (Exception e) {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    static String firstPath(Map<String, Object> row, List<String> paths) {
        if (row == null || CollectionUtil.isEmpty(paths)) {
            return "";
        }
        for (String path : paths) {
            if (StrUtil.isBlank(path)) {
                continue;
            }
            Object v = resolve(row, path.trim());
            if (v != null && StrUtil.isNotBlank(String.valueOf(v))) {
                return String.valueOf(v).trim();
            }
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private static Object resolve(Map<String, Object> root, String path) {
        String[] parts = path.split("\\.");
        Object cur = root;
        for (String part : parts) {
            if (cur == null) {
                return null;
            }
            if (cur instanceof Map) {
                cur = ((Map<String, Object>) cur).get(part);
                continue;
            }
            // 嵌套对象可能被 Feign 解成 JSONObject / Bean
            try {
                Map<String, Object> asMap = JSONUtil.parseObj(JSONUtil.toJsonStr(cur));
                cur = asMap.get(part);
            } catch (Exception e) {
                return null;
            }
        }
        return cur;
    }

}
