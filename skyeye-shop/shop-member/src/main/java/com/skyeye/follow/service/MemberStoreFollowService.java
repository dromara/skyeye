/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.follow.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.follow.entity.MemberStoreFollow;

public interface MemberStoreFollowService extends SkyeyeBusinessService<MemberStoreFollow> {

    void toggleStoreFollow(InputObject inputObject, OutputObject outputObject);

    void checkStoreFollow(InputObject inputObject, OutputObject outputObject);
}
