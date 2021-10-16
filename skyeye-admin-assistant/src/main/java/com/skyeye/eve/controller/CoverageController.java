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
import com.skyeye.eve.service.CoverageService;

@Controller
public class CoverageController {
	
	@Autowired
	private CoverageService coverageService;
	
	/**
	 * 
	     * @Title: selectAllCoverageMation
	     * @Description: 遍历所有的险种
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CoverageController/selectAllCoverageMation")
	@ResponseBody
	public void selectAllCoverageMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		coverageService.selectAllCoverageMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertCoverageMation
	     * @Description: 新增险种
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CoverageController/insertCoverageMation")
	@ResponseBody
	public void insertCoverageMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		coverageService.insertCoverageMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteCoverageById
	     * @Description: 删除险种
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CoverageController/deleteCoverageById")
	@ResponseBody
	public void deleteCoverageById(InputObject inputObject, OutputObject outputObject) throws Exception{
		coverageService.deleteCoverageById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCoverageMationById
	     * @Description: 查询险种信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CoverageController/queryCoverageMationById")
	@ResponseBody
	public void queryCoverageMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		coverageService.queryCoverageMationById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: editCoverageMationById
	     * @Description: 编辑险种
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CoverageController/editCoverageMationById")
	@ResponseBody
	public void editCoverageMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		coverageService.editCoverageMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAllCoverageToChoose
	     * @Description: 查询所有的险种用于复选框
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CoverageController/queryAllCoverageToChoose")
	@ResponseBody
	public void queryAllCoverageToChoose(InputObject inputObject, OutputObject outputObject) throws Exception{
		coverageService.queryAllCoverageToChoose(inputObject, outputObject);
	}
}
