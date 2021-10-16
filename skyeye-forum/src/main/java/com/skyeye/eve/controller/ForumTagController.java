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
import com.skyeye.eve.service.ForumTagService;

@Controller
public class ForumTagController {

	@Autowired
	private ForumTagService forumTagService;
	
	/**
	 * 
	     * @Title: queryForumTagList
	     * @Description: 获取论坛标签列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumTagController/queryForumTagList")
	@ResponseBody
	public void queryForumTagList(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumTagService.queryForumTagList(inputObject, outputObject);
	}
	
	
	/**
	 * 
	     * @Title: insertForumTagMation
	     * @Description: 添加论坛标签
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumTagController/insertForumTagMation")
	@ResponseBody
	public void insertForumTagMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumTagService.insertForumTagMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteForumTagById
	     * @Description: 删除论坛标签
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumTagController/deleteForumTagById")
	@ResponseBody
	public void deleteForumTagById(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumTagService.deleteForumTagById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateUpForumTagById
	     * @Description: 上线论坛标签
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumTagController/updateUpForumTagById")
	@ResponseBody
	public void updateUpForumTagById(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumTagService.updateUpForumTagById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: updateDownForumTagById
	     * @Description: 下线论坛标签
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumTagController/updateDownForumTagById")
	@ResponseBody
	public void updateDownForumTagById(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumTagService.updateDownForumTagById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectForumTagById
	     * @Description: 通过id查找对应的论坛标签信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumTagController/selectForumTagById")
	@ResponseBody
	public void selectForumTagById(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumTagService.selectForumTagById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editForumTagMationById
	     * @Description: 通过id编辑对应的论坛标签信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumTagController/editForumTagMationById")
	@ResponseBody
	public void editForumTagMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumTagService.editForumTagMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editForumTagMationOrderNumUpById
	     * @Description: 论坛标签上移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumTagController/editForumTagMationOrderNumUpById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumTagService.editForumTagMationOrderNumUpById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editForumTagMationOrderNumDownById
	     * @Description: 论坛标签下移
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumTagController/editForumTagMationOrderNumDownById")
	@ResponseBody
	public void editSysWinTypeMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumTagService.editForumTagMationOrderNumDownById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryForumTagUpStateList
	     * @Description: 获取已经上线的论坛标签列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumTagController/queryForumTagUpStateList")
	@ResponseBody
	public void queryForumTagUpStateList(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumTagService.queryForumTagUpStateList(inputObject, outputObject);
	}

}
