/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.order.entity.Order;
import com.skyeye.order.entity.OrderItem;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: OrderItemService
 * @Description: 商品订单评论管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface OrderItemService extends SkyeyeBusinessService<OrderItem> {

    void deleteByPerentIds(List<String> ids);

    List<OrderItem> queryListByStateAndOrderId(String orderId, Integer state);

    Map<String, List<OrderItem>> queryListByParentId(List<String> idList);

    void setValueAndCreateEntity(Order order, String userId);

    void updateCommentStateById(String id);

    List<OrderItem> queryOrderItemByParentId(String orderId);

    void deliverGoodsById(InputObject inputObject, OutputObject outputObject);

    void updateDeliverStateByParentId(String parentId, Integer state);

    void changeOrderItemAdjustPrice(InputObject inputObject, OutputObject outputObject);

    void signOrderItem(InputObject inputObject, OutputObject outputObject);

    /**
     * 补全子单展示信息
     */
    List<OrderItem> setDateForItemLIst(List<OrderItem> list);
}
