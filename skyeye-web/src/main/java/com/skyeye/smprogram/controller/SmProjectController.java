package com.skyeye.smprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.smprogram.service.SmProjectService;

@Controller
public class SmProjectController {
	
	@Autowired
	private SmProjectService smProjectService;
	
	/**
	 * 
	     * @Title: querySmProjectList
	     * @Description: 获取小程序列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectController/querySmProjectList")
	@ResponseBody
	public void querySmProjectList(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectService.querySmProjectList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertSmProjectMation
	     * @Description: 新增小程序
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectController/insertSmProjectMation")
	@ResponseBody
	public void insertSmProjectMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectService.insertSmProjectMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSmProjectById
	     * @Description: 删除小程序
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectController/deleteSmProjectById")
	@ResponseBody
	public void deleteSmProjectById(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectService.deleteSmProjectById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySmProjectMationToEditById
	     * @Description: 编辑小程序信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectController/querySmProjectMationToEditById")
	@ResponseBody
	public void querySmProjectMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectService.querySmProjectMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSmProjectMationById
	     * @Description: 编辑小程序信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectController/editSmProjectMationById")
	@ResponseBody
	public void editSmProjectMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectService.editSmProjectMationById(inputObject, outputObject);
	}
	
}
