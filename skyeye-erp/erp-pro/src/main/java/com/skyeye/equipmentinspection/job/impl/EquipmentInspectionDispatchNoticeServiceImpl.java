/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.job.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Joiner;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.enumeration.NoticeUserMessageTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.MailUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.eve.rest.mq.JobMateUpdateMation;
import com.skyeye.eve.rest.notice.UserMessage;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.eve.service.IUserNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 设备巡检单派工通知
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "${topic.equipment-inspection-dispatch-service}",
    consumerGroup = "${topic.equipment-inspection-dispatch-service}",
    selectorExpression = "${spring.profiles.active}")
public class EquipmentInspectionDispatchNoticeServiceImpl implements RocketMQListener<String> {

    @Autowired
    private EquipmentInspectionOrderService equipmentInspectionOrderService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private IUserNoticeService iUserNoticeService;

    @Autowired
    private Executor watiWorkerSendEmailExecutor;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        String jobId = map.get("jobMateId").toString();
        try {
            String tenantId;
            if (tenantEnable) {
                tenantId = map.get("tenantId").toString();
                TenantContext.setTenantId(tenantId);
            }
            // 任务开始
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, StrUtil.EMPTY);
            // 巡检单id
            String serviceId = map.get("serviceId").toString();
            log.info("开始设备巡检派工通知，巡检单id：{}，主题：{}，消息内容：{}", serviceId,
                MqConstants.JobMateMationJobType.EQUIPMENT_INSPECTION_DISPATCH, data);
            // 获取巡检单接收人和协助人id
            EquipmentInspectionOrder order = equipmentInspectionOrderService.selectById(serviceId);
            // 如果巡检单信息不为空
            if (ObjectUtil.isNotEmpty(order)) {
                // 调用消息系统添加通知
                String orderStr = JSONUtil.toJsonStr(order);
                List<UserMessage> userMessageBoxList = new ArrayList<>();
                log.info("接收人是：{}", order.getServiceUserId());
                // 1.接收人通知
                if (StrUtil.isNotEmpty(order.getServiceUserId())) {
                    Map<String, Object> userMation = iAuthUserService.queryDataMationById(order.getServiceUserId());
                    // 1.1内部消息
                    String content = NoticeUserMessageTypeEnum.getNoticeServiceUserContent(
                        order.getOddNumber(), userMation.get("name").toString());
                    UserMessage userMessage = getUserNotice(order.getServiceUserId(), content,
                        userMation.getOrDefault("email", StrUtil.EMPTY).toString(), orderStr);
                    userMessageBoxList.add(userMessage);
                }
                log.info("协助人是：{}", order.getCooperationUserId());
                // 2.协助人通知
                if (CollectionUtil.isNotEmpty(order.getCooperationUserId())) {
                    // 获取协助人
                    List<Map<String, Object>> cooperationUser = iAuthUserService
                        .queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(order.getCooperationUserId()));
                    for (Map<String, Object> user : cooperationUser) {
                        // 2.1内部消息
                        String content = NoticeUserMessageTypeEnum.getNoticeCooperationUserContent(
                            order.getOddNumber(), user.get("name").toString());
                        UserMessage userMessage = getUserNotice(user.get("id").toString(), content,
                            user.getOrDefault("email", StrUtil.EMPTY).toString(), orderStr);
                        userMessageBoxList.add(userMessage);
                    }
                }
                log.info("通知列表：{}", JSONUtil.toJsonStr(userMessageBoxList));
                if (!userMessageBoxList.isEmpty()) {
                    iUserNoticeService.insertUserNoticeMation(userMessageBoxList);
                    // 发送消息
                    watiWorkerSendEmailExecutor.execute(() -> {
                        for (UserMessage userMessage : userMessageBoxList) {
                            if (ToolUtil.isEmail(userMessage.getEmail()) && !ToolUtil.isBlank(userMessage.getEmail())) {
                                new MailUtil().send(userMessage.getEmail(),
                                    NoticeUserMessageTypeEnum.WORK_ORDER_REMINDER.getValue(), userMessage.getContent());
                            }
                        }
                    });
                }
            }
            // 任务完成
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, StrUtil.EMPTY);
        } catch (Exception e) {
            log.warn("Dispatch notice failed, reason is {}.", e);
            // 任务失败
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, StrUtil.EMPTY);
        }
    }

    private UserMessage getUserNotice(String userId, String content, String email, String objectData) {
        UserMessage userMessage = new UserMessage();
        userMessage.setName(NoticeUserMessageTypeEnum.WORK_ORDER_REMINDER.getValue());
        userMessage.setRemark(NoticeUserMessageTypeEnum.WORK_ORDER_REMINDER.getRemark());
        userMessage.setEmail(email);
        userMessage.setContent(content);
        userMessage.setReceiveId(userId);
        userMessage.setType(NoticeUserMessageTypeEnum.WORK_ORDER_REMINDER.getKey());
        userMessage.setCreateUserId(CommonConstants.ADMIN_USER_ID);
        userMessage.setObjectData(objectData);
        return userMessage;
    }

    private void updateJobMation(String jobId, String status, String responseBody) {
        JobMateUpdateMation jobMateUpdateMation = new JobMateUpdateMation();
        jobMateUpdateMation.setJobId(jobId);
        jobMateUpdateMation.setStatus(status);
        jobMateUpdateMation.setResponseBody(responseBody);
        iJobMateMationService.comMQJobMation(jobMateUpdateMation);
    }

}
