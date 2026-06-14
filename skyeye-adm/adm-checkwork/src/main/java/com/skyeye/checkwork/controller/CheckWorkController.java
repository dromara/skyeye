/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.checkwork.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.checkwork.classenum.ClockSource;
import com.skyeye.checkwork.service.CheckWorkService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.CheckWorkShiftType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.centerrest.entity.checkwork.DayWork;
import com.skyeye.eve.centerrest.entity.checkwork.UserOtherDayMation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CheckWorkController
 * @Description: 考勤打卡管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/6 15:00
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "考勤打卡管理", tags = "考勤打卡管理", modelName = "考勤打卡管理")
public class CheckWorkController {

    @Autowired
    private CheckWorkService checkWorkService;

    @ApiOperation(id = "checkwork001", value = "上班打卡", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "timeId", name = "timeId", value = "班次id", required = "required"),
        @ApiImplicitParam(id = "longitude", name = "longitude", value = "经度"),
        @ApiImplicitParam(id = "latitude", name = "latitude", value = "纬度"),
        @ApiImplicitParam(id = "address", name = "address", value = "打卡的地址"),
        @ApiImplicitParam(id = "shiftType", name = "shiftType", value = "班次类型", enumClass = CheckWorkShiftType.class, defaultValue = "fixed"),
        @ApiImplicitParam(id = "clockSource", name = "clockSource", value = "打卡来源", required = "required", enumClass = ClockSource.class)})
    @RequestMapping("/post/CheckWorkController/insertCheckWorkStartWork")
    public void insertCheckWorkStartWork(InputObject inputObject, OutputObject outputObject) {
        checkWorkService.insertCheckWorkStartWork(inputObject, outputObject);
    }

    @ApiOperation(id = "checkwork002", value = "下班打卡", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "timeId", name = "timeId", value = "班次id", required = "required"),
        @ApiImplicitParam(id = "longitude", name = "longitude", value = "经度"),
        @ApiImplicitParam(id = "latitude", name = "latitude", value = "纬度"),
        @ApiImplicitParam(id = "address", name = "address", value = "打卡的地址"),
        @ApiImplicitParam(id = "shiftType", name = "shiftType", value = "班次类型", enumClass = CheckWorkShiftType.class, defaultValue = "fixed"),
        @ApiImplicitParam(id = "clockSource", name = "clockSource", value = "打卡来源", required = "required", enumClass = ClockSource.class)})
    @RequestMapping("/post/CheckWorkController/editCheckWorkEndWork")
    public void editCheckWorkEndWork(InputObject inputObject, OutputObject outputObject) {
        checkWorkService.editCheckWorkEndWork(inputObject, outputObject);
    }

    @ApiOperation(id = "checkwork003", value = "查看我的考勤列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CheckWorkController/queryCheckWorkList")
    public void queryCheckWorkList(InputObject inputObject, OutputObject outputObject) {
        checkWorkService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "checkwork004", value = "当前登录用户可以进行申诉的打卡信息列表", method = "GET", allUse = "2")
    @RequestMapping("/post/CheckWorkController/queryCheckWorkIdByAppealType")
    public void queryCheckWorkIdByAppealType(InputObject inputObject, OutputObject outputObject) {
        checkWorkService.queryCheckWorkIdByAppealType(inputObject, outputObject);
    }

    @ApiOperation(id = "checkwork013", value = "判断显示打上班卡或者下班卡", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "timeId", name = "timeId", value = "班次id", required = "required"),
        @ApiImplicitParam(id = "shiftType", name = "shiftType", value = "班次类型", enumClass = CheckWorkShiftType.class, defaultValue = "fixed")})
    @RequestMapping("/post/CheckWorkController/queryCheckWorkTimeToShowButton")
    public void queryCheckWorkTimeToShowButton(InputObject inputObject, OutputObject outputObject) {
        checkWorkService.queryCheckWorkTimeToShowButton(inputObject, outputObject);
    }

    @ApiOperation(id = "checkwork014", value = "根据月份查询当月的考勤信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "timeId", name = "timeId", value = "班次id"),
        @ApiImplicitParam(id = "monthMation", name = "monthMation", value = "当前月上个年月", required = "required"),
        @ApiImplicitParam(id = "shiftType", name = "shiftType", value = "班次类型", enumClass = CheckWorkShiftType.class, defaultValue = "fixed")})
    @RequestMapping("/post/CheckWorkController/queryCheckWorkMationByMonth")
    public void queryCheckWorkMationByMonth(InputObject inputObject, OutputObject outputObject) {
        checkWorkService.queryCheckWorkMationByMonth(inputObject, outputObject);
    }

    @ApiOperation(id = "checkwork015", value = "获取考勤报表数据", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "limit", name = "limit", value = "分页参数,每页多少条数据", required = "required,num"),
        @ApiImplicitParam(id = "page", name = "page", value = "分页参数,第几页", required = "required,num"),
        @ApiImplicitParam(id = "userName", name = "userName", value = "员工姓名"),
        @ApiImplicitParam(id = "startTime", name = "startTime", value = "起始时间", required = "required"),
        @ApiImplicitParam(id = "endTime", name = "endTime", value = "最后时间", required = "required"),
        @ApiImplicitParam(id = "timeId", name = "timeId", value = "班次id")})
    @RequestMapping("/post/CheckWorkController/queryCheckWorkReport")
    public void queryCheckWorkReport(InputObject inputObject, OutputObject outputObject) {
        checkWorkService.queryCheckWorkReport(inputObject, outputObject);
    }

    @ApiOperation(id = "checkwork016", value = "获取考勤报表数据", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "userName", name = "userName", value = "员工姓名"),
        @ApiImplicitParam(id = "arr", name = "arr", value = "时间数组", required = "required"),
        @ApiImplicitParam(id = "timeId", name = "timeId", value = "班次id")})
    @RequestMapping("/post/CheckWorkController/queryCheckWorkEcharts")
    public void queryCheckWorkEcharts(InputObject inputObject, OutputObject outputObject) {
        checkWorkService.queryCheckWorkEcharts(inputObject, outputObject);
    }

    @ApiOperation(id = "checkwork018", value = "获取表格数据详情信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "userId", name = "userId", value = "用户id"),
        @ApiImplicitParam(id = "state", name = "state", value = "状态"),
        @ApiImplicitParam(id = "startTime", name = "startTime", value = "起始时间"),
        @ApiImplicitParam(id = "endTime", name = "endTime", value = "最后时间"),
        @ApiImplicitParam(id = "timeId", name = "timeId", value = "班次id"),
        @ApiImplicitParam(id = "day", name = "day", value = "指定日期")})
    @RequestMapping("/post/CheckWorkController/queryReportDetail")
    public void queryReportDetail(InputObject inputObject, OutputObject outputObject) {
        checkWorkService.queryReportDetail(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDayWorkMation", value = "获取指定天中的工作日", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DayWork.class)
    @RequestMapping("/post/CheckWorkController/queryDayWorkMation")
    public void queryDayWorkMation(InputObject inputObject, OutputObject outputObject) {
        checkWorkService.queryDayWorkMation(inputObject, outputObject);
    }

    @ApiOperation(id = "getUserOtherDayMation", value = "获取用户指定班次在指定月份的其他日期信息[审核通过的](例如：请假，出差，加班等)", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = UserOtherDayMation.class)
    @RequestMapping("/post/CheckWorkController/getUserOtherDayMation")
    public void getUserOtherDayMation(InputObject inputObject, OutputObject outputObject) {
        checkWorkService.getUserOtherDayMation(inputObject, outputObject);
    }

    @ApiOperation(id = "queryInfoByStaffIdsAndDates", value = "根据日期以及员工ids批量查询考勤信息，用于erp工序核算", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "staffIds", name = "staffIds", value = "员工id集合,逗号隔开", required = "required"),
        @ApiImplicitParam(id = "dates", name = "dates", value = "日期列表，逗号隔开", required = "required")})
    @RequestMapping("/post/CheckWorkController/queryInfoByStaffIdsAndDates")
    public void queryInfoByStaffIdsAndDates(InputObject inputObject, OutputObject outputObject) {
        checkWorkService.queryInfoByStaffIdsAndDates(inputObject, outputObject);
    }
}
