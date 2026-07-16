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
import com.skyeye.coupon.entity.Coupon;
import com.skyeye.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CouponController
 * @Description: 优惠券/模版信息管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/23 10:06
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "优惠券/模版信息管理", tags = "优惠券/模版信息管理", modelName = "优惠券/模版信息管理")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @ApiOperation(id = "writeCoupon", value = "新增/编辑优惠券/模版信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Coupon.class)
    @RequestMapping("/post/CouponController/writeCoupon")
    public void writeCoupon(InputObject inputObject, OutputObject outputObject) {
        couponService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCouponList", value = "分页获取优惠券/模版信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CouponController/queryCouponList")
    public void queryCouponList(InputObject inputObject, OutputObject outputObject) {
        couponService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCouponListByState", value = "根据类型/门店获取已启用的优惠券/模版信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id"),
        @ApiImplicitParam(id = "type", name = "type", value = "类型：优惠券：1，优惠券模板：0，全部：为空")})
    @RequestMapping("/post/CouponController/queryCouponListByState")
    public void queryCouponListByState(InputObject inputObject, OutputObject outputObject) {
        couponService.queryCouponListByState(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCouponById", value = "根据id获取优惠券/模版信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CouponController/queryCouponById")
    public void queryCouponById(InputObject inputObject, OutputObject outputObject) {
        couponService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteCouponById", value = "根据id删除优惠券/模版信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id列表，多个主键用逗号隔开", required = "required")})
    @RequestMapping("/post/CouponController/deleteCouponById")
    public void deleteCouponById(InputObject inputObject, OutputObject outputObject) {
        couponService.deleteByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCouponListByMaterialId", value = "根据商品id获取所有已启用的优惠券列表", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id", required = "required"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/CouponController/queryCouponListByMaterialId")
    public void queryCouponListByMaterialId(InputObject inputObject, OutputObject outputObject) {
        couponService.queryCouponListByMaterialId(inputObject, outputObject);
    }
}