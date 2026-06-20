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
import com.skyeye.sparepart.entity.EquipmentSparePartRequisition;
import com.skyeye.sparepart.service.EquipmentSparePartRequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 备件领用单（支持从我的库存发起领用）
 */
@RestController
@Api(value = "备件领用单", tags = "备件领用单", modelName = "备件领用单")
public class EquipmentSparePartRequisitionController {

    @Autowired
    private EquipmentSparePartRequisitionService equipmentSparePartRequisitionService;

    @ApiOperation(id = "queryEquipmentSparePartRequisitionList", value = "获取备件领用单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentSparePartRequisitionController/queryEquipmentSparePartRequisitionList")
    public void queryEquipmentSparePartRequisitionList(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartRequisitionService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentSparePartRequisition", value = "新增/编辑备件领用单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentSparePartRequisition.class)
    @RequestMapping("/post/EquipmentSparePartRequisitionController/writeEquipmentSparePartRequisition")
    public void writeEquipmentSparePartRequisition(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartRequisitionService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentSparePartRequisitionById", value = "根据ID查询备件领用单详情", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentSparePartRequisitionController/queryEquipmentSparePartRequisitionById")
    public void queryEquipmentSparePartRequisitionById(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartRequisitionService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentSparePartRequisitionById", value = "根据ID删除备件领用单", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentSparePartRequisitionController/deleteEquipmentSparePartRequisitionById")
    public void deleteEquipmentSparePartRequisitionById(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartRequisitionService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentSparePartRequisitionByIds", value = "批量删除备件领用单", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id列表，多个用逗号分隔", required = "required")})
    @RequestMapping("/post/EquipmentSparePartRequisitionController/deleteEquipmentSparePartRequisitionByIds")
    public void deleteEquipmentSparePartRequisitionByIds(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartRequisitionService.deleteByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "submitToApprovalEquipmentSparePartRequisition", value = "备件领用单提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/EquipmentSparePartRequisitionController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartRequisitionService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeEquipmentSparePartRequisition", value = "撤销备件领用单审批申请", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/EquipmentSparePartRequisitionController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartRequisitionService.revoke(inputObject, outputObject);
    }

}
