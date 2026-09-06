/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.schedule;

import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.server.service.AutoServerMetricHistoryService;
import com.skyeye.server.service.AutoServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时 SSH 采集全部已启用资源监控的服务器。
 * 默认每 2 分钟一次，可用 skyeye.auto.server.metric-cron 覆盖。
 * 开启租户时设置平台租户上下文，避免「租户ID不能为空」。
 */
@Slf4j
@Component
public class AutoServerMetricSchedule {

    @Autowired
    private AutoServerService autoServerService;

    @Autowired
    private AutoServerMetricHistoryService autoServerMetricHistoryService;

    @Value("${skyeye.tenant.enable:false}")
    private boolean tenantEnable;

    @Scheduled(cron = "${skyeye.auto.server.metric-cron:0 */2 * * * ?}")
    public void collectMetrics() {
        try {
            if (tenantEnable) {
                TenantContext.setTenantId(TenantTypeEnum.PLATFORM.getCode());
            }
            int count = autoServerService.collectAllEnabledServerMetrics();
            if (count > 0) {
                log.info("服务器资源定时采集完成，处理 {} 台", count);
            }
        } catch (Exception e) {
            log.warn("服务器资源定时采集失败", e);
        } finally {
            if (tenantEnable) {
                TenantContext.clear();
            }
        }
    }

    /** 每天凌晨 3 点清理 30 天前历史 */
    @Scheduled(cron = "${skyeye.auto.server.metric-clean-cron:0 0 3 * * ?}")
    public void cleanHistory() {
        try {
            if (tenantEnable) {
                TenantContext.setTenantId(TenantTypeEnum.PLATFORM.getCode());
            }
            autoServerMetricHistoryService.cleanExpiredHistory(30);
        } catch (Exception e) {
            log.warn("清理服务器采集历史失败", e);
        } finally {
            if (tenantEnable) {
                TenantContext.clear();
            }
        }
    }
}
