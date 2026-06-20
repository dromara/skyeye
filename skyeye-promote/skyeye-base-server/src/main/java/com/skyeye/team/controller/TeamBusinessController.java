/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.team.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.team.entity.TeamBusiness;
import com.skyeye.team.service.TeamBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TeamBusinessController
 * @Description: 团队管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/13 19:35
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "业务对象团队管理", tags = "业务对象团队管理", modelName = "团队模块")
public class TeamBusinessController {

    @Autowired
    private TeamBusinessService teamBusinessService;

    @ApiOperation(id = "createTeamBusiness", value = "根据团队模板生成团队信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "teamTemplateId", name = "teamTemplateId", value = "团队模板id", required = "required"),
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "业务对象id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "业务对象的key", required = "required")})
    @RequestMapping("/post/TeamBusinessController/createTeamBusiness")
    public void createTeamBusiness(InputObject inputObject, OutputObject outputObject) {
        teamBusinessService.createTeamBusiness(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTeamBusiness", value = "根据业务对象id获取团队信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "业务对象id", required = "required")})
    @RequestMapping("/post/TeamBusinessController/queryTeamBusiness")
    public void queryTeamBusiness(InputObject inputObject, OutputObject outputObject) {
        teamBusinessService.queryTeamBusiness(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteTeamBusiness", value = "根据业务对象id删除团队信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "业务对象id", required = "required")})
    @RequestMapping("/post/TeamBusinessController/deleteTeamBusiness")
    public void deleteTeamBusiness(InputObject inputObject, OutputObject outputObject) {
        teamBusinessService.deleteTeamBusiness(inputObject, outputObject);
    }

    @ApiOperation(id = "updateTeamBusiness", value = "编辑团队信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TeamBusiness.class)
    @RequestMapping("/post/TeamBusinessController/updateTeamBusiness")
    public void updateTeamBusiness(InputObject inputObject, OutputObject outputObject) {
        teamBusinessService.updateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "checkTeamBusinessAuthPermission", value = "校验团队权限信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "业务对象id", required = "required"),
        @ApiImplicitParam(id = "enumKey", name = "enumKey", value = "权限的枚举类短名称", required = "required"),
        @ApiImplicitParam(id = "enumClassName", name = "enumClassName", value = "权限的枚举类className名称", required = "required")})
    @RequestMapping("/post/TeamBusinessController/checkTeamBusinessAuthPermission")
    public void checkTeamBusinessAuthPermission(InputObject inputObject, OutputObject outputObject) {
        teamBusinessService.checkTeamBusinessAuthPermission(inputObject, outputObject);
    }

    @ApiOperation(id = "getMyTeamIds", value = "获取我所在的团队对应的团队模板id", method = "GET", allUse = "2")
    @RequestMapping("/post/TeamBusinessController/getMyTeamIds")
    public void getMyTeamIds(InputObject inputObject, OutputObject outputObject) {
        teamBusinessService.getMyTeamIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyBusinessTeamList", value = "分页获取我所在的业务团队信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TeamBusinessController/queryMyBusinessTeamList")
    public void queryMyBusinessTeamList(InputObject inputObject, OutputObject outputObject) {
        teamBusinessService.queryMyBusinessTeamList(inputObject, outputObject);
    }

    @ApiOperation(id = "transferAllChargeUser", value = "转让全部团队经理身份", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "fromUserId", name = "fromUserId", value = "原团队经理用户id", required = "required"),
        @ApiImplicitParam(id = "toUserId", name = "toUserId", value = "新团队经理用户id", required = "required")})
    @RequestMapping("/post/TeamBusinessController/transferAllChargeUser")
    public void transferAllChargeUser(InputObject inputObject, OutputObject outputObject) {
        teamBusinessService.transferAllChargeUser(inputObject, outputObject);
    }

}
