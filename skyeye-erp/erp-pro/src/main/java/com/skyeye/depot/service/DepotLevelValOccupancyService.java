/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotLevelValOccupancy;

import java.util.List;

public interface DepotLevelValOccupancyService extends SkyeyeBusinessService<DepotLevelValOccupancy> {

    void queryDepotLevelValOccupancyByDepotId(InputObject inputObject, OutputObject outputObject);

    void writeDepotLevelValOccupancyBatch(InputObject inputObject, OutputObject outputObject);

    /**
     * 删库位时级联清理占用
     */
    void deleteByLevelValIds(List<String> levelValIds);

    /**
     * 出入库审核成功后按增量调整占用数量。
     *
     * @param depotId     仓库id
     * @param levelValId  库位值id
     * @param operNumber  变动数量（正数）
     * @param putOrOutType DepotPutOutType：1入库加、2出库减
     * @param userId      操作人
     */
    void adjustOccupancyQty(String depotId, String levelValId, String operNumber, int putOrOutType, String userId);
}
