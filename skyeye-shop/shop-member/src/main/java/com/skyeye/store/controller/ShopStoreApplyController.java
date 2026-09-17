/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.store.service.ShopStoreApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopStoreApplyController
 * @Description: 个人开店申请控制层
 */
@RestController
@Api(value = "个人开店申请", tags = "个人开店申请", modelName = "门店管理")
public class ShopStoreApplyController {

    @Autowired
    private ShopStoreApplyService shopStoreApplyService;

    @ApiOperation(id = "applyPersonalStore", value = "会员申请个人开店", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "storeName", name = "storeName", value = "店铺名称", required = "required"),
        @ApiImplicitParam(id = "logo", name = "logo", value = "店铺logo"),
        @ApiImplicitParam(id = "remark", name = "remark", value = "店铺简介/申请说明"),
        @ApiImplicitParam(id = "contactName", name = "contactName", value = "联系人"),
        @ApiImplicitParam(id = "contactPhone", name = "contactPhone", value = "联系电话"),
        @ApiImplicitParam(id = "provinceId", name = "provinceId", value = "省ID"),
        @ApiImplicitParam(id = "cityId", name = "cityId", value = "市ID"),
        @ApiImplicitParam(id = "areaId", name = "areaId", value = "区县ID"),
        @ApiImplicitParam(id = "townshipId", name = "townshipId", value = "乡镇ID"),
        @ApiImplicitParam(id = "absoluteAddress", name = "absoluteAddress", value = "详细地址")})
    @RequestMapping("/post/ShopStoreApplyController/applyPersonalStore")
    public void applyPersonalStore(InputObject inputObject, OutputObject outputObject) {
        shopStoreApplyService.applyPersonalStore(inputObject, outputObject);
    }

    @ApiOperation(id = "cancelMyPersonalStoreApply", value = "取消我的个人开店申请", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "申请记录id", required = "required")})
    @RequestMapping("/post/ShopStoreApplyController/cancelMyPersonalStoreApply")
    public void cancelMyPersonalStoreApply(InputObject inputObject, OutputObject outputObject) {
        shopStoreApplyService.cancelMyPersonalStoreApply(inputObject, outputObject);
    }

    @ApiOperation(id = "approvePersonalStoreApply", value = "通过个人开店申请", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "申请记录id", required = "required"),
        @ApiImplicitParam(id = "auditRemark", name = "auditRemark", value = "审核备注")})
    @RequestMapping("/post/ShopStoreApplyController/approvePersonalStoreApply")
    public void approvePersonalStoreApply(InputObject inputObject, OutputObject outputObject) {
        shopStoreApplyService.approvePersonalStoreApply(inputObject, outputObject);
    }

    @ApiOperation(id = "rejectPersonalStoreApply", value = "拒绝个人开店申请", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "申请记录id", required = "required"),
        @ApiImplicitParam(id = "auditRemark", name = "auditRemark", value = "审核备注")})
    @RequestMapping("/post/ShopStoreApplyController/rejectPersonalStoreApply")
    public void rejectPersonalStoreApply(InputObject inputObject, OutputObject outputObject) {
        shopStoreApplyService.rejectPersonalStoreApply(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPersonalStoreApplyList", value = "分页查询个人开店申请（平台审核）", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopStoreApplyController/queryPersonalStoreApplyList")
    public void queryPersonalStoreApplyList(InputObject inputObject, OutputObject outputObject) {
        shopStoreApplyService.queryPersonalStoreApplyList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyPersonalStoreApplyList", value = "分页查询我的个人开店申请", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopStoreApplyController/queryMyPersonalStoreApplyList")
    public void queryMyPersonalStoreApplyList(InputObject inputObject, OutputObject outputObject) {
        shopStoreApplyService.queryMyPersonalStoreApplyList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyPersonalStoreQuota", value = "查询我的个人门店配额与已用数量", method = "GET", allUse = "2")
    @RequestMapping("/post/ShopStoreApplyController/queryMyPersonalStoreQuota")
    public void queryMyPersonalStoreQuota(InputObject inputObject, OutputObject outputObject) {
        shopStoreApplyService.queryMyPersonalStoreQuota(inputObject, outputObject);
    }

}
