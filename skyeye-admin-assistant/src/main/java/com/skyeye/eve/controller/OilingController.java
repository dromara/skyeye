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
import com.skyeye.eve.service.OilingService;

@Controller
public class OilingController {
	
	@Autowired
	private OilingService oilingService;
	
	/**
	 * 
	     * @Title: selectAllOilingMation
	     * @Description: 遍历所有的加油
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/OilingController/selectAllOilingMation")
	@ResponseBody
	public void selectAllOilingMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		oilingService.selectAllOilingMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertOilingMation
	     * @Description: 新增加油
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/OilingController/insertOilingMation")
	@ResponseBody
	public void insertOilingMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		oilingService.insertOilingMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteOilingById
	     * @Description: 删除加油
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/OilingController/deleteOilingById")
	@ResponseBody
	public void deleteOilingById(InputObject inputObject, OutputObject outputObject) throws Exception{
		oilingService.deleteOilingById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryOilingMationById
	     * @Description: 查询加油信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/OilingController/queryOilingMationById")
	@ResponseBody
	public void queryOilingMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		oilingService.queryOilingMationById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: editOilingMationById
	     * @Description: 编辑加油
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/OilingController/editOilingMationById")
	@ResponseBody
	public void editOilingMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		oilingService.editOilingMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectOilingDetailsById
	     * @Description: 加油详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/OilingController/selectOilingDetailsById")
	@ResponseBody
	public void selectOilingDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		oilingService.selectOilingDetailsById(inputObject, outputObject);
	}
}
