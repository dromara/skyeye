/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.notice.entity.NoticeDispatchChannelLog;

import java.util.List;

public interface NoticeDispatchChannelLogService extends SkyeyeBusinessService<NoticeDispatchChannelLog> {

    NoticeDispatchChannelLog createLog(String historyId, Integer channel, String receiveUserId, String receiveAccount,
                                       String title, String content, Integer status, String errorMsg, String createUserId);

    void updateChannelResult(String channelLogId, Integer status, String jobMateId, String errorMsg);

    List<NoticeDispatchChannelLog> queryByHistoryId(String historyId);

}
