/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.tenant.entity.TenantAppBuyOrderToken;

import java.util.List;

public interface TenantAppBuyOrderTokenService extends SkyeyeBusinessService<TenantAppBuyOrderToken> {

    void saveList(String parentId, List<TenantAppBuyOrderToken> beans);

    void deleteByParentId(String parentId);

    List<TenantAppBuyOrderToken> selectByParentId(String parentId);

}
