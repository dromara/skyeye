package com.skyeye.order.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.order.entity.ItemDeliverHistory;
import com.skyeye.order.entity.OrderItem;

import java.util.List;

/**
 * @ClassName: ItemDeliverHistoryService
 * @Description: 商品订单子单项快递信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ItemDeliverHistoryService extends SkyeyeBusinessService<ItemDeliverHistory> {
    void queryMyItemDeliverHistoryPageList(InputObject inputObject, OutputObject outputObject);

    void insertEntity(OrderItem orderItem, String deliverNumber, String deliveryTemplateChargeId, String deliveryCompanyId, String num);

    List<ItemDeliverHistory> queryListByItemId(String itemId);

    List<ItemDeliverHistory> queryListByItemId(List<String> itemIdList);
}
