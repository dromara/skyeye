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
import com.skyeye.eve.service.ActBaseClassService;

@Controller
public class ActBaseClassController {
	
	@Autowired
	private ActBaseClassService actBaseClassService;
	
	/**
	 * 
	     * @Title: queryActBaseClassList
	     * @Description: 获取配置类列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActBaseClassController/queryActBaseClassList")
	@ResponseBody
	public void queryActBaseClassList(InputObject inputObject, OutputObject outputObject) throws Exception{
		actBaseClassService.queryActBaseClassList(inputObject, outputObject);
	}
	
	
	/**
	 * 
	     * @Title: insertActBaseClassMation
	     * @Description: 添加配置类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActBaseClassController/insertActBaseClassMation")
	@ResponseBody
	public void insertActBaseClassMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		actBaseClassService.insertActBaseClassMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteActBaseClassById
	     * @Description: 删除配置类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActBaseClassController/deleteActBaseClassById")
	@ResponseBody
	public void deleteActBaseClassById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actBaseClassService.deleteActBaseClassById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: selectActBaseClassById
	     * @Description: 通过id查找对应的配置类信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActBaseClassController/selectActBaseClassById")
	@ResponseBody
	public void selectActBaseClassById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actBaseClassService.selectActBaseClassById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editActBaseClassMationById
	     * @Description: 编辑配置类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActBaseClassController/editActBaseClassMationById")
	@ResponseBody
	public void editActBaseClassMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		actBaseClassService.editActBaseClassMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectActBaseClassByIdToDedails
	     * @Description: 配置类详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ActBaseClassController/selectActBaseClassByIdToDedails")
	@ResponseBody
	public void selectActBaseClassByIdToDedails(InputObject inputObject, OutputObject outputObject) throws Exception{
		actBaseClassService.selectActBaseClassByIdToDedails(inputObject, outputObject);
	}

}
