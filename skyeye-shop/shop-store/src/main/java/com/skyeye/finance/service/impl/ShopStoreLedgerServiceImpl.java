package com.skyeye.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.finance.dao.ShopStoreLedgerDao;
import com.skyeye.finance.entity.ShopStoreLedger;
import com.skyeye.finance.service.ShopStoreLedgerService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "个人门店资金流水", groupName = "个人门店资金", tenant = TenantEnum.NO_ISOLATION)
public class ShopStoreLedgerServiceImpl extends SkyeyeBusinessServiceImpl<ShopStoreLedgerDao, ShopStoreLedger> implements ShopStoreLedgerService {

    @Override
    public boolean existsByBiz(Integer bizType, String bizId) {
        QueryWrapper<ShopStoreLedger> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(ShopStoreLedger::getBizType), bizType)
            .eq(MybatisPlusUtil.toColumns(ShopStoreLedger::getBizId), bizId);
        return count(qw) > 0;
    }
}
