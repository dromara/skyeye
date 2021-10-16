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
import com.skyeye.eve.service.ForumSensitiveWordsService;

@Controller
public class ForumSensitiveWordsController {

	@Autowired
	private ForumSensitiveWordsService forumSensitiveWordsService;
	
	/**
	 * 
	     * @Title: queryForumSensitiveWordsList
	     * @Description: 获取论坛敏感词列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumSensitiveWordsController/queryForumSensitiveWordsList")
	@ResponseBody
	public void queryForumSensitiveWordsList(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumSensitiveWordsService.queryForumSensitiveWordsList(inputObject, outputObject);
	}
	
	
	/**
	 * 
	     * @Title: insertForumSensitiveWordsMation
	     * @Description: 添加论坛敏感词
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumSensitiveWordsController/insertForumSensitiveWordsMation")
	@ResponseBody
	public void insertForumSensitiveWordsMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumSensitiveWordsService.insertForumSensitiveWordsMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteForumSensitiveWordsById
	     * @Description: 删除论坛敏感词
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumSensitiveWordsController/deleteForumSensitiveWordsById")
	@ResponseBody
	public void deleteForumSensitiveWordsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumSensitiveWordsService.deleteForumSensitiveWordsById(inputObject, outputObject);
	}

	/**
	 * 
	     * @Title: selectForumSensitiveWordsById
	     * @Description: 通过id查找对应的论坛敏感词信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumSensitiveWordsController/selectForumSensitiveWordsById")
	@ResponseBody
	public void selectForumSensitiveWordsById(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumSensitiveWordsService.selectForumSensitiveWordsById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editForumSensitiveWordsMationById
	     * @Description: 通过id编辑对应的论坛敏感词信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumSensitiveWordsController/editForumSensitiveWordsMationById")
	@ResponseBody
	public void editForumSensitiveWordsMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumSensitiveWordsService.editForumSensitiveWordsMationById(inputObject, outputObject);
	}

}
