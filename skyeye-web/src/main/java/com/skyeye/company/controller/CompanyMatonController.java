package com.skyeye.company.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.skyeye.company.service.CompanyMatonService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Controller
public class CompanyMatonController {
	
	@Autowired
	private CompanyMatonService companyMatonService;
	
	/**
	 * 
	     * @Title: queryCompanyMatonList
	     * @Description: 获取公司信息列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyMatonController/queryCompanyMatonList")
	@ResponseBody
	public void queryCompanyMatonList(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMatonService.queryCompanyMatonList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertCompanyMatonMation
	     * @Description: 添加公司信息信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyMatonController/insertCompanyMatonMation")
	@ResponseBody
	public void insertCompanyMatonMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMatonService.insertCompanyMatonMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteCompanyMatonMationById
	     * @Description: 删除公司信息信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyMatonController/deleteCompanyMatonMationById")
	@ResponseBody
	public void deleteCompanyMatonMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMatonService.deleteCompanyMatonMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCompanyMatonMationToEditById
	     * @Description: 编辑公司信息信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyMatonController/queryCompanyMatonMationToEditById")
	@ResponseBody
	public void queryCompanyMatonMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMatonService.queryCompanyMatonMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editCompanyMatonMationById
	     * @Description: 编辑公司信息信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CompanyMatonController/editCompanyMatonMationById")
	@ResponseBody
	public void editCompanyMatonMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		companyMatonService.editCompanyMatonMationById(inputObject, outputObject);
	}
	
}
