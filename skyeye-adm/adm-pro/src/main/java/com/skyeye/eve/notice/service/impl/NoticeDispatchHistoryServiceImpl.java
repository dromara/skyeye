/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.notice.classenum.NoticeChannelEnum;
import com.skyeye.eve.notice.classenum.NoticeDispatchChannelStatusEnum;
import com.skyeye.eve.notice.classenum.NoticeDispatchHistoryStatusEnum;
import com.skyeye.eve.notice.dao.NoticeDispatchHistoryDao;
import com.skyeye.eve.notice.entity.NoticeDispatchChannelLog;
import com.skyeye.eve.notice.entity.NoticeDispatchHistory;
import com.skyeye.eve.notice.service.NoticeDispatchChannelLogService;
import com.skyeye.eve.notice.service.NoticeDispatchHistoryService;
import com.skyeye.eve.rest.notice.NoticeDispatchChannelLogUpdateBody;
import com.skyeye.eve.service.IAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "消息通知派发历史", groupName = "消息通知")
public class NoticeDispatchHistoryServiceImpl
    extends SkyeyeBusinessServiceImpl<NoticeDispatchHistoryDao, NoticeDispatchHistory>
    implements NoticeDispatchHistoryService {

    @Autowired
    private NoticeDispatchChannelLogService noticeDispatchChannelLogService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    protected QueryWrapper<NoticeDispatchHistory> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<NoticeDispatchHistory> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotBlank(commonPageInfo.getType())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(NoticeDispatchHistory::getTypeCode), commonPageInfo.getType());
        }
        if (StrUtil.isNotBlank(commonPageInfo.getState()) && StrUtil.isNumeric(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(NoticeDispatchHistory::getStatus),
                Integer.valueOf(commonPageInfo.getState()));
        }
        if (StrUtil.isNotBlank(commonPageInfo.getKeyword())) {
            String keyword = commonPageInfo.getKeyword();
            queryWrapper.and(w -> w.like(MybatisPlusUtil.toColumns(NoticeDispatchHistory::getTitle), keyword)
                .or().like(MybatisPlusUtil.toColumns(NoticeDispatchHistory::getTypeCode), keyword)
                .or().like(MybatisPlusUtil.toColumns(NoticeDispatchHistory::getTypeName), keyword));
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(NoticeDispatchHistory::getCreateTime));
        return queryWrapper;
    }

    @Override
    public NoticeDispatchHistory createHistory(String typeCode, String typeName, String title, String objectData,
                                               int receiverCount, String createUserId) {
        NoticeDispatchHistory history = new NoticeDispatchHistory();
        history.setTypeCode(StrUtil.blankToDefault(typeCode, StrUtil.EMPTY));
        history.setTypeName(StrUtil.blankToDefault(typeName, StrUtil.EMPTY));
        history.setTitle(StrUtil.blankToDefault(title, StrUtil.EMPTY));
        history.setObjectData(StrUtil.blankToDefault(objectData, StrUtil.EMPTY));
        history.setReceiverCount(Math.max(receiverCount, 0));
        history.setSuccessCount(0);
        history.setFailCount(0);
        history.setSkipCount(0);
        history.setStatus(NoticeDispatchHistoryStatusEnum.PROCESSING.getKey());
        createEntity(history, StrUtil.blankToDefault(createUserId, StrUtil.EMPTY));
        return history;
    }

    @Override
    public void refreshHistoryStatus(String historyId) {
        if (StrUtil.isBlank(historyId)) {
            return;
        }
        List<NoticeDispatchChannelLog> logs = noticeDispatchChannelLogService.queryByHistoryId(historyId);
        int success = 0;
        int fail = 0;
        int skip = 0;
        int processing = 0;
        if (CollectionUtil.isNotEmpty(logs)) {
            for (NoticeDispatchChannelLog log : logs) {
                Integer status = log.getStatus();
                if (NoticeDispatchChannelStatusEnum.SUCCESS.getKey().equals(status)) {
                    success++;
                } else if (NoticeDispatchChannelStatusEnum.FAIL.getKey().equals(status)) {
                    fail++;
                } else if (NoticeDispatchChannelStatusEnum.SKIPPED.getKey().equals(status)) {
                    skip++;
                } else {
                    processing++;
                }
            }
        }
        Integer historyStatus;
        if (processing > 0) {
            historyStatus = NoticeDispatchHistoryStatusEnum.PROCESSING.getKey();
        } else if (fail == 0) {
            historyStatus = NoticeDispatchHistoryStatusEnum.SUCCESS.getKey();
        } else if (success == 0) {
            historyStatus = NoticeDispatchHistoryStatusEnum.FAIL.getKey();
        } else {
            historyStatus = NoticeDispatchHistoryStatusEnum.PARTIAL_SUCCESS.getKey();
        }

        UpdateWrapper<NoticeDispatchHistory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, historyId);
        updateWrapper.set(MybatisPlusUtil.toColumns(NoticeDispatchHistory::getSuccessCount), success);
        updateWrapper.set(MybatisPlusUtil.toColumns(NoticeDispatchHistory::getFailCount), fail);
        updateWrapper.set(MybatisPlusUtil.toColumns(NoticeDispatchHistory::getSkipCount), skip);
        updateWrapper.set(MybatisPlusUtil.toColumns(NoticeDispatchHistory::getStatus), historyStatus);
        update(updateWrapper);
    }

    @Override
    public void updateChannelLogFromRemote(InputObject inputObject, OutputObject outputObject) {
        NoticeDispatchChannelLogUpdateBody body = inputObject.getParams(NoticeDispatchChannelLogUpdateBody.class);
        updateChannelLog(body);
    }

    @Override
    public void updateChannelLog(NoticeDispatchChannelLogUpdateBody body) {
        if (body == null || StrUtil.isBlank(body.getChannelLogId())) {
            return;
        }
        NoticeDispatchChannelLog log = selectByIdSafe(body.getChannelLogId());
        noticeDispatchChannelLogService.updateChannelResult(
            body.getChannelLogId(), body.getStatus(), body.getJobMateId(), body.getErrorMsg());
        if (log != null) {
            refreshHistoryStatus(log.getHistoryId());
        }
    }

    @Override
    public void queryHistoryDetail(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        NoticeDispatchHistory history = selectById(id);
        List<NoticeDispatchChannelLog> logs = noticeDispatchChannelLogService.queryByHistoryId(id);
        if (CollectionUtil.isNotEmpty(logs)) {
            iAuthUserService.setDataMation(logs, NoticeDispatchChannelLog::getReceiveUserId);
            for (NoticeDispatchChannelLog item : logs) {
                // 站内信旧数据账号曾存 userId，展示时替换为姓名
                if (NoticeChannelEnum.SITE.getKey().equals(item.getChannel())
                    && item.getReceiveUserMation() != null
                    && StrUtil.equals(item.getReceiveAccount(), item.getReceiveUserId())) {
                    Object name = item.getReceiveUserMation().getOrDefault("name",
                        item.getReceiveUserMation().get("userName"));
                    if (name != null && StrUtil.isNotBlank(name.toString())) {
                        item.setReceiveAccount(name.toString());
                    }
                }
            }
        }
        history.setChannelLogList(logs);
        outputObject.setBean(history);
        outputObject.settotal(1);
    }

    private NoticeDispatchChannelLog selectByIdSafe(String channelLogId) {
        try {
            return noticeDispatchChannelLogService.selectById(channelLogId);
        } catch (Exception e) {
            return null;
        }
    }

}
