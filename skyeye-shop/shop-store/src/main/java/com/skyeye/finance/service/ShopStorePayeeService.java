package com.skyeye.finance.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.finance.entity.ShopStorePayee;

public interface ShopStorePayeeService extends SkyeyeBusinessService<ShopStorePayee> {

    /**
     * 按门店查询默认收款账户（无则 null）
     */
    ShopStorePayee getByStoreId(String storeId);

    /**
     * 保存/更新门店默认收款账户（每店一条）
     */
    ShopStorePayee upsertByStoreId(String storeId, String accountName, String accountNo, String bankName, String userId);
}
