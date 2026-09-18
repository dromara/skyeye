/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.browse.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.browse.service.MemberBrowseHistoryService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "会员浏览足迹", tags = "会员浏览足迹", modelName = "会员管理")
public class MemberBrowseHistoryController {

    @Autowired
    private MemberBrowseHistoryService memberBrowseHistoryService;

    @ApiOperation(id = "recordBrowseHistory", value = "记录商品浏览足迹（异步调用即可）", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "materialStoreId", name = "materialStoreId", value = "门店商品关联id", required = "required"),
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id"),
        @ApiImplicitParam(id = "goodsName", name = "goodsName", value = "商品名称"),
        @ApiImplicitParam(id = "goodsLogo", name = "goodsLogo", value = "商品主图"),
        @ApiImplicitParam(id = "price", name = "price", value = "展示价格"),
        @ApiImplicitParam(id = "storeName", name = "storeName", value = "门店名称")
    })
    @RequestMapping("/post/MemberBrowseHistoryController/recordBrowseHistory")
    public void recordBrowseHistory(InputObject inputObject, OutputObject outputObject) {
        memberBrowseHistoryService.recordBrowseHistory(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyBrowseHistoryList", value = "分页查询我的浏览足迹", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MemberBrowseHistoryController/queryMyBrowseHistoryList")
    public void queryMyBrowseHistoryList(InputObject inputObject, OutputObject outputObject) {
        memberBrowseHistoryService.queryMyBrowseHistoryList(inputObject, outputObject);
    }

    @ApiOperation(id = "clearMyBrowseHistory", value = "清空我的浏览足迹", method = "POST", allUse = "2")
    @RequestMapping("/post/MemberBrowseHistoryController/clearMyBrowseHistory")
    public void clearMyBrowseHistory(InputObject inputObject, OutputObject outputObject) {
        memberBrowseHistoryService.clearMyBrowseHistory(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteMyBrowseHistoryById", value = "删除单条浏览足迹", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "足迹主键id", required = "required")
    })
    @RequestMapping("/post/MemberBrowseHistoryController/deleteMyBrowseHistoryById")
    public void deleteMyBrowseHistoryById(InputObject inputObject, OutputObject outputObject) {
        memberBrowseHistoryService.deleteMyBrowseHistoryById(inputObject, outputObject);
    }
}
