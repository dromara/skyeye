/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
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

    @ApiOperation(id = "queryRepairMonthlyTrendStats", value = "报修维修统计-按派工时间月度趋势", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentRepairStatisticsController/queryRepairMonthlyTrendStats")
    public void queryRepairMonthlyTrendStats(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairStatisticsService.queryRepairMonthlyTrendStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRepairStatsByEquipmentName", value = "报修维修统计-按设备id统计工单数（全量）", method = "POST", allUse = "2")
    @RequestMapping("/post/EquipmentRepairStatisticsController/queryRepairStatsByEquipmentName")
    public void queryRepairStatsByEquipmentName(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairStatisticsService.queryRepairStatsByEquipmentName(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRepairOrderPageList", value = "报修维修统计-按设备分页查询维修单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentRepairStatisticsController/queryRepairOrderPageList")
    public void queryRepairOrderPageList(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairStatisticsService.queryPageList(inputObject, outputObject);
    }
}
