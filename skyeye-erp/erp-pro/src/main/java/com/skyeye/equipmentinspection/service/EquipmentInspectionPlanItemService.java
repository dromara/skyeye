/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlanItem;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionPlanItemService
 * @Description: 设备巡检方案项目关联服务接口层
 */
public interface EquipmentInspectionPlanItemService extends SkyeyeBusinessService<EquipmentInspectionPlanItem> {

    void deleteByParentId(String planId);

    List<String> selectByParentId(String planId);

    Map<String, List<String>> selectMapByParentId(List<String> planIds);

    void saveList(String planId, List<String> itemIds);

}
