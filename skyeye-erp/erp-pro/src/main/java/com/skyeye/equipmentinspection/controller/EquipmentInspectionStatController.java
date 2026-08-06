/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentinspection.service.EquipmentInspectionStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentInspectionStatController
 * @Description: 设备巡检统计控制层（对齐工单 AfterSealStatistics：卡片 + 图）
 */
@RestController
@Api(value = "设备巡检统计", tags = "设备巡检统计", modelName = "设备巡检统计")
public class EquipmentInspectionStatController {

    @Autowired
    private EquipmentInspectionStatService equipmentInspectionStatService;

    @ApiOperation(id = "queryTodayInspectedTotal", value = "今日已巡检次数", method = "POST", allUse = "2")
    @RequestMapping("/post/EquipmentInspectionStatController/queryTodayInspectedTotal")
    public void queryTodayInspectedTotal(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionStatService.queryTodayInspectedTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTodayAbnormalInspectionTotal", value = "今日异常巡检数", method = "POST", allUse = "2")
    @RequestMapping("/post/EquipmentInspectionStatController/queryTodayAbnormalInspectionTotal")
    public void queryTodayAbnormalInspectionTotal(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionStatService.queryTodayAbnormalInspectionTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryInspectionOrderStateStats", value = "巡检单按状态统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentInspectionStatController/queryInspectionOrderStateStats")
    public void queryInspectionOrderStateStats(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionStatService.queryInspectionOrderStateStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryInspectionOrderCompletionRateStats", value = "巡检单完成率统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentInspectionStatController/queryInspectionOrderCompletionRateStats")
    public void queryInspectionOrderCompletionRateStats(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionStatService.queryInspectionOrderCompletionRateStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryInspectionOrderStatsByCheckResult", value = "巡检单按检查结果统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentInspectionStatController/queryInspectionOrderStatsByCheckResult")
    public void queryInspectionOrderStatsByCheckResult(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionStatService.queryInspectionOrderStatsByCheckResult(inputObject, outputObject);
    }

    @ApiOperation(id = "queryInspectionOrderStatsByEquipment", value = "巡检单按设备统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentInspectionStatController/queryInspectionOrderStatsByEquipment")
    public void queryInspectionOrderStatsByEquipment(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionStatService.queryInspectionOrderStatsByEquipment(inputObject, outputObject);
    }

    @ApiOperation(id = "queryInspectionOrderStatsByInspector", value = "巡检单按巡检员统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentInspectionStatController/queryInspectionOrderStatsByInspector")
    public void queryInspectionOrderStatsByInspector(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionStatService.queryInspectionOrderStatsByInspector(inputObject, outputObject);
    }

    @ApiOperation(id = "queryInspectionOrderTrendStats", value = "巡检单日度趋势统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentInspectionStatController/queryInspectionOrderTrendStats")
    public void queryInspectionOrderTrendStats(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionStatService.queryInspectionOrderTrendStats(inputObject, outputObject);
    }

}
