/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.meal.entity.MealOrder;
import com.skyeye.meal.service.MealOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MealOrderController
 * @Description: 套餐订单管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/6 19:56
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "套餐订单管理", tags = "套餐订单管理", modelName = "套餐订单管理")
public class MealOrderController {

    @Autowired
    private MealOrderService mealOrderService;

    @ApiOperation(id = "queryMealOrderList", value = "获取套餐订单信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MealOrderController/queryMealOrderList")
    public void queryMealOrderList(InputObject inputObject, OutputObject outputObject) {
        mealOrderService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "insertMealOrder", value = "添加订单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = MealOrder.class)
    @RequestMapping("/post/MealController/insertMealOrder")
    public void insertMealOrder(InputObject inputObject, OutputObject outputObject) {
        mealOrderService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "mealOrderNotify", value = "支付订单完成后的回调", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "out_trade_no", name = "outTradeNo", value = "商户订单号", required = "required"),
        @ApiImplicitParam(id = "total_fee", name = "totalFee", value = "实际支付的订单金额:单位 分", required = "required")})
    @RequestMapping("/post/MealController/mealOrderNotify")
    public void mealOrderNotify(InputObject inputObject, OutputObject outputObject) {
        mealOrderService.mealOrderNotify(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMealOrderById", value = "套餐订单购买详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "套餐订单id", required = "required")})
    @RequestMapping("/post/MealController/queryMealOrderById")
    public void queryMealOrderById(InputObject inputObject, OutputObject outputObject) {
        mealOrderService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteMealOrderById", value = "删除套餐订单(待支付状态可以删除)", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "套餐订单id", required = "required")})
    @RequestMapping("/post/MealController/deleteMealOrderById")
    public void deleteMealOrderById(InputObject inputObject, OutputObject outputObject) {
        mealOrderService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "updateMealOrderState", value = "套餐订单状态修改", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "套餐订单id", required = "required"),
        @ApiImplicitParam(id = "state", name = "state", value = "订单状态  1.待支付  2.已支付 线上订单有以下状态： (0.已提交订单  3.已收货  4.已关闭  5.已退款)", required = "required,num")})
    @RequestMapping("/post/MealController/updateMealOrderState")
    public void updateMealOrderState(InputObject inputObject, OutputObject outputObject) {
        mealOrderService.updateMealOrderState(inputObject, outputObject);
    }

}
