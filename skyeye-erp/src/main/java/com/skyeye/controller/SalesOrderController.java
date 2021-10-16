/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.SalesOrderService;

/**
 * 销售管理
 * @author Lenovo
 *
 */
@Controller
public class SalesOrderController {
	
	@Autowired
	private SalesOrderService salesOrderService;
	
	/**
     * 获取销售单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesOrderController/querySalesOrderToList")
    @ResponseBody
    public void querySalesOrderToList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesOrderService.querySalesOrderToList(inputObject, outputObject);
    }
    
    /**
     * 新增销售单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesOrderController/insertSalesOrderMation")
    @ResponseBody
    public void insertSalesOrderMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesOrderService.insertSalesOrderMation(inputObject, outputObject);
    }
    
    /**
     * 销售单信息编辑回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesOrderController/querySalesOrderToEditById")
    @ResponseBody
    public void querySalesOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesOrderService.querySalesOrderToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑销售单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesOrderController/editSalesOrderMationById")
    @ResponseBody
    public void editSalesOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesOrderService.editSalesOrderMationById(inputObject, outputObject);
    }
    
    /**
     * 销售单信息转销售出库回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesOrderController/querySalesOrderToTurnPutById")
    @ResponseBody
    public void querySalesOrderToTurnPutById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesOrderService.querySalesOrderToTurnPutById(inputObject, outputObject);
    }
    
    /**
     * 销售单信息转销售出库
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesOrderController/insertSalesOrderToTurnPut")
    @ResponseBody
    public void insertSalesOrderToTurnPut(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesOrderService.insertSalesOrderToTurnPut(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesOrderController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesOrderService.queryMationToExcel(inputObject, outputObject);
    }
    
    /**
     * 获取审核通过的销售单列表展示为树
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesOrderController/querySalesOrderListToTree")
    @ResponseBody
    public void querySalesOrderListToTree(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesOrderService.querySalesOrderListToTree(inputObject, outputObject);
    }
    
    /**
     * 根据销售单id获取销售的子单据列表
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesOrderController/querySalesOrderMaterialListByOrderId")
    @ResponseBody
    public void querySalesOrderMaterialListByOrderId(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesOrderService.querySalesOrderMaterialListByOrderId(inputObject, outputObject);
    }
    
}
