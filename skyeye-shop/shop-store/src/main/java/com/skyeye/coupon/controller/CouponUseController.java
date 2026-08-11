/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.coupon.entity.CouponUse;
import com.skyeye.coupon.service.CouponUseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CouponUseController
 * @Description: 优惠券领取信息管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/23 10:43
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "优惠券领取信息管理", tags = "优惠券领取信息管理", modelName = "优惠券领取信息管理")
public class CouponUseController {

    @Autowired
    private CouponUseService couponUseService;

    @ApiOperation(id = "writeCouponUse", value = "新增/编辑优惠券领取信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CouponUse.class)
    @RequestMapping("/post/CouponUseController/writeCouponUse")
    public void writeCouponUse(InputObject inputObject, OutputObject outputObject) {
        couponUseService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCouponUsePageList", value = "分页查询优惠券领取信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CouponUseController/queryCouponUsePageList")
    public void queryCouponUsePageList(InputObject inputObject, OutputObject outputObject) {
        couponUseService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyCouponUseByState", value = "根据状态分页查询自己的优惠券领取信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CouponUseController/queryMyCouponUseByState")
    public void queryMyCouponUseByState(InputObject inputObject, OutputObject outputObject) {
        couponUseService.queryMyCouponUseByState(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteCouponUseByIds", value = "批量删除优惠券领取信息", method = "POST", allUse = "2")
    @ApiImplicitParams({@ApiImplicitParam(id = "ids", name = "ids", value = "主键id列表，多个id用逗号隔开", required = "required")})
    @RequestMapping("/post/CouponUseController/deleteCouponUseByIds")
    public void deleteCouponUseByIds(InputObject inputObject, OutputObject outputObject) {
        couponUseService.deleteByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCouponUseById", value = "根据id查询优惠券领取信息", method = "POST", allUse = "2")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CouponUseController/queryCouponUseById")
    public void queryCouponUseById(InputObject inputObject, OutputObject outputObject) {
        couponUseService.selectById(inputObject, outputObject);
    }
}