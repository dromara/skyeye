/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.eve.service.CompanyTalkGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description: 离职群聊退群/转让/解散消息监听器（广播模式）
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "${topic.quit-manager-transfer-service}",
    consumerGroup = "${topic.quit-manager-transfer-service}-talk-group",
    selectorExpression = "${spring.profiles.active}",
    messageModel = MessageModel.BROADCASTING)
public class QuitTalkGroupExitConsume implements RocketMQListener<String> {

    @Autowired
    private CompanyTalkGroupService companyTalkGroupService;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        Map<String, Object> quitMap = JSONUtil.toBean(map.get("content").toString(), null);
        String userId = quitMap.get("createId").toString();
        String transferUserId = quitMap.get("managerTransferUserId") != null ? quitMap.get("managerTransferUserId").toString() : null;
        String quitId = quitMap.get("id") != null ? quitMap.get("id").toString() : "";
        companyTalkGroupService.handleUserQuitGroup(userId, transferUserId);
        log.info("离职申请[{}]群聊退群/转让处理完成，userId: {}", quitId, userId);
    }

}
