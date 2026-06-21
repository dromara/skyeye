/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionItem;

/**
 * @ClassName: EquipmentInspectionItemService
 * @Description: 设备巡检项目服务接口层
 */
public interface EquipmentInspectionItemService extends SkyeyeBusinessService<EquipmentInspectionItem> {

    void queryAllEquipmentInspectionItemList(InputObject inputObject, OutputObject outputObject);

}
