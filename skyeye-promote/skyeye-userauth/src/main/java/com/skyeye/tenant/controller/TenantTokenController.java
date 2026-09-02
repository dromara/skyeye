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
import com.skyeye.tenant.service.TenantTokenAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "租户Token计费", tags = "租户Token计费", modelName = "租户管理")
public class TenantTokenController {

    @Autowired
    private TenantTokenAccountService tenantTokenAccountService;

    @ApiOperation(id = "queryCurrentTenantTokenAccount", value = "查询当前租户Token账户", method = "GET", allUse = "2")
    @RequestMapping("/post/TenantTokenController/queryCurrentTenantTokenAccount")
    public void queryCurrentTenantTokenAccount(InputObject inputObject, OutputObject outputObject) {
        tenantTokenAccountService.queryCurrentTenantTokenAccount(inputObject, outputObject);
    }

    @ApiOperation(id = "saveCurrentTenantTokenMode", value = "设置当前租户Token计费方式", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "billingMode", name = "billingMode", value = "计费方式，见 TenantTokenBillingMode", required = "required,num")})
    @RequestMapping("/post/TenantTokenController/saveCurrentTenantTokenMode")
    public void saveCurrentTenantTokenMode(InputObject inputObject, OutputObject outputObject) {
        tenantTokenAccountService.saveCurrentTenantTokenMode(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCurrentTenantTokenDailyUsage", value = "查询当前租户每日Token用量", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TenantTokenController/queryCurrentTenantTokenDailyUsage")
    public void queryCurrentTenantTokenDailyUsage(InputObject inputObject, OutputObject outputObject) {
        tenantTokenAccountService.queryCurrentTenantTokenDailyUsage(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCurrentTenantTokenBillList", value = "查询当前租户Token月结账单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TenantTokenController/queryCurrentTenantTokenBillList")
    public void queryCurrentTenantTokenBillList(InputObject inputObject, OutputObject outputObject) {
        tenantTokenAccountService.queryCurrentTenantTokenBillList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPlatformTenantTokenAccountList", value = "平台查询租户Token账户", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TenantTokenController/queryPlatformTenantTokenAccountList")
    public void queryPlatformTenantTokenAccountList(InputObject inputObject, OutputObject outputObject) {
        tenantTokenAccountService.queryPlatformTenantTokenAccountList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPlatformTenantTokenDailyUsage", value = "平台查询租户每日Token用量", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TenantTokenController/queryPlatformTenantTokenDailyUsage")
    public void queryPlatformTenantTokenDailyUsage(InputObject inputObject, OutputObject outputObject) {
        tenantTokenAccountService.queryPlatformTenantTokenDailyUsage(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPlatformTenantTokenBillList", value = "平台查询租户Token月结账单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TenantTokenController/queryPlatformTenantTokenBillList")
    public void queryPlatformTenantTokenBillList(InputObject inputObject, OutputObject outputObject) {
        tenantTokenAccountService.queryPlatformTenantTokenBillList(inputObject, outputObject);
    }

    @ApiOperation(id = "checkTenantTokenAllowUse", value = "校验租户Token是否可用", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "tenantId", name = "tenantId", value = "租户id")})
    @RequestMapping("/post/TenantTokenController/checkTenantTokenAllowUse")
    public void checkTenantTokenAllowUse(InputObject inputObject, OutputObject outputObject) {
        tenantTokenAccountService.checkTenantTokenAllowUse(inputObject, outputObject);
    }

    @ApiOperation(id = "recordTenantTokenUsage", value = "记录租户Token用量", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "tenantId", name = "tenantId", value = "租户id"),
        @ApiImplicitParam(id = "promptTokens", name = "promptTokens", value = "提问Token", required = "num"),
        @ApiImplicitParam(id = "completionTokens", name = "completionTokens", value = "回答Token", required = "num"),
        @ApiImplicitParam(id = "totalTokens", name = "totalTokens", value = "合计Token", required = "num")})
    @RequestMapping("/post/TenantTokenController/recordTenantTokenUsage")
    public void recordTenantTokenUsage(InputObject inputObject, OutputObject outputObject) {
        tenantTokenAccountService.recordTenantTokenUsage(inputObject, outputObject);
    }

}
