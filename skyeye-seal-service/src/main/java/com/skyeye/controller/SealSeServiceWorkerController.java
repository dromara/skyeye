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
import com.skyeye.service.SealSeServiceWorkerService;

@Controller
public class SealSeServiceWorkerController {
	
	@Autowired
	private SealSeServiceWorkerService sealSeServiceWorkerService;
	
	/**
     *
     * @Title: queryServiceWorkerList
     * @Description: 获取所有工人信息
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
	@RequestMapping("/post/SealSeServiceWorkerController/queryServiceWorkerList")
	@ResponseBody
	public void queryServiceWorkerList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceWorkerService.queryServiceWorkerList(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: insertServiceWorkerMation
    * @Description: 新增工人资料信息
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/SealSeServiceWorkerController/insertServiceWorkerMation")
	@ResponseBody
	public void insertServiceWorkerMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceWorkerService.insertServiceWorkerMation(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: deleteServiceWorkerMationById
    * @Description: 删除工人资料信息
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/SealSeServiceWorkerController/deleteServiceWorkerMationById")
	@ResponseBody
	public void deleteServiceWorkerMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceWorkerService.deleteServiceWorkerMationById(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: queryServiceWorkerMationToEditById
    * @Description: 编辑工人资料信息时进行回显
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/SealSeServiceWorkerController/queryServiceWorkerMationToEditById")
	@ResponseBody
	public void queryServiceWorkerMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceWorkerService.queryServiceWorkerMationToEditById(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: editServiceWorkerMationById
    * @Description: 编辑工人资料信息
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/SealSeServiceWorkerController/editServiceWorkerMationById")
	@ResponseBody
	public void editServiceWorkerMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceWorkerService.editServiceWorkerMationById(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: queryServiceWorkerShowList
    * @Description: 获取所有工人信息供展示选择
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/SealSeServiceWorkerController/queryServiceWorkerShowList")
	@ResponseBody
	public void queryServiceWorkerShowList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceWorkerService.queryServiceWorkerShowList(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: queryServiceWorkerToMapList
    * @Description: 获取所有工人信息信息分布图
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/SealSeServiceWorkerController/queryServiceWorkerToMapList")
	@ResponseBody
	public void queryServiceWorkerToMapList(InputObject inputObject, OutputObject outputObject) throws Exception {
		sealSeServiceWorkerService.queryServiceWorkerToMapList(inputObject, outputObject);
	}
	
}
