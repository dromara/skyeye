/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.maintenance.service.EquipmentMaintainStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设备保养单统计控制层
 */
@RestController
@Api(value = "设备保养单统计", tags = "设备保养", modelName = "设备保养")
public class EquipmentMaintainStatisticsController {

    @Autowired
    private EquipmentMaintainStatisticsService equipmentMaintainStatisticsService;

    @ApiOperation(id = "queryMaintainOrderStateStats", value = "保养单按状态统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentMaintainStatisticsController/queryMaintainOrderStateStats")
    public void queryMaintainOrderStateStats(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainStatisticsService.queryMaintainOrderStateStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMaintainOrderCompletionRateStats", value = "保养单完成率统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentMaintainStatisticsController/queryMaintainOrderCompletionRateStats")
    public void queryMaintainOrderCompletionRateStats(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainStatisticsService.queryMaintainOrderCompletionRateStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMaintainOrderSparePartStats", value = "保养单备件使用统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentMaintainStatisticsController/queryMaintainOrderSparePartStats")
    public void queryMaintainOrderSparePartStats(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainStatisticsService.queryMaintainOrderSparePartStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMaintainOrderTrendStats", value = "保养单日度趋势统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentMaintainStatisticsController/queryMaintainOrderTrendStats")
    public void queryMaintainOrderTrendStats(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainStatisticsService.queryMaintainOrderTrendStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMaintainOrderStatsByExecutor", value = "保养单按执行人统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentMaintainStatisticsController/queryMaintainOrderStatsByExecutor")
    public void queryMaintainOrderStatsByExecutor(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainStatisticsService.queryMaintainOrderStatsByExecutor(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMaintainOrderStatsByResult", value = "保养单按保养结果统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentMaintainStatisticsController/queryMaintainOrderStatsByResult")
    public void queryMaintainOrderStatsByResult(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainStatisticsService.queryMaintainOrderStatsByResult(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMaintainOrderStatsByPlan", value = "保养单按保养计划统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentMaintainStatisticsController/queryMaintainOrderStatsByPlan")
    public void queryMaintainOrderStatsByPlan(InputObject inputObject, OutputObject outputObject) {
        equipmentMaintainStatisticsService.queryMaintainOrderStatsByPlan(inputObject, outputObject);
    }
}
