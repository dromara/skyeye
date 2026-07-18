/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.repair.entity.EquipmentScrapOrder;
import com.skyeye.repair.service.EquipmentScrapOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentScrapOrderController
 * @Description: 设备报废单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/04/30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "设备报废单", tags = "设备报废单", modelName = "设备报废单")
public class EquipmentScrapOrderController {

    @Autowired
    private EquipmentScrapOrderService equipmentScrapOrderService;

    @ApiOperation(id = "queryEquipmentScrapOrderList", value = "获取设备报废单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentScrapOrderController/queryEquipmentScrapOrderList")
    public void queryEquipmentScrapOrderList(InputObject inputObject, OutputObject outputObject) {
        equipmentScrapOrderService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentScrapOrderListByEquipmentId", value = "根据设备id分页查询报废单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "设备id", required = "required")})
    @RequestMapping("/post/EquipmentScrapOrderController/queryEquipmentScrapOrderListByEquipmentId")
    public void queryEquipmentScrapOrderListByEquipmentId(InputObject inputObject, OutputObject outputObject) {
        equipmentScrapOrderService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllEquipmentScrapOrderList", value = "获取所有设备报废单", method = "POST", allUse = "2")
    @RequestMapping("/post/EquipmentScrapOrderController/queryAllEquipmentScrapOrderList")
    public void queryAllEquipmentScrapOrderList(InputObject inputObject, OutputObject outputObject) {
        equipmentScrapOrderService.queryAllEquipmentScrapOrderList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentScrapOrder", value = "新增/编辑设备报废单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = EquipmentScrapOrder.class)
    @RequestMapping("/post/EquipmentScrapOrderController/writeEquipmentScrapOrder")
    public void writeEquipmentScrapOrder(InputObject inputObject, OutputObject outputObject) {
        equipmentScrapOrderService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentScrapOrderById", value = "根据ID查询设备报废单详情", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentScrapOrderController/queryEquipmentScrapOrderById")
    public void queryEquipmentScrapOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentScrapOrderService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentScrapOrderById", value = "根据ID删除设备报废单", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentScrapOrderController/deleteEquipmentScrapOrderById")
    public void deleteEquipmentScrapOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentScrapOrderService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentScrapOrderByIds", value = "批量删除设备报废单（传 ids）", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id列表，多个用逗号分隔", required = "required")})
    @RequestMapping("/post/EquipmentScrapOrderController/deleteEquipmentScrapOrderByIds")
    public void deleteEquipmentScrapOrderByIds(InputObject inputObject, OutputObject outputObject) {
        equipmentScrapOrderService.deleteByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "submitToApprovalEquipmentScrapOrder", value = "设备报废单提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/EquipmentScrapOrderController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        equipmentScrapOrderService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeEquipmentScrapOrder", value = "撤销设备报废单审批申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/EquipmentScrapOrderController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        equipmentScrapOrderService.revoke(inputObject, outputObject);
    }

}
