/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 设备保养单统计服务接口
 */
public interface EquipmentMaintainStatisticsService {

    /**
     * 保养单按状态统计：各状态数量（待执行、执行中、已完成、已取消、已超时）
     */
    void queryMaintainOrderStateStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 保养单完成率统计：时间范围内总单数、已完成数、完成率
     */
    void queryMaintainOrderCompletionRateStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 保养单备件使用统计：领用数量、金额、涉及工单数及按备件分布
     */
    void queryMaintainOrderSparePartStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 保养单日度趋势统计：已/未完成单量
     */
    void queryMaintainOrderTrendStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 保养单按执行人统计
     */
    void queryMaintainOrderStatsByExecutor(InputObject inputObject, OutputObject outputObject);
}
