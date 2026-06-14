/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentInspectionPlanController
 * @Description: 设备巡检方案控制层
 */
@RestController
@Api(value = "设备巡检方案", tags = "设备巡检方案", modelName = "设备巡检方案")
public class EquipmentInspectionPlanController {

    @Autowired
    private EquipmentInspectionPlanService equipmentInspectionPlanService;

    @ApiOperation(id = "queryEquipmentInspectionPlanList", value = "分页获取设备巡检方案列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentInspectionPlanController/queryEquipmentInspectionPlanList")
    public void queryEquipmentInspectionPlanList(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionPlanService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentInspectionPlan", value = "新增/编辑设备巡检方案", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = EquipmentInspectionPlan.class)
    @RequestMapping("/post/EquipmentInspectionPlanController/writeEquipmentInspectionPlan")
    public void writeEquipmentInspectionPlan(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionPlanService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentInspectionPlanById", value = "根据ID删除设备巡检方案", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionPlanController/deleteEquipmentInspectionPlanById")
    public void deleteEquipmentInspectionPlanById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionPlanService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentInspectionPlanById", value = "根据ID获取设备巡检方案(含明细)", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionPlanController/queryEquipmentInspectionPlanById")
    public void queryEquipmentInspectionPlanById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionPlanService.selectById(inputObject, outputObject);
    }
}

