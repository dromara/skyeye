/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.common.util.ImportExportRowUtil;
import com.skyeye.service.JobMateMationService;
import com.skyeye.exception.CustomException;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读取 DataApi 落盘的 JSON（仅传访问路径 filePath），在本地拼绝对路径后转 Excel。
 */
@Component
@RocketMQMessageListener(
    topic = "${topic.import-export-json-to-excel-service}",
    consumerGroup = "${topic.import-export-json-to-excel-service}",
    selectorExpression = "${spring.profiles.active}")
public class ImportExportJsonToExcelConsume implements RocketMQListener<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportExportJsonToExcelConsume.class);

    /** 超过此大小的 JSON 走 Jackson 流式解析 + SXSSF 写 xlsx，避免整文件进内存 */
    private static final long MAX_JSON_FILE_BYTES_BUFFERED = 50L * 1024 * 1024;

    private static final int SXSSF_ROW_ACCESS_WINDOW = 500;

    @Autowired
    private JobMateMationService jobMateMationService;

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    /**
     * 异步导出消费：JSON 数据文件 → Excel。
     * <pre>
     * 1. 标记任务处理中，解析租户 / JSON 路径 / Sheet 模式
     * 2. 多 Sheet 或小文件：整包读入内存后写 .xls
     * 3. 单 Sheet 大文件：SXSSF 流式读 JSON 写 .xlsx（边展开明细边写）
     * 4. 删掉临时 JSON，回写访问路径并标记成功/失败
     * </pre>
     */
    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        String jobId = map.get("jobMateId").toString();
        Map<String, Object> mation = new HashMap<>();
        try {
            // ① 租户上下文 + 任务状态
            String tenantId = map.getOrDefault("tenantId", StrUtil.EMPTY).toString();
            if (tenantEnable) {
                TenantContext.setTenantId(tenantId);
            }
            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, StrUtil.EMPTY);

            // ② 定位数据服务落盘的 JSON
            String visitJsonPath = map.get("filePath").toString();
            Path jsonPath = resolveAbsolutePathFromVisitFilePath(visitJsonPath);
            if (!Files.isRegularFile(jsonPath)) {
                throw new CustomException("导出 JSON 文件不存在: " + jsonPath);
            }

            // ③ 解析 Sheet 模式与明细集合键
            String sheetMode = map.get("sheetMode") == null ? "single" : String.valueOf(map.get("sheetMode"));
            List<String> collectionAttrKeys = toStringList(map.get("collectionAttrKeys"));
            String collectionAttrKey = map.get("collectionAttrKey") == null ? null : String.valueOf(map.get("collectionAttrKey"));
            if (StrUtil.isBlank(collectionAttrKey) || "null".equals(collectionAttrKey)) {
                collectionAttrKey = null;
            }
            if (collectionAttrKeys.isEmpty() && StrUtil.isNotBlank(collectionAttrKey)) {
                collectionAttrKeys.add(collectionAttrKey);
            }

            int exportType = FileConstants.FileUploadPath.EXPORT_DATA.getType()[0];
            String saveDir = tPath + FileConstants.FileUploadPath.getSavePath(exportType);
            Files.createDirectories(Paths.get(saveDir));

            long jsonBytes = Files.size(jsonPath);
            String excelFileName;
            Path outPath;
            boolean multi = "multi".equalsIgnoreCase(sheetMode) && !collectionAttrKeys.isEmpty();
            if (multi || jsonBytes <= MAX_JSON_FILE_BYTES_BUFFERED) {
                // ④a 内存写出：多 Sheet 或小 JSON → .xls
                String jsonContent = new String(Files.readAllBytes(jsonPath), StandardCharsets.UTF_8);
                JSONArray arr = JSONUtil.parseArray(jsonContent);
                List<Map<String, Object>> rows = new ArrayList<>();
                for (int i = 0; i < arr.size(); i++) {
                    rows.add(arr.getJSONObject(i));
                }
                excelFileName = "export-excel-" + System.currentTimeMillis() + ".xls";
                outPath = Paths.get(saveDir).resolve(excelFileName);
                String title = map.containsKey("title") ? map.get("title").toString() : "导出数据";
                if (multi) {
                    writeMultiSheetAsync(map, rows, collectionAttrKeys, outPath.toFile());
                } else {
                    String[] keys = toStringArray(map.get("keys"));
                    String[] columnNames = toStringArray(map.get("columnNames"));
                    if (keys.length == 0 || columnNames.length == 0 || keys.length != columnNames.length) {
                        throw new CustomException("导入导出异步任务列配置无效.");
                    }
                    // 单 Sheet 多集合并排展开
                    if (!collectionAttrKeys.isEmpty()) {
                        rows = ImportExportRowUtil.flattenByCollections(rows, collectionAttrKeys);
                    }
                    ExcelUtil.SheetExportStyle exportStyle = parseExportStyle(map.get("exportStyleJson"));
                    // 列类型唯一来源：exportStyle.columnDataTypes（任务组装时已写入 exportStyleJson）
                    String[] dataTypes = exportStyle != null && exportStyle.columnDataTypes != null
                        ? exportStyle.columnDataTypes : new String[0];
                    ExcelUtil.createWorkBookToFile(title, "导出数据", rows, keys, columnNames, dataTypes, outPath.toFile(), exportStyle);
                }
            } else {
                // ④b 流式写出：超大单 Sheet → SXSSF .xlsx
                String[] keys = toStringArray(map.get("keys"));
                String[] columnNames = toStringArray(map.get("columnNames"));
                if (keys.length == 0 || columnNames.length == 0 || keys.length != columnNames.length) {
                    throw new CustomException("导入导出异步任务列配置无效.");
                }
                excelFileName = "export-excel-" + System.currentTimeMillis() + ".xlsx";
                outPath = Paths.get(saveDir).resolve(excelFileName);
                // SXSSF 只从 exportStyle 读 columnDataTypes / columnDateFormats
                ExcelUtil.SheetExportStyle exportStyle = parseExportStyle(map.get("exportStyleJson"));
                ExcelUtil.createSxssfExcelFromJsonArrayFile(jsonPath.toFile(), "导出数据", keys, columnNames,
                    outPath.toFile(), SXSSF_ROW_ACCESS_WINDOW, exportStyle, collectionAttrKeys);
            }
            // ⑤ 清理 JSON，回传 Excel 访问路径
            Files.deleteIfExists(jsonPath);

            String visitExcel = FileConstants.FileUploadPath.getVisitPath(exportType) + excelFileName;
            mation.put("filePath", visitExcel);
            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, JSONUtil.toJsonStr(mation));
        } catch (Exception e) {
            LOGGER.info("import export json to excel job fail, message is {}", e.getMessage(), e);
            mation.put("message", e.getMessage());
            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, JSONUtil.toJsonStr(mation));
        }
    }

    private Path resolveAbsolutePathFromVisitFilePath(String visitFilePath) {
        if (StrUtil.isBlank(visitFilePath)) {
            throw new CustomException("filePath 为空.");
        }
        String relative = visitFilePath.replace("/images/", "");
        if (StrUtil.isBlank(relative)) {
            throw new CustomException("无法从访问路径解析本地路径: " + visitFilePath);
        }
        if (relative.startsWith("/")) {
            relative = relative.substring(1);
        }
        return Paths.get(tPath.trim()).resolve(relative);
    }

    private static ExcelUtil.SheetExportStyle parseExportStyle(Object raw) {
        if (raw == null) {
            return null;
        }
        String jsonStr;
        if (raw instanceof String) {
            jsonStr = (String) raw;
        } else if (raw instanceof Map) {
            jsonStr = JSONUtil.toJsonStr(raw);
        } else {
            jsonStr = String.valueOf(raw);
        }
        if (StrUtil.isBlank(jsonStr) || "{}".equals(jsonStr.trim())) {
            return null;
        }
        try {
            JSONObject o = JSONUtil.parseObj(jsonStr);
            ExcelUtil.SheetExportStyle s = new ExcelUtil.SheetExportStyle();
            if (o.containsKey("headerRowHeight")) {
                Float h = o.getFloat("headerRowHeight");
                if (h != null && h > 0) {
                    s.headerRowHeight = h;
                }
            }
            if (o.containsKey("dataRowHeight")) {
                Float h = o.getFloat("dataRowHeight");
                if (h != null && h > 0) {
                    s.dataRowHeight = h;
                }
            }
            JSONArray cw = o.getJSONArray("columnWidths");
            if (cw != null && !cw.isEmpty()) {
                s.columnWidths = new int[cw.size()];
                for (int i = 0; i < cw.size(); i++) {
                    Object v = cw.get(i);
                    if (v == null) {
                        s.columnWidths[i] = -1;
                    } else if (v instanceof Number) {
                        s.columnWidths[i] = ((Number) v).intValue();
                    } else {
                        s.columnWidths[i] = -1;
                    }
                }
            }
            JSONArray bg = o.getJSONArray("headerBackgroundColors");
            if (bg != null && !bg.isEmpty()) {
                s.headerBackgroundColors = new String[bg.size()];
                for (int i = 0; i < bg.size(); i++) {
                    Object v = bg.get(i);
                    s.headerBackgroundColors[i] = v == null || StrUtil.isBlank(String.valueOf(v)) ? null : String.valueOf(v);
                }
            }
            JSONArray fg = o.getJSONArray("headerFontColors");
            if (fg != null && !fg.isEmpty()) {
                s.headerFontColors = new String[fg.size()];
                for (int i = 0; i < fg.size(); i++) {
                    Object v = fg.get(i);
                    s.headerFontColors[i] = v == null || StrUtil.isBlank(String.valueOf(v)) ? null : String.valueOf(v);
                }
            }
            JSONArray groups = o.getJSONArray("headerGroupNames");
            if (groups != null && !groups.isEmpty()) {
                s.headerGroupNames = new String[groups.size()];
                for (int i = 0; i < groups.size(); i++) {
                    Object v = groups.get(i);
                    s.headerGroupNames[i] = v == null || StrUtil.isBlank(String.valueOf(v)) ? null : String.valueOf(v);
                }
            }
            JSONArray groupBg = o.getJSONArray("headerGroupBackgroundColors");
            if (groupBg != null && !groupBg.isEmpty()) {
                s.headerGroupBackgroundColors = new String[groupBg.size()];
                for (int i = 0; i < groupBg.size(); i++) {
                    Object v = groupBg.get(i);
                    s.headerGroupBackgroundColors[i] = v == null || StrUtil.isBlank(String.valueOf(v)) ? null : String.valueOf(v);
                }
            }
            JSONArray groupFg = o.getJSONArray("headerGroupFontColors");
            if (groupFg != null && !groupFg.isEmpty()) {
                s.headerGroupFontColors = new String[groupFg.size()];
                for (int i = 0; i < groupFg.size(); i++) {
                    Object v = groupFg.get(i);
                    s.headerGroupFontColors[i] = v == null || StrUtil.isBlank(String.valueOf(v)) ? null : String.valueOf(v);
                }
            }
            JSONArray dropdowns = o.getJSONArray("columnDropdownOptions");
            if (dropdowns != null && !dropdowns.isEmpty()) {
                s.columnDropdownOptions = new String[dropdowns.size()][];
                for (int i = 0; i < dropdowns.size(); i++) {
                    Object cell = dropdowns.get(i);
                    if (cell instanceof JSONArray) {
                        JSONArray arr = (JSONArray) cell;
                        String[] opts = new String[arr.size()];
                        for (int j = 0; j < arr.size(); j++) {
                            Object v = arr.get(j);
                            opts[j] = v == null ? null : String.valueOf(v);
                        }
                        s.columnDropdownOptions[i] = opts;
                    }
                }
            }
            if (o.containsKey("writeAttrKeyRow")) {
                s.writeAttrKeyRow = o.getBool("writeAttrKeyRow", false);
            }
            if (o.containsKey("columnDataTypes")) {
                JSONArray arr = o.getJSONArray("columnDataTypes");
                if (arr != null && !arr.isEmpty()) {
                    String[] types = new String[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        types[i] = arr.getStr(i);
                    }
                    s.columnDataTypes = types;
                }
            }
            if (o.containsKey("columnDateFormats")) {
                JSONArray arr = o.getJSONArray("columnDateFormats");
                if (arr != null && !arr.isEmpty()) {
                    String[] formats = new String[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        String f = arr.getStr(i);
                        formats[i] = StrUtil.isBlank(f) ? null : f;
                    }
                    s.columnDateFormats = formats;
                }
            }
            return s;
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void writeMultiSheetAsync(Map<String, Object> map, List<Map<String, Object>> rows,
                                      List<String> collectionAttrKeys, java.io.File outFile) {
        String[] masterKeys = toStringArray(map.get("masterKeys"));
        String[] masterColumnNames = toStringArray(map.get("masterColumnNames"));
        if (masterKeys.length == 0) {
            throw new CustomException("多 Sheet 异步任务主表列配置无效.");
        }
        ImportExportRowUtil.MultiSheetRows split = ImportExportRowUtil.splitToMultiSheets(rows, collectionAttrKeys);
        List<Map<String, Object>> sheetDefs = new ArrayList<>();
        Map<String, Object> masterDef = new HashMap<>();
        masterDef.put("sheetName", "主表");
        masterDef.put("keys", masterKeys);
        masterDef.put("columnNames", masterColumnNames);
        ExcelUtil.SheetExportStyle masterStyle = parseExportStyle(map.get("masterExportStyleJson"));
        masterDef.put("dataType", masterStyle != null ? masterStyle.columnDataTypes : null);
        masterDef.put("rows", split.getMasterRows());
        masterDef.put("exportStyle", masterStyle);
        sheetDefs.add(masterDef);

        Object detailSheetsObj = map.get("detailSheets");
        if (detailSheetsObj instanceof List) {
            for (Object item : (List<?>) detailSheetsObj) {
                Map<String, Object> ds = item instanceof Map ? (Map<String, Object>) item
                    : JSONUtil.toBean(JSONUtil.toJsonStr(item), Map.class);
                if (ds == null) {
                    continue;
                }
                String collectionAttrKey = ds.get("collectionAttrKey") == null ? null : String.valueOf(ds.get("collectionAttrKey"));
                String sheetName = ds.get("sheetName") == null ? collectionAttrKey : String.valueOf(ds.get("sheetName"));
                String[] detailKeys = toStringArray(ds.get("keys"));
                String[] detailColumnNames = toStringArray(ds.get("columnNames"));
                if (StrUtil.isBlank(collectionAttrKey) || detailKeys.length == 0) {
                    continue;
                }
                Map<String, Object> detailDef = new HashMap<>();
                detailDef.put("sheetName", sheetName);
                detailDef.put("keys", detailKeys);
                detailDef.put("columnNames", detailColumnNames);
                ExcelUtil.SheetExportStyle detailStyle = parseExportStyle(ds.get("exportStyleJson"));
                detailDef.put("dataType", detailStyle != null ? detailStyle.columnDataTypes : null);
                detailDef.put("rows", split.getDetailRowsByCollection()
                    .getOrDefault(collectionAttrKey, new ArrayList<>()));
                detailDef.put("exportStyle", detailStyle);
                sheetDefs.add(detailDef);
            }
        } else if (collectionAttrKeys.size() == 1) {
            // 兼容旧任务格式
            String collectionAttrKey = collectionAttrKeys.get(0);
            Map<String, Object> detailDef = new HashMap<>();
            detailDef.put("sheetName", map.getOrDefault("detailSheetName", collectionAttrKey));
            detailDef.put("keys", toStringArray(map.get("detailKeys")));
            detailDef.put("columnNames", toStringArray(map.get("detailColumnNames")));
            detailDef.put("rows", split.getDetailRowsByCollection()
                .getOrDefault(collectionAttrKey, new ArrayList<>()));
            detailDef.put("exportStyle", parseExportStyle(map.get("detailExportStyleJson")));
            sheetDefs.add(detailDef);
        }
        ExcelUtil.createMultiSheetWorkBookToFile(sheetDefs, outFile);
    }

    private static List<String> toStringList(Object o) {
        List<String> list = new ArrayList<>();
        if (o == null) {
            return list;
        }
        if (o instanceof List) {
            for (Object item : (List<?>) o) {
                if (item != null && StrUtil.isNotBlank(String.valueOf(item))) {
                    list.add(String.valueOf(item));
                }
            }
            return list;
        }
        if (o instanceof String[]) {
            for (String s : (String[]) o) {
                if (StrUtil.isNotBlank(s)) {
                    list.add(s);
                }
            }
        }
        return list;
    }

    private static String[] toStringArray(Object o) {
        if (o == null) {
            return new String[0];
        }
        if (o instanceof String[]) {
            return (String[]) o;
        }
        if (o instanceof List) {
            List<?> list = (List<?>) o;
            String[] arr = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                arr[i] = list.get(i) == null ? "" : list.get(i).toString();
            }
            return arr;
        }
        return new String[0];
    }
}
