package com.skyeye.company.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.skyeye.company.service.CompanyDepartmentService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Controller
public class CompanyDepartmentController {
	
	@Autowired
	private CompanyDepartmentService companyDepartmentService;
	
	/**
	 * 
	     * @Title: queryCompanyDepartmentList
	     * @Description: 获取公司部门信息列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
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
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyDepartmentController/queryCompanyDepartmentListTreeByCompanyId")
	@ResponseBody
	public void queryCompanyDepartmentListTreeByCompanyId(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyDepartmentService.queryCompanyDepartmentListTreeByCompanyId(inputObject, outputObject);
	}
	
}
