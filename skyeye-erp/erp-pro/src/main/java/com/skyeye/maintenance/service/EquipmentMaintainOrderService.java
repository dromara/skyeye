/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.maintenance.entity.EquipmentMaintainOrder;

/**
 * @Description: 设备保养任务服务接口
 */
public interface EquipmentMaintainOrderService extends SkyeyeBusinessService<EquipmentMaintainOrder> {

    void startTask(InputObject inputObject, OutputObject outputObject);

    void completeTask(InputObject inputObject, OutputObject outputObject);

    void cancelTask(InputObject inputObject, OutputObject outputObject);

    void reassignTimeoutTask(InputObject inputObject, OutputObject outputObject);
}
