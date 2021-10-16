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
import com.skyeye.service.OtherOutLetsService;

/**
 * 其他出库
 * @author Lenovo
 *
 */
@Controller
public class OtherOutLetsController {
	
	@Autowired
	private OtherOutLetsService otherOutLetsService;
	
	/**
     * 获取其他出库列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/OtherOutLetsController/queryOtherOutLetsToList")
    @ResponseBody
    public void queryOtherOutLetsToList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	otherOutLetsService.queryOtherOutLetsToList(inputObject, outputObject);
    }
    
    /**
     * 新增其他出库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/OtherOutLetsController/insertOtherOutLetsMation")
    @ResponseBody
    public void insertOtherOutLetsMation(InputObject inputObject, OutputObject outputObject) throws Exception{
    	otherOutLetsService.insertOtherOutLetsMation(inputObject, outputObject);
    }
    
    /**
     * 其他出库信息编辑回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/OtherOutLetsController/queryOtherOutLetsToEditById")
    @ResponseBody
    public void queryOtherOutLetsToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	otherOutLetsService.queryOtherOutLetsToEditById(inputObject, outputObject);
    }
    
    /**
     * 编辑其他出库信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/OtherOutLetsController/editOtherOutLetsMationById")
    @ResponseBody
    public void editOtherOutLetsMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
    	otherOutLetsService.editOtherOutLetsMationById(inputObject, outputObject);
    }
    
    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/OtherOutLetsController/queryMationToExcel")
    @ResponseBody
    public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception{
    	otherOutLetsService.queryMationToExcel(inputObject, outputObject);
    }
    
}
