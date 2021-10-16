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
import com.skyeye.eve.service.LightAppService;

@Controller
public class LightAppController {

	@Autowired
	private LightAppService lightAppService;
	
	/**
	 * 
	     * @Title: queryLightAppList
	     * @Description: 获取轻应用列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppController/queryLightAppList")
	@ResponseBody
	public void queryLightAppList(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppService.queryLightAppList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertLightAppMation
	     * @Description: 新增轻应用
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppController/insertLightAppMation")
	@ResponseBody
	public void insertLightAppMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppService.insertLightAppMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryLightAppMationToEditById
	     * @Description: 编辑轻应用时进行信息回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppController/queryLightAppMationToEditById")
	@ResponseBody
	public void queryLightAppMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppService.queryLightAppMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editLightAppMationById
	     * @Description: 编辑轻应用信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppController/editLightAppMationById")
	@ResponseBody
	public void editLightAppMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppService.editLightAppMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteLightAppById
	     * @Description: 删除轻应用
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppController/deleteLightAppById")
	@ResponseBody
	public void deleteLightAppById(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppService.deleteLightAppById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editLightAppUpById
	     * @Description: 轻应用上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppController/editLightAppUpById")
	@ResponseBody
	public void editLightAppUpTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppService.editLightAppUpById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editLightAppDownById
	     * @Description: 轻应用下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppController/editLightAppDownById")
	@ResponseBody
	public void editLightAppDownTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppService.editLightAppDownById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryLightAppUpList
	     * @Description: 获取轻应用上线列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppController/queryLightAppUpList")
	@ResponseBody
	public void queryLightAppUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppService.queryLightAppUpList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertLightAppToWin
	     * @Description: 添加轻应用到桌面
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppController/insertLightAppToWin")
	@ResponseBody
	public void insertLightAppToWin(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppService.insertLightAppToWin(inputObject, outputObject);
	}
	
}
