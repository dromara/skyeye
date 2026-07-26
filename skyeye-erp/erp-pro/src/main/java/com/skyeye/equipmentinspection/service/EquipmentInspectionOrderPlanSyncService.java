/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service;

/**
 * 设备巡检方案与系统生成巡检单的同步：由 XXL 子任务按 planId 生成实例。
 */
public interface EquipmentInspectionOrderPlanSyncService {

    /**
     * 按指定方案与频次，在滚动时间窗内生成待派工巡检单（幂等）。由动态注册的 XXL 任务调用。
     */
    void generateInspectionOrdersForPlan(String planId);

}
