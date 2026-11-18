/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.ai.dto.DsFormAiAction;
import com.skyeye.ai.dto.DsFormAiAssistParseResult;
import com.skyeye.ai.dto.DsFormAiClarification;
import com.skyeye.ai.dto.DsFormAiClarifyOption;
import com.skyeye.ai.service.PlatformDsFormAiDraftService;
import com.skyeye.ai.skill.PlatformAiSkillPromptBuilder;
import com.skyeye.ai.util.PlatformAiChatHelper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.AiJsonHelper;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 表单布局 AI 辅助编排（不落库）：结合 AI 技能说明书 + 表单字段上下文，调用大模型生成建议或草稿。
 */
@Service
public class PlatformDsFormAiDraftServiceImpl implements PlatformDsFormAiDraftService {

    /** 与前端 useDsFormAi 监听的 bizType 一致；布局/表单填写共用这一路流 */
    private static final String BIZ_TYPE = "dsFormAssist";

    @Autowired
    private PlatformAiChatHelper platformAiChatHelper;

    @Autowired
    private PlatformAiSkillPromptBuilder platformAiSkillPromptBuilder;

    @Override
    public void generate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String question = strParam(params, "question");
        if (StrUtil.isBlank(question)) {
            throw new CustomException("请输入问题");
        }
        String pageTitle = strParam(params, "pageTitle");
        String appId = strParam(params, "appId");
        String serviceClassName = strParam(params, "serviceClassName");
        String skillId = strParam(params, "skillId");
        String suiteId = strParam(params, "suiteId");
        String confirmedRaw = strParam(params, "confirmedAnswers");
        String confirmedAnswers = StrUtil.isBlank(confirmedRaw)
            ? null
            : AiJsonHelper.normalizeJsonText(confirmedRaw);
        String content = platformAiSkillPromptBuilder.buildForDsForm(
            question, pageTitle, appId, serviceClassName, skillId, suiteId,
            AiJsonHelper.normalizeJsonText(params.get("formContext")),
            strParam(params, "assistScene"),
            confirmedAnswers);
        Map<String, Object> extra = new HashMap<>();
        if (StrUtil.isNotBlank(skillId)) {
            extra.put("skillId", skillId);
        }
        if (platformAiSkillPromptBuilder.resolveEnableAgentForDsForm(
            question, pageTitle, appId, serviceClassName, skillId, suiteId)) {
            extra.put("enableAgent", 1);
        }
        outputObject.setBean(platformAiChatHelper.startStreamingChat(content, BIZ_TYPE, extra));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void parseAnswer(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        outputObject.setBean(parseAssistAnswer(
            String.valueOf(params.get("answer")),
            String.valueOf(params.get("formContext"))));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private DsFormAiAssistParseResult parseAssistAnswer(String answer, String formContext) {
        DsFormAiAssistParseResult bean = new DsFormAiAssistParseResult();
        JSONObject json = AiJsonHelper.parseJsonObject(AiJsonHelper.extractJsonBlock(answer));
        if (json == null) {
            bean.setReply(StrUtil.blankToDefault(answer, "").trim());
            return bean;
        }
        Set<String> allowedKeys = loadAllowedFieldKeys(formContext);
        bean.setReply(json.getStr("reply"));
        bean.setFieldValues(filterFieldValues(json.get("fieldValues"), allowedKeys));
        bean.setActions(toBeanListQuietly(json.get("actions"), DsFormAiAction.class));
        bean.setClarifications(parseClarifications(json.get("clarifications"), allowedKeys));
        return bean;
    }

    private List<DsFormAiClarification> parseClarifications(Object raw, Set<String> allowedKeys) {
        List<DsFormAiClarification> list = new ArrayList<>();
        for (DsFormAiClarification row : toBeanListQuietly(raw, DsFormAiClarification.class)) {
            row.normalize(list.size());
            if (!row.isChoice()) {
                continue;
            }
            if (!allowedKeys.isEmpty() && StrUtil.isNotBlank(row.getAttrKey())
                && !allowedKeys.contains(row.getAttrKey())) {
                continue;
            }
            List<DsFormAiClarifyOption> options = parseClarifyOptions(row.getOptions());
            if (CollectionUtil.isEmpty(options)) {
                continue;
            }
            row.setOptions(options);
            list.add(row);
        }
        return list;
    }

    private List<DsFormAiClarifyOption> parseClarifyOptions(Object raw) {
        List<DsFormAiClarifyOption> list = toBeanListQuietly(raw, DsFormAiClarifyOption.class);
        list.forEach(DsFormAiClarifyOption::normalize);
        list.removeIf(opt -> !opt.isValid());
        return list.size() > 20 ? new ArrayList<>(list.subList(0, 20)) : list;
    }

    /**
     * 模型输出的数组字段：兼容 JSONArray / Collection / JSON 数组字符串；其它类型当空。
     */
    private <T> List<T> toBeanListQuietly(Object raw, Class<T> type) {
        JSONArray array = toJsonArrayQuietly(raw);
        if (array.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<T> list = JSONUtil.toList(array, type);
            if (CollectionUtil.isEmpty(list)) {
                return new ArrayList<>();
            }
            list.removeIf(Objects::isNull);
            return list;
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    private JSONArray toJsonArrayQuietly(Object raw) {
        if (raw == null) {
            return new JSONArray();
        }
        if (raw instanceof JSONArray) {
            return (JSONArray) raw;
        }
        try {
            if (raw instanceof String) {
                String s = ((String) raw).trim();
                if (StrUtil.isBlank(s) || !s.startsWith("[")) {
                    return new JSONArray();
                }
                return JSONUtil.parseArray(s);
            }
            if (raw instanceof Collection) {
                return JSONUtil.parseArray(JSONUtil.toJsonStr(raw));
            }
        } catch (Exception ignored) {
            // 关 Agent 时常见非数组字符串，忽略
        }
        return new JSONArray();
    }

    private Set<String> loadAllowedFieldKeys(String formContext) {
        Set<String> keys = new HashSet<>();
        if (StrUtil.isBlank(formContext)) {
            return keys;
        }
        try {
            JSONArray fields = JSONUtil.parseObj(formContext).getJSONArray("fields");
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
            // 解析失败时不限制
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

    private String strParam(Map<String, Object> params, String key) {
        if (params == null || params.get(key) == null) {
            return "";
        }
        return String.valueOf(params.get(key));
    }
}
