/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionTaskState;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionTaskDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionTask;
import com.skyeye.equipmentinspection.service.EquipmentInspectionItemService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionTaskService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentInspectionTaskServiceImpl
 * @Description: 设备巡检任务服务层
 */
@Service
@SkyeyeService(name = "设备巡检任务", groupName = "设备巡检")
public class EquipmentInspectionTaskServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentInspectionTaskDao, EquipmentInspectionTask>
    implements EquipmentInspectionTaskService {

    @Autowired
    private EquipmentInspectionPlanService equipmentInspectionPlanService;

    @Autowired
    private EquipmentInspectionItemService equipmentInspectionItemService;

    @Autowired
    private EquipmentService equipmentService;

    @Override
    public void createPrepose(EquipmentInspectionTask entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
        if (entity.getState() == null) {
            entity.setState(EquipmentInspectionTaskState.PENDING.getKey());
        }
    }

    @Override
    protected void createPrepose(List<EquipmentInspectionTask> list) {
        list.forEach(task -> {
            Map<String, Object> business = BeanUtil.beanToMap(task);
            String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
            task.setOddNumber(oddNumber);
            if (task.getState() == null) {
                task.setState(EquipmentInspectionTaskState.PENDING.getKey());
            }
        });
    }

    @Override
    protected void updatePrepose(EquipmentInspectionTask entity) {
        EquipmentInspectionTask oldTask = selectById(entity.getId());
        if (EquipmentInspectionTaskState.CANCELLED.getKey().equals(oldTask.getState())) {
            entity.setState(EquipmentInspectionTaskState.PENDING.getKey());
        }
    }

    @Override
    protected QueryWrapper<EquipmentInspectionTask> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentInspectionTask> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionTask::getState), commonPageInfo.getState());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionTask::getPlanId), commonPageInfo.getObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionTask::getEquipmentId), commonPageInfo.getHolderId());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentInspectionTask::getPlannedStartTime));
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        equipmentInspectionPlanService.setMationForMap(beans, "planId", "planMation");
        equipmentService.setMationForMap(beans, "equipmentId", "equipmentMation");
        equipmentInspectionItemService.setMationForMap(beans, "itemId", "itemMation");
        setExecutorMationForMap(beans);
        return beans;
    }

    @Override
    public EquipmentInspectionTask selectById(String id) {
        EquipmentInspectionTask task = super.selectById(id);
        Map<String, Object> taskMap = BeanUtil.beanToMap(task, false, true);
        List<Map<String, Object>> beans = Collections.singletonList(taskMap);
        equipmentInspectionPlanService.setMationForMap(beans, "planId", "planMation");
        Object planMation = beans.get(0).get("planMation");
        if (planMation instanceof Map) {
            task.setPlanMation((Map<String, Object>) planMation);
        }
        equipmentService.setDataMation(task, EquipmentInspectionTask::getEquipmentId);
        equipmentInspectionItemService.setDataMation(task, EquipmentInspectionTask::getItemId);
        setExecutorMation(task);
        return task;
    }

    private void setExecutorMationForMap(List<Map<String, Object>> beans) {
        List<String> executorIds = beans.stream()
            .map(bean -> bean.get("executorId"))
            .filter(ObjectUtil::isNotNull)
            .map(Object::toString)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(executorIds)) {
            return;
        }
        Map<String, Map<String, Object>> executorMap = iAuthUserService.queryUserMationListByStaffIds(executorIds);
        beans.forEach(bean -> {
            if (bean.get("executorId") != null) {
                bean.put("executorMation", executorMap.get(bean.get("executorId").toString()));
            }
        });
    }

    private void setExecutorMation(EquipmentInspectionTask task) {
        if (StrUtil.isEmpty(task.getExecutorId())) {
            return;
        }
        Map<String, Map<String, Object>> executorMap = iAuthUserService.queryUserMationListByStaffIds(
            java.util.Collections.singletonList(task.getExecutorId()));
        task.setExecutorMation(executorMap.get(task.getExecutorId()));
    }

    @Override
    public void startTask(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        EquipmentInspectionTask task = selectById(id);
        if (!EquipmentInspectionTaskState.PENDING.getKey().equals(task.getState())) {
            throw new CustomException("只有待执行状态的任务才能开始执行");
        }
        String userId = inputObject.getLogParamsStatic().get("id").toString();
        task.setState(EquipmentInspectionTaskState.IN_PROGRESS.getKey());
        task.setActualStartTime(DateUtil.getTimeAndToString());
        updateEntity(task, userId);
        outputObject.setBean(task);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void completeTask(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        EquipmentInspectionTask task = selectById(id);
        if (!EquipmentInspectionTaskState.IN_PROGRESS.getKey().equals(task.getState())) {
            throw new CustomException("只有执行中状态的任务才能完成");
        }
        String userId = inputObject.getLogParamsStatic().get("id").toString();
        task.setState(EquipmentInspectionTaskState.COMPLETED.getKey());
        task.setActualEndTime(DateUtil.getTimeAndToString());
        updateEntity(task, userId);
        outputObject.setBean(task);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void cancelTask(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        EquipmentInspectionTask task = selectById(id);
        if (!EquipmentInspectionTaskState.PENDING.getKey().equals(task.getState())
            && !EquipmentInspectionTaskState.IN_PROGRESS.getKey().equals(task.getState())
            && !EquipmentInspectionTaskState.TIMEOUT.getKey().equals(task.getState())) {
            throw new CustomException("只有待执行、执行中或已超时状态的任务才能取消");
        }
        String userId = inputObject.getLogParamsStatic().get("id").toString();
        if (EquipmentInspectionTaskState.IN_PROGRESS.getKey().equals(task.getState())) {
            task.setActualEndTime(DateUtil.getTimeAndToString());
        }
        task.setState(EquipmentInspectionTaskState.CANCELLED.getKey());
        updateEntity(task, userId);
        outputObject.setBean(task);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void reassignTimeoutTask(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        EquipmentInspectionTask task = selectById(id);
        if (!EquipmentInspectionTaskState.TIMEOUT.getKey().equals(task.getState())) {
            throw new CustomException("只有已超时状态的任务才能重新分配");
        }
        String userId = inputObject.getLogParamsStatic().get("id").toString();
        if (inputObject.getParams().get("executorId") != null) {
            task.setExecutorId(inputObject.getParams().get("executorId").toString());
        }
        if (inputObject.getParams().get("plannedStartTime") != null) {
            task.setPlannedStartTime(inputObject.getParams().get("plannedStartTime").toString());
        }
        task.setState(EquipmentInspectionTaskState.PENDING.getKey());
        task.setActualStartTime(null);
        task.setActualEndTime(null);
        updateEntity(task, userId);
        outputObject.setBean(task);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}
