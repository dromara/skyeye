/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.schedule.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.CheckWorkShiftType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.schedule.entity.ScheduleDay;
import com.skyeye.eve.rest.schedule.OtherModuleScheduleMation;
import com.skyeye.eve.schedule.service.ScheduleDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ScheduleDayController
 * @Description: 日程管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/24 17:42
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "日程管理", tags = "日程管理", modelName = "日程管理")
public class ScheduleDayController {

    @Autowired
    private ScheduleDayService scheduleDayService;

    @ApiOperation(id = "insertScheduleDay", value = "添加日程信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ScheduleDay.class)
    @RequestMapping("/post/ScheduleDayController/insertScheduleDay")
    public void insertScheduleDay(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "syseveschedule017", value = "新增节假日", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "name", name = "name", value = "节假日标题", required = "required"),
        @ApiImplicitParam(id = "startTime", name = "startTime", value = "开始时间", required = "required"),
        @ApiImplicitParam(id = "endTime", name = "endTime", value = "结束时间", required = "required")})
    @RequestMapping("/post/ScheduleDayController/addSchedule")
    public void addSchedule(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.addSchedule(inputObject, outputObject);
    }

    @ApiOperation(id = "syseveschedule002", value = "获取当前用户的日程信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "yearMonth", name = "yearMonth", value = "指定年月：YYYY-MM", required = "required"),
        @ApiImplicitParam(id = "checkWorkId", name = "checkWorkId", value = "班次id"),
        @ApiImplicitParam(id = "shiftType", name = "shiftType", value = "班次类型", enumClass = CheckWorkShiftType.class, defaultValue = "fixed")})
    @RequestMapping("/post/ScheduleDayController/queryScheduleDayByUserId")
    public void queryScheduleDayByUserId(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.queryList(inputObject, outputObject);
    }

    @ApiOperation(id = "syseveschedule003", value = "根据用户获取今日日程信息", method = "GET", allUse = "2")
    @RequestMapping("/post/ScheduleDayController/queryTodayScheduleDayByUserId")
    public void queryTodayScheduleDayByUserId(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.queryTodayScheduleDayByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryScheduleDayByPointHms", value = "获取当前用户指定日期的日程信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "pointHms", name = "pointHms", value = "指定日期", required = "required")})
    @RequestMapping("/post/ScheduleDayController/queryScheduleDayByPointHms")
    public void queryScheduleDayByPointHms(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.queryScheduleDayByPointHms(inputObject, outputObject);
    }

    @ApiOperation(id = "syseveschedule005", value = "修改日程日期信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "name", name = "name", value = "标题", required = "required"),
        @ApiImplicitParam(id = "startTime", name = "startTime", value = "开始时间", required = "required"),
        @ApiImplicitParam(id = "endTime", name = "endTime", value = "结束时间", required = "required")})
    @RequestMapping("/post/ScheduleDayController/editScheduleDayById")
    public void editScheduleDayById(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.editScheduleDayById(inputObject, outputObject);
    }

    @ApiOperation(id = "syseveschedule006", value = "获取日程详细信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ScheduleDayController/queryScheduleDayById")
    public void queryScheduleDayById(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "syseveschedule007", value = "删除日程信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ScheduleDayController/deleteScheduleDayById")
    public void deleteScheduleDayById(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "syseveschedule019", value = "获取我的日程", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ScheduleDayController/queryMyScheduleList")
    public void queryMyScheduleList(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "insertScheduleMationByOtherModule", value = "其他模块同步到日程", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = OtherModuleScheduleMation.class)
    @RequestMapping("/post/ScheduleDayController/insertScheduleByOtherModule")
    public void insertScheduleByOtherModule(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.insertScheduleByOtherModule(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteScheduleMationByObjectId", value = "根据ObjectId删除日程", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "日程关联ID", required = "required")})
    @RequestMapping("/post/ScheduleDayController/deleteScheduleMationByObjectId")
    public void deleteScheduleMationByObjectId(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.deleteScheduleMationByObjectId(inputObject, outputObject);
    }

    @ApiOperation(id = "judgeISHoliday", value = "判断指定日期是否是节假日", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "day", name = "day", value = "日期，格式为yyyy-mm-dd", required = "required")})
    @RequestMapping("/post/ScheduleDayController/judgeISHoliday")
    public void judgeISHoliday(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.judgeISHoliday(inputObject, outputObject);
    }

    @ApiOperation(id = "syseveschedule012", value = "删除本年度节假日日程", method = "POST", allUse = "1")
    @RequestMapping("/post/ScheduleDayController/deleteHolidayScheduleByThisYear")
    public void deleteHolidayScheduleByThisYear(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.deleteHolidayScheduleByThisYear(inputObject, outputObject);
    }

    @ApiOperation(id = "syseveschedule009", value = "下载节假日导入模板", method = "POST", allUse = "1")
    @RequestMapping("/post/ScheduleDayController/downloadScheduleTemplate")
    public void downloadScheduleTemplate(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.downloadScheduleTemplate(inputObject, outputObject);
    }

    @ApiOperation(id = "syseveschedule010", value = "导入节假日日程", method = "POST", allUse = "1")
    @RequestMapping("/post/ScheduleDayController/exploreScheduleTemplate")
    public void exploreScheduleTemplate(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.exploreScheduleTemplate(inputObject, outputObject);
    }

    @ApiOperation(id = "syseveschedule013", value = "添加节假日日程提醒", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "节假日ID", required = "required"),
        @ApiImplicitParam(id = "remindType", name = "remindType", value = "提醒时间所属类型", required = "required,num")})
    @RequestMapping("/post/ScheduleDayController/addHolidayScheduleRemind")
    public void addHolidayScheduleRemind(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.addHolidayScheduleRemind(inputObject, outputObject);
    }

    @ApiOperation(id = "syseveschedule014", value = "取消节假日日程提醒", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "节假日ID", required = "required")})
    @RequestMapping("/post/ScheduleDayController/deleteHolidayScheduleRemind")
    public void deleteHolidayScheduleRemind(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.deleteHolidayScheduleRemind(inputObject, outputObject);
    }

    @ApiOperation(id = "syseveschedule018", value = "获取所有节假日", method = "POST", allUse = "2")
    @RequestMapping("/post/ScheduleDayController/queryHolidayScheduleListBySys")
    public void queryHolidayScheduleListBySys(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.queryHolidayScheduleListBySys(inputObject, outputObject);
    }

    @ApiOperation(id = "myagency001", value = "获取我的代办列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ScheduleDayController/queryMyAgencyList")
    public void queryMyAgencyList(InputObject inputObject, OutputObject outputObject) {
        scheduleDayService.queryMyAgencyList(inputObject, outputObject);
    }

}
