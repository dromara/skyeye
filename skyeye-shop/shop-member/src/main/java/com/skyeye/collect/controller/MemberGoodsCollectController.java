/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.collect.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.collect.service.MemberGoodsCollectService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "会员商品收藏", tags = "会员商品收藏", modelName = "会员管理")
public class MemberGoodsCollectController {

    @Autowired
    private MemberGoodsCollectService memberGoodsCollectService;

    @ApiOperation(id = "toggleGoodsCollect", value = "切换商品收藏状态", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "materialStoreId", name = "materialStoreId", value = "门店商品关联id", required = "required")})
    @RequestMapping("/post/MemberGoodsCollectController/toggleGoodsCollect")
    public void toggleGoodsCollect(InputObject inputObject, OutputObject outputObject) {
        memberGoodsCollectService.toggleGoodsCollect(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyGoodsCollectList", value = "分页查询我的商品收藏", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MemberGoodsCollectController/queryMyGoodsCollectList")
    public void queryMyGoodsCollectList(InputObject inputObject, OutputObject outputObject) {
        memberGoodsCollectService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "checkGoodsCollect", value = "检查商品是否已收藏", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "materialStoreId", name = "materialStoreId", value = "门店商品关联id", required = "required")})
    @RequestMapping("/post/MemberGoodsCollectController/checkGoodsCollect")
    public void checkGoodsCollect(InputObject inputObject, OutputObject outputObject) {
        memberGoodsCollectService.checkGoodsCollect(inputObject, outputObject);
    }
}
