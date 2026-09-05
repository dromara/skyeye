/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.service.PlatformReportPageAiDraftService;
import com.skyeye.ai.skill.PlatformAiSkillPromptBuilder;
import com.skyeye.ai.util.PlatformAiChatHelper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.AiJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 报表大屏 AI 辅助编排（不落库）：结合技能说明书 + 报表 content，调用大模型生成建议或草稿。
 * 与表单 AI 同一套路：generate 流式出 chatId，parse 把回答拆成画布操作。
 */
@Service
public class PlatformReportPageAiDraftServiceImpl implements PlatformReportPageAiDraftService {

    /** 流式对话业务类型，前端 WebSocket 按此订阅。 */
    private static final String BIZ_TYPE = "reportPageAssist";

    @Autowired
    private PlatformAiChatHelper platformAiChatHelper;

    @Autowired
    private PlatformAiSkillPromptBuilder platformAiSkillPromptBuilder;

    /**
     * 拼 prompt 并启动大模型。content 为空画布时走整屏生成，有组件时可追加或修改。
     */
    @Override
    public void generate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String question = params.get("question").toString();
        // 对象或 JSON 字符串都收成同一段文本，后续 parseObj 用
        String contentJson = AiJsonHelper.normalizeJsonText(params.get("content"));
        String pageTitle = params.get("pageTitle").toString();
        String appId = params.get("appId").toString();
        String serviceClassName = params.get("serviceClassName").toString();
        String skillId = params.get("skillId").toString();
        String suiteId = params.get("suiteId").toString();
        String content = platformAiSkillPromptBuilder.buildForReportPage(
            question, pageTitle, appId, serviceClassName, skillId, suiteId, contentJson);
        Map<String, Object> bean = platformAiChatHelper.startStreamingChat(content, BIZ_TYPE);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 流结束后解析。answer 为模型全文；content 用于限制 update/remove 只能改已有组件。
     */
    @Override
    public void parseAnswer(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String answer = params.get("answer").toString();
        String contentJson = AiJsonHelper.normalizeJsonText(params.get("content"));
        Map<String, Object> bean = parseAssistAnswer(answer, contentJson);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 从标记块抽出 JSON。拆不出结构时仍返回原文 reply，ops 为空。
     */
    private Map<String, Object> parseAssistAnswer(String answer, String contentJson) {
        JSONObject json = AiJsonHelper.parseJsonObject(AiJsonHelper.extractJsonBlock(answer));
        Map<String, Object> bean = new HashMap<>();
        Set<String> allowedIds = loadExistingIds(contentJson);
        if (json == null) {
            bean.put("reply", answer.trim());
            bean.put("canvas", new HashMap<>());
            bean.put("ops", new ArrayList<>());
            return bean;
        }
        bean.put("reply", json.getStr("reply"));
        bean.put("canvas", parseCanvas(json.get("canvas")));
        bean.put("ops", parseOps(json.get("ops"), allowedIds));
        return bean;
    }

    /** 画布尺寸/背景，仅透传模型给出的字段。 */
    private Map<String, Object> parseCanvas(Object raw) {
        Map<String, Object> canvas = new HashMap<>();
        if (!(raw instanceof JSONObject) && !(raw instanceof Map)) {
            return canvas;
        }
        JSONObject json = raw instanceof JSONObject ? (JSONObject) raw : JSONUtil.parseObj(raw);
        if (json.get("contentWidth") != null) {
            canvas.put("contentWidth", json.get("contentWidth"));
        }
        if (json.get("contentHeight") != null) {
            canvas.put("contentHeight", json.get("contentHeight"));
        }
        if (StrUtil.isNotBlank(json.getStr("bgImage"))) {
            canvas.put("bgImage", json.getStr("bgImage"));
        }
        if (StrUtil.isNotBlank(json.getStr("background"))) {
            canvas.put("background", json.getStr("background"));
        }
        return canvas;
    }

    /**
     * 解析画布操作。op 为 add / update / remove / clear。
     * 当前 content 已有组件时，update/remove 的 id 必须在这些列表里。
     */
    private List<Map<String, Object>> parseOps(Object raw, Set<String> allowedIds) {
        List<Map<String, Object>> ops = new ArrayList<>();
        if (raw == null) {
            return ops;
        }
        JSONArray array;
        try {
            if (raw instanceof JSONArray) {
                array = (JSONArray) raw;
            } else if (raw instanceof List) {
                array = JSONUtil.parseArray(JSONUtil.toJsonStr(raw));
            } else {
                array = JSONUtil.parseArray(raw.toString());
            }
        } catch (Exception e) {
            return ops;
        }
        if (array == null) {
            return ops;
        }
        for (Object item : array) {
            if (!(item instanceof JSONObject)) {
                continue;
            }
            JSONObject action = (JSONObject) item;
            String op = action.getStr("op");
            if (StrUtil.isBlank(op)) {
                continue;
            }
            String id = action.getStr("id");
            // 空画布 allowedIds 为空，不拦截，便于整屏 add
            if (("update".equals(op) || "remove".equals(op))
                && !allowedIds.isEmpty() && !allowedIds.contains(id)) {
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("op", op);
            map.put("type", action.getStr("type"));
            map.put("id", id);
            map.put("modelId", action.getStr("modelId"));
            map.put("x", action.get("x"));
            map.put("y", action.get("y"));
            map.put("width", action.get("width"));
            map.put("height", action.get("height"));
            map.put("props", parseProps(action.get("props")));
            ops.add(map);
        }
        return ops;
    }

    /** 组件示意属性（文案、标题等），不是设计器 attrMation。 */
    private Map<String, Object> parseProps(Object raw) {
        Map<String, Object> props = new HashMap<>();
        if (!(raw instanceof JSONObject) && !(raw instanceof Map)) {
            return props;
        }
        JSONObject json = raw instanceof JSONObject ? (JSONObject) raw : JSONUtil.parseObj(raw);
        for (String key : json.keySet()) {
            Object value = json.get(key);
            if (value != null) {
                props.put(key, value);
            }
        }
        return props;
    }

    /**
     * 从设计器 content 六类组件列表收集已有 id，供 update/remove 校验。
     */
    private Set<String> loadExistingIds(String contentJson) {
        Set<String> ids = new HashSet<>();
        if (StrUtil.isBlank(contentJson)) {
            return ids;
        }
        try {
            JSONObject ctx = JSONUtil.parseObj(contentJson);
            collectIds(ids, ctx.getJSONArray("wordMationList"));
            collectIds(ids, ctx.getJSONArray("modelList"));
            collectIds(ids, ctx.getJSONArray("imgMationList"));
            collectIds(ids, ctx.getJSONArray("domMationList"));
            collectIds(ids, ctx.getJSONArray("tableMationList"));
            collectIds(ids, ctx.getJSONArray("basicComponentList"));
        } catch (Exception ignored) {
            // 解析失败时不限制，避免阻断展示
        }
        return ids;
    }

    private void collectIds(Set<String> ids, JSONArray list) {
        if (list == null) {
            return;
        }
        for (Object item : list) {
            if (!(item instanceof JSONObject)) {
                continue;
            }
            JSONObject src = (JSONObject) item;
            if (StrUtil.isNotBlank(src.getStr("id"))) {
                ids.add(src.getStr("id"));
            }
        }
    }
}
