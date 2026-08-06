/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: EquipmentInspectionStatService
 * @Description: 设备巡检统计服务接口
 */
public interface EquipmentInspectionStatService {

    /**
     * 今日已巡检次数
     */
    void queryTodayInspectedTotal(InputObject inputObject, OutputObject outputObject);

    /**
     * 今日异常巡检数
     */
    void queryTodayAbnormalInspectionTotal(InputObject inputObject, OutputObject outputObject);

    /**
     * 巡检单按状态统计：各状态数量（待派工、待接单、待填报、待完工、已完成）
     */
    void queryInspectionOrderStateStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 巡检单完成率统计：时间范围内总数、已完成数、完成率
     */
    void queryInspectionOrderCompletionRateStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 巡检单按检查结果统计：正常 / 异常；未完成不入统计
     */
    void queryInspectionOrderStatsByCheckResult(InputObject inputObject, OutputObject outputObject);

    /**
     * 巡检单按设备统计：按 equipmentId 分组数量
     */
    void queryInspectionOrderStatsByEquipment(InputObject inputObject, OutputObject outputObject);

    /**
     * 巡检单按巡检员统计：未指派归为其他
     */
    void queryInspectionOrderStatsByInspector(InputObject inputObject, OutputObject outputObject);

}
