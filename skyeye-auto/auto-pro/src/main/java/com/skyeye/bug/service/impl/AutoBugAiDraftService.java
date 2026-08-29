/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bug.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.util.AutoAiChatHelper;
import com.skyeye.ai.util.AutoAiHtmlHelper;
import com.skyeye.ai.util.AutoAiJsonHelper;
import com.skyeye.ai.util.AutoAiProjectContextHelper;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bug 草稿 AI 生成编排（不落库）。
 */
@Service
public class AutoBugAiDraftService {

    private static final String[] BUG_SECTION_LABELS = {
        "bug描述", "复现步骤", "预期结果", "实际结果", "改进需求", "效果截图"
    };

    @Autowired
    private AutoAiChatHelper autoAiChatHelper;

    @Autowired
    private AutoAiProjectContextHelper autoAiProjectContextHelper;

    public Map<String, Object> generate(Map<String, Object> params) {
        String name = params.get("name").toString().trim();
        List<String> images = readImages(params.get("images").toString());
        if (StrUtil.isBlank(name) && images.isEmpty()) {
            throw new CustomException("请输入一句话描述，或先上传截图再生成");
        }
        Map<String, Object> extraParams = new HashMap<>();
        if (!images.isEmpty()) {
            extraParams.put("images", JSONUtil.toJsonStr(images));
        }
        return autoAiChatHelper.startStreamingChat(
            buildUserContent(params, name, images),
            "bugDraft",
            extraParams.isEmpty() ? null : extraParams);
    }

    public Map<String, Object> parseAnswer(Map<String, Object> params) {
        return parseDraft(autoAiChatHelper.requireAnswer(params));
    }

    /**
     * 自动化用例/步骤执行失败上下文 → Bug 草稿。
     */
    public Map<String, Object> generateFromCaseFailure(Map<String, Object> params) {
        if (params.get("objectId") == null || StrUtil.isBlank(params.get("objectId").toString())) {
            throw new CustomException("objectId不能为空");
        }
        return autoAiChatHelper.startStreamingChat(buildCaseFailureContent(params), "bugDraft");
    }

    private List<String> readImages(String raw) {
        if (StrUtil.isEmpty(raw)) {
            return new ArrayList<>();
        }
        return JSONUtil.toList(raw, null);
    }

    private String buildUserContent(Map<String, Object> params, String name, List<String> images) {
        String objectId = params.get("objectId").toString();
        String moduleId = params.get("moduleId") == null ? "" : params.get("moduleId").toString();
        String versionId = params.get("versionId") == null ? "" : params.get("versionId").toString();
        String content = params.get("content") == null ? "" : params.get("content").toString();
        String remark = params.get("remark") == null ? "" : params.get("remark").toString();
        StringBuilder sb = new StringBuilder();
        sb.append("你是软件测试工程师，只输出 JSON，不要 markdown。\n");
        sb.append("请按测试工程师写缺陷单的习惯生成 Bug 草稿：标题点明现象，描述包含复现步骤、预期结果、实际结果，分类客观准确，不要 markdown 代码块。\n");
        if (StrUtil.isNotBlank(name)) {
            sb.append("用户一句话描述：").append(name).append("\n");
        } else {
            sb.append("用户一句话描述：无，请主要根据截图识别问题。\n");
        }
        sb.append("项目：").append(AutoAiHtmlHelper.nvlText(autoAiProjectContextHelper.loadProjectName(objectId))).append("\n");
        if (StrUtil.isNotBlank(moduleId)) {
            sb.append("用户已选模块：").append(AutoAiHtmlHelper.nvlText(autoAiProjectContextHelper.loadModuleName(moduleId))).append("\n");
        }
        sb.append("版本：").append(AutoAiHtmlHelper.nvlText(autoAiProjectContextHelper.loadVersionName(versionId))).append("\n");
        sb.append("已有问题描述：").append(AutoAiHtmlHelper.nvlText(AutoAiHtmlHelper.plainText(content))).append("\n");
        sb.append("已有备注：").append(AutoAiHtmlHelper.nvlText(remark)).append("\n");
        if (!images.isEmpty()) {
            sb.append("用户上传了 ").append(images.size()).append(" 张截图，请结合截图里的界面、报错和文案分析问题。\n");
            sb.append("识别模块时：只看截图里实际出现的顶部导航、左侧菜单高亮、页面标题、弹窗标题，把看到的菜单或页面名称原样写入 moduleName。截图里没出现的名称一律不要填，禁止猜测。\n");
        }
        appendOptions(sb, "可选严重性", params.get("severityOptions"));
        appendOptions(sb, "可选必现类型", params.get("necessaryOptions"));
        appendOptions(sb, "可选终端", params.get("terminalOptions"));
        AutoAiJsonHelper.appendMarkedJsonOutput(sb,
            "{\n"
                + "  \"name\": \"简洁的 Bug 标题\",\n"
                + "  \"contentHtml\": \"问题描述 HTML\",\n"
                + "  \"remark\": \"简要备注\",\n"
                + "  \"severity\": \"从可选严重性中选一个原文\",\n"
                + "  \"necessaryToPresent\": \"必现 或 非必现\",\n"
                + "  \"terminalOccurrence\": \"从可选终端中选一个原文\",\n"
                + "  \"moduleName\": \"截图中看到的模块或菜单名称，看不清则留空\"\n"
                + "}");
        sb.append("contentHtml 必须严格按以下 HTML 结构输出（标题用 strong 加粗，不要纯文本标题）：\n");
        sb.append("<p><strong>bug描述：</strong>现象说明</p>");
        sb.append("<p><strong>复现步骤：</strong><br/>1. …<br/>2. …</p>");
        sb.append("<p><strong>预期结果：</strong>…</p>");
        sb.append("<p><strong>实际结果：</strong>…</p>");
        sb.append("<p><strong>改进需求：</strong>…</p>\n");
        sb.append("要求：contentHtml 为可直接放入富文本的 HTML；分类字段必须从可选值中选，不要自造。moduleName 只能来自截图可见文字，不要编造。");
        return sb.toString();
    }

    private String buildCaseFailureContent(Map<String, Object> params) {
        String objectId = params.get("objectId").toString();
        String moduleId = params.get("moduleId") == null ? "" : params.get("moduleId").toString();
        String caseName = params.get("caseName") == null ? "" : params.get("caseName").toString();
        String stepName = params.get("stepName") == null ? "" : params.get("stepName").toString();
        String resultKey = params.get("resultKey") == null ? "" : params.get("resultKey").toString();
        String failMessage = params.get("failMessage") == null ? "" : params.get("failMessage").toString();
        String inputParams = AutoAiJsonHelper.normalizeJsonText(params.get("inputParams"));
        String output = AutoAiJsonHelper.normalizeJsonText(params.get("output"));
        String assertList = params.get("assertList") == null
            ? "[]" : AutoAiJsonHelper.normalizeJsonText(params.get("assertList"));
        String apiDetail = params.get("apiDetail") == null
            ? "{}" : AutoAiJsonHelper.normalizeJsonText(params.get("apiDetail"));
        StringBuilder sb = new StringBuilder();
        sb.append("你是软件测试工程师。根据自动化用例执行失败信息，生成 Bug 草稿，只输出 JSON，不要 markdown 代码块。\n");
        sb.append("请写清：失败现象、复现步骤（结合用例与步骤名）、预期结果、实际结果（引用失败输出/断言）。\n");
        sb.append("项目：").append(AutoAiHtmlHelper.nvlText(autoAiProjectContextHelper.loadProjectName(objectId))).append("\n");
        if (StrUtil.isNotBlank(moduleId)) {
            sb.append("用例模块：").append(AutoAiHtmlHelper.nvlText(autoAiProjectContextHelper.loadModuleName(moduleId))).append("\n");
        }
        sb.append("用例名称：").append(StrUtil.blankToDefault(caseName, "无")).append("\n");
        sb.append("失败步骤：").append(StrUtil.blankToDefault(stepName, "无")).append("\n");
        sb.append("步骤编码(resultKey)：").append(StrUtil.blankToDefault(resultKey, "无")).append("\n");
        sb.append("失败摘要：").append(StrUtil.blankToDefault(failMessage, "无")).append("\n");
        sb.append("步骤入参(JSON)：\n").append(inputParams).append("\n");
        sb.append("步骤输出(JSON)：\n").append(output).append("\n");
        sb.append("断言结果(JSON)：\n").append(assertList).append("\n");
        sb.append("API 请求详情(JSON，含 url/method/inputValue/outputValue 等)：\n").append(apiDetail).append("\n");
        appendHistoryAnalysisSection(sb, params);
        appendOptions(sb, "可选严重性", params.get("severityOptions"));
        appendOptions(sb, "可选必现类型", params.get("necessaryOptions"));
        appendOptions(sb, "可选终端", params.get("terminalOptions"));
        AutoAiJsonHelper.appendSkyeyeApiResponseRules(sb);
        AutoAiJsonHelper.appendMarkedJsonOutput(sb,
            "{\n"
                + "  \"name\": \"[自动化] 步骤失败简述\",\n"
                + "  \"contentHtml\": \"问题描述 HTML\",\n"
                + "  \"remark\": \"来自用例执行失败\",\n"
                + "  \"severity\": \"从可选严重性中选一个原文\",\n"
                + "  \"necessaryToPresent\": \"必现\",\n"
                + "  \"terminalOccurrence\": \"从可选终端中选一个原文\",\n"
                + "  \"moduleName\": \"\"\n"
                + "}");
        sb.append("contentHtml 必须严格按以下 HTML 结构输出：\n");
        sb.append("<p><strong>bug描述：</strong>…</p>");
        sb.append("<p><strong>复现步骤：</strong><br/>1. 执行用例 …<br/>2. …</p>");
        sb.append("<p><strong>预期结果：</strong>…</p>");
        sb.append("<p><strong>实际结果：</strong>…（引用 returnCode/断言失败信息）</p>");
        sb.append("<p><strong>改进需求：</strong>…</p>\n");
        sb.append("标题建议以 [自动化] 开头；moduleName 留空除非能从上下文明确判断模块名。");
        return sb.toString();
    }

    private void appendOptions(StringBuilder sb, String title, Object value) {
        if (value == null || StrUtil.isBlank(value.toString())) {
            return;
        }
        sb.append(title).append("：").append(value.toString()).append("\n");
    }

    private void appendHistoryAnalysisSection(StringBuilder sb, Map<String, Object> params) {
        Object summary = params.get("historyAnalysisSummary");
        if (summary == null || StrUtil.isBlank(summary.toString())) {
            return;
        }
        sb.append("\n【本次执行 AI 分析结论（请优先参考）】\n");
        sb.append("分析摘要：").append(summary.toString().trim()).append("\n");
        Object rootCause = params.get("historyAnalysisRootCause");
        if (rootCause != null && StrUtil.isNotBlank(rootCause.toString())) {
            sb.append("根因：").append(rootCause.toString().trim()).append("\n");
        }
        appendJsonListSection(sb, "失败步骤", params.get("historyAnalysisFailedSteps"));
        appendJsonListSection(sb, "改进建议", params.get("historyAnalysisSuggestions"));
    }

    private void appendJsonListSection(StringBuilder sb, String title, Object raw) {
        if (raw == null || StrUtil.isBlank(raw.toString())) {
            return;
        }
        try {
            List<String> items = JSONUtil.toList(raw.toString(), null);
            if (items == null || items.isEmpty()) {
                return;
            }
            sb.append(title).append("：\n");
            for (int i = 0; i < items.size(); i++) {
                sb.append(i + 1).append(". ").append(items.get(i)).append("\n");
            }
        } catch (Exception ignored) {
            sb.append(title).append("：").append(raw.toString()).append("\n");
        }
    }

    private Map<String, Object> parseDraft(String answer) {
        JSONObject json = AutoAiJsonHelper.parseJsonObject(AutoAiJsonHelper.extractJsonBlock(answer));
        Map<String, Object> bean = new HashMap<>();
        if (json == null) {
            bean.put("name", "");
            bean.put("content", AutoAiHtmlHelper.ensureSectionBold(AutoAiHtmlHelper.wrapAsHtml(answer), BUG_SECTION_LABELS));
            bean.put("remark", "");
            return bean;
        }
        bean.put("name", json.getStr("name") == null ? "" : json.getStr("name").trim());
        String contentHtml = json.getStr("contentHtml");
        if (StrUtil.isBlank(contentHtml)) {
            contentHtml = AutoAiHtmlHelper.wrapAsHtml(answer);
        }
        bean.put("content", AutoAiHtmlHelper.ensureSectionBold(contentHtml, BUG_SECTION_LABELS));
        bean.put("remark", json.getStr("remark") == null ? "" : json.getStr("remark"));
        bean.put("severity", json.getStr("severity"));
        bean.put("necessaryToPresent", json.getStr("necessaryToPresent"));
        bean.put("terminalOccurrence", json.getStr("terminalOccurrence"));
        bean.put("moduleName", json.getStr("moduleName"));
        return bean;
    }
}
