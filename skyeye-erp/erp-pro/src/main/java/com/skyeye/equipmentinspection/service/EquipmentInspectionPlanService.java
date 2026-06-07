/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;

/**
 * 设备巡检方案服务接口
 */
public interface EquipmentInspectionPlanService extends SkyeyeBusinessService<EquipmentInspectionPlan> {

    int calcRequiredInspectionCount(EquipmentInspectionPlan plan, String startTime, String endTime);

}

