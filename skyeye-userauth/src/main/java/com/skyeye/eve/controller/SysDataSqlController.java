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
import com.skyeye.eve.service.SysDataSqlService;

@Controller
public class SysDataSqlController {
	
	@Autowired
	private SysDataSqlService sysDataSqlService;
	
	/**
	 * 
	     * @Title: querySysDataSqlBackupsList
	     * @Description: 获取历史备份列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDataSqlController/querySysDataSqlBackupsList")
	@ResponseBody
	public void querySysDataSqlBackupsList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDataSqlService.querySysDataSqlBackupsList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryAllTableMationList
	     * @Description: 获取所有表的列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDataSqlController/queryAllTableMationList")
	@ResponseBody
	public void queryAllTableMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDataSqlService.queryAllTableMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertTableBackUps
	     * @Description: 开始备份
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDataSqlController/insertTableBackUps")
	@ResponseBody
	public void insertTableBackUps(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDataSqlService.insertTableBackUps(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertTableReduction
	     * @Description: 开始还原
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysDataSqlController/insertTableReduction")
	@ResponseBody
	public void insertTableReduction(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysDataSqlService.insertTableReduction(inputObject, outputObject);
	}
	
}
