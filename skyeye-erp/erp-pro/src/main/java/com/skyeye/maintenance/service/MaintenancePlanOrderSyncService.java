/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service;

/**
 * @Description: 保养计划与设备保养单自动同步服务
 */
public interface MaintenancePlanOrderSyncService {

    /**
     * 在滚动窗口内按保养计划频次生成保养单（幂等）
     */
    void generateMaintainOrdersForPlan(String planId);
}
