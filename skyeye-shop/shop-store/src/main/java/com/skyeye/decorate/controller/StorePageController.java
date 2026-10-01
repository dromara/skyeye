/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.decorate.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.decorate.service.StorePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "门店装修页", tags = "门店装修", modelName = "门店装修")
public class StorePageController {

    @Autowired
    private StorePageService storePageService;

    @ApiOperation(id = "queryStorePageDecorate", value = "查询门店装修（设计器回显，无则初始化默认组件）", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "pageType", name = "pageType", value = "页面类型，默认1门店首页", required = "num")})
    @RequestMapping("/post/StorePageController/queryStorePageDecorate")
    public void queryStorePageDecorate(InputObject inputObject, OutputObject outputObject) {
        storePageService.queryStorePageDecorate(inputObject, outputObject);
    }

    @ApiOperation(id = "saveStorePageDecorate", value = "保存门店装修整页", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "pageType", name = "pageType", value = "页面类型，默认1门店首页", required = "num"),
        @ApiImplicitParam(id = "compList", name = "compList", value = "组件列表JSON", required = "required,json")})
    @RequestMapping("/post/StorePageController/saveStorePageDecorate")
    public void saveStorePageDecorate(InputObject inputObject, OutputObject outputObject) {
        storePageService.saveStorePageDecorate(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStorePageForC", value = "C端查询门店装修页", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required"),
        @ApiImplicitParam(id = "clientType", name = "clientType", value = "端类型 app/pc")})
    @RequestMapping("/post/StorePageController/queryStorePageForC")
    public void queryStorePageForC(InputObject inputObject, OutputObject outputObject) {
        storePageService.queryStorePageForC(inputObject, outputObject);
    }
}
