/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. all rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.handler.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.chat.enums.TalkChatType;
import com.skyeye.chat.service.TalkChatHistoryService;
import com.skyeye.common.constans.SocketConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroup;
import com.skyeye.eve.enumclass.CompanyTalkGroupState;
import com.skyeye.eve.service.CompanyTalkGroupService;
import com.skyeye.eve.service.CompanyTalkGroupUserService;
import com.skyeye.websocket.TalkWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 策略：处理群聊消息（type=Eleventh），负责群状态校验、消息入库与群消息推送。
 */
@Slf4j
public class EleventhGroupChatHandler implements TalkWebSocketClientMessageHandler {

    @Override
    public void handle(int type, JSONObject jsonObject, TalkWebSocket socket) {
        TransactionTemplate transactionTemplate = SpringUtils.getBean(TransactionTemplate.class);
        transactionTemplate.execute(status -> {
            try {
                Map<String, Object> finalMap = new HashMap<>();
                finalMap.put("messageType", type);
                finalMap = SocketConstants.sendGroupTalkPeopleMsg(jsonObject);
                CompanyTalkGroupService companyTalkGroupService = SpringUtils.getBean(CompanyTalkGroupService.class);
                CompanyTalkGroup groupMation = companyTalkGroupService.selectById(finalMap.get("id").toString());
                if (groupMation == null || StrUtil.isBlank(groupMation.getId())) {
                    finalMap.clear();
                    finalMap.put("messageType", "1301");
                    finalMap.put("groupId", jsonObject.getStr("to"));
                    socket.sendMessageToSession(JSONUtil.toJsonStr(finalMap), socket.getWsSession());
                    return true;
                }
                String senderId = StrUtil.blankToDefault(jsonObject.getStr("userId"), socket.getUserId());
                CompanyTalkGroupUserService companyTalkGroupUserService = SpringUtils.getBean(CompanyTalkGroupUserService.class);
                // 一次缓存读取：同时用于成员校验与消息推送
                List<String> memberUserIds = companyTalkGroupUserService.selectMemberUserIdsByGroupIdWithCache(groupMation.getId());
                boolean senderIsMember = StrUtil.isNotBlank(senderId) && CollectionUtil.isNotEmpty(memberUserIds)
                    && memberUserIds.stream().anyMatch(memberUserId -> StrUtil.equals(memberUserId, senderId));
                if (!senderIsMember) {
                    finalMap.clear();
                    finalMap.put("messageType", "1301");
                    finalMap.put("groupId", jsonObject.getStr("to"));
                    socket.sendMessageToSession(JSONUtil.toJsonStr(finalMap), socket.getWsSession());
                    return true;
                }
                if (CompanyTalkGroupState.NORMAL.getKey() == groupMation.getState()) {
                    TalkChatHistoryService talkChatHistoryService = SpringUtils.getBean(TalkChatHistoryService.class);
                    String id = talkChatHistoryService.createEntity(jsonObject, TalkChatType.GROUP_CHAT.getKey());
                    finalMap.put("createTime", DateUtil.getTimeAndToString());
                    finalMap.put("dataId", id);
                    finalMap.put("groupname", groupMation.getGroupName());
                    sendGroupMessageToMembers(socket, finalMap, memberUserIds, senderId);
                } else {
                    finalMap.clear();
                    finalMap.put("messageType", "1301");
                    finalMap.put("groupId", jsonObject.getStr("to"));
                    socket.sendMessageToSession(JSONUtil.toJsonStr(finalMap), socket.getWsSession());
                }
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("发送群聊消息失败: {}", e.getMessage(), e);
                throw e;
            }
        });
    }

    /**
     * 仅向群成员推送消息；发送者当前终端不重复推送（其他终端仍可收到）。
     */
    private void sendGroupMessageToMembers(TalkWebSocket socket, Map<String, Object> finalMap,
                                           List<String> memberUserIds, String senderId) {
        if (CollectionUtil.isEmpty(memberUserIds)) {
            return;
        }
        String messageJson = JSONUtil.toJsonStr(finalMap);
        String currentSessionId = socket.getWsSession() != null ? socket.getWsSession().getId() : null;
        for (String memberUserId : memberUserIds) {
            if (StrUtil.isBlank(memberUserId)) {
                continue;
            }
            if (StrUtil.equals(senderId, memberUserId)) {
                socket.sendMessageTo(messageJson, memberUserId, currentSessionId);
            } else {
                socket.sendMessageTo(messageJson, memberUserId, null);
            }
        }
    }
}
