/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.classenum.EquipmentState;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.exception.CustomException;
import com.skyeye.maintenance.classenum.EquipmentMaintainTaskState;
import com.skyeye.maintenance.entity.EquipmentMaintainOrderSparePartDetail;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.maintenance.dao.EquipmentMaintainOrderDao;
import com.skyeye.maintenance.entity.EquipmentMaintainOrder;
import com.skyeye.maintenance.service.EquipmentMaintainOrderItemService;
import com.skyeye.maintenance.service.EquipmentMaintainOrderService;
import com.skyeye.maintenance.service.EquipmentMaintainOrderSparePartDetailService;
import com.skyeye.maintenance.service.MaintenancePlanService;
import com.skyeye.rest.sealservice.service.IServiceUserStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 设备保养任务服务层
 */
@Service
@SkyeyeService(name = "设备保养任务", groupName = "设备保养")
public class EquipmentMaintainOrderServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentMaintainOrderDao, EquipmentMaintainOrder>
    implements EquipmentMaintainOrderService {

    @Autowired
    private EquipmentMaintainOrderItemService equipmentMaintainOrderItemService;

    @Autowired
    private EquipmentMaintainOrderSparePartDetailService equipmentMaintainOrderSparePartDetailService;

    @Autowired
    private MaintenancePlanService maintenancePlanService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private IServiceUserStockService iServiceUserStockService;

    @Override
    public void createPrepose(EquipmentMaintainOrder entity) {
        Equipment equipment = equipmentService.selectById(entity.getEquipmentId());
        if (ObjectUtil.isNotEmpty(equipment) && EquipmentState.SCRAPPED.getKey().equals(equipment.getEquipmentState())) {
            throw new CustomException("设备已报废，无法新增保养任务");
        }
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        entity.setOddNumber(iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business));
        if (entity.getState() == null) {
            entity.setState(EquipmentMaintainTaskState.PENDING.getKey());
        }
    }

    @Override
    protected void createPrepose(List<EquipmentMaintainOrder> list) {
        list.forEach(task -> {
            Map<String, Object> business = BeanUtil.beanToMap(task);
            task.setOddNumber(iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business));
            if (task.getState() == null) {
                task.setState(EquipmentMaintainTaskState.PENDING.getKey());
            }
        });
    }

    @Override
    protected void updatePrepose(EquipmentMaintainOrder entity) {
        EquipmentMaintainOrder oldTask = selectById(entity.getId());
        if (EquipmentMaintainTaskState.CANCELLED.getKey().equals(oldTask.getState())) {
            entity.setState(EquipmentMaintainTaskState.PENDING.getKey());
        }
    }

    @Override
    public void writePostpose(EquipmentMaintainOrder entity, String userId) {
        if (entity.getMaintainOrderItemList() != null) {
            equipmentMaintainOrderItemService.saveList(entity.getId(), entity.getMaintainOrderItemList());
        }
        if (entity.getSparePartDetailList() != null) {
            equipmentMaintainOrderSparePartDetailService.saveLinkList(entity.getId(), entity.getSparePartDetailList());
        }
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePreExecution(EquipmentMaintainOrder entity) {
        equipmentMaintainOrderItemService.deleteByParentId(entity.getId());
        equipmentMaintainOrderSparePartDetailService.deleteByParentId(entity.getId());
        refreshCache(entity.getId());
    }

    @Override
    protected QueryWrapper<EquipmentMaintainOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentMaintainOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentMaintainOrder::getState), commonPageInfo.getState());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentMaintainOrder::getPlanId), commonPageInfo.getObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentMaintainOrder::getEquipmentId), commonPageInfo.getHolderId());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentMaintainOrder::getPlannedStartTime));
        return queryWrapper;
    }

    @Override
    public EquipmentMaintainOrder getDataFromDb(String id) {
        EquipmentMaintainOrder task = super.getDataFromDb(id);
        task.setMaintainOrderItemList(equipmentMaintainOrderItemService.selectByParentId(id));
        task.setSparePartDetailList(equipmentMaintainOrderSparePartDetailService.selectByParentId(id));
        return task;
    }

    @Override
    public EquipmentMaintainOrder selectById(String id) {
        EquipmentMaintainOrder task = super.selectById(id);
        if (task == null) {
            return null;
        }
        maintenancePlanService.setDataMation(task, EquipmentMaintainOrder::getPlanId);
        equipmentService.setDataMation(task, EquipmentMaintainOrder::getEquipmentId);
        if (StrUtil.isNotEmpty(task.getExecutorId())) {
            Map<String, Map<String, Object>> executorMap = iAuthUserService.queryUserMationListByStaffIds(
                Collections.singletonList(task.getExecutorId()));
            task.setExecutorMation(executorMap.get(task.getExecutorId()));
        }
        if (CollectionUtil.isEmpty(task.getSparePartDetailList())) {
            return task;
        }
        materialService.setDataMation(task.getSparePartDetailList(), EquipmentMaintainOrderSparePartDetail::getMaterialId);
        materialNormsService.setDataMation(task.getSparePartDetailList(), EquipmentMaintainOrderSparePartDetail::getNormsId);
        List<String> normsIds = task.getSparePartDetailList().stream()
            .map(EquipmentMaintainOrderSparePartDetail::getNormsId)
            .collect(Collectors.toList());
        Map<String, Map<String, Object>> serviceUserStockMap = iServiceUserStockService.queryUserStock(normsIds);
        task.getSparePartDetailList().forEach(detail ->
            detail.setServiceUserStock(serviceUserStockMap.get(detail.getNormsId())));
        return task;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        maintenancePlanService.setMationForMap(beans, "planId", "planMation");
        equipmentService.setMationForMap(beans, "equipmentId", "equipmentMation");
        List<String> executorIds = beans.stream()
            .filter(bean -> bean.get("executorId") != null)
            .map(bean -> bean.get("executorId").toString())
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(executorIds)) {
            Map<String, Map<String, Object>> executorMap = iAuthUserService.queryUserMationListByStaffIds(executorIds);
            beans.forEach(bean -> {
                if (bean.get("executorId") != null) {
                    bean.put("executorMation", executorMap.get(bean.get("executorId").toString()));
                }
            });
        }
        return beans;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void startTask(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        EquipmentMaintainOrder task = selectById(id);
        if (task == null) {
            throw new CustomException("任务不存在");
        }
        if (!EquipmentMaintainTaskState.PENDING.getKey().equals(task.getState())) {
            throw new CustomException("只有待执行状态的任务才能开始执行");
        }
        String userId = inputObject.getLogParamsStatic().get("id").toString();
        task.setState(EquipmentMaintainTaskState.IN_PROGRESS.getKey());
        task.setActualStartTime(DateUtil.getTimeAndToString());
        updateEntity(task, userId);
        outputObject.setBean(task);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void completeTask(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        EquipmentMaintainOrder task = selectById(id);
        if (task == null) {
            throw new CustomException("任务不存在");
        }
        if (!EquipmentMaintainTaskState.IN_PROGRESS.getKey().equals(task.getState())) {
            throw new CustomException("只有执行中状态的任务才能完成");
        }
        String userId = inputObject.getLogParamsStatic().get("id").toString();
        equipmentMaintainOrderSparePartDetailService.deductStockByParentId(id);
        task.setState(EquipmentMaintainTaskState.COMPLETED.getKey());
        task.setActualEndTime(DateUtil.getTimeAndToString());
        updateEntity(task, userId);
        outputObject.setBean(selectById(id));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void cancelTask(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        EquipmentMaintainOrder task = selectById(id);
        if (task == null) {
            throw new CustomException("任务不存在");
        }
        if (!EquipmentMaintainTaskState.PENDING.getKey().equals(task.getState())
            && !EquipmentMaintainTaskState.IN_PROGRESS.getKey().equals(task.getState())
            && !EquipmentMaintainTaskState.TIMEOUT.getKey().equals(task.getState())) {
            throw new CustomException("只有待执行、执行中或已超时状态的任务才能取消");
        }
        String userId = inputObject.getLogParamsStatic().get("id").toString();
        if (EquipmentMaintainTaskState.IN_PROGRESS.getKey().equals(task.getState())) {
            equipmentMaintainOrderSparePartDetailService.deleteByParentId(id);
            refreshCache(id);
            task.setActualEndTime(DateUtil.getTimeAndToString());
        }
        task.setState(EquipmentMaintainTaskState.CANCELLED.getKey());
        updateEntity(task, userId);
        outputObject.setBean(task);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void reassignTimeoutTask(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        EquipmentMaintainOrder task = selectById(id);
        if (task == null) {
            throw new CustomException("任务不存在");
        }
        if (!EquipmentMaintainTaskState.TIMEOUT.getKey().equals(task.getState())) {
            throw new CustomException("只有已超时状态的任务才能重新分配");
        }
        String userId = inputObject.getLogParamsStatic().get("id").toString();
        if (inputObject.getParams().get("executorId") != null) {
            task.setExecutorId(inputObject.getParams().get("executorId").toString());
        }
        if (inputObject.getParams().get("plannedStartTime") != null) {
            task.setPlannedStartTime(inputObject.getParams().get("plannedStartTime").toString());
        }
        task.setState(EquipmentMaintainTaskState.PENDING.getKey());
        task.setActualStartTime(null);
        task.setActualEndTime(null);
        updateEntity(task, userId);
        outputObject.setBean(task);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}
