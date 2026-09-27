package com.skyeye.order.conroller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.order.service.OrderAfterSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "商城商品售后", tags = "商城商品售后", modelName = "商城商品售后")
public class OrderAfterSaleController {

    @Autowired
    private OrderAfterSaleService orderAfterSaleService;

    @ApiOperation(id = "applyOrderAfterSale", value = "买家申请售后", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "orderItemId", name = "orderItemId", value = "订单子单id", required = "required"),
        @ApiImplicitParam(id = "type", name = "type", value = "1仅退款 2退货退款 3换货", required = "required,num"),
        @ApiImplicitParam(id = "reasonText", name = "reasonText", value = "申请原因", required = "required"),
        @ApiImplicitParam(id = "applyAmount", name = "applyAmount", value = "申请退款金额（分）"),
        @ApiImplicitParam(id = "applyCount", name = "applyCount", value = "申请数量"),
        @ApiImplicitParam(id = "evidence", name = "evidence", value = "凭证图片JSON")})
    @RequestMapping("/post/OrderAfterSaleController/applyOrderAfterSale")
    public void applyOrderAfterSale(InputObject inputObject, OutputObject outputObject) {
        orderAfterSaleService.applyOrderAfterSale(inputObject, outputObject);
    }

    @ApiOperation(id = "cancelOrderAfterSale", value = "买家撤销售后申请", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "售后单id", required = "required")})
    @RequestMapping("/post/OrderAfterSaleController/cancelOrderAfterSale")
    public void cancelOrderAfterSale(InputObject inputObject, OutputObject outputObject) {
        orderAfterSaleService.cancelOrderAfterSale(inputObject, outputObject);
    }

    @ApiOperation(id = "queryOrderAfterSaleByItemId", value = "按子单查询售后", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "orderItemId", name = "orderItemId", value = "订单子单id", required = "required")})
    @RequestMapping("/post/OrderAfterSaleController/queryOrderAfterSaleByItemId")
    public void queryOrderAfterSaleByItemId(InputObject inputObject, OutputObject outputObject) {
        orderAfterSaleService.queryOrderAfterSaleByItemId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryOrderAfterSaleById", value = "按id查询售后详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "售后单id", required = "required")})
    @RequestMapping("/post/OrderAfterSaleController/queryOrderAfterSaleById")
    public void queryOrderAfterSaleById(InputObject inputObject, OutputObject outputObject) {
        orderAfterSaleService.queryOrderAfterSaleById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyOrderAfterSaleList", value = "买家售后记录列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/OrderAfterSaleController/queryMyOrderAfterSaleList")
    public void queryMyOrderAfterSaleList(InputObject inputObject, OutputObject outputObject) {
        orderAfterSaleService.queryMyOrderAfterSaleList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStoreOrderAfterSaleList", value = "门店售后记录列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/OrderAfterSaleController/queryStoreOrderAfterSaleList")
    public void queryStoreOrderAfterSaleList(InputObject inputObject, OutputObject outputObject) {
        orderAfterSaleService.queryStoreOrderAfterSaleList(inputObject, outputObject);
    }

    @ApiOperation(id = "agreeOrderAfterSale", value = "商家同意售后", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "售后单id", required = "required"),
        @ApiImplicitParam(id = "refundAmount", name = "refundAmount", value = "实际退款金额（分），可下调"),
        @ApiImplicitParam(id = "restoreStock", name = "restoreStock", value = "完成后是否回补库存：0否1是，默认按售后类型")})
    @RequestMapping("/post/OrderAfterSaleController/agreeOrderAfterSale")
    public void agreeOrderAfterSale(InputObject inputObject, OutputObject outputObject) {
        orderAfterSaleService.agreeOrderAfterSale(inputObject, outputObject);
    }

    @ApiOperation(id = "rejectOrderAfterSale", value = "商家拒绝售后", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "售后单id", required = "required"),
        @ApiImplicitParam(id = "rejectReason", name = "rejectReason", value = "拒绝原因", required = "required")})
    @RequestMapping("/post/OrderAfterSaleController/rejectOrderAfterSale")
    public void rejectOrderAfterSale(InputObject inputObject, OutputObject outputObject) {
        orderAfterSaleService.rejectOrderAfterSale(inputObject, outputObject);
    }

    @ApiOperation(id = "fillAfterSaleReturnLogistics", value = "买家填写退货单号", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "售后单id", required = "required"),
        @ApiImplicitParam(id = "returnDeliverNumber", name = "returnDeliverNumber", value = "退货快递单号", required = "required")})
    @RequestMapping("/post/OrderAfterSaleController/fillAfterSaleReturnLogistics")
    public void fillAfterSaleReturnLogistics(InputObject inputObject, OutputObject outputObject) {
        orderAfterSaleService.fillAfterSaleReturnLogistics(inputObject, outputObject);
    }

    @ApiOperation(id = "confirmAfterSaleReturn", value = "商家确认收到退货", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "售后单id", required = "required")})
    @RequestMapping("/post/OrderAfterSaleController/confirmAfterSaleReturn")
    public void confirmAfterSaleReturn(InputObject inputObject, OutputObject outputObject) {
        orderAfterSaleService.confirmAfterSaleReturn(inputObject, outputObject);
    }

    @ApiOperation(id = "notifyOrderAfterSaleRefundSuccess", value = "售后退款成功回调", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "outRefundNo", name = "outRefundNo", value = "商户退款单号"),
        @ApiImplicitParam(id = "outTradeNo", name = "outTradeNo", value = "兼容字段，可传退款单号"),
        @ApiImplicitParam(id = "channelRefundNo", name = "channelRefundNo", value = "通道退款单号")})
    @RequestMapping("/post/OrderAfterSaleController/notifyOrderAfterSaleRefundSuccess")
    public void notifyOrderAfterSaleRefundSuccess(InputObject inputObject, OutputObject outputObject) {
        orderAfterSaleService.notifyOrderAfterSaleRefundSuccess(inputObject, outputObject);
    }
}
