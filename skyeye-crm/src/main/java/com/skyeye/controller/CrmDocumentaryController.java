/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.CrmDocumentaryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CrmDocumentaryController {

	@Autowired
	private CrmDocumentaryService customerService;

	/**
	 *
	 * @Title: queryMyDocumentaryList
	 * @Description: 获取我的跟单管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/DocumentaryController/queryMyDocumentaryList")
	@ResponseBody
	public void queryMyDocumentaryList(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.queryMyDocumentaryList(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryAllDocumentaryList
	 * @Description: 获取所有跟单管理列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/DocumentaryController/queryAllDocumentaryList")
	@ResponseBody
	public void queryAllDocumentaryList(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.queryAllDocumentaryList(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: insertDocumentary
	 * @Description: 新增跟单信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/DocumentaryController/insertDocumentary")
	@ResponseBody
	public void insertDocumentary(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.insertDocumentary(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryDocumentaryMationById
	 * @Description: 根据id获取跟单信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/DocumentaryController/queryDocumentaryMationById")
	@ResponseBody
	public void queryDocumentaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.queryDocumentaryMationById(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: editDocumentaryMationById
	 * @Description: 编辑跟单信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/DocumentaryController/editDocumentaryMationById")
	@ResponseBody
	public void editDocumentaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.editDocumentaryMationById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: deleteDocumentaryMationById
	 * @Description: 删除跟单信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/DocumentaryController/deleteDocumentaryMationById")
	@ResponseBody
	public void deleteDocumentaryMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.deleteDocumentaryMationById(inputObject, outputObject);
	}
	
	/**
	 *
	 * @Title: queryDocumentaryMationToDetail
	 * @Description: 根据id获取跟单信息详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception 参数
	 * @return void 返回类型
	 * @throws
	 */
	@RequestMapping("/post/DocumentaryController/queryDocumentaryMationToDetail")
	@ResponseBody
	public void queryDocumentaryMationToDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
		customerService.queryDocumentaryMationToDetail(inputObject, outputObject);
	}
	
}
