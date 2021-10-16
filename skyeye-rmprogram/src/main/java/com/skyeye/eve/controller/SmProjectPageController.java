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
import com.skyeye.eve.service.SmProjectPageService;

@Controller
public class SmProjectPageController {
	
	@Autowired
	private SmProjectPageService smProjectPageService;
	
	/**
	 * 
	     * @Title: queryProPageMationByProIdList
	     * @Description: 根据项目获取项目内部的页面
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectPageController/queryProPageMationByProIdList")
	@ResponseBody
	public void queryProPageMationByProIdList(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectPageService.queryProPageMationByProIdList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertProPageMationByProId
	     * @Description: 添加项目内部的页面
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectPageController/insertProPageMationByProId")
	@ResponseBody
	public void insertProPageMationByProId(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectPageService.insertProPageMationByProId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSmProjectPageSortTopById
	     * @Description: 小程序页面展示顺序上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectPageController/editSmProjectPageSortTopById")
	@ResponseBody
	public void editSmProjectPageSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectPageService.editSmProjectPageSortTopById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSmProjectPageSortLowerById
	     * @Description: 小程序页面展示顺序下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectPageController/editSmProjectPageSortLowerById")
	@ResponseBody
	public void editSmProjectPageSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectPageService.editSmProjectPageSortLowerById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySmProjectPageMationToEditById
	     * @Description: 编辑小程序页面信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectPageController/querySmProjectPageMationToEditById")
	@ResponseBody
	public void querySmProjectPageMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectPageService.querySmProjectPageMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editSmProjectPageMationById
	     * @Description: 编辑小程序页面信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectPageController/editSmProjectPageMationById")
	@ResponseBody
	public void editSmProjectPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectPageService.editSmProjectPageMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteSmProjectPageMationById
	     * @Description: 删除小程序页面信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SmProjectPageController/deleteSmProjectPageMationById")
	@ResponseBody
	public void deleteSmProjectPageMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		smProjectPageService.deleteSmProjectPageMationById(inputObject, outputObject);
	}
	
	
}
