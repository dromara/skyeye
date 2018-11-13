package com.skyeye.smprogram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.skyeye.smprogram.service.RmPropertyService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

@Controller
public class RmPropertyController {
	
	@Autowired
	private RmPropertyService rmPropertyService;
	
	/**
	 * 
	     * @Title: queryRmPropertyList
	     * @Description: 获取小程序样式属性列表
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmPropertyController/queryRmPropertyList")
	@ResponseBody
	public void queryRmPropertyList(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmPropertyService.queryRmPropertyList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertRmPropertyMation
	     * @Description: 添加小程序样式属性信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmPropertyController/insertRmPropertyMation")
	@ResponseBody
	public void insertRmPropertyMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmPropertyService.insertRmPropertyMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteRmPropertyMationById
	     * @Description: 删除小程序样式属性信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmPropertyController/deleteRmPropertyMationById")
	@ResponseBody
	public void deleteRmPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmPropertyService.deleteRmPropertyMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryRmPropertyMationToEditById
	     * @Description: 编辑小程序样式属性信息时进行回显
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmPropertyController/queryRmPropertyMationToEditById")
	@ResponseBody
	public void queryRmPropertyMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmPropertyService.queryRmPropertyMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editRmPropertyMationById
	     * @Description: 编辑小程序样式属性信息
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmPropertyController/editRmPropertyMationById")
	@ResponseBody
	public void editRmPropertyMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmPropertyService.editRmPropertyMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryRmPropertyListToShow
	     * @Description: 获取小程序样式属性供展示
	     * @param @param inputObject
	     * @param @param outputObject
	     * @param @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/RmPropertyController/queryRmPropertyListToShow")
	@ResponseBody
	public void queryRmPropertyListToShow(InputObject inputObject, OutputObject outputObject) throws Exception{
		rmPropertyService.queryRmPropertyListToShow(inputObject, outputObject);
	}
	
}
