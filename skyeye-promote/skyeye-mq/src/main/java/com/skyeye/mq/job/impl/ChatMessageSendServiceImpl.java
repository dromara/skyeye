/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.chat.enums.TalkChatType;
import com.skyeye.chat.service.TalkChatHistoryService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.constans.SocketConstants;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.rest.notice.NoticeDispatchChannelLogUpdateBody;
import com.skyeye.eve.service.INoticeDispatchHistoryService;
import com.skyeye.personnel.dao.SysEveUserDao;
import com.skyeye.service.JobMateMationService;
import com.skyeye.websocket.TalkWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ChatMessageSendServiceImpl
 * @Description: 消息通知-聊天渠道 MQ 消费（入库 sys_talk_chat_history + WebSocket 推送）
 * @author: skyeye云系列--卫志强
 * @date: 2026/3/29 22:30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "${topic.chat-message-send-service}",
    consumerGroup = "${topic.chat-message-send-service}",
    selectorExpression = "${spring.profiles.active}")
public class ChatMessageSendServiceImpl implements RocketMQListener<String> {

    @Autowired
    private JobMateMationService jobMateMationService;

    @Autowired
    private TalkChatHistoryService talkChatHistoryService;

    @Autowired
    private TalkWebSocket talkWebSocket;

    @Autowired
    private SysEveUserDao sysEveUserDao;

    @Autowired
    private INoticeDispatchHistoryService iNoticeDispatchHistoryService;

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
            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, StrUtil.EMPTY);

            String receiveId = MapUtil.getStr(map, "receiveId");
            if (StrUtil.isBlank(receiveId)) {
                throw new IllegalArgumentException("聊天发送缺少接收人 receiveId");
            }
            String sendUserId = StrUtil.blankToDefault(MapUtil.getStr(map, "createUserId"), CommonConstants.ADMIN_USER_ID);
            String text = buildChatText(map);
            if (StrUtil.isBlank(text)) {
                throw new IllegalArgumentException("聊天发送内容为空");
            }

            Map<String, Object> sender = resolveSender(sendUserId);
            // 管理员账号发出的通知聊天：统一展示为「系统通知」，避免出现 Master / 空名称
            boolean systemSender = StrUtil.equals(sendUserId, CommonConstants.ADMIN_USER_ID);
            JSONObject json = new JSONObject();
            json.set("userId", sendUserId);
            json.set("to", receiveId);
            json.set("message", text);
            json.set("username", systemSender ? "系统通知" : MapUtil.getStr(sender, "userName", "系统通知"));
            json.set("avatar", systemSender ? StrUtil.EMPTY : MapUtil.getStr(sender, "userPhoto", StrUtil.EMPTY));
            json.set("type", SocketConstants.MessageType.Fourth.getType());

            String dataId = talkChatHistoryService.createEntity(json,
                TalkChatType.PERSONAL_TO_PERSONAL.getKey(), WhetherEnum.DISABLE_USING.getKey());
            if (TalkWebSocket.isUserOnline(receiveId)) {
                Map<String, Object> finalMap = new HashMap<>();
                finalMap.put("messageType", SocketConstants.MessageType.Fourth.getType());
                finalMap.put("dataId", dataId);
                finalMap.putAll(SocketConstants.sendOrdinaryMsg(json));
                talkWebSocket.sendMessageTo(JSONUtil.toJsonStr(finalMap), receiveId, null);
            }

            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, StrUtil.EMPTY);
            iNoticeDispatchHistoryService.updateChannelLog(channelLogId,
                NoticeDispatchChannelLogUpdateBody.STATUS_SUCCESS, jobId, StrUtil.EMPTY);
        } catch (Exception e) {
            log.warn("chat message send failed", e);
            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL,
                StrUtil.blankToDefault(e.getMessage(), StrUtil.EMPTY));
            iNoticeDispatchHistoryService.updateChannelLog(channelLogId,
                NoticeDispatchChannelLogUpdateBody.STATUS_FAIL, jobId, e.getMessage());
        }
    }

    private String buildChatText(Map<String, Object> map) {
        String title = MapUtil.getStr(map, "title");
        String content = MapUtil.getStr(map, "content");
        String plain = StrUtil.blankToDefault(content, StrUtil.EMPTY)
            .replaceAll("(?i)<br\\s*/?>", "\n");
        plain = HtmlUtil.cleanHtmlTag(plain).trim();
        if (StrUtil.isNotBlank(title) && !StrUtil.contains(plain, title)) {
            return "【" + title + "】\n" + plain;
        }
        return plain;
    }

    private Map<String, Object> resolveSender(String userId) {
        try {
            Map<String, Object> user = sysEveUserDao.queryUserDetailsMationByUserId(userId);
            if (user != null && !user.isEmpty()) {
                return user;
            }
        } catch (Exception e) {
            log.warn("resolve chat sender failed, userId={}", userId, e);
        }
        return new HashMap<>();
    }

}
