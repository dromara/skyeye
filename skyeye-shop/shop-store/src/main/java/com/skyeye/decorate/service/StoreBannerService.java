/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.decorate.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.decorate.entity.StoreBanner;

import java.util.List;

public interface StoreBannerService extends SkyeyeBusinessService<StoreBanner> {

    /**
     * C 端：按门店查已启用轮播
     */
    void queryEnabledStoreBannerList(InputObject inputObject, OutputObject outputObject);

    List<StoreBanner> listEnabledByStoreId(String storeId);
}
