/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.browse.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.browse.entity.MemberBrowseHistory;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface MemberBrowseHistoryService extends SkyeyeBusinessService<MemberBrowseHistory> {

    void recordBrowseHistory(InputObject inputObject, OutputObject outputObject);

    void queryMyBrowseHistoryList(InputObject inputObject, OutputObject outputObject);

    void clearMyBrowseHistory(InputObject inputObject, OutputObject outputObject);

    void deleteMyBrowseHistoryById(InputObject inputObject, OutputObject outputObject);
}
