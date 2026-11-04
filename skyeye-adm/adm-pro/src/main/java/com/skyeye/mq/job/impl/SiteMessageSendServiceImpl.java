/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.enumeration.NoticeUserMessageTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import cn.hutool.core.map.MapUtil;
import com.skyeye.eve.notice.classenum.NoticeDispatchChannelStatusEnum;
import com.skyeye.eve.notice.entity.UserMessage;
import com.skyeye.eve.notice.service.NoticeDispatchHistoryService;
import com.skyeye.eve.notice.service.UserMessageService;
import com.skyeye.eve.rest.notice.NoticeDispatchChannelLogUpdateBody;
import com.skyeye.util.MqSendUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * @ClassName: SiteMessageSendServiceImpl
 * @Description: 站内信通知 MQ 消费
 * @author: skyeye云系列--卫志强
 * @date: 2026/3/29 22:30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "${topic.site-message-send-service}",
    consumerGroup = "${topic.site-message-send-service}",
    selectorExpression = "${spring.profiles.active}")
public class SiteMessageSendServiceImpl implements RocketMQListener<String> {

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private NoticeDispatchHistoryService noticeDispatchHistoryService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        String jobId = map.get("jobMateId").toString();
        String channelLogId = MapUtil.getStr(map, "channelLogId");
        try {
            if (tenantEnable && map.get("tenantId") != null) {
                TenantContext.setTenantId(map.get("tenantId").toString());
            }
            MqSendUtil.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, StrUtil.EMPTY);

            UserMessage userMessage = new UserMessage();
            userMessage.setName(map.getOrDefault("title", "系统消息").toString());
            userMessage.setRemark(map.getOrDefault("remark", userMessage.getName()).toString());
            userMessage.setContent(map.getOrDefault("content", StrUtil.EMPTY).toString());
            userMessage.setReceiveId(map.get("receiveId").toString());
            String typeCode = map.get("typeCode") == null ? StrUtil.EMPTY : map.get("typeCode").toString();
            if (StrUtil.isBlank(typeCode) && map.get("typeKey") != null) {
                typeCode = map.get("typeKey").toString();
            }
            userMessage.setType(NoticeUserMessageTypeEnum.normalizeType(typeCode));
            userMessage.setCreateUserId(map.getOrDefault("createUserId", CommonConstants.ADMIN_USER_ID).toString());
            if (map.get("objectData") != null) {
                userMessage.setObjectData(map.get("objectData").toString());
            }
            userMessageService.insertUserMessageList(Collections.singletonList(userMessage));
            MqSendUtil.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, StrUtil.EMPTY);
            callbackHistory(channelLogId, NoticeDispatchChannelStatusEnum.SUCCESS.getKey(), jobId, StrUtil.EMPTY);
        } catch (Exception e) {
            log.warn("site message send failed", e);
            MqSendUtil.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, StrUtil.blankToDefault(e.getMessage(), StrUtil.EMPTY));
            callbackHistory(channelLogId, NoticeDispatchChannelStatusEnum.FAIL.getKey(), jobId, e.getMessage());
        }
    }

    private void callbackHistory(String channelLogId, Integer status, String jobMateId, String errorMsg) {
        if (StrUtil.isBlank(channelLogId)) {
            return;
        }
        try {
            NoticeDispatchChannelLogUpdateBody body = new NoticeDispatchChannelLogUpdateBody();
            body.setChannelLogId(channelLogId);
            body.setStatus(status);
            body.setJobMateId(jobMateId);
            body.setErrorMsg(errorMsg);
            noticeDispatchHistoryService.updateChannelLog(body);
        } catch (Exception e) {
            // 回写失败不能拖垮已发送成功的消费，避免 MQ 无限重试
            log.warn("callback notice history failed, channelLogId={}", channelLogId, e);
        }
    }

}
