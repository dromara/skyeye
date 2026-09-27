package com.skyeye.finance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.entity.Member;
import com.skyeye.finance.dao.ShopStoreWithdrawDao;
import com.skyeye.finance.entity.ShopStoreWithdraw;
import com.skyeye.finance.service.ShopStoreWithdrawService;
import com.skyeye.store.entity.ShopStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "个人门店提现申请", groupName = "个人门店资金", tenant = TenantEnum.NO_ISOLATION)
public class ShopStoreWithdrawServiceImpl extends SkyeyeBusinessServiceImpl<ShopStoreWithdrawDao, ShopStoreWithdraw>
    implements ShopStoreWithdrawService {

    @Override
    public List<ShopStoreWithdraw> queryAdminPageList(CommonPageInfo pageInfo) {
        MPJLambdaWrapper<ShopStoreWithdraw> wrapper = JoinWrappers.lambda("w", ShopStoreWithdraw.class)
            .selectAll(ShopStoreWithdraw.class)
            .leftJoin(ShopStore.class, "s", ShopStore::getId, ShopStoreWithdraw::getStoreId)
            .leftJoin(Member.class, "m", Member::getId, ShopStoreWithdraw::getMemberId);
        if (StrUtil.isNotBlank(pageInfo.getState())) {
            wrapper.eq(ShopStoreWithdraw::getState, Integer.parseInt(pageInfo.getState()));
        }
        if (StrUtil.isNotBlank(pageInfo.getKeyword())) {
            String kw = pageInfo.getKeyword().trim();
            wrapper.and(w -> w.like(ShopStoreWithdraw::getAccountName, kw)
                .or().like(ShopStoreWithdraw::getAccountNo, kw)
                .or().like(ShopStoreWithdraw::getBankName, kw)
                .or().like(ShopStore::getName, kw)
                .or().like(Member::getName, kw)
                .or().like(Member::getRealName, kw)
                .or().like(Member::getPhone, kw));
        }
        wrapper.orderByDesc(ShopStoreWithdraw::getCreateTime);
        return skyeyeBaseMapper.selectJoinList(ShopStoreWithdraw.class, wrapper);
    }
}
