/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipment.classenum.EquipmentState;
import com.skyeye.repair.classenum.EquipmentRepairFaultReason;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentRepairOrderController
 * @Description: 设备维修单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "设备维修单", tags = "设备维修单", modelName = "设备维修单")
public class EquipmentRepairOrderController {

    @Autowired
    private EquipmentRepairOrderService equipmentRepairOrderService;

    @ApiOperation(id = "queryEquipmentRepairOrderList", value = "获取设备维修单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentRepairOrderController/queryEquipmentRepairOrderList")
    public void queryEquipmentRepairOrderList(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllEquipmentRepairOrderList", value = "获取所有设备维修单", method = "POST", allUse = "2")
    @RequestMapping("/post/EquipmentRepairOrderController/queryAllEquipmentRepairOrderList")
    public void queryAllEquipmentRepairOrderList(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.queryAllEquipmentRepairOrderList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentRepairOrder", value = "新增/添加故障报修", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = EquipmentRepairOrder.class)
    @RequestMapping("/post/EquipmentRepairOrderController/writeEquipmentRepairOrder")
    public void writeEquipmentRepairOrder(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "insertEquipmentRepairWaitToWorkMation", value = "派工", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "serviceUserId", name = "serviceUserId", value = "接收人", required = "required"),
        @ApiImplicitParam(id = "supplierId", name = "supplierId", value = "供应商")})
    @RequestMapping("/post/EquipmentRepairOrderController/insertEquipmentRepairWaitToWorkMation")
    public void insertEquipmentRepairWaitToWorkMation(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.insertEquipmentRepairWaitToWorkMation(inputObject, outputObject);
    }

    @ApiOperation(id = "insertEquipmentRepairResult", value = "添加维修结果", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "isRepaired", name = "isRepaired", value = "是否进行维修", enumClass = WhetherEnum.class, required = "required,num"),
        @ApiImplicitParam(id = "isReplaceSpare", name = "isReplaceSpare", value = "是否更换配件", enumClass = WhetherEnum.class, required = "num"),
        @ApiImplicitParam(id = "faultReason", name = "faultReason", value = "故障原因", enumClass = EquipmentRepairFaultReason.class, required = "num"),
        @ApiImplicitParam(id = "cancelReason", name = "cancelReason", value = "作废原因"),
        @ApiImplicitParam(id = "repairDesc", name = "repairDesc", value = "维修情况说明"),
        @ApiImplicitParam(id = "repairFinishPhoto", name = "repairFinishPhoto", value = "维修完成拍照"),
        @ApiImplicitParam(id = "sparePartUsageList", name = "sparePartUsageList", value = "备件使用明细", required = "json")})
    @RequestMapping("/post/EquipmentRepairOrderController/insertEquipmentRepairResult")
    public void insertEquipmentRepairResult(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.insertEquipmentRepairResult(inputObject, outputObject);
    }

    @ApiOperation(id = "completeEquipmentRepairOrderById", value = "完工操作", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/completeEquipmentRepairOrderById")
    public void completeEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.completeEquipmentRepairOrderById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertEquipmentRepairEvaluate", value = "添加评价", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "evaluateTypeId", name = "evaluateTypeId", value = "评价类型，参考数据字典", required = "required"),
        @ApiImplicitParam(id = "evaluateContent", name = "evaluateContent", value = "评价内容")})
    @RequestMapping("/post/EquipmentRepairOrderController/insertEquipmentRepairEvaluate")
    public void insertEquipmentRepairEvaluate(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.insertEquipmentRepairEvaluate(inputObject, outputObject);
    }

    @ApiOperation(id = "insertEquipmentRepairAcceptance", value = "添加结果确认", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "isFixed", name = "isFixed", value = "是否修复", enumClass = WhetherEnum.class, required = "required,num"),
        @ApiImplicitParam(id = "equipmentState", name = "equipmentState", value = "设备状态", enumClass = EquipmentState.class, required = "required,num")})
    @RequestMapping("/post/EquipmentRepairOrderController/insertEquipmentRepairAcceptance")
    public void insertEquipmentRepairAcceptance(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.insertEquipmentRepairAcceptance(inputObject, outputObject);
    }

    @ApiOperation(id = "receivingEquipmentRepairOrderById", value = "接单", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/receivingEquipmentRepairOrderById")
    public void receivingEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.receivingEquipmentRepairOrderById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentRepairOrderById", value = "根据id查询设备维修单", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/queryEquipmentRepairOrderById")
    public void queryEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentRepairOrderById", value = "删除设备维修单", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/deleteEquipmentRepairOrderById")
    public void deleteEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.deleteById(inputObject, outputObject);
    }

}
