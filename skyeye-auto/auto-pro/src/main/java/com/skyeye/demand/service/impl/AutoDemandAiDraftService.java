/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.demand.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.skyeye.ai.util.AutoAiChatHelper;
import com.skyeye.ai.util.AutoAiHtmlHelper;
import com.skyeye.ai.util.AutoAiJsonHelper;
import com.skyeye.ai.util.AutoAiProjectContextHelper;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.NumberParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: AutoDemandAiDraftService
 * @Description: 需求草稿 AI 生成编排（不落库）
 */
@Service
public class AutoDemandAiDraftService {

    private static final String[] DEMAND_SECTION_LABELS = {
        "背景", "范围", "前端任务", "后端任务", "测试任务", "功能说明", "验收标准"
    };

    @Autowired
    private AutoAiChatHelper autoAiChatHelper;

    @Autowired
    private AutoAiProjectContextHelper autoAiProjectContextHelper;

    public Map<String, Object> generate(Map<String, Object> params) {
        String name = params.get("name").toString().trim();
        String objectId = params.get("objectId").toString();
        String projectName = autoAiProjectContextHelper.loadProjectName(objectId);
        String moduleName = autoAiProjectContextHelper.loadModuleName(
            params.get("moduleId") == null ? "" : params.get("moduleId").toString());
        String versionName = autoAiProjectContextHelper.loadVersionName(
            params.get("versionId") == null ? "" : params.get("versionId").toString());
        String content = params.get("content") == null ? "" : params.get("content").toString();
        String remark = params.get("remark") == null ? "" : params.get("remark").toString();
        String testJoin = formatTestJoin(params.get("testJoinAnalysis"));
        return autoAiChatHelper.startStreamingChat(
            buildUserContent(name, projectName, moduleName, versionName, content, remark, testJoin),
            "demandDraft");
    }

    public Map<String, Object> parseAnswer(Map<String, Object> params) {
        return parseDraft(autoAiChatHelper.requireAnswer(params));
    }

    private String formatTestJoin(Object value) {
        if (value == null) {
            return "否";
        }
        String str = value.toString();
        if (StrUtil.equals(str, String.valueOf(IsDefaultEnum.IS_DEFAULT.getKey()))) {
            return "是";
        }
        if (StrUtil.equals(str, String.valueOf(IsDefaultEnum.NOT_DEFAULT.getKey()))) {
            return "否";
        }
        return StrUtil.isBlank(str) ? "否" : str;
    }

    private String buildUserContent(String name, String projectName, String moduleName, String versionName,
                                    String content, String remark, String testJoin) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是软件研发需求分析师，只输出 JSON，不要 markdown。\n");
        sb.append("请按需求分析师写需求的习惯生成需求草稿：讲清背景、范围、前后端与测试任务拆分及工期，表述给研发可直接落地，不要 markdown 代码块。\n");
        sb.append("标题：").append(name).append("\n");
        sb.append("项目：").append(AutoAiHtmlHelper.nvlText(projectName)).append("\n");
        sb.append("模块：").append(AutoAiHtmlHelper.nvlText(moduleName)).append("\n");
        sb.append("版本：").append(AutoAiHtmlHelper.nvlText(versionName)).append("\n");
        sb.append("测试参与需求分析：").append(testJoin).append("\n");
        sb.append("已有内容：").append(AutoAiHtmlHelper.nvlText(content)).append("\n");
        sb.append("已有备注：").append(AutoAiHtmlHelper.nvlText(remark)).append("\n");
        AutoAiJsonHelper.appendMarkedJsonOutput(sb,
            "{\n"
                + "  \"contentHtml\": \"需求正文 HTML\",\n"
                + "  \"remark\": \"简要备注\",\n"
                + "  \"frontTasks\": [\"前端任务\"],\n"
                + "  \"backTasks\": [\"后端任务\"],\n"
                + "  \"testTasks\": [\"测试任务\"],\n"
                + "  \"totalScore\": 8.00,\n"
                + "  \"frontDays\": 2,\n"
                + "  \"backDays\": 3,\n"
                + "  \"testDays\": 1\n"
                + "}");
        sb.append("contentHtml 必须严格按以下 HTML 结构输出（小节标题用 strong 加粗，不要纯文本标题）：\n");
        sb.append("<p><strong>背景：</strong>…</p>");
        sb.append("<p><strong>范围：</strong>…</p>");
        sb.append("<p><strong>前端任务：</strong><br/>1. …</p>");
        sb.append("<p><strong>后端任务：</strong><br/>1. …</p>");
        sb.append("<p><strong>测试任务：</strong><br/>1. …</p>\n");
        sb.append("要求：contentHtml 为可直接放入富文本的 HTML；totalScore 为数字；frontDays/backDays/testDays 为正整数，表示各端预计工期（天）。");
        return sb.toString();
    }

    private Map<String, Object> parseDraft(String answer) {
        JSONObject json = AutoAiJsonHelper.parseJsonObject(AutoAiJsonHelper.extractJsonBlock(answer));
        Map<String, Object> bean = new HashMap<>();
        if (json == null) {
            bean.put("content", AutoAiHtmlHelper.ensureSectionBold(AutoAiHtmlHelper.wrapAsHtml(answer), DEMAND_SECTION_LABELS));
            bean.put("remark", "");
            bean.put("totalScore", "0.00");
            fillEstimate(bean, 1, 1, 1);
            return bean;
        }
        String contentHtml = json.getStr("contentHtml");
        String taskHtml = buildHtmlFromTasks(json);
        if (StrUtil.isBlank(contentHtml)) {
            contentHtml = taskHtml;
        } else if (StrUtil.isNotBlank(taskHtml) && !contentHtml.contains("前端任务") && !contentHtml.contains("后端任务")) {
            contentHtml = contentHtml + taskHtml;
        }
        if (StrUtil.isBlank(contentHtml)) {
            contentHtml = AutoAiHtmlHelper.wrapAsHtml(answer);
        }
        bean.put("content", AutoAiHtmlHelper.ensureSectionBold(contentHtml, DEMAND_SECTION_LABELS));
        bean.put("remark", json.getStr("remark") == null ? "" : json.getStr("remark").toString());
        bean.put("totalScore", formatScore(json.get("totalScore")));
        fillEstimate(bean, parseDays(json.get("frontDays")), parseDays(json.get("backDays")), parseDays(json.get("testDays")));
        return bean;
    }

    private String buildHtmlFromTasks(JSONObject json) {
        StringBuilder sb = new StringBuilder();
        sb.append(taskSection("前端任务", safeArray(json, "frontTasks")));
        sb.append(taskSection("后端任务", safeArray(json, "backTasks")));
        sb.append(taskSection("测试任务", safeArray(json, "testTasks")));
        return sb.toString();
    }

    private JSONArray safeArray(JSONObject json, String key) {
        try {
            return json.getJSONArray(key);
        } catch (Exception e) {
            return null;
        }
    }

    private String taskSection(String title, JSONArray tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<p><strong>").append(AutoAiHtmlHelper.escapeHtml(title)).append("：</strong></p><ul>");
        for (Object item : tasks) {
            if (item == null) {
                continue;
            }
            sb.append("<li>").append(AutoAiHtmlHelper.escapeHtml(item.toString())).append("</li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }

    private String formatScore(Object value) {
        if (value == null) {
            return "0.00";
        }
        String str = value.toString();
        if (StrUtil.isBlank(str) || "null".equalsIgnoreCase(str)) {
            return "0.00";
        }
        try {
            return CalculationUtil.add(str, "0", 2);
        } catch (Exception e) {
            return value.toString();
        }
    }

    private int parseDays(Object value) {
        int days = (int) Math.round(NumberParseUtil.parseDouble(value));
        if (days < 1) {
            return 1;
        }
        return Math.min(days, 30);
    }

    private void fillEstimate(Map<String, Object> bean, int frontDays, int backDays, int testDays) {
        String[] front = estimateRange(frontDays);
        String[] back = estimateRange(backDays);
        String[] test = estimateRange(testDays);
        bean.put("frontEstimateStartTime", front[0]);
        bean.put("frontEstimateEndTime", front[1]);
        bean.put("backEstimateStartTime", back[0]);
        bean.put("backEstimateEndTime", back[1]);
        bean.put("testEstimateStartTime", test[0]);
        bean.put("testEstimateEndTime", test[1]);
    }

    private String[] estimateRange(int days) {
        Date start = new Date();
        Date end = DateUtil.getAfDate(start, Math.max(days - 1, 0), "d");
        return new String[]{
            DateUtil.formatDate2Str(start, DateUtil.YYYY_MM_DD),
            DateUtil.formatDate2Str(end, DateUtil.YYYY_MM_DD)
        };
    }
}
