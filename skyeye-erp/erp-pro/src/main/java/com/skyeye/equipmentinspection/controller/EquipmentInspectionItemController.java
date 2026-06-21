/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionItem;
import com.skyeye.equipmentinspection.service.EquipmentInspectionItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentInspectionItemController
 * @Description: 设备巡检项目控制层
 */
@RestController
@Api(value = "设备巡检项目", tags = "设备巡检项目", modelName = "设备巡检")
public class EquipmentInspectionItemController {

    @Autowired
    private EquipmentInspectionItemService equipmentInspectionItemService;

    @ApiOperation(id = "queryEquipmentInspectionItemList", value = "获取设备巡检项目列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentInspectionItemController/queryEquipmentInspectionItemList")
    public void queryEquipmentInspectionItemList(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionItemService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentInspectionItem", value = "新增/编辑设备巡检项目", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = EquipmentInspectionItem.class)
    @RequestMapping("/post/EquipmentInspectionItemController/writeEquipmentInspectionItem")
    public void writeEquipmentInspectionItem(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionItemService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentInspectionItemById", value = "根据ID获取设备巡检项目", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionItemController/queryEquipmentInspectionItemById")
    public void queryEquipmentInspectionItemById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionItemService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentInspectionItemById", value = "根据ID删除设备巡检项目", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionItemController/deleteEquipmentInspectionItemById")
    public void deleteEquipmentInspectionItemById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionItemService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllEquipmentInspectionItemList", value = "获取所有设备巡检项目（下拉选用）", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "enabled", name = "enabled", value = "启用状态", enumClass = EnableEnum.class)})
    @RequestMapping("/post/EquipmentInspectionItemController/queryAllEquipmentInspectionItemList")
    public void queryAllEquipmentInspectionItemList(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionItemService.queryAllEquipmentInspectionItemList(inputObject, outputObject);
    }

}
