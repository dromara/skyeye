/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.chat.enums.TalkChatType;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroup;
import com.skyeye.eve.service.CompanyTalkGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CompanyTalkGroupController
 * @Description: 群组管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/28 15:40
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "群组管理", tags = "群组管理", modelName = "聊天模块")
public class CompanyTalkGroupController {

    @Autowired
    private CompanyTalkGroupService companyTalkGroupService;

    @ApiOperation(id = "companytalkgroup001", value = "添加群组信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CompanyTalkGroup.class)
    @RequestMapping("/post/CompanyTalkGroupController/insertGroupMation")
    public void insertGroupMation(InputObject inputObject, OutputObject outputObject) {
        companyTalkGroupService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "companytalkgroup005", value = "搜索群组列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CompanyTalkGroupController/queryGroupMationList")
    public void queryGroupMationList(InputObject inputObject, OutputObject outputObject) {
        companyTalkGroupService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "companytalkgroup006", value = "申请加入群聊", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "groupId", name = "groupId", value = "群组id", required = "required")})
    @RequestMapping("/post/CompanyTalkGroupController/insertGroupMationToTalk")
    public void insertGroupMationToTalk(InputObject inputObject, OutputObject outputObject) {
        companyTalkGroupService.insertGroupMationToTalk(inputObject, outputObject);
    }

    @ApiOperation(id = "queryGroupMemberByGroupId", value = "获取群成员", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "群组id", required = "required")})
    @RequestMapping("/post/CompanyTalkGroupController/queryGroupMemberByGroupId")
    public void queryGroupMemberByGroupId(InputObject inputObject, OutputObject outputObject) {
        companyTalkGroupService.queryGroupMemberByGroupId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryChatLogByType", value = "获取聊天记录", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "limit", name = "limit", value = "分页参数,每页多少条数据", required = "required,num"),
        @ApiImplicitParam(id = "page", name = "page", value = "分页参数,第几页", required = "required,num"),
        @ApiImplicitParam(id = "receiveId", name = "receiveId", value = "接收人id", required = "required"),
        @ApiImplicitParam(id = "chatType", name = "chatType", value = "消息类型", required = "required", enumClass = TalkChatType.class)})
    @RequestMapping("/post/CompanyTalkGroupController/queryChatLogByType")
    public void queryChatLogByType(InputObject inputObject, OutputObject outputObject) {
        companyTalkGroupService.queryChatLogByType(inputObject, outputObject);
    }

    @ApiOperation(id = "companytalkgroup009", value = "退出群聊", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "groupId", name = "groupId", value = "群组id", required = "required")})
    @RequestMapping("/post/CompanyTalkGroupController/editUserToExitGroup")
    public void editUserToExitGroup(InputObject inputObject, OutputObject outputObject) {
        companyTalkGroupService.editUserToExitGroup(inputObject, outputObject);
    }

    @ApiOperation(id = "companytalkgroup010", value = "解散群聊", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "groupId", name = "groupId", value = "群组id", required = "required")})
    @RequestMapping("/post/CompanyTalkGroupController/editCreateToExitGroup")
    public void editCreateToExitGroup(InputObject inputObject, OutputObject outputObject) {
        companyTalkGroupService.editCreateToExitGroup(inputObject, outputObject);
    }

    @ApiOperation(id = "editGroupMation", value = "编辑群组信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "群组id", required = "required"),
        @ApiImplicitParam(id = "groupName", name = "groupName", value = "群组名称", required = "required"),
        @ApiImplicitParam(id = "groupImg", name = "groupImg", value = "群组头像")})
    @RequestMapping("/post/CompanyTalkGroupController/editGroupMation")
    public void editGroupMation(InputObject inputObject, OutputObject outputObject) {
        companyTalkGroupService.editGroupMation(inputObject, outputObject);
    }

    @ApiOperation(id = "insertGroupMemberInvite", value = "邀请成员加入群聊", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "groupId", name = "groupId", value = "群组id", required = "required"),
        @ApiImplicitParam(id = "userIds", name = "userIds", value = "被邀请用户id，逗号隔开", required = "required")})
    @RequestMapping("/post/CompanyTalkGroupController/insertGroupMemberInvite")
    public void insertGroupMemberInvite(InputObject inputObject, OutputObject outputObject) {
        companyTalkGroupService.insertGroupMemberInvite(inputObject, outputObject);
    }

}
