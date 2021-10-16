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
import com.skyeye.service.PurchaseOrderService;

/**
 * 采购管理
 * @author Lenovo
 *
 */
@Controller
public class PurchaseOrderController {
	
	@Autowired
	private PurchaseOrderService purchaseOrderService;
	
	/**
     * 获取采购单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchaseOrderController/queryPurchaseOrderToList")
    @ResponseBody
    public void queryPurchaseOrderToList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchaseOrderService.queryPurchaseOrderToList(inputObject, outputObject);
    }
    
    /**
     * 新增采购单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchaseOrderController/insertPurchaseOrderMation")
    @ResponseBody
    public void insertPurchaseOrderMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchaseOrderService.insertPurchaseOrderMation(inputObject, outputObject);
    }
    
    /**
     * 采购单信息编辑回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchaseOrderController/queryPurchaseOrderToEditById")
    @ResponseBody
    public void queryPurchaseOrderToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchaseOrderService.queryPurchaseOrderToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑采购单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchaseOrderController/editPurchaseOrderMationById")
    @ResponseBody
    public void editPurchaseOrderMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchaseOrderService.editPurchaseOrderMationById(inputObject, outputObject);
    }
    
    /**
     * 采购单信息转采购入库回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchaseOrderController/queryPurchaseOrderToTurnPutById")
    @ResponseBody
    public void queryPurchaseOrderToTurnPutById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchaseOrderService.queryPurchaseOrderToTurnPutById(inputObject, outputObject);
    }
    
    /**
     * 采购单信息转采购入库
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchaseOrderController/insertPurchaseOrderToTurnPut")
    @ResponseBody
    public void insertPurchaseOrderToTurnPut(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchaseOrderService.insertPurchaseOrderToTurnPut(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchaseOrderController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchaseOrderService.queryMationToExcel(inputObject, outputObject);
    }
    
}
