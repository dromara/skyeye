/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;

import java.util.List;

/**
 * @ClassName: EquipmentInspectionPlanService
 * @Description: 设备巡检方案服务接口层
 */
public interface EquipmentInspectionPlanService extends SkyeyeBusinessService<EquipmentInspectionPlan> {

    List<EquipmentInspectionPlan> getDataFromDb(List<String> idList);

    int calcRequiredInspectionCount(EquipmentInspectionPlan plan, String startTime, String endTime);

    void queryAllEquipmentInspectionPlanList(InputObject inputObject, OutputObject outputObject);

}

