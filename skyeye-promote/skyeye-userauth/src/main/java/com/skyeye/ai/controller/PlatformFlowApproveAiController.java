/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.controller;

import com.skyeye.ai.service.PlatformFlowApproveAiDraftService;
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
 * 审批详情 AI 辅助（只读）。
 */
@RestController
@Api(value = "审批AI辅助", tags = "审批AI辅助", modelName = "审批AI辅助")
public class PlatformFlowApproveAiController {

    @Autowired
    private PlatformFlowApproveAiDraftService platformFlowApproveAiDraftService;

    @ApiOperation(id = "aiGenerateFlowApproveAssist", value = "审批详情AI摘要与建议意见", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "question", name = "question", value = "用户问题或指令"),
        @ApiImplicitParam(id = "taskNodeName", name = "taskNodeName", value = "当前审批节点名称"),
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例ID"),
        @ApiImplicitParam(id = "taskId", name = "taskId", value = "任务ID"),
        @ApiImplicitParam(id = "businessContext", name = "businessContext", value = "业务单据摘要JSON", required = "json"),
        @ApiImplicitParam(id = "historyContext", name = "historyContext", value = "审批历史JSON", required = "json")})
    @RequestMapping("/post/PlatformFlowApproveAiController/aiGenerateFlowApproveAssist")
    public void aiGenerateFlowApproveAssist(InputObject inputObject, OutputObject outputObject) {
        platformFlowApproveAiDraftService.generate(inputObject, outputObject);
    }

    @ApiOperation(id = "aiParseFlowApproveAssist", value = "解析审批AI辅助结果", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "answer", name = "answer", value = "AI完整回答", required = "required")})
    @RequestMapping("/post/PlatformFlowApproveAiController/aiParseFlowApproveAssist")
    public void aiParseFlowApproveAssist(InputObject inputObject, OutputObject outputObject) {
        platformFlowApproveAiDraftService.parseAnswer(inputObject, outputObject);
    }
}
