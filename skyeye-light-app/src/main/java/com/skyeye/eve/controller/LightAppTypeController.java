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
import com.skyeye.eve.service.LightAppTypeService;

@Controller
public class LightAppTypeController {

	@Autowired
	private LightAppTypeService lightAppTypeService;
	
	/**
	 * 
	     * @Title: queryLightAppTypeList
	     * @Description: 获取轻应用类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppTypeController/queryLightAppTypeList")
	@ResponseBody
	public void queryLightAppTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppTypeService.queryLightAppTypeList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertLightAppTypeMation
	     * @Description: 新增轻应用类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppTypeController/insertLightAppTypeMation")
	@ResponseBody
	public void insertLightAppTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppTypeService.insertLightAppTypeMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryLightAppTypeMationToEditById
	     * @Description: 编辑轻应用类型时进行信息回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppTypeController/queryLightAppTypeMationToEditById")
	@ResponseBody
	public void queryLightAppTypeMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppTypeService.queryLightAppTypeMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editLightAppTypeMationById
	     * @Description: 编辑轻应用类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppTypeController/editLightAppTypeMationById")
	@ResponseBody
	public void editLightAppTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppTypeService.editLightAppTypeMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editLightAppTypeSortTopById
	     * @Description: 轻应用类型展示顺序上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppTypeController/editLightAppTypeSortTopById")
	@ResponseBody
	public void editLightAppTypeSortTopById(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppTypeService.editLightAppTypeSortTopById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editLightAppTypeSortLowerById
	     * @Description: 轻应用类型展示顺序下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppTypeController/editLightAppTypeSortLowerById")
	@ResponseBody
	public void editLightAppTypeSortLowerById(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppTypeService.editLightAppTypeSortLowerById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteLightAppTypeById
	     * @Description: 删除轻应用类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppTypeController/deleteLightAppTypeById")
	@ResponseBody
	public void deleteLightAppTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppTypeService.deleteLightAppTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editLightAppTypeUpTypeById
	     * @Description: 轻应用类型上线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppTypeController/editLightAppTypeUpTypeById")
	@ResponseBody
	public void editLightAppTypeUpTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppTypeService.editLightAppTypeUpTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editLightAppTypeDownTypeById
	     * @Description: 轻应用类型下线
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppTypeController/editLightAppTypeDownTypeById")
	@ResponseBody
	public void editLightAppTypeDownTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppTypeService.editLightAppTypeDownTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryLightAppTypeUpList
	     * @Description: 获取轻应用上线类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/LightAppTypeController/queryLightAppTypeUpList")
	@ResponseBody
	public void queryLightAppTypeUpList(InputObject inputObject, OutputObject outputObject) throws Exception{
		lightAppTypeService.queryLightAppTypeUpList(inputObject, outputObject);
	}
	
}
