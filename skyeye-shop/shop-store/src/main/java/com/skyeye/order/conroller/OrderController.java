/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.conroller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.order.entity.Order;
import com.skyeye.order.service.OrderService;
import com.skyeye.store.entity.ShopAddressHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: OrderController
 * @Description: 商品订单管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "商品订单管理", tags = "商品订单管理", modelName = "商品订单管理")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation(id = "insertOrder", value = "新增商品订单信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Order.class)
    @RequestMapping("/post/OrderController/insertOrder")
    public void insertOrder(InputObject inputObject, OutputObject outputObject) {
        orderService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryOrderPageListPC", value = "分页获取所有的商品订单信息(不区分租户，因为是平台租户用的)--管理端", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/OrderController/queryOrderPageListPC")
    public void queryOrderPageListPC(InputObject inputObject, OutputObject outputObject) {
        orderService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "changeOrderAddress", value = "修改订单的收件地址", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ShopAddressHistory.class)
    @RequestMapping("/post/OrderController/changeOrderAddress")
    public void changeOrderAddress(InputObject inputObject, OutputObject outputObject) {
        orderService.changeOrderAddress(inputObject, outputObject);
    }

    @ApiOperation(id = "queryOrderPageList", value = "分页获取商品订单信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/OrderController/queryOrderPageList")
    public void queryOrderList(InputObject inputObject, OutputObject outputObject) {
        orderService.queryOrderPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteOrderByIds", value = "批量删除商品订单信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({@ApiImplicitParam(id = "ids", name = "ids", value = "主键id,多个id用逗号分隔", required = "required")})
    @RequestMapping("/post/OrderController/deleteOrderByIds")
    public void deleteOrderByIds(InputObject inputObject, OutputObject outputObject) {
        orderService.deleteByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "selectOrderById", value = "根据id查询商品订单信息", method = "POST", allUse = "2")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OrderController/selectOrderById")
    public void selectOrderById(InputObject inputObject, OutputObject outputObject) {
        orderService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "cancelOrder", value = "商品订单取消", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "子单id", required = "required"),
        @ApiImplicitParam(id = "cancelType", name = "cancelType", value = "取消类型", required = "required")})
    @RequestMapping("/post/OrderController/cancelOrder")
    public void cancelOrder(InputObject inputObject, OutputObject outputObject) {
        orderService.cancelOrder(inputObject, outputObject);
    }

    @ApiOperation(id = "finishOrder", value = "商品订单完成", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OrderController/finishOrder")
    public void finishOrder(InputObject inputObject, OutputObject outputObject) {
        orderService.finishOrder(inputObject, outputObject);
    }

    /**
     * 租户收银台发起支付；PayApp.orderNotifyUrl 需指向 notifyOrderPaySuccess
     */
    @ApiOperation(id = "payOrder", value = "商品订单支付", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "channelCode", name = "channelCode", value = "支付渠道编码", required = "required"),
        @ApiImplicitParam(id = "returnUrl", name = "returnUrl", value = "支付完成跳转地址"),
        @ApiImplicitParam(id = "channelExtras", name = "channelExtras", value = "支付渠道的额外参数，例如说，微信公众号需要传递 openid 参数", required = "json")})
    @RequestMapping("/post/OrderController/payOrder")
    public void payOrder(InputObject inputObject, OutputObject outputObject) {
        orderService.payOrder(inputObject, outputObject);
    }

    @ApiOperation(id = "notifyOrderPaySuccess", value = "商城订单支付成功回调", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "outTradeNo", name = "outTradeNo", value = "订单编号", required = "required"),
        @ApiImplicitParam(id = "channelCode", name = "channelCode", value = "支付渠道编码"),
        @ApiImplicitParam(id = "successTime", name = "successTime", value = "支付成功时间")})
    @RequestMapping("/post/OrderController/notifyOrderPaySuccess")
    public void notifyOrderPaySuccess(InputObject inputObject, OutputObject outputObject) {
        orderService.notifyOrderPaySuccess(inputObject, outputObject);
    }

    @ApiOperation(id = "generatePayOrderRrCode", value = "生成支付订单的二维码", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "channelCode", name = "channelCode", value = "支付渠道编码", required = "required"),
        @ApiImplicitParam(id = "returnUrl", name = "returnUrl", value = "支付完成跳转地址"),
        @ApiImplicitParam(id = "channelExtras", name = "channelExtras", value = "渠道扩展参数", required = "json")})
    @RequestMapping("/post/OrderController/generatePayOrderRrCode")
    public void generatePayOrderRrCode(InputObject inputObject, OutputObject outputObject) {
        orderService.generatePayOrderRrCode(inputObject, outputObject);
    }

    @ApiOperation(id = "updateOrderToPayState", value = "完成支付", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OrderController/updateOrderToPayState")
    public void updateOrderToPayState(InputObject inputObject, OutputObject outputObject) {
        orderService.updateOrderToPayState(inputObject, outputObject);
    }

}