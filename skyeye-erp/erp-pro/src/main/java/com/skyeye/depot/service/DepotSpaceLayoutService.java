/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotSpaceLayout;

import java.util.List;

public interface DepotSpaceLayoutService extends SkyeyeBusinessService<DepotSpaceLayout> {

    void queryDepotSpaceLayoutByDepotId(InputObject inputObject, OutputObject outputObject);

    void writeDepotSpaceLayoutBatch(InputObject inputObject, OutputObject outputObject);

    void deleteDepotSpaceLayoutByLevelValId(InputObject inputObject, OutputObject outputObject);

    /**
     * 按库位值 id 批量删除布局（删库位时级联调用）
     */
    void deleteByLevelValIds(List<String> levelValIds);
}
