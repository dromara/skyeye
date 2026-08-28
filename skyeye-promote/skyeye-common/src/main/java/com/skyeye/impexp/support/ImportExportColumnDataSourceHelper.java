/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.attr.classenum.AttrKeyDataType;
import com.skyeye.attr.entity.AttrDefinition;
import com.skyeye.attr.entity.AttrDefinitionCustom;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper.ColumnDataSourceOverride;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper.ColumnSpec;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 导入导出列「数据来源 / 值显示」解析辅助。
 */
public final class ImportExportColumnDataSourceHelper {

    public static final String EXPORT_VALUE_MODE_LABEL = "label";
    public static final String EXPORT_VALUE_MODE_CODE = "code";

    private ImportExportColumnDataSourceHelper() {
    }

    /**
     * 是否跟随属性配置的数据来源（默认 true）。
     */
    public static boolean isFollowAttrDataSource(ColumnSpec spec) {
        if (spec == null || spec.getFollowAttrDataSource() == null) {
            return true;
        }
        return Boolean.TRUE.equals(spec.getFollowAttrDataSource());
    }

    /**
     * 导出时是否将编号转为显示名称（默认 label）。
     */
    public static boolean shouldExportAsLabel(ColumnSpec spec) {
        if (spec == null) {
            return false;
        }
        String mode = spec.getExportValueMode();
        if (StrUtil.isBlank(mode)) {
            return true;
        }
        return EXPORT_VALUE_MODE_LABEL.equalsIgnoreCase(mode.trim());
    }

    /**
     * 属性侧是否配置了可用的数据来源（枚举/字典/JSON/API 或 enumClassStr）。
     */
    public static boolean hasAttrDataSource(AttrDefinition attr) {
        return resolveAttrEffectiveSource(attr) != null;
    }

    /**
     * 解析列最终生效的数据来源：跟随属性 或 列自定义。
     */
    public static EffectiveDataSource resolveEffectiveSource(ColumnSpec spec, AttrDefinition attr) {
        if (isFollowAttrDataSource(spec)) {
            return resolveAttrEffectiveSource(attr);
        }
        ColumnDataSourceOverride override = spec == null ? null : spec.getColumnDataSource();
        if (override == null || override.getDataType() == null) {
            return null;
        }
        EffectiveDataSource out = new EffectiveDataSource();
        out.setDataType(override.getDataType());
        out.setObjectId(StrUtil.blankToDefault(override.getObjectId(), null));
        out.setDefaultData(StrUtil.blankToDefault(override.getDefaultData(), null));
        return out;
    }

    /**
     * 从属性定义解析数据来源（与模板下拉解析顺序一致）。
     */
    public static EffectiveDataSource resolveAttrEffectiveSource(AttrDefinition attr) {
        if (attr == null) {
            return null;
        }
        if (StrUtil.isNotBlank(attr.getEnumClassStr())) {
            EffectiveDataSource out = new EffectiveDataSource();
            out.setDataType(AttrKeyDataType.ENUM_DATA.getKey());
            out.setObjectId(attr.getEnumClassStr().trim());
            out.setEnumClassStr(attr.getEnumClassStr().trim());
            return out;
        }
        AttrDefinitionCustom custom = attr.getAttrDefinitionCustom();
        if (custom == null || custom.getDataType() == null) {
            return null;
        }
        EffectiveDataSource out = new EffectiveDataSource();
        out.setDataType(custom.getDataType());
        out.setObjectId(StrUtil.blankToDefault(custom.getObjectId(), null));
        out.setDefaultData(StrUtil.blankToDefault(custom.getDefaultData(), null));
        if (StrUtil.isNotBlank(custom.getEnumClassStr())) {
            out.setEnumClassStr(custom.getEnumClassStr().trim());
        }
        return out;
    }

    /**
     * 收集枚举 ref / 字典 code（供批量预加载）。
     */
    public static void collectEnumAndDictRefs(EffectiveDataSource source, java.util.Set<String> enumRefs,
                                              java.util.Set<String> dictCodes) {
        if (source == null || source.getDataType() == null) {
            return;
        }
        Integer dataType = source.getDataType();
        if (AttrKeyDataType.ENUM_DATA.getKey().equals(dataType)) {
            if (StrUtil.isNotBlank(source.getObjectId())) {
                enumRefs.add(source.getObjectId().trim());
            }
            if (StrUtil.isNotBlank(source.getEnumClassStr())) {
                enumRefs.add(source.getEnumClassStr().trim());
            }
        } else if (AttrKeyDataType.DICT_DATA.getKey().equals(dataType) && StrUtil.isNotBlank(source.getObjectId())) {
            dictCodes.add(source.getObjectId().trim());
        }
    }

    /**
     * 解析 JSON 自定义数据来源的下拉文案。
     */
    public static List<String> loadCustomJsonLabels(String defaultData) {
        List<String> labels = new ArrayList<>();
        if (StrUtil.isBlank(defaultData)) {
            return labels;
        }
        try {
            JSONArray arr = JSONUtil.parseArray(defaultData);
            for (int i = 0; i < arr.size(); i++) {
                Object item = arr.get(i);
                if (item instanceof JSONObject) {
                    JSONObject o = (JSONObject) item;
                    String label = firstNonBlank(o.getStr("name"), o.getStr("label"), o.getStr("title"), o.getStr("id"));
                    if (StrUtil.isNotBlank(label)) {
                        labels.add(label.trim());
                    }
                } else if (item != null && StrUtil.isNotBlank(String.valueOf(item))) {
                    labels.add(String.valueOf(item).trim());
                }
            }
        } catch (Exception ignore) {
            // ignore
        }
        return labels;
    }

    /**
     * 构建 JSON 自定义 id → 显示名。
     */
    public static Map<String, String> buildCustomJsonIdToLabel(String defaultData) {
        Map<String, String> map = new LinkedHashMap<>();
        if (StrUtil.isBlank(defaultData)) {
            return map;
        }
        try {
            JSONArray arr = JSONUtil.parseArray(defaultData);
            for (int i = 0; i < arr.size(); i++) {
                Object item = arr.get(i);
                if (!(item instanceof JSONObject)) {
                    continue;
                }
                JSONObject o = (JSONObject) item;
                String id = firstNonBlank(o.getStr("id"), o.getStr("key"), o.getStr("value"));
                String label = firstNonBlank(o.getStr("name"), o.getStr("label"), o.getStr("title"), id);
                if (StrUtil.isNotBlank(id) && StrUtil.isNotBlank(label)) {
                    map.put(id.trim(), label.trim());
                    map.putIfAbsent(label.trim(), label.trim());
                }
            }
        } catch (Exception ignore) {
            // ignore
        }
        return map;
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

    @Data
    public static class EffectiveDataSource {
        private Integer dataType;
        private String objectId;
        private String defaultData;
        private String enumClassStr;
    }
}
