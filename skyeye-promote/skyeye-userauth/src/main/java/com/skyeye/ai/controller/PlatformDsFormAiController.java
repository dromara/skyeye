/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.controller;

import com.skyeye.ai.service.PlatformDsFormAiDraftService;
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
 * 表单布局 AI 辅助控制层。
 */
@RestController
@Api(value = "表单布局AI辅助", tags = "表单布局AI辅助", modelName = "表单布局AI辅助")
public class PlatformDsFormAiController {

    @Autowired
    private PlatformDsFormAiDraftService platformDsFormAiDraftService;

    @ApiOperation(id = "aiGenerateDsFormAssist", value = "AI表单布局辅助生成", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "question", name = "question", value = "用户问题或指令", required = "required"),
        @ApiImplicitParam(id = "pageTitle", name = "pageTitle", value = "当前页面标题"),
        @ApiImplicitParam(id = "appId", name = "appId", value = "业务应用 appId"),
        @ApiImplicitParam(id = "serviceClassName", name = "serviceClassName", value = "业务对象 className 全路径"),
        @ApiImplicitParam(id = "skillId", name = "skillId", value = "用户点选的技能id（单个）"),
        @ApiImplicitParam(id = "suiteId", name = "suiteId", value = "用户点选的套件id（单个）"),
        @ApiImplicitParam(id = "formContext", name = "formContext", value = "表单布局上下文JSON", required = "required,json")})
    @RequestMapping("/post/PlatformDsFormAiController/aiGenerateDsFormAssist")
    public void aiGenerateDsFormAssist(InputObject inputObject, OutputObject outputObject) {
        platformDsFormAiDraftService.generate(inputObject, outputObject);
    }

    @ApiOperation(id = "aiParseDsFormAssist", value = "解析AI表单布局辅助结果", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "answer", name = "answer", value = "AI完整回答", required = "required"),
        @ApiImplicitParam(id = "formContext", name = "formContext", value = "表单布局上下文JSON", required = "json")})
    @RequestMapping("/post/PlatformDsFormAiController/aiParseDsFormAssist")
    public void aiParseDsFormAssist(InputObject inputObject, OutputObject outputObject) {
        platformDsFormAiDraftService.parseAnswer(inputObject, outputObject);
    }
}
