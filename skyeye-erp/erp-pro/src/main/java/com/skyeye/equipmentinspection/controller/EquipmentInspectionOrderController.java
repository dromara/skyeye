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
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionAssignType;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionCheckResult;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentInspectionOrderController
 * @Description: 设备巡检单控制层
 */
@RestController
@Api(value = "设备巡检单", tags = "设备巡检单", modelName = "设备巡检单")
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

    @ApiOperation(id = "queryEquipmentInspectionOrderById", value = "根据ID查询设备巡检单详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionOrderController/queryEquipmentInspectionOrderById")
    public void queryEquipmentInspectionOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentInspectionOrderById", value = "根据ID删除设备巡检单", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionOrderController/deleteEquipmentInspectionOrderById")
    public void deleteEquipmentInspectionOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "editEquipmentInspectionWaitToWorkMation", value = "派工/指派", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "serviceUserId", name = "serviceUserId", value = "巡检员", required = "required"),
        @ApiImplicitParam(id = "cooperationUserId", name = "cooperationUserId", value = "协助巡检员", required = "json"),
        @ApiImplicitParam(id = "assignType", name = "assignType", value = "巡检员指派方式",
            enumClass = EquipmentInspectionAssignType.class)})
    @RequestMapping("/post/EquipmentInspectionOrderController/editEquipmentInspectionWaitToWorkMation")
    public void editEquipmentInspectionWaitToWorkMation(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.editEquipmentInspectionWaitToWorkMation(inputObject, outputObject);
    }

    @ApiOperation(id = "receivingEquipmentInspectionOrderById", value = "接单", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionOrderController/receivingEquipmentInspectionOrderById")
    public void receivingEquipmentInspectionOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.receivingEquipmentInspectionOrderById(inputObject, outputObject);
    }

    @ApiOperation(id = "registerEquipmentInspectionOnce", value = "登记本单巡检一次", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionOrderController/registerEquipmentInspectionOnce")
    public void registerEquipmentInspectionOnce(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.registerEquipmentInspectionOnce(inputObject, outputObject);
    }

    @ApiOperation(id = "submitEquipmentInspectionResult", value = "提交巡检结果", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "checkResult", name = "checkResult", value = "检查结果",
            enumClass = EquipmentInspectionCheckResult.class, required = "required,num"),
        @ApiImplicitParam(id = "inspectionTime", name = "inspectionTime", value = "巡检时间"),
        @ApiImplicitParam(id = "summary", name = "summary", value = "本次巡检总结"),
        @ApiImplicitParam(id = "photoUrls", name = "photoUrls", value = "拍照URL，逗号分隔"),
        @ApiImplicitParam(id = "locationText", name = "locationText", value = "定位文本"),
        @ApiImplicitParam(id = "longitude", name = "longitude", value = "经度"),
        @ApiImplicitParam(id = "latitude", name = "latitude", value = "纬度"),
        @ApiImplicitParam(id = "address", name = "address", value = "定位地址")})
    @RequestMapping("/post/EquipmentInspectionOrderController/submitEquipmentInspectionResult")
    public void submitEquipmentInspectionResult(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.submitEquipmentInspectionResult(inputObject, outputObject);
    }

    @ApiOperation(id = "auditEquipmentInspectionOrderById", value = "审核巡检单", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "pass", name = "pass", value = "是否通过 1通过 0驳回", required = "required")})
    @RequestMapping("/post/EquipmentInspectionOrderController/auditEquipmentInspectionOrderById")
    public void auditEquipmentInspectionOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.auditEquipmentInspectionOrderById(inputObject, outputObject);
    }

    @ApiOperation(id = "transferEquipmentInspectionToRepair", value = "巡检单转维修单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = EquipmentRepairOrder.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionOrderController/transferEquipmentInspectionToRepair")
    public void transferEquipmentInspectionToRepair(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.transferEquipmentInspectionToRepair(inputObject, outputObject);
    }

}
