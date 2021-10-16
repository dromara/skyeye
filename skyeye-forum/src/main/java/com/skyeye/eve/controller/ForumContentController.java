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
import com.skyeye.eve.service.ForumContentService;

@Controller
public class ForumContentController {

	@Autowired
	private ForumContentService forumContentService;
	
	/**
	 * 
	     * @Title: queryMyForumContentList
	     * @Description: 获取我的帖子列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumContentController/queryMyForumContentList")
	@ResponseBody
	public void queryMyForumContentList(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumContentService.queryMyForumContentList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertForumContentMation
	     * @Description: 新增我的帖子
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumContentController/insertForumContentMation")
	@ResponseBody
	public void insertForumContentMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumContentService.insertForumContentMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: deleteForumContentById
	     * @Description: 删除帖子
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumContentController/deleteForumContentById")
	@ResponseBody
	public void deleteForumContentById(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumContentService.deleteForumContentById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryForumContentMationById
	     * @Description: 查询帖子信息用以编辑
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumContentController/queryForumContentMationById")
	@ResponseBody
	public void queryForumContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumContentService.queryForumContentMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: editForumContentMationById
	     * @Description: 编辑帖子信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumContentController/editForumContentMationById")
	@ResponseBody
	public void editForumContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumContentService.editForumContentMationById(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryForumContentMationToDetails
	     * @Description: 帖子详情
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumContentController/queryForumContentMationToDetails")
	@ResponseBody
	public void queryForumContentMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumContentService.queryForumContentMationToDetails(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryNewForumContentList
	     * @Description: 获取最新帖子
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumContentController/queryNewForumContentList")
	@ResponseBody
	public void queryNewForumContentList(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumContentService.queryNewForumContentList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertForumCommentMation
	     * @Description: 新增帖子评论
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumContentController/insertForumCommentMation")
	@ResponseBody
	public void insertForumCommentMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumContentService.insertForumCommentMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryForumCommentList
	     * @Description: 获取帖子评论信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumContentController/queryForumCommentList")
	@ResponseBody
	public void queryForumCommentList(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumContentService.queryForumCommentList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: insertForumReplyMation
	     * @Description: 新增帖子评论回复
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumContentController/insertForumReplyMation")
	@ResponseBody
	public void insertForumReplyMation(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumContentService.insertForumReplyMation(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryForumReplyList
	     * @Description: 获取帖子评论回复信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumContentController/queryForumReplyList")
	@ResponseBody
	public void queryForumReplyList(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumContentService.queryForumReplyList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryForumMyBrowerList
	     * @Description: 获取我的浏览信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumContentController/queryForumMyBrowerList")
	@ResponseBody
	public void queryForumMyBrowerList(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumContentService.queryForumMyBrowerList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryNewCommentList
	     * @Description: 获取最新评论
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumContentController/queryNewCommentList")
	@ResponseBody
	public void queryNewCommentList(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumContentService.queryNewCommentList(inputObject, outputObject);
	}
	
	/**
	 * 
	     * @Title: queryForumListByTagId
	     * @Description: 根据标签id获取帖子列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@RequestMapping("/post/ForumContentController/queryForumListByTagId")
	@ResponseBody
	public void queryForumListByTagId(InputObject inputObject, OutputObject outputObject) throws Exception{
		forumContentService.queryForumListByTagId(inputObject, outputObject);
	}
	
	/**
     * 
         * @Title: queryHotTagList
         * @Description: 获取热门标签
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/ForumContentController/queryHotTagList")
    @ResponseBody
    public void queryHotTagList(InputObject inputObject, OutputObject outputObject) throws Exception{
        forumContentService.queryHotTagList(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: queryActiveUsersList
         * @Description: 获取活跃用户
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/ForumContentController/queryActiveUsersList")
    @ResponseBody
    public void queryActiveUsersList(InputObject inputObject, OutputObject outputObject) throws Exception{
        forumContentService.queryActiveUsersList(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: queryHotForumList
         * @Description: 获取热门贴
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/ForumContentController/queryHotForumList")
    @ResponseBody
    public void queryHotForumList(InputObject inputObject, OutputObject outputObject) throws Exception{
        forumContentService.queryHotForumList(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: querySearchForumList
         * @Description: 获取用户搜索的帖子
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/ForumContentController/querySearchForumList")
    @ResponseBody
    public void querySearchForumList(InputObject inputObject, OutputObject outputObject) throws Exception{
        forumContentService.querySearchForumList(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: querySolrSynchronousTime
         * @Description: 获取solr上次同步数据的时间
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/ForumContentController/querySolrSynchronousTime")
    @ResponseBody
    public void querySolrSynchronousTime(InputObject inputObject, OutputObject outputObject) throws Exception{
        forumContentService.querySolrSynchronousTime(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: updateSolrSynchronousData
         * @Description: solr同步数据
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/ForumContentController/updateSolrSynchronousData")
    @ResponseBody
    public void updateSolrSynchronousData(InputObject inputObject, OutputObject outputObject) throws Exception{
        forumContentService.updateSolrSynchronousData(inputObject, outputObject);
    }
    
    /**
     * 
         * @Title: queryMyCommentList
         * @Description: 获取我的帖子列表
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/ForumContentController/queryMyCommentList")
    @ResponseBody
    public void queryMyCommentList(InputObject inputObject, OutputObject outputObject) throws Exception{
        forumContentService.queryMyCommentList(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: deleteCommentById
         * @Description: 根据评论id删除评论
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/ForumContentController/deleteCommentById")
    @ResponseBody
    public void deleteCommentById(InputObject inputObject, OutputObject outputObject) throws Exception{
        forumContentService.deleteCommentById(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: queryMyNoticeList
         * @Description: 获取我的通知列表
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/ForumContentController/queryMyNoticeList")
    @ResponseBody
    public void queryMyNoticeList(InputObject inputObject, OutputObject outputObject) throws Exception{
        forumContentService.queryMyNoticeList(inputObject, outputObject);
    }
	
    /**
     * 
         * @Title: deleteNoticeById
         * @Description: 根据通知id删除通知
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @RequestMapping("/post/ForumContentController/deleteNoticeById")
    @ResponseBody
    public void deleteNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception{
        forumContentService.deleteNoticeById(inputObject, outputObject);
    }

}
