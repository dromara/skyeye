/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service.impl;

import com.skyeye.maintenance.entity.MaintenanceStandard;
import com.skyeye.maintenance.service.MaintenanceStandardService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.maintenance.dao.MaintenanceStandardDao;
import org.springframework.stereotype.Service;

/**
 * @Description: 保养标准服务层
 */
@Service
@SkyeyeService(name = "保养标准", groupName = "设备保养")
public class MaintenanceStandardServiceImpl extends SkyeyeBusinessServiceImpl<MaintenanceStandardDao, MaintenanceStandard>
    implements MaintenanceStandardService {

}
