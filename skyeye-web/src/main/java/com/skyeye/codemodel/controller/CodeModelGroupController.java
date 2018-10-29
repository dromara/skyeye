package com.skyeye.codemodel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.codemodel.service.CodeModelGroupService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

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
	
}
