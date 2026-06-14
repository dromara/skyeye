/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentInspectionStatController
 * @Description: 设备巡检统计控制层
 */
@RestController
@Api(value = "设备巡检统计", tags = "设备巡检统计", modelName = "设备巡检统计")
public class EquipmentInspectionStatController {

    @Autowired
    private EquipmentInspectionOrderService equipmentInspectionOrderService;

    @Autowired
    private EquipmentInspectionStatService equipmentInspectionStatService;

    @ApiOperation(id = "queryInspectionRecordStatList", value = "巡检记录明细分页", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentInspectionStatController/queryInspectionRecordStatList")
    public void queryInspectionRecordStatList(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentInspectionSummaryList", value = "本月巡检设备明细分页", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentInspectionStatController/queryEquipmentInspectionSummaryList")
    public void queryEquipmentInspectionSummaryList(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionStatService.queryEquipmentInspectionSummaryList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentInspectionMissedList", value = "本月漏检设备明细分页", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentInspectionStatController/queryEquipmentInspectionMissedList")
    public void queryEquipmentInspectionMissedList(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionStatService.queryEquipmentInspectionMissedList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentInspectionDistributionPanel", value = "本月未检/已检分布", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentInspectionStatController/queryEquipmentInspectionDistributionPanel")
    public void queryEquipmentInspectionDistributionPanel(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionStatService.queryEquipmentInspectionDistributionPanel(inputObject, outputObject);
    }

    @ApiOperation(id = "queryInspectionRecordStatById", value = "根据ID获取巡检记录统计详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "巡检单id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionStatController/queryInspectionRecordStatById")
    public void queryInspectionRecordStatById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderService.selectById(inputObject, outputObject);
    }

}
