/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.seal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.production.entity.ProductionPlan;
import com.skyeye.purchase.entity.PurchaseOrder;
import com.skyeye.seal.entity.SalesExchanges;
import com.skyeye.seal.entity.SalesOrder;
import com.skyeye.seal.entity.SalesOutLet;
import com.skyeye.seal.entity.SalesReturns;
import com.skyeye.seal.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SalesOrderController
 * @Description: 销售订单管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "销售订单", tags = "销售订单", modelName = "销售模块")
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    @ApiOperation(id = "salesorder001", value = "获取销售订单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SalesOrderController/querySalesOrderToList")
    public void querySalesOrderToList(InputObject inputObject, OutputObject outputObject) {
        salesOrderService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeSalesOrder", value = "新增/编辑销售订单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SalesOrder.class)
    @RequestMapping("/post/SalesOrderController/writeSalesOrder")
    public void writeSalesOrder(InputObject inputObject, OutputObject outputObject) {
        salesOrderService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "salesorder011", value = "获取审核通过的销售单列表展示为树", method = "GET", allUse = "2")
    @RequestMapping("/post/SalesOrderController/querySalesOrderListToTree")
    public void querySalesOrderListToTree(InputObject inputObject, OutputObject outputObject) {
        salesOrderService.querySalesOrderListToTree(inputObject, outputObject);
    }

    @ApiOperation(id = "salesorder012", value = "根据销售单id获取子单据列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesOrderController/querySalesOrderMaterialListByOrderId")
    public void querySalesOrderMaterialListByOrderId(InputObject inputObject, OutputObject outputObject) {
        salesOrderService.querySalesOrderMaterialListByOrderId(inputObject, outputObject);
    }

    @ApiOperation(id = "querySealsOrderTransById", value = "转销售出库单/销售退货单/销售换货单时，根据id查询销售订单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesOrderController/querySealsOrderTransById")
    public void querySealsOrderTransById(InputObject inputObject, OutputObject outputObject) {
        salesOrderService.querySealsOrderTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "salesorder009", value = "销售订单转销售出库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SalesOutLet.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesOrderController/insertSalesOrderToTurnPut")
    public void insertSalesOrderToTurnPut(InputObject inputObject, OutputObject outputObject) {
        salesOrderService.insertSalesOrderToTurnPut(inputObject, outputObject);
    }

    @ApiOperation(id = "insertSealsOrderToSealsReturns", value = "销售订单转销售退货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SalesReturns.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesOrderController/insertSealsOrderToSealsReturns")
    public void insertSealsOrderToSealsReturns(InputObject inputObject, OutputObject outputObject) {
        salesOrderService.insertSealsOrderToSealsReturns(inputObject, outputObject);
    }

    @ApiOperation(id = "insertSealsOrderToSealExchanges", value = "销售订单转换货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SalesExchanges.class, value = {
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesOrderController/insertSealsOrderToSealExchanges")
    public void insertSealsOrderToSealExchanges(InputObject inputObject, OutputObject outputObject) {
        salesOrderService.insertSealsOrderToSealExchanges(inputObject, outputObject);
    }

    /**
     * 客户合同转销售订单时，根据id查询客户合同信息
     * 因为这里涉及到了微服务的调用，直接写在ERP微服务会方便很多
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryCrmContractTransById", value = "客户合同转销售订单时，根据id查询客户合同信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesOrderController/queryCrmContractTransById")
    public void queryCrmContractTransById(InputObject inputObject, OutputObject outputObject) {
        salesOrderService.queryCrmContractTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertCrmContractToSealsOrder", value = "客户合同转销售订单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SalesOrder.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesOrderController/insertCrmContractToSealsOrder")
    public void insertCrmContractToSealsOrder(InputObject inputObject, OutputObject outputObject) {
        salesOrderService.insertCrmContractToSealsOrder(inputObject, outputObject);
    }

    @ApiOperation(id = "querySealsOrderTransProductionPlanById", value = "转出货计划单时，根据id查询销售订单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesOrderController/querySealsOrderTransProductionPlanById")
    public void querySealsOrderTransProductionPlanById(InputObject inputObject, OutputObject outputObject) {
        salesOrderService.querySealsOrderTransProductionPlanById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertSealsOrderToProductionPlan", value = "销售订单转出货计划单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ProductionPlan.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesOrderController/insertSealsOrderToProductionPlan")
    public void insertSealsOrderToProductionPlan(InputObject inputObject, OutputObject outputObject) {
        salesOrderService.insertSealsOrderToProductionPlan(inputObject, outputObject);
    }

    @ApiOperation(id = "querySealsOrderTransPurchaseOrderById", value = "转采购订单时，根据id查询销售订单信息（仅外购商品）", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesOrderController/querySealsOrderTransPurchaseOrderById")
    public void querySealsOrderTransPurchaseOrderById(InputObject inputObject, OutputObject outputObject) {
        salesOrderService.querySealsOrderTransPurchaseOrderById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertSealsOrderToPurchaseOrder", value = "销售订单转采购订单（仅外购商品）", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseOrder.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesOrderController/insertSealsOrderToPurchaseOrder")
    public void insertSealsOrderToPurchaseOrder(InputObject inputObject, OutputObject outputObject) {
        salesOrderService.insertSealsOrderToPurchaseOrder(inputObject, outputObject);
    }

}
