package com.skyeye.order.conroller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.order.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "订单子单表管理", tags = "订单子单表管理", modelName = "订单子单表管理")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;

    @ApiOperation(id = "queryOrderItemByStoreId", value = "根据门店Id分页查询订单子单信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "门店id")})
    @RequestMapping("/post/OrderItemController/queryOrderItemByStoreId")
    public void queryOrderByStoreId(InputObject inputObject, OutputObject outputObject) {
        orderItemService.queryPageList(inputObject, outputObject);
    }

    /**
     * 商品订单子单发货。
     * <p>个人店会同步扣库存；物流相关参数可选（未配置快递模板时可只填发货数量）。</p>
     */
    @ApiOperation(id = "deliverGoodsById", value = "商品订单子单发货", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "orderId", name = "orderId", value = "订单id", required = "required"),
        @ApiImplicitParam(id = "deliverNumber", name = "deliverNumber", value = "快递单号(唯一，不重复)"),
        @ApiImplicitParam(id = "deliveryTemplateChargeId", name = "deliveryTemplateChargeId", value = "快递运费模板计费配置表id"),
        @ApiImplicitParam(id = "deliveryCompanyId", name = "deliveryCompanyId", value = "快递公司信息id"),
        @ApiImplicitParam(id = "num", name = "num", value = "发货数量", required = "required,num")})
    @RequestMapping("/post/OrderItemController/deliverGoodsById")
    public void deliverGoodsById(InputObject inputObject, OutputObject outputObject) {
        orderItemService.deliverGoodsById(inputObject, outputObject);
    }

    @ApiOperation(id = "changeOrderItemAdjustPrice", value = "订单子单调价", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "adjustPrice", name = "adjustPrice", value = "调整的价格，不可为负数(单位 元)", required = "required,num")})
    @RequestMapping("/post/OrderItemController/changeOrderItemAdjustPrice")
    public void changeOrderItemAdjustPrice(InputObject inputObject, OutputObject outputObject) {
        orderItemService.changeOrderItemAdjustPrice(inputObject, outputObject);
    }

    @ApiOperation(id = "selectOrderItemById", value = "根据id查询订单子单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OrderItemController/selectOrderItemById")
    public void selectOrderItemById(InputObject inputObject, OutputObject outputObject) {
        orderItemService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "signOrderItem", value = "签收订单子单", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "orderId", name = "orderId", value = "订单总单id", required = "required"),
        @ApiImplicitParam(id = "itemId", name = "itemId", value = "订单子单id", required = "required")})
    @RequestMapping("/post/OrderItemController/signOrderItem")
    public void signOrderItem(InputObject inputObject, OutputObject outputObject) {
        orderItemService.signOrderItem(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMaterialStoreDailySales", value = "商品按门店日销量统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/OrderItemController/queryMaterialStoreDailySales")
    public void queryMaterialStoreDailySales(InputObject inputObject, OutputObject outputObject) {
        orderItemService.queryMaterialStoreDailySales(inputObject, outputObject);
    }
}
