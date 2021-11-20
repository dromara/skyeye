/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SysEveModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SysEveModelController {

	@Autowired
	private SysEveModelService sysEveModelService;
	
	/**
	 * 
	     * @Title: querySysEveModelList
	     * @Description: 获取系统编辑器模板表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveModelController/querySysEveModelList")
	@ResponseBody
	public void querySysEveModelList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveModelService.querySysEveModelList(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: insertSysEveModelMation
	     * @Description: 新增系统编辑器模板
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveModelController/insertSysEveModelMation")
	@ResponseBody
	public void insertSysEveModelMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveModelService.insertSysEveModelMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysEveModelById
	     * @Description: 删除系统编辑器模板
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveModelController/deleteSysEveModelById")
	@ResponseBody
	public void deleteSysEveModelById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveModelService.deleteSysEveModelById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: selectSysEveModelById
	     * @Description: 通过id查找对应的系统编辑器模板
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveModelController/selectSysEveModelById")
	@ResponseBody
	public void selectSysEveModelById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveModelService.selectSysEveModelById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysEveModelMationById
	     * @Description: 通过id编辑对应的系统编辑器模板
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveModelController/editSysEveModelMationById")
	@ResponseBody
	public void editSysEveModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveModelService.editSysEveModelMationById(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: selectSysEveModelMationById
	 * @Description: 通过id查找对应的系统编辑器模板详情
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/SysEveModelController/selectSysEveModelMationById")
	@ResponseBody
	public void selectSysEveModelMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveModelService.selectSysEveModelMationById(inputObject, outputObject);
	}
	
}
