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
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.farm.service.FarmService;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionOrderDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionItem;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrderItem;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.service.EquipmentInspectionItemService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderItemService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionAbnormalFlag;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionResultType;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionRunStatus;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private FarmService farmService;

    @Autowired
    private EquipmentInspectionPlanService equipmentInspectionPlanService;

    @Autowired
    private EquipmentInspectionItemService equipmentInspectionItemService;

    @Override
    protected QueryWrapper<EquipmentInspectionOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentInspectionOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), commonPageInfo.getObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getStartTime())) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), commonPageInfo.getStartTime());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getEndTime())) {
            queryWrapper.le(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), commonPageInfo.getEndTime());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getCreateTime));
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        equipmentService.setMationForMap(beans, "equipmentId", "equipmentMation");
        appendFarmMationForEquipmentMap(beans);
        equipmentInspectionPlanService.setMationForMap(beans, "planId", "planMation");
        iAuthUserService.setMationForMap(beans, "inspectorUserId", "inspectorUserMation");
        beans.forEach(this::appendEnumMationForMap);
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.isNotBlank(pageInfo.getStartTime()) || StrUtil.isNotBlank(pageInfo.getEndTime())) {
            appendStatRecordDisplayFields(beans);
        }
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
        Equipment equipment = equipmentService.selectById(order.getEquipmentId());
        order.setEquipmentMation(BeanUtil.beanToMap(equipment, false, true));
        appendPlanItemMation(order);
        appendOrderItemResultMation(order);
        appendEnumMation(order);
        equipmentInspectionPlanService.setDataMation(order, EquipmentInspectionOrder::getPlanId);
        iAuthUserService.setDataMation(order, EquipmentInspectionOrder::getInspectorUserId);
        return order;
    }

    @Override
    public void createPrepose(EquipmentInspectionOrder entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business);
        if (StrUtil.isBlank(oddNumber)) {
            oddNumber = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        }
        entity.setOddNumber(oddNumber);
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
        super.validatorEntity(entity);
        alignOrderItemsWithPlan(entity.getEquipmentInspectionOrderItemList(), entity.getPlanId());
        if (entity.getOverallResult() == null) {
            boolean abnormal = entity.getEquipmentInspectionOrderItemList().stream()
                .anyMatch(row -> EquipmentInspectionResultType.ABNORMAL.getKey().equals(row.getItemResult())
                    || EquipmentInspectionAbnormalFlag.YES.getKey().equals(row.getAbnormalFlag()));
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

    private void appendStatRecordDisplayFields(List<Map<String, Object>> beans) {
        for (Map<String, Object> bean : beans) {
            String photoUrls = MapUtil.getStr(bean, "headerPhotoUrls");
            bean.put("photo", photoUrls);
            bean.put("photoUrls", photoUrls);
            String location = StrUtil.blankToDefault(MapUtil.getStr(bean, "locationText"),
                MapUtil.getStr(bean, "headerLocationText"));
            bean.put("location", location);
            if (StrUtil.isBlank(MapUtil.getStr(bean, "farmName"))) {
                Map<String, Object> farmMation = BeanUtil.toBean(bean.get("farmMation"), Map.class);
                if (farmMation != null) {
                    bean.put("farmName", farmMation.get("name"));
                }
            }
        }
    }

    private void appendFarmMationForEquipmentMap(List<Map<String, Object>> beans) {
        beans.forEach(bean -> {
            Map<String, Object> equipmentMation = BeanUtil.toBean(bean.get("equipmentMation"), Map.class);
            if (equipmentMation != null && equipmentMation.get("farmId") != null) {
                bean.put("farmId", equipmentMation.get("farmId"));
            }
        });
        farmService.setMationForMap(beans, "farmId", "farmMation");
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
            Integer lineNo = orderItem.getLineNo();
            if (lineNo != null && lineNo > 0 && lineNo <= plan.getItemId().size()) {
                orderItem.setItemMation(itemMap.get(plan.getItemId().get(lineNo - 1)));
            }
        }
    }

    private void appendOrderItemResultMation(EquipmentInspectionOrder order) {
        if (CollectionUtil.isEmpty(order.getEquipmentInspectionOrderItemList())) {
            return;
        }
        for (EquipmentInspectionOrderItem orderItem : order.getEquipmentInspectionOrderItemList()) {
            orderItem.setItemResultMation(EquipmentInspectionResultType.getMation(orderItem.getItemResult()));
            orderItem.setAbnormalFlagMation(EquipmentInspectionAbnormalFlag.getMation(orderItem.getAbnormalFlag()));
        }
    }

    private void appendEnumMation(EquipmentInspectionOrder order) {
        order.setOverallResultMation(EquipmentInspectionResultType.getMation(order.getOverallResult()));
        order.setEquipmentRunStatusMation(EquipmentInspectionRunStatus.getMation(order.getEquipmentRunStatus()));
    }

    private void appendEnumMationForMap(Map<String, Object> bean) {
        bean.put("overallResultMation", EquipmentInspectionResultType.getMation(MapUtil.getInt(bean, "overallResult")));
        bean.put("equipmentRunStatusMation", EquipmentInspectionRunStatus.getMation(MapUtil.getInt(bean, "equipmentRunStatus")));
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
            orderItems.get(i).setLineNo(i + 1);
        }
    }

}
