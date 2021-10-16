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
import com.skyeye.eve.service.RmPropertyValueService;

@Controller
public class RmPropertyValueController {
	
	@Autowired
	private RmPropertyValueService rmPropertyValueService;
	
	/**
	 * 
	     * @Title: queryRmPropertyValueList
	     * @Description: 获取小程序样式属性值列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmPropertyValueController/queryRmPropertyValueList")
	@ResponseBody
	public void queryRmPropertyValueList(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmPropertyValueService.queryRmPropertyValueList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertRmPropertyValueMation
	     * @Description: 添加小程序样式属性值信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmPropertyValueController/insertRmPropertyValueMation")
	@ResponseBody
	public void insertRmPropertyValueMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmPropertyValueService.insertRmPropertyValueMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteRmPropertyValueMationById
	     * @Description: 删除小程序样式属性值信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmPropertyValueController/deleteRmPropertyValueMationById")
	@ResponseBody
	public void deleteRmPropertyValueMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmPropertyValueService.deleteRmPropertyValueMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryRmPropertyValueMationToEditById
	     * @Description: 编辑小程序样式属性值信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmPropertyValueController/queryRmPropertyValueMationToEditById")
	@ResponseBody
	public void queryRmPropertyValueMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmPropertyValueService.queryRmPropertyValueMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmPropertyValueMationById
	     * @Description: 编辑小程序样式属性值信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmPropertyValueController/editRmPropertyValueMationById")
	@ResponseBody
	public void editRmPropertyValueMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmPropertyValueService.editRmPropertyValueMationById(inputObject, outputObject);
	}
	
}
