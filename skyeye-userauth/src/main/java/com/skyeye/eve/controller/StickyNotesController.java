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
import com.skyeye.eve.service.StickyNotesService;

@Controller
public class StickyNotesController {
	
	@Autowired
	private StickyNotesService stickyNotesService;
	
	/**
	 * 
	     * @Title: insertStickyNotesMation
	     * @Description: 新增便签
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/StickyNotesController/insertStickyNotesMation")
	@ResponseBody
	public void insertStickyNotesMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		stickyNotesService.insertStickyNotesMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: selectStickyNotesMation
	     * @Description: 查询便签
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/StickyNotesController/selectStickyNotesMation")
	@ResponseBody
	public void selectStickyNotesMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		stickyNotesService.selectStickyNotesMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editStickyNotesMation
	     * @Description: 编辑便签
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/StickyNotesController/editStickyNotesMation")
	@ResponseBody
	public void editStickyNotesMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		stickyNotesService.editStickyNotesMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteStickyNotesMation
	     * @Description: 删除便签
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/StickyNotesController/deleteStickyNotesMation")
	@ResponseBody
	public void deleteStickyNotesMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		stickyNotesService.deleteStickyNotesMation(inputObject, outputObject);
	}

}
