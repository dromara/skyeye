/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.MailUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.email.dao.EmailDao;
import com.skyeye.eve.service.ISystemFoundationSettingsService;
import com.skyeye.util.MqSendUtil;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ComplexMailDeliveryServiceImpl
 * @Description: 邮件发送
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 21:57
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
@RocketMQMessageListener(
    topic = "${topic.complex-mail-delivery-service}",
    consumerGroup = "${topic.complex-mail-delivery-service}",
    selectorExpression = "${spring.profiles.active}")
public class ComplexMailDeliveryServiceImpl implements RocketMQListener<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComplexMailDeliveryServiceImpl.class);

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @Autowired
    private EmailDao emailDao;

    @Autowired
    private ISystemFoundationSettingsService iSystemFoundationSettingsService;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        String jobId = map.get("jobMateId").toString();
        try {
            String tenantId = StrUtil.EMPTY;
            if (tenantEnable) {
                tenantId = map.get("tenantId").toString();
                TenantContext.setTenantId(tenantId);
            }
            // 任务开始
            MqSendUtil.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, StrUtil.EMPTY);
            // 获取服务器信息
            Map<String, Object> emailServer = iSystemFoundationSettingsService.querySystemFoundationSettingsList();
            String title = map.get("title").toString();// 标题
            String content = map.get("content").toString();// 邮件内容
            String toPeople = map.get("toPeople").toString();// 收件人
            String toCc = map.get("toCc").toString();// 抄送人
            String toBCc = map.get("toBcc").toString();// 暗送人
            String username = map.get("userAddress").toString();// 登录邮箱账号
            String password = map.get("userPassword").toString();// 密码
            String emailEnclosure = map.get("emailEnclosure").toString();
            String emailId = map.get("emailId").toString();
            List<Map<String, Object>> emailEnclosureList = JSONUtil.toList(emailEnclosure, null);
            List<Map<String, Object>> beans = new ArrayList<>();
            for (int i = 0; i < emailEnclosureList.size(); i++) {
                Map<String, Object> j = emailEnclosureList.get(i);
                Map<String, Object> bean = new HashMap<>();
                bean.put("fileName", j.get("fileName"));
                bean.put("filePath", j.get("filePath"));
                beans.add(bean);
            }
            // 发送邮件
            String messageId = new MailUtil(username, password, emailServer.get("emailSendServer").toString())
                .send(toPeople, toCc, toBCc, title, content, tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY), beans);
            if (!ToolUtil.isBlank(messageId)) {
                Map<String, Object> emailEditMessageId = new HashMap<>();
                emailEditMessageId.put("id", emailId);
                emailEditMessageId.put("messageId", messageId);
                if (tenantEnable) {
                    emailEditMessageId.put("tenantId", tenantId);
                }
                emailDao.editEmailMessageIdByEmailId(emailEditMessageId);
            }
            // 任务完成
            MqSendUtil.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, StrUtil.EMPTY);
        } catch (Exception e) {
            LOGGER.warn("send email failed, reason is {}.", e);
            // 任务失败
            MqSendUtil.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, StrUtil.EMPTY);
        }
    }

}
