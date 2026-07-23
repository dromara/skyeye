/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.repair.entity.EquipmentRepairFailRecord;
import com.skyeye.repair.entity.EquipmentRepairOrder;

import java.util.List;

/**
 * 设备维修失败履历
 */
public interface EquipmentRepairFailRecordService extends SkyeyeBusinessService<EquipmentRepairFailRecord> {

    void saveFailRecord(EquipmentRepairOrder order);

    List<EquipmentRepairFailRecord> selectByParentId(String parentId);

    void deleteByParentId(String parentId);

}
