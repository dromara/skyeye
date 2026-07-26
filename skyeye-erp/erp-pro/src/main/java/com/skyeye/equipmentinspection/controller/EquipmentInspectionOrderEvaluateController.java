/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrderEvaluate;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderEvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentInspectionOrderEvaluateController
 * @Description: 设备巡检单评价控制层
 */
@RestController
@Api(value = "设备巡检单评价", tags = "设备巡检单评价", modelName = "设备巡检单")
public class EquipmentInspectionOrderEvaluateController {

    @Autowired
    private EquipmentInspectionOrderEvaluateService equipmentInspectionOrderEvaluateService;

    @ApiOperation(id = "queryEquipmentInspectionOrderEvaluateList", value = "获取巡检单评价列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentInspectionOrderEvaluateController/queryEquipmentInspectionOrderEvaluateList")
    public void queryEquipmentInspectionOrderEvaluateList(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderEvaluateService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "insertEquipmentInspectionOrderEvaluate", value = "新增巡检单评价", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentInspectionOrderEvaluate.class)
    @RequestMapping("/post/EquipmentInspectionOrderEvaluateController/insertEquipmentInspectionOrderEvaluate")
    public void insertEquipmentInspectionOrderEvaluate(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionOrderEvaluateService.createEntity(inputObject, outputObject);
    }

}
