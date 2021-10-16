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
import com.skyeye.eve.service.InsuranceService;

@Controller
public class InsuranceController {
	
	@Autowired
	private InsuranceService insuranceService;
	
	/**
	 * 
	     * @Title: selectAllInsuranceMation
	     * @Description: 遍历所有的保险
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/InsuranceController/selectAllInsuranceMation")
	@ResponseBody
	public void selectAllInsuranceMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		insuranceService.selectAllInsuranceMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertInsuranceMation
	     * @Description: 新增保险
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/InsuranceController/insertInsuranceMation")
	@ResponseBody
	public void insertInsuranceMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		insuranceService.insertInsuranceMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteInsuranceById
	     * @Description: 删除保险
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/InsuranceController/deleteInsuranceById")
	@ResponseBody
	public void deleteInsuranceById(InputObject inputObject, OutputObject outputObject) throws Exception{
		insuranceService.deleteInsuranceById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryInsuranceMationById
	     * @Description: 查询保险信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/InsuranceController/queryInsuranceMationById")
	@ResponseBody
	public void queryInsuranceMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		insuranceService.queryInsuranceMationById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: editInsuranceMationById
	     * @Description: 编辑保险
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/InsuranceController/editInsuranceMationById")
	@ResponseBody
	public void editInsuranceMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		insuranceService.editInsuranceMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectInsuranceDetailsById
	     * @Description: 保险详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/InsuranceController/selectInsuranceDetailsById")
	@ResponseBody
	public void selectInsuranceDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		insuranceService.selectInsuranceDetailsById(inputObject, outputObject);
	}
}
