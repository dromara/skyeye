/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.xxljob;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderEvaluateService;
import com.skyeye.eve.service.IQuartzService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 巡检单完成后延时自动评价，执行后删除任务。
 */
@Slf4j
@Component
public class EquipmentInspectionOrderAutoEvaluateService {

    @Autowired
    private EquipmentInspectionOrderEvaluateService equipmentInspectionOrderEvaluateService;

    @Autowired
    private IQuartzService iQuartzService;

    @Value("${skyeye.tenant.enable:false}")
    private boolean tenantEnable;

    @XxlJob("equipmentInspectionOrderAutoEvaluateService")
    public void autoEvaluate() {
        String param = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(param)) {
            log.warn("设备巡检单自动评价：执行参数为空");
            return;
        }
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String orderId = paramMap.get("objectId");
        String userId = paramMap.get("userId");
        if (StrUtil.isBlank(orderId)) {
            log.warn("设备巡检单自动评价：objectId 为空");
            return;
        }
        if (tenantEnable) {
            TenantContext.setTenantId(paramMap.get("tenantId"));
        }
        try {
            log.info("巡检单id(orderId){}---自动评价---开始", orderId);
            equipmentInspectionOrderEvaluateService.autoEvaluateByOrderId(orderId, userId);
            log.info("巡检单id(orderId){}---自动评价---结束", orderId);
        } finally {
            log.info("巡检单id(orderId){}---删除自动评价任务---开始", orderId);
            iQuartzService.stopAndDeleteTaskQuartz(orderId);
            log.info("巡检单id(orderId){}---删除自动评价任务---结束", orderId);
        }
    }

}
