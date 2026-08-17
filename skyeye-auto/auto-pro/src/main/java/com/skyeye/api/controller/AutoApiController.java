/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.api.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.api.entity.AutoApi;
import com.skyeye.api.entity.AutoApiQueryDo;
import com.skyeye.api.service.AutoApiService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoApiController
 * @Description: 接口管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 8:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "接口管理", tags = "接口管理", modelName = "接口管理")
public class AutoApiController {

    @Autowired
    private AutoApiService autoApiService;

    @ApiOperation(id = "queryAutoApiList", value = "获取接口信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AutoApiQueryDo.class)
    @RequestMapping("/post/AutoApiController/queryAutoApiList")
    public void queryAutoApiList(InputObject inputObject, OutputObject outputObject) {
        autoApiService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAutoApi", value = "新增/编辑接口信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AutoApi.class)
    @RequestMapping("/post/AutoApiController/writeAutoApi")
    public void writeAutoApi(InputObject inputObject, OutputObject outputObject) {
        autoApiService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAutoApiById", value = "根据ID删除接口信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoApiController/deleteAutoApiById")
    public void deleteAutoApiById(InputObject inputObject, OutputObject outputObject) {
        autoApiService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoApiById", value = "根据id查询接口信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoApiController/queryAutoApiById")
    public void queryAutoApiById(InputObject inputObject, OutputObject outputObject) {
        autoApiService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "apiTest", value = "接口测试", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AutoApi.class)
    @RequestMapping("/post/AutoApiController/apiTest")
    public void apiTest(InputObject inputObject, OutputObject outputObject) {
        autoApiService.apiTest(inputObject, outputObject);
    }

    @ApiOperation(id = "apiTestById", value = "根据ID测试接口信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoApiController/apiTestById")
    public void apiTestById(InputObject inputObject, OutputObject outputObject) {
        autoApiService.apiTestById(inputObject, outputObject);
    }
}

