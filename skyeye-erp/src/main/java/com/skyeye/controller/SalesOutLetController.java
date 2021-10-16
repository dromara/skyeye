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
import com.skyeye.service.SalesOutLetService;

/**
 * @Author 卫志强
 * @Description 销售出库
 * @Date 2019/10/16 15:32
 */
@Controller
public class SalesOutLetController {
	
	@Autowired
	private SalesOutLetService salesOutLetService;
	
	/**
     * 获取销售出库列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesOutLetController/querySalesOutLetToList")
    @ResponseBody
    public void querySalesOutLetToList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesOutLetService.querySalesOutLetToList(inputObject, outputObject);
    }
	
    /**
     * 新增销售出库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesOutLetController/insertSalesOutLetMation")
    @ResponseBody
    public void insertSalesOutLetMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesOutLetService.insertSalesOutLetMation(inputObject, outputObject);
    }
    
    /**
     * 编辑销售出库信息时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesOutLetController/querySalesOutLetMationToEditById")
    @ResponseBody
    public void querySalesOutLetMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesOutLetService.querySalesOutLetMationToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑销售出库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesOutLetController/editSalesOutLetMationById")
    @ResponseBody
    public void editSalesOutLetMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesOutLetService.editSalesOutLetMationById(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SalesOutLetController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	salesOutLetService.queryMationToExcel(inputObject, outputObject);
    }

}
