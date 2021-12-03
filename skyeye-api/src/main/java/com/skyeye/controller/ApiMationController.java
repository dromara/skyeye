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
	 * @param inputObject
	 * @param outputObject
	 * @return void    返回类型
	 * @throws Exception 参数
	 * @throws
	 * @Title: queryApiMationList
	 * @Description: 获取api接口信息列表
	 */
	@RequestMapping("/post/ApiMationController/queryApiMationList")
	@ResponseBody
	public void queryApiMationList(InputObject inputObject, OutputObject outputObject) throws Exception {
		apiMationService.queryApiMationList(inputObject, outputObject);
	}

	/**
	 * @param inputObject
	 * @param outputObject
	 * @return void    返回类型
	 * @throws Exception 参数
	 * @throws
	 * @Title: insertApiMationMation
	 * @Description: 新增api接口信息
	 */
	@RequestMapping("/post/ApiMationController/insertApiMationMation")
	@ResponseBody
	public void insertApiMationMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		apiMationService.insertApiMation(inputObject, outputObject);
	}

	/**
	 * @param inputObject
	 * @param outputObject
	 * @return void    返回类型
	 * @throws Exception 参数
	 * @throws
	 * @Title: deleteApiMationById
	 * @Description: 删除api接口信息
	 */
	@RequestMapping("/post/ApiMationController/deleteApiMationById")
	@ResponseBody
	public void deleteApiMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		apiMationService.deleteApiMationById(inputObject, outputObject);
	}

	/**
	 * @param inputObject
	 * @param outputObject
	 * @return void    返回类型
	 * @throws Exception 参数
	 * @throws
	 * @Title: selectApiMationById
	 * @Description: 通过id查找对应的api接口信息
	 */
	@RequestMapping("/post/ApiMationController/selectApiMationById")
	@ResponseBody
	public void selectApiMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		apiMationService.selectApiMationById(inputObject, outputObject);
	}

	/**
	 * @param inputObject
	 * @param outputObject
	 * @return void    返回类型
	 * @throws Exception 参数
	 * @throws
	 * @Title: editApiMationMationById
	 * @Description: 通过id编辑对应的api接口信息
	 */
	@RequestMapping("/post/ApiMationController/editApiMationById")
	@ResponseBody
	public void editApiMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		apiMationService.editApiMationById(inputObject, outputObject);
	}

}
