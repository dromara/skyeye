/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.SexEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.entity.TenantUser;
import com.skyeye.tenant.service.TenantUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TenantUserController
 * @Description: 租户下的用户管理
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/26 22:50
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "租户下的用户管理", tags = "租户下的用户管理", modelName = "租户管理")
public class TenantUserController {

    @Autowired
    private TenantUserService tenantUserService;

    @ApiOperation(id = "queryTenantUserList", value = "分页查询租户下的用户信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TenantUserController/queryTenantUserList")
    public void queryTenantUserList(InputObject inputObject, OutputObject outputObject) {
        tenantUserService.queryTenantUserList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantUserById", value = "根据id获取租户下的用户信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "组件id", required = "required")})
    @RequestMapping("/post/TenantUserController/queryTenantUserById")
    public void queryTenantUserById(InputObject inputObject, OutputObject outputObject) {
        tenantUserService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "editTenantUserById", value = "编辑租户下的用户信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = TenantUser.class)
    @RequestMapping("/post/TenantUserController/editTenantUserById")
    public void editTenantUserById(InputObject inputObject, OutputObject outputObject) {
        tenantUserService.updateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "removeTenantUserByStaffId", value = "根据员工ID移除租户用户", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "staffId", name = "staffId", value = "员工ID", required = "required")})
    @RequestMapping("/post/TenantUserController/removeTenantUserByStaffId")
    public void removeTenantUserByStaffId(InputObject inputObject, OutputObject outputObject) {
        tenantUserService.removeTenantUserByStaffId(inputObject, outputObject);
    }

    @ApiOperation(id = "exitTenantUser", value = "用户自主退出租户", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "tenantId", name = "tenantId", value = "租户ID", required = "required")})
    @RequestMapping("/post/TenantUserController/exitTenantUser")
    public void exitTenantUser(InputObject inputObject, OutputObject outputObject) {
        tenantUserService.exitTenantUser(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantUserByStaffId", value = "获取当前用户所属的租户信息", method = "GET", allUse = "2")
    @RequestMapping("/post/TenantUserController/queryTenantUserByStaffId")
    public void queryTenantUserByStaffId(InputObject inputObject, OutputObject outputObject) {
        tenantUserService.queryTenantUserByStaffId(inputObject, outputObject);
    }

    @ApiOperation(id = "addTenantAdminUser", value = "新增租户管理员", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "userName", name = "userName", value = "姓名", required = "required"),
        @ApiImplicitParam(id = "phone", name = "phone", value = "手机号", required = "required"),
        @ApiImplicitParam(id = "userSex", name = "userSex", value = "性别", enumClass = SexEnum.class, required = "required"),
        @ApiImplicitParam(id = "tenantId", name = "tenantId", value = "租户id", required = "required"),
        @ApiImplicitParam(id = "userIdCard", name = "userIdCard", value = "身份证号", required = "required"),
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required"),})
    @RequestMapping("/post/TenantUserController/addTenantAdminUser")
    public void addTenantAdminUser(InputObject inputObject, OutputObject outputObject) {
        tenantUserService.addTenantAdminUser(inputObject, outputObject);
    }

    @ApiOperation(id = "switchingIdentitiesById", value = "根据id切换用户身份", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键ID", required = "required")})
    @RequestMapping("/post/TenantUserController/switchingIdentitiesById")
    public void switchingIdentitiesById(InputObject inputObject, OutputObject outputObject) {
        tenantUserService.switchingIdentitiesById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantUserStaffIdByTenantId", value = "根据租户ID获取租户下的员工ID信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "tenantId", name = "tenantId", value = "租户ID", required = "required"),
        @ApiImplicitParam(id = "stateList", name = "stateList", value = "状态列表，多个逗号分隔")})
    @RequestMapping("/post/TenantUserController/queryTenantUserStaffIdByTenantId")
    public void queryTenantUserStaffIdByTenantId(InputObject inputObject, OutputObject outputObject) {
        tenantUserService.queryTenantUserStaffIdByTenantId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantUserListByTenantId", value = "根据租户id分页查询租户下的用户信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TenantUserController/queryTenantUserListByTenantId")
    public void queryTenantUserListByTenantId(InputObject inputObject, OutputObject outputObject) {
        tenantUserService.queryTenantUserListByTenantId(inputObject, outputObject);
    }
}
