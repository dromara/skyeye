/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.controller;

import com.skyeye.ai.service.PlatformShopAfterSaleAiDraftService;
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
 * 商城售后 AI 摘要（只读）。
 */
@RestController
@Api(value = "售后AI摘要", tags = "售后AI摘要", modelName = "售后AI摘要")
public class PlatformShopAfterSaleAiController {

    @Autowired
    private PlatformShopAfterSaleAiDraftService platformShopAfterSaleAiDraftService;

    @ApiOperation(id = "aiGenerateShopAfterSaleSummary", value = "售后工单AI摘要与建议", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "question", name = "question", value = "用户问题或指令"),
        @ApiImplicitParam(id = "afterSaleContext", name = "afterSaleContext", value = "当前售后单JSON", required = "json"),
        @ApiImplicitParam(id = "historyContext", name = "historyContext", value = "本单售后历史JSON", required = "json")})
    @RequestMapping("/post/PlatformShopAfterSaleAiController/aiGenerateShopAfterSaleSummary")
    public void aiGenerateShopAfterSaleSummary(InputObject inputObject, OutputObject outputObject) {
        platformShopAfterSaleAiDraftService.generate(inputObject, outputObject);
    }

    @ApiOperation(id = "aiParseShopAfterSaleSummary", value = "解析售后AI摘要结果", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "answer", name = "answer", value = "AI完整回答", required = "required")})
    @RequestMapping("/post/PlatformShopAfterSaleAiController/aiParseShopAfterSaleSummary")
    public void aiParseShopAfterSaleSummary(InputObject inputObject, OutputObject outputObject) {
        platformShopAfterSaleAiDraftService.parseAnswer(inputObject, outputObject);
    }
}
