/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;

/**
 * @ClassName: EquipmentInspectionOrderService
 * @Description: 设备巡检单服务接口层
 */
public interface EquipmentInspectionOrderService extends SkyeyeBusinessService<EquipmentInspectionOrder> {

    /**
     * 派工/指派
     */
    void editEquipmentInspectionWaitToWorkMation(InputObject inputObject, OutputObject outputObject);

    /**
     * 接单
     */
    void receivingEquipmentInspectionOrderById(InputObject inputObject, OutputObject outputObject);

    /**
     * 登记本单巡检一次
     */
    void registerEquipmentInspectionOnce(InputObject inputObject, OutputObject outputObject);

    void submitEquipmentInspectionResult(InputObject inputObject, OutputObject outputObject);

    void finishEquipmentInspectionOrderById(InputObject inputObject, OutputObject outputObject);

    void transferEquipmentInspectionToRepair(InputObject inputObject, OutputObject outputObject);

    void updateStateById(String id, Integer state);

}
