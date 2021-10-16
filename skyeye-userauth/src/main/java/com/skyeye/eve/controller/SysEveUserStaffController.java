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
import com.skyeye.eve.service.SysEveUserStaffService;

@Controller
public class SysEveUserStaffController {

	@Autowired
	private SysEveUserStaffService sysEveUserStaffService;
	
	/**
	 * 
	     * @Title: querySysUserStaffList
	     * @Description: 获取员工列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserStaffController/querySysUserStaffList")
	@ResponseBody
	public void querySysUserStaffList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserStaffService.querySysUserStaffList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSysUserStaffMation
	     * @Description: 新增员工
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserStaffController/insertSysUserStaffMation")
	@ResponseBody
	public void insertSysUserStaffMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserStaffService.insertSysUserStaffMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysUserStaffById
	     * @Description: 通过id查询一条员工信息回显编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserStaffController/querySysUserStaffById")
	@ResponseBody
	public void querySysUserStaffById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserStaffService.querySysUserStaffById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysUserStaffById
	     * @Description: 编辑员工信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserStaffController/editSysUserStaffById")
	@ResponseBody
	public void editSysUserStaffById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserStaffService.editSysUserStaffById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysUserStaffByIdToDetails
	     * @Description: 通过id查询一条员工信息展示详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserStaffController/querySysUserStaffByIdToDetails")
	@ResponseBody
	public void querySysUserStaffByIdToDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserStaffService.querySysUserStaffByIdToDetails(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysUserStaffState
	     * @Description: 员工离职
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserStaffController/editSysUserStaffState")
	@ResponseBody
	public void editSysUserStaffState(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserStaffService.editSysUserStaffState(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editTurnTeacher
	     * @Description: 普通员工转教职工
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserStaffController/editTurnTeacher")
	@ResponseBody
	public void editTurnTeacher(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserStaffService.editTurnTeacher(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysUserStaffListToTable
	     * @Description: 查看所有员工列表展示为表格供其他选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveUserStaffController/querySysUserStaffListToTable")
	@ResponseBody
	public void querySysUserStaffListToTable(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserStaffService.querySysUserStaffListToTable(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: querySysUserStaffListByIds
	 * @Description: 根据员工ids获取员工信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/SysEveUserStaffController/querySysUserStaffListByIds")
	@ResponseBody
	public void querySysUserStaffListByIds(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserStaffService.querySysUserStaffListByIds(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: querySysUserStaffLogin
	 * @Description: 获取当前登录员工的信息
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/SysEveUserStaffController/querySysUserStaffLogin")
	@ResponseBody
	public void querySysUserStaffLogin(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveUserStaffService.querySysUserStaffLogin(inputObject, outputObject);
	}

}
