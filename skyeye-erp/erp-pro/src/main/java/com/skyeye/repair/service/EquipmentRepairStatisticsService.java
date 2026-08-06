/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 报修维修统计服务接口层
 *
 * @author skyeye云系列--卫志强
 */
public interface EquipmentRepairStatisticsService {

    /**
     * 按创建时间的日度趋势（dayList + allNewOrders + completedOrders）
     */
    void queryEquipmentRepairOrderTrendStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 报修维修总览卡片：总工单数、完成工单数、配件使用数、平均处理时长
     */
    void queryOverviewEquipmentRepairOrder(InputObject inputObject, OutputObject outputObject);

    /**
     * 按状态划分
     */
    void queryRepairOrderStateStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 按紧急程度划分
     */
    void queryRepairOrderStatsByUrgency(InputObject inputObject, OutputObject outputObject);

    /**
     * 按维修负责人划分
     */
    void queryRepairOrderStatsByServiceUser(InputObject inputObject, OutputObject outputObject);

    /**
     * 按故障类别划分
     */
    void queryRepairOrderStatsByFaultType(InputObject inputObject, OutputObject outputObject);

    /**
     * 按来源类型划分
     */
    void queryRepairOrderStatsByFromType(InputObject inputObject, OutputObject outputObject);
}
