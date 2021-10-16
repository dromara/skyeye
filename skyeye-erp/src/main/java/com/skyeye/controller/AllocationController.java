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
import com.skyeye.service.AllocationService;

/**
 * @Author 卫志强
 * @Description 调拨单
 * @Date 2019/10/16 15:32
 */
@Controller
public class AllocationController {
	
	@Autowired
	private AllocationService allocationService;
	
	/**
     * 获取调拨单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/AllocationController/queryAllocationToList")
    @ResponseBody
    public void queryAllocationToList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	allocationService.queryAllocationToList(inputObject, outputObject);
    }
	
    /**
     * 新增调拨单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/AllocationController/insertAllocationMation")
    @ResponseBody
    public void insertAllocationMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	allocationService.insertAllocationMation(inputObject, outputObject);
    }
    
    /**
     * 编辑调拨单信息时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/AllocationController/queryAllocationMationToEditById")
    @ResponseBody
    public void queryAllocationMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	allocationService.queryAllocationMationToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑调拨单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/AllocationController/editAllocationMationById")
    @ResponseBody
    public void editAllocationMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	allocationService.editAllocationMationById(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/AllocationController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	allocationService.queryMationToExcel(inputObject, outputObject);
    }
    
    /**
     * 调拨单信息提交审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/AllocationController/editAllocationOrderStateToSubExamineById")
    @ResponseBody
    public void editAllocationOrderStateToSubExamineById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	allocationService.editAllocationOrderStateToSubExamineById(inputObject, outputObject);
    }
    
}
