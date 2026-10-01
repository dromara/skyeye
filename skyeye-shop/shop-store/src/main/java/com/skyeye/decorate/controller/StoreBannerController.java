/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.decorate.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.decorate.entity.StoreBanner;
import com.skyeye.decorate.service.StoreBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门店轮播：对齐 Adsense，写/删/分页走低代码基类接口。
 */
@RestController
@Api(value = "门店轮播", tags = "门店装修", modelName = "门店装修")
public class StoreBannerController {

    @Autowired
    private StoreBannerService storeBannerService;

    @ApiOperation(id = "queryStoreBannerList", value = "分页查询门店轮播（objectId=门店id）", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/StoreBannerController/queryStoreBannerList")
    public void queryStoreBannerList(InputObject inputObject, OutputObject outputObject) {
        storeBannerService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeStoreBanner", value = "新增/编辑门店轮播", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = StoreBanner.class)
    @RequestMapping("/post/StoreBannerController/writeStoreBanner")
    public void writeStoreBanner(InputObject inputObject, OutputObject outputObject) {
        storeBannerService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteStoreBannerByIds", value = "批量删除门店轮播", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id列表，多个id用逗号分隔", required = "required")})
    @RequestMapping("/post/StoreBannerController/deleteStoreBannerByIds")
    public void deleteStoreBannerByIds(InputObject inputObject, OutputObject outputObject) {
        storeBannerService.deleteByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledStoreBannerList", value = "C端查询已启用门店轮播", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/StoreBannerController/queryEnabledStoreBannerList")
    public void queryEnabledStoreBannerList(InputObject inputObject, OutputObject outputObject) {
        storeBannerService.queryEnabledStoreBannerList(inputObject, outputObject);
    }
}
