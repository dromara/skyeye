/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dashboard.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.dashboard.entity.DashboardUserLayout;

/**
 * @ClassName: DashboardUserLayoutService
 * @Description: 用户仪表盘布局服务层
 */
public interface DashboardUserLayoutService extends SkyeyeBusinessService<DashboardUserLayout> {

    void queryDashboardLayoutList(InputObject inputObject, OutputObject outputObject);

    void queryDefaultDashboardLayout(InputObject inputObject, OutputObject outputObject);

    void setDefaultDashboardLayoutById(InputObject inputObject, OutputObject outputObject);

    void deleteDashboardLayoutById(InputObject inputObject, OutputObject outputObject);

}
