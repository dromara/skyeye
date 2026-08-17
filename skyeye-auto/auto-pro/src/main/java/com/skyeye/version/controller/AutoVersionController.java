/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.version.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.version.entity.AutoVersion;
import com.skyeye.version.service.AutoVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoVersionController
 * @Description: 版本管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 9:04
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "版本管理", tags = "版本管理", modelName = "版本管理")
public class AutoVersionController {

    @Autowired
    private AutoVersionService autoVersionService;

    @ApiOperation(id = "queryAutoVersionList", value = "获取版本信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AutoVersionController/queryAutoVersionList")
    public void queryAutoVersionList(InputObject inputObject, OutputObject outputObject) {
        autoVersionService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAutoVersion", value = "新增/编辑版本信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AutoVersion.class)
    @RequestMapping("/post/AutoVersionController/writeAutoVersion")
    public void writeAutoVersion(InputObject inputObject, OutputObject outputObject) {
        autoVersionService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAutoVersionById", value = "根据ID删除版本信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoVersionController/deleteAutoVersionById")
    public void deleteAutoVersionById(InputObject inputObject, OutputObject outputObject) {
        autoVersionService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoVersionByObjectId", value = "根据项目id获取版本信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "项目id", required = "required")})
    @RequestMapping("/post/AutoVersionController/queryAutoVersionByObjectId")
    public void queryAutoVersionByObjectId(InputObject inputObject, OutputObject outputObject) {
        autoVersionService.queryAutoVersionByObjectId(inputObject, outputObject);
    }
}

