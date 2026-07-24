/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.forum.entity.ForumComment;
import com.skyeye.eve.forum.service.ForumCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ForumCommentController
 * @Description: 论坛评论管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "论坛评论管理", tags = "论坛评论管理", modelName = "论坛评论管理")
public class ForumCommentController {

    @Autowired
    private ForumCommentService forumContentService;

    @ApiOperation(id = "insertForumCommentMation", value = "新增帖子评论", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ForumComment.class)
    @RequestMapping("/post/ForumCommentController/insertForumCommentMation")
    public void insertForumCommentMation(InputObject inputObject, OutputObject outputObject) {
        forumContentService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryForumCommentList", value = "获取帖子评论信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "帖子id", required = "required")})
    @RequestMapping("/post/ForumCommentController/queryForumCommentList")
    public void queryForumCommentList(InputObject inputObject, OutputObject outputObject) {
        forumContentService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyForumCommentList", value = "获取我的评论信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ForumCommentController/queryMyForumCommentList")
    public void queryMyForumCommentList(InputObject inputObject, OutputObject outputObject) {
        forumContentService.queryMyForumCommentList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteCommentById", value = "根据评论id删除评论", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ForumCommentController/deleteCommentById")
    public void deleteCommentById(InputObject inputObject, OutputObject outputObject) {
        forumContentService.deleteById(inputObject, outputObject);
    }
}
