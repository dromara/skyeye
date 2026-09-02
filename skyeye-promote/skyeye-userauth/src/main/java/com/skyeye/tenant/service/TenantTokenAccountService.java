/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.entity.TenantTokenAccount;

public interface TenantTokenAccountService extends SkyeyeBusinessService<TenantTokenAccount> {

    TenantTokenAccount getOrCreateByTenantId(String tenantId);

    void creditTokens(String tenantId, long tokenQty);

    void queryCurrentTenantTokenAccount(InputObject inputObject, OutputObject outputObject);

    void saveCurrentTenantTokenMode(InputObject inputObject, OutputObject outputObject);

    void checkTenantTokenAllowUse(InputObject inputObject, OutputObject outputObject);

    void recordTenantTokenUsage(InputObject inputObject, OutputObject outputObject);

    void queryCurrentTenantTokenDailyUsage(InputObject inputObject, OutputObject outputObject);

    void queryCurrentTenantTokenBillList(InputObject inputObject, OutputObject outputObject);

    void queryPlatformTenantTokenAccountList(InputObject inputObject, OutputObject outputObject);

    void queryPlatformTenantTokenDailyUsage(InputObject inputObject, OutputObject outputObject);

    void queryPlatformTenantTokenBillList(InputObject inputObject, OutputObject outputObject);

    void settlePaygBills();

}
