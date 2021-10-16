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
import com.skyeye.eve.service.CompanyMationService;

@Controller
public class CompanyMationController {
	
	@Autowired
	private CompanyMationService companyMationService;
	
	/**
	 * 
	     * @Title: queryCompanyMationList
	     * @Description: 获取公司信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyMationController/queryCompanyMationList")
	@ResponseBody
	public void queryCompanyMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMationService.queryCompanyMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertCompanyMation
	     * @Description: 添加公司信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyMationController/insertCompanyMation")
	@ResponseBody
	public void insertCompanyMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMationService.insertCompanyMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteCompanyMationById
	     * @Description: 删除公司信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyMationController/deleteCompanyMationById")
	@ResponseBody
	public void deleteCompanyMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMationService.deleteCompanyMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCompanyMationToEditById
	     * @Description: 编辑公司信息信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyMationController/queryCompanyMationToEditById")
	@ResponseBody
	public void queryCompanyMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMationService.queryCompanyMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editCompanyMationById
	     * @Description: 编辑公司信息信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyMationController/editCompanyMationById")
	@ResponseBody
	public void editCompanyMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMationService.editCompanyMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryOverAllCompanyMationList
	     * @Description: 获取总公司信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyMationController/queryOverAllCompanyMationList")
	@ResponseBody
	public void queryOverAllCompanyMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMationService.queryOverAllCompanyMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCompanyMationListTree
	     * @Description: 获取公司信息列表展示为树
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyMationController/queryCompanyMationListTree")
	@ResponseBody
	public void queryCompanyMationListTree(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMationService.queryCompanyMationListTree(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCompanyListToSelect
	     * @Description: 获取公司列表展示为下拉选择框
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyMationController/queryCompanyListToSelect")
	@ResponseBody
	public void queryCompanyListToSelect(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMationService.queryCompanyListToSelect(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryCompanyOrganization
	 * @Description: 获取企业组织机构图
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/CompanyMationController/queryCompanyOrganization")
	@ResponseBody
	public void queryCompanyOrganization(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMationService.queryCompanyOrganization(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryCompanyMationListToChoose
	 * @Description: 获取公司信息列表展示为表格供其他需要选择
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/CompanyMationController/queryCompanyMationListToChoose")
	@ResponseBody
	public void queryCompanyMationListToChoose(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMationService.queryCompanyMationListToChoose(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryCompanyMationListByIds
	 * @Description: 根据公司ids获取公司信息列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/CompanyMationController/queryCompanyMationListByIds")
	@ResponseBody
	public void queryCompanyMationListByIds(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMationService.queryCompanyMationListByIds(inputObject, outputObject);
	}
	
}
