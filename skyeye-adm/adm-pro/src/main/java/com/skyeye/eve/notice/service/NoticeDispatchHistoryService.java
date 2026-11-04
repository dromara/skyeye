/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.notice.entity.NoticeDispatchHistory;
import com.skyeye.eve.rest.notice.NoticeDispatchChannelLogUpdateBody;

public interface NoticeDispatchHistoryService extends SkyeyeBusinessService<NoticeDispatchHistory> {

    NoticeDispatchHistory createHistory(String typeCode, String typeName, String title, String objectData,
                                        int receiverCount, String createUserId);

    void refreshHistoryStatus(String historyId);

    void updateChannelLogFromRemote(InputObject inputObject, OutputObject outputObject);

    void updateChannelLog(NoticeDispatchChannelLogUpdateBody body);

    void queryHistoryDetail(InputObject inputObject, OutputObject outputObject);

}
