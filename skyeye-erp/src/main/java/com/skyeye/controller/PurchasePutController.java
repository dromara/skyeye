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
import com.skyeye.service.PurchasePutService;

/**
 * @Author 卫志强
 * @Description 采购入库
 * @Date 2019/10/16 15:32
 */
@Controller
public class PurchasePutController {
	
	@Autowired
	private PurchasePutService purchasePutService;
	
	/**
     * 获取采购入库列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchasePutController/queryPurchasePutToList")
    @ResponseBody
    public void queryPurchasePutToList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchasePutService.queryPurchasePutToList(inputObject, outputObject);
    }
	
    /**
     * 新增采购入库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchasePutController/insertPurchasePutMation")
    @ResponseBody
    public void insertPurchasePutMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchasePutService.insertPurchasePutMation(inputObject, outputObject);
    }
    
    /**
     * 编辑采购入库信息时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchasePutController/queryPurchasePutMationToEditById")
    @ResponseBody
    public void queryPurchasePutMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchasePutService.queryPurchasePutMationToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑采购入库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchasePutController/editPurchasePutMationById")
    @ResponseBody
    public void editPurchasePutMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchasePutService.editPurchasePutMationById(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/PurchasePutController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	purchasePutService.queryMationToExcel(inputObject, outputObject);
    }
    
}
