/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.service.KnowledgePointsMationService;

@Controller
public class KnowledgePointsMationController {
	
	@Autowired
	private KnowledgePointsMationService knowledgePointsMationService;
	
	/**
	 * 
	     * @Title: queryKnowledgePointsMationList
	     * @Description: 获取知识点列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgePointsMationController/queryKnowledgePointsMationList")
	@ResponseBody
	public void queryKnowledgePointsMationList(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgePointsMationService.queryKnowledgePointsMationList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertKnowledgePointsMation
	     * @Description: 添加知识点
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgePointsMationController/insertKnowledgePointsMation")
	@ResponseBody
	public void insertKnowledgePointsMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgePointsMationService.insertKnowledgePointsMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteKnowledgePointsMationById
	     * @Description: 删除知识点信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgePointsMationController/deleteKnowledgePointsMationById")
	@ResponseBody
	public void deleteKnowledgePointsMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgePointsMationService.deleteKnowledgePointsMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryKnowledgePointsMationToEditById
	     * @Description: 编辑知识点信息时进行回显
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgePointsMationController/queryKnowledgePointsMationToEditById")
	@ResponseBody
	public void queryKnowledgePointsMationToEditById(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgePointsMationService.queryKnowledgePointsMationToEditById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editKnowledgePointsMationById
	     * @Description: 编辑知识点信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgePointsMationController/editKnowledgePointsMationById")
	@ResponseBody
	public void editKnowledgePointsMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgePointsMationService.editKnowledgePointsMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryKnowledgePointsMationById
	     * @Description: 知识点详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgePointsMationController/queryKnowledgePointsMationById")
	@ResponseBody
	public void queryKnowledgePointsMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgePointsMationService.queryKnowledgePointsMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryKnowledgePointsMationListToTable
	     * @Description: 获取知识点列表展示为表格供其他选择
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgePointsMationController/queryKnowledgePointsMationListToTable")
	@ResponseBody
	public void queryKnowledgePointsMationListToTable(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgePointsMationService.queryKnowledgePointsMationListToTable(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryKnowledgePointsMationListByIds
	     * @Description: 根据知识点id串获取知识点列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgePointsMationController/queryKnowledgePointsMationListByIds")
	@ResponseBody
	public void queryKnowledgePointsMationListByIds(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgePointsMationService.queryKnowledgePointsMationListByIds(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryKnowledgePointsMationBankList
	     * @Description: 获取知识点库列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgePointsMationController/queryKnowledgePointsMationBankList")
	@ResponseBody
	public void queryKnowledgePointsMationBankList(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgePointsMationService.queryKnowledgePointsMationBankList(inputObject, outputObject);
	}
	
}
