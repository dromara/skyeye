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
import com.skyeye.eve.service.ReportTypeService;

@Controller
public class ReportTypeController {

	@Autowired
	private ReportTypeService reportTypeService;
	
	/**
	 * 
	     * @Title: queryReportTypeList
	     * @Description: 获取举报类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ReportTypeController/queryReportTypeList")
	@ResponseBody
	public void queryReportTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
		reportTypeService.queryReportTypeList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertReportTypeMation
	     * @Description: 新增举报类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ReportTypeController/insertReportTypeMation")
	@ResponseBody
	public void insertReportTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		reportTypeService.insertReportTypeMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryReportTypeMationToEditById
	     * @Description: 编辑举报类型时进行信息回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ReportTypeController/queryReportTypeMationToEditById")
	@ResponseBody
	public void queryReportTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		reportTypeService.queryReportTypeMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editReportTypeMationById
	     * @Description: 编辑举报类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ReportTypeController/editReportTypeMationById")
	@ResponseBody
	public void editReportTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		reportTypeService.editReportTypeMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editReportTypeSortTopById
	     * @Description: 举报类型展示顺序上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ReportTypeController/editReportTypeSortTopById")
	@ResponseBody
	public void editReportTypeSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		reportTypeService.editReportTypeSortTopById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editReportTypeSortLowerById
	     * @Description: 举报类型展示顺序下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ReportTypeController/editReportTypeSortLowerById")
	@ResponseBody
	public void editReportTypeSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception{
		reportTypeService.editReportTypeSortLowerById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteReportTypeById
	     * @Description: 删除举报类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ReportTypeController/deleteReportTypeById")
	@ResponseBody
	public void deleteReportTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		reportTypeService.deleteReportTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editReportTypeUpTypeById
	     * @Description: 举报类型上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ReportTypeController/editReportTypeUpTypeById")
	@ResponseBody
	public void editReportTypeUpTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		reportTypeService.editReportTypeUpTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editReportTypeDownTypeById
	     * @Description: 举报类型下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ReportTypeController/editReportTypeDownTypeById")
	@ResponseBody
	public void editReportTypeDownTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		reportTypeService.editReportTypeDownTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryReportTypeUpList
	     * @Description: 获取举报上线类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ReportTypeController/queryReportTypeUpList")
	@ResponseBody
	public void queryReportTypeUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
		reportTypeService.queryReportTypeUpList(inputObject, outputObject);
	}
	
}
