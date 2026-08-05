/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentcheck.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentcheck.service.EquipmentCheckStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentCheckStatisticsController
 * @Description: 设备点检统计控制层
 */
@RestController
@Api(value = "设备点检统计", tags = "设备点检统计", modelName = "设备点检")
public class EquipmentCheckStatisticsController {

    @Autowired
    private EquipmentCheckStatisticsService equipmentCheckStatisticsService;

    @ApiOperation(id = "queryTodayCheckedTotal", value = "今日已点检次数", method = "POST", allUse = "2")
    @RequestMapping("/post/EquipmentCheckStatisticsController/queryTodayCheckedTotal")
    public void queryTodayCheckedTotal(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckStatisticsService.queryTodayCheckedTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTodayAbnormalCheckTotal", value = "今日异常点检数", method = "POST", allUse = "2")
    @RequestMapping("/post/EquipmentCheckStatisticsController/queryTodayAbnormalCheckTotal")
    public void queryTodayAbnormalCheckTotal(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckStatisticsService.queryTodayAbnormalCheckTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCheckStatsByCheckTime", value = "点检次数按点检时间趋势统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentCheckStatisticsController/queryCheckStatsByCheckTime")
    public void queryCheckStatsByCheckTime(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckStatisticsService.queryCheckStatsByCheckTime(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAbnormalCheckStatsByCheckTime", value = "异常点检数按点检时间趋势统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentCheckStatisticsController/queryAbnormalCheckStatsByCheckTime")
    public void queryAbnormalCheckStatsByCheckTime(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckStatisticsService.queryAbnormalCheckStatsByCheckTime(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCheckOrderStateStats", value = "点检单按审批状态统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentCheckStatisticsController/queryCheckOrderStateStats")
    public void queryCheckOrderStateStats(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckStatisticsService.queryCheckOrderStateStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCheckOrderToRepairRateStats", value = "点检转维修统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentCheckStatisticsController/queryCheckOrderToRepairRateStats")
    public void queryCheckOrderToRepairRateStats(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckStatisticsService.queryCheckOrderToRepairRateStats(inputObject, outputObject);
    }

}
