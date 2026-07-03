/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionTask;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: EquipmentInspectionTaskService
 * @Description: 设备巡检任务服务接口层
 */
public interface EquipmentInspectionTaskService extends SkyeyeBusinessService<EquipmentInspectionTask> {

    void startTask(InputObject inputObject, OutputObject outputObject);

    void completeTask(InputObject inputObject, OutputObject outputObject);

    void cancelTask(InputObject inputObject, OutputObject outputObject);

    void reassignTimeoutTask(InputObject inputObject, OutputObject outputObject);

}
