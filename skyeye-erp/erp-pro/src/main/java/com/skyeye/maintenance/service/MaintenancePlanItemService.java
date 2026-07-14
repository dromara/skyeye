/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.maintenance.entity.MaintenancePlanItem;

import java.util.List;

/**
 * @Description: 保养计划明细服务接口
 */
public interface MaintenancePlanItemService extends SkyeyeBusinessService<MaintenancePlanItem> {

    void saveList(String parentId, List<MaintenancePlanItem> beans);

    void deleteByParentId(String parentId);

    List<MaintenancePlanItem> selectByParentId(String parentId);
}
