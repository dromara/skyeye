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
import com.skyeye.eve.service.SysTAreaService;

@Controller
public class SysTAreaController {
	
	@Autowired
	private SysTAreaService sysTAreaService;
	
	/**
	 * 
	     * @Title: querySysTAreaList
	     * @Description: 获取行政区划信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysTAreaController/querySysTAreaList")
	@ResponseBody
	public void querySysTAreaList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysTAreaService.querySysTAreaList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysTAreaProvinceList
	     * @Description: 获取一级省行政区划信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysTAreaController/querySysTAreaProvinceList")
	@ResponseBody
	public void querySysTAreaProvinceList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysTAreaService.querySysTAreaProvinceList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysTAreaCityList
	     * @Description: 获取二级市行政区划信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysTAreaController/querySysTAreaCityList")
	@ResponseBody
	public void querySysTAreaCityList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysTAreaService.querySysTAreaCityList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysTAreaChildAreaList
	     * @Description: 获取三级县行政区划信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysTAreaController/querySysTAreaChildAreaList")
	@ResponseBody
	public void querySysTAreaChildAreaList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysTAreaService.querySysTAreaChildAreaList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: querySysTAreaTownShipList
	     * @Description: 获取四级镇行政区划信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/SysTAreaController/querySysTAreaTownShipList")
	@ResponseBody
	public void querySysTAreaTownShipList(InputObject inputObject, OutputObject outputObject) throws Exception{
		sysTAreaService.querySysTAreaTownShipList(inputObject, outputObject);
	}
	
}
