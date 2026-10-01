/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.decorate.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.decorate.entity.StorePage;

public interface StorePageService extends SkyeyeBusinessService<StorePage> {

    void queryStorePageDecorate(InputObject inputObject, OutputObject outputObject);

    void saveStorePageDecorate(InputObject inputObject, OutputObject outputObject);

    void queryStorePageForC(InputObject inputObject, OutputObject outputObject);
}
