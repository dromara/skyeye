/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.ExExplainService;

@Controller
public class ExExplainController {
	
	@Autowired
	private ExExplainService exExplainService;
	
	/**
	 * 
	     * @Title: insertExExplainMation
	     * @Description: 添加代码生成器说明信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainController/insertExExplainMation")
	@ResponseBody
	public void insertExExplainMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainService.insertExExplainMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryExExplainMation
	     * @Description: 编辑代码生成器说明信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainController/queryExExplainMation")
	@ResponseBody
	public void queryExExplainMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainService.queryExExplainMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editExExplainMationById
	     * @Description: 编辑代码生成器说明信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainController/editExExplainMationById")
	@ResponseBody
	public void editExExplainMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainService.editExExplainMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryExExplainMationToShow
	     * @Description: 获取代码生成器说明信息供展示
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainController/queryExExplainMationToShow")
	@ResponseBody
	public void queryExExplainMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainService.queryExExplainMationToShow(inputObject, outputObject);
	}
	
}
