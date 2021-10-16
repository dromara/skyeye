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
import com.skyeye.eve.service.SysEveWinService;

@Controller
public class SysEveWinController {
	
	@Autowired
	private SysEveWinService sysEveWinService;
	
	/**
	 * 
	     * @Title: queryWinMationList
	     * @Description: 获取系统信息列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinController/queryWinMationList")
	@ResponseBody
	public void queryWinMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinService.queryWinMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertWinMation
	     * @Description: 新增系统信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinController/insertWinMation")
	@ResponseBody
	public void insertWinMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinService.insertWinMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryWinMationToEditById
	     * @Description: 编辑系统信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinController/queryWinMationToEditById")
	@ResponseBody
	public void queryWinMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinService.queryWinMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editWinMationById
	     * @Description: 编辑系统信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinController/editWinMationById")
	@ResponseBody
	public void editWinMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinService.editWinMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteWinMationById
	     * @Description: 删除系统信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinController/deleteWinMationById")
	@ResponseBody
	public void deleteWinMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinService.deleteWinMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editAuthorizationById
	     * @Description: 进行商户系统授权
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinController/editAuthorizationById")
	@ResponseBody
	public void editAuthorizationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinService.editAuthorizationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editCancleAuthorizationById
	     * @Description: 进行商户系统取消授权
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinController/editCancleAuthorizationById")
	@ResponseBody
	public void editCancleAuthorizationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinService.editCancleAuthorizationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryWinMationListToShow
	     * @Description: 获取应用商店
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinController/queryWinMationListToShow")
	@ResponseBody
	public void queryWinMationListToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinService.queryWinMationListToShow(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertWinMationImportantSynchronization
	     * @Description: 系统重要的同步操作
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinController/insertWinMationImportantSynchronization")
	@ResponseBody
	public void insertWinMationImportantSynchronization(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinService.insertWinMationImportantSynchronization(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryWinMationImportantSynchronizationData
	     * @Description: 系统重要的同步操作获取数据
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinController/queryWinMationImportantSynchronizationData")
	@ResponseBody
	public void queryWinMationImportantSynchronizationData(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinService.queryWinMationImportantSynchronizationData(inputObject, outputObject);
	}
	
}
