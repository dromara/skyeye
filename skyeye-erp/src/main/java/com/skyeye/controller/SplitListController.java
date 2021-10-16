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
import com.skyeye.service.SplitListService;

/**
 * @Author 卫志强
 * @Description 拆分单
 * @Date 2019/10/16 15:32
 */
@Controller
public class SplitListController {
	
	@Autowired
	private SplitListService splitListService;
	
	/**
     * 获取拆分单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SplitListController/querySplitListToList")
    @ResponseBody
    public void querySplitListToList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	splitListService.querySplitListToList(inputObject, outputObject);
    }
	
    /**
     * 新增拆分单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SplitListController/insertSplitListMation")
    @ResponseBody
    public void insertSplitListMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	splitListService.insertSplitListMation(inputObject, outputObject);
    }
    
    /**
     * 编辑拆分单信息时进行回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SplitListController/querySplitListMationToEditById")
    @ResponseBody
    public void querySplitListMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	splitListService.querySplitListMationToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑拆分单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SplitListController/editSplitListMationById")
    @ResponseBody
    public void editSplitListMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	splitListService.editSplitListMationById(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SplitListController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	splitListService.queryMationToExcel(inputObject, outputObject);
    }
    
    /**
     * 拆分单信息提交审核
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/SplitListController/editSplitOrderStateToSubExamineById")
    @ResponseBody
    public void editSplitOrderStateToSubExamineById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	splitListService.editSplitOrderStateToSubExamineById(inputObject, outputObject);
    }
    
}
