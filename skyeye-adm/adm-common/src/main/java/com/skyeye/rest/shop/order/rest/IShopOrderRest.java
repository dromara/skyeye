/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.shop.order.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 商城订单统计（与买家「我的订单」角标、门店经营概览同口径）。
 */
@FeignClient(value = "${webroot.skyeye-shop}", configuration = ClientConfiguration.class)
public interface IShopOrderRest {

    /** 当前用户作为买家的订单待办笔数 */
    @PostMapping("/queryMyOrderStat")
    String queryMyOrderStat(@RequestBody Map<String, Object> params);

    /**
     * 门店订单经营统计。params.objectId = 门店 id。
     */
    @PostMapping("/queryStoreOrderStat")
    String queryStoreOrderStat(@RequestBody Map<String, Object> params);

    /**
     * 买家「我的订单」分页列表。params：page/limit/type(1待支付~6售后，空=全部)/keyword。
     */
    @PostMapping("/queryOrderPageList")
    String queryOrderPageList(@RequestBody Map<String, Object> params);
}
