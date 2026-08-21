/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyeye.skill.exception.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName: ScreenToReportContentConverter
 * @Description: 把 Skills 的 screen JSON 转成 Skyeye 设计器 content 结构
 *               （contentWidth/Height + modelList + wordMationList）
 */
@Component
public class ScreenToReportContentConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> convert(Map<String, Object> screen) {
        int width = intVal(screen.get("width"), 1920);
        int height = intVal(screen.get("height"), 1080);
        String title = text(screen.get("title"), "数据监控大屏");
        String background = text(screen.get("background"), "#0b1a33");

        List<Map<String, Object>> modelList = new ArrayList<>();
        List<Map<String, Object>> wordMationList = new ArrayList<>();

        Object widgetsObj = screen.get("widgets");
        if (widgetsObj instanceof List) {
            List<?> widgets = (List<?>) widgetsObj;
            int index = 0;
            for (Object item : widgets) {
                if (!(item instanceof Map)) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> widget = (Map<String, Object>) item;
                String type = text(widget.get("type"), text(widget.get("name"), "kpi")).toLowerCase();
                if ("header".equals(type) || "kpi".equals(type)) {
                    wordMationList.add(toWord(widget, title, index++));
                } else if ("line".equals(type) || "bar".equals(type) || "pie".equals(type) || "table".equals(type)) {
                    modelList.add(toChart(widget, type, index++));
                } else {
                    // 未知类型当文字块
                    wordMationList.add(toWord(widget, title, index++));
                }
            }
        }

        if (wordMationList.isEmpty() && modelList.isEmpty()) {
            wordMationList.add(toWord(defaultHeader(title), title, 0));
        }

        Map<String, Object> content = new LinkedHashMap<>();
        content.put("contentWidth", width);
        content.put("contentHeight", height);
        content.put("bgImage", "none");
        content.put("background", background);
        content.put("title", title);
        content.put("source", "ai-skill");
        content.put("modelList", modelList);
        content.put("wordMationList", wordMationList);
        return content;
    }

    public String convertJson(Map<String, Object> screen) {
        try {
            return objectMapper.writeValueAsString(convert(screen));
        } catch (JsonProcessingException e) {
            throw new CustomException("转换报表 content 失败");
        }
    }

    private Map<String, Object> defaultHeader(String title) {
        Map<String, Object> w = new LinkedHashMap<>();
        w.put("id", "header");
        w.put("name", "标题栏");
        w.put("type", "header");
        w.put("x", 40);
        w.put("y", 20);
        w.put("w", 1840);
        w.put("h", 80);
        w.put("text", title);
        return w;
    }

    private Map<String, Object> toWord(Map<String, Object> widget, String pageTitle, int index) {
        String id = text(widget.get("id"), "word-" + index);
        String modelId = "skill-word-" + id;
        String name = text(widget.get("name"), "文字");
        String type = text(widget.get("type"), "kpi");
        String textContent = buildWordText(widget, type, pageTitle);
        int x = intVal(widget.get("x"), 40);
        int y = intVal(widget.get("y"), 20);
        int w = intVal(first(widget.get("w"), widget.get("width")), 400);
        int h = intVal(first(widget.get("h"), widget.get("height")), 80);

        Map<String, Object> attr = new LinkedHashMap<>();
        putAttr(attr, "custom.move.x", String.valueOf(x), "X坐标", "98", "坐标");
        putAttr(attr, "custom.move.y", String.valueOf(y), "Y坐标", "98", "坐标");
        putAttr(attr, "custom.textContent", textContent, "文字内容", "2", "文字");
        putAttr(attr, "custom.box.background", "rgba(11,26,51,0.85)", "背景色", "3", "盒子");
        putAttr(attr, "color", "#ffffff", "文字颜色", "3", "文字");
        putAttr(attr, "font-size", "header".equalsIgnoreCase(type) ? "36px" : "22px", "字号", "2", "文字");

        Map<String, Object> attrMation = new LinkedHashMap<>();
        attrMation.put("id", modelId);
        attrMation.put("title", name);
        attrMation.put("version", 1);
        attrMation.put("menuType", "wordModel");
        attrMation.put("image", "");
        attrMation.put("attr", attr);

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("modelId", modelId);
        item.put("width", w);
        item.put("height", h);
        item.put("attrMation", attrMation);
        return item;
    }

    private Map<String, Object> toChart(Map<String, Object> widget, String chartType, int index) {
        String id = text(widget.get("id"), "chart-" + index);
        String modelId = "skill-chart-" + id;
        String name = text(widget.get("name"), text(widget.get("text"), chartType));
        int x = intVal(widget.get("x"), 40);
        int y = intVal(widget.get("y"), 310);
        int w = intVal(first(widget.get("w"), widget.get("width")), 600);
        int h = intVal(first(widget.get("h"), widget.get("height")), 400);

        String seriesType = "table".equals(chartType) ? "bar" : chartType;
        List<String> xData = Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日");
        List<Object> seriesData = Arrays.asList(120, 200, 150, 80, 70, 110, 130);
        if ("pie".equals(chartType)) {
            seriesData = Arrays.asList(
                mapOf("name", "A", "value", 335),
                mapOf("name", "B", "value", 310),
                mapOf("name", "C", "value", 234)
            );
        }

        Map<String, Object> attr = new LinkedHashMap<>();
        putAttr(attr, "custom.move.x", String.valueOf(x), "X坐标", "98", "坐标");
        putAttr(attr, "custom.move.y", String.valueOf(y), "Y坐标", "98", "坐标");
        putAttr(attr, "custom.box.background", "rgba(11,26,51,0.85)", "背景色", "3", "盒子");
        putAttr(attr, "title.text", name, "标题", "2", "标题");
        putAttr(attr, "title.textStyle.color", "#ffffff", "标题颜色", "3", "标题");
        putAttr(attr, "series.type", seriesType, "图表类型", "1", "系列");
        putAttr(attr, "series.data", seriesData, "系列数据", "9", "系列");
        putAttr(attr, "xAxis.data", xData, "X轴数据", "9", "X轴");
        putAttr(attr, "xAxis.type", "category", "X轴类型", "1", "X轴");
        putAttr(attr, "yAxis.type", "value", "Y轴类型", "1", "Y轴");
        putAttr(attr, "tooltip.trigger", "axis", "提示触发", "1", "提示");
        putAttr(attr, "legend.show", true, "图例", "1", "图例");
        putAttr(attr, "custom.dataBaseMation", "", "数据来源", "99", "数据源");

        Map<String, Object> attrMation = new LinkedHashMap<>();
        attrMation.put("id", modelId);
        attrMation.put("title", name);
        attrMation.put("version", 1);
        attrMation.put("menuType", "echartsModel");
        attrMation.put("image", "");
        attrMation.put("attr", attr);

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("modelId", modelId);
        item.put("width", w);
        item.put("height", h);
        item.put("attrMation", attrMation);
        return item;
    }

    private String buildWordText(Map<String, Object> widget, String type, String pageTitle) {
        if ("header".equalsIgnoreCase(type)) {
            return text(first(widget.get("text"), widget.get("name")), pageTitle);
        }
        String name = text(widget.get("name"), text(widget.get("text"), "指标"));
        String value = text(widget.get("value"), "--");
        String delta = text(widget.get("delta"), "");
        if (StringUtils.hasText(delta)) {
            return name + "\n" + value + "  " + delta;
        }
        return name + "\n" + value;
    }

    private void putAttr(Map<String, Object> attr, String key, Object value,
                         String title, String editor, String typeName) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("value", value);
        item.put("edit", 1);
        item.put("editor", editor);
        item.put("title", title);
        item.put("typeName", typeName);
        item.put("desc", title);
        item.put("useKey", key);
        item.put("attrCode", key);
        item.put("editorChooseValue", "");
        attr.put(key, item);
    }

    private Map<String, Object> mapOf(String k1, Object v1, String k2, Object v2) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    private Object first(Object a, Object b) {
        return a != null ? a : b;
    }

    private int intVal(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return (int) Double.parseDouble(String.valueOf(value));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String text(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        String text = String.valueOf(value).trim();
        return StringUtils.hasText(text) ? text : defaultValue;
    }

    public String newId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
