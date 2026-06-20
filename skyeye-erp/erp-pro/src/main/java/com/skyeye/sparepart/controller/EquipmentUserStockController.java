/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.sparepart.service.EquipmentUserStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设备维修-我的备件库存
 */
@RestController
@Api(value = "我的备件", tags = "我的备件", modelName = "设备备件")
public class EquipmentUserStockController {

    @Autowired
    private EquipmentUserStockService equipmentUserStockService;

    @ApiOperation(id = "queryEquipmentUserStockList", value = "获取我的备件库存列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentUserStockController/queryEquipmentUserStockList")
    public void queryEquipmentUserStockList(InputObject inputObject, OutputObject outputObject) {
        equipmentUserStockService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyPartsNumByNormsId", value = "根据规格id获取我的备件库存", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "normsId", name = "normsId", value = "规格id", required = "required")})
    @RequestMapping("/post/EquipmentUserStockController/queryMyPartsNumByNormsId")
    public void queryMyPartsNumByNormsId(InputObject inputObject, OutputObject outputObject) {
        equipmentUserStockService.queryMyPartsNumByNormsId(inputObject, outputObject);
    }

}
