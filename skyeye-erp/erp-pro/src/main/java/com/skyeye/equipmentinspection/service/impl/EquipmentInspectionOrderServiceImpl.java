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
import com.skyeye.equipmentarchive.entity.EquipmentArchive;
import com.skyeye.equipmentarchive.service.EquipmentArchiveService;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionOrderDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrderItem;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlanItem;
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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
    private EquipmentArchiveService equipmentArchiveService;

    @Autowired
    private EquipmentInspectionPlanService equipmentInspectionPlanService;

    @Autowired
    private EquipmentInspectionStatQuerySupport equipmentInspectionStatQuerySupport;

    @Override
    protected QueryWrapper<EquipmentInspectionOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentInspectionOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        equipmentInspectionStatQuerySupport.applyOrderQueryWrapper(queryWrapper, commonPageInfo);
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        equipmentArchiveService.setMationForMap(beans, "equipmentId", "equipmentMation");
        equipmentArchiveService.appendPatrolRecordForMationMap(beans, "equipmentId", "equipmentMation");
        iAuthUserService.setMationForMap(beans, "inspectorUserId", "inspectorUserMation");
        if (equipmentInspectionStatQuerySupport.isStatPageQuery(inputObject)) {
            equipmentInspectionStatQuerySupport.appendStatRecordDisplayFields(beans);
        }
        return beans;
    }

    @Override
    public EquipmentInspectionOrder getDataFromDb(String id) {
        EquipmentInspectionOrder entity = super.getDataFromDb(id);
        if (entity == null) {
            return null;
        }
        entity.setEquipmentInspectionOrderItemList(equipmentInspectionOrderItemService.selectByPId(entity.getId()));
        return entity;
    }

    @Override
    public EquipmentInspectionOrder selectById(String id) {
        EquipmentInspectionOrder order = super.selectById(id);
        if (order == null) {
            return null;
        }
        EquipmentArchive archive = equipmentArchiveService.selectById(order.getEquipmentId());
        if (archive != null) {
            order.setEquipmentMation(BeanUtil.beanToMap(archive, false, true));
            appendPlanItemMation(order, archive);
        }
        iAuthUserService.setDataMation(order, EquipmentInspectionOrder::getInspectorUserId);
        return order;
    }

    @Override
    public void createPrepose(EquipmentInspectionOrder entity) {
        if (StrUtil.isBlank(entity.getId()) && InputObject.getLogParamsStatic() != null
            && InputObject.getLogParamsStatic().get("id") != null) {
            entity.setInspectorUserId(InputObject.getLogParamsStatic().get("id").toString());
        }
        if (StrUtil.isBlank(entity.getOddNumber())) {
            assignOddNumber(entity);
        }
        super.createPrepose(entity);
    }

    @Override
    public void updatePrepose(EquipmentInspectionOrder entity) {
        super.updatePrepose(entity);
        if (StrUtil.isBlank(entity.getId())) {
            return;
        }
        EquipmentInspectionOrder oldOrder = getDataFromDb(entity.getId());
        if (oldOrder == null) {
            throw new CustomException("巡检单不存在.");
        }
        entity.setOddNumber(oldOrder.getOddNumber());
        entity.setEquipmentId(oldOrder.getEquipmentId());
        entity.setSeqInDay(oldOrder.getSeqInDay());
        if (StrUtil.isNotBlank(oldOrder.getInspectorUserId())) {
            entity.setInspectorUserId(oldOrder.getInspectorUserId());
        }
    }

    @Override
    public void validatorEntity(EquipmentInspectionOrder entity) {
        normalizeCreateIdentity(entity);
        if (StrUtil.isBlank(entity.getId())) {
            entity.setOddNumber(null);
            assignOddNumber(entity);
        }
        if (StrUtil.isBlank(entity.getId()) && entity.getSeqInDay() == null) {
            String today = LocalDate.now().toString();
            QueryWrapper<EquipmentInspectionOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), entity.getEquipmentId());
            queryWrapper.apply("DATE_FORMAT(inspection_time, '%Y-%m-%d') = {0}", today);
            entity.setSeqInDay((int) count(queryWrapper) + 1);
        }
        super.validatorEntity(entity);
        if (CollectionUtil.isEmpty(entity.getEquipmentInspectionOrderItemList())) {
            throw new CustomException("请至少填写一条巡检明细.");
        }
        EquipmentArchive archive = equipmentArchiveService.selectById(entity.getEquipmentId());
        if (archive == null) {
            throw new CustomException("设备档案不存在.");
        }
        Map<String, Object> patrolRecord = archive.getPatrolRecord();
        if (StrUtil.isBlank(entity.getId())) {
            if (MapUtil.isEmpty(patrolRecord) || StrUtil.isBlank(MapUtil.getStr(patrolRecord, "planId"))) {
                throw new CustomException("所选设备未配置巡检方案，请先在设备档案中关联巡检方案.");
            }
        }
        validateOrderItemsAgainstPlan(entity.getEquipmentInspectionOrderItemList(), patrolRecord);
        if (entity.getOverallResult() == null) {
            boolean abnormal = entity.getEquipmentInspectionOrderItemList().stream()
                .anyMatch(row -> EquipmentInspectionResultType.ABNORMAL.getKey().equals(row.getItemResult())
                    || Integer.valueOf(1).equals(row.getAbnormalFlag()));
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

    @Override
    public void deletePostpose(String id) {
        equipmentInspectionOrderItemService.deleteByPId(id);
    }

    private void appendPlanItemMation(EquipmentInspectionOrder order, EquipmentArchive archive) {
        if (CollectionUtil.isEmpty(order.getEquipmentInspectionOrderItemList())) {
            return;
        }
        EquipmentInspectionPlan plan = loadPlanFromPatrolRecord(archive.getPatrolRecord());
        if (plan == null || CollectionUtil.isEmpty(plan.getEquipmentInspectionPlanItemList())) {
            return;
        }
        Map<Integer, EquipmentInspectionPlanItem> planItemByLine = plan.getEquipmentInspectionPlanItemList().stream()
            .filter(item -> item.getLineNo() != null)
            .collect(Collectors.toMap(EquipmentInspectionPlanItem::getLineNo, Function.identity(), (a, b) -> a));
        for (EquipmentInspectionOrderItem orderItem : order.getEquipmentInspectionOrderItemList()) {
            EquipmentInspectionPlanItem planItem = planItemByLine.get(orderItem.getLineNo());
            if (planItem != null) {
                orderItem.setPlanItemMation(planItem);
            }
        }
    }

    private void validateOrderItemsAgainstPlan(List<EquipmentInspectionOrderItem> orderItems, Map<String, Object> patrolRecord) {
        EquipmentInspectionPlan plan = loadPlanFromPatrolRecord(patrolRecord);
        if (plan == null || CollectionUtil.isEmpty(plan.getEquipmentInspectionPlanItemList())) {
            throw new CustomException("设备关联的巡检方案不存在或未配置检查项.");
        }
        Map<Integer, EquipmentInspectionPlanItem> planItemByLine = plan.getEquipmentInspectionPlanItemList().stream()
            .filter(item -> item.getLineNo() != null)
            .collect(Collectors.toMap(EquipmentInspectionPlanItem::getLineNo, Function.identity(), (a, b) -> a));
        for (EquipmentInspectionOrderItem orderItem : orderItems) {
            if (orderItem.getLineNo() == null || !planItemByLine.containsKey(orderItem.getLineNo())) {
                throw new CustomException("巡检明细行号与方案不匹配.");
            }
        }
    }

    private EquipmentInspectionPlan loadPlanFromPatrolRecord(Map<String, Object> patrolRecord) {
        if (MapUtil.isEmpty(patrolRecord)) {
            return null;
        }
        String planId = MapUtil.getStr(patrolRecord, "planId");
        if (StrUtil.isBlank(planId)) {
            return null;
        }
        return equipmentInspectionPlanService.getDataFromDb(planId);
    }

    private void normalizeCreateIdentity(EquipmentInspectionOrder entity) {
        if (StrUtil.isBlank(entity.getId())) {
            return;
        }
        EquipmentInspectionOrder existing = super.getDataFromDb(entity.getId());
        if (existing == null) {
            entity.setId(null);
        }
    }

    private void assignOddNumber(EquipmentInspectionOrder entity) {
        entity.setOddNumber(iCodeRuleService.getNextCodeByClassName(getServiceClassName(), BeanUtil.beanToMap(entity)));
    }

}
