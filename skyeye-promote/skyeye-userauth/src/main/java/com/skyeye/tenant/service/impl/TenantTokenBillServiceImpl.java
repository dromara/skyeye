/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.tenant.dao.TenantTokenBillDao;
import com.skyeye.tenant.entity.TenantTokenBill;
import com.skyeye.tenant.service.TenantTokenBillService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "租户Token月结账单", groupName = "租户管理", manageShow = false, tenant = TenantEnum.PLATE)
public class TenantTokenBillServiceImpl extends SkyeyeBusinessServiceImpl<TenantTokenBillDao, TenantTokenBill> implements TenantTokenBillService {

    @Override
    @IgnoreTenant
    public boolean existsByTenantAndPeriod(String tenantId, String billPeriod) {
        QueryWrapper<TenantTokenBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenBill::getTenantId), tenantId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenBill::getBillPeriod), billPeriod);
        return count(queryWrapper) > 0;
    }

}
