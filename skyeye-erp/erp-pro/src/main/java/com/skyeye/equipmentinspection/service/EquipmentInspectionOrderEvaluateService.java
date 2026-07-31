/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrderEvaluate;

/**
 * @ClassName: EquipmentInspectionOrderEvaluateService
 * @Description: 设备巡检单评价服务接口层
 */
public interface EquipmentInspectionOrderEvaluateService extends SkyeyeBusinessService<EquipmentInspectionOrderEvaluate> {

    /**
     * 按巡检单 id 查询评价
     */
    EquipmentInspectionOrderEvaluate selectByObjectId(String objectId);

    /**
     * 系统自动好评（已完成且尚未评价时）
     */
    void autoEvaluateByOrderId(String orderId, String userId);

}
