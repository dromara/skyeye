/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.microservice.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.microservice.entity.AutoMicroservice;
import com.skyeye.microservice.service.AutoMicroserviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoMicroserviceController
 * @Description: 微服务管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 8:52
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "微服务管理", tags = "微服务管理", modelName = "微服务管理")
public class AutoMicroserviceController {

    @Autowired
    private AutoMicroserviceService autoMicroserviceService;

    @ApiOperation(id = "queryAutoMicroserviceList", value = "获取微服务信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AutoMicroserviceController/queryAutoMicroserviceList")
    public void queryAutoMicroserviceList(InputObject inputObject, OutputObject outputObject) {
        autoMicroserviceService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAutoMicroservice", value = "新增/编辑微服务信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AutoMicroservice.class)
    @RequestMapping("/post/AutoMicroserviceController/writeAutoMicroservice")
    public void writeAutoMicroservice(InputObject inputObject, OutputObject outputObject) {
        autoMicroserviceService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAutoMicroserviceById", value = "根据ID删除微服务信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoMicroserviceController/deleteAutoMicroserviceById")
    public void deleteAutoMicroserviceById(InputObject inputObject, OutputObject outputObject) {
        autoMicroserviceService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoMicroserviceById", value = "根据id获取微服务信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoMicroserviceController/queryAutoMicroserviceById")
    public void queryAutoMicroserviceById(InputObject inputObject, OutputObject outputObject) {
        autoMicroserviceService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoMicroserviceListByServerId", value = "根据服务器id获取微服务列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "serverId", name = "serverId", value = "服务器id", required = "required")})
    @RequestMapping("/post/AutoMicroserviceController/queryAutoMicroserviceListByServerId")
    public void queryAutoMicroserviceListByServerId(InputObject inputObject, OutputObject outputObject) {
        autoMicroserviceService.queryAutoMicroserviceListByServerId(inputObject, outputObject);
    }
}
