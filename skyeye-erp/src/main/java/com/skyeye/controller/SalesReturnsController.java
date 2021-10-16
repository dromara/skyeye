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
import com.skyeye.service.SalesReturnsService;

/**
 * @Author 卫志强
 * @Description 销售退货
 * @Date 2019/10/16 15:32
 */
@Controller
public class SalesReturnsController {
	
	@Autowired
	private SalesReturnsService salesReturnsService;
	
	/**
     * 获取销售退货列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesReturnsController/querySalesReturnsToList")
    @ResponseBody
    public void querySalesReturnsToList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesReturnsService.querySalesReturnsToList(inputObject, outputObject);
    }
	
    /**
     * 新增销售退货信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesReturnsController/insertSalesReturnsMation")
    @ResponseBody
    public void insertSalesReturnsMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesReturnsService.insertSalesReturnsMation(inputObject, outputObject);
    }
    
    /**
     * 编辑销售退货信息时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesReturnsController/querySalesReturnsMationToEditById")
    @ResponseBody
    public void querySalesReturnsMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesReturnsService.querySalesReturnsMationToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑销售退货信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesReturnsController/editSalesReturnsMationById")
    @ResponseBody
    public void editSalesReturnsMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesReturnsService.editSalesReturnsMationById(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesReturnsController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesReturnsService.queryMationToExcel(inputObject, outputObject);
    }
    
}
