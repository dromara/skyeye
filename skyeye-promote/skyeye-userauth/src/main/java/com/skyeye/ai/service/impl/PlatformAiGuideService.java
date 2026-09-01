/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.service.impl;

import com.skyeye.ai.skill.PlatformAiSkillPromptBuilder;
import com.skyeye.ai.util.PlatformAiChatHelper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 平台办公 AI 编排。角色提示词负责人设；技能/套件由配置台维护并在此注入。
 */
@Service
public class PlatformAiGuideService {

    private static final String BIZ_TYPE_CHAT = "chat";

    @Autowired
    private PlatformAiChatHelper platformAiChatHelper;

    @Autowired
    private PlatformAiSkillPromptBuilder platformAiSkillPromptBuilder;

    public void generate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> bean = generate(inputObject.getParams());
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    public Map<String, Object> generate(Map<String, Object> params) {
        String question = params.get("question").toString().trim();
        String pageTitle = params.get("pageTitle").toString();
        String pagePath = params.get("pagePath").toString();
        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("saveChat", 1);
        extraParams.put("knowledgeQuery", question);
        extraParams.put("userMessage", question);
        return platformAiChatHelper.startStreamingChat(
            platformAiSkillPromptBuilder.build(question, pageTitle, pagePath),
            BIZ_TYPE_CHAT,
            extraParams);
    }
}
