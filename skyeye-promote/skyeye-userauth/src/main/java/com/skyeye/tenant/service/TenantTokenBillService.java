/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.tenant.entity.TenantTokenBill;

public interface TenantTokenBillService extends SkyeyeBusinessService<TenantTokenBill> {

    boolean existsByTenantAndPeriod(String tenantId, String billPeriod);

    boolean hasUnpaidBills(String tenantId);

    long countUnpaidBills(String tenantId);

    void markPaid(String billId, String payOrderId);

    void markPaidByPayOrderId(String payOrderId);

    void bindPayOrderId(String billId, String payOrderId);

    boolean isPayableAmount(TenantTokenBill bill);

}
