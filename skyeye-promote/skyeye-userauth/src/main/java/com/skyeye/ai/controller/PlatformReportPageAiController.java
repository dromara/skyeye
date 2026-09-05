/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.controller;

import com.skyeye.ai.service.PlatformReportPageAiDraftService;
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
 * 报表大屏 AI 辅助控制层。
 */
@RestController
@Api(value = "报表大屏AI辅助", tags = "报表大屏AI辅助", modelName = "报表大屏AI辅助")
public class PlatformReportPageAiController {

    @Autowired
    private PlatformReportPageAiDraftService platformReportPageAiDraftService;

    /**
     * 启动流式生成：把技能说明书 + 当前 content 拼进 prompt，调用大模型。
     * 只返回 chatId，不写 report_page；模型正文走 WebSocket。
     */
    @ApiOperation(id = "aiGenerateReportPageAssist", value = "AI报表大屏辅助生成", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "question", name = "question", value = "用户问题或指令", required = "required"),
        @ApiImplicitParam(id = "pageTitle", name = "pageTitle", value = "当前页面标题"),
        @ApiImplicitParam(id = "appId", name = "appId", value = "业务应用 appId"),
        @ApiImplicitParam(id = "serviceClassName", name = "serviceClassName", value = "业务对象 className 全路径"),
        @ApiImplicitParam(id = "skillId", name = "skillId", value = "用户点选的技能id（单个）"),
        @ApiImplicitParam(id = "suiteId", name = "suiteId", value = "用户点选的套件id（单个）"),
        @ApiImplicitParam(id = "content", name = "content", value = "页面报表json串（设计器当前画布，空画布也传）", required = "required,json")})
    @RequestMapping("/post/PlatformReportPageAiController/aiGenerateReportPageAssist")
    public void aiGenerateReportPageAssist(InputObject inputObject, OutputObject outputObject) {
        platformReportPageAiDraftService.generate(inputObject, outputObject);
    }

    /**
     * 流式回答收齐后调用：把模型原文拆成 reply / canvas / ops，供前端改画布。
     */
    @ApiOperation(id = "aiParseReportPageAssist", value = "解析AI报表大屏辅助结果", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "answer", name = "answer", value = "AI完整回答", required = "required"),
        @ApiImplicitParam(id = "content", name = "content", value = "页面报表json串（用于校验 update/remove 的 id）", required = "json")})
    @RequestMapping("/post/PlatformReportPageAiController/aiParseReportPageAssist")
    public void aiParseReportPageAssist(InputObject inputObject, OutputObject outputObject) {
        platformReportPageAiDraftService.parseAnswer(inputObject, outputObject);
    }
}
