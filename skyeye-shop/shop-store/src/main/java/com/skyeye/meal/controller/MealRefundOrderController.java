/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.meal.service.MealRefundOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MealRefundOrderController
 * @Description: 套餐退款订单管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/12 9:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "套餐退款订单管理", tags = "套餐退款订单管理", modelName = "套餐退款订单管理")
public class MealRefundOrderController {

    @Autowired
    private MealRefundOrderService mealRefundOrderService;

    @ApiOperation(id = "queryRefundMealOrderList", value = "查询会员套餐退款订单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MealRefundOrderController/queryRefundMealOrderList")
    public void queryRefundMealOrderList(InputObject inputObject, OutputObject outputObject) {
        mealRefundOrderService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "refundMealOrder", value = "会员套餐退款申请操作", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "mealOrderChildId", name = "mealOrderChildId", value = "套餐订单子单据id", required = "required"),
        @ApiImplicitParam(id = "mealRefundReasonId", name = "mealRefundReasonId", value = "退款原因id", required = "required"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "退款门店id"),
        @ApiImplicitParam(id = "refundPrice", name = "refundPrice", value = "退款金额", required = "required,double")})
    @RequestMapping("/post/MealRefundOrderController/refundMealOrder")
    public void refundMealOrder(InputObject inputObject, OutputObject outputObject) {
        mealRefundOrderService.refundMealOrder(inputObject, outputObject);
    }

    @ApiOperation(id = "submitMealRefundOrderToApproval", value = "套餐退款订单提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/MealRefundOrderController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        mealRefundOrderService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeMealRefundOrder", value = "撤销套餐退款订单", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/MealRefundOrderController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        mealRefundOrderService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteMealRefundOrderById", value = "删除套餐退款订单", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MealRefundOrderController/deleteMealRefundOrderById")
    public void deleteMealRefundOrderById(InputObject inputObject, OutputObject outputObject) {
        mealRefundOrderService.deleteById(inputObject, outputObject);
    }

}
