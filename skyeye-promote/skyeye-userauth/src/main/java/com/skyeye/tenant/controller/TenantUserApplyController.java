/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.service.TenantUserApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TenantUserApplyController
 * @Description: 用户申请加入租户管理
 */
@RestController
@Api(value = "租户加入申请管理", tags = "租户加入申请管理", modelName = "租户管理")
public class TenantUserApplyController {

    @Autowired
    private TenantUserApplyService tenantUserApplyService;

    @ApiOperation(id = "applyToJoinTenant", value = "申请加入组织", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "tenantId", name = "tenantId", value = "组织id", required = "required"),
        @ApiImplicitParam(id = "applyMessage", name = "applyMessage", value = "申请留言")})
    @RequestMapping("/post/TenantUserApplyController/applyToJoinTenant")
    public void applyToJoinTenant(InputObject inputObject, OutputObject outputObject) {
        tenantUserApplyService.applyToJoinTenant(inputObject, outputObject);
    }

    @ApiOperation(id = "cancelMyTenantUserApply", value = "取消我的加入申请", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "申请记录id", required = "required")})
    @RequestMapping("/post/TenantUserApplyController/cancelMyTenantUserApply")
    public void cancelMyTenantUserApply(InputObject inputObject, OutputObject outputObject) {
        tenantUserApplyService.cancelMyTenantUserApply(inputObject, outputObject);
    }

    @ApiOperation(id = "approveTenantUserApply", value = "通过加入申请", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "申请记录id", required = "required"),
        @ApiImplicitParam(id = "auditRemark", name = "auditRemark", value = "审核备注")})
    @RequestMapping("/post/TenantUserApplyController/approveTenantUserApply")
    public void approveTenantUserApply(InputObject inputObject, OutputObject outputObject) {
        tenantUserApplyService.approveTenantUserApply(inputObject, outputObject);
    }

    @ApiOperation(id = "rejectTenantUserApply", value = "拒绝加入申请", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "申请记录id", required = "required"),
        @ApiImplicitParam(id = "auditRemark", name = "auditRemark", value = "审核备注")})
    @RequestMapping("/post/TenantUserApplyController/rejectTenantUserApply")
    public void rejectTenantUserApply(InputObject inputObject, OutputObject outputObject) {
        tenantUserApplyService.rejectTenantUserApply(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantUserApplyList", value = "分页查询组织加入申请（管理员）", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TenantUserApplyController/queryTenantUserApplyList")
    public void queryTenantUserApplyList(InputObject inputObject, OutputObject outputObject) {
        tenantUserApplyService.queryTenantUserApplyList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyTenantUserApplyList", value = "分页查询我的加入申请", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TenantUserApplyController/queryMyTenantUserApplyList")
    public void queryMyTenantUserApplyList(InputObject inputObject, OutputObject outputObject) {
        tenantUserApplyService.queryMyTenantUserApplyList(inputObject, outputObject);
    }

}
