/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.portal.entity.PortalProductFeature;
import com.skyeye.portal.service.PortalProductFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 官网产品功能矩阵接口
 */
@RestController
@Api(value = "官网产品功能矩阵", tags = "官网产品功能矩阵", modelName = "门户管理")
public class PortalProductFeatureController {

    @Autowired
    private PortalProductFeatureService portalProductFeatureService;

    @ApiOperation(id = "queryPortalProductFeatureList", value = "分页查询产品功能矩阵", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PortalProductFeatureController/queryPortalProductFeatureList")
    public void queryPortalProductFeatureList(InputObject inputObject, OutputObject outputObject) {
        portalProductFeatureService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePortalProductFeature", value = "新增/编辑产品功能矩阵", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PortalProductFeature.class)
    @RequestMapping("/post/PortalProductFeatureController/writePortalProductFeature")
    public void writePortalProductFeature(InputObject inputObject, OutputObject outputObject) {
        portalProductFeatureService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePortalProductFeatureById", value = "删除产品功能矩阵", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PortalProductFeatureController/deletePortalProductFeatureById")
    public void deletePortalProductFeatureById(InputObject inputObject, OutputObject outputObject) {
        portalProductFeatureService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPortalProductFeatureById", value = "根据id获取产品功能矩阵", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PortalProductFeatureController/queryPortalProductFeatureById")
    public void queryPortalProductFeatureById(InputObject inputObject, OutputObject outputObject) {
        portalProductFeatureService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledPortalProductFeatureList", value = "获取已启用产品功能矩阵（官网展示）", method = "POST", allUse = "0")
    @RequestMapping("/post/PortalProductFeatureController/queryEnabledPortalProductFeatureList")
    public void queryEnabledPortalProductFeatureList(InputObject inputObject, OutputObject outputObject) {
        portalProductFeatureService.queryEnabledPortalProductFeatureList(inputObject, outputObject);
    }
}
