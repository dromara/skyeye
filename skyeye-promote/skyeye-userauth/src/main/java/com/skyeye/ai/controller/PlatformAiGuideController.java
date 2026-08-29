/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ai.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.ai.service.impl.PlatformAiGuideService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台办公 AI 控制层。
 */
@RestController
@Api(value = "平台办公AI", tags = "平台办公AI", modelName = "平台办公AI")
public class PlatformAiGuideController {

    @Autowired
    private PlatformAiGuideService platformAiGuideService;

    @ApiOperation(id = "aiGeneratePlatformGuide", value = "平台办公AI对话", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "question", name = "question", value = "用户问题", required = "required"),
        @ApiImplicitParam(id = "pageTitle", name = "pageTitle", value = "当前页面标题"),
        @ApiImplicitParam(id = "pagePath", name = "pagePath", value = "当前页面路径")})
    @RequestMapping("/post/PlatformAiGuideController/aiGeneratePlatformGuide")
    public void aiGeneratePlatformGuide(InputObject inputObject, OutputObject outputObject) {
        platformAiGuideService.generate(inputObject, outputObject);
    }
}
