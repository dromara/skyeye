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
import com.skyeye.service.PurchaseReturnsService;

/**
 * @Author 卫志强
 * @Description 采购退货
 * @Date 2019/10/16 15:32
 */
@Controller
public class PurchaseReturnsController {
	
	@Autowired
	private PurchaseReturnsService purchaseReturnsService;
	
	/**
     * 获取采购退货列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchaseReturnsController/queryPurchaseReturnsToList")
    @ResponseBody
    public void queryPurchaseReturnsToList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchaseReturnsService.queryPurchaseReturnsToList(inputObject, outputObject);
    }
	
    /**
     * 新增采购退货信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchaseReturnsController/insertPurchaseReturnsMation")
    @ResponseBody
    public void insertPurchaseReturnsMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchaseReturnsService.insertPurchaseReturnsMation(inputObject, outputObject);
    }
    
    /**
     * 编辑采购退货信息时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchaseReturnsController/queryPurchaseReturnsMationToEditById")
    @ResponseBody
    public void queryPurchaseReturnsMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchaseReturnsService.queryPurchaseReturnsMationToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑采购退货信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchaseReturnsController/editPurchaseReturnsMationById")
    @ResponseBody
    public void editPurchaseReturnsMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchaseReturnsService.editPurchaseReturnsMationById(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchaseReturnsController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchaseReturnsService.queryMationToExcel(inputObject, outputObject);
    }
    
}
