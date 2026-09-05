/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.projectconfig.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.projectconfig.entity.AutoProjectConfig;
import com.skyeye.projectconfig.service.AutoProjectConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目配置控制层。
 */
@RestController
@Api(value = "项目配置", tags = "项目配置", modelName = "项目配置")
public class AutoProjectConfigController {

    @Autowired
    private AutoProjectConfigService autoProjectConfigService;

    @ApiOperation(id = "queryAutoProjectConfigByObjectId", value = "按项目查询功能配置", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "项目id", required = "required")})
    @RequestMapping("/post/AutoProjectConfigController/queryAutoProjectConfigByObjectId")
    public void queryAutoProjectConfigByObjectId(InputObject inputObject, OutputObject outputObject) {
        autoProjectConfigService.queryAutoProjectConfigByObjectId(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAutoProjectConfig", value = "保存项目配置", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AutoProjectConfig.class)
    @RequestMapping("/post/AutoProjectConfigController/writeAutoProjectConfig")
    public void writeAutoProjectConfig(InputObject inputObject, OutputObject outputObject) {
        autoProjectConfigService.saveOrUpdateEntity(inputObject, outputObject);
    }

}
