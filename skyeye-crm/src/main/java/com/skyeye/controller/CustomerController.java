/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	/**
	 *
	 * @Title: queryCustomerList
	 * @Description: 获取客户管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CustomerController/queryCustomerList")
	@ResponseBody
	public void queryCustomerList(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.queryCustomerList(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: insertCustomerMation
	 * @Description: 新增客户信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CustomerController/insertCustomerMation")
	@ResponseBody
	public void insertCustomer(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.insertCustomerMation(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryCustomerMationById
	 * @Description: 根据id获取客户信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CustomerController/queryCustomerMationById")
	@ResponseBody
	public void queryCustomerMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.queryCustomerMationById(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: editCustomerMationById
	 * @Description: 编辑客户信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CustomerController/editCustomerMationById")
	@ResponseBody
	public void editCustomerMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.editCustomerMationById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: deleteCustomerMationById
	 * @Description: 删除客户信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CustomerController/deleteCustomerMationById")
	@ResponseBody
	public void deleteCustomerMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.deleteCustomerMationById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryCustomerMationToDetail
	 * @Description: 根据id获取客户信息详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CustomerController/queryCustomerMationToDetail")
	@ResponseBody
	public void queryCustomerMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.queryCustomerMationToDetail(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryCustomerListToChoose
	 * @Description: 获取客户列表用于下拉框选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CustomerController/queryCustomerListToChoose")
	@ResponseBody
	public void queryCustomerListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.queryCustomerListToChoose(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryCustomerNumsDetail
	 * @Description: 获取客户列表中商机，合同，售后服务，跟单记录，联系人，讨论板列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CustomerController/queryCustomerNumsDetail")
	@ResponseBody
	public void queryCustomerNumsDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.queryCustomerNumsDetail(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryCustomerListTableToChoose
	 * @Description: 获取客户列表供其他内容选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CustomerController/queryCustomerListTableToChoose")
	@ResponseBody
	public void queryCustomerListTableToChoose(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.queryCustomerListTableToChoose(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryMyConscientiousList
	 * @Description: 获取我负责的客户管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CustomerController/queryMyConscientiousList")
	@ResponseBody
	public void queryMyConscientiousList(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.queryMyConscientiousList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryMyCreateList
	 * @Description: 获取我创建的客户管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CustomerController/queryMyCreateList")
	@ResponseBody
	public void queryMyCreateList(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.queryMyCreateList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryInternationalCustomerList
	 * @Description: 获取公海客户群列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/CustomerController/queryInternationalCustomerList")
	@ResponseBody
	public void queryInternationalCustomerList(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.queryInternationalCustomerList(inputObject, outputObject);
	}

}
