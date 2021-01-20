/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.skyeye.eve.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.CodeModelGroupService;

/**
 * @auther 卫志强 QQ：598748873@qq.com，微信：wzq_598748873
 * @desc 禁止商用
 */
@Controller
public class CodeModelGroupController {
	
	@Autowired
	private CodeModelGroupService codeModelGroupService;
	
	/**
	 * 
	     * @Title: queryCodeModelGroupList
	     * @Description: 获取模板分组列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CodeModelGroupController/queryCodeModelGroupList")
	@ResponseBody
	public void queryCodeModelGroupList(InputObject inputObject, OutputObject outputObject) throws Exception{
		codeModelGroupService.queryCodeModelGroupList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertCodeModelGroupMation
	     * @Description: 新增模板分组列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CodeModelGroupController/insertCodeModelGroupMation")
	@ResponseBody
	public void insertCodeModelGroupMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		codeModelGroupService.insertCodeModelGroupMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteCodeModelGroupById
	     * @Description: 删除模板分组信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CodeModelGroupController/deleteCodeModelGroupById")
	@ResponseBody
	public void deleteCodeModelGroupById(InputObject inputObject, OutputObject outputObject) throws Exception{
		codeModelGroupService.deleteCodeModelGroupById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCodeModelGroupMationToEditById
	     * @Description: 编辑模板分组信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CodeModelGroupController/queryCodeModelGroupMationToEditById")
	@ResponseBody
	public void queryCodeModelGroupMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		codeModelGroupService.queryCodeModelGroupMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editCodeModelGroupMationById
	     * @Description: 编辑模板分组信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CodeModelGroupController/editCodeModelGroupMationById")
	@ResponseBody
	public void editCodeModelGroupMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		codeModelGroupService.editCodeModelGroupMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryTableParameterByTableName
	     * @Description: 根据表名获取表的相关信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CodeModelGroupController/queryTableParameterByTableName")
	@ResponseBody
	public void queryTableParameterByTableName(InputObject inputObject, OutputObject outputObject) throws Exception{
		codeModelGroupService.queryTableParameterByTableName(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryTableMationByTableName
	     * @Description: 根据表名获取表的相关转换信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CodeModelGroupController/queryTableMationByTableName")
	@ResponseBody
	public void queryTableMationByTableName(InputObject inputObject, OutputObject outputObject) throws Exception{
		codeModelGroupService.queryTableMationByTableName(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCodeModelListByGroupId
	     * @Description: 根据分组id获取模板列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CodeModelGroupController/queryCodeModelListByGroupId")
	@ResponseBody
	public void queryCodeModelListByGroupId(InputObject inputObject, OutputObject outputObject) throws Exception{
		codeModelGroupService.queryCodeModelListByGroupId(inputObject, outputObject);
	}
	
}
