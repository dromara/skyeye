/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.portal.entity.PortalProductFeature;

/**
 * 官网产品功能矩阵服务接口
 */
public interface PortalProductFeatureService extends SkyeyeBusinessService<PortalProductFeature> {

    /**
     * 官网展示：获取已启用功能矩阵列表
     */
    void queryEnabledPortalProductFeatureList(InputObject inputObject, OutputObject outputObject);
}
