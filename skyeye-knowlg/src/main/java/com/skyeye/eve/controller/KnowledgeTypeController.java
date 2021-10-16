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
import com.skyeye.eve.service.KnowledgeTypeService;


@Controller
public class KnowledgeTypeController {
	
	@Autowired
	private KnowledgeTypeService knowledgeTypeService;
	
	/**
	 * 
	     * @Title: queryKnowledgeTypeList
	     * @Description: 获取知识库类型列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgeTypeController/queryKnowledgeTypeList")
	@ResponseBody
	public void queryKnowledgeTypeList(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgeTypeService.queryKnowledgeTypeList(inputObject, outputObject);
	}
	
	
	/**
	 * 
	     * @Title: insertKnowledgeTypeMation
	     * @Description: 添加知识库类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgeTypeController/insertKnowledgeTypeMation")
	@ResponseBody
	public void insertKnowledgeTypeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgeTypeService.insertKnowledgeTypeMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteKnowledgeTypeById
	     * @Description: 删除知识库类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgeTypeController/deleteKnowledgeTypeById")
	@ResponseBody
	public void deleteKnowledgeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgeTypeService.deleteKnowledgeTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateUpKnowledgeTypeById
	     * @Description: 上线知识库类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgeTypeController/updateUpKnowledgeTypeById")
	@ResponseBody
	public void updateUpKnowledgeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgeTypeService.updateUpKnowledgeTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateDownKnowledgeTypeById
	     * @Description: 下线知识库类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgeTypeController/updateDownKnowledgeTypeById")
	@ResponseBody
	public void updateDownKnowledgeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgeTypeService.updateDownKnowledgeTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectKnowledgeTypeById
	     * @Description: 通过id查找对应的知识库类型信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgeTypeController/selectKnowledgeTypeById")
	@ResponseBody
	public void selectKnowledgeTypeById(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgeTypeService.selectKnowledgeTypeById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editKnowledgeTypeMationById
	     * @Description: 编辑知识库类型
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/KnowledgeTypeController/editKnowledgeTypeMationById")
	@ResponseBody
	public void editKnowledgeTypeMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgeTypeService.editKnowledgeTypeMationById(inputObject, outputObject);
	}

	/**
	 *
	 * @Title: queryUpKnowledgeTypeTreeMation
	 * @Description: 获取已经上线的知识库类型，数据为tree格式
	 * @param inputObject
	 * @param outputObject
	 * @throws Exception    参数
	 * @return void    返回类型
	 * @throws
	 */
	@RequestMapping("/post/KnowledgeTypeController/queryUpKnowledgeTypeTreeMation")
	@ResponseBody
	public void queryUpKnowledgeTypeTreeMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		knowledgeTypeService.queryUpKnowledgeTypeTreeMation(inputObject, outputObject);
	}
	
}
