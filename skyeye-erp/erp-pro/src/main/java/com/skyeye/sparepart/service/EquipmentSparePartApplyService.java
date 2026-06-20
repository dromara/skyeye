/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.sparepart.entity.EquipmentSparePartApply;
import com.skyeye.sparepart.entity.EquipmentSparePartApplyChangeStock;

/**
 * 设备备件-申领单
 */
public interface EquipmentSparePartApplyService extends SkyeyeBusinessService<EquipmentSparePartApply> {

    void editApplyOtherState(InputObject inputObject, OutputObject outputObject);

    void editApplyOtherState(String id, Integer otherState);

    void editApplyOutNum(InputObject inputObject, OutputObject outputObject);

    void editApplyOutNum(EquipmentSparePartApplyChangeStock changeStock);

}
