/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ApiMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiMationController {

	@Autowired
	private ApiMationService apiMationService;
	
	/**
	 * 
	     * @Title: insertApiMationMation
	     * @Description: 新增api接口信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ApiMationController/insertApiMationMation")
	@ResponseBody
	public void insertApiMationMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		apiMationService.insertApiMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectApiMationById
	     * @Description: 查看api接口信息详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ApiMationController/selectApiMationById")
	@ResponseBody
	public void selectApiMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		apiMationService.selectApiMationById(inputObject, outputObject);
	}
	
}
