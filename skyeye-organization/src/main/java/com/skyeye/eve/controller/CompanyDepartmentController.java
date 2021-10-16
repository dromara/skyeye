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
import com.skyeye.eve.service.CompanyDepartmentService;

@Controller
public class CompanyDepartmentController {
	
	@Autowired
	private CompanyDepartmentService companyDepartmentService;
	
	/**
	 * 
	     * @Title: queryCompanyDepartmentList
	     * @Description: 获取公司部门信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyDepartmentController/queryCompanyDepartmentList")
	@ResponseBody
	public void queryCompanyDepartmentList(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyDepartmentService.queryCompanyDepartmentList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertCompanyDepartmentMation
	     * @Description: 添加公司部门信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyDepartmentController/insertCompanyDepartmentMation")
	@ResponseBody
	public void insertCompanyDepartmentMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyDepartmentService.insertCompanyDepartmentMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteCompanyDepartmentMationById
	     * @Description: 删除公司部门信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyDepartmentController/deleteCompanyDepartmentMationById")
	@ResponseBody
	public void deleteCompanyDepartmentMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyDepartmentService.deleteCompanyDepartmentMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCompanyDepartmentMationToEditById
	     * @Description: 编辑公司部门信息信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyDepartmentController/queryCompanyDepartmentMationToEditById")
	@ResponseBody
	public void queryCompanyDepartmentMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyDepartmentService.queryCompanyDepartmentMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editCompanyDepartmentMationById
	     * @Description: 编辑公司部门信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyDepartmentController/editCompanyDepartmentMationById")
	@ResponseBody
	public void editCompanyDepartmentMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyDepartmentService.editCompanyDepartmentMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCompanyDepartmentListTreeByCompanyId
	     * @Description: 获取公司部门信息列表展示为树根据公司id
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyDepartmentController/queryCompanyDepartmentListTreeByCompanyId")
	@ResponseBody
	public void queryCompanyDepartmentListTreeByCompanyId(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyDepartmentService.queryCompanyDepartmentListTreeByCompanyId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCompanyDepartmentListByCompanyIdToSelect
	     * @Description: 根据公司id获取部门列表展示为下拉选择框
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyDepartmentController/queryCompanyDepartmentListByCompanyIdToSelect")
	@ResponseBody
	public void queryCompanyDepartmentListByCompanyIdToSelect(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyDepartmentService.queryCompanyDepartmentListByCompanyIdToSelect(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryCompanyDepartmentListToChoose
	 * @Description: 获取部门列表展示为表格供其他选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/CompanyDepartmentController/queryCompanyDepartmentListToChoose")
	@ResponseBody
	public void queryCompanyDepartmentListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyDepartmentService.queryCompanyDepartmentListToChoose(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryCompanyDepartmentListByIds
	 * @Description: 根据公部门ids获取部门信息列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/CompanyDepartmentController/queryCompanyDepartmentListByIds")
	@ResponseBody
	public void queryCompanyDepartmentListByIds(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyDepartmentService.queryCompanyDepartmentListByIds(inputObject, outputObject);
	}
	
}
