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
import com.skyeye.eve.service.MyNoteService;

@Controller
public class MyNoteController {
	
	@Autowired
	private MyNoteService myNoteService;
	
	/**
	 * 
	     * @Title: queryFileMyNoteByUserId
	     * @Description: 根据当前用户获取笔记文件夹
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MyNoteController/queryFileMyNoteByUserId")
	@ResponseBody
	public void queryFileMyNoteByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		myNoteService.queryFileMyNoteByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertFileMyNoteByUserId
	     * @Description: 添加文件夹
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MyNoteController/insertFileMyNoteByUserId")
	@ResponseBody
	public void insertFileMyNoteByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		myNoteService.insertFileMyNoteByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteFileFolderById
	     * @Description: 删除文件夹以及文件夹下的所有文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MyNoteController/deleteFileFolderById")
	@ResponseBody
	public void deleteFileFolderById(InputObject inputObject, OutputObject outputObject) throws Exception{
		myNoteService.deleteFileFolderById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editFileFolderById
	     * @Description: 编辑文件夹名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MyNoteController/editFileFolderById")
	@ResponseBody
	public void editFileFolderById(InputObject inputObject, OutputObject outputObject) throws Exception{
		myNoteService.editFileFolderById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryMyNoteListNewByUserId
	     * @Description: 根据当前用户获取最新的笔记列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MyNoteController/queryMyNoteListNewByUserId")
	@ResponseBody
	public void queryMyNoteListNewByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		myNoteService.queryMyNoteListNewByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertMyNoteContentByUserId
	     * @Description: 添加笔记
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MyNoteController/insertMyNoteContentByUserId")
	@ResponseBody
	public void insertMyNoteContentByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
		myNoteService.insertMyNoteContentByUserId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryFileAndContentListByFolderId
	     * @Description: 根据文件夹id获取文件夹下的文件夹和笔记列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MyNoteController/queryFileAndContentListByFolderId")
	@ResponseBody
	public void queryFileAndContentListByFolderId(InputObject inputObject, OutputObject outputObject) throws Exception{
		myNoteService.queryFileAndContentListByFolderId(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryMyNoteContentMationById
	     * @Description: 编辑笔记时回显信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MyNoteController/queryMyNoteContentMationById")
	@ResponseBody
	public void queryMyNoteContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		myNoteService.queryMyNoteContentMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editMyNoteContentById
	     * @Description: 编辑笔记信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MyNoteController/editMyNoteContentById")
	@ResponseBody
	public void editMyNoteContentById(InputObject inputObject, OutputObject outputObject) throws Exception{
		myNoteService.editMyNoteContentById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editFileToDragById
	     * @Description: 保存文件夹拖拽后的信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/MyNoteController/editFileToDragById")
	@ResponseBody
	public void editFileToDragById(InputObject inputObject, OutputObject outputObject) throws Exception{
		myNoteService.editFileToDragById(inputObject, outputObject);
	}
	
	/**
     * 
         * @Title: editNoteToMoveById
         * @Description: 保存笔记移动后的信息
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/MyNoteController/editNoteToMoveById")
    @ResponseBody
    public void editNoteToMoveById(InputObject inputObject, OutputObject outputObject) throws Exception{
        myNoteService.editNoteToMoveById(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: queryTreeToMoveByUserId
         * @Description: 获取文件夹或笔记移动时的选择树
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/MyNoteController/queryTreeToMoveByUserId")
    @ResponseBody
    public void queryTreeToMoveByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
        myNoteService.queryTreeToMoveByUserId(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: queryShareNoteById
         * @Description: 根据id获取分享笔记的内容
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/MyNoteController/queryShareNoteById")
    @ResponseBody
    public void queryShareNoteById(InputObject inputObject, OutputObject outputObject) throws Exception{
        myNoteService.queryShareNoteById(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: outputNoteIsZipJob
         * @Description: 根据id(文件夹或者笔记id)将笔记输出为压缩包
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/MyNoteController/outputNoteIsZipJob")
    @ResponseBody
    public void outputNoteIsZipJob(InputObject inputObject, OutputObject outputObject) throws Exception{
        myNoteService.outputNoteIsZipJob(inputObject, outputObject);
    }
	
}
