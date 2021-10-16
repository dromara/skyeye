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
import com.skyeye.eve.service.InspectionService;

@Controller
public class InspectionController {
	
	@Autowired
	private InspectionService inspectionService;
	
	/**
	 * 
	     * @Title: selectAllInspectionMation
	     * @Description: 遍历所有的年检
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/InspectionController/selectAllInspectionMation")
	@ResponseBody
	public void selectAllInspectionMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		inspectionService.selectAllInspectionMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertInspectionMation
	     * @Description: 新增年检
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/InspectionController/insertInspectionMation")
	@ResponseBody
	public void insertInspectionMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		inspectionService.insertInspectionMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteInspectionById
	     * @Description: 删除年检
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/InspectionController/deleteInspectionById")
	@ResponseBody
	public void deleteInspectionById(InputObject inputObject, OutputObject outputObject) throws Exception{
		inspectionService.deleteInspectionById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryInspectionMationById
	     * @Description: 查询年检信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/InspectionController/queryInspectionMationById")
	@ResponseBody
	public void queryInspectionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		inspectionService.queryInspectionMationById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: editInspectionMationById
	     * @Description: 编辑年检
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/InspectionController/editInspectionMationById")
	@ResponseBody
	public void editInspectionMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		inspectionService.editInspectionMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectInspectionDetailsById
	     * @Description: 年检详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/InspectionController/selectInspectionDetailsById")
	@ResponseBody
	public void selectInspectionDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		inspectionService.selectInspectionDetailsById(inputObject, outputObject);
	}
}
