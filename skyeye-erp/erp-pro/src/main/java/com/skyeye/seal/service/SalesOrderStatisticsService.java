/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.seal.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 销售订单统计：现查现算，不落统计表。口径均为「已签署」订单（审核通过及之后）。
 */
public interface SalesOrderStatisticsService {

    /**
     * 已签署订单金额合计。全量，不按时间筛选。
     */
    void querySalesOrderTotalPrice(InputObject inputObject, OutputObject outputObject);

    /**
     * 签单客户数：已签署订单的客户 id 去重。同一客户多单只算 1。全量，不按时间筛选。
     */
    void querySalesOrderCustomerCount(InputObject inputObject, OutputObject outputObject);

    /**
     * 签署数量：已签署订单张数。全量，不按时间筛选。
     */
    void querySalesOrderSignCount(InputObject inputObject, OutputObject outputObject);

    /**
     * 按单据日期的日度趋势（当天单量 + 当天金额）。未传日期默认近 30 天。
     */
    void querySalesOrderTrend(InputObject inputObject, OutputObject outputObject);
}
