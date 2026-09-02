/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.tenant.classenum.TenantTokenBillState;
import com.skyeye.tenant.classenum.TenantTokenBillingMode;
import com.skyeye.tenant.dao.TenantTokenAccountDao;
import com.skyeye.tenant.entity.TenantTokenAccount;
import com.skyeye.tenant.entity.TenantTokenBill;
import com.skyeye.tenant.entity.TenantTokenDailyUsage;
import com.skyeye.tenant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "租户Token账户", groupName = "租户管理", tenant = TenantEnum.PLATE)
public class TenantTokenAccountServiceImpl extends SkyeyeBusinessServiceImpl<TenantTokenAccountDao, TenantTokenAccount> implements TenantTokenAccountService {

    private static final int SETTLE_DAY = 15;

    @Value("${skyeye.tenant.enable:false}")
    private boolean tenantEnable;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private PlatformBaseSettingService platformBaseSettingService;

    @Autowired
    private TenantTokenDailyUsageService tenantTokenDailyUsageService;

    @Autowired
    private TenantTokenBillService tenantTokenBillService;

    @Override
    @IgnoreTenant
    public TenantTokenAccount getOrCreateByTenantId(String tenantId) {
        if (StrUtil.isBlank(tenantId)) {
            throw new CustomException("租户id不能为空");
        }
        QueryWrapper<TenantTokenAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenAccount::getTenantId), tenantId);
        TenantTokenAccount account = getOne(queryWrapper, false);
        if (account != null && StrUtil.isNotBlank(account.getId())) {
            fillDefault(account);
            return account;
        }
        account = new TenantTokenAccount();
        account.setTenantId(tenantId);
        account.setBillingMode(TenantTokenBillingMode.NONE.getKey());
        account.setTokenBalance(0L);
        account.setTokenUsed(0L);
        account.setStopped(WhetherEnum.DISABLE_USING.getKey());
        createEntity(account, StrUtil.EMPTY);
        return getOrCreateByTenantId(tenantId);
    }

    private void fillDefault(TenantTokenAccount account) {
        if (account.getBillingMode() == null) {
            account.setBillingMode(TenantTokenBillingMode.NONE.getKey());
        }
        if (account.getTokenBalance() == null) {
            account.setTokenBalance(0L);
        }
        if (account.getTokenUsed() == null) {
            account.setTokenUsed(0L);
        }
        if (account.getStopped() == null) {
            account.setStopped(WhetherEnum.DISABLE_USING.getKey());
        }
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void creditTokens(String tenantId, long tokenQty) {
        if (tokenQty <= 0) {
            return;
        }
        TenantTokenAccount account = getOrCreateByTenantId(tenantId);
        UpdateWrapper<TenantTokenAccount> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, account.getId());
        updateWrapper.setSql("token_balance = IFNULL(token_balance, 0) + " + tokenQty);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantTokenAccount::getStopped), WhetherEnum.DISABLE_USING.getKey());
        if (TenantTokenBillingMode.NONE.getKey().equals(account.getBillingMode())
            || TenantTokenBillingMode.PREPAID.getKey().equals(account.getBillingMode())) {
            updateWrapper.set(MybatisPlusUtil.toColumns(TenantTokenAccount::getBillingMode), TenantTokenBillingMode.PREPAID.getKey());
        }
        update(updateWrapper);
        refreshCache(account.getId());
    }

    @Override
    @IgnoreTenant
    public void queryCurrentTenantTokenAccount(InputObject inputObject, OutputObject outputObject) {
        String tenantId = requireCurrentTenantId();
        TenantTokenAccount account = getOrCreateByTenantId(tenantId);
        healPlatformAccount(account);
        account = getOrCreateByTenantId(tenantId);
        applyPlatformView(account);
        fillUnpaidBillCount(account);
        tenantService.setDataMation(account, TenantTokenAccount::getTenantId);
        outputObject.setBean(account);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void saveCurrentTenantTokenMode(InputObject inputObject, OutputObject outputObject) {
        String tenantId = requireCurrentTenantId();
        if (skipBilling(tenantId)) {
            throw new CustomException("平台租户无需设置 Token 计费方式");
        }
        Integer billingMode = Integer.parseInt(inputObject.getParams().get("billingMode").toString());
        if (!TenantTokenBillingMode.PAYG.getKey().equals(billingMode)
            && !TenantTokenBillingMode.PREPAID.getKey().equals(billingMode)
            && !TenantTokenBillingMode.NONE.getKey().equals(billingMode)) {
            throw new CustomException("计费方式无效");
        }
        TenantTokenAccount account = getOrCreateByTenantId(tenantId);
        UpdateWrapper<TenantTokenAccount> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, account.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantTokenAccount::getBillingMode), billingMode);
        long balance = account.getTokenBalance() == null ? 0L : account.getTokenBalance();
        if (TenantTokenBillingMode.PREPAID.getKey().equals(billingMode) && balance <= 0) {
            updateWrapper.set(MybatisPlusUtil.toColumns(TenantTokenAccount::getStopped), WhetherEnum.ENABLE_USING.getKey());
        } else {
            updateWrapper.set(MybatisPlusUtil.toColumns(TenantTokenAccount::getStopped), WhetherEnum.DISABLE_USING.getKey());
        }
        update(updateWrapper);
        refreshCache(account.getId());
        outputObject.setBean(getOrCreateByTenantId(tenantId));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @IgnoreTenant
    public void checkTenantTokenAllowUse(InputObject inputObject, OutputObject outputObject) {
        String tenantId = StrUtil.blankToDefault(String.valueOf(inputObject.getParams().get("tenantId")), TenantContext.getTenantId());
        assertAllowUse(tenantId);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void recordTenantTokenUsage(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String tenantId = StrUtil.blankToDefault(String.valueOf(params.get("tenantId")), TenantContext.getTenantId());
        if (skipBilling(tenantId)) {
            return;
        }
        TenantTokenAccount account = getOrCreateByTenantId(tenantId);
        if (TenantTokenBillingMode.NONE.getKey().equals(account.getBillingMode())) {
            return;
        }
        long promptTokens = parseLong(params.get("promptTokens"));
        long completionTokens = parseLong(params.get("completionTokens"));
        long totalTokens = parseLong(params.get("totalTokens"));
        if (totalTokens <= 0) {
            totalTokens = promptTokens + completionTokens;
        }
        if (totalTokens <= 0) {
            return;
        }
        String usageDate = DateUtil.getYmdTimeAndToString();
        tenantTokenDailyUsageService.addUsage(tenantId, usageDate, promptTokens, completionTokens, totalTokens);

        UpdateWrapper<TenantTokenAccount> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, account.getId());
        updateWrapper.setSql("token_used = IFNULL(token_used, 0) + " + totalTokens);
        if (TenantTokenBillingMode.PREPAID.getKey().equals(account.getBillingMode())) {
            updateWrapper.setSql("token_balance = GREATEST(IFNULL(token_balance, 0) - " + totalTokens + ", 0)");
            long remain = Math.max(0L, (account.getTokenBalance() == null ? 0L : account.getTokenBalance()) - totalTokens);
            if (remain <= 0) {
                updateWrapper.set(MybatisPlusUtil.toColumns(TenantTokenAccount::getStopped), WhetherEnum.ENABLE_USING.getKey());
            }
        }
        update(updateWrapper);
        refreshCache(account.getId());
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @IgnoreTenant
    public void queryCurrentTenantTokenDailyUsage(InputObject inputObject, OutputObject outputObject) {
        String tenantId = requireCurrentTenantId();
        fillDailyUsageOutput(inputObject, outputObject, tenantId);
    }

    @Override
    @IgnoreTenant
    public void queryCurrentTenantTokenBillList(InputObject inputObject, OutputObject outputObject) {
        String tenantId = requireCurrentTenantId();
        fillBillOutput(inputObject, outputObject, tenantId);
    }

    @Override
    @IgnoreTenant
    public void queryPlatformTenantTokenAccountList(InputObject inputObject, OutputObject outputObject) {
        assertPlatformTenant();
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        QueryWrapper<TenantTokenAccount> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(pageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenAccount::getTenantId), pageInfo.getObjectId());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(TenantTokenAccount::getLastUpdateTime));
        List<TenantTokenAccount> list = list(queryWrapper);
        list.forEach(item -> {
            applyPlatformView(item);
            fillUnpaidBillCount(item);
        });
        tenantService.setDataMation(list, TenantTokenAccount::getTenantId);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    @IgnoreTenant
    public void queryPlatformTenantTokenDailyUsage(InputObject inputObject, OutputObject outputObject) {
        assertPlatformTenant();
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String tenantId = StrUtil.blankToDefault(inputObject.getParams().get("tenantId") == null ? "" : inputObject.getParams().get("tenantId").toString(),
            pageInfo.getObjectId());
        if (StrUtil.isBlank(tenantId)) {
            throw new CustomException("请选择租户");
        }
        fillDailyUsageOutput(inputObject, outputObject, tenantId);
    }

    @Override
    @IgnoreTenant
    public void queryPlatformTenantTokenBillList(InputObject inputObject, OutputObject outputObject) {
        assertPlatformTenant();
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String tenantId = StrUtil.blankToDefault(inputObject.getParams().get("tenantId") == null ? "" : inputObject.getParams().get("tenantId").toString(),
            pageInfo.getObjectId());
        fillBillOutput(inputObject, outputObject, tenantId);
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void settlePaygBills() {
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;
        if (today.getDayOfMonth() >= SETTLE_DAY) {
            startDate = today.minusMonths(1).withDayOfMonth(SETTLE_DAY);
            endDate = today.withDayOfMonth(SETTLE_DAY).minusDays(1);
        } else {
            startDate = today.minusMonths(2).withDayOfMonth(SETTLE_DAY);
            endDate = today.minusMonths(1).withDayOfMonth(SETTLE_DAY).minusDays(1);
        }
        String start = startDate.toString();
        String end = endDate.toString();
        String billPeriod = start + "~" + end;
        String tokensPerYuan = platformBaseSettingService.getTokensPerYuan();
        QueryWrapper<TenantTokenAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenAccount::getBillingMode), TenantTokenBillingMode.PAYG.getKey());
        List<TenantTokenAccount> accounts = list(queryWrapper);
        if (CollectionUtil.isEmpty(accounts)) {
            return;
        }
        for (TenantTokenAccount account : accounts) {
            if (tenantTokenBillService.existsByTenantAndPeriod(account.getTenantId(), billPeriod)) {
                continue;
            }
            List<TenantTokenDailyUsage> usages = tenantTokenDailyUsageService.queryByTenantAndDateRange(account.getTenantId(), start, end);
            long totalTokens = 0L;
            if (CollectionUtil.isNotEmpty(usages)) {
                totalTokens = usages.stream().mapToLong(item -> item.getTotalTokens() == null ? 0L : item.getTotalTokens()).sum();
            }
            TenantTokenBill bill = new TenantTokenBill();
            bill.setTenantId(account.getTenantId());
            bill.setBillPeriod(billPeriod);
            bill.setStartDate(start);
            bill.setEndDate(end);
            bill.setTotalTokens(totalTokens);
            bill.setTokensPerYuan(tokensPerYuan);
            bill.setAmount(calcAmount(totalTokens, tokensPerYuan));
            bill.setSettleTime(DateUtil.getTimeAndToString());
            if (new BigDecimal(bill.getAmount()).compareTo(BigDecimal.ZERO) <= 0) {
                bill.setState(TenantTokenBillState.PAID.getKey());
                bill.setPayTime(bill.getSettleTime());
            } else {
                bill.setState(TenantTokenBillState.SETTLED.getKey());
            }
            tenantTokenBillService.createEntity(bill, StrUtil.EMPTY);
        }
    }

    private void fillDailyUsageOutput(InputObject inputObject, OutputObject outputObject, String tenantId) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String startDate = pageInfo.getStartTime();
        String endDate = pageInfo.getEndTime();
        Page page = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<TenantTokenDailyUsage> list = tenantTokenDailyUsageService.queryByTenantAndDateRange(tenantId, startDate, endDate);
        tenantService.setDataMation(list, TenantTokenDailyUsage::getTenantId);
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    private void fillBillOutput(InputObject inputObject, OutputObject outputObject, String tenantId) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        QueryWrapper<TenantTokenBill> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(tenantId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenBill::getTenantId), tenantId);
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(TenantTokenBill::getSettleTime));
        Page page = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<TenantTokenBill> list = tenantTokenBillService.list(queryWrapper);
        tenantService.setDataMation(list, TenantTokenBill::getTenantId);
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    private void assertAllowUse(String tenantId) {
        if (skipBilling(tenantId)) {
            return;
        }
        if (tenantTokenBillService.hasUnpaidBills(tenantId)) {
            throw new CustomException("存在未结清的 Token 月结账单，请到组织信息管理 → Token用量 结清后再使用 AI");
        }
        TenantTokenAccount account = getOrCreateByTenantId(tenantId);
        if (TenantTokenBillingMode.NONE.getKey().equals(account.getBillingMode())
            || TenantTokenBillingMode.PAYG.getKey().equals(account.getBillingMode())) {
            return;
        }
        if (WhetherEnum.ENABLE_USING.getKey().equals(account.getStopped())
            || account.getTokenBalance() == null || account.getTokenBalance() <= 0) {
            throw new CustomException("Token 余额已用完，请先购买后再使用 AI 功能");
        }
    }

    private void healPlatformAccount(TenantTokenAccount account) {
        if (account == null || !skipBilling(account.getTenantId())) {
            return;
        }
        boolean needHeal = !TenantTokenBillingMode.NONE.getKey().equals(account.getBillingMode())
            || WhetherEnum.ENABLE_USING.getKey().equals(account.getStopped());
        if (!needHeal) {
            return;
        }
        UpdateWrapper<TenantTokenAccount> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, account.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantTokenAccount::getBillingMode), TenantTokenBillingMode.NONE.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantTokenAccount::getStopped), WhetherEnum.DISABLE_USING.getKey());
        update(updateWrapper);
        refreshCache(account.getId());
    }

    private void applyPlatformView(TenantTokenAccount account) {
        if (account == null || !skipBilling(account.getTenantId())) {
            return;
        }
        account.setBillingMode(TenantTokenBillingMode.NONE.getKey());
        account.setStopped(WhetherEnum.DISABLE_USING.getKey());
        account.setUnpaidBillCount(0L);
    }

    private void fillUnpaidBillCount(TenantTokenAccount account) {
        if (account == null || skipBilling(account.getTenantId())) {
            if (account != null) {
                account.setUnpaidBillCount(0L);
            }
            return;
        }
        account.setUnpaidBillCount(tenantTokenBillService.countUnpaidBills(account.getTenantId()));
    }

    private boolean skipBilling(String tenantId) {
        if (!tenantEnable || StrUtil.isBlank(tenantId) || "null".equals(tenantId)) {
            return true;
        }
        return StrUtil.equals(tenantId, TenantTypeEnum.PLATFORM.getCode());
    }

    private String requireCurrentTenantId() {
        String tenantId = TenantContext.getTenantId();
        if (StrUtil.isBlank(tenantId)) {
            throw new CustomException("未获取到当前租户信息");
        }
        return tenantId;
    }

    private void assertPlatformTenant() {
        if (!tenantEnable) {
            throw new CustomException("租户功能未开启");
        }
        if (!StrUtil.equals(TenantContext.getTenantId(), TenantTypeEnum.PLATFORM.getCode())) {
            throw new CustomException("非平台租户不能访问");
        }
    }

    private long parseLong(Object value) {
        if (ObjectUtil.isEmpty(value) || StrUtil.isBlank(value.toString()) || "null".equals(value.toString())) {
            return 0L;
        }
        return NumberUtil.parseLong(value.toString());
    }

    private String calcAmount(long totalTokens, String tokensPerYuan) {
        BigDecimal rate = NumberUtil.toBigDecimal(tokensPerYuan);
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            return "0.00";
        }
        return new BigDecimal(totalTokens).divide(rate, CommonNumConstants.NUM_TWO, RoundingMode.HALF_UP).toPlainString();
    }

}
