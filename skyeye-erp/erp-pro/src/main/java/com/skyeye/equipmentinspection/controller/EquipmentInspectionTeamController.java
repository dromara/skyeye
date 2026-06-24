/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionTeam;
import com.skyeye.equipmentinspection.service.EquipmentInspectionTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentInspectionTeamController
 * @Description: 设备巡检班组控制层
 */
@RestController
@Api(value = "设备巡检班组", tags = "设备巡检班组", modelName = "设备巡检班组")
public class EquipmentInspectionTeamController {

    @Autowired
    private EquipmentInspectionTeamService equipmentInspectionTeamService;

    @ApiOperation(id = "queryEquipmentInspectionTeamList", value = "获取设备巡检班组列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/EquipmentInspectionTeamController/queryEquipmentInspectionTeamList")
    public void queryEquipmentInspectionTeamList(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTeamService.queryList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentInspectionTeam", value = "新增/编辑设备巡检班组", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = EquipmentInspectionTeam.class)
    @RequestMapping("/post/EquipmentInspectionTeamController/writeEquipmentInspectionTeam")
    public void writeEquipmentInspectionTeam(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTeamService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentInspectionTeamById", value = "根据ID获取设备巡检班组信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionTeamController/queryEquipmentInspectionTeamById")
    public void queryEquipmentInspectionTeamById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTeamService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentInspectionTeamById", value = "根据ID删除设备巡检班组", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionTeamController/deleteEquipmentInspectionTeamById")
    public void deleteEquipmentInspectionTeamById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTeamService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllEquipmentInspectionTeamList", value = "获取所有设备巡检班组列表", method = "POST", allUse = "2")
    @RequestMapping("/post/EquipmentInspectionTeamController/queryAllEquipmentInspectionTeamList")
    public void queryAllEquipmentInspectionTeamList(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTeamService.queryAllEquipmentInspectionTeamList(inputObject, outputObject);
    }

}