/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentcheck.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: EquipmentCheckStatisticsService
 * @Description: 设备点检统计服务接口层
 */
public interface EquipmentCheckStatisticsService {

    void queryTodayCheckedTotal(InputObject inputObject, OutputObject outputObject);

    void queryTodayAbnormalCheckTotal(InputObject inputObject, OutputObject outputObject);

    void queryCheckStatsByCheckTime(InputObject inputObject, OutputObject outputObject);

    void queryAbnormalCheckStatsByCheckTime(InputObject inputObject, OutputObject outputObject);

    /**
     * 点检单按审批状态统计：草稿、审核中、审核通过、驳回、作废、撤销
     */
    void queryCheckOrderStateStats(InputObject inputObject, OutputObject outputObject);

}
