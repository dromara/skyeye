/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.MainPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "首页统计相关", tags = "首页统计相关", modelName = "首页统计相关")
public class MainPageController {

    @Autowired
    private MainPageService mainPageService;

    @ApiOperation(id = "mainpage001", value = "获取本月考勤天数，我的文件数，我的论坛帖数，我的知识库文档数", method = "POST", allUse = "2")
    @RequestMapping("/post/MainPageController/queryFourNumListByUserId")
    public void queryFourNumListByUserId(InputObject inputObject, OutputObject outputObject) {
        mainPageService.queryFourNumListByUserId(inputObject, outputObject);
    }

}
