/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentInspectionOrderController
 * @Description: 设备巡检单控制层
 */
@RestController
@Api(value = "设备巡检单", tags = "设备巡检单", modelName = "设备巡检")
public class EquipmentInspectionOrderController {

    @Autowired
    private EquipmentInspectionOrderService equipmentInspectionOrderService;

    @ApiOperation(id = "queryEquipmentInspectionOrderList", value = "获取设备巡检单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentInspectionOrderController/queryEquipmentInspectionOrderList")
    public void queryEquipmentInspectionOrderList(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentInspectionOrder", value = "新增/编辑设备巡检单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = EquipmentInspectionOrder.class)
    @RequestMapping("/post/EquipmentInspectionOrderController/writeEquipmentInspectionOrder")
    public void writeEquipmentInspectionOrder(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "submitEquipmentInspectionOrderToApproval", value = "设备巡检单提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/EquipmentInspectionOrderController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeEquipmentInspectionOrder", value = "撤销设备巡检单审批申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionOrderController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentInspectionOrderById", value = "根据ID删除设备巡检单", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionOrderController/deleteEquipmentInspectionOrderById")
    public void deleteEquipmentInspectionOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentInspectionOrderById", value = "根据ID获取设备巡检单(含明细)", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionOrderController/queryEquipmentInspectionOrderById")
    public void queryEquipmentInspectionOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.selectById(inputObject, outputObject);
    }

}