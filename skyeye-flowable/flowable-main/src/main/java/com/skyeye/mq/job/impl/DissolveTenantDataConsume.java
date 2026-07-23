/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.service.ActFlowService;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName: DissolveTenantDataConsume
 * @Description: 解散租户后清理该租户工作流数据 消息监听器配置为广播模式，所有消费者实例都会消费同一条消息
 * @author: skyeye云系列--卫志强
 * @date: 2026/7/22
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Component
@RocketMQMessageListener(
    topic = "${topic.dissolve-tenant-service}",
    consumerGroup = "${topic.dissolve-tenant-service}-flowable",
    selectorExpression = "${spring.profiles.active}",
    messageModel = MessageModel.BROADCASTING)
public class DissolveTenantDataConsume implements RocketMQListener<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DissolveTenantDataConsume.class);

    @Autowired
    private ActFlowService actFlowService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        String tenantId = null;
        // 未开启多租户时无按租户维度的工作流数据，直接跳过
        if (tenantEnable) {
            tenantId = map.get("tenantId").toString();
            TenantContext.setTenantId(tenantId);
        }
        // 清理该租户下的流程定义及 Activiti 模型
        actFlowService.deleteByTenantId(tenantId);
        LOGGER.info("解散租户工作流清理完成，tenantId: {}", tenantId);
    }

}
