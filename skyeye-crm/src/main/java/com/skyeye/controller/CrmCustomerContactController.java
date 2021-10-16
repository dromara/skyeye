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
import com.skyeye.service.CrmCustomerContactService;

@Controller
public class CrmCustomerContactController {
	
	@Autowired
	private CrmCustomerContactService crmCustomerContactService;
	
	/**
	 *
	 * @Title: queryCustomerContactList
	 * @Description: 获取联系人列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmCustomerContactController/queryCustomerContactList")
	@ResponseBody
	public void queryCustomerContactList(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmCustomerContactService.queryCustomerContactList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: insertCustomerContactMation
	 * @Description: 新增联系人信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmCustomerContactController/insertCustomerContactMation")
	@ResponseBody
	public void insertCustomerContactMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmCustomerContactService.insertCustomerContactMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryCustomerContactMationToEditById
	 * @Description: 编辑联系人信息时进行回显
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmCustomerContactController/queryCustomerContactMationToEditById")
	@ResponseBody
	public void queryCustomerContactMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmCustomerContactService.queryCustomerContactMationToEditById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: editCustomerContactMation
	 * @Description: 编辑联系人信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmCustomerContactController/editCustomerContactMation")
	@ResponseBody
	public void editCustomerContactMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmCustomerContactService.editCustomerContactMation(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: deleteCustomerContactMationById
	 * @Description: 删除联系人信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CrmCustomerContactController/deleteCustomerContactMationById")
	@ResponseBody
	public void deleteCustomerContactMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		crmCustomerContactService.deleteCustomerContactMationById(inputObject, outputObject);
	}
	
}
