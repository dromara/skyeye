/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.service.SchoolBodyMindService;

@Controller
public class SchoolBodyMindController {

	@Autowired
	private SchoolBodyMindService schoolBodyMindService;
	
	/**
	 * 
	     * @Title: querySchoolBodyMindList
	     * @Description: 获取身心障碍列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolBodyMindController/querySchoolBodyMindList")
	@ResponseBody
	public void querySchoolBodyMindList(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolBodyMindService.querySchoolBodyMindList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSchoolBodyMindMation
	     * @Description: 新增身心障碍
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolBodyMindController/insertSchoolBodyMindMation")
	@ResponseBody
	public void insertSchoolBodyMindMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolBodyMindService.insertSchoolBodyMindMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolBodyMindToEditById
	     * @Description: 通过id查询一条身心障碍信息回显编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolBodyMindController/querySchoolBodyMindToEditById")
	@ResponseBody
	public void querySchoolBodyMindToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolBodyMindService.querySchoolBodyMindToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSchoolBodyMindById
	     * @Description: 编辑身心障碍信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolBodyMindController/editSchoolBodyMindById")
	@ResponseBody
	public void editSchoolBodyMindById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolBodyMindService.editSchoolBodyMindById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSchoolBodyMindById
	     * @Description: 删除身心障碍信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolBodyMindController/deleteSchoolBodyMindById")
	@ResponseBody
	public void deleteSchoolBodyMindById(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolBodyMindService.deleteSchoolBodyMindById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySchoolBodyMindListToShow
	     * @Description: 获取身心障碍信息列表展示为select/chockbox
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SchoolBodyMindController/querySchoolBodyMindListToShow")
	@ResponseBody
	public void querySchoolBodyMindListToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		schoolBodyMindService.querySchoolBodyMindListToShow(inputObject, outputObject);
	}
	
}
