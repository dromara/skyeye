/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.shop.list.service;

import java.util.Map;

/**
 * 商城订单列表（买家「我的订单」），分页只读。
 */
public interface IShopListService {

    /**
     * @param type waitPay / waitDeliver / waitReceive / waitReview / afterSale / all
     * @param page 从 1 开始
     * @param limit 上限由调用方裁剪
     * @return total + items（已精简字段）
     */
    Map<String, Object> queryMyOrderList(String type, int page, int limit);
}
