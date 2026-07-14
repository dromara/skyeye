/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.maintenance.entity.MaintenancePlan;

/**
 * @Description: 保养计划服务接口
 */
public interface MaintenancePlanService extends SkyeyeBusinessService<MaintenancePlan> {

    void queryAllMaintenancePlanList(InputObject inputObject, OutputObject outputObject);
}
