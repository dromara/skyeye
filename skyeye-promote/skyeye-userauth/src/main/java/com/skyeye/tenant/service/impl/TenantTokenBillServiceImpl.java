/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.tenant.classenum.TenantTokenBillState;
import com.skyeye.tenant.dao.TenantTokenBillDao;
import com.skyeye.tenant.entity.TenantTokenBill;
import com.skyeye.tenant.service.TenantTokenBillService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@SkyeyeService(name = "租户Token月结账单", groupName = "租户管理", manageShow = false, tenant = TenantEnum.PLATE)
public class TenantTokenBillServiceImpl extends SkyeyeBusinessServiceImpl<TenantTokenBillDao, TenantTokenBill> implements TenantTokenBillService {

    @Override
    @IgnoreTenant
    public TenantTokenBill selectById(String id) {
        return super.selectById(id);
    }

    @Override
    @IgnoreTenant
    public boolean existsByTenantAndPeriod(String tenantId, String billPeriod) {
        QueryWrapper<TenantTokenBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenBill::getTenantId), tenantId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenBill::getBillPeriod), billPeriod);
        return count(queryWrapper) > 0;
    }

    @Override
    @IgnoreTenant
    public boolean hasUnpaidBills(String tenantId) {
        return countUnpaidBills(tenantId) > 0;
    }

    @Override
    @IgnoreTenant
    public long countUnpaidBills(String tenantId) {
        if (StrUtil.isBlank(tenantId)) {
            return 0L;
        }
        QueryWrapper<TenantTokenBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenBill::getTenantId), tenantId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenBill::getState), TenantTokenBillState.SETTLED.getKey());
        List<TenantTokenBill> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return 0L;
        }
        return list.stream().filter(this::isPayableAmount).count();
    }

    @Override
    @IgnoreTenant
    public void markPaid(String billId, String payOrderId) {
        if (StrUtil.isBlank(billId)) {
            return;
        }
        UpdateWrapper<TenantTokenBill> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, billId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenBill::getState), TenantTokenBillState.SETTLED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantTokenBill::getState), TenantTokenBillState.PAID.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantTokenBill::getPayTime), DateUtil.getTimeAndToString());
        if (StrUtil.isNotBlank(payOrderId)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(TenantTokenBill::getPayOrderId), payOrderId);
        }
        update(updateWrapper);
        refreshCache(billId);
    }

    @Override
    @IgnoreTenant
    public void markPaidByPayOrderId(String payOrderId) {
        if (StrUtil.isBlank(payOrderId)) {
            return;
        }
        QueryWrapper<TenantTokenBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenBill::getPayOrderId), payOrderId);
        TenantTokenBill bill = getOne(queryWrapper, false);
        if (bill == null || StrUtil.isBlank(bill.getId())) {
            return;
        }
        markPaid(bill.getId(), payOrderId);
    }

    @Override
    @IgnoreTenant
    public void bindPayOrderId(String billId, String payOrderId) {
        if (StrUtil.isBlank(billId) || StrUtil.isBlank(payOrderId)) {
            return;
        }
        UpdateWrapper<TenantTokenBill> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, billId);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantTokenBill::getPayOrderId), payOrderId);
        update(updateWrapper);
        refreshCache(billId);
    }

    @Override
    public boolean isPayableAmount(TenantTokenBill bill) {
        if (bill == null || StrUtil.isBlank(bill.getAmount())) {
            return false;
        }
        try {
            return new BigDecimal(bill.getAmount()).compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception e) {
            return false;
        }
    }

}
