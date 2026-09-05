/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.projectconfig.service;

import com.skyeye.base.business.service.SkyeyeTeamAuthService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.projectconfig.entity.AutoProjectConfig;

/**
 * 项目配置服务。
 */
public interface AutoProjectConfigService extends SkyeyeTeamAuthService<AutoProjectConfig> {

    /**
     * 按项目查询配置；不存在时返回默认开启项（不落库）。
     */
    void queryAutoProjectConfigByObjectId(InputObject inputObject, OutputObject outputObject);

    /**
     * 获取项目配置；无记录返回默认开启。
     */
    AutoProjectConfig getOrDefaultByObjectId(String objectId);

    /**
     * 是否允许设置预计时间。
     */
    boolean isEstimateTimeEnabled(String objectId);

    /**
     * 是否允许积分分配。
     */
    boolean isScoreAllocateEnabled(String objectId);

}
