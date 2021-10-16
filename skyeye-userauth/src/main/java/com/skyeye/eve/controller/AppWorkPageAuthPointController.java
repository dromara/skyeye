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
import com.skyeye.eve.service.AppWorkPageAuthPointService;

@Controller
public class AppWorkPageAuthPointController {
	
	@Autowired
	private AppWorkPageAuthPointService appWorkPageAuthPointService;
	
	/**
	 * 
	     * @Title: queryAppWorkPageAuthPointListByMenuId
	     * @Description: 获取菜单权限点列表根据菜单id
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageAuthPointController/queryAppWorkPageAuthPointListByMenuId")
	@ResponseBody
	public void queryAppWorkPageAuthPointListByMenuId(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageAuthPointService.queryAppWorkPageAuthPointListByMenuId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertAppWorkPageAuthPointMation
	     * @Description: 添加菜单权限点
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageAuthPointController/insertAppWorkPageAuthPointMation")
	@ResponseBody
	public void insertAppWorkPageAuthPointMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageAuthPointService.insertAppWorkPageAuthPointMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAppWorkPageAuthPointMationToEditById
	     * @Description: 编辑菜单权限点时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageAuthPointController/queryAppWorkPageAuthPointMationToEditById")
	@ResponseBody
	public void queryAppWorkPageAuthPointMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageAuthPointService.queryAppWorkPageAuthPointMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editAppWorkPageAuthPointMationById
	     * @Description: 编辑菜单权限点
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageAuthPointController/editAppWorkPageAuthPointMationById")
	@ResponseBody
	public void editAppWorkPageAuthPointMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageAuthPointService.editAppWorkPageAuthPointMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteAppWorkPageAuthPointMationById
	     * @Description: 删除菜单权限点
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/AppWorkPageAuthPointController/deleteAppWorkPageAuthPointMationById")
	@ResponseBody
	public void deleteAppWorkPageAuthPointMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		appWorkPageAuthPointService.deleteAppWorkPageAuthPointMationById(inputObject, outputObject);
	}
	
}
