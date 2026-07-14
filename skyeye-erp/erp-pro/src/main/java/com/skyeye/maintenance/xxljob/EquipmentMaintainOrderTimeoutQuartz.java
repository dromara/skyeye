/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.xxljob;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.maintenance.classenum.EquipmentMaintainTaskState;
import com.skyeye.maintenance.entity.EquipmentMaintainOrder;
import com.skyeye.maintenance.service.EquipmentMaintainOrderService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备保养任务定时任务：检查并处理超时任务。
 * 建议执行频率：每小时执行一次。
 */
@Slf4j
@Component
public class EquipmentMaintainOrderTimeoutQuartz {

    @Autowired
    private EquipmentMaintainOrderService equipmentMaintainOrderService;

    @Autowired
    private ITenantService iTenantService;

    @Value("${skyeye.tenant.enable:false}")
    private boolean tenantEnable;

    @XxlJob("equipmentMaintainOrderTimeoutQuartz")
    public void checkTimeoutTasks() {
        log.info("开始检查超时的设备保养任务");
        try {
            if (tenantEnable) {
                List<Map<String, Object>> tenantList = iTenantService.queryAllTenantList();
                if (CollectionUtil.isEmpty(tenantList)) {
                    return;
                }
                tenantList.forEach(tenant -> {
                    TenantContext.setTenantId(tenant.get("id").toString());
                    checkTimeoutTasksForTenant();
                });
            } else {
                checkTimeoutTasksForTenant();
            }
        } catch (Exception e) {
            log.warn("检查超时设备保养任务失败", e);
        }
        log.info("检查超时的设备保养任务结束");
    }

    private void checkTimeoutTasksForTenant() {
        String currentTime = DateUtil.getTimeAndToString();
        QueryWrapper<EquipmentMaintainOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentMaintainOrder::getState), EquipmentMaintainTaskState.PENDING.getKey());
        queryWrapper.lt(MybatisPlusUtil.toColumns(EquipmentMaintainOrder::getPlannedStartTime), currentTime);
        List<EquipmentMaintainOrder> timeoutTasks = equipmentMaintainOrderService.list(queryWrapper);
        if (CollectionUtil.isEmpty(timeoutTasks)) {
            return;
        }
        log.info("发现 {} 个超时的待执行保养任务，开始标记为已超时", timeoutTasks.size());
        List<String> taskIds = timeoutTasks.stream().map(EquipmentMaintainOrder::getId).collect(Collectors.toList());
        UpdateWrapper<EquipmentMaintainOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(CommonConstants.ID, taskIds);
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentMaintainOrder::getState), EquipmentMaintainTaskState.TIMEOUT.getKey());
        equipmentMaintainOrderService.update(updateWrapper);
        equipmentMaintainOrderService.clearCache(taskIds);
        log.info("成功将 {} 个设备保养任务标记为已超时", timeoutTasks.size());
    }
}
