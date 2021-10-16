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
import com.skyeye.eve.service.CheckWorkReasonService;

@Controller
public class CheckWorkReasonController {

	@Autowired
	private CheckWorkReasonService checkWorkReasonService;
	
	/**
	 * 
	     * @Title: queryCheckWorkReasonList
	     * @Description: 获取申诉原因名称列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkReasonController/queryCheckWorkReasonList")
	@ResponseBody
	public void queryCheckWorkReasonList(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkReasonService.queryCheckWorkReasonList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertCheckWorkReasonMation
	     * @Description: 添加申诉原因名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkReasonController/insertCheckWorkReasonMation")
	@ResponseBody
	public void insertSysPicTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkReasonService.insertCheckWorkReasonMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteCheckWorkReasonById
	     * @Description: 删除申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkReasonController/deleteCheckWorkReasonById")
	@ResponseBody
	public void deleteCheckWorkReasonById(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkReasonService.deleteCheckWorkReasonById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateUpCheckWorkReasonById
	     * @Description: 上线申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkReasonController/updateUpCheckWorkReasonById")
	@ResponseBody
	public void updateUpCheckWorkReasonById(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkReasonService.updateUpCheckWorkReasonById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateDownCheckWorkReasonById
	     * @Description: 下线申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkReasonController/updateDownCheckWorkReasonById")
	@ResponseBody
	public void updateDownCheckWorkReasonById(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkReasonService.updateDownCheckWorkReasonById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectCheckWorkReasonById
	     * @Description: 通过id查找对应的申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkReasonController/selectCheckWorkReasonById")
	@ResponseBody
	public void selectCheckWorkReasonById(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkReasonService.selectCheckWorkReasonById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editCheckWorkReasonMationById
	     * @Description: 通过id编辑对应的申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkReasonController/editCheckWorkReasonMationById")
	@ResponseBody
	public void editCheckWorkReasonMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkReasonService.editCheckWorkReasonMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCheckWorkReasonUpMationById
	     * @Description: 上移申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkReasonController/queryCheckWorkReasonUpMationById")
	@ResponseBody
	public void queryCheckWorkReasonUpMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkReasonService.queryCheckWorkReasonUpMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCheckWorkReasonDownMationById
	     * @Description: 下移申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkReasonController/queryCheckWorkReasonDownMationById")
	@ResponseBody
	public void queryCheckWorkReasonDownMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkReasonService.queryCheckWorkReasonDownMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryCheckWorkReasonUpStateList
	     * @Description: 获取已经上线的申诉原因
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/CheckWorkReasonController/queryCheckWorkReasonUpStateList")
	@ResponseBody
	public void querySysPicTypeUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception{
		checkWorkReasonService.queryCheckWorkReasonUpStateList(inputObject, outputObject);
	}
	
}
