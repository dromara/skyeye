/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.history.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.history.service.AutoHistoryCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoHistoryCaseController
 * @Description: 用例执行历史控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/16 20:26
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "用例执行历史", tags = "用例执行历史", modelName = "用例执行历史")
public class AutoHistoryCaseController {

    @Autowired
    private AutoHistoryCaseService autoHistoryCaseService;

    @ApiOperation(id = "queryAutoCaseHistoryList", value = "获取用例执行历史列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AutoHistoryCaseController/queryAutoCaseHistoryList")
    public void queryAutoCaseHistoryList(InputObject inputObject, OutputObject outputObject) {
        autoHistoryCaseService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoCaseHistoryById", value = "根据id查询执行历史详情信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id。", required = "required")})
    @RequestMapping("/post/AutoHistoryCaseController/queryAutoCaseHistoryById")
    public void queryAutoCaseHistoryById(InputObject inputObject, OutputObject outputObject) {
        autoHistoryCaseService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "finishAutoCaseHistoryById", value = "根据id强制结束执行信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id。", required = "required")})
    @RequestMapping("/post/AutoHistoryCaseController/finishAutoCaseHistoryById")
    public void finishAutoCaseHistoryById(InputObject inputObject, OutputObject outputObject) {
        autoHistoryCaseService.finishAutoCaseHistoryById(inputObject, outputObject);
    }

}
