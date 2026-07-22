/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.entity.Member;
import com.skyeye.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MemberController
 * @Description: 会员管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/2 15:36
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "会员管理", tags = "会员管理", modelName = "会员管理")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @ApiOperation(id = "member001", value = "获取会员信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MemberController/queryMemberByList")
    public void queryMemberByList(InputObject inputObject, OutputObject outputObject) {
        memberService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeMember", value = "添加/编辑会员信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Member.class)
    @RequestMapping("/post/MemberController/writeMember")
    public void writeMember(InputObject inputObject, OutputObject outputObject) {
        memberService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMemberById", value = "据ID查询会员信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MemberController/queryMemberById")
    public void queryMemberById(InputObject inputObject, OutputObject outputObject) {
        memberService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMemberListById", value = "根据ID批量查询会员信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/MemberController/queryMemberListById")
    public void queryMemberListById(InputObject inputObject, OutputObject outputObject) {
        memberService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "member004", value = "删除会员信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MemberController/deleteMemberById")
    public void deleteMemberById(InputObject inputObject, OutputObject outputObject) {
        memberService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyWriteMemberList", value = "获取我录入的会员信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MemberController/queryMyWriteMemberList")
    public void queryMyWriteMemberList(InputObject inputObject, OutputObject outputObject) {
        memberService.queryMyWriteMemberList(inputObject, outputObject);
    }

    @ApiOperation(id = "updateCurrentLoginMemberNickname", value = "修改当前登录用户的昵称", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "name", name = "name", value = "昵称", required = "required")})
    @RequestMapping("/post/MemberController/updateCurrentLoginMemberNickname")
    public void updateCurrentLoginMemberNickname(InputObject inputObject, OutputObject outputObject) {
        memberService.updateCurrentLoginMemberNickname(inputObject, outputObject);
    }

    @ApiOperation(id = "updateCurrentLoginMemberAvatar", value = "修改当前登录用户的头像", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "avatar", name = "avatar", value = "头像地址", required = "required")})
    @RequestMapping("/post/MemberController/updateCurrentLoginMemberAvatar")
    public void updateCurrentLoginMemberAvatar(InputObject inputObject, OutputObject outputObject) {
        memberService.updateCurrentLoginMemberAvatar(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCurrentLoginMember", value = "获取当前登录会员信息", method = "POST", allUse = "2")
    @RequestMapping("/post/MemberController/queryCurrentLoginMember")
    public void queryCurrentLoginMember(InputObject inputObject, OutputObject outputObject) {
        memberService.queryCurrentLoginMember(inputObject, outputObject);
    }

}
