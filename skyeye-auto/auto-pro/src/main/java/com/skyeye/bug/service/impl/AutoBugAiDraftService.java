/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bug.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.client.ExecuteFeignClient;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bug 草稿 AI 生成编排（不落库）。
 * 支持一句话描述，或结合用户上传的截图生成标题、问题描述和分类。
 */
@Service
public class AutoBugAiDraftService {

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
        List<String> images = readImages(params.get("images").toString());
        if (StrUtil.isBlank(name) && images.isEmpty()) {
            throw new CustomException("请输入一句话描述，或先上传截图再生成");
        }
        String roleId = loadPlatformAiRoleId();
        Map<String, Object> chatParams = new HashMap<>();
        chatParams.put("content", buildUserContent(params, name, images));
        chatParams.put("bizType", "bugDraft");
        chatParams.put("roleId", roleId);
        chatParams.put("saveChat", 0);
        if (!images.isEmpty()) {
            chatParams.put("images", JSONUtil.toJsonStr(images));
        }
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
        sb.append("请根据以下信息生成 Bug 草稿，只输出 JSON，不要 markdown 代码块。\n");
        if (StrUtil.isNotBlank(name)) {
            sb.append("用户一句话描述：").append(name).append("\n");
        } else {
            sb.append("用户一句话描述：无，请主要根据截图识别问题。\n");
        }
        sb.append("项目：").append(nvlText(loadProjectName(objectId))).append("\n");
        if (StrUtil.isNotBlank(moduleId)) {
            sb.append("用户已选模块：").append(nvlText(loadModuleName(moduleId))).append("\n");
        }
        sb.append("版本：").append(nvlText(loadVersionName(versionId))).append("\n");
        sb.append("已有问题描述：").append(nvlText(plainText(content))).append("\n");
        sb.append("已有备注：").append(nvlText(remark)).append("\n");
        if (!images.isEmpty()) {
            sb.append("用户上传了 ").append(images.size()).append(" 张截图，请结合截图里的界面、报错和文案分析问题。\n");
            sb.append("识别模块时：只看截图里实际出现的顶部导航、左侧菜单高亮、页面标题、弹窗标题，把看到的菜单或页面名称原样写入 moduleName。截图里没出现的名称一律不要填，禁止猜测。\n");
        }
        appendOptions(sb, "可选严重性", params.get("severityOptions"));
        appendOptions(sb, "可选必现类型", params.get("necessaryOptions"));
        appendOptions(sb, "可选终端", params.get("terminalOptions"));
        sb.append("请输出 JSON：\n");
        sb.append("{\n");
        sb.append("  \"name\": \"简洁的 Bug 标题\",\n");
        sb.append("  \"contentHtml\": \"问题描述 HTML，必须包含：bug描述、复现步骤、预期结果、实际结果、改进需求\",\n");
        sb.append("  \"remark\": \"简要备注\",\n");
        sb.append("  \"severity\": \"从可选严重性中选一个原文\",\n");
        sb.append("  \"necessaryToPresent\": \"必现 或 非必现\",\n");
        sb.append("  \"terminalOccurrence\": \"从可选终端中选一个原文\",\n");
        sb.append("  \"moduleName\": \"截图中看到的模块或菜单名称，看不清则留空\"\n");
        sb.append("}\n");
        sb.append("要求：contentHtml 为可直接放入富文本的 HTML；分类字段必须从可选值中选，不要自造。moduleName 只能来自截图可见文字，不要编造。");
        return sb.toString();
    }

    private void appendOptions(StringBuilder sb, String title, Object value) {
        if (value == null || StrUtil.isBlank(value.toString())) {
            return;
        }
        sb.append(title).append("：").append(value.toString()).append("\n");
    }

    private Map<String, Object> parseDraft(String answer) {
        String jsonText = extractJson(answer);
        JSONObject json = parseJson(jsonText);
        Map<String, Object> bean = new HashMap<>();
        if (json == null) {
            bean.put("name", "");
            bean.put("content", wrapAsHtml(answer));
            bean.put("remark", "");
            return bean;
        }
        bean.put("name", json.getStr("name") == null ? "" : json.getStr("name").trim());
        String contentHtml = json.getStr("contentHtml");
        if (StrUtil.isBlank(contentHtml)) {
            contentHtml = wrapAsHtml(answer);
        }
        bean.put("content", contentHtml);
        bean.put("remark", json.getStr("remark") == null ? "" : json.getStr("remark"));
        bean.put("severity", json.getStr("severity"));
        bean.put("necessaryToPresent", json.getStr("necessaryToPresent"));
        bean.put("terminalOccurrence", json.getStr("terminalOccurrence"));
        bean.put("moduleName", json.getStr("moduleName"));
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

    private String plainText(String html) {
        if (StrUtil.isBlank(html)) {
            return "";
        }
        return html.replaceAll("<[^>]*>", " ").replace("&nbsp;", " ").trim();
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

    private String nvlText(String value) {
        return StrUtil.isBlank(value) ? "无" : value;
    }
}
