/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.SealSeServiceStatisFormsService;

@Controller
public class SealSeServiceStatisFormsController {
	
	@Autowired
	private SealSeServiceStatisFormsService sealSeServiceStatisFormsService;
	
	/**
    *
    * @Title: queryCustomOrderTable
    * @Description: 获取客户工单数量统计表格
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/SealSeServiceStatisFormsController/queryCustomOrderTable")
	@ResponseBody
	public void queryCustomOrderTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceStatisFormsService.queryCustomOrderTable(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: queryUserWorkerOrderTable
    * @Description: 获取员工工单数量统计表格
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/SealSeServiceStatisFormsController/queryUserWorkerOrderTable")
	@ResponseBody
	public void queryUserWorkerOrderTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceStatisFormsService.queryUserWorkerOrderTable(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: queryWarrantyOrderTable
    * @Description: 获取质保类型工单数量统计表格
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/SealSeServiceStatisFormsController/queryWarrantyOrderTable")
	@ResponseBody
	public void queryWarrantyOrderTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceStatisFormsService.queryWarrantyOrderTable(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: queryProductTypeOrderTable
    * @Description: 获取设备产品类型工单数量统计表格
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/SealSeServiceStatisFormsController/queryProductTypeOrderTable")
	@ResponseBody
	public void queryProductTypeOrderTable(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceStatisFormsService.queryProductTypeOrderTable(inputObject, outputObject);
	}
	
}
