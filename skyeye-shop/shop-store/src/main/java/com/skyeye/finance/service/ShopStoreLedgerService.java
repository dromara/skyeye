package com.skyeye.finance.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.finance.entity.ShopStoreLedger;

public interface ShopStoreLedgerService extends SkyeyeBusinessService<ShopStoreLedger> {

    boolean existsByBiz(Integer bizType, String bizId);
}
