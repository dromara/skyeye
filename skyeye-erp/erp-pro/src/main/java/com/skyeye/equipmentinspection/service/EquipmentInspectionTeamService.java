/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionTeam;

/**
 * @ClassName: EquipmentInspectionTeamService
 * @Description: 设备巡检班组服务接口层
 */
public interface EquipmentInspectionTeamService extends SkyeyeBusinessService<EquipmentInspectionTeam> {

    void queryAllEquipmentInspectionTeamList(InputObject inputObject, OutputObject outputObject);

}
