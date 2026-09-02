/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.tenant.entity.TenantTokenDailyUsage;

import java.util.List;

public interface TenantTokenDailyUsageService extends SkyeyeBusinessService<TenantTokenDailyUsage> {

    void addUsage(String tenantId, String usageDate, long promptTokens, long completionTokens, long totalTokens);

    List<TenantTokenDailyUsage> queryByTenantAndDateRange(String tenantId, String startDate, String endDate);

}
