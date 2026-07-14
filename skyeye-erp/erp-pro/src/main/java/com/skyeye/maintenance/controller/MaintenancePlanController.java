/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.controller;

import com.skyeye.maintenance.entity.MaintenancePlan;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.maintenance.service.MaintenancePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 保养计划控制层
 */
@RestController
@Api(value = "保养计划", tags = "保养计划", modelName = "保养计划")
public class MaintenancePlanController {

    @Autowired
    private MaintenancePlanService maintenancePlanService;

    @ApiOperation(id = "queryMaintenancePlanList", value = "分页查询保养计划", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MaintenancePlanController/queryMaintenancePlanList")
    public void queryMaintenancePlanList(InputObject inputObject, OutputObject outputObject) {
        maintenancePlanService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeMaintenancePlan", value = "新增/编辑保养计划", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = MaintenancePlan.class)
    @RequestMapping("/post/MaintenancePlanController/writeMaintenancePlan")
    public void writeMaintenancePlan(InputObject inputObject, OutputObject outputObject) {
        maintenancePlanService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMaintenancePlanById", value = "根据id查询保养计划详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MaintenancePlanController/queryMaintenancePlanById")
    public void queryMaintenancePlanById(InputObject inputObject, OutputObject outputObject) {
        maintenancePlanService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteMaintenancePlanById", value = "删除保养计划", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MaintenancePlanController/deleteMaintenancePlanById")
    public void deleteMaintenancePlanById(InputObject inputObject, OutputObject outputObject) {
        maintenancePlanService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllMaintenancePlanList", value = "查询全部保养计划", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "enabled", name = "enabled", value = "状态", required = "required")})
    @RequestMapping("/post/MaintenancePlanController/queryAllMaintenancePlanList")
    public void queryAllMaintenancePlanList(InputObject inputObject, OutputObject outputObject) {
        maintenancePlanService.queryAllMaintenancePlanList(inputObject, outputObject);
    }

}
