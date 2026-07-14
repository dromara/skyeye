/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.xxljob;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.maintenance.service.MaintenancePlanOrderSyncService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 由 SysQuartz 按保养计划注册的 XXL 子任务：根据 objectId(计划 id) 自动生成设备保养单。
 * 与巡检 {@code PatrolPlanTaskGenerateService} 相同，任务参数为 JSON：objectId、userId、tenantId。
 */
@Slf4j
@Component
public class MaintenancePlanOrderGenerateService {

    @Autowired
    private MaintenancePlanOrderSyncService maintenancePlanOrderSyncService;

    @Value("${skyeye.tenant.enable:false}")
    private boolean tenantEnable;

    @XxlJob("maintenancePlanOrderGenerateService")
    public void generateMaintainOrders() {
        String param = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(param)) {
            log.warn("保养计划自动下发：执行参数为空");
            return;
        }
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String planId = paramMap.get("objectId");
        if (StrUtil.isBlank(planId)) {
            log.warn("保养计划自动下发：objectId 为空");
            return;
        }
        String tenantId = tenantEnable ? paramMap.get("tenantId") : StrUtil.EMPTY;
        if (tenantEnable) {
            TenantContext.setTenantId(tenantId);
        }
        try {
            maintenancePlanOrderSyncService.generateMaintainOrdersForPlan(planId);
        } catch (Exception e) {
            log.warn("保养计划[{}]定时下发保养单失败", planId, e);
        }
    }
}
