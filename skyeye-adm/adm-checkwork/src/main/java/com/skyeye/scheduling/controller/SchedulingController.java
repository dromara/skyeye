package com.skyeye.scheduling.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.scheduling.entity.Scheduling;
import com.skyeye.scheduling.entity.SchedulingAuto;
import com.skyeye.scheduling.service.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "排班管理", tags = "排班管理", modelName = "排班管理")
public class SchedulingController {

    @Autowired
    private SchedulingService schedulingService;

    @ApiOperation(id = "writeManualScheduling", value = "新增/编辑排班", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Scheduling.class)
    @RequestMapping("/post/SchedulingController/writeManualScheduling")
    public void writeManualScheduling(InputObject inputObject, OutputObject outputObject) {
        schedulingService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulingById", value = "根据Id查询排班记录", method = "POST", allUse = "2")
    @ApiImplicitParams(
        @ApiImplicitParam(id = "id", name = "id", value = "排班id", required = "required"))
    @RequestMapping("/post/SchedulingController/querySchedulingById")
    public void querySchedulingById(InputObject inputObject, OutputObject outputObject) {
        schedulingService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "autoComputeScheduling", value = "智能计算排班", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SchedulingAuto.class)
    @RequestMapping("/post/SchedulingController/autoComputeScheduling")
    public void autoComputeScheduling(InputObject inputObject, OutputObject outputObject) {
        schedulingService.autoComputeScheduling(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulingByStaffId", value = "查询当前账户的排班记录", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SchedulingController/querySchedulingByStaffId")
    public void querySchedulingByStaffId(InputObject inputObject, OutputObject outputObject) {
        schedulingService.querySchedulingByStaffId(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSchedulingByIds", value = "删除排班人员", method = "DELETE", allUse = "2")
    @ApiImplicitParams(
        @ApiImplicitParam(id = "ids", name = "ids", value = "排班人员ids", required = "required"))
    @RequestMapping("/post/SchedulingController/deleteSchedulingByIds")
    public void deleteSchedulingByIds(InputObject inputObject, OutputObject outputObject) {
        schedulingService.deleteSchedulingByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulingList", value = "查询所有排班记录", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SchedulingController/querySchedulingList")
    public void querySchedulingList(InputObject inputObject, OutputObject outputObject) {
        schedulingService.querySchedulingList(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulingByStaffIdAndMouths", value = "根据用户Id和月份查询用户的排班记录", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "staffId", name = "staffId", value = "用户Id", required = "required"),
        @ApiImplicitParam(id = "mouths", name = "mouths", value = "月份", required = "required")})
    @RequestMapping("/post/SchedulingController/querySchedulingByStaffIdAndMouths")
    public void querySchedulingByStaffIdAndMouths(InputObject inputObject, OutputObject outputObject) {
        schedulingService.querySchedulingByStaffIdAndMouths(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulingByStaffIdAndOneDay", value = "根据用户Id和日期/月份查询用户的排班班次", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "staffId", name = "staffId", value = "用户Id", required = "required"),
        @ApiImplicitParam(id = "day", name = "day", value = "指定日期 yyyy-MM-dd，考勤打卡传当天；与 monthMation 二选一，优先 day"),
        @ApiImplicitParam(id = "monthMation", name = "monthMation", value = "指定月份 yyyy-MM，考勤月历切换月份时使用")})
    @RequestMapping("/post/SchedulingController/querySchedulingByStaffIdAndOneDay")
    public void querySchedulingByStaffIdAndOneDay(InputObject inputObject, OutputObject outputObject) {
        schedulingService.querySchedulingByStaffIdAndOneDay(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulingByStaffIdAndDays", value = "根据用户Id和多个天份查询用户的排班记录", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "staffId", name = "staffId", value = "用户Id", required = "required"),
        @ApiImplicitParam(id = "days", name = "days", value = "具体某几天(年月日,逗号隔开)", required = "required")})
    @RequestMapping("/post/SchedulingController/querySchedulingByStaffIdAndDays")
    public void querySchedulingByStaffIdAndDays(InputObject inputObject, OutputObject outputObject) {
        schedulingService.querySchedulingByStaffIdAndDays(inputObject, outputObject);
    }

    @ApiOperation(id = "publishSchedulingById", value = "发布排班", method = "POST", allUse = "1")
    @ApiImplicitParams(
        @ApiImplicitParam(id = "id", name = "id", value = "排班id", required = "required"))
    @RequestMapping("/post/SchedulingController/publishSchedulingById")
    public void publishSchedulingById(InputObject inputObject, OutputObject outputObject) {
        schedulingService.publishSchedulingById(inputObject, outputObject);
    }
}
