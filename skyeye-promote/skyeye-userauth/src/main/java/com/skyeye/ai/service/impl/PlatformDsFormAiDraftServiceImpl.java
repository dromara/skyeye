/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.service.PlatformDsFormAiDraftService;
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
 * 表单布局 AI 辅助编排（不落库）：结合 AI 技能说明书 + 表单字段上下文，调用大模型生成建议或草稿。
 */
@Service
public class PlatformDsFormAiDraftServiceImpl implements PlatformDsFormAiDraftService {

    private static final String BIZ_TYPE = "dsFormAssist";

    @Autowired
    private PlatformAiChatHelper platformAiChatHelper;

    @Autowired
    private PlatformAiSkillPromptBuilder platformAiSkillPromptBuilder;

    @Override
    public void generate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String question = params.get("question").toString();
        String formContext = AiJsonHelper.normalizeJsonText(params.get("formContext"));
        String pageTitle = params.get("pageTitle").toString();
        String appId = params.get("appId").toString();
        String serviceClassName = params.get("serviceClassName").toString();
        String skillId = params.get("skillId").toString();
        String suiteId = params.get("suiteId").toString();
        String content = platformAiSkillPromptBuilder.buildForDsForm(
            question, pageTitle, appId, serviceClassName, skillId, suiteId, formContext);
        Map<String, Object> bean = platformAiChatHelper.startStreamingChat(content, BIZ_TYPE);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void parseAnswer(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String answer = params.get("answer").toString();
        String formContext = params.get("formContext").toString();
        Map<String, Object> bean = parseAssistAnswer(answer, formContext);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private Map<String, Object> parseAssistAnswer(String answer, String formContext) {
        JSONObject json = AiJsonHelper.parseJsonObject(AiJsonHelper.extractJsonBlock(answer));
        Map<String, Object> bean = new HashMap<>();
        Set<String> allowedKeys = loadAllowedFieldKeys(formContext);
        if (json == null) {
            bean.put("reply", answer.trim());
            bean.put("fieldValues", new HashMap<>());
            bean.put("actions", new ArrayList<>());
            return bean;
        }
        bean.put("reply", json.getStr("reply"));
        bean.put("fieldValues", filterFieldValues(json.get("fieldValues"), allowedKeys));
        bean.put("actions", parseActions(json.get("actions")));
        return bean;
    }

    private Set<String> loadAllowedFieldKeys(String formContext) {
        Set<String> keys = new HashSet<>();
        if (StrUtil.isBlank(formContext)) {
            return keys;
        }
        try {
            JSONObject ctx = JSONUtil.parseObj(formContext);
            JSONArray fields = ctx.getJSONArray("fields");
            if (fields == null) {
                return keys;
            }
            for (Object item : fields) {
                if (!(item instanceof JSONObject)) {
                    continue;
                }
                String attrKey = ((JSONObject) item).getStr("attrKey");
                if (StrUtil.isNotBlank(attrKey)) {
                    keys.add(attrKey);
                }
            }
        } catch (Exception ignored) {
            // 解析失败时不限制，避免阻断展示
        }
        return keys;
    }

    private Map<String, Object> filterFieldValues(Object raw, Set<String> allowedKeys) {
        Map<String, Object> result = new HashMap<>();
        if (!(raw instanceof JSONObject) && !(raw instanceof Map)) {
            return result;
        }
        JSONObject json = raw instanceof JSONObject ? (JSONObject) raw : JSONUtil.parseObj(raw);
        for (String key : json.keySet()) {
            if (!allowedKeys.isEmpty() && !allowedKeys.contains(key)) {
                continue;
            }
            Object value = json.get(key);
            if (value != null) {
                result.put(key, value);
            }
        }
        return result;
    }

    private List<Map<String, Object>> parseActions(Object raw) {
        List<Map<String, Object>> actions = new ArrayList<>();
        if (raw == null) {
            return actions;
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
            return actions;
        }
        if (array == null) {
            return actions;
        }
        for (Object item : array) {
            if (!(item instanceof JSONObject)) {
                continue;
            }
            JSONObject action = (JSONObject) item;
            Map<String, Object> map = new HashMap<>();
            map.put("type", action.getStr("type"));
            map.put("label", action.getStr("label"));
            map.put("menuId", action.getStr("menuId"));
            map.put("pageId", action.getStr("pageId"));
            actions.add(map);
        }
        return actions;
    }
}
