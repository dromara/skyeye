/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.classenum.EquipmentState;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionAssignType;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionCheckResult;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionOrderState;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionOrderDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import com.skyeye.equipmentinspection.support.EquipmentInspectionOrderQuerySupport;
import com.skyeye.exception.CustomException;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionOrderServiceImpl
 * @Description: 设备巡检单服务层（CRUD 钩子 + 派工/接单/填报/审核/转维修）
 */
@Service
@SkyeyeService(name = "设备巡检单", groupName = "设备巡检", allowDynamicAttrKey = false)
public class EquipmentInspectionOrderServiceImpl
    extends SkyeyeBusinessServiceImpl<EquipmentInspectionOrderDao, EquipmentInspectionOrder>
    implements EquipmentInspectionOrderService {

    @Autowired
    private EquipmentService equipmentService;

    @Lazy
    @Autowired
    private EquipmentInspectionPlanService equipmentInspectionPlanService;

    @Lazy
    @Autowired
    private EquipmentRepairOrderService equipmentRepairOrderService;

    @Autowired
    private EquipmentInspectionOrderQuerySupport orderQuerySupport;

    @Override
    protected QueryWrapper<EquipmentInspectionOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentInspectionOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String state = commonPageInfo.getState();
        if (StrUtil.isNotEmpty(state) && StrUtil.isNumeric(state)) {
            Integer stateVal = Integer.valueOf(state);
            if (ObjectUtil.equal(stateVal, EquipmentInspectionOrderState.PENDING_ORDERS.getKey())
                || ObjectUtil.equal(stateVal, EquipmentInspectionOrderState.BE_EXECUTED.getKey())) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getServiceUserId), userId)
                    .eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getState), stateVal);
            } else {
                queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getState), stateVal);
            }
        }
        String planId = commonPageInfo.getObjectId();
        if (StrUtil.isEmpty(planId)) {
            planId = commonPageInfo.getCustomParamsMapStr("planId");
        }
        if (StrUtil.isNotEmpty(planId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getPlanId), planId);
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), commonPageInfo.getHolderId());
        }
        String checkResult = commonPageInfo.getCustomParamsMapStr("checkResult");
        if (StrUtil.isNotEmpty(checkResult) && StrUtil.isNumeric(checkResult)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getCheckResult), Integer.valueOf(checkResult));
        }
        orderQuerySupport.applyStatTimeRange(queryWrapper, commonPageInfo.getStartTime(), commonPageInfo.getEndTime());
        orderQuerySupport.applyEquipmentNameCodeFilter(queryWrapper, commonPageInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getCreateTime));
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        equipmentInspectionPlanService.setMationForMap(beans, "planId", "planMation");
        equipmentService.setMationForMap(beans, "equipmentId", "equipmentMation");
        iAuthUserService.setMationForMap(beans, "serviceUserId", "serviceUserMation");
        return beans;
    }

    @Override
    public void createPrepose(EquipmentInspectionOrder entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        entity.setOddNumber(iCodeRuleService.getNextCodeByClassName(getClass().getName(), business));
        fillCreateDefaults(entity);
    }

    @Override
    protected void createPrepose(List<EquipmentInspectionOrder> list) {
        List<String> oddNumbers = iCodeRuleService.getNextCodeByClassName(
            getClass().getName(), BeanUtil.beanToMap(list.get(0)), list.size());
        for (int i = 0; i < list.size(); i++) {
            EquipmentInspectionOrder order = list.get(i);
            order.setOddNumber(oddNumbers.get(i));
            fillCreateDefaults(order);
        }
    }

    private void fillCreateDefaults(EquipmentInspectionOrder entity) {
        applyServiceUserState(entity, true);
        if (StrUtil.isNotEmpty(entity.getPlannedStartTime()) && StrUtil.isEmpty(entity.getPlanDate())
            && entity.getPlannedStartTime().length() >= 10) {
            entity.setPlanDate(entity.getPlannedStartTime().substring(0, 10));
        }
    }

    @Override
    protected void updatePrepose(EquipmentInspectionOrder entity) {
        applyServiceUserState(entity, false);
    }

    private void applyServiceUserState(EquipmentInspectionOrder entity, boolean create) {
        if (StrUtil.isEmpty(entity.getServiceUserId())) {
            entity.setState(EquipmentInspectionOrderState.BE_DISPATCHED.getKey());
            entity.setServiceTime(null);
            return;
        }
        entity.setState(EquipmentInspectionOrderState.PENDING_ORDERS.getKey());
        if (create || StrUtil.isEmpty(entity.getServiceTime())) {
            entity.setServiceTime(DateUtil.getTimeAndToString());
        }
        if (StrUtil.isEmpty(entity.getAssignType())) {
            entity.setAssignType(EquipmentInspectionAssignType.MANUAL.getKey());
        }
    }

    @Override
    public void validatorEntity(EquipmentInspectionOrder entity) {
        if (StrUtil.isNotEmpty(entity.getId())) {
            EquipmentInspectionOrder dbOrder = selectById(entity.getId());
            if (ObjectUtil.equal(dbOrder.getState(), EquipmentInspectionOrderState.BE_DISPATCHED.getKey())
                || ObjectUtil.equal(dbOrder.getState(), EquipmentInspectionOrderState.PENDING_ORDERS.getKey())) {
                // 待派工、待接单可以进行编辑
            } else {
                throw new CustomException("该数据状态已改变，请刷新页面！");
            }
        }
    }

    @Override
    public EquipmentInspectionOrder selectById(String id) {
        EquipmentInspectionOrder order = super.selectById(id);
        equipmentInspectionPlanService.setDataMation(order, EquipmentInspectionOrder::getPlanId);
        equipmentService.setDataMation(order, EquipmentInspectionOrder::getEquipmentId);
        iAuthUserService.setDataMation(order, EquipmentInspectionOrder::getServiceUserId);
        if (CollectionUtil.isNotEmpty(order.getCooperationUserId())) {
            order.setCooperationUserMation(iAuthUserService.queryDataMationByIds(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(order.getCooperationUserId())));
        }
        order.setStateMation(EquipmentInspectionOrderState.getMation(order.getState()));
        order.setCheckResultMation(EquipmentInspectionCheckResult.getMation(order.getCheckResult()));
        order.setAssignTypeMation(EquipmentInspectionAssignType.getMation(order.getAssignType()));
        return order;
    }

    @Override
    public void deletePreExecution(EquipmentInspectionOrder entity) {
        if (ObjectUtil.equal(entity.getState(), EquipmentInspectionOrderState.BE_DISPATCHED.getKey())
            || ObjectUtil.equal(entity.getState(), EquipmentInspectionOrderState.PENDING_ORDERS.getKey())) {
            // 待派工、待接单可以进行删除
        } else {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editEquipmentInspectionWaitToWorkMation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String serviceUserId = map.get("serviceUserId").toString();
        EquipmentInspectionOrder order = selectById(id);
        if (!ObjectUtil.equal(order.getState(), EquipmentInspectionOrderState.BE_DISPATCHED.getKey())) {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
        UpdateWrapper<EquipmentInspectionOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, order.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getState),
            EquipmentInspectionOrderState.PENDING_ORDERS.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getServiceUserId), serviceUserId);
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getCooperationUserId),
            map.get("cooperationUserId").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getServiceTime), DateUtil.getTimeAndToString());
        String assignType = map.get("assignType").toString();
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getAssignType),
            StrUtil.isEmpty(assignType) ? EquipmentInspectionAssignType.MANUAL.getKey() : assignType);
        update(updateWrapper);
        refreshCache(id);
        outputObject.setBean(selectById(id));
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void receivingEquipmentInspectionOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        EquipmentInspectionOrder order = selectById(map.get("id").toString());
        if (!ObjectUtil.equal(order.getState(), EquipmentInspectionOrderState.PENDING_ORDERS.getKey())) {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
        updateStateById(order.getId(), EquipmentInspectionOrderState.BE_EXECUTED.getKey());
        outputObject.setBean(selectById(order.getId()));
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void registerEquipmentInspectionOnce(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        EquipmentInspectionOrder order = selectById(id);
        if (!ObjectUtil.equal(order.getState(), EquipmentInspectionOrderState.BE_EXECUTED.getKey())) {
            throw new CustomException("仅待填报状态可登记巡检次数");
        }
        int required = resolveRequiredInspectCount(order);
        int current = order.getInspectedCount();
        int next = current + 1;
        if (next > required) {
            throw new CustomException("已达规定巡检次数，请提交巡检结果");
        }
        UpdateWrapper<EquipmentInspectionOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectedCount), next);
        update(updateWrapper);
        refreshCache(id);
        outputObject.setBean(selectById(id));
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void submitEquipmentInspectionResult(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        EquipmentInspectionOrder dbOrder = selectById(id);
        if (!ObjectUtil.equal(dbOrder.getState(), EquipmentInspectionOrderState.BE_EXECUTED.getKey())) {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
        int required = resolveRequiredInspectCount(dbOrder);
        int current = dbOrder.getInspectedCount();
        if (current < required) {
            throw new CustomException("未达规定巡检次数（已巡 " + current + " / 规定 " + required + "），无法提交结果");
        }
        Integer checkResult = Integer.valueOf(map.get("checkResult").toString());
        String inspectionTime = map.get("inspectionTime").toString();
        if (StrUtil.isEmpty(inspectionTime)) {
            inspectionTime = DateUtil.getTimeAndToString();
        }
        UpdateWrapper<EquipmentInspectionOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getCheckResult), checkResult);
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), inspectionTime);
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getSummary),
            map.get("summary").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getPhotoUrls),
            map.get("photoUrls").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getLocationText),
            map.get("locationText").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getLongitude),
            map.get("longitude").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getLatitude),
            map.get("latitude").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getAddress),
            map.get("address").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getState),
            EquipmentInspectionOrderState.BE_AUDITED.getKey());
        update(updateWrapper);
        refreshCache(id);
        outputObject.setBean(selectById(id));
    }

    private int resolveRequiredInspectCount(EquipmentInspectionOrder order) {
        EquipmentInspectionPlan plan = equipmentInspectionPlanService.selectById(order.getPlanId());
        if (StrUtil.isEmpty(plan.getId()) || plan.getInspectionsPerDay() == null || plan.getInspectionsPerDay() < 1) {
            return 1;
        }
        return plan.getInspectionsPerDay();
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void auditEquipmentInspectionOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        boolean pass = !"0".equals(map.get("pass").toString());
        EquipmentInspectionOrder dbOrder = selectById(id);
        if (!ObjectUtil.equal(dbOrder.getState(), EquipmentInspectionOrderState.BE_AUDITED.getKey())) {
            throw new CustomException("该数据状态已改变，请刷新页面！");
        }
        if (pass) {
            if (dbOrder.getCheckResult() == null) {
                throw new CustomException("检查结果为空，无法审核");
            }
            if (ObjectUtil.equal(dbOrder.getCheckResult(), EquipmentInspectionCheckResult.ABNORMAL.getKey())) {
                equipmentService.editEquipmentStateById(dbOrder.getEquipmentId(), EquipmentState.DEGRADED.getKey());
            } else if (ObjectUtil.equal(dbOrder.getCheckResult(), EquipmentInspectionCheckResult.NORMAL.getKey())) {
                equipmentService.editEquipmentStateById(dbOrder.getEquipmentId(), EquipmentState.NORMAL.getKey());
            }
            updateStateById(id, EquipmentInspectionOrderState.COMPLETED.getKey());
        } else {
            updateStateById(id, EquipmentInspectionOrderState.BE_EXECUTED.getKey());
        }
        outputObject.setBean(selectById(id));
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void transferEquipmentInspectionToRepair(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        EquipmentInspectionOrder order = selectById(id);
        if (!ObjectUtil.equal(order.getState(), EquipmentInspectionOrderState.COMPLETED.getKey())) {
            throw new CustomException("仅已完成的巡检单可转维修");
        }
        if (!ObjectUtil.equal(order.getCheckResult(), EquipmentInspectionCheckResult.ABNORMAL.getKey())) {
            throw new CustomException("仅检查结果为异常的巡检单可转维修");
        }
        if (StrUtil.isNotEmpty(order.getRepairOrderId())) {
            throw new CustomException("该巡检单已转维修，请勿重复操作");
        }
        EquipmentRepairOrder repairOrder = new EquipmentRepairOrder();
        repairOrder.setEquipmentId(order.getEquipmentId());
        repairOrder.setUserId(StrUtil.blankToDefault(order.getServiceUserId(), order.getCreateId()));
        repairOrder.setReportTime(StrUtil.blankToDefault(order.getInspectionTime(), DateUtil.getTimeAndToString()));
        repairOrder.setFaultBrief(buildFaultDesc(order));
        repairOrder.setFaultPhoto(order.getPhotoUrls());
        String operatorId = InputObject.getLogParamsStatic().get("id").toString();
        equipmentRepairOrderService.createEntity(repairOrder, operatorId);

        UpdateWrapper<EquipmentInspectionOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getRepairOrderId), repairOrder.getId());
        update(updateWrapper);
        refreshCache(id);
        outputObject.setBean(selectById(id));
    }

    private String buildFaultDesc(EquipmentInspectionOrder order) {
        StringBuilder sb = new StringBuilder();
        sb.append("巡检单[").append(order.getOddNumber()).append("]检查异常");
        if (StrUtil.isNotEmpty(order.getSummary())) {
            sb.append("：").append(order.getSummary());
        }
        return sb.toString();
    }

    @Override
    public void updateStateById(String id, Integer state) {
        UpdateWrapper<EquipmentInspectionOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getState), state);
        update(updateWrapper);
        refreshCache(id);
    }

}
