package com.skyeye.chat.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.chat.service.ChatService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ChatController
 * @Description: 聊天记录控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/10/5 17:24
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "聊天记录管理", tags = "聊天记录管理", modelName = "聊天记录管理")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @ApiOperation(id = "sendChatMessage", value = "发送消息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "content", name = "content", value = "消息", required = "required"),
        @ApiImplicitParam(id = "apiKeyId", name = "apiKeyId", value = "AI配置id", required = "required")})
    @RequestMapping("/post/ChatController/sendChatMessage")
    public void sendChatMessage(InputObject inputObject, OutputObject outputObject) {
        chatService.sendChatMessage(inputObject, outputObject);
    }

    @ApiOperation(id = "syncChatCompletion", value = "流式调用大模型", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "content", name = "content", value = "用户消息（有的地方会基于原话进行拼接再发给大模型）", required = "required"),
        @ApiImplicitParam(id = "roleId", name = "roleId", value = "AI角色id，优先按角色取唯一启用配置"),
        @ApiImplicitParam(id = "apiKeyId", name = "apiKeyId", value = "AI配置id，roleId为空时使用"),
        @ApiImplicitParam(id = "bizType", name = "bizType", value = "业务类型，如 demandDraft、bugDraft、chat", defaultValue = "demandDraft"),
        @ApiImplicitParam(id = "saveChat", name = "saveChat", value = "是否记录聊天，1是 0否，默认是。需求/Bug草稿传0", enumClass = WhetherEnum.class),
        @ApiImplicitParam(id = "images", name = "images", value = "截图地址列表，看图提 Bug 时传入"),
        @ApiImplicitParam(id = "knowledgeQuery", name = "knowledgeQuery", value = "知识库检索词；闲聊绑知识库时优先用此字段检索，不传则用 content"),
        @ApiImplicitParam(id = "userMessage", name = "userMessage", value = "用户原话。落库和历史展示用；不传则把 content 当作用户消息")})
    @RequestMapping("/post/ChatController/syncChatCompletion")
    public void syncChatCompletion(InputObject inputObject, OutputObject outputObject) {
        chatService.syncChatCompletion(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPageMessageList", value = "分页查询聊天记录", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ChatController/queryPageMessageList")
    public void queryPageMessageList(InputObject inputObject, OutputObject outputObject) {
        chatService.queryPageMessageList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteChatMessageByIds", value = "批量删除聊天记录", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id列表，多个id用逗号分隔", required = "required")})
    @RequestMapping("/post/ChatController/deleteChatMessageByIds")
    public void deleteChatMessageByIds(InputObject inputObject, OutputObject outputObject) {
        chatService.deleteByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAllByApiKeyId", value = "根据apiKeyId一键删除记录", method = "POST", allUse = "2")
    @ApiImplicitParams(
        @ApiImplicitParam(id = "apiKeyId", name = "apiKeyId", value = "ai配置id", required = "required"))
    @RequestMapping("/post/ChatController/deleteAllByApiKeyId")
    public void deleteAllByApiKeyId(InputObject inputObject, OutputObject outputObject) {
        chatService.deleteAllByApiKeyId(inputObject, outputObject);
    }
}
