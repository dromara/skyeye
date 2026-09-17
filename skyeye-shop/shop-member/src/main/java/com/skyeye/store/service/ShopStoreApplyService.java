/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.store.entity.ShopStoreApply;

/**
 * @ClassName: ShopStoreApplyService
 * @Description: 个人开店申请服务
 */
public interface ShopStoreApplyService extends SkyeyeBusinessService<ShopStoreApply> {

    void applyPersonalStore(InputObject inputObject, OutputObject outputObject);

    void cancelMyPersonalStoreApply(InputObject inputObject, OutputObject outputObject);

    void approvePersonalStoreApply(InputObject inputObject, OutputObject outputObject);

    void rejectPersonalStoreApply(InputObject inputObject, OutputObject outputObject);

    void queryPersonalStoreApplyList(InputObject inputObject, OutputObject outputObject);

    void queryMyPersonalStoreApplyList(InputObject inputObject, OutputObject outputObject);

    void queryMyPersonalStoreQuota(InputObject inputObject, OutputObject outputObject);

}
