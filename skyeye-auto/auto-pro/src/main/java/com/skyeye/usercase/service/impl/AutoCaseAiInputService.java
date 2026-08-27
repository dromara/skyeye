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
import com.skyeye.api.entity.AutoApi;
import com.skyeye.api.service.AutoApiService;
import com.skyeye.exception.CustomException;
import com.skyeye.usercase.classenum.AutoValueFromTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 步骤入参 AI 建议（不落库）。
 */
@Service
public class AutoCaseAiInputService {

    private static final int MAX_INPUT_COUNT = 30;

    @Autowired
    private AutoAiChatHelper autoAiChatHelper;

    @Autowired
    private AutoApiService autoApiService;

    public Map<String, Object> generate(Map<String, Object> params) {
        String apiId = params.get("apiId") == null ? "" : params.get("apiId").toString().trim();
        if (StrUtil.isBlank(apiId)) {
            throw new CustomException("接口id不能为空");
        }
        AutoApi autoApi = autoApiService.selectById(apiId);
        if (autoApi == null || StrUtil.isBlank(autoApi.getId())) {
            throw new CustomException("接口不存在");
        }
        String stepName = params.get("stepName") == null ? "" : params.get("stepName").toString();
        String resultKey = params.get("resultKey") == null ? "" : params.get("resultKey").toString().trim();
        String hint = params.get("hint") == null ? "" : params.get("hint").toString().trim();
        String inputExample = AutoAiJsonHelper.normalizeJsonText(autoApi.getInputExample());
        String preStepJson = params.get("preStepList") == null
            ? "[]" : AutoAiJsonHelper.normalizeJsonText(params.get("preStepList"));
        String existingInputJson = params.get("existingInputList") == null
            ? "[]" : AutoAiJsonHelper.normalizeJsonText(params.get("existingInputList"));
        return autoAiChatHelper.startStreamingChat(
            buildUserContent(autoApi, stepName, resultKey, hint, inputExample, preStepJson, existingInputJson),
            "stepInputSuggest");
    }

    public Map<String, Object> parseAnswer(Map<String, Object> params) {
        return parseInputAnswer(autoAiChatHelper.requireAnswer(params));
    }

    private String buildUserContent(AutoApi autoApi, String stepName, String resultKey, String hint,
                                    String inputExample, String preStepJson, String existingInputJson) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是自动化测试入参配置助手。根据接口入参示例与前序步骤，生成 API 步骤 stepInputList。\n");
        sb.append("步骤名称：").append(StrUtil.blankToDefault(stepName, "无")).append("\n");
        sb.append("当前步骤编码(resultKey)：").append(StrUtil.blankToDefault(resultKey, "无")).append("\n");
        sb.append("接口名称：").append(StrUtil.blankToDefault(autoApi.getName(), "无")).append("\n");
        sb.append("请求方式：").append(StrUtil.blankToDefault(autoApi.getRequestWay(), "无")).append("\n");
        sb.append("接口地址：").append(StrUtil.blankToDefault(autoApi.getAddress(), "无")).append("\n");
        sb.append("接口入参示例(JSON)：\n").append(inputExample).append("\n");
        sb.append("前序步骤(可用于表达式引用，value 填 resultKey 开头的 JsonPath)：\n").append(preStepJson).append("\n");
        sb.append("已有入参(JSON，可参考或优化)：\n").append(existingInputJson).append("\n");
        if (StrUtil.isNotBlank(hint)) {
            sb.append("用户补充说明：").append(hint).append("\n");
        }
        sb.append("规则：\n");
        sb.append("1. key 必须是接口入参字段名，来自入参示例，不要编造不存在的键\n");
        sb.append("2. valueFrom：1=自定义字面量；2=表达式（value 填前序步骤 JsonPath，如 prevResultKey.data.id）\n");
        sb.append("3. 需要唯一名称时可加 randomCategory(date/datetime/code6/code8) 与 randomPosition(front/back)，一般不必加\n");
        sb.append("4. 若前序步骤已产出 id/token 等，优先用表达式引用，不要写死\n");
        sb.append("5. 必须输出 {\"inputList\":[...]} 结构，不要只返回数组\n");
        AutoAiJsonHelper.appendMarkedJsonOutput(sb,
            "{\n"
                + "  \"inputList\": [\n"
                + "    {\"key\": \"name\", \"valueFrom\": 1, \"value\": \"测试名称\"},\n"
                + "    {\"key\": \"parentId\", \"valueFrom\": 2, \"value\": \"prevStep.data.id\"}\n"
                + "  ]\n"
                + "}");
        return sb.toString();
    }

    private Map<String, Object> parseInputAnswer(String answer) {
        JSONArray inputArray = resolveInputArray(answer);
        List<Map<String, Object>> inputList = new ArrayList<>();
        if (inputArray != null) {
            for (Object item : inputArray) {
                if (item == null) {
                    continue;
                }
                Map<String, Object> row = normalizeInputRow(JSONUtil.parseObj(item));
                if (row != null) {
                    inputList.add(row);
                }
                if (inputList.size() >= MAX_INPUT_COUNT) {
                    break;
                }
            }
        }
        if (inputList.isEmpty()) {
            throw new CustomException("未能解析出有效入参，请重试（请在 "
                + AutoAiJsonHelper.JSON_BLOCK_BEGIN + " 与 "
                + AutoAiJsonHelper.JSON_BLOCK_END + " 之间输出 inputList JSON）");
        }
        Map<String, Object> bean = new HashMap<>();
        bean.put("inputList", inputList);
        return bean;
    }

    private JSONArray resolveInputArray(String answer) {
        String jsonText = AutoAiJsonHelper.extractJsonBlock(answer);
        JSONObject json = AutoAiJsonHelper.parseJsonObject(jsonText);
        if (json != null) {
            JSONArray inputList = pickArray(json, "inputList", "inputs", "stepInputList", "list");
            if (inputList != null && !inputList.isEmpty()) {
                return inputList;
            }
            if (json.containsKey("key")) {
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

    private Map<String, Object> normalizeInputRow(JSONObject item) {
        if (item == null) {
            return null;
        }
        String key = firstNonBlank(item.getStr("key"), item.getStr("name"), item.getStr("field"));
        if (StrUtil.isBlank(key)) {
            return null;
        }
        Map<String, Object> row = new HashMap<>();
        row.put("key", key.trim());
        row.put("valueFrom", parseValueFrom(item.get("valueFrom")));
        Object valueObj = item.get("value");
        if (valueObj == null) {
            valueObj = item.get("val");
        }
        row.put("value", valueObj == null ? "" : String.valueOf(valueObj));
        String randomCategory = item.getStr("randomCategory");
        if (StrUtil.isNotBlank(randomCategory)) {
            row.put("randomCategory", randomCategory.trim());
        }
        String randomPosition = item.getStr("randomPosition");
        if (StrUtil.isNotBlank(randomPosition)) {
            row.put("randomPosition", randomPosition.trim());
        }
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
}
