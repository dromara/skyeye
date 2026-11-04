/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.notice.service.NoticeDispatchHistoryService;
import com.skyeye.eve.rest.notice.NoticeDispatchChannelLogUpdateBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息通知派发历史
 */
@RestController
@Api(value = "消息通知派发历史", tags = "消息通知派发历史", modelName = "消息通知")
public class NoticeDispatchHistoryController {

    @Autowired
    private NoticeDispatchHistoryService noticeDispatchHistoryService;

    @ApiOperation(id = "queryNoticeDispatchHistoryList", value = "分页查询消息通知派发历史", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/NoticeDispatchHistoryController/queryNoticeDispatchHistoryList")
    public void queryNoticeDispatchHistoryList(InputObject inputObject, OutputObject outputObject) {
        noticeDispatchHistoryService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryNoticeDispatchHistoryById", value = "查询消息通知派发历史详情（含渠道明细）", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/NoticeDispatchHistoryController/queryNoticeDispatchHistoryById")
    public void queryNoticeDispatchHistoryById(InputObject inputObject, OutputObject outputObject) {
        noticeDispatchHistoryService.queryHistoryDetail(inputObject, outputObject);
    }

    @ApiOperation(id = "updateNoticeDispatchChannelLog", value = "回写渠道通知结果---供其他微服务调用", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = NoticeDispatchChannelLogUpdateBody.class)
    @RequestMapping("/post/NoticeDispatchHistoryController/updateNoticeDispatchChannelLog")
    public void updateNoticeDispatchChannelLog(InputObject inputObject, OutputObject outputObject) {
        noticeDispatchHistoryService.updateChannelLogFromRemote(inputObject, outputObject);
    }

}
