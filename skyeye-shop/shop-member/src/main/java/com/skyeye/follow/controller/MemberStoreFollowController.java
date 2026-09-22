/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.follow.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.follow.service.MemberStoreFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "会员门店关注", tags = "会员门店关注", modelName = "会员管理")
public class MemberStoreFollowController {

    @Autowired
    private MemberStoreFollowService memberStoreFollowService;

    @ApiOperation(id = "toggleStoreFollow", value = "切换门店关注状态", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/MemberStoreFollowController/toggleStoreFollow")
    public void toggleStoreFollow(InputObject inputObject, OutputObject outputObject) {
        memberStoreFollowService.toggleStoreFollow(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyStoreFollowList", value = "分页查询我的门店关注", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MemberStoreFollowController/queryMyStoreFollowList")
    public void queryMyStoreFollowList(InputObject inputObject, OutputObject outputObject) {
        memberStoreFollowService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "checkStoreFollow", value = "检查门店是否已关注", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/MemberStoreFollowController/checkStoreFollow")
    public void checkStoreFollow(InputObject inputObject, OutputObject outputObject) {
        memberStoreFollowService.checkStoreFollow(inputObject, outputObject);
    }
}
