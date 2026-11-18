/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.skyeye.common.util.BarCodeUtil;
import com.skyeye.common.util.qrcode.QRCodeLogoUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.print.entity.PrintElement;
import com.skyeye.print.entity.PrintTemplate;
import com.skyeye.print.entity.TableColumn;
import com.skyeye.print.service.PrintHtmlGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.*;

/**
 * @ClassName: PrintHtmlGeneratorImpl
 * @Description: 打印html生成器实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/15 8:49
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PrintHtmlGeneratorImpl implements PrintHtmlGenerator {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${webroot.fileBath}")
    private String webRootfileBath;

    @Override
    public String generatePreviewImage(String html) {
        try {
            // 此处应实现实际的HTML转图片逻辑
            // 例如使用html2canvas+phantomjs或其他工具
            // 以下为模拟实现，生成一个唯一的预览图URL
            String previewId = UUID.randomUUID().toString().replace("-", "");
            return "/api/print/preview/image/" + previewId + ".png";
        } catch (Exception e) {
            log.error("生成预览图片失败", e);
            throw new CustomException("生成预览图片失败: " + e.getMessage());
        }
    }

    @Override
    public String generateHtml(PrintTemplate printTemplate, Map<String, Object> data) {
        try {
            // 解析模板配置
            JSONObject config = JSON.parseObject(printTemplate.getConfigContent());
            // 如果config为空，则返回空字符串
            if (config == null) {
                return "";
            }

            // 准备模板元素
            List<PrintElement> elements = prepareElements(config.getJSONArray("elements"), data);

            // 准备模板上下文
            Map<String, Object> context = new java.util.HashMap<>();
            context.put("template", printTemplate);
            context.put("elements", elements);
            context.put("data", data);

            // 渲染模板
            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(context);

            return templateEngine.process("print-template", thymeleafContext);
        } catch (Exception e) {
            log.error("生成打印HTML失败", e);
            throw new CustomException("生成打印HTML失败: " + e.getMessage());
        }
    }

    private List<PrintElement> prepareElements(JSONArray elements, Map<String, Object> data) {
        List<PrintElement> result = new ArrayList<>();

        if (elements == null) {
            return result;
        }

        for (int i = 0; i < elements.size(); i++) {
            JSONObject element = elements.getJSONObject(i);
            PrintElement processedElement = JSON.parseObject(element.toJSONString(), PrintElement.class);

            // 处理定位属性
            if (processedElement.getX() != null) {
                processedElement.setMarginLeft(processedElement.getX() + "px");
            }
            if (processedElement.getY() != null) {
                processedElement.setMarginTop(processedElement.getY() + "px");
            }

            // 处理动态数据
            if (StrUtil.isNotBlank(element.getString("dataField"))) {
                String dataField = element.getString("dataField");
                Object value = getValueByPath(data, dataField);
                processedElement.setContent(value != null ? value.toString() : null);
            } else if ("text".equals(processedElement.getType()) && StrUtil.isNotBlank(processedElement.getContent())) {
                // 处理文本内容中的变量
                String content = processedElement.getContent();
                processedElement.setContent(parseContentVariables(content, data));
            }

            // 处理特殊元素类型
            if ("table".equals(processedElement.getType())) {
                processTableElement(processedElement, data);
                // 与其它元素一致用 absolute，避免 relative 把后续绝对定位元素顶乱
                processedElement.setPosition("absolute");
            } else if ("table-container".equals(processedElement.getType())) {
                processTableContainerElement(processedElement, data);
            } else if ("barcode".equals(processedElement.getType())) {
                processBarcodeElement(processedElement, data);
            } else if ("image".equals(processedElement.getType())) {
                processImageElement(processedElement, data);
            }

            result.add(processedElement);
        }

        // 表格行数超出设计高度时，把下方元素整体下推，避免重叠
        pushDownBelowTables(result);
        return result;
    }

    /**
     * 表格实际高度大于设计高度时，下推表格下方（含设计时压进表格框内）的元素，避免重叠
     */
    private void pushDownBelowTables(List<PrintElement> elements) {
        if (elements == null || elements.isEmpty()) {
            return;
        }
        List<PrintElement> tables = new ArrayList<>();
        for (PrintElement element : elements) {
            if ("table".equals(element.getType())) {
                tables.add(element);
            }
        }
        tables.sort(Comparator.comparingInt(e -> e.getY() != null ? e.getY() : 0));

        for (PrintElement table : tables) {
            int designedH = parsePx(table.getHeight());
            int actualH = estimateTableHeight(table);
            if (designedH <= 0 || actualH <= designedH) {
                continue;
            }
            int delta = actualH - designedH;
            int tableX = table.getX() != null ? table.getX() : 0;
            int tableY = table.getY() != null ? table.getY() : 0;
            int tableW = parsePx(table.getWidth());

            for (PrintElement other : elements) {
                if (other == table) {
                    continue;
                }
                int otherX = other.getX() != null ? other.getX() : 0;
                int otherY = other.getY() != null ? other.getY() : 0;
                int otherW = parsePx(other.getWidth());
                // 完全在表格左右外侧的不推
                boolean sideBySide = otherX >= tableX + tableW || otherX + otherW <= tableX;
                // 顶边在表格顶边之下的（含原先塞在设计高度内的备注等）都下移
                if (!sideBySide && otherY > tableY) {
                    int newY = otherY + delta;
                    other.setY(newY);
                    other.setMarginTop(newY + "px");
                }
            }
            table.setHeight(String.valueOf(actualH));
        }
    }

    /**
     * 按行列内容估算表格高度（换行列按字数折算行数，避免低估导致 PDF 重叠）
     */
    private int estimateTableHeight(PrintElement table) {
        int baseRowH = table.getRowHeight() != null && table.getRowHeight() > 0 ? table.getRowHeight() : 36;
        int headerH = Boolean.TRUE.equals(table.getShowHeader()) ? baseRowH : 0;
        List<Map<String, Object>> rows = table.getRows();
        if (rows == null || rows.isEmpty()) {
            return headerH;
        }
        int total = headerH;
        List<TableColumn> columns = table.getColumns();
        for (Map<String, Object> row : rows) {
            int rowLines = 1;
            if (columns != null) {
                for (TableColumn col : columns) {
                    Object val = row.get(col.getField());
                    String text = val != null ? String.valueOf(val) : "";
                    if (StrUtil.isBlank(text)) {
                        continue;
                    }
                    // wrap 默认 true（与设计器一致）；false 才按单行
                    if (Boolean.FALSE.equals(col.getWrap())) {
                        continue;
                    }
                    int fontSize = col.getFontSize() != null && col.getFontSize() > 0 ? col.getFontSize() : 12;
                    int colW = col.getWidth() != null && col.getWidth() > 0 ? col.getWidth() : 100;
                    int contentW = Math.max(colW - 16, fontSize);
                    int charsPerLine = Math.max(contentW / fontSize, 1);
                    int linesByWidth = (int) Math.ceil((double) text.length() / charsPerLine);
                    int linesByBreak = text.split("\r?\n", -1).length;
                    rowLines = Math.max(rowLines, Math.max(linesByWidth, linesByBreak));
                }
            }
            int lineBox = Math.max(fontSizeLike(table), Math.max(baseRowH / 2, 14));
            int thisRowH = Math.max(baseRowH, rowLines * lineBox + 10);
            total += thisRowH;
        }
        // 略留余量，避免 iText 换行比估算更高
        return total + 4;
    }

    private int fontSizeLike(PrintElement table) {
        if (table.getFontSize() != null && table.getFontSize() > 0) {
            return table.getFontSize();
        }
        return 14;
    }

    private void processTableElement(PrintElement element, Map<String, Object> data) {
        // 处理表格数据源
        if (element.getRows() == null && StrUtil.isNotBlank(element.getValue())) {
            // 解析数据源变量
            Object tableData;
            String content = element.getValue();

            // 支持${xxx}格式的变量
            if (content.startsWith("${") && content.endsWith("}")) {
                String variable = content.substring(2, content.length() - 1);
                tableData = getValueByPath(data, variable);
            } else {
                tableData = getValueByPath(data, content);
            }

            // 设置表格数据
            if (tableData instanceof List && element.getColumns() != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> sourceRows = (List<Map<String, Object>>) tableData;
                List<Map<String, Object>> processedRows = new ArrayList<>();

                // 根据列配置处理每一行数据
                for (Map<String, Object> sourceRow : sourceRows) {
                    Map<String, Object> processedRow = new HashMap<>();
                    for (TableColumn column : element.getColumns()) {
                        if (StrUtil.isNotBlank(column.getField())) {
                            // 解析字段内容中的变量
                            String fieldContent = parseContentVariables(column.getField(), sourceRow);
                            processedRow.put(column.getField(), fieldContent);
                        }
                    }
                    processedRows.add(processedRow);
                }
                element.setRows(processedRows);
            }
        }
        fitTableColumnsToWidth(element);
    }

    /**
     * 列宽之和与表格宽度不一致时按比例缩放，避免 PDF/预览横向撑出纸面
     */
    private void fitTableColumnsToWidth(PrintElement element) {
        if (element == null || element.getColumns() == null || element.getColumns().isEmpty()) {
            return;
        }
        int tableWidth = parsePx(element.getWidth());
        if (tableWidth <= 0) {
            return;
        }
        int sum = 0;
        for (TableColumn column : element.getColumns()) {
            sum += column.getWidth() != null && column.getWidth() > 0 ? column.getWidth() : 100;
        }
        if (sum <= 0 || sum == tableWidth) {
            return;
        }
        double ratio = (double) tableWidth / sum;
        int allocated = 0;
        List<TableColumn> columns = element.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            TableColumn column = columns.get(i);
            int raw = column.getWidth() != null && column.getWidth() > 0 ? column.getWidth() : 100;
            if (i == columns.size() - 1) {
                column.setWidth(Math.max(tableWidth - allocated, 20));
            } else {
                int w = Math.max((int) Math.floor(raw * ratio), 20);
                column.setWidth(w);
                allocated += w;
            }
        }
    }

    private int parsePx(String value) {
        if (StrUtil.isBlank(value)) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim().toLowerCase().replace("px", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void processBarcodeElement(PrintElement element, Map<String, Object> data) {
        // 处理条码值
        if (StrUtil.isNotBlank(element.getValue())) {
            // 变量类型，解析变量
            String value = element.getValue();
            if (value.startsWith("${") && value.endsWith("}")) {
                element.setValueType("variable");
                String variable = value.substring(2, value.length() - 1);
                Object barcodeValue = getValueByPath(data, variable);
                if (barcodeValue != null) {
                    element.setValue(barcodeValue.toString());
                }
            }
        }

        // 设置默认值
        if (StrUtil.isBlank(element.getValueType())) {
            element.setValueType("static"); // 默认为静态值
        }
        if (StrUtil.isBlank(element.getBarcodeType())) {
            element.setBarcodeType("barcode");
        }
        if (StrUtil.isBlank(element.getFormat())) {
            element.setFormat("CODE128");
        }
        if (StrUtil.isBlank(element.getForeground())) {
            element.setForeground("#000000");
        }
        if (StrUtil.isBlank(element.getBackground())) {
            element.setBackground("#ffffff");
        }
        if (StrUtil.isBlank(element.getErrorLevel())) {
            element.setErrorLevel("M");
        }
        if (element.getBarWidth() == null) {
            element.setBarWidth(2);
        }
        if (element.getFontSize() == null) {
            element.setFontSize(14);
        }
        if (StrUtil.isBlank(element.getTextPosition())) {
            element.setTextPosition("bottom");
        }

        // 生成Base64字符串
        if (StrUtil.isNotBlank(element.getValue())) {
            String base64;
            Integer width = Integer.parseInt(element.getWidth().replace("px", "").trim());
            Integer height = Integer.parseInt(element.getHeight().replace("px", "").trim());
            if ("barcode".equals(element.getBarcodeType())) {
                // 生成一维码Base64
                base64 = BarCodeUtil.generateBarcodeBase64(
                    element.getValue(),
                    element.getBarWidth(),
                    height - (element.getShowLabel() ? 20 : 0),
                    element.getFormat(),
                    element.getForeground(),
                    element.getBackground(),
                    element.getShowLabel(),
                    element.getFontSize(),
                    5 // 固定边距
                );
            } else {
                // 生成二维码Base64
                base64 = QRCodeLogoUtil.generateQRCodeBase64(
                    element.getValue(),
                    Math.min(width, height),
                    element.getForeground(),
                    element.getBackground(),
                    element.getErrorLevel(),
                    1 // 固定边距
                );
            }

            // 设置生成的Base64字符串
            if (StrUtil.isNotBlank(base64)) {
                element.setValue(base64);
            }
        }
    }

    private void processImageElement(PrintElement element, Map<String, Object> data) {
        // 根据图片来源类型处理
        String sourceType = element.getSourceType();

        if ("upload".equals(sourceType)) {
            // 上传图片，直接使用url
            element.setSrc(element.getUrl());
        } else if ("variable".equals(sourceType)) {
            // 变量图片，从数据中获取
            if (StrUtil.isNotBlank(element.getValue())) {
                String content = element.getValue();
                Object imageData;

                // 支持${xxx}格式的变量
                if (content.startsWith("${") && content.endsWith("}")) {
                    String variable = content.substring(2, content.length() - 1);
                    imageData = getValueByPath(data, variable);
                } else {
                    imageData = getValueByPath(data, content);
                }

                // 设置图片src
                if (imageData != null) {
                    element.setSrc(imageData.toString());
                }
            }
        }

        if (StrUtil.isNotEmpty(element.getSrc())) {
            element.setSrc(webRootfileBath + element.getUrl());
        }

        // 设置图片填充方式
        if (StrUtil.isBlank(element.getFit())) {
            element.setFit("contain"); // 默认填充方式
        }
    }

    /**
     * 处理表格容器元素
     *
     * @param element 表格容器元素
     * @param data    业务数据
     */
    private void processTableContainerElement(PrintElement element, Map<String, Object> data) {
        // 处理单元格内的元素
        if (element.getCells() != null) {
            for (com.skyeye.print.entity.TableCell cell : element.getCells()) {
                if (cell.getElements() != null) {
                    // 递归处理单元格内的每个元素
                    for (PrintElement cellElement : cell.getElements()) {
                        // 处理定位属性
                        if (cellElement.getX() != null) {
                            cellElement.setMarginLeft(cellElement.getX() + "px");
                        }
                        if (cellElement.getY() != null) {
                            cellElement.setMarginTop(cellElement.getY() + "px");
                        }

                        // 处理动态数据
                        if (StrUtil.isNotBlank(cellElement.getContent())) {
                            if ("text".equals(cellElement.getType())) {
                                // 处理文本内容中的变量
                                String content = cellElement.getContent();
                                cellElement.setContent(parseContentVariables(content, data));
                            }
                        }

                        // 处理特殊元素类型
                        if ("table".equals(cellElement.getType())) {
                            processTableElement(cellElement, data);
                            cellElement.setPosition("absolute");
                        } else if ("barcode".equals(cellElement.getType())) {
                            processBarcodeElement(cellElement, data);
                        } else if ("image".equals(cellElement.getType())) {
                            processImageElement(cellElement, data);
                        }
                    }
                }
            }
        }

        // 设置默认值
        if (element.getContainerRows() == null) {
            element.setContainerRows(3);
        }
        if (element.getContainerCols() == null) {
            element.setContainerCols(3);
        }
        if (element.getCellWidth() == null) {
            element.setCellWidth(120);
        }
        if (element.getCellHeight() == null) {
            element.setCellHeight(80);
        }
        if (StrUtil.isBlank(element.getBorderStyle())) {
            element.setBorderStyle("solid");
        }
        if (element.getBorderWidth() == null) {
            element.setBorderWidth(1);
        }
        if (StrUtil.isBlank(element.getBorderColor())) {
            element.setBorderColor("#d9d9d9");
        }
    }

    private Object getValueByPath(Map<String, Object> data, String path) {
        if (data == null || StrUtil.isBlank(path)) {
            return null;
        }

        // 支持嵌套属性访问，如 orderInfo.orderNo
        String[] parts = path.split("\\.");
        Object current = data;

        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
                if (current == null) {
                    return null;
                }
            } else {
                return null;
            }
        }

        return current;
    }

    /**
     * 解析文本内容中的变量
     *
     * @param content 原始内容
     * @param data    数据
     * @return 解析后的内容
     */
    private String parseContentVariables(String content, Map<String, Object> data) {
        if (content == null || data == null) {
            return content;
        }

        // 匹配${xxx}格式的变量
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\$\\{([^}]+)}");
        java.util.regex.Matcher matcher = pattern.matcher(content);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String variable = matcher.group(1);
            Object value = getValueByPath(data, variable);
            String replacement = value != null ? value.toString() : "";
            matcher.appendReplacement(result, java.util.regex.Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
