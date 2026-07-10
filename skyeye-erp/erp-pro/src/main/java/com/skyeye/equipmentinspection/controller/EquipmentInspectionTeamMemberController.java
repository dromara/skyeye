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
import com.skyeye.equipmentinspection.entity.EquipmentInspectionTeamMember;
import com.skyeye.equipmentinspection.service.EquipmentInspectionTeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentInspectionTeamMemberController
 * @Description: 设备巡检班组人员控制层
 */
@RestController
@Api(value = "设备巡检班组人员", tags = "设备巡检班组人员", modelName = "设备巡检班组人员")
public class EquipmentInspectionTeamMemberController {

    @Autowired
    private EquipmentInspectionTeamMemberService equipmentInspectionTeamMemberService;

    @ApiOperation(id = "queryEquipmentInspectionTeamMemberList", value = "获取设备巡检班组人员列表（objectId=班组id）", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentInspectionTeamMemberController/queryEquipmentInspectionTeamMemberList")
    public void queryEquipmentInspectionTeamMemberList(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTeamMemberService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentInspectionTeamMember", value = "新增/编辑设备巡检班组人员", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = EquipmentInspectionTeamMember.class)
    @RequestMapping("/post/EquipmentInspectionTeamMemberController/writeEquipmentInspectionTeamMember")
    public void writeEquipmentInspectionTeamMember(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTeamMemberService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentInspectionTeamMemberById", value = "根据ID删除设备巡检班组人员", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentInspectionTeamMemberController/deleteEquipmentInspectionTeamMemberById")
    public void deleteEquipmentInspectionTeamMemberById(InputObject inputObject, OutputObject outputObject) {
        equipmentInspectionTeamMemberService.deleteById(inputObject, outputObject);
    }

}