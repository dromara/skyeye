/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.enumeration.SexEnum;
import com.skyeye.common.enumeration.SmsSceneEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personnel.service.AppAuthService;
import com.skyeye.personnel.service.SysEveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AppAuthController
 * @Description: 登录管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/28 17:04
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "登录管理", tags = "登录管理", modelName = "登录管理")
public class AppAuthController {

    @Autowired
    public SysEveUserService sysEveUserService;

    @Autowired
    private AppAuthService appAuthService;

    @ApiOperation(id = "registerUser", value = "注册用户", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "phone", name = "phone", value = "手机号", required = "required"),
        @ApiImplicitParam(id = "userName", name = "userName", value = "姓名", required = "required"),
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required"),
        @ApiImplicitParam(id = "userPhoto", name = "userPhoto", value = "头像", required = "required"),
        @ApiImplicitParam(id = "userSex", name = "userSex", value = "性别", enumClass = SexEnum.class, required = "required,num")})
    @RequestMapping("/post/AppAuthController/registerUser")
    public void registerUser(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.registerUser(inputObject, outputObject);
    }

    @ApiOperation(id = "login001", value = "PC端用户登录", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "userCode", name = "userCode", value = "账号", required = "required"),
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required")})
    @RequestMapping("/post/AppAuthController/queryUserToLogin")
    public void queryUserToLogin(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.queryUserToLogin(inputObject, outputObject);
    }

    @ApiOperation(id = "userphone001", value = "手机端用户登录", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "userCode", name = "userCode", value = "账号", required = "required"),
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required"),
        @ApiImplicitParam(id = "cId", name = "cId", value = "cId,用于手机端消息通知")})
    @RequestMapping("/post/AppAuthController/queryPhoneToLogin")
    public void queryPhoneToLogin(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.queryPhoneToLogin(inputObject, outputObject);
    }

    @ApiOperation(id = "login003", value = "退出", method = "POST", allUse = "2")
    @RequestMapping("/post/AppAuthController/deleteUserMationBySession")
    public void deleteUserMationBySession(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.deleteUserMationBySession(inputObject, outputObject);
    }

    @ApiOperation(id = "login007", value = "修改密码", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "newPassword", name = "newPassword", value = "新密码", required = "required"),
        @ApiImplicitParam(id = "oldPassword", name = "oldPassword", value = "旧密码", required = "required")})
    @RequestMapping("/post/AppAuthController/editUserPassword")
    public void editUserPassword(InputObject inputObject, OutputObject outputObject) {
        sysEveUserService.editUserPassword(inputObject, outputObject);
    }

    @ApiOperation(id = "sendSmsCode", value = "发送手机验证码", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "mobile", name = "mobile", value = "手机号"),
        @ApiImplicitParam(id = "scene", name = "scene", value = "发送场景", enumClass = SmsSceneEnum.class, required = "required")})
    @RequestMapping("/post/AppAuthController/sendSmsCode")
    public void sendSmsCode(InputObject inputObject, OutputObject outputObject) {
        appAuthService.sendSmsCode(inputObject, outputObject);
    }

    @ApiOperation(id = "smsLogin", value = "短信验证码登录", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "mobile", name = "mobile", value = "手机号", required = "required"),
        @ApiImplicitParam(id = "smsCode", name = "smsCode", value = "短信验证码", required = "required")})
    @RequestMapping("/post/AppAuthController/smsLogin")
    public void smsLogin(InputObject inputObject, OutputObject outputObject) {
        appAuthService.smsLogin(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAuthPointByUserId", value = "获取当前登录用户的所有的权限点(PC+APP)", method = "GET", allUse = "2")
    @RequestMapping("/post/AppAuthController/queryAuthPointByUserId")
    public void queryAuthPointByUserId(InputObject inputObject, OutputObject outputObject) {
        appAuthService.queryAuthPointByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "switchTenantSetAuthPoint", value = "用户切换租户重新设置权限点(PC+APP)", method = "POST", allUse = "2")
    @RequestMapping("/post/AppAuthController/switchTenantSetAuthPoint")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "tenantId", name = "tenantId", value = "租户id", required = "required")})
    public void switchTenantSetAuthPoint(InputObject inputObject, OutputObject outputObject) {
        appAuthService.switchTenantSetAuthPoint(inputObject, outputObject);
    }

}
