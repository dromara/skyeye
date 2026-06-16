/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.worktime.entity.CheckWorkTime;
import com.skyeye.worktime.service.CheckWorkTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CheckWorkTimeController
 * @Description: 考勤班次管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/3 14:37
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "考勤班次", tags = "考勤班次", modelName = "考勤班次")
public class CheckWorkTimeController {

    @Autowired
    private CheckWorkTimeService checkWorkTimeService;

    @ApiOperation(id = "checkworktime001", value = "查询考勤班次列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CheckWorkTimeController/queryCheckWorkTimeList")
    public void queryCheckWorkTimeList(InputObject inputObject, OutputObject outputObject) {
        checkWorkTimeService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeCheckWorkTime", value = "新增/编辑考勤班次信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CheckWorkTime.class)
    @RequestMapping("/post/CheckWorkTimeController/writeCheckWorkTime")
    public void writeCheckWorkTime(InputObject inputObject, OutputObject outputObject) {
        checkWorkTimeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "setOnlineCheckWorkTime", value = "设置线上打卡的信息", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "checkWorkTimePointList", name = "checkWorkTimePointList", value = "打卡点位列表", required = "json")})
    @RequestMapping("/post/CheckWorkTimeController/setOnlineCheckWorkTime")
    public void setOnlineCheckWorkTime(InputObject inputObject, OutputObject outputObject) {
        checkWorkTimeService.setOnlineCheckWorkTime(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCheckWorkTimeById", value = "根据id查询考勤班次信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CheckWorkTimeController/queryCheckWorkTimeById")
    public void queryCheckWorkTimeById(InputObject inputObject, OutputObject outputObject) {
        checkWorkTimeService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCheckWorkTimeByIds", value = "根据id批量查询考勤班次信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/CheckWorkTimeController/queryCheckWorkTimeByIds")
    public void queryCheckWorkTimeByIds(InputObject inputObject, OutputObject outputObject) {
        checkWorkTimeService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteCheckWorkTimeById", value = "根据id删除考勤班次信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CheckWorkTimeController/deleteCheckWorkTimeById")
    public void deleteCheckWorkTimeById(InputObject inputObject, OutputObject outputObject) {
        checkWorkTimeService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnableCheckWorkTimeList", value = "查询启用的考勤班次列表", method = "GET", allUse = "2")
    @RequestMapping("/post/CheckWorkTimeController/queryEnableCheckWorkTimeList")
    public void queryEnableCheckWorkTimeList(InputObject inputObject, OutputObject outputObject) {
        checkWorkTimeService.queryEnableCheckWorkTimeList(inputObject, outputObject);
    }

    @ApiOperation(id = "checkworktime007", value = "获取当前登陆人的考勤班次", method = "GET", allUse = "2")
    @RequestMapping("/post/CheckWorkTimeController/queryCheckWorkTimeListByLoginUser")
    public void queryCheckWorkTimeListByLoginUser(InputObject inputObject, OutputObject outputObject) {
        checkWorkTimeService.queryCheckWorkTimeListByLoginUser(inputObject, outputObject);
    }

    @ApiOperation(id = "getAllCheckWorkTime", value = "根据指定年月获取所有的考勤班次的信息以及工作日信息等", method = "GET", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "pointMonthDate", name = "pointMonthDate", value = "指定年月，格式为yyyy-MM", required = "required")})
    @RequestMapping("/post/CheckWorkTimeController/getAllCheckWorkTime")
    public void getAllCheckWorkTime(InputObject inputObject, OutputObject outputObject) {
        checkWorkTimeService.getAllCheckWorkTime(inputObject, outputObject);
    }

}
