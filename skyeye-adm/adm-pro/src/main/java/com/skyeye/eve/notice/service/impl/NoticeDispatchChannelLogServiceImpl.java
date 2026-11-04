/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.notice.classenum.NoticeDispatchChannelStatusEnum;
import com.skyeye.eve.notice.dao.NoticeDispatchChannelLogDao;
import com.skyeye.eve.notice.entity.NoticeDispatchChannelLog;
import com.skyeye.eve.notice.service.NoticeDispatchChannelLogService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@SkyeyeService(name = "消息通知渠道明细", groupName = "消息通知", manageShow = false)
public class NoticeDispatchChannelLogServiceImpl
    extends SkyeyeBusinessServiceImpl<NoticeDispatchChannelLogDao, NoticeDispatchChannelLog>
    implements NoticeDispatchChannelLogService {

    private static final int ERROR_MSG_LIMIT = 1000;

    @Override
    public NoticeDispatchChannelLog createLog(String historyId, Integer channel, String receiveUserId,
                                              String receiveAccount, String title, String content,
                                              Integer status, String errorMsg, String createUserId) {
        NoticeDispatchChannelLog log = new NoticeDispatchChannelLog();
        log.setHistoryId(historyId);
        log.setChannel(channel);
        log.setReceiveUserId(StrUtil.blankToDefault(receiveUserId, StrUtil.EMPTY));
        log.setReceiveAccount(StrUtil.blankToDefault(receiveAccount, StrUtil.EMPTY));
        log.setTitle(StrUtil.blankToDefault(title, StrUtil.EMPTY));
        log.setContent(StrUtil.blankToDefault(content, StrUtil.EMPTY));
        log.setStatus(status);
        log.setErrorMsg(trimError(errorMsg));
        String now = DateUtil.getTimeAndToString();
        log.setSendTime(now);
        if (!NoticeDispatchChannelStatusEnum.PROCESSING.getKey().equals(status)) {
            log.setFinishTime(now);
        } else {
            log.setFinishTime(StrUtil.EMPTY);
        }
        createEntity(log, StrUtil.blankToDefault(createUserId, StrUtil.EMPTY));
        return log;
    }

    @Override
    public void updateChannelResult(String channelLogId, Integer status, String jobMateId, String errorMsg) {
        if (StrUtil.isBlank(channelLogId) || status == null) {
            return;
        }
        UpdateWrapper<NoticeDispatchChannelLog> updateWrapper = new UpdateWrapper<>();
        // TableId 字段不能用 MybatisPlusUtil.toColumns，会得到空列名导致更新异常
        updateWrapper.eq(CommonConstants.ID, channelLogId);
        updateWrapper.set(MybatisPlusUtil.toColumns(NoticeDispatchChannelLog::getStatus), status);
        updateWrapper.set(MybatisPlusUtil.toColumns(NoticeDispatchChannelLog::getFinishTime), DateUtil.getTimeAndToString());
        if (StrUtil.isNotBlank(jobMateId)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(NoticeDispatchChannelLog::getJobMateId), jobMateId);
        }
        if (errorMsg != null) {
            updateWrapper.set(MybatisPlusUtil.toColumns(NoticeDispatchChannelLog::getErrorMsg), trimError(errorMsg));
        }
        update(updateWrapper);
    }

    @Override
    public List<NoticeDispatchChannelLog> queryByHistoryId(String historyId) {
        if (StrUtil.isBlank(historyId)) {
            return Collections.emptyList();
        }
        QueryWrapper<NoticeDispatchChannelLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(NoticeDispatchChannelLog::getHistoryId), historyId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(NoticeDispatchChannelLog::getCreateTime));
        return list(queryWrapper);
    }

    private String trimError(String errorMsg) {
        if (StrUtil.isBlank(errorMsg)) {
            return StrUtil.EMPTY;
        }
        return errorMsg.length() > ERROR_MSG_LIMIT ? errorMsg.substring(0, ERROR_MSG_LIMIT) : errorMsg;
    }

}
