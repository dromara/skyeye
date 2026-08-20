/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.demand.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.module.entity.AutoModule;
import com.skyeye.module.service.AutoModuleService;
import com.skyeye.project.entity.AutoProject;
import com.skyeye.project.service.AutoProjectService;
import com.skyeye.rest.ai.IAiChatRest;
import com.skyeye.rest.platform.IPlatformBaseSettingRest;
import com.skyeye.version.entity.AutoVersion;
import com.skyeye.version.service.AutoVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: AutoDemandAiDraftService
 * @Description: 需求草稿 AI 生成编排（不落库）
 * @author: skyeye云系列--卫志强
 * @date: 2026/8/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AutoDemandAiDraftService {

    @Autowired
    private IAiChatRest iAiChatRest;

    @Autowired
    private IPlatformBaseSettingRest iPlatformBaseSettingRest;

    @Autowired
    private AutoProjectService autoProjectService;

    @Autowired
    private AutoModuleService autoModuleService;

    @Autowired
    private AutoVersionService autoVersionService;

    public Map<String, Object> generate(Map<String, Object> params) {
        String name = params.get("name").toString().trim();
        String objectId = params.get("objectId").toString();
        String projectName = loadProjectName(objectId);
        String moduleName = loadModuleName(params.get("moduleId") == null ? "" : params.get("moduleId").toString());
        String versionName = loadVersionName(params.get("versionId") == null ? "" : params.get("versionId").toString());
        String content = params.get("content") == null ? "" : params.get("content").toString();
        String remark = params.get("remark") == null ? "" : params.get("remark").toString();
        String testJoin = formatTestJoin(params.get("testJoinAnalysis"));

        String roleId = loadPlatformAiRoleId();
        Map<String, Object> chatParams = new HashMap<>();
        chatParams.put("content", buildUserContent(name, projectName, moduleName, versionName, content, remark, testJoin));
        chatParams.put("bizType", "demandDraft");
        chatParams.put("roleId", roleId);
        Map<String, Object> chatBean = ExecuteFeignClient.get(() -> iAiChatRest.syncChatCompletion(chatParams)).getBean();
        if (chatBean == null || chatBean.get("id") == null) {
            throw new CustomException("启动AI生成失败");
        }
        Map<String, Object> bean = new HashMap<>();
        bean.put("chatId", chatBean.get("id").toString());
        bean.put("streaming", true);
        return bean;
    }

    public Map<String, Object> parseAnswer(Map<String, Object> params) {
        if (params.get("answer") == null) {
            throw new CustomException("生成结果不能为空");
        }
        String answer = params.get("answer").toString();
        if (StrUtil.isBlank(answer)) {
            throw new CustomException("生成结果不能为空");
        }
        return parseDraft(answer);
    }

    private String loadPlatformAiRoleId() {
        Map<String, Object> bean = ExecuteFeignClient.get(() -> iPlatformBaseSettingRest.queryPlatformAiRole()).getBean();
        String roleId = bean == null || bean.get("roleId") == null ? "" : bean.get("roleId").toString();
        if (StrUtil.isBlank(roleId)) {
            throw new CustomException("请先在平台信息设置中绑定AI角色");
        }
        return roleId;
    }

    private String loadProjectName(String objectId) {
        AutoProject project = autoProjectService.selectById(objectId);
        return project == null || project.getName() == null ? "" : project.getName().toString();
    }

    private String loadModuleName(String moduleId) {
        if (StrUtil.isBlank(moduleId)) {
            return "";
        }
        AutoModule module = autoModuleService.selectById(moduleId);
        return module == null || module.getName() == null ? "" : module.getName().toString();
    }

    private String loadVersionName(String versionId) {
        if (StrUtil.isBlank(versionId)) {
            return "";
        }
        AutoVersion version = autoVersionService.selectById(versionId);
        return version == null || version.getName() == null ? "" : version.getName().toString();
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
        sb.append("请根据以下信息生成需求草稿，只输出 JSON，不要 markdown 代码块。\n");
        sb.append("标题：").append(name).append("\n");
        sb.append("项目：").append(nvlText(projectName)).append("\n");
        sb.append("模块：").append(nvlText(moduleName)).append("\n");
        sb.append("版本：").append(nvlText(versionName)).append("\n");
        sb.append("测试参与需求分析：").append(testJoin).append("\n");
        sb.append("已有内容：").append(nvlText(content)).append("\n");
        sb.append("已有备注：").append(nvlText(remark)).append("\n");
        sb.append("请输出 JSON：\n");
        sb.append("{\n");
        sb.append("  \"contentHtml\": \"需求正文 HTML，包含背景、范围、前端/后端/测试拆分\",\n");
        sb.append("  \"remark\": \"简要备注\",\n");
        sb.append("  \"frontTasks\": [\"前端任务\"],\n");
        sb.append("  \"backTasks\": [\"后端任务\"],\n");
        sb.append("  \"testTasks\": [\"测试任务\"],\n");
        sb.append("  \"totalScore\": 8.00,\n");
        sb.append("  \"frontDays\": 2,\n");
        sb.append("  \"backDays\": 3,\n");
        sb.append("  \"testDays\": 1\n");
        sb.append("}\n");
        sb.append("要求：contentHtml 为可直接放入富文本的 HTML；totalScore 为数字；frontDays/backDays/testDays 为正整数，表示各端预计工期（天）。");
        return sb.toString();
    }

    private Map<String, Object> parseDraft(String answer) {
        String jsonText = extractJson(answer);
        JSONObject json = parseJson(jsonText);
        Map<String, Object> bean = new HashMap<>();
        if (json == null) {
            bean.put("content", wrapAsHtml(answer));
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
            contentHtml = wrapAsHtml(answer);
        }
        bean.put("content", contentHtml);
        bean.put("remark", json.getStr("remark") == null ? "" : json.getStr("remark").toString());
        bean.put("totalScore", formatScore(json.get("totalScore")));
        fillEstimate(bean, parseDays(json.get("frontDays")), parseDays(json.get("backDays")), parseDays(json.get("testDays")));
        return bean;
    }

    private JSONObject parseJson(String jsonText) {
        if (StrUtil.isBlank(jsonText)) {
            return null;
        }
        try {
            return JSONUtil.parseObj(jsonText);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractJson(String answer) {
        if (StrUtil.isBlank(answer)) {
            return "";
        }
        String text = answer.trim();
        if (text.startsWith("```")) {
            int firstNl = text.indexOf('\n');
            if (firstNl > 0) {
                text = text.substring(firstNl + 1);
            }
            int lastFence = text.lastIndexOf("```");
            if (lastFence >= 0) {
                text = text.substring(0, lastFence);
            }
            text = text.trim();
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
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
        sb.append("<h3>").append(escapeHtml(title)).append("</h3><ul>");
        for (Object item : tasks) {
            if (item == null) {
                continue;
            }
            sb.append("<li>").append(escapeHtml(item.toString())).append("</li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }

    private String wrapAsHtml(String raw) {
        if (StrUtil.isBlank(raw)) {
            return "";
        }
        String text = raw.trim();
        if (text.startsWith("<")) {
            return text;
        }
        String escaped = escapeHtml(text);
        return "<p>" + escaped.replace("\n", "</p><p>") + "</p>";
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
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

    private String nvlText(String value) {
        return StrUtil.isBlank(value) ? "无" : value;
    }
}
