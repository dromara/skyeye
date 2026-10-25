/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.controller;

import com.skyeye.ai.service.PlatformAiSkillPromptDraftService;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 技能提示词辅助生成。
 */
@RestController
@Api(value = "AI技能提示词", tags = "AI技能提示词", modelName = "AI技能提示词")
public class PlatformAiSkillPromptDraftController {

    @Autowired
    private PlatformAiSkillPromptDraftService platformAiSkillPromptDraftService;

    @ApiOperation(id = "aiGenerateAiSkillPromptDraft", value = "AI辅助生成技能提示词", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "name", name = "name", value = "技能名称", required = "required"),
        @ApiImplicitParam(id = "description", name = "description", value = "何时使用"),
        @ApiImplicitParam(id = "businessName", name = "businessName", value = "业务对象名称"),
        @ApiImplicitParam(id = "useSceneLabel", name = "useSceneLabel", value = "使用位置文案"),
        @ApiImplicitParam(id = "currentPrompt", name = "currentPrompt", value = "当前提示词草稿"),
        @ApiImplicitParam(id = "hint", name = "hint", value = "额外生成要求")})
    @RequestMapping("/post/PlatformAiSkillPromptDraftController/aiGenerateAiSkillPromptDraft")
    public void aiGenerateAiSkillPromptDraft(InputObject inputObject, OutputObject outputObject) {
        platformAiSkillPromptDraftService.generate(inputObject, outputObject);
    }
}
