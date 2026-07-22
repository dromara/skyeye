/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.schedule.entity.AutoScheduleTask;
import com.skyeye.schedule.service.AutoScheduleTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 自动化定时任务控制层
 */
@RestController
@Api(value = "定时任务", tags = "定时任务", modelName = "定时任务")
public class AutoScheduleTaskController {

    @Autowired
    private AutoScheduleTaskService autoScheduleTaskService;

    @ApiOperation(id = "queryAutoScheduleTaskList", value = "分页查询定时任务", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AutoScheduleTaskController/queryAutoScheduleTaskList")
    public void queryAutoScheduleTaskList(InputObject inputObject, OutputObject outputObject) {
        autoScheduleTaskService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAutoScheduleTask", value = "新增/编辑定时任务", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AutoScheduleTask.class)
    @RequestMapping("/post/AutoScheduleTaskController/writeAutoScheduleTask")
    public void writeAutoScheduleTask(InputObject inputObject, OutputObject outputObject) {
        autoScheduleTaskService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoScheduleTaskById", value = "根据id查询定时任务详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoScheduleTaskController/queryAutoScheduleTaskById")
    public void queryAutoScheduleTaskById(InputObject inputObject, OutputObject outputObject) {
        autoScheduleTaskService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAutoScheduleTaskById", value = "删除定时任务", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoScheduleTaskController/deleteAutoScheduleTaskById")
    public void deleteAutoScheduleTaskById(InputObject inputObject, OutputObject outputObject) {
        autoScheduleTaskService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "executeAutoScheduleTaskById", value = "执行定时任务", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoScheduleTaskController/executeAutoScheduleTaskById")
    public void executeAutoScheduleTaskById(InputObject inputObject, OutputObject outputObject) {
        autoScheduleTaskService.executeScheduleTask(inputObject, outputObject);
    }

}
