/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.rest.notice.NoticeDispatchChannelLogUpdateBody;
import com.skyeye.eve.service.INoticeDispatchHistoryService;
import com.skyeye.service.JobMateMationService;
import com.skyeye.sms.entity.SmsSendMessage;
import com.skyeye.sms.service.SmsSendService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName: SmsSendConsumer
 * @Description: 短信发送消费者
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/28 21:37
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
@RocketMQMessageListener(
        topic = "${topic.sms-send-service}",
        consumerGroup = "${topic.sms-send-service}",
        selectorExpression = "${spring.profiles.active}")
public class SmsSendConsumer implements RocketMQListener<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsSendConsumer.class);

    @Autowired
    private JobMateMationService jobMateMationService;

    @Autowired
    private SmsSendService smsSendService;

    @Autowired
    private INoticeDispatchHistoryService iNoticeDispatchHistoryService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        String jobId = map.get("jobMateId").toString();
        String content = map.get("content").toString();
        String channelLogId = MapUtil.getStr(map, "channelLogId");
        try {
            String tenantId = StrUtil.EMPTY;
            if (tenantEnable) {
                tenantId = map.get("tenantId").toString();
                TenantContext.setTenantId(tenantId);
            }
            TenantContext.setTenantId(map.get("tenantId").toString());
            // 任务开始
            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, "");
            // 发送短信
            SmsSendMessage message = JSONUtil.toBean(content, SmsSendMessage.class);
            smsSendService.doSendSms(message);
            // 任务完成
            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, "");
            iNoticeDispatchHistoryService.updateChannelLog(channelLogId,
                NoticeDispatchChannelLogUpdateBody.STATUS_SUCCESS, jobId, StrUtil.EMPTY);
        } catch (Exception e) {
            LOGGER.warn("notification failed, reason is {}.", e);
            // 任务失败
            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL,
                StrUtil.blankToDefault(e.getMessage(), StrUtil.EMPTY));
            iNoticeDispatchHistoryService.updateChannelLog(channelLogId,
                NoticeDispatchChannelLogUpdateBody.STATUS_FAIL, jobId, e.getMessage());
        }
    }

}
