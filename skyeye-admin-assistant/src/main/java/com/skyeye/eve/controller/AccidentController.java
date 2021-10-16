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
import com.skyeye.eve.service.AccidentService;

@Controller
public class AccidentController {
	
	@Autowired
	private AccidentService accidentService;
	
	/**
	 * 
	     * @Title: selectAllAccidentMation
	     * @Description: 遍历所有的事故
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AccidentController/selectAllAccidentMation")
	@ResponseBody
	public void selectAllAccidentMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		accidentService.selectAllAccidentMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertAccidentMation
	     * @Description: 新增事故
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AccidentController/insertAccidentMation")
	@ResponseBody
	public void insertAccidentMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		accidentService.insertAccidentMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteAccidentById
	     * @Description: 删除事故
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AccidentController/deleteAccidentById")
	@ResponseBody
	public void deleteAccidentById(InputObject inputObject, OutputObject outputObject) throws Exception{
		accidentService.deleteAccidentById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAccidentMationById
	     * @Description: 查询事故信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AccidentController/queryAccidentMationById")
	@ResponseBody
	public void queryAccidentMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		accidentService.queryAccidentMationById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: editAccidentMationById
	     * @Description: 编辑事故
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AccidentController/editAccidentMationById")
	@ResponseBody
	public void editAccidentMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		accidentService.editAccidentMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectAccidentDetailsById
	     * @Description: 事故详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AccidentController/selectAccidentDetailsById")
	@ResponseBody
	public void selectAccidentDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		accidentService.selectAccidentDetailsById(inputObject, outputObject);
	}
}
