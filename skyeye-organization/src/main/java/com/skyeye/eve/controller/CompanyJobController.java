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
import com.skyeye.eve.service.CompanyJobService;

@Controller
public class CompanyJobController {
	
	@Autowired
	private CompanyJobService companyJobService;
	
	/**
	 * 
	     * @Title: queryCompanyJobList
	     * @Description: 获取公司部门职位信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyJobController/queryCompanyJobList")
	@ResponseBody
	public void queryCompanyJobList(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyJobService.queryCompanyJobList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertCompanyJobMation
	     * @Description: 添加公司部门职位信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyJobController/insertCompanyJobMation")
	@ResponseBody
	public void insertCompanyJobMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyJobService.insertCompanyJobMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteCompanyJobMationById
	     * @Description: 删除公司部门职位信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyJobController/deleteCompanyJobMationById")
	@ResponseBody
	public void deleteCompanyJobMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyJobService.deleteCompanyJobMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCompanyJobMationToEditById
	     * @Description: 编辑公司部门职位信息信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyJobController/queryCompanyJobMationToEditById")
	@ResponseBody
	public void queryCompanyJobMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyJobService.queryCompanyJobMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editCompanyJobMationById
	     * @Description: 编辑公司部门职位信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyJobController/editCompanyJobMationById")
	@ResponseBody
	public void editCompanyJobMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyJobService.editCompanyJobMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCompanyJobListTreeByDepartmentId
	     * @Description: 获取公司部门职位信息列表展示为树根据公司id
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyJobController/queryCompanyJobListTreeByDepartmentId")
	@ResponseBody
	public void queryCompanyJobListTreeByDepartmentId(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyJobService.queryCompanyJobListTreeByDepartmentId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCompanyJobListByToSelect
	     * @Description: 根据公司id和部门id获取职位列表展示为下拉选择框
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyJobController/queryCompanyJobListByToSelect")
	@ResponseBody
	public void queryCompanyJobListByToSelect(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyJobService.queryCompanyJobListByToSelect(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCompanyJobSimpleListByToSelect
	     * @Description: 根据部门id获取职位同级列表且不包含当前id的值展示为下拉选择框
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyJobController/queryCompanyJobSimpleListByToSelect")
	@ResponseBody
	public void queryCompanyJobSimpleListByToSelect(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyJobService.queryCompanyJobSimpleListByToSelect(inputObject, outputObject);
	}
	
}
