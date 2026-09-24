/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.shop.entity.ShopStoreInventoryCheck;
import com.skyeye.shop.entity.StoreInventoryCheckItem;

import java.util.List;

public interface ShopStoreInventoryCheckService extends SkyeyeBusinessService<ShopStoreInventoryCheck> {

    /**
     * 保存盘点历史（确认盘点时调用）
     */
    void saveInventoryCheckHistory(String storeId, List<StoreInventoryCheckItem> itemList);

    /**
     * 盘点历史列表
     */
    void queryStoreInventoryCheckHistoryList(InputObject inputObject, OutputObject outputObject);

    /**
     * 盘点历史详情
     */
    void queryStoreInventoryCheckHistoryDetail(InputObject inputObject, OutputObject outputObject);
}
