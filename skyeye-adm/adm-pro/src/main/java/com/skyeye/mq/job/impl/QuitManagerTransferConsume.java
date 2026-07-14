/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.centerrest.team.TeamBusinessRestService;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.quit.entity.Quit;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 离职经理转让消息监听器（广播模式）
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "${topic.quit-manager-transfer-service}",
    consumerGroup = "${topic.quit-manager-transfer-service}-team-transfer",
    selectorExpression = "${spring.profiles.active}",
    messageModel = MessageModel.BROADCASTING)
public class QuitManagerTransferConsume implements RocketMQListener<String> {

    @Autowired
    private TeamBusinessRestService teamBusinessRestService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        if (tenantEnable) {
            TenantContext.setTenantId(map.get("tenantId").toString());
        }
        Quit quit = JSONUtil.toBean(map.get("content").toString(), Quit.class);
        if (StrUtil.isEmpty(quit.getManagerTransferUserId())) {
            log.info("离职申请[{}]未指定经理转让交接人，跳过转让", quit.getId());
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("fromUserId", quit.getCreateId());
        params.put("toUserId", quit.getManagerTransferUserId());
        ExecuteFeignClient.get(() -> teamBusinessRestService.transferAllChargeUser(params));
        log.info("离职申请[{}]经理转让完成，fromUserId: {}, toUserId: {}", quit.getId(), quit.getCreateId(), quit.getManagerTransferUserId());
    }

}
