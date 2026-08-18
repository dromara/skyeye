/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: BigScreenTemplateGenerator
 * @Description: 按固定模板生成 1920x1080 深色大屏 JSON（未接大模型）
 */
@Component
public class BigScreenTemplateGenerator {

    private static final Pattern TITLE_PATTERN = Pattern.compile("做[一个]{0,2}(.{2,20}?)大屏");

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> generate(String userInput) {
        String title = resolveTitle(userInput);
        Map<String, Object> screen = new LinkedHashMap<>();
        screen.put("mode", "bigScreen");
        screen.put("title", title);
        screen.put("width", 1920);
        screen.put("height", 1080);
        screen.put("theme", "dark");
        screen.put("background", "#0b1a33");
        screen.put("userInput", userInput == null ? "" : userInput);
        screen.put("source", "template");
        screen.put("remark", "由固定模板生成");

        List<Map<String, Object>> widgets = new ArrayList<>();
        widgets.add(widget("header", "标题栏", 40, 20, 1840, 80, title));
        widgets.add(kpi("kpi-1", "今日总额", 40, 120, 420, 160, "1286.5万", "+12.3%"));
        widgets.add(kpi("kpi-2", "订单数量", 500, 120, 420, 160, "3,842", "+6.1%"));
        widgets.add(kpi("kpi-3", "完成率", 960, 120, 420, 160, "92.4%", "+1.8%"));
        widgets.add(kpi("kpi-4", "异常数", 1420, 120, 460, 160, "17", "-4"));
        widgets.add(chart("trend", "近7日趋势", 40, 310, 1200, 420, "line"));
        widgets.add(chart("rank", "排行榜", 1280, 310, 600, 420, "bar"));
        widgets.add(chart("table", "明细列表", 40, 760, 1840, 280, "table"));
        screen.put("widgets", widgets);
        return screen;
    }

    public String generateJson(String userInput) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(generate(userInput));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("生成大屏JSON失败", e);
        }
    }

    private String resolveTitle(String userInput) {
        if (!StringUtils.hasText(userInput)) {
            return "数据监控大屏";
        }
        Matcher matcher = TITLE_PATTERN.matcher(userInput);
        if (matcher.find()) {
            return matcher.group(1).trim() + "大屏";
        }
        return userInput.length() > 20 ? userInput.substring(0, 20) + "大屏" : userInput;
    }

    private Map<String, Object> kpi(String id, String name, int x, int y, int w, int h, String value, String delta) {
        Map<String, Object> item = widget(id, name, x, y, w, h, name);
        item.put("type", "kpi");
        item.put("value", value);
        item.put("delta", delta);
        return item;
    }

    private Map<String, Object> chart(String id, String name, int x, int y, int w, int h, String chartType) {
        Map<String, Object> item = widget(id, name, x, y, w, h, name);
        item.put("type", chartType);
        return item;
    }

    private Map<String, Object> widget(String id, String name, int x, int y, int w, int h, String text) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("name", name);
        item.put("x", x);
        item.put("y", y);
        item.put("w", w);
        item.put("h", h);
        item.put("text", text);
        return item;
    }
}
