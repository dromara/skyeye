/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 设备巡检统计服务
 */
public interface EquipmentInspectionStatService {

    void queryEquipmentInspectionSummaryList(InputObject inputObject, OutputObject outputObject);

    void queryEquipmentInspectionMissedList(InputObject inputObject, OutputObject outputObject);

    void queryEquipmentInspectionDistributionPanel(InputObject inputObject, OutputObject outputObject);

}
