/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.support;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper.CascadeBind;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper.CascadeItem;

import java.util.*;

/**
 * 将「数据来源行 + 关联父值字段」编译为 Excel 级联用的 {@link CascadeItem} 树。
 * <p>规则：行上 {@code linkField} 的值 = 依赖列单元格值时，该行是子选项；子单元格写入 {@code valueField}。</p>
 */
public final class ImportExportCascadeBindCompiler {

    private ImportExportCascadeBindCompiler() {
    }

    public static List<CascadeItem> buildFromJsonDefaultData(String defaultData, CascadeBind bind) {
        return buildFromRows(parseJsonRows(defaultData), bind);
    }

    public static List<CascadeItem> buildFromRows(List<Map<String, Object>> rows, CascadeBind bind) {
        if (CollectionUtil.isEmpty(rows) || bind == null || StrUtil.isBlank(bind.getLinkField())) {
            return Collections.emptyList();
        }
        String linkField = bind.getLinkField().trim();
        String valueField = StrUtil.blankToDefault(StrUtil.trim(bind.getValueField()), "id");
        String labelField = StrUtil.blankToDefault(StrUtil.trim(bind.getLabelField()), "name");

        Map<String, CascadeItem> roots = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            if (row == null || row.isEmpty()) {
                continue;
            }
            String parentVal = strVal(row, linkField);
            String childCode = strVal(row, valueField);
            if (StrUtil.isBlank(parentVal) || StrUtil.isBlank(childCode)) {
                continue;
            }
            if (!ImportExportCascadeHelper.isValidCode(parentVal) || !ImportExportCascadeHelper.isValidCode(childCode)) {
                continue;
            }
            String childName = firstNonBlank(strVal(row, labelField), strVal(row, "dictName"),
                strVal(row, "label"), strVal(row, "title"), childCode);
            CascadeItem root = roots.computeIfAbsent(parentVal, k -> {
                CascadeItem item = new CascadeItem();
                item.setCode(k);
                item.setName(k);
                item.setChildren(new ArrayList<>());
                return item;
            });
            if (root.getChildren() == null) {
                root.setChildren(new ArrayList<>());
            }
            boolean exists = root.getChildren().stream()
                .anyMatch(c -> c != null && StrUtil.equals(childCode, StrUtil.trim(c.getCode())));
            if (exists) {
                continue;
            }
            CascadeItem child = new CascadeItem();
            child.setCode(childCode);
            child.setName(childName);
            root.getChildren().add(child);
        }
        List<CascadeItem> result = new ArrayList<>();
        for (CascadeItem root : roots.values()) {
            if (CollectionUtil.isNotEmpty(root.getChildren())) {
                result.add(root);
            }
        }
        return result;
    }

    public static List<Map<String, Object>> parseJsonRows(String defaultData) {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (StrUtil.isBlank(defaultData)) {
            return rows;
        }
        try {
            if (!JSONUtil.isTypeJSONArray(defaultData)) {
                return rows;
            }
            JSONArray arr = JSONUtil.parseArray(defaultData);
            for (int i = 0; i < arr.size(); i++) {
                Object item = arr.get(i);
                if (item instanceof JSONObject) {
                    rows.add(new LinkedHashMap<>(((JSONObject) item)));
                } else if (item instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) item;
                    rows.add(new LinkedHashMap<>(map));
                }
            }
        } catch (Exception ignore) {
            // ignore
        }
        return rows;
    }

    private static String strVal(Map<String, Object> row, String field) {
        if (row == null || StrUtil.isBlank(field)) {
            return null;
        }
        Object v = row.get(field);
        if (v == null) {
            // 兼容驼峰/下划线
            v = row.get(StrUtil.toUnderlineCase(field));
            if (v == null) {
                v = row.get(StrUtil.toCamelCase(field));
            }
        }
        return v == null ? null : String.valueOf(v).trim();
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String v : values) {
            if (StrUtil.isNotBlank(v)) {
                return v.trim();
            }
        }
        return null;
    }
}
