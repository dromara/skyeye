/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.controller;

import com.skyeye.maintenance.entity.EquipmentMaintainOrder;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.maintenance.classenum.EquipmentMaintainResult;
import com.skyeye.maintenance.service.EquipmentMaintainOrderService;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 设备保养任务控制层
 */
@RestController
@Api(value = "设备保养任务", tags = "设备保养", modelName = "设备保养")
public class EquipmentMaintainOrderController {

    @Autowired
    private EquipmentMaintainOrderService equipmentMaintainOrderService;

    @ApiOperation(id = "queryEquipmentMaintainOrderList", value = "分页获取设备保养任务列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentMaintainOrderController/queryEquipmentMaintainOrderList")
    public void queryEquipmentMaintainOrderList(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainOrderService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentMaintainOrder", value = "新增/编辑设备保养任务", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = EquipmentMaintainOrder.class)
    @RequestMapping("/post/EquipmentMaintainOrderController/writeEquipmentMaintainOrder")
    public void writeEquipmentMaintainOrder(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainOrderService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentMaintainOrderById", value = "根据id查询设备保养任务详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentMaintainOrderController/queryEquipmentMaintainOrderById")
    public void queryEquipmentMaintainOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainOrderService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentMaintainOrderById", value = "根据id删除设备保养任务", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentMaintainOrderController/deleteEquipmentMaintainOrderById")
    public void deleteEquipmentMaintainOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainOrderService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "startEquipmentMaintainTask", value = "开始执行设备保养任务", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "任务ID", required = "required")})
    @RequestMapping("/post/EquipmentMaintainOrderController/startEquipmentMaintainTask")
    public void startEquipmentMaintainTask(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainOrderService.startTask(inputObject, outputObject);
    }

    @ApiOperation(id = "completeEquipmentMaintainTask", value = "完成设备保养任务", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "任务ID", required = "required"),
        @ApiImplicitParam(id = "maintainResult", name = "maintainResult", value = "保养结果", enumClass = EquipmentMaintainResult.class, required = "required,num"),
        @ApiImplicitParam(id = "isToRepair", name = "isToRepair", value = "是否转维修", enumClass = WhetherEnum.class, required = "required,num")})
    @RequestMapping("/post/EquipmentMaintainOrderController/completeEquipmentMaintainTask")
    public void completeEquipmentMaintainTask(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainOrderService.completeTask(inputObject, outputObject);
    }

    @ApiOperation(id = "cancelEquipmentMaintainTask", value = "取消设备保养任务", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "任务ID", required = "required")})
    @RequestMapping("/post/EquipmentMaintainOrderController/cancelEquipmentMaintainTask")
    public void cancelEquipmentMaintainTask(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainOrderService.cancelTask(inputObject, outputObject);
    }

    @ApiOperation(id = "reassignEquipmentMaintainTimeoutTask", value = "重新分配超时设备保养任务", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "任务ID", required = "required"),
        @ApiImplicitParam(id = "executorId", name = "executorId", value = "执行人ID（员工ID），可选"),
        @ApiImplicitParam(id = "plannedStartTime", name = "plannedStartTime", value = "计划开始执行时间，可选")})
    @RequestMapping("/post/EquipmentMaintainOrderController/reassignEquipmentMaintainTimeoutTask")
    public void reassignEquipmentMaintainTimeoutTask(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainOrderService.reassignTimeoutTask(inputObject, outputObject);
    }

    @ApiOperation(id = "insertMaintainOrderToRepair", value = "保养任务转维修单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = EquipmentRepairOrder.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/insertMaintainOrderToRepair")
    public void insertMaintainOrderToRepair(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainOrderService.insertMaintainOrderToRepair(inputObject, outputObject);
    }
}
