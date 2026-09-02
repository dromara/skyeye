package com.skyeye.tenant.task;

import com.skyeye.tenant.service.TenantTokenAccountService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 每月 15 号结算按量计费租户的 Token 账单。
 * 请在 XXL-JOB 配置 cron：0 0 1 15 * ?
 */
@Slf4j
@Component
public class TenantTokenSettleTask {

    @Autowired
    private TenantTokenAccountService tenantTokenAccountService;

    @XxlJob("tenantTokenSettleTask")
    public void settlePaygBills() {
        log.info("开始执行租户 Token 月结任务");
        tenantTokenAccountService.settlePaygBills();
        log.info("租户 Token 月结任务结束");
    }

}
