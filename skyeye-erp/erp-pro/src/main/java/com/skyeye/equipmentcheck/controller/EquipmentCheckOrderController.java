package com.skyeye.equipmentcheck.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentcheck.entity.EquipmentCheckOrder;
import com.skyeye.equipmentcheck.service.EquipmentCheckOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentCheckOrderController
 * @Description: 设备点检单控制层
 */
@RestController
@Api(value = "设备点检单", tags = "设备点检单", modelName = "设备点检")
public class EquipmentCheckOrderController {

    @Autowired
    private EquipmentCheckOrderService equipmentCheckOrderService;

    @ApiOperation(id = "queryEquipmentCheckOrderList", value = "获取设备点检单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentCheckOrderController/queryEquipmentCheckOrderList")
    public void queryEquipmentCheckOrderList(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckOrderService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentCheckOrder", value = "新增/编辑设备点检单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = EquipmentCheckOrder.class)
    @RequestMapping("/post/EquipmentCheckOrderController/writeEquipmentCheckOrder")
    public void writeEquipmentCheckOrder(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckOrderService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentCheckOrderById", value = "根据id查询设备点检单详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentCheckOrderController/queryEquipmentCheckOrderById")
    public void queryEquipmentCheckOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckOrderService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "submitEquipmentCheckOrderToApproval", value = "设备点检单提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/EquipmentCheckOrderController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckOrderService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeEquipmentCheckOrder", value = "撤销设备点检单申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/EquipmentCheckOrderController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckOrderService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentCheckOrderByIds", value = "删除设备点检单（传 ids，单个或多个逗号分隔）", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id列表，多个用逗号分隔", required = "required")})
    @RequestMapping(value = "/post/EquipmentCheckOrderController/deleteEquipmentCheckOrderByIds", method = RequestMethod.DELETE)
    public void deleteEquipmentCheckOrderByIds(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckOrderService.deleteByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentCheckStatistics", value = "设备点检统计", method = "POST", allUse = "1")
    @RequestMapping("/post/EquipmentCheckOrderController/queryStatistics")
    public void queryStatistics(InputObject inputObject, OutputObject outputObject) {
        equipmentCheckOrderService.queryStatistics(inputObject, outputObject);
    }

}

