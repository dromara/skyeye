package com.skyeye.finance.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.finance.entity.ShopStoreWithdraw;

import java.util.List;

public interface ShopStoreWithdrawService extends SkyeyeBusinessService<ShopStoreWithdraw> {

    /**
     * 管理端分页：联表门店/会员，按门店名、户名、卡号、开户行、申请人姓名/手机号模糊查
     */
    List<ShopStoreWithdraw> queryAdminPageList(CommonPageInfo pageInfo);
}
