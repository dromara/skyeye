/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.ai.service.PlatformAiSkillPromptDraftService;
import com.skyeye.ai.util.PlatformAiChatHelper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 根据技能名称、业务对象、使用说明等，生成可作 system 人设的技能提示词。
 */
@Service
public class PlatformAiSkillPromptDraftServiceImpl implements PlatformAiSkillPromptDraftService {

    public static final String BIZ_TYPE = "aiSkillPromptDraft";

    @Autowired
    private PlatformAiChatHelper platformAiChatHelper;

    @Override
    public void generate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String name = str(params, "name");
        if (StrUtil.isBlank(name)) {
            throw new CustomException("请先填写技能名称");
        }
        String content = buildPrompt(
            name,
            str(params, "description"),
            str(params, "businessName"),
            str(params, "useSceneLabel"),
            str(params, "currentPrompt"),
            str(params, "hint"));
        outputObject.setBean(platformAiChatHelper.startStreamingChat(content, BIZ_TYPE));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private String buildPrompt(String name, String description, String businessName,
                               String useSceneLabel, String currentPrompt, String hint) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是企业中台「AI 技能」配置助手，负责撰写技能的 system 人设提示词。\n");
        sb.append("目标：写出可直接粘贴到「技能提示词」字段的文案，用于约束大模型角色、硬规则与输出边界。\n");
        sb.append("说明：技能设计积木（说明书）会另拼进用户消息，这里不要写操作步骤长文，只写「你是谁 / 必须遵守 / 禁止事项 / 输出形态」。\n");
        sb.append("硬性要求：\n");
        sb.append("1. 只输出提示词正文，不要标题、不要 markdown 代码块、不要前后解释。\n");
        sb.append("2. 使用中文，语气专业克制，篇幅约 120～500 字，最多不超过 800 字。\n");
        sb.append("3. 明确业务对象与使用场景；强调仅给建议、不代用户提交/过账/审批/上架。\n");
        sb.append("4. 若已有草稿，在保留合理约束的前提下改写优化，不要原样照抄。\n");
        sb.append("技能名称：").append(trim(name, 100)).append("\n");
        if (StrUtil.isNotBlank(businessName)) {
            sb.append("业务对象：").append(trim(businessName, 100)).append("\n");
        }
        if (StrUtil.isNotBlank(useSceneLabel)) {
            sb.append("使用位置：").append(trim(useSceneLabel, 80)).append("\n");
        }
        if (StrUtil.isNotBlank(description)) {
            sb.append("何时使用：").append(trim(description, 500)).append("\n");
        }
        if (StrUtil.isNotBlank(hint)) {
            sb.append("额外要求：").append(trim(hint, 300)).append("\n");
        }
        if (StrUtil.isNotBlank(currentPrompt)) {
            sb.append("当前提示词草稿（请优化）：\n").append(trim(currentPrompt, 2000)).append("\n");
        }
        sb.append("请直接输出优化后的技能提示词正文。");
        return sb.toString();
    }

    private String trim(String text, int max) {
        if (StrUtil.isBlank(text)) {
            return "";
        }
        String t = text.trim();
        if (t.length() <= max) {
            return t;
        }
        return t.substring(0, max) + "…";
    }

    private String str(Map<String, Object> params, String key) {
        if (params == null || params.get(key) == null) {
            return "";
        }
        return String.valueOf(params.get(key)).trim();
    }
}
