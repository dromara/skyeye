/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
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
import com.skyeye.equipmentinspection.entity.EquipmentInspectionItem;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrderItem;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.service.EquipmentInspectionItemService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderItemService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionResultType;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentInspectionOrderServiceImpl
 * @Description: 设备巡检单服务实现类
 */
@Service
@SkyeyeService(name = "设备巡检单", groupName = "设备巡检", flowable = true, allowDynamicAttrKey = false)
public class EquipmentInspectionOrderServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentInspectionOrderDao, EquipmentInspectionOrder>
    implements EquipmentInspectionOrderService {

    @Autowired
    private EquipmentInspectionOrderItemService equipmentInspectionOrderItemService;

    @Lazy
    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EquipmentInspectionPlanService equipmentInspectionPlanService;

    @Autowired
    private EquipmentInspectionItemService equipmentInspectionItemService;

    @Override
    protected QueryWrapper<EquipmentInspectionOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentInspectionOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String equipmentId = commonPageInfo.getCustomParamsMapStr("equipmentId");
        if (StrUtil.isNotEmpty(equipmentId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), equipmentId);
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
        equipmentService.setMationForMap(beans, "equipmentId", "equipmentMation");
        equipmentInspectionPlanService.setMationForMap(beans, "planId", "planMation");
        iAuthUserService.setMationForMap(beans, "inspectorUserId", "inspectorUserMation");
        beans.forEach(this::appendEnumMationForMap);
        return beans;
    }

    @Override
    public EquipmentInspectionOrder getDataFromDb(String id) {
        EquipmentInspectionOrder entity = super.getDataFromDb(id);
        entity.setEquipmentInspectionOrderItemList(equipmentInspectionOrderItemService.selectByPId(entity.getId()));
        return entity;
    }

    @Override
    public EquipmentInspectionOrder selectById(String id) {
        EquipmentInspectionOrder order = super.selectById(id);
        equipmentService.setDataMation(order, EquipmentInspectionOrder::getEquipmentId);
        appendPlanItemMation(order);
        appendOrderItemResultMation(order);
        appendEnumMation(order);
        equipmentInspectionPlanService.setDataMation(order, EquipmentInspectionOrder::getPlanId);
        iAuthUserService.setDataMation(order, EquipmentInspectionOrder::getInspectorUserId);
        return order;
    }

    @Override
    public void createPrepose(EquipmentInspectionOrder entity) {
        normalizeEquipmentRunStatus(entity);
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        entity.setOddNumber(iCodeRuleService.getNextCodeByClassName(getClass().getName(), business));
        if (entity.getSeqInDay() == null) {
            String today = LocalDate.now().toString();
            QueryWrapper<EquipmentInspectionOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), entity.getEquipmentId());
            queryWrapper.apply("DATE_FORMAT(inspection_time, '%Y-%m-%d') = {0}", today);
            entity.setSeqInDay((int) count(queryWrapper) + 1);
        }
    }

    @Override
    public void validatorEntity(EquipmentInspectionOrder entity) {
        normalizeEquipmentRunStatus(entity);
        super.validatorEntity(entity);
        alignOrderItemsWithPlan(entity.getEquipmentInspectionOrderItemList(), entity.getPlanId());
        if (entity.getOverallResult() == null) {
            boolean abnormal = entity.getEquipmentInspectionOrderItemList().stream()
                .anyMatch(row -> EquipmentInspectionResultType.ABNORMAL.getKey().equals(row.getItemResult()));
            entity.setOverallResult(abnormal
                ? EquipmentInspectionResultType.ABNORMAL.getKey()
                : EquipmentInspectionResultType.NORMAL.getKey());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writePostpose(EquipmentInspectionOrder entity, String userId) {
        equipmentInspectionOrderItemService.saveLinkList(entity.getId(), entity.getEquipmentInspectionOrderItemList());
        super.writePostpose(entity, userId);
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

    @Override
    public void deletePostpose(String id) {
        equipmentInspectionOrderItemService.deleteByPId(id);
    }

    private void normalizeEquipmentRunStatus(EquipmentInspectionOrder entity) {
        if (entity.getEquipmentRunStatus() == null && entity.getEquipmentState() != null) {
            entity.setEquipmentRunStatus(entity.getEquipmentState());
        }
        if (entity.getEquipmentRunStatus() == null) {
            entity.setEquipmentRunStatus(EquipmentState.NORMAL.getKey());
        }
    }

    private void appendPlanItemMation(EquipmentInspectionOrder order) {
        if (CollectionUtil.isEmpty(order.getEquipmentInspectionOrderItemList()) || StrUtil.isBlank(order.getPlanId())) {
            return;
        }
        EquipmentInspectionPlan plan = equipmentInspectionPlanService.getDataFromDb(order.getPlanId());
        if (CollectionUtil.isEmpty(plan.getItemId())) {
            return;
        }
        List<EquipmentInspectionItem> planItems = equipmentInspectionItemService.selectByIds(plan.getItemId().toArray(new String[]{}));
        if (CollectionUtil.isEmpty(planItems)) {
            return;
        }
        Map<String, EquipmentInspectionItem> itemMap = planItems.stream()
            .collect(Collectors.toMap(EquipmentInspectionItem::getId, item -> item, (a, b) -> a));
        for (EquipmentInspectionOrderItem orderItem : order.getEquipmentInspectionOrderItemList()) {
            Integer orderBy = orderItem.getOrderBy();
            if (orderBy != null && orderBy > 0 && orderBy <= plan.getItemId().size()) {
                orderItem.setItemMation(itemMap.get(plan.getItemId().get(orderBy - 1)));
            }
        }
    }

    private void appendOrderItemResultMation(EquipmentInspectionOrder order) {
        if (CollectionUtil.isEmpty(order.getEquipmentInspectionOrderItemList())) {
            return;
        }
        for (EquipmentInspectionOrderItem orderItem : order.getEquipmentInspectionOrderItemList()) {
            orderItem.setItemResultMation(EquipmentInspectionResultType.getMation(orderItem.getItemResult()));
        }
    }

    private void appendEnumMation(EquipmentInspectionOrder order) {
        order.setOverallResultMation(EquipmentInspectionResultType.getMation(order.getOverallResult()));
        order.setEquipmentRunStatusMation(getEquipmentRunStatusMation(order.getEquipmentRunStatus()));
    }

    private void appendEnumMationForMap(Map<String, Object> bean) {
        bean.put("overallResultMation", EquipmentInspectionResultType.getMation(MapUtil.getInt(bean, "overallResult")));
        Integer runStatus = MapUtil.getInt(bean, "equipmentRunStatus");
        if (runStatus == null) {
            runStatus = MapUtil.getInt(bean, "equipmentState");
        }
        bean.put("equipmentRunStatusMation", getEquipmentRunStatusMation(runStatus));
    }

    private Map<String, Object> getEquipmentRunStatusMation(Integer type) {
        if (type == null) {
            return MapUtil.newHashMap();
        }
        for (EquipmentState state : EquipmentState.values()) {
            if (type.equals(state.getKey())) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", state.getKey());
                result.put("name", state.getValue());
                return result;
            }
        }
        return MapUtil.newHashMap();
    }

    private void alignOrderItemsWithPlan(List<EquipmentInspectionOrderItem> orderItems, String planId) {
        EquipmentInspectionPlan plan = equipmentInspectionPlanService.getDataFromDb(planId);
        if (CollectionUtil.isEmpty(plan.getItemId())) {
            throw new CustomException("巡检方案未配置巡检项目.");
        }
        if (orderItems.size() != plan.getItemId().size()) {
            throw new CustomException("巡检明细条数与方案巡检项目不一致.");
        }
        for (int i = 0; i < orderItems.size(); i++) {
            orderItems.get(i).setOrderBy(i + 1);
        }
    }

}
