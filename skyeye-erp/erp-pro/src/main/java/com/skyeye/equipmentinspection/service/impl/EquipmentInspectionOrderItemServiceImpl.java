/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionOrderItemDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrderItem;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderItemService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: EquipmentInspectionOrderItemServiceImpl
 * @Description: 设备巡检单子表服务实现类
 */
@Service
@SkyeyeService(name = "设备巡检单子表", groupName = "设备巡检单", manageShow = false)
public class EquipmentInspectionOrderItemServiceImpl extends SkyeyeLinkDataServiceImpl<EquipmentInspectionOrderItemDao, EquipmentInspectionOrderItem>
    implements EquipmentInspectionOrderItemService {

}
