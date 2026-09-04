/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.history.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.skyeye.ai.util.AutoAiChatHelper;
import com.skyeye.ai.util.AutoAiJsonHelper;
import com.skyeye.common.util.AiJsonHelper;
import com.skyeye.exception.CustomException;
import com.skyeye.history.classenum.AutoHistoryCaseExecuteResult;
import com.skyeye.history.entity.AutoHistoryCase;
import com.skyeye.history.entity.AutoHistoryStep;
import com.skyeye.history.entity.AutoHistoryStepApi;
import com.skyeye.history.entity.AutoHistoryStepAssert;
import com.skyeye.history.service.AutoHistoryCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用例执行历史 AI 分析（不落库）。
 */
@Service
public class AutoCaseAiHistoryService {

    private static final int MAX_OUTPUT_CHARS = 800;
    private static final int MAX_STEPS = 30;

    @Autowired
    private AutoAiChatHelper autoAiChatHelper;

    @Autowired
    private AutoHistoryCaseService autoHistoryCaseService;

    public Map<String, Object> generate(Map<String, Object> params) {
        String historyId = params.get("historyId") == null ? "" : params.get("historyId").toString().trim();
        if (StrUtil.isBlank(historyId)) {
            throw new CustomException("历史记录id不能为空");
        }
        AutoHistoryCase historyCase = autoHistoryCaseService.getDataFromDb(historyId);
        if (historyCase == null || StrUtil.isBlank(historyCase.getId())) {
            throw new CustomException("执行历史不存在");
        }
        return autoAiChatHelper.startStreamingChat(
            buildUserContent(historyCase),
            "caseHistoryAnalysis");
    }

    public Map<String, Object> parseAnswer(Map<String, Object> params) {
        return parseAnalysisAnswer(autoAiChatHelper.requireAnswer(params));
    }

    private String buildUserContent(AutoHistoryCase historyCase) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是自动化测试分析助手。根据一次用例执行历史的步骤结果，输出结构化分析报告，只输出 JSON。\n");
        sb.append("用例名称：").append(StrUtil.blankToDefault(historyCase.getName(), "无")).append("\n");
        sb.append("执行结果码：").append(historyCase.getExecuteResult()).append("（2=成功，3=失败，1=执行中）\n");
        sb.append("开始时间：").append(StrUtil.blankToDefault(historyCase.getExecuteStartTime(), "无")).append("\n");
        sb.append("结束时间：").append(StrUtil.blankToDefault(historyCase.getExecuteEndTime(), "无")).append("\n");
        sb.append("耗时(ms)：").append(StrUtil.blankToDefault(historyCase.getExecuteTime(), "无")).append("\n");
        sb.append("步骤摘要(JSON 数组，按顺序)：\n");
        sb.append(buildStepSummaries(historyCase.getStepList())).append("\n");
        AutoAiJsonHelper.appendSkyeyeApiResponseRules(sb);
        sb.append("规则：\n");
        sb.append("1. 找出首个失败步骤及根因（断言失败/接口 returnCode/异常等）\n");
        sb.append("2. suggestions 给出 2~5 条可操作的修复或排查建议\n");
        sb.append("3. 必须输出 {\"summary\",\"rootCause\",\"failedSteps\",\"suggestions\"} 结构\n");
        AiJsonHelper.appendMarkedJsonOutput(sb,
            "{\n"
                + "  \"summary\": \"整体结论一句话\",\n"
                + "  \"rootCause\": \"根因说明\",\n"
                + "  \"failedSteps\": [\"步骤名(resultKey)\"],\n"
                + "  \"suggestions\": [\"建议1\", \"建议2\"]\n"
                + "}");
        return sb.toString();
    }

    private String buildStepSummaries(List<AutoHistoryStep> stepList) {
        JSONArray array = new JSONArray();
        if (CollectionUtil.isEmpty(stepList)) {
            return array.toString();
        }
        int count = 0;
        for (AutoHistoryStep step : stepList) {
            if (step == null) {
                continue;
            }
            JSONObject item = new JSONObject();
            item.set("orderBy", step.getOrderBy());
            item.set("name", step.getName());
            item.set("resultKey", step.getResultKey());
            item.set("type", step.getType());
            item.set("executeResult", step.getExecuteResult());
            item.set("success", AutoHistoryCaseExecuteResult.EXECUTION_SUCCESSFUL.getKey().equals(step.getExecuteResult()));
            item.set("failedAsserts", buildFailedAsserts(step.getAutoHistoryStepAssertList()));
            item.set("api", buildApiBrief(step.getAutoHistoryStepApi()));
            array.add(item);
            count++;
            if (count >= MAX_STEPS) {
                break;
            }
        }
        return array.toString();
    }

    private JSONArray buildFailedAsserts(List<AutoHistoryStepAssert> assertList) {
        JSONArray array = new JSONArray();
        if (CollectionUtil.isEmpty(assertList)) {
            return array;
        }
        for (AutoHistoryStepAssert row : assertList) {
            if (row == null) {
                continue;
            }
            if (!AutoHistoryCaseExecuteResult.EXECUTION_FAILED.getKey().equals(row.getExecuteResult())) {
                continue;
            }
            JSONObject item = new JSONObject();
            item.set("key", row.getKey());
            item.set("operator", row.getOperator());
            item.set("value", row.getValue());
            item.set("realValue", row.getRealValue());
            array.add(item);
        }
        return array;
    }

    private JSONObject buildApiBrief(AutoHistoryStepApi api) {
        JSONObject item = new JSONObject();
        if (api == null) {
            return item;
        }
        item.set("url", api.getUrl());
        item.set("method", api.getMethod());
        item.set("outputSnippet", truncate(api.getOutputValue()));
        return item;
    }

    private String truncate(String text) {
        if (StrUtil.isBlank(text)) {
            return "";
        }
        String trimmed = text.trim();
        if (trimmed.length() <= MAX_OUTPUT_CHARS) {
            return trimmed;
        }
        return trimmed.substring(0, MAX_OUTPUT_CHARS) + "...(truncated)";
    }

    private Map<String, Object> parseAnalysisAnswer(String answer) {
        JSONObject json = AiJsonHelper.parseJsonObject(AiJsonHelper.extractJsonBlock(answer));
        Map<String, Object> bean = new HashMap<>();
        if (json == null) {
            bean.put("summary", StrUtil.blankToDefault(answer, "").trim());
            bean.put("rootCause", "");
            bean.put("failedSteps", new ArrayList<>());
            bean.put("suggestions", new ArrayList<>());
            return bean;
        }
        bean.put("summary", json.getStr("summary") == null ? "" : json.getStr("summary").trim());
        bean.put("rootCause", json.getStr("rootCause") == null ? "" : json.getStr("rootCause").trim());
        bean.put("failedSteps", parseStringList(json.getJSONArray("failedSteps")));
        bean.put("suggestions", parseStringList(json.getJSONArray("suggestions")));
        if (StrUtil.isBlank((String) bean.get("summary"))) {
            throw new CustomException("未能解析出有效分析结论，请重试");
        }
        return bean;
    }

    private List<String> parseStringList(JSONArray array) {
        List<String> list = new ArrayList<>();
        if (array == null) {
            return list;
        }
        for (Object item : array) {
            if (item == null) {
                continue;
            }
            String text = String.valueOf(item).trim();
            if (StrUtil.isNotBlank(text)) {
                list.add(text);
            }
        }
        return list;
    }
}
