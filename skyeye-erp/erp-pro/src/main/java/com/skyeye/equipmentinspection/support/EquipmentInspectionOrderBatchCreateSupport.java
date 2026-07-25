/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.support;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanEquipmentService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 方案定时下发批量建单：方案/设备各查一次，ThreadLocal 供 validator 复用，循环内不再查库
 */
@Component
public class EquipmentInspectionOrderBatchCreateSupport {

    private static final ThreadLocal<BatchValidateContext> BATCH_VALIDATE_CTX = new ThreadLocal<>();

    @Lazy
    @Autowired
    private EquipmentInspectionOrderService equipmentInspectionOrderService;

    @Lazy
    @Autowired
    private EquipmentInspectionPlanService equipmentInspectionPlanService;

    @Autowired
    private EquipmentInspectionPlanEquipmentService equipmentInspectionPlanEquipmentService;

    @Autowired
    private EquipmentService equipmentService;

    /**
     * 批量建单入口（由 Sync 调用）
     */
    public void createEntityBatchForPlanGenerate(List<EquipmentInspectionOrder> orderList, String userId) {
        if (CollectionUtil.isEmpty(orderList)) {
            return;
        }
        try {
            BATCH_VALIDATE_CTX.set(buildBatchValidateContext(orderList));
            equipmentInspectionOrderService.createEntity(orderList, userId);
        } finally {
            BATCH_VALIDATE_CTX.remove();
        }
    }

    /**
     * 供 Order.validatorEntity：有批量上下文则走批量校验，否则单条校验
     */
    public void validateOnWrite(EquipmentInspectionOrder entity) {
        BatchValidateContext batchCtx = BATCH_VALIDATE_CTX.get();
        if (batchCtx != null) {
            validateAgainstBatchContext(entity, batchCtx);
            return;
        }
        validatePlanAndEquipmentOnce(entity);
    }

    private BatchValidateContext buildBatchValidateContext(List<EquipmentInspectionOrder> orderList) {
        Set<String> planIds = orderList.stream()
            .map(EquipmentInspectionOrder::getPlanId)
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.toSet());
        if (planIds.size() != 1) {
            throw new CustomException("批量下发巡检单必须属于同一方案");
        }
        String planId = planIds.iterator().next();
        EquipmentInspectionPlan plan = equipmentInspectionPlanService.selectById(planId);
        if (StrUtil.isBlank(plan.getId())) {
            throw new CustomException("巡检方案不存在");
        }
        Set<String> allowedEquipmentIds = CollectionUtil.isEmpty(plan.getEquipmentId())
            ? new HashSet<>(equipmentInspectionPlanEquipmentService.selectByParentId(planId))
            : plan.getEquipmentId().stream().filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        if (CollectionUtil.isEmpty(allowedEquipmentIds)) {
            throw new CustomException("巡检方案未关联设备");
        }
        List<String> needEquipmentIds = orderList.stream()
            .map(EquipmentInspectionOrder::getEquipmentId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Set<String> existEquipmentIds = Collections.emptySet();
        if (CollectionUtil.isNotEmpty(needEquipmentIds)) {
            existEquipmentIds = equipmentService.selectByIds(needEquipmentIds.toArray(new String[]{})).stream()
                .filter(e -> StrUtil.isNotBlank(e.getId()))
                .map(Equipment::getId)
                .collect(Collectors.toSet());
        }
        return new BatchValidateContext(planId, allowedEquipmentIds, existEquipmentIds);
    }

    private void validateAgainstBatchContext(EquipmentInspectionOrder entity, BatchValidateContext batchCtx) {
        if (!batchCtx.planId.equals(entity.getPlanId())) {
            throw new CustomException("巡检方案不存在");
        }
        if (!batchCtx.existEquipmentIds.contains(entity.getEquipmentId())) {
            throw new CustomException("设备不存在: " + entity.getEquipmentId());
        }
        if (!batchCtx.allowedEquipmentIds.contains(entity.getEquipmentId())) {
            throw new CustomException("所选设备不属于该巡检方案");
        }
    }

    private void validatePlanAndEquipmentOnce(EquipmentInspectionOrder entity) {
        EquipmentInspectionPlan plan = equipmentInspectionPlanService.selectById(entity.getPlanId());
        if (StrUtil.isBlank(plan.getId())) {
            throw new CustomException("巡检方案不存在");
        }
        Equipment equipment = equipmentService.selectById(entity.getEquipmentId());
        if (StrUtil.isBlank(equipment.getId())) {
            throw new CustomException("设备不存在: " + entity.getEquipmentId());
        }
        List<String> planEquipmentIds = plan.getEquipmentId();
        if (CollectionUtil.isEmpty(planEquipmentIds)) {
            planEquipmentIds = equipmentInspectionPlanEquipmentService.selectByParentId(entity.getPlanId());
        }
        if (CollectionUtil.isEmpty(planEquipmentIds) || !planEquipmentIds.contains(entity.getEquipmentId())) {
            throw new CustomException("所选设备不属于该巡检方案");
        }
    }

    private static class BatchValidateContext {
        private final String planId;
        private final Set<String> allowedEquipmentIds;
        private final Set<String> existEquipmentIds;

        private BatchValidateContext(String planId, Set<String> allowedEquipmentIds, Set<String> existEquipmentIds) {
            this.planId = planId;
            this.allowedEquipmentIds = allowedEquipmentIds;
            this.existEquipmentIds = existEquipmentIds;
        }
    }

}
