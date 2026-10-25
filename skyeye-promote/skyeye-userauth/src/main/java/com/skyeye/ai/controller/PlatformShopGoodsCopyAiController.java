/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.controller;

import com.skyeye.ai.service.PlatformShopGoodsCopyAiDraftService;
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
 * 商城商品文案 AI 草稿。
 */
@RestController
@Api(value = "商品文案AI", tags = "商品文案AI", modelName = "商品文案AI")
public class PlatformShopGoodsCopyAiController {

    @Autowired
    private PlatformShopGoodsCopyAiDraftService platformShopGoodsCopyAiDraftService;

    @ApiOperation(id = "aiGenerateShopGoodsCopyDraft", value = "生成商品文案草稿", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "question", name = "question", value = "用户要求/卖点", required = "required"),
        @ApiImplicitParam(id = "name", name = "name", value = "当前商品名称"),
        @ApiImplicitParam(id = "model", name = "model", value = "当前型号"),
        @ApiImplicitParam(id = "remark", name = "remark", value = "当前简介"),
        @ApiImplicitParam(id = "content", name = "content", value = "当前详情HTML"),
        @ApiImplicitParam(id = "categoryName", name = "categoryName", value = "商城分类名称")})
    @RequestMapping("/post/PlatformShopGoodsCopyAiController/aiGenerateShopGoodsCopyDraft")
    public void aiGenerateShopGoodsCopyDraft(InputObject inputObject, OutputObject outputObject) {
        platformShopGoodsCopyAiDraftService.generate(inputObject, outputObject);
    }

    @ApiOperation(id = "aiParseShopGoodsCopyDraft", value = "解析商品文案草稿", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "answer", name = "answer", value = "AI完整回答", required = "required")})
    @RequestMapping("/post/PlatformShopGoodsCopyAiController/aiParseShopGoodsCopyDraft")
    public void aiParseShopGoodsCopyDraft(InputObject inputObject, OutputObject outputObject) {
        platformShopGoodsCopyAiDraftService.parseAnswer(inputObject, outputObject);
    }
}
