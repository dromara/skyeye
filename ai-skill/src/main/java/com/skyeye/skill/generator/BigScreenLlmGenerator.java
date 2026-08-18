/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.generator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyeye.skill.exception.CustomException;
import com.skyeye.skill.llm.LlmChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: BigScreenLlmGenerator
 * @Description: 用大模型按固定 JSON 结构生成 1920x1080 大屏
 */
@Component
public class BigScreenLlmGenerator {

    private static final Logger log = LoggerFactory.getLogger(BigScreenLlmGenerator.class);

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {
    };

    @Autowired
    private LlmChatService llmChatService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> generate(String userInput, String instruction) {
        String raw = llmChatService.chat(buildSystemPrompt(instruction), userInput);
        Map<String, Object> screen = parseScreen(raw);
        normalize(screen, userInput);
        return screen;
    }

    private String buildSystemPrompt(String instruction) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是数据大屏布局生成器。只输出一个 JSON 对象，不要 markdown，不要解释。\n");
        sb.append("画布必须是 1920x1080 深色全屏。JSON 结构必须是：\n");
        sb.append("{\n");
        sb.append("  \"mode\": \"bigScreen\",\n");
        sb.append("  \"title\": \"大屏标题\",\n");
        sb.append("  \"width\": 1920,\n");
        sb.append("  \"height\": 1080,\n");
        sb.append("  \"theme\": \"dark\",\n");
        sb.append("  \"background\": \"#0b1a33\",\n");
        sb.append("  \"widgets\": [\n");
        sb.append("    {\"id\":\"header\",\"name\":\"标题栏\",\"type\":\"header\",\"x\":40,\"y\":20,\"w\":1840,\"h\":80,\"text\":\"标题\"},\n");
        sb.append("    {\"id\":\"kpi-1\",\"name\":\"指标名\",\"type\":\"kpi\",\"x\":40,\"y\":120,\"w\":420,\"h\":160,\"text\":\"指标名\",\"value\":\"1286.5万\",\"delta\":\"+12.3%\"},\n");
        sb.append("    {\"id\":\"trend\",\"name\":\"趋势\",\"type\":\"line\",\"x\":40,\"y\":310,\"w\":1200,\"h\":420,\"text\":\"近7日趋势\"},\n");
        sb.append("    {\"id\":\"rank\",\"name\":\"排行\",\"type\":\"bar\",\"x\":1280,\"y\":310,\"w\":600,\"h\":420,\"text\":\"排行榜\"},\n");
        sb.append("    {\"id\":\"table\",\"name\":\"明细\",\"type\":\"table\",\"x\":40,\"y\":760,\"w\":1840,\"h\":280,\"text\":\"明细列表\"}\n");
        sb.append("  ]\n");
        sb.append("}\n");
        sb.append("规则：\n");
        sb.append("1. 必须有 1 个 header，3~6 个 kpi，至少 1 个 line 或 bar，建议有 table。\n");
        sb.append("2. type 只能是 header、kpi、line、bar、pie、table。\n");
        sb.append("3. 组件不要超出 1920x1080；标题、KPI 名称、图表名称必须贴合用户说的业务场景。\n");
        sb.append("4. value/delta 可以用合理示例数据。\n");
        sb.append("5. 只输出 JSON。\n");
        if (StringUtils.hasText(instruction)) {
            sb.append("额外说明书：\n").append(instruction).append("\n");
        }
        return sb.toString();
    }

    private Map<String, Object> parseScreen(String raw) {
        if (!StringUtils.hasText(raw)) {
            throw new CustomException("大模型返回为空");
        }
        String json = extractJson(raw);
        try {
            Map<String, Object> screen = objectMapper.readValue(json, MAP_TYPE);
            Object widgets = screen.get("widgets");
            if (!(widgets instanceof List) || ((List<?>) widgets).isEmpty()) {
                throw new CustomException("大模型 JSON 缺少 widgets");
            }
            return screen;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.warn("解析大模型 JSON 失败: {}", e.getMessage());
            throw new CustomException("大模型返回不是合法大屏JSON");
        }
    }

    private String extractJson(String raw) {
        String text = raw.trim();
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new CustomException("大模型返回中没有 JSON 对象");
        }
        return text.substring(start, end + 1);
    }

    private void normalize(Map<String, Object> screen, String userInput) {
        Map<String, Object> ordered = new LinkedHashMap<String, Object>();
        ordered.put("mode", "bigScreen");
        ordered.put("title", firstText(screen.get("title"), "数据监控大屏"));
        ordered.put("width", 1920);
        ordered.put("height", 1080);
        ordered.put("theme", firstText(screen.get("theme"), "dark"));
        ordered.put("background", firstText(screen.get("background"), "#0b1a33"));
        ordered.put("userInput", userInput == null ? "" : userInput);
        ordered.put("source", "llm");
        ordered.put("llmPlatform", llmChatService.currentPlatform());
        ordered.put("remark", "由大模型生成");
        ordered.put("widgets", screen.get("widgets"));
        screen.clear();
        screen.putAll(ordered);
    }

    private String firstText(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        String text = String.valueOf(value).trim();
        return StringUtils.hasText(text) ? text : defaultValue;
    }
}
