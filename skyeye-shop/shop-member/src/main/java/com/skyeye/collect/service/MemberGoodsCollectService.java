/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.collect.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.collect.entity.MemberGoodsCollect;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface MemberGoodsCollectService extends SkyeyeBusinessService<MemberGoodsCollect> {

    void toggleGoodsCollect(InputObject inputObject, OutputObject outputObject);

    void checkGoodsCollect(InputObject inputObject, OutputObject outputObject);
}
