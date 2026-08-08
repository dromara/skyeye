package com.skyeye.equipmentcheckstandard.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentcheckstandard.entity.EquipmentCheckStandard;

/**
 * @ClassName: EquipmentCheckStandardService
 * @Description: 设备点检标准服务接口层
 */
public interface EquipmentCheckStandardService extends SkyeyeBusinessService<EquipmentCheckStandard> {

    /**
     * 获取审批通过的设备点检标准列表
     */
    void queryApprovedEquipmentCheckStandardList(InputObject inputObject, OutputObject outputObject);
}

