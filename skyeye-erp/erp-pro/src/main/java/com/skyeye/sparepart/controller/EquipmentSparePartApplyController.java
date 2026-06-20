/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.sparepart.entity.EquipmentSparePartApply;
import com.skyeye.sparepart.entity.EquipmentSparePartApplyChangeStock;
import com.skyeye.sparepart.service.EquipmentSparePartApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设备备件-申领单
 */
@RestController
@Api(value = "备件申领单", tags = "备件申领单", modelName = "设备维修")
public class EquipmentSparePartApplyController {

    @Autowired
    private EquipmentSparePartApplyService equipmentSparePartApplyService;

    @ApiOperation(id = "queryEquipmentSparePartApplyList", value = "查询我的备件申领单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentSparePartApplyController/queryEquipmentSparePartApplyList")
    public void queryEquipmentSparePartApplyList(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartApplyService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentSparePartApply", value = "新增/编辑备件申领单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentSparePartApply.class)
    @RequestMapping("/post/EquipmentSparePartApplyController/writeEquipmentSparePartApply")
    public void writeEquipmentSparePartApply(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartApplyService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentSparePartApplyById", value = "根据ID查询备件申领单详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentSparePartApplyController/queryEquipmentSparePartApplyById")
    public void queryEquipmentSparePartApplyById(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartApplyService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentSparePartApplyById", value = "删除备件申领单", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentSparePartApplyController/deleteEquipmentSparePartApplyById")
    public void deleteEquipmentSparePartApplyById(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartApplyService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "submitToApproval", value = "备件申领单提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/EquipmentSparePartApplyController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartApplyService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revoke", value = "撤销备件申领单申请", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/EquipmentSparePartApplyController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartApplyService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "editApplyOtherState", value = "修改备件申领单出库状态", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "otherState", name = "otherState", value = "出库状态", required = "required,num")})
    @RequestMapping("/post/EquipmentSparePartApplyController/editApplyOtherState")
    public void editApplyOtherState(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartApplyService.editApplyOtherState(inputObject, outputObject);
    }

    @ApiOperation(id = "editApplyOutNum", value = "备件申领单出库后增加我的库存", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentSparePartApplyChangeStock.class)
    @RequestMapping("/post/EquipmentSparePartApplyController/editApplyOutNum")
    public void editApplyOutNum(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartApplyService.editApplyOutNum(inputObject, outputObject);
    }

}
