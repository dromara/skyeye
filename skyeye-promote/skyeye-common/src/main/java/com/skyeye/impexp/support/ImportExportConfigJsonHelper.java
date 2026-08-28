/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.support;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.dsform.classenum.DateTimeType;
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
 *     "sheetKey": "purchaseRequestChildList",
 *     "cellDataType": "text"
 *   }]
 * }
 * </pre>
 * columnWidth 与 POI Sheet#setColumnWidth 一致（1/256 字符宽）。
 * multi 模式下明细 Sheet 通过「主表序号」列关联主表行。
 * cellDataType：列数据类型，text=文本（默认），date=日期（Excel 单元格日期格式）。
 */
public final class ImportExportConfigJsonHelper {

    public static final String SHEET_MODE_SINGLE = "single";
    public static final String SHEET_MODE_MULTI = "multi";
    public static final String MAIN_SHEET_KEY = "main";
    public static final String MAIN_SHEET_NAME = "主表";
    public static final String LINK_ATTR_KEY = "__rowNo";
    public static final String LINK_COLUMN_TITLE = "主表序号";

    /** 列数据类型：文本（默认） */
    public static final String CELL_DATA_TYPE_TEXT = "text";
    /** 列数据类型：日期 */
    public static final String CELL_DATA_TYPE_DATE = "date";
    /**
     * 传给 {@link com.skyeye.common.util.ExcelUtil} 的 dataType 约定：date 列使用 "data"
     */
    public static final String EXCEL_DATA_TYPE_DATE = "data";
    /**
     * 日期列默认格式：与 {@link DateTimeType} 枚举 key 一致
     */
    public static final String DEFAULT_CELL_DATE_FORMAT = DateTimeType.DATETIME.getKey();

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
        /**
         * 列数据类型：{@link #CELL_DATA_TYPE_TEXT}（默认）/ {@link #CELL_DATA_TYPE_DATE}
         */
        private String cellDataType;
        /**
         * 日期列显示格式：与前端枚举 dateTimeType 的 id 一致
         * （year / month / date / time / datetime / timeminute），
         * 写出 Excel 时映射为 POI DataFormat；亦兼容直接填写 Excel 格式串。
         * 仅 cellDataType=date 时有效。
         */
        private String cellDateFormat;
        /**
         * 是否跟随属性配置的数据来源；默认 true（null 视为 true）。
         * 导入模板：控制 Excel 下拉选项来源。
         */
        private Boolean followAttrDataSource;
        /**
         * 列自定义数据来源（followAttrDataSource=false 时生效）。
         */
        private ColumnDataSourceOverride columnDataSource;
        /**
         * 导出值显示：label=显示名称，code=显示原始编号；默认 label。
         */
        private String exportValueMode;
    }

    @Data
    public static class ColumnDataSourceOverride {
        /** 1=JSON 2=枚举 3=字典 4=API */
        private Integer dataType;
        private String objectId;
        private String defaultData;
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
            spec.setCellDataType(normalizeCellDataType(row.getStr("cellDataType")));
            if (isDateCellDataType(spec.getCellDataType())) {
                spec.setCellDateFormat(normalizeCellDateFormat(row.getStr("cellDateFormat")));
            }
            if (row.containsKey("followAttrDataSource")) {
                spec.setFollowAttrDataSource(row.getBool("followAttrDataSource"));
            }
            JSONObject ds = row.getJSONObject("columnDataSource");
            if (ds != null && !ds.isEmpty()) {
                ColumnDataSourceOverride override = new ColumnDataSourceOverride();
                if (ds.containsKey("dataType")) {
                    override.setDataType(ds.getInt("dataType"));
                }
                override.setObjectId(StrUtil.blankToDefault(ds.getStr("objectId"), null));
                override.setDefaultData(StrUtil.blankToDefault(ds.getStr("defaultData"), null));
                spec.setColumnDataSource(override);
            }
            spec.setExportValueMode(StrUtil.blankToDefault(row.getStr("exportValueMode"), null));
            result.add(spec);
        }
        out.setItems(result);
        return out;
    }

    public static boolean isMultiSheet(ParsedConfig parsed) {
        return parsed != null && SHEET_MODE_MULTI.equalsIgnoreCase(parsed.getSheetMode());
    }

    /**
     * 归一化列数据类型：仅 text / date，其它按文本。
     */
    public static String normalizeCellDataType(String cellDataType) {
        if (StrUtil.isBlank(cellDataType)) {
            return CELL_DATA_TYPE_TEXT;
        }
        String t = cellDataType.trim().toLowerCase();
        if (CELL_DATA_TYPE_DATE.equals(t)) {
            return CELL_DATA_TYPE_DATE;
        }
        return CELL_DATA_TYPE_TEXT;
    }

    /**
     * 是否日期列。
     */
    public static boolean isDateCellDataType(String cellDataType) {
        return CELL_DATA_TYPE_DATE.equals(normalizeCellDataType(cellDataType));
    }

    /**
     * 归一化日期格式配置值（DateTimeType 枚举 id；空白则用默认 datetime）。
     */
    public static String normalizeCellDateFormat(String cellDateFormat) {
        if (StrUtil.isBlank(cellDateFormat)) {
            return DEFAULT_CELL_DATE_FORMAT;
        }
        return cellDateFormat.trim();
    }

    /**
     * 将 config 中的日期格式转为 ExcelUtil 可用的 DataFormat 模式串。
     * <p>识别 {@link DateTimeType} 枚举 key；无法识别时原样当作 Excel 格式串（兼容历史配置）。</p>
     */
    public static String toExcelDatePattern(String cellDateFormat) {
        String key = normalizeCellDateFormat(cellDateFormat);
        String pattern = DateTimeType.getExcelPatternByKey(key);
        if (StrUtil.isNotBlank(pattern)) {
            return pattern;
        }
        return key;
    }

    /**
     * 将配置列转为 ExcelUtil 的 dataType 数组。
     * <p>date → 一般写 "data"；「年」「年-月」不写 Excel 日期序列（输入 2025 会被当成第 2025 天变成约 1905），
     * 按文本处理。</p>
     */
    public static String[] toExcelDataTypes(List<ColumnSpec> specs) {
        if (specs == null || specs.isEmpty()) {
            return new String[0];
        }
        String[] types = new String[specs.size()];
        for (int i = 0; i < specs.size(); i++) {
            ColumnSpec spec = specs.get(i);
            if (spec != null && isDateCellDataType(spec.getCellDataType())
                && DateTimeType.isExcelDateSerialByKey(normalizeCellDateFormat(spec.getCellDateFormat()))) {
                types[i] = EXCEL_DATA_TYPE_DATE;
            } else {
                types[i] = CELL_DATA_TYPE_TEXT;
            }
        }
        return types;
    }

    /**
     * 与 {@link #toExcelDataTypes} 等长的日期格式数组（Excel 模式串）；非日期序列列为 null。
     */
    public static String[] toExcelDateFormats(List<ColumnSpec> specs) {
        if (specs == null || specs.isEmpty()) {
            return new String[0];
        }
        String[] formats = new String[specs.size()];
        for (int i = 0; i < specs.size(); i++) {
            ColumnSpec spec = specs.get(i);
            if (spec != null && isDateCellDataType(spec.getCellDataType())
                && DateTimeType.isExcelDateSerialByKey(normalizeCellDateFormat(spec.getCellDateFormat()))) {
                formats[i] = toExcelDatePattern(spec.getCellDateFormat());
            } else {
                formats[i] = null;
            }
        }
        return formats;
    }

    /**
     * 多 Sheet 带「主表序号」列：首列为文本，其后与业务列一致。
     */
    public static String[] toExcelDataTypesWithLink(List<ColumnSpec> specs) {
        String[] business = toExcelDataTypes(specs);
        String[] types = new String[business.length + 1];
        types[0] = CELL_DATA_TYPE_TEXT;
        System.arraycopy(business, 0, types, 1, business.length);
        return types;
    }

    /**
     * 多 Sheet 带关联列：首列无日期格式，其后与业务列一致。
     */
    public static String[] toExcelDateFormatsWithLink(List<ColumnSpec> specs) {
        String[] business = toExcelDateFormats(specs);
        String[] formats = new String[business.length + 1];
        formats[0] = null;
        System.arraycopy(business, 0, formats, 1, business.length);
        return formats;
    }
}
