/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.dao;

import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.order.entity.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: OrderItemDao
 * @Description: 订单评论数据持久层
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface OrderItemDao extends SkyeyeBaseMapper<OrderItem> {

    /**
     * 商品按门店日销量汇总（已支付订单）
     */
    @Select("SELECT oi.store_id AS storeId, DATE(o.pay_time) AS saleDay, " +
        "SUM(oi.count) AS saleCount, SUM(CAST(oi.pay_price AS DECIMAL(18, 2))) AS saleAmountFen " +
        "FROM shop_order_item oi " +
        "INNER JOIN shop_order o ON o.id = oi.parent_id " +
        "WHERE oi.material_id = #{materialId} " +
        "AND o.state = 3 " +
        "AND o.pay_time IS NOT NULL AND o.pay_time <> '' " +
        "AND o.pay_time >= #{startDate} AND o.pay_time <= #{endDate} " +
        "GROUP BY oi.store_id, DATE(o.pay_time) " +
        "ORDER BY saleDay DESC, storeId ASC")
    List<Map<String, Object>> queryMaterialStoreDailySales(@Param("materialId") String materialId,
                                                           @Param("startDate") String startDate,
                                                           @Param("endDate") String endDate);

}
