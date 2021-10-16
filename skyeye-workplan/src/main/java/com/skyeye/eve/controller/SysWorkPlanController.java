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
import com.skyeye.eve.service.SysWorkPlanService;

@Controller
public class SysWorkPlanController {
	
	@Autowired
	private SysWorkPlanService sysWorkPlanService;
	
	/**
	 * 
	     * @Title: querySysWorkPlanList
	     * @Description: 获取计划列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/querySysWorkPlanList")
	@ResponseBody
	public void querySysWorkPlanList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.querySysWorkPlanList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSysWorkPlanISPeople
	     * @Description: 新增个人计划
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/insertSysWorkPlanISPeople")
	@ResponseBody
	public void insertSysWorkPlanISPeople(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.insertSysWorkPlanISPeople(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSysWorkPlanISDepartMent
	     * @Description: 新增部门计划
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/insertSysWorkPlanISDepartMent")
	@ResponseBody
	public void insertSysWorkPlanISDepartMent(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.insertSysWorkPlanISDepartMent(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSysWorkPlanISCompany
	     * @Description: 新增公司计划
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/insertSysWorkPlanISCompany")
	@ResponseBody
	public void insertSysWorkPlanISCompany(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.insertSysWorkPlanISCompany(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysWorkPlanTimingById
	     * @Description: 取消定时发送
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/deleteSysWorkPlanTimingById")
	@ResponseBody
	public void deleteSysWorkPlanTimingById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.deleteSysWorkPlanTimingById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysWorkPlanById
	     * @Description: 删除计划
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/deleteSysWorkPlanById")
	@ResponseBody
	public void deleteSysWorkPlanById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.deleteSysWorkPlanById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysWorkPlanToEditById
	     * @Description: 编辑计划时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/querySysWorkPlanToEditById")
	@ResponseBody
	public void querySysWorkPlanToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.querySysWorkPlanToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysWorkPlanISPeople
	     * @Description: 编辑个人计划
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/editSysWorkPlanISPeople")
	@ResponseBody
	public void editSysWorkPlanISPeople(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.editSysWorkPlanISPeople(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysWorkPlanISDepartMentOrCompany
	     * @Description: 编辑部门计划或者公司计划
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/editSysWorkPlanISDepartMentOrCompany")
	@ResponseBody
	public void editSysWorkPlanISDepartMentOrCompany(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.editSysWorkPlanISDepartMentOrCompany(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysWorkPlanTimingSend
	     * @Description: 定时发送
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/editSysWorkPlanTimingSend")
	@ResponseBody
	public void editSysWorkPlanTimingSend(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.editSysWorkPlanTimingSend(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysWorkPlanDetailsById
	     * @Description: 计划详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/querySysWorkPlanDetailsById")
	@ResponseBody
	public void querySysWorkPlanDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.querySysWorkPlanDetailsById(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryMySysWorkPlanListByUserId
	 * @Description: 获取我的任务计划列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/queryMySysWorkPlanListByUserId")
	@ResponseBody
	public void queryMySysWorkPlanListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.queryMySysWorkPlanListByUserId(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: subEditWorkPlanStateById
	 * @Description: 计划状态变更
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/subEditWorkPlanStateById")
	@ResponseBody
	public void subEditWorkPlanStateById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.subEditWorkPlanStateById(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryMyCreateSysWorkPlanList
	 * @Description: 获取我创建的任务计划列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/queryMyCreateSysWorkPlanList")
	@ResponseBody
	public void queryMyCreateSysWorkPlanList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.queryMyCreateSysWorkPlanList(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryAllSysWorkPlanList
	 * @Description: 获取所有任务计划列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/queryAllSysWorkPlanList")
	@ResponseBody
	public void queryAllSysWorkPlanList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.queryAllSysWorkPlanList(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryMyBranchSysWorkPlanList
	 * @Description: 获取我的下属的任务计划列表
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/SysWorkPlanController/queryMyBranchSysWorkPlanList")
	@ResponseBody
	public void queryMyBranchSysWorkPlanList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysWorkPlanService.queryMyBranchSysWorkPlanList(inputObject, outputObject);
	}
	
}
