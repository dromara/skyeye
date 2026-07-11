/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chat.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.chat.enums.TalkChatType;
import com.skyeye.chat.service.TalkChatHistoryService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TalkChatHistoryController
 * @Description: 聊天历史记录控制器
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/12 14:26
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "聊天历史记录", tags = "聊天历史记录", modelName = "聊天历史记录")
public class TalkChatHistoryController {

    @Autowired
    private TalkChatHistoryService talkChatHistoryService;

    @ApiOperation(id = "queryMyUnReadMessageList", value = "查询我的未读消息列表", method = "GET", allUse = "2")
    @RequestMapping("/post/TalkChatHistoryController/queryMyUnReadMessageList")
    public void queryMyUnReadMessageList(InputObject inputObject, OutputObject outputObject) {
        talkChatHistoryService.queryMyUnReadMessageList(inputObject, outputObject);
    }

    @ApiOperation(id = "editTalkChatHistoryToRead", value = "修改我与另一个用户/群聊的聊天记录为已读", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "sendId", name = "sendId", value = "单聊为对方用户id，群聊为群组id", required = "required"),
        @ApiImplicitParam(id = "chatType", name = "chatType", value = "消息类型", enumClass = TalkChatType.class)})
    @RequestMapping("/post/TalkChatHistoryController/editTalkChatHistoryToRead")
    public void editTalkChatHistoryToRead(InputObject inputObject, OutputObject outputObject) {
        talkChatHistoryService.editTalkChatHistoryToRead(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyTalkMessageList", value = "查询我的最近的聊天消息列表", method = "GET", allUse = "2")
    @RequestMapping("/post/TalkChatHistoryController/queryMyTalkMessageList")
    public void queryMyTalkMessageList(InputObject inputObject, OutputObject outputObject) {
        talkChatHistoryService.queryMyTalkMessageList(inputObject, outputObject);
    }
}
