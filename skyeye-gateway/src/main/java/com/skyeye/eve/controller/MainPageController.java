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
import com.skyeye.eve.service.MainPageService;

@Controller
public class MainPageController {
	
	@Autowired
	private MainPageService mainPageService;
	
	/**
     * 获取本月考勤天数，我的文件数，我的论坛帖数，我的知识库文档数
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MainPageController/queryFourNumListByUserId")
    @ResponseBody
    public void queryFourNumListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
    	mainPageService.queryFourNumListByUserId(inputObject, outputObject);
    }
    
    /**
     * 获取公告类型以及前八条内容
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MainPageController/queryNoticeContentListByUserId")
    @ResponseBody
    public void queryNoticeContentListByUserId(InputObject inputObject, OutputObject outputObject) throws Exception{
    	mainPageService.queryNoticeContentListByUserId(inputObject, outputObject);
    }
    
    /**
     * 获取前八条热门论坛帖
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MainPageController/queryHotForumList")
    @ResponseBody
    public void queryHotForumList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	mainPageService.queryHotForumList(inputObject, outputObject);
    }
    
    /**
     * 获取近期八条已审核的知识库
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @RequestMapping("/post/MainPageController/queryKnowledgeEightList")
    @ResponseBody
    public void queryKnowledgeEightList(InputObject inputObject, OutputObject outputObject) throws Exception{
    	mainPageService.queryKnowledgeEightList(inputObject, outputObject);
    }
	
}
