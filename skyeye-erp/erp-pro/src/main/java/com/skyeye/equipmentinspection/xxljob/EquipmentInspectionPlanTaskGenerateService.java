/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.xxljob;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderPlanSyncService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 由 SysQuartz 按巡检方案注册的 XXL 子任务：仅根据 objectId(方案 id) 生成待派工巡检单。
 */
@Slf4j
@Component
public class EquipmentInspectionPlanTaskGenerateService {

    @Autowired
    private EquipmentInspectionOrderPlanSyncService equipmentInspectionOrderPlanSyncService;

    @Value("${skyeye.tenant.enable:false}")
    private boolean tenantEnable;

    @XxlJob("equipmentInspectionPlanTaskGenerateService")
    public void generateInspectionOrders() {
        String param = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(param)) {
            log.warn("设备巡检方案下发：执行参数为空");
            return;
        }
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String planId = paramMap.get("objectId");
        if (StrUtil.isBlank(planId)) {
            log.warn("设备巡检方案下发：objectId 为空");
            return;
        }
        if (tenantEnable) {
            TenantContext.setTenantId(paramMap.get("tenantId"));
        }
        try {
            equipmentInspectionOrderPlanSyncService.generateInspectionOrdersForPlan(planId);
        } catch (Exception e) {
            log.warn("设备巡检方案[{}]定时生成巡检单失败", planId, e);
        }
    }

}
