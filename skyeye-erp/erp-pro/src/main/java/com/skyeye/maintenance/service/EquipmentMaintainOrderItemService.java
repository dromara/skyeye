/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.maintenance.entity.EquipmentMaintainOrderItem;
import com.skyeye.maintenance.entity.MaintenancePlanItem;

import java.util.List;

/**
 * @Description: 设备保养单明细服务接口
 */
public interface EquipmentMaintainOrderItemService extends SkyeyeBusinessService<EquipmentMaintainOrderItem> {

    void saveList(String parentId, List<EquipmentMaintainOrderItem> beans);

    void deleteByParentId(String parentId);

    List<EquipmentMaintainOrderItem> selectByParentId(String parentId);

    /**
     * 从保养计划明细复制为保养单明细（自动下发时使用）
     */
    List<EquipmentMaintainOrderItem> copyFromPlanItems(List<MaintenancePlanItem> planItems);
}
