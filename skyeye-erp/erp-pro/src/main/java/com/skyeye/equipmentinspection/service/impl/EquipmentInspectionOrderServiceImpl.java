/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.classenum.EquipmentState;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionOrderDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionTaskService;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionOrderServiceImpl
 * @Description: 设备巡检单服务实现类
 */
@Service
@SkyeyeService(name = "设备巡检单", groupName = "设备巡检", flowable = true, allowDynamicAttrKey = false)
public class EquipmentInspectionOrderServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentInspectionOrderDao, EquipmentInspectionOrder>
    implements EquipmentInspectionOrderService {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EquipmentInspectionTaskService equipmentInspectionTaskService;

    @Override
    protected QueryWrapper<EquipmentInspectionOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentInspectionOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getTaskId), commonPageInfo.getObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("planId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getPlanId), commonPageInfo.getCustomParamsMapStr("planId"));
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("equipmentId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), commonPageInfo.getCustomParamsMapStr("equipmentId"));
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime));
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        equipmentInspectionTaskService.setMationForMap(beans, "taskId", "taskMation");
        iAuthUserService.setMationForMap(beans, "inspectorUserId", "inspectorUserMation");
        return beans;
    }

    @Override
    public EquipmentInspectionOrder selectById(String id) {
        EquipmentInspectionOrder order = super.selectById(id);
        equipmentInspectionTaskService.setDataMation(order, EquipmentInspectionOrder::getTaskId);
        iAuthUserService.setDataMation(order, EquipmentInspectionOrder::getInspectorUserId);
        return order;
    }

    @Override
    public void createPrepose(EquipmentInspectionOrder entity) {
        normalizeEquipmentRunStatus(entity);
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        entity.setOddNumber(iCodeRuleService.getNextCodeByClassName(getClass().getName(), business));
    }

    @Override
    public void validatorEntity(EquipmentInspectionOrder entity) {
        normalizeEquipmentRunStatus(entity);
        super.validatorEntity(entity);
    }

    /**
     * 巡检审批通过：将巡检结果回写设备档案状态。
     * 巡检异常时默认映射为「带病运行」；若巡检员指定了其它运行状态（如维修中），则以其为准。
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void approvalEndIsSuccess(EquipmentInspectionOrder entity) {
        EquipmentInspectionOrder order = selectById(entity.getId());
        if (StrUtil.isBlank(order.getEquipmentId())) {
            return;
        }
        equipmentService.editEquipmentStateById(order.getEquipmentId(), resolveSyncEquipmentState(order));
    }

    private Integer resolveSyncEquipmentState(EquipmentInspectionOrder order) {
        if (EquipmentInspectionResultType.ABNORMAL.getKey().equals(order.getOverallResult())) {
            if (order.getEquipmentRunStatus() != null && !EquipmentState.NORMAL.getKey().equals(order.getEquipmentRunStatus())) {
                return order.getEquipmentRunStatus();
            }
            return EquipmentState.DEGRADED.getKey();
        }
        return EquipmentState.NORMAL.getKey();
    }

    private void normalizeEquipmentRunStatus(EquipmentInspectionOrder entity) {
        if (entity.getEquipmentRunStatus() == null && entity.getEquipmentState() != null) {
            entity.setEquipmentRunStatus(entity.getEquipmentState());
        }
        if (entity.getEquipmentRunStatus() == null) {
            entity.setEquipmentRunStatus(EquipmentState.NORMAL.getKey());
        }
    }

}
