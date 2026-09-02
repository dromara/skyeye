/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.tenant.dao.TenantTokenDailyUsageDao;
import com.skyeye.tenant.entity.TenantTokenDailyUsage;
import com.skyeye.tenant.service.TenantTokenDailyUsageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SkyeyeService(name = "租户Token日用量", groupName = "租户管理", manageShow = false, tenant = TenantEnum.PLATE)
public class TenantTokenDailyUsageServiceImpl extends SkyeyeBusinessServiceImpl<TenantTokenDailyUsageDao, TenantTokenDailyUsage> implements TenantTokenDailyUsageService {

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void addUsage(String tenantId, String usageDate, long promptTokens, long completionTokens, long totalTokens) {
        QueryWrapper<TenantTokenDailyUsage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenDailyUsage::getTenantId), tenantId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenDailyUsage::getUsageDate), usageDate);
        TenantTokenDailyUsage existing = getOne(queryWrapper, false);
        if (existing == null || StrUtil.isBlank(existing.getId())) {
            TenantTokenDailyUsage usage = new TenantTokenDailyUsage();
            usage.setId(ToolUtil.getSurFaceId());
            usage.setTenantId(tenantId);
            usage.setUsageDate(usageDate);
            usage.setPromptTokens(promptTokens);
            usage.setCompletionTokens(completionTokens);
            usage.setTotalTokens(totalTokens);
            usage.setCallCount(CommonNumConstants.NUM_ONE);
            save(usage);
            return;
        }
        UpdateWrapper<TenantTokenDailyUsage> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenDailyUsage::getId), existing.getId());
        updateWrapper.setSql("prompt_tokens = IFNULL(prompt_tokens, 0) + " + promptTokens);
        updateWrapper.setSql("completion_tokens = IFNULL(completion_tokens, 0) + " + completionTokens);
        updateWrapper.setSql("total_tokens = IFNULL(total_tokens, 0) + " + totalTokens);
        updateWrapper.setSql("call_count = IFNULL(call_count, 0) + 1");
        update(updateWrapper);
    }

    @Override
    @IgnoreTenant
    public List<TenantTokenDailyUsage> queryByTenantAndDateRange(String tenantId, String startDate, String endDate) {
        QueryWrapper<TenantTokenDailyUsage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantTokenDailyUsage::getTenantId), tenantId);
        if (StrUtil.isNotBlank(startDate)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(TenantTokenDailyUsage::getUsageDate), startDate);
        }
        if (StrUtil.isNotBlank(endDate)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(TenantTokenDailyUsage::getUsageDate), endDate);
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(TenantTokenDailyUsage::getUsageDate));
        return list(queryWrapper);
    }

}
