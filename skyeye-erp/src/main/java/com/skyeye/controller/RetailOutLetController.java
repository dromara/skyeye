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
import com.skyeye.service.RetailOutLetService;

/**
 * @Author 卫志强
 * @Description 零售出库
 * @Date 2019/10/16 15:32
 */
@Controller
public class RetailOutLetController {
	
	@Autowired
	private RetailOutLetService retailOutLetService;
	
	/**
     * 获取零售出库列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/RetailOutLetController/queryRetailOutLetToList")
    @ResponseBody
    public void queryRetailOutLetToList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	retailOutLetService.queryRetailOutLetToList(inputObject, outputObject);
    }
	
    /**
     * 新增零售出库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/RetailOutLetController/insertRetailOutLetMation")
    @ResponseBody
    public void insertRetailOutLetMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	retailOutLetService.insertRetailOutLetMation(inputObject, outputObject);
    }
    
    /**
     * 编辑零售出库信息时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/RetailOutLetController/queryRetailOutLetMationToEditById")
    @ResponseBody
    public void queryRetailOutLetMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	retailOutLetService.queryRetailOutLetMationToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑零售出库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/RetailOutLetController/editRetailOutLetMationById")
    @ResponseBody
    public void editRetailOutLetMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	retailOutLetService.editRetailOutLetMationById(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/RetailOutLetController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	retailOutLetService.queryMationToExcel(inputObject, outputObject);
    }
    
}
