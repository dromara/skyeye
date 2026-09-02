/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.entity.PlatformBaseSetting;
import com.skyeye.tenant.service.PlatformBaseSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PlatformBaseSettingController
 * @Description: 平台基础信息设置控制层（管理接口需平台租户权限）
 */
@RestController
@Api(value = "平台基础信息设置", tags = "平台基础信息设置", modelName = "租户管理")
public class PlatformBaseSettingController {

    @Autowired
    private PlatformBaseSettingService platformBaseSettingService;

    @ApiOperation(id = "queryPlatformBaseSetting", value = "获取平台基础信息设置", method = "GET", allUse = "2")
    @RequestMapping("/post/PlatformBaseSettingController/queryPlatformBaseSetting")
    public void queryPlatformBaseSetting(InputObject inputObject, OutputObject outputObject) {
        platformBaseSettingService.queryPlatformBaseSetting(inputObject, outputObject);
    }

    @ApiOperation(id = "updatePlatformBaseSetting", value = "编辑平台基础信息设置", method = "PUT", allUse = "1")
    @ApiImplicitParams(classBean = PlatformBaseSetting.class)
    @RequestMapping("/post/PlatformBaseSettingController/updatePlatformBaseSetting")
    public void updatePlatformBaseSetting(InputObject inputObject, OutputObject outputObject) {
        platformBaseSettingService.updatePlatformBaseSetting(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPlatformAccountUnitPrice", value = "获取平台成员席位单价", method = "GET", allUse = "2")
    @RequestMapping("/post/PlatformBaseSettingController/queryPlatformAccountUnitPrice")
    public void queryPlatformAccountUnitPrice(InputObject inputObject, OutputObject outputObject) {
        platformBaseSettingService.queryPlatformAccountUnitPrice(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPlatformTenantOrgSeatConfig", value = "按组织类型获取席位计费规则", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "orgType", name = "orgType", value = "组织类型，见 TenantOrgType", required = "required,num")})
    @RequestMapping("/post/PlatformBaseSettingController/queryPlatformTenantOrgSeatConfig")
    public void queryPlatformTenantOrgSeatConfig(InputObject inputObject, OutputObject outputObject) {
        platformBaseSettingService.queryPlatformTenantOrgSeatConfig(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPlatformTenantCreateConfig", value = "获取当前用户组织创建限制", method = "GET", allUse = "2")
    @RequestMapping("/post/PlatformBaseSettingController/queryPlatformTenantCreateConfig")
    public void queryPlatformTenantCreateConfig(InputObject inputObject, OutputObject outputObject) {
        platformBaseSettingService.queryPlatformTenantCreateConfig(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPlatformAiRole", value = "获取平台绑定的AI角色", method = "GET", allUse = "2")
    @RequestMapping("/post/PlatformBaseSettingController/queryPlatformAiRole")
    public void queryPlatformAiRole(InputObject inputObject, OutputObject outputObject) {
        platformBaseSettingService.queryPlatformAiRole(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPlatformOaAiRole", value = "获取平台绑定的办公OA AI角色", method = "GET", allUse = "2")
    @RequestMapping("/post/PlatformBaseSettingController/queryPlatformOaAiRole")
    public void queryPlatformOaAiRole(InputObject inputObject, OutputObject outputObject) {
        platformBaseSettingService.queryPlatformOaAiRole(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPlatformTokenBilling", value = "获取平台Token计费标准", method = "GET", allUse = "2")
    @RequestMapping("/post/PlatformBaseSettingController/queryPlatformTokenBilling")
    public void queryPlatformTokenBilling(InputObject inputObject, OutputObject outputObject) {
        platformBaseSettingService.queryPlatformTokenBilling(inputObject, outputObject);
    }

}
