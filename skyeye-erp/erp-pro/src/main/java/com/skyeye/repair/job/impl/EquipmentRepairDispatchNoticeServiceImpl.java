/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.job.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.rest.mq.JobMateUpdateMation;
import com.skyeye.eve.rest.notice.NoticeDispatchBody;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.eve.service.INoticeDispatchService;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备维修单派工通知（统一走 NoticeDispatch）
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "${topic.equipment-repair-dispatch-service}",
    consumerGroup = "${topic.equipment-repair-dispatch-service}",
    selectorExpression = "${spring.profiles.active}")
public class EquipmentRepairDispatchNoticeServiceImpl implements RocketMQListener<String> {

    @Autowired
    private EquipmentRepairOrderService equipmentRepairOrderService;

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
            EquipmentRepairOrder repairOrder = equipmentRepairOrderService.selectById(serviceId);
            if (ObjectUtil.isNotEmpty(repairOrder) && StrUtil.isNotEmpty(repairOrder.getServiceUserId())) {
                Map<String, Object> userMation = iAuthUserService.queryDataMationById(repairOrder.getServiceUserId());
                Map<String, Object> params = new HashMap<>();
                params.put("id", repairOrder.getId());
                params.put("orderNum", repairOrder.getOddNumber());
                Map<String, Map<String, Object>> extras = new HashMap<>();
                Map<String, Object> extra = new HashMap<>();
                extra.put("email", userMation.getOrDefault("email", StrUtil.EMPTY));
                extras.put(repairOrder.getServiceUserId(), extra);

                NoticeDispatchBody body = new NoticeDispatchBody();
                body.setTypeCode("EQUIPMENT_REPAIR_REMINDER");
                body.setReceiveUserIds(Collections.singletonList(repairOrder.getServiceUserId()));
                body.setParams(params);
                body.setCreateUserId(CommonConstants.ADMIN_USER_ID);
                body.setObjectData(JSONUtil.toJsonStr(repairOrder));
                body.setReceiverExtras(extras);
                iNoticeDispatchService.dispatch(body);
            }
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, StrUtil.EMPTY);
        } catch (Exception e) {
            log.warn("Equipment repair dispatch notice failed, reason is {}.", e);
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
