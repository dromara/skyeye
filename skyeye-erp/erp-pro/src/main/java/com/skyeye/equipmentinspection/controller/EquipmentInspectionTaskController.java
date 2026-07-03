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
import com.skyeye.equipmentinspection.entity.EquipmentInspectionTask;
import com.skyeye.equipmentinspection.service.EquipmentInspectionTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentInspectionTaskController
 * @Description: 设备巡检任务控制层
 */
@RestController
@Api(value = "设备巡检任务", tags = "设备巡检任务", modelName = "设备巡检任务")
public class EquipmentInspectionTaskController {

    @Autowired
    private EquipmentInspectionTaskService equipmentInspectionTaskService;

    @ApiOperation(id = "queryEquipmentInspectionTaskList", value = "获取设备巡检任务列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentInspectionTaskController/queryEquipmentInspectionTaskList")
    public void queryEquipmentInspectionTaskList(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTaskService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentInspectionTask", value = "新增/编辑设备巡检任务", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = EquipmentInspectionTask.class)
    @RequestMapping("/post/EquipmentInspectionTaskController/writeEquipmentInspectionTask")
    public void writeEquipmentInspectionTask(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTaskService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentInspectionTaskById", value = "根据ID查询设备巡检任务详情", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionTaskController/queryEquipmentInspectionTaskById")
    public void queryEquipmentInspectionTaskById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTaskService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentInspectionTaskById", value = "根据ID删除设备巡检任务", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionTaskController/deleteEquipmentInspectionTaskById")
    public void deleteEquipmentInspectionTaskById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTaskService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "startEquipmentInspectionTask", value = "开始执行设备巡检任务", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "任务ID", required = "required")})
    @RequestMapping("/post/EquipmentInspectionTaskController/startEquipmentInspectionTask")
    public void startEquipmentInspectionTask(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTaskService.startTask(inputObject, outputObject);
    }

    @ApiOperation(id = "completeEquipmentInspectionTask", value = "完成设备巡检任务", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "任务ID", required = "required")})
    @RequestMapping("/post/EquipmentInspectionTaskController/completeEquipmentInspectionTask")
    public void completeEquipmentInspectionTask(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTaskService.completeTask(inputObject, outputObject);
    }

    @ApiOperation(id = "cancelEquipmentInspectionTask", value = "取消设备巡检任务", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "任务ID", required = "required")})
    @RequestMapping("/post/EquipmentInspectionTaskController/cancelEquipmentInspectionTask")
    public void cancelEquipmentInspectionTask(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTaskService.cancelTask(inputObject, outputObject);
    }

    @ApiOperation(id = "reassignEquipmentInspectionTimeoutTask", value = "重新分配超时设备巡检任务", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "任务ID", required = "required"),
        @ApiImplicitParam(id = "executorId", name = "executorId", value = "执行人ID（员工ID），可选"),
        @ApiImplicitParam(id = "plannedStartTime", name = "plannedStartTime", value = "计划开始执行时间，可选")})
    @RequestMapping("/post/EquipmentInspectionTaskController/reassignEquipmentInspectionTimeoutTask")
    public void reassignEquipmentInspectionTimeoutTask(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTaskService.reassignTimeoutTask(inputObject, outputObject);
    }
}