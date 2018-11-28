package com.skyeye.company.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.skyeye.company.service.CompanyMationService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Controller
public class CompanyMationController {
	
	@Autowired
	private CompanyMationService companyMationService;
	
	/**
	 * 
	     * @Title: queryCompanyMationList
	     * @Description: 获取公司信息列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyMationController/queryCompanyMationListTree")
	@ResponseBody
	public void queryCompanyMationListTree(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMationService.queryCompanyMationListTree(inputObject, outputObject);
	}
	
}
