/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.order.entity.Order;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: OrderService
 * @Description: 商品订单管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface OrderService extends SkyeyeBusinessService<Order> {
    void cancelOrder(InputObject inputObject, OutputObject outputObject);

    void finishOrder(InputObject inputObject, OutputObject outputObject);

    void payOrder(InputObject inputObject, OutputObject outputObject);

    /**
     * 商城订单支付成功业务回调（PayApp.orderNotifyUrl 指向本接口，由 promote PayNotify 转发）
     */
    void notifyOrderPaySuccess(InputObject inputObject, OutputObject outputObject);

    void updateCommonState(String id, Integer state);

    void queryOrderPageList(InputObject inputObject, OutputObject outputObject);

    void generatePayOrderRrCode(InputObject inputObject, OutputObject outputObject);

    void updateOrderToPayState(InputObject inputObject, OutputObject outputObject);

    void setOrderCancle(String orderId);

    List<Order> queryOrderList(String orderId);

    void updateByAddressId(Map<String, String> map);

    void changeOrderAddress(InputObject inputObject, OutputObject outputObject);

    void changeAdjustPriceById(String id, String interpolation);
}
