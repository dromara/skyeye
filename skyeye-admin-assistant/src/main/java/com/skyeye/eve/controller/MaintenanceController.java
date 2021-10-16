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
import com.skyeye.eve.service.MaintenanceService;

@Controller
public class MaintenanceController {
	
	@Autowired
	private MaintenanceService maintenanceService;
	
	/**
	 * 
	     * @Title: selectAllMaintenanceMation
	     * @Description: 遍历所有的维修保养
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MaintenanceController/selectAllMaintenanceMation")
	@ResponseBody
	public void selectAllMaintenanceMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		maintenanceService.selectAllMaintenanceMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertMaintenanceMation
	     * @Description: 新增维修保养
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MaintenanceController/insertMaintenanceMation")
	@ResponseBody
	public void insertMaintenanceMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		maintenanceService.insertMaintenanceMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteMaintenanceById
	     * @Description: 删除维修保养
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MaintenanceController/deleteMaintenanceById")
	@ResponseBody
	public void deleteMaintenanceById(InputObject inputObject, OutputObject outputObject) throws Exception{
		maintenanceService.deleteMaintenanceById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryMaintenanceMationById
	     * @Description: 查询维修保养信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MaintenanceController/queryMaintenanceMationById")
	@ResponseBody
	public void queryMaintenanceMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		maintenanceService.queryMaintenanceMationById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: editMaintenanceMationById
	     * @Description: 编辑维修保养
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MaintenanceController/editMaintenanceMationById")
	@ResponseBody
	public void editMaintenanceMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		maintenanceService.editMaintenanceMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectMaintenanceDetailsById
	     * @Description: 维修保养详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MaintenanceController/selectMaintenanceDetailsById")
	@ResponseBody
	public void selectMaintenanceDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		maintenanceService.selectMaintenanceDetailsById(inputObject, outputObject);
	}
}
