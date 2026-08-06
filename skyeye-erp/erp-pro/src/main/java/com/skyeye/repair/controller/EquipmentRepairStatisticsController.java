/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.repair.service.EquipmentRepairStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(value = "报修维修统计", tags = "报修维修统计", modelName = "报修维修统计")
public class EquipmentRepairStatisticsController {

    @Autowired
    private EquipmentRepairStatisticsService equipmentRepairStatisticsService;

    @ApiOperation(id = "queryEquipmentRepairOrderTrendStats", value = "报修维修统计-按创建时间日度趋势", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentRepairStatisticsController/queryEquipmentRepairOrderTrendStats")
    public void queryEquipmentRepairOrderTrendStats(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairStatisticsService.queryEquipmentRepairOrderTrendStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryOverviewEquipmentRepairOrder", value = "报修维修统计-总览卡片", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentRepairStatisticsController/queryOverviewEquipmentRepairOrder")
    public void queryOverviewEquipmentRepairOrder(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairStatisticsService.queryOverviewEquipmentRepairOrder(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRepairOrderStateStats", value = "报修维修统计-按状态划分", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentRepairStatisticsController/queryRepairOrderStateStats")
    public void queryRepairOrderStateStats(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairStatisticsService.queryRepairOrderStateStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRepairOrderStatsByUrgency", value = "报修维修统计-按紧急程度划分", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentRepairStatisticsController/queryRepairOrderStatsByUrgency")
    public void queryRepairOrderStatsByUrgency(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairStatisticsService.queryRepairOrderStatsByUrgency(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRepairOrderStatsByServiceUser", value = "报修维修统计-按维修负责人划分", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentRepairStatisticsController/queryRepairOrderStatsByServiceUser")
    public void queryRepairOrderStatsByServiceUser(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairStatisticsService.queryRepairOrderStatsByServiceUser(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRepairOrderStatsByFaultType", value = "报修维修统计-按故障类别划分", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentRepairStatisticsController/queryRepairOrderStatsByFaultType")
    public void queryRepairOrderStatsByFaultType(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairStatisticsService.queryRepairOrderStatsByFaultType(inputObject, outputObject);
    }
}
