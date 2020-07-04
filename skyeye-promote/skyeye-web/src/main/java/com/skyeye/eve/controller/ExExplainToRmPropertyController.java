/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.ExExplainToRmPropertyService;

@Controller
public class ExExplainToRmPropertyController {
	
	@Autowired
	private ExExplainToRmPropertyService exExplainToRmPropertyService;
	
	/**
	 * 
	     * @Title: insertExExplainToRmPropertyMation
	     * @Description: 添加标签属性说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToRmPropertyController/insertExExplainToRmPropertyMation")
	@ResponseBody
	public void insertExExplainToRmPropertyMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToRmPropertyService.insertExExplainToRmPropertyMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryExExplainToRmPropertyMation
	     * @Description: 编辑标签属性说明信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToRmPropertyController/queryExExplainToRmPropertyMation")
	@ResponseBody
	public void queryExExplainToRmPropertyMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToRmPropertyService.queryExExplainToRmPropertyMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editExExplainToRmPropertyMationById
	     * @Description: 编辑标签属性说明信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToRmPropertyController/editExExplainToRmPropertyMationById")
	@ResponseBody
	public void editExExplainToRmPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToRmPropertyService.editExExplainToRmPropertyMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryExExplainToRmPropertyMationToShow
	     * @Description: 获取标签属性说明信息供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ExExplainToRmPropertyController/queryExExplainToRmPropertyMationToShow")
	@ResponseBody
	public void queryExExplainToRmPropertyMationToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		exExplainToRmPropertyService.queryExExplainToRmPropertyMationToShow(inputObject, outputObject);
	}
	
}
