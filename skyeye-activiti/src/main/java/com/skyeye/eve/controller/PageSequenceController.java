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
import com.skyeye.eve.service.PageSequenceService;

@Controller
public class PageSequenceController {
	
	@Autowired
	private PageSequenceService pageSequenceService;
	
	/**
	 * 
	     * @Title: queryDsFormISDraftListByUser
	     * @Description: 获取所有草稿状态的动态表单提交项
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PageSequenceController/queryDsFormISDraftListByUser")
	@ResponseBody
	public void queryDsFormISDraftListByUser(InputObject inputObject, OutputObject outputObject) throws Exception{
		pageSequenceService.queryDsFormISDraftListByUser(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteDsFormISDraftByUser
	     * @Description: 删除草稿状态的动态表单提交项
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PageSequenceController/deleteDsFormISDraftByUser")
	@ResponseBody
	public void deleteDsFormISDraftListByUser(InputObject inputObject, OutputObject outputObject) throws Exception{
		pageSequenceService.deleteDsFormISDraftByUser(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryDsFormISDraftToEditById
	     * @Description: 编辑动态表单时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PageSequenceController/queryDsFormISDraftToEditById")
	@ResponseBody
	public void queryDsFormISDraftToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		pageSequenceService.queryDsFormISDraftToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editDsFormISDraftById
	     * @Description: 编辑动态表单(无工作流)
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PageSequenceController/editDsFormISDraftById")
	@ResponseBody
	public void editDsFormISDraftById(InputObject inputObject, OutputObject outputObject) throws Exception{
		pageSequenceService.editDsFormISDraftById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editDsFormISDraftToSubApprovalById
	     * @Description: 提交审批
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PageSequenceController/editDsFormISDraftToSubApprovalById")
	@ResponseBody
	public void editDsFormISDraftToSubApprovalById(InputObject inputObject, OutputObject outputObject) throws Exception{
		pageSequenceService.editDsFormISDraftToSubApprovalById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryDsFormISDraftDetailsById
	     * @Description: 表单详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/PageSequenceController/queryDsFormISDraftDetailsById")
	@ResponseBody
	public void queryDsFormISDraftDetailsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		pageSequenceService.queryDsFormISDraftDetailsById(inputObject, outputObject);
	}
	
}
