/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.decorate.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.decorate.entity.StorePageComp;

import java.util.List;

public interface StorePageCompService extends SkyeyeBusinessService<StorePageComp> {

    List<StorePageComp> queryListByPageId(String pageId);

    void deleteByPageId(String pageId);

    void createDefaultComps(String pageId, String storeId, String userId);
}
