/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlanEquipment;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionPlanEquipmentService
 * @Description: 设备巡检方案设备关联服务接口层
 */
public interface EquipmentInspectionPlanEquipmentService extends SkyeyeBusinessService<EquipmentInspectionPlanEquipment> {

    void deleteByParentId(String planId);

    List<String> selectByParentId(String planId);

    Map<String, List<String>> selectMapByParentId(List<String> planIds);

    void saveList(String planId, List<String> equipmentIds);

}
