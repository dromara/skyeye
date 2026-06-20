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
     * 按派工时间的月度趋势（xAxisData + seriesData + total）
     */
    void queryRepairMonthlyTrendStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 按设备 id 统计维修单数
     */
    void queryRepairStatsByEquipmentName(InputObject inputObject, OutputObject outputObject);

    /**
     * 按设备分页查询维修单
     */
    void queryPageList(InputObject inputObject, OutputObject outputObject);
}
