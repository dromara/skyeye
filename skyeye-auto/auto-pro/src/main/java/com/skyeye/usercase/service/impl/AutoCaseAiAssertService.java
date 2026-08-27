/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.usercase.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.util.AutoAiChatHelper;
import com.skyeye.ai.util.AutoAiJsonHelper;
import com.skyeye.attr.classenum.AttrSymbols;
import com.skyeye.exception.CustomException;
import com.skyeye.usercase.classenum.AutoValueFromTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 步骤断言 AI 建议（不落库）。
 */
@Service
public class AutoCaseAiAssertService {

    private static final int MAX_ASSERT_COUNT = 20;

    private static final Set<String> VALID_OPERATORS = new HashSet<>();

    private static final Map<String, String> OPERATOR_ALIASES = new LinkedHashMap<>();

    static {
        for (AttrSymbols symbol : AttrSymbols.values()) {
            VALID_OPERATORS.add(symbol.getKey());
        }
        OPERATOR_ALIASES.put("equal", AttrSymbols.EQUAL_TO.getKey());
        OPERATOR_ALIASES.put("equals", AttrSymbols.EQUAL_TO.getKey());
        OPERATOR_ALIASES.put("==", AttrSymbols.EQUAL_TO.getKey());
        OPERATOR_ALIASES.put("notequal", AttrSymbols.NOT_EQUAL.getKey());
        OPERATOR_ALIASES.put("!=", AttrSymbols.NOT_EQUAL.getKey());
        OPERATOR_ALIASES.put("lessthan", AttrSymbols.LESS_THAN.getKey());
        OPERATOR_ALIASES.put("<", AttrSymbols.LESS_THAN.getKey());
        OPERATOR_ALIASES.put("greaterthan", AttrSymbols.GREATER_THAN.getKey());
        OPERATOR_ALIASES.put(">", AttrSymbols.GREATER_THAN.getKey());
        OPERATOR_ALIASES.put("lessthanorequal", AttrSymbols.LESS_THAN_OR_EQUAL.getKey());
        OPERATOR_ALIASES.put("<=", AttrSymbols.LESS_THAN_OR_EQUAL.getKey());
        OPERATOR_ALIASES.put("greaterthanorequal", AttrSymbols.GREATER_THAN_OR_EQUAL.getKey());
        OPERATOR_ALIASES.put(">=", AttrSymbols.GREATER_THAN_OR_EQUAL.getKey());
        OPERATOR_ALIASES.put("contains", AttrSymbols.CONTAIN.getKey());
        OPERATOR_ALIASES.put("include", AttrSymbols.CONTAIN.getKey());
        OPERATOR_ALIASES.put("等于", AttrSymbols.EQUAL_TO.getKey());
        OPERATOR_ALIASES.put("不等于", AttrSymbols.NOT_EQUAL.getKey());
        OPERATOR_ALIASES.put("小于", AttrSymbols.LESS_THAN.getKey());
        OPERATOR_ALIASES.put("大于", AttrSymbols.GREATER_THAN.getKey());
        OPERATOR_ALIASES.put("小于等于", AttrSymbols.LESS_THAN_OR_EQUAL.getKey());
        OPERATOR_ALIASES.put("大于等于", AttrSymbols.GREATER_THAN_OR_EQUAL.getKey());
        OPERATOR_ALIASES.put("包含", AttrSymbols.CONTAIN.getKey());
    }

    @Autowired
    private AutoAiChatHelper autoAiChatHelper;

    public Map<String, Object> generate(Map<String, Object> params) {
        String resultKey = params.get("resultKey") == null ? "" : params.get("resultKey").toString().trim();
        if (StrUtil.isBlank(resultKey)) {
            throw new CustomException("步骤编码不能为空");
        }
        String stepName = params.get("stepName") == null ? "" : params.get("stepName").toString();
        Object outputObj = params.get("output");
        if (outputObj == null || StrUtil.isBlank(String.valueOf(outputObj))) {
            throw new CustomException("步骤输出不能为空");
        }
        String outputJson = AutoAiJsonHelper.normalizeJsonText(outputObj);
        String existingAssertJson = params.get("existingAssertList") == null
            ? "[]" : AutoAiJsonHelper.normalizeJsonText(params.get("existingAssertList"));
        return autoAiChatHelper.startStreamingChat(
            buildUserContent(resultKey, stepName, outputJson, existingAssertJson),
            "stepAssertSuggest");
    }

    public Map<String, Object> parseAnswer(Map<String, Object> params) {
        String resultKey = params.get("resultKey") == null ? "" : params.get("resultKey").toString().trim();
        return parseAssertAnswer(autoAiChatHelper.requireAnswer(params), resultKey);
    }

    private String buildUserContent(String resultKey, String stepName, String outputJson, String existingAssertJson) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是自动化测试断言助手。根据步骤试跑输出，生成合理的断言配置，用于接口/数据库步骤结果校验。\n");
        sb.append("步骤名称：").append(StrUtil.blankToDefault(stepName, "无")).append("\n");
        sb.append("步骤编码(resultKey)：").append(resultKey).append("\n");
        sb.append("试跑输出(JSON)：\n").append(outputJson).append("\n");
        sb.append("已有断言(JSON，可参考或优化)：\n").append(existingAssertJson).append("\n");
        AutoAiJsonHelper.appendSkyeyeApiResponseRules(sb);
        sb.append("规则：\n");
        sb.append("1. key 为 JsonPath 相对路径，必须以步骤编码开头，如 ").append(resultKey).append(".returnCode、")
            .append(resultKey).append(".returnMessage、").append(resultKey).append(".rows[0].id\n");
        sb.append("2. operator 必须使用英文 key：equalTo、notEqual、lessThan、greaterThan、lessThanOrEqual、greaterThanOrEqual、contain（不要用 equal/==）\n");
        sb.append("3. valueFrom：1=自定义字面量；2=表达式(JsonPath 取期望值，一般优先用 1)\n");
        sb.append("4. value：自定义时填期望字面量（如 0、成功）；成功标志优先断言 returnCode=0，不要用 code=200\n");
        sb.append("5. 优先断言业务成功标志、状态码、关键 id/消息等，条数 3~8 条，不要重复\n");
        sb.append("6. 必须输出 {\"assertList\":[...]} 结构，不要只返回数组\n");
        AutoAiJsonHelper.appendMarkedJsonOutput(sb,
            "{\n"
                + "  \"assertList\": [\n"
                + "    {\"key\": \"" + resultKey + ".returnCode\", \"operator\": \"equalTo\", \"valueFrom\": 1, \"value\": \"0\"}\n"
                + "  ]\n"
                + "}");
        return sb.toString();
    }

    private Map<String, Object> parseAssertAnswer(String answer, String resultKey) {
        JSONArray assertArray = resolveAssertArray(answer);
        List<Map<String, Object>> assertList = new ArrayList<>();
        if (assertArray != null) {
            for (Object item : assertArray) {
                if (item == null) {
                    continue;
                }
                Map<String, Object> row = normalizeAssertRow(JSONUtil.parseObj(item), resultKey);
                if (row != null) {
                    assertList.add(row);
                }
                if (assertList.size() >= MAX_ASSERT_COUNT) {
                    break;
                }
            }
        }
        if (assertList.isEmpty()) {
            throw new CustomException("未能解析出有效断言，请重试（请在 "
                + AutoAiJsonHelper.JSON_BLOCK_BEGIN + " 与 "
                + AutoAiJsonHelper.JSON_BLOCK_END + " 之间输出 assertList JSON）");
        }
        Map<String, Object> bean = new HashMap<>();
        bean.put("assertList", assertList);
        return bean;
    }

    private JSONArray resolveAssertArray(String answer) {
        String jsonText = AutoAiJsonHelper.extractJsonBlock(answer);
        JSONObject json = AutoAiJsonHelper.parseJsonObject(jsonText);
        if (json != null) {
            JSONArray assertList = pickArray(json, "assertList", "asserts", "assertions", "list");
            if (assertList != null && !assertList.isEmpty()) {
                return assertList;
            }
            if (json.containsKey("key") && json.containsKey("operator")) {
                JSONArray single = new JSONArray();
                single.add(json);
                return single;
            }
        }
        return AutoAiJsonHelper.parseJsonArray(jsonText);
    }

    private JSONArray pickArray(JSONObject json, String... keys) {
        for (String key : keys) {
            try {
                JSONArray array = json.getJSONArray(key);
                if (array != null && !array.isEmpty()) {
                    return array;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private Map<String, Object> normalizeAssertRow(JSONObject item, String resultKey) {
        if (item == null) {
            return null;
        }
        String key = normalizeKey(firstNonBlank(item.getStr("key"), item.getStr("path"), item.getStr("field")), resultKey);
        if (StrUtil.isBlank(key)) {
            return null;
        }
        String operator = normalizeOperator(firstNonBlank(item.getStr("operator"), item.getStr("condition"), item.getStr("symbol")));
        Integer valueFrom = parseValueFrom(item.get("valueFrom"));
        Object valueObj = item.get("value");
        if (valueObj == null) {
            valueObj = item.get("expected");
        }
        if (valueObj == null) {
            valueObj = item.get("expect");
        }
        String value = valueObj == null ? "" : String.valueOf(valueObj);
        String[] normalized = AutoAiJsonHelper.normalizeSkyeyeAssertKeyValue(key, value);
        Map<String, Object> row = new HashMap<>();
        row.put("key", normalized[0]);
        row.put("operator", operator);
        row.put("valueFrom", valueFrom);
        row.put("value", normalized[1]);
        return row;
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (StrUtil.isNotBlank(value)) {
                return value.trim();
            }
        }
        return "";
    }

    private String normalizeOperator(String operator) {
        if (StrUtil.isBlank(operator)) {
            return AttrSymbols.EQUAL_TO.getKey();
        }
        String text = operator.trim();
        if (VALID_OPERATORS.contains(text)) {
            return text;
        }
        String alias = OPERATOR_ALIASES.get(text.toLowerCase(Locale.ROOT));
        if (alias != null) {
            return alias;
        }
        return AttrSymbols.EQUAL_TO.getKey();
    }

    private Integer parseValueFrom(Object value) {
        if (value == null) {
            return AutoValueFromTypeEnum.CUSTOMIZE.getKey();
        }
        try {
            int from = Integer.parseInt(value.toString());
            if (from == AutoValueFromTypeEnum.EXPRESSION.getKey()) {
                return AutoValueFromTypeEnum.EXPRESSION.getKey();
            }
        } catch (Exception ignored) {
        }
        return AutoValueFromTypeEnum.CUSTOMIZE.getKey();
    }

    private String normalizeKey(String key, String resultKey) {
        if (StrUtil.isBlank(key)) {
            return "";
        }
        key = key.trim();
        if (key.startsWith("$.")) {
            key = key.substring(2);
        }
        if (StrUtil.isNotBlank(resultKey)) {
            if (key.startsWith(resultKey + ".") || key.startsWith(resultKey + "[")) {
                return key;
            }
            if (key.equals(resultKey)) {
                return key;
            }
            if (!key.contains(".") && !key.contains("[")) {
                return resultKey + "." + key;
            }
            if (!key.startsWith(resultKey)) {
                return resultKey + "." + key;
            }
        }
        return key;
    }
}
