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
import com.skyeye.service.ProWorkloadService;

@Controller
public class ProWorkloadController {

    @Autowired
    private ProWorkloadService proWorkloadService;

    /**
     *
     * @Title: queryProWorkloadList
     * @Description: 获取我的项目工作量列表
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @RequestMapping("/post/ProWorkloadController/queryProWorkloadList")
    @ResponseBody
    public void queryProWorkloadList(InputObject inputObject, OutputObject outputObject) throws Exception {
    	proWorkloadService.queryProWorkloadList(inputObject, outputObject);
    }
    
	/**
    *
    * @Title: insertProWorkloadMation
    * @Description: 新增项目工作量
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProWorkloadController/insertProWorkloadMation")
	@ResponseBody
	public void insertProWorkloadMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		proWorkloadService.insertProWorkloadMation(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: queryAllProWorkloadList
    * @Description: 获取所有的项目工作量列表
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProWorkloadController/queryAllProWorkloadList")
	@ResponseBody
	public void queryAllProWorkloadList(InputObject inputObject, OutputObject outputObject) throws Exception {
		proWorkloadService.queryAllProWorkloadList(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: editProWorkloadToApprovalById
    * @Description: 提交审批
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProWorkloadController/editProWorkloadToApprovalById")
	@ResponseBody
	public void editProWorkloadToApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception {
		proWorkloadService.editProWorkloadToApprovalById(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: editProWorkloadProcessToRevoke
    * @Description: 撤销工作量审批申请
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProWorkloadController/editProWorkloadProcessToRevoke")
	@ResponseBody
	public void editProWorkloadProcessToRevoke(InputObject inputObject, OutputObject outputObject) throws Exception {
		proWorkloadService.editProWorkloadProcessToRevoke(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: queryProWorkloadMationToDetails
    * @Description: 工作量详情
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProWorkloadController/queryProWorkloadMationToDetails")
	@ResponseBody
	public void queryProWorkloadMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception {
		proWorkloadService.queryProWorkloadMationToDetails(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: queryProWorkloadMationToEdit
    * @Description: 获取工作量信息用以编辑
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProWorkloadController/queryProWorkloadMationToEdit")
	@ResponseBody
	public void queryProWorkloadMationToEdit(InputObject inputObject, OutputObject outputObject) throws Exception {
		proWorkloadService.queryProWorkloadMationToEdit(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: editProWorkloadMation
    * @Description: 编辑工作量信息
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProWorkloadController/editProWorkloadMation")
	@ResponseBody
	public void editProWorkloadMation(InputObject inputObject, OutputObject outputObject) throws Exception {
		proWorkloadService.editProWorkloadMation(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: deleteProWorkloadMationById
    * @Description: 删除工作量
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProWorkloadController/deleteProWorkloadMationById")
	@ResponseBody
	public void deleteProWorkloadMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		proWorkloadService.deleteProWorkloadMationById(inputObject, outputObject);
	}
	
	/**
    *
    * @Title: updateProWorkloadToCancellation
    * @Description: 作废工作量
    * @param inputObject
    * @param outputObject
    * @throws Exception    参数
    * @return void    返回类型
    * @throws
    */
	@RequestMapping("/post/ProWorkloadController/updateProWorkloadToCancellation")
	@ResponseBody
	public void updateProWorkloadToCancellation(InputObject inputObject, OutputObject outputObject) throws Exception {
		proWorkloadService.updateProWorkloadToCancellation(inputObject, outputObject);
	}

}
