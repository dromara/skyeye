/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.job.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.enumeration.NoticeUserMessageTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.eve.rest.mq.JobMateUpdateMation;
import com.skyeye.eve.rest.notice.NoticeDispatchBody;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.eve.service.INoticeDispatchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备巡检单派工通知（统一走 NoticeDispatch）
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
    private INoticeDispatchService iNoticeDispatchService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        String jobId = map.get("jobMateId").toString();
        try {
            if (tenantEnable) {
                TenantContext.setTenantId(map.get("tenantId").toString());
            }
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, StrUtil.EMPTY);
            String serviceId = map.get("serviceId").toString();
            EquipmentInspectionOrder order = equipmentInspectionOrderService.selectById(serviceId);
            if (ObjectUtil.isNotEmpty(order)) {
                List<String> receiveUserIds = new ArrayList<>();
                Map<String, Map<String, Object>> extras = new HashMap<>();
                if (StrUtil.isNotEmpty(order.getServiceUserId())) {
                    receiveUserIds.add(order.getServiceUserId());
                    Map<String, Object> userMation = iAuthUserService.queryDataMationById(order.getServiceUserId());
                    Map<String, Object> extra = new HashMap<>();
                    extra.put("email", userMation.getOrDefault("email", StrUtil.EMPTY));
                    extras.put(order.getServiceUserId(), extra);
                }
                if (CollectionUtil.isNotEmpty(order.getCooperationUserId())) {
                    for (String cooperationUserId : order.getCooperationUserId()) {
                        if (StrUtil.isEmpty(cooperationUserId) || receiveUserIds.contains(cooperationUserId)) {
                            continue;
                        }
                        receiveUserIds.add(cooperationUserId);
                        Map<String, Object> user = iAuthUserService.queryDataMationById(cooperationUserId);
                        Map<String, Object> extra = new HashMap<>();
                        extra.put("email", user.getOrDefault("email", StrUtil.EMPTY));
                        extras.put(cooperationUserId, extra);
                    }
                }
                if (CollectionUtil.isNotEmpty(receiveUserIds)) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("id", order.getId());
                    params.put("orderNum", order.getOddNumber());
                    NoticeDispatchBody body = new NoticeDispatchBody();
                    body.setTypeCode(NoticeUserMessageTypeEnum.EQUIPMENT_INSPECTION.getKey());
                    body.setReceiveUserIds(receiveUserIds);
                    body.setParams(params);
                    body.setCreateUserId(CommonConstants.ADMIN_USER_ID);
                    body.setObjectData(JSONUtil.toJsonStr(order));
                    body.setReceiverExtras(extras);
                    iNoticeDispatchService.dispatch(body);
                }
            }
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, StrUtil.EMPTY);
        } catch (Exception e) {
            log.warn("Dispatch notice failed, reason is {}.", e);
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, StrUtil.EMPTY);
        }
    }

    private void updateJobMation(String jobId, String status, String responseBody) {
        JobMateUpdateMation jobMateUpdateMation = new JobMateUpdateMation();
        jobMateUpdateMation.setJobId(jobId);
        jobMateUpdateMation.setStatus(status);
        jobMateUpdateMation.setResponseBody(responseBody);
        iJobMateMationService.comMQJobMation(jobMateUpdateMation);
    }

}
