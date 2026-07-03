/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlanEquipment;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanEquipmentService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionStatService;
import com.skyeye.farm.service.FarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentInspectionStatServiceImpl
 * @Description: 设备巡检统计服务实现类（对齐 patrol：单表 QueryWrapper + Service 关联，不用 MPJ JOIN）
 */
@Service
public class EquipmentInspectionStatServiceImpl implements EquipmentInspectionStatService {

    @Autowired
    private EquipmentInspectionOrderService equipmentInspectionOrderService;

    @Autowired
    private EquipmentInspectionPlanService equipmentInspectionPlanService;

    @Autowired
    private EquipmentInspectionPlanEquipmentService equipmentInspectionPlanEquipmentService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private FarmService farmService;

    @Override
    public void queryEquipmentInspectionSummaryList(InputObject inputObject, OutputObject outputObject) {
        querySummaryPage(inputObject, outputObject, false);
    }

    @Override
    public void queryEquipmentInspectionMissedList(InputObject inputObject, OutputObject outputObject) {
        querySummaryPage(inputObject, outputObject, true);
    }

    @Override
    public void queryEquipmentInspectionDistributionPanel(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        StatScope scope = StatScope.of(tableSelectInfo);
        fillDefaultMonthRange(scope);
        StatBundle bundle = loadStatBundle(scope);
        Map<String, Object> panel = new HashMap<>(2);
        panel.put("uninspected", buildTypeDistribution(bundle, false));
        panel.put("inspected", buildTypeDistribution(bundle, true));
        outputObject.setBean(panel);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private void querySummaryPage(InputObject inputObject, OutputObject outputObject, boolean onlyMissed) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        StatScope scope = StatScope.of(pageInfo);
        fillDefaultMonthRange(scope);
        StatBundle bundle = loadStatBundle(scope);
        if (bundle.statMap.isEmpty()) {
            outputObject.setBeans(Collections.emptyList());
            outputObject.settotal(0);
            return;
        }
        List<String> targetIds = bundle.statMap.entrySet().stream()
            .filter(entry -> !onlyMissed || entry.getValue()[2] > 0)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(targetIds)) {
            outputObject.setBeans(Collections.emptyList());
            outputObject.settotal(0);
            return;
        }
        Page<Object> page = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<Map<String, Object>> rows = new ArrayList<>();
        for (String equipmentId : targetIds) {
            Equipment equipment = bundle.equipmentMap.get(equipmentId);
            if (equipment == null) {
                continue;
            }
            int[] stat = bundle.statMap.get(equipmentId);
            rows.add(buildSummaryRow(equipment, bundle.planIdMap.get(equipmentId), stat));
        }
        farmService.setMationForMap(rows, "farmId", "farmMation");
        outputObject.setBeans(rows);
        outputObject.settotal(page.getTotal());
    }

    private StatBundle loadStatBundle(StatScope scope) {
        List<PlanEquipmentRow> scopedRows = queryScopedPlanEquipmentRows(scope);
        if (CollectionUtil.isEmpty(scopedRows)) {
            return StatBundle.empty();
        }
        Map<String, Equipment> equipmentMap = scopedRows.stream()
            .collect(Collectors.toMap(PlanEquipmentRow::getEquipmentId, PlanEquipmentRow::getEquipment,
                (a, b) -> a, LinkedHashMap::new));
        Map<String, String> planIdMap = scopedRows.stream()
            .collect(Collectors.toMap(PlanEquipmentRow::getEquipmentId, PlanEquipmentRow::getPlanId,
                (a, b) -> a, LinkedHashMap::new));
        List<String> planIds = planIdMap.values().stream().distinct().collect(Collectors.toList());
        Map<String, EquipmentInspectionPlan> planMap = equipmentInspectionPlanService.getDataFromDb(planIds).stream()
            .filter(plan -> StrUtil.isNotBlank(plan.getId()))
            .filter(plan -> EnableEnum.ENABLE_USING.getKey().equals(plan.getEnabled()))
            .collect(Collectors.toMap(EquipmentInspectionPlan::getId, plan -> plan, (a, b) -> a));
        Map<String, Integer> inspectedMap = countInspected(scope, equipmentMap.keySet());
        Map<String, int[]> statMap = new LinkedHashMap<>();
        for (PlanEquipmentRow row : scopedRows) {
            EquipmentInspectionPlan plan = planMap.get(row.getPlanId());
            if (plan == null) {
                continue;
            }
            int required = equipmentInspectionPlanService.calcRequiredInspectionCount(plan, scope.getStartTime(), scope.getEndTime());
            int inspected = inspectedMap.getOrDefault(row.getEquipmentId(), 0);
            statMap.put(row.getEquipmentId(), new int[]{required, inspected, Math.max(0, required - inspected)});
        }
        StatBundle bundle = new StatBundle();
        bundle.equipmentMap = equipmentMap;
        bundle.planIdMap = planIdMap;
        bundle.statMap = statMap;
        return bundle;
    }

    /**
     * 对齐 patrol：先查关联表，再通过 Service 批量取方案/设备，内存过滤车间与关键字。
     */
    private List<PlanEquipmentRow> queryScopedPlanEquipmentRows(StatScope scope) {
        QueryWrapper<EquipmentInspectionPlanEquipment> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(scope.getEquipmentId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionPlanEquipment::getEquipmentId), scope.getEquipmentId());
        }
        List<EquipmentInspectionPlanEquipment> planEquipmentList = equipmentInspectionPlanEquipmentService.list(queryWrapper);
        if (CollectionUtil.isEmpty(planEquipmentList)) {
            return Collections.emptyList();
        }

        List<String> planIds = planEquipmentList.stream()
            .map(EquipmentInspectionPlanEquipment::getPlanId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Set<String> enabledPlanIds = equipmentInspectionPlanService.getDataFromDb(planIds).stream()
            .filter(plan -> StrUtil.isNotBlank(plan.getId()))
            .filter(plan -> EnableEnum.ENABLE_USING.getKey().equals(plan.getEnabled()))
            .map(EquipmentInspectionPlan::getId)
            .collect(Collectors.toSet());

        List<String> equipmentIds = planEquipmentList.stream()
            .map(EquipmentInspectionPlanEquipment::getEquipmentId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Map<String, Equipment> equipmentMap = equipmentService.selectByIds(equipmentIds.toArray(new String[]{})).stream()
            .filter(item -> StrUtil.isNotBlank(item.getId()))
            .collect(Collectors.toMap(Equipment::getId, item -> item, (a, b) -> a));

        Map<String, PlanEquipmentRow> uniq = new LinkedHashMap<>();
        for (EquipmentInspectionPlanEquipment planEquipment : planEquipmentList) {
            if (!enabledPlanIds.contains(planEquipment.getPlanId())) {
                continue;
            }
            Equipment equipment = equipmentMap.get(planEquipment.getEquipmentId());
            if (equipment == null) {
                continue;
            }
            if (StrUtil.isNotBlank(scope.getHolderId()) && !scope.getHolderId().equals(equipment.getFarmId())) {
                continue;
            }
            if (StrUtil.isNotBlank(scope.getKeyword())) {
                String keyword = scope.getKeyword();
                if (!StrUtil.contains(equipment.getName(), keyword) && !StrUtil.contains(equipment.getOddNumber(), keyword)) {
                    continue;
                }
            }
            uniq.putIfAbsent(planEquipment.getEquipmentId(), PlanEquipmentRow.of(planEquipment.getPlanId(), equipment));
        }
        return new ArrayList<>(uniq.values());
    }

    private Map<String, Object> buildTypeDistribution(StatBundle bundle, boolean inspected) {
        Map<String, Long> grouped = new LinkedHashMap<>();
        for (Map.Entry<String, int[]> entry : bundle.statMap.entrySet()) {
            int[] stat = entry.getValue();
            if (inspected ? stat[1] < stat[0] : stat[1] >= stat[0]) {
                continue;
            }
            Equipment equipment = bundle.equipmentMap.get(entry.getKey());
            String typeName = resolveEquipmentTypeName(equipment);
            grouped.merge(typeName, 1L, Long::sum);
        }
        List<Map<String, Object>> rows = grouped.entrySet().stream()
            .sorted(Comparator.comparingLong(entry -> -entry.getValue()))
            .map(entry -> {
                Map<String, Object> row = new HashMap<>(2);
                row.put("equipmentTypeName", entry.getKey());
                row.put("equipmentCount", entry.getValue());
                return row;
            })
            .collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>(2);
        result.put("rows", rows);
        result.put("total", rows.stream().mapToInt(row -> MapUtil.getInt(row, "equipmentCount", 0)).sum());
        return result;
    }

    private void fillDefaultMonthRange(StatScope scope) {
        Date now = new Date();
        if (StrUtil.isBlank(scope.getStartTime())) {
            scope.setStartTime(DateUtil.format(DateUtil.beginOfMonth(now), "yyyy-MM-dd HH:mm:ss"));
        }
        if (StrUtil.isBlank(scope.getEndTime())) {
            scope.setEndTime(DateUtil.format(DateUtil.endOfMonth(now), "yyyy-MM-dd HH:mm:ss"));
        }
    }

    private Map<String, Integer> countInspected(StatScope scope, Set<String> equipmentIds) {
        if (CollectionUtil.isEmpty(equipmentIds)) {
            return Collections.emptyMap();
        }
        QueryWrapper<EquipmentInspectionOrder> wrapper = new QueryWrapper<>();
        wrapper.in(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), equipmentIds);
        wrapper.ge(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), scope.getStartTime());
        wrapper.le(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), scope.getEndTime());
        wrapper.select(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId));
        Map<String, Integer> countMap = new HashMap<>();
        equipmentInspectionOrderService.list(wrapper).forEach(order -> countMap.merge(order.getEquipmentId(), 1, Integer::sum));
        return countMap;
    }

    private Map<String, Object> buildSummaryRow(Equipment equipment, String planId, int[] stat) {
        Map<String, Object> row = new HashMap<>(12);
        row.put("equipmentId", equipment.getId());
        row.put("equipmentName", equipment.getName());
        row.put("equipmentCode", equipment.getOddNumber());
        row.put("equipmentTypeId", equipment.getEquipmentTypeId());
        row.put("equipmentTypeName", resolveEquipmentTypeName(equipment));
        row.put("farmId", equipment.getFarmId());
        row.put("model", equipment.getModel());
        row.put("planId", planId);
        row.put("requiredCount", stat[0]);
        row.put("inspectedCount", stat[1]);
        row.put("missedCount", stat[2]);
        return row;
    }

    private String resolveEquipmentTypeName(Equipment equipment) {
        if (equipment == null || StrUtil.isBlank(equipment.getId())) {
            return "其他设备";
        }
        return StrUtil.blankToDefault(equipment.getEquipmentTypeName(), "其他设备");
    }

    private static class StatScope {
        private String startTime;
        private String endTime;
        private String holderId;
        private String equipmentId;
        private String keyword;

        static StatScope of(CommonPageInfo pageInfo) {
            StatScope scope = new StatScope();
            scope.startTime = pageInfo.getStartTime();
            scope.endTime = pageInfo.getEndTime();
            scope.holderId = pageInfo.getHolderId();
            scope.equipmentId = pageInfo.getCustomParamsMapStr("equipmentId");
            scope.keyword = pageInfo.getKeyword();
            return scope;
        }

        static StatScope of(TableSelectInfo tableSelectInfo) {
            StatScope scope = new StatScope();
            scope.startTime = tableSelectInfo.getStartTime();
            scope.endTime = tableSelectInfo.getEndTime();
            scope.holderId = tableSelectInfo.getHolderId();
            scope.equipmentId = tableSelectInfo.getCustomParamsMapStr("equipmentId");
            scope.keyword = tableSelectInfo.getKeyword();
            return scope;
        }

        String getStartTime() {
            return startTime;
        }

        void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        String getEndTime() {
            return endTime;
        }

        void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        String getHolderId() {
            return holderId;
        }

        String getEquipmentId() {
            return equipmentId;
        }

        String getKeyword() {
            return keyword;
        }
    }

    private static class StatBundle {
        private Map<String, Equipment> equipmentMap = Collections.emptyMap();
        private Map<String, String> planIdMap = Collections.emptyMap();
        private Map<String, int[]> statMap = Collections.emptyMap();

        private static StatBundle empty() {
            return new StatBundle();
        }
    }

    private static class PlanEquipmentRow {
        private String planId;
        private Equipment equipment;

        static PlanEquipmentRow of(String planId, Equipment equipment) {
            PlanEquipmentRow row = new PlanEquipmentRow();
            row.planId = planId;
            row.equipment = equipment;
            return row;
        }

        String getPlanId() {
            return planId;
        }

        String getEquipmentId() {
            return equipment.getId();
        }

        Equipment getEquipment() {
            return equipment;
        }
    }
}
