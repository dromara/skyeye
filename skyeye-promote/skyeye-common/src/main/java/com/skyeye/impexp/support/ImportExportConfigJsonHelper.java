/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析 config_json。
 * <pre>
 * {
 *   "sheetMode": "single",          // single=单Sheet主从同行；multi=多Sheet（主表+明细Sheet）
 *   "headerRowHeight": 22,
 *   "dataRowHeight": 18,
 *   "defaultHeaderBackgroundColor": "#4472C4",
 *   "defaultHeaderFontColor": "#FFFFFF",
 *   "items": [{
 *     "attrKey": "title",
 *     "columnTitle": "单据主题",
 *     "sheetKey": "main"
 *   }, {
 *     "attrKey": "purchaseRequestChildList.materialId",
 *     "columnTitle": "物料",
 *     "sheetKey": "purchaseRequestChildList"
 *   }]
 * }
 * </pre>
 * columnWidth 与 POI Sheet#setColumnWidth 一致（1/256 字符宽）。
 * multi 模式下明细 Sheet 通过「主表序号」列关联主表行。
 */
public final class ImportExportConfigJsonHelper {

    public static final String SHEET_MODE_SINGLE = "single";
    public static final String SHEET_MODE_MULTI = "multi";
    public static final String MAIN_SHEET_KEY = "main";
    public static final String MAIN_SHEET_NAME = "主表";
    public static final String LINK_ATTR_KEY = "__rowNo";
    public static final String LINK_COLUMN_TITLE = "主表序号";

    private ImportExportConfigJsonHelper() {
    }

    @Data
    public static class SheetLayoutOptions {
        private Float headerRowHeight;
        private Float dataRowHeight;
        private String defaultHeaderBackgroundColor;
        private String defaultHeaderFontColor;
        /**
         * 一级表头样式：key=父属性 attrKey（如 purchaseRequestChildList）
         */
        private Map<String, HeaderGroupStyle> headerGroups = new LinkedHashMap<>();
    }

    @Data
    public static class HeaderGroupStyle {
        private String title;
        private String headerBackgroundColor;
        private String headerFontColor;
    }

    @Data
    public static class ColumnSpec {
        private String attrKey;
        private String columnTitle;
        private Integer columnWidth;
        private String headerBackgroundColor;
        private String headerFontColor;
        /**
         * 所属 Sheet：main=主表；明细集合则为集合 attrKey
         */
        private String sheetKey;
    }

    @Data
    public static class ParsedConfig {
        /**
         * single / multi，默认 single
         */
        private String sheetMode = SHEET_MODE_SINGLE;
        private SheetLayoutOptions layout = new SheetLayoutOptions();
        private List<ColumnSpec> items = new ArrayList<>();
    }

    public static ParsedConfig parseConfig(String configJson) {
        ParsedConfig out = new ParsedConfig();
        if (StrUtil.isBlank(configJson)) {
            return out;
        }
        JSONObject root = JSONUtil.parseObj(configJson);
        String mode = root.getStr("sheetMode");
        if (SHEET_MODE_MULTI.equalsIgnoreCase(mode)) {
            out.setSheetMode(SHEET_MODE_MULTI);
        } else {
            out.setSheetMode(SHEET_MODE_SINGLE);
        }
        SheetLayoutOptions layout = out.getLayout();
        if (root.containsKey("headerRowHeight")) {
            layout.setHeaderRowHeight(root.getFloat("headerRowHeight"));
        }
        if (root.containsKey("dataRowHeight")) {
            layout.setDataRowHeight(root.getFloat("dataRowHeight"));
        }
        layout.setDefaultHeaderBackgroundColor(StrUtil.blankToDefault(root.getStr("defaultHeaderBackgroundColor"), null));
        layout.setDefaultHeaderFontColor(StrUtil.blankToDefault(root.getStr("defaultHeaderFontColor"), null));
        JSONObject headerGroups = root.getJSONObject("headerGroups");
        if (headerGroups != null && !headerGroups.isEmpty()) {
            Map<String, HeaderGroupStyle> groupMap = new LinkedHashMap<>();
            for (String parentKey : headerGroups.keySet()) {
                if (StrUtil.isBlank(parentKey)) {
                    continue;
                }
                JSONObject g = headerGroups.getJSONObject(parentKey);
                if (g == null) {
                    continue;
                }
                HeaderGroupStyle style = new HeaderGroupStyle();
                style.setTitle(StrUtil.blankToDefault(g.getStr("title"), null));
                style.setHeaderBackgroundColor(StrUtil.blankToDefault(g.getStr("headerBackgroundColor"), null));
                style.setHeaderFontColor(StrUtil.blankToDefault(g.getStr("headerFontColor"), null));
                groupMap.put(parentKey, style);
            }
            layout.setHeaderGroups(groupMap);
        }

        JSONArray items = root.getJSONArray("items");
        if (items == null || items.isEmpty()) {
            return out;
        }
        List<ColumnSpec> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            JSONObject row = items.getJSONObject(i);
            if (row == null) {
                continue;
            }
            String attrKey = row.getStr("attrKey");
            if (StrUtil.isBlank(attrKey)) {
                continue;
            }
            ColumnSpec spec = new ColumnSpec();
            spec.setAttrKey(attrKey);
            String title = row.getStr("columnTitle");
            if (StrUtil.isBlank(title)) {
                title = row.getStr("title");
            }
            if (StrUtil.isBlank(title)) {
                title = row.getStr("name");
            }
            spec.setColumnTitle(StrUtil.isBlank(title) ? null : title);
            if (row.containsKey("columnWidth")) {
                spec.setColumnWidth(row.getInt("columnWidth"));
            }
            spec.setHeaderBackgroundColor(StrUtil.blankToDefault(row.getStr("headerBackgroundColor"), null));
            spec.setHeaderFontColor(StrUtil.blankToDefault(row.getStr("headerFontColor"), null));
            String sheetKey = row.getStr("sheetKey");
            if (StrUtil.isBlank(sheetKey)) {
                sheetKey = MAIN_SHEET_KEY;
            }
            spec.setSheetKey(sheetKey);
            result.add(spec);
        }
        out.setItems(result);
        return out;
    }

    public static boolean isMultiSheet(ParsedConfig parsed) {
        return parsed != null && SHEET_MODE_MULTI.equalsIgnoreCase(parsed.getSheetMode());
    }
}
