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
import com.skyeye.eve.service.MailListService;

@Controller
public class MailListController {
	
	@Autowired
	private MailListService mailListService;
	
	/**
	 * 
	     * @Title: queryMailMationList
	     * @Description: 获取通讯录列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MailListController/queryMailMationList")
	@ResponseBody
	public void queryMailMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		mailListService.queryMailMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertMailMation
	     * @Description: 新增通讯录(个人或者公共通讯录)
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MailListController/insertMailMation")
	@ResponseBody
	public void insertMailMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		mailListService.insertMailMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteMailMationById
	     * @Description: 删除通讯录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MailListController/deleteMailMationById")
	@ResponseBody
	public void deleteMailMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		mailListService.deleteMailMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryMailMationToEditById
	     * @Description: 编辑通讯录进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MailListController/queryMailMationToEditById")
	@ResponseBody
	public void queryMailMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		mailListService.queryMailMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editMailMationById
	     * @Description: 编辑通讯录
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MailListController/editMailMationById")
	@ResponseBody
	public void editMailMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		mailListService.editMailMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryMailMationDetailsById
	     * @Description: 个人/公共通讯录详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MailListController/queryMailMationDetailsById")
	@ResponseBody
	public void queryMailMationDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		mailListService.queryMailMationDetailsById(inputObject, outputObject);
	}
	
}
