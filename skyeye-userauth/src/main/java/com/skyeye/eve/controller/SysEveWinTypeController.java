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
import com.skyeye.eve.service.SysEveWinTypeService;

@Controller
public class SysEveWinTypeController {
	
	@Autowired
	private SysEveWinTypeService sysEveWinTypeService;
	
	/**
	 * 
	     * @Title: querySysWinTypeList
	     * @Description: 获取分类列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinTypeController/querySysWinTypeList")
	@ResponseBody
	public void querySysWinTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinTypeService.querySysWinTypeList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysWinFirstTypeList
	     * @Description: 获取所有一级分类展示为下拉选项
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinTypeController/querySysWinFirstTypeList")
	@ResponseBody
	public void querySysWinFirstTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinTypeService.querySysWinFirstTypeList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSysWinTypeMation
	     * @Description: 新增系统分类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinTypeController/insertSysWinTypeMation")
	@ResponseBody
	public void insertSysWinTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinTypeService.insertSysWinTypeMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysWinTypeMationToEditById
	     * @Description: 编辑系统分类时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinTypeController/querySysWinTypeMationToEditById")
	@ResponseBody
	public void querySysWinTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinTypeService.querySysWinTypeMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysWinTypeMationById
	     * @Description: 编辑系统分类时
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinTypeController/editSysWinTypeMationById")
	@ResponseBody
	public void editSysWinTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinTypeService.editSysWinTypeMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysWinFirstTypeListNotIsThisId
	     * @Description: 获取所有不是当前分类的一级分类展示为下拉选项
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinTypeController/querySysWinFirstTypeListNotIsThisId")
	@ResponseBody
	public void querySysWinFirstTypeListNotIsThisId(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinTypeService.querySysWinFirstTypeListNotIsThisId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSysWinTypeMationById
	     * @Description: 删除系统分类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinTypeController/deleteSysWinTypeMationById")
	@ResponseBody
	public void deleteSysWinTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinTypeService.deleteSysWinTypeMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysWinTypeMationOrderNumUpById
	     * @Description: 系统分类上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinTypeController/editSysWinTypeMationOrderNumUpById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinTypeService.editSysWinTypeMationOrderNumUpById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysWinTypeMationOrderNumDownById
	     * @Description: 系统分类下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinTypeController/editSysWinTypeMationOrderNumDownById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinTypeService.editSysWinTypeMationOrderNumDownById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysWinTypeMationStateUpById
	     * @Description: 系统分类上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinTypeController/editSysWinTypeMationStateUpById")
	@ResponseBody
	public void editSysWinTypeMationStateUpById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinTypeService.editSysWinTypeMationStateUpById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSysWinTypeMationStateDownById
	     * @Description: 系统分类下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinTypeController/editSysWinTypeMationStateDownById")
	@ResponseBody
	public void editSysWinTypeMationStateDownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinTypeService.editSysWinTypeMationStateDownById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysWinTypeFirstMationStateIsUp
	     * @Description: 获取已经上线的一级分类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinTypeController/querySysWinTypeFirstMationStateIsUp")
	@ResponseBody
	public void querySysWinTypeFirstMationStateIsUp(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinTypeService.querySysWinTypeFirstMationStateIsUp(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysWinTypeSecondMationStateIsUp
	     * @Description: 获取已经上线的二级分类
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysEveWinTypeController/querySysWinTypeSecondMationStateIsUp")
	@ResponseBody
	public void querySysWinTypeSecondMationStateIsUp(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysEveWinTypeService.querySysWinTypeSecondMationStateIsUp(inputObject, outputObject);
	}
	
}
