/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentarchive.classenum.EquipmentArchiveBizType;
import com.skyeye.equipmentarchive.classenum.EquipmentArchiveType;
import com.skyeye.equipmentarchive.entity.EquipmentArchive;
import com.skyeye.equipmentarchive.entity.EquipmentArchiveBizRecord;
import com.skyeye.equipmentarchive.service.EquipmentArchiveBizRecordService;
import com.skyeye.equipmentarchive.service.EquipmentArchiveService;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionStatPageInfo;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionStatService;
import com.skyeye.farm.entity.Farm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentInspectionStatServiceImpl
 * @Description: 设备巡检统计服务实现类
 */
@Service
public class EquipmentInspectionStatServiceImpl implements EquipmentInspectionStatService {

    private static final String DISTRIBUTION_UNINSPECTED = "uninspected";
    private static final String DISTRIBUTION_INSPECTED = "inspected";

    @Autowired
    private EquipmentInspectionOrderService equipmentInspectionOrderService;

    @Autowired
    private EquipmentInspectionStatQuerySupport equipmentInspectionStatQuerySupport;

    @Autowired
    private EquipmentArchiveBizRecordService equipmentArchiveBizRecordService;

    @Autowired
    private EquipmentArchiveService equipmentArchiveService;

    @Autowired
    private EquipmentInspectionPlanService equipmentInspectionPlanService;

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
        EquipmentInspectionStatPageInfo pageInfo = equipmentInspectionStatQuerySupport.buildQueryInfo(inputObject);
        if (equipmentInspectionStatQuerySupport.prepareEquipmentScope(pageInfo)) {
            Map<String, Object> panel = new HashMap<>(4);
            panel.put("uninspected", emptyDistribution());
            panel.put("inspected", emptyDistribution());
            outputObject.setBean(panel);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
            return;
        }
        Map<String, Object> panel = new HashMap<>(4);
        panel.put("uninspected", buildDistribution(pageInfo, DISTRIBUTION_UNINSPECTED));
        panel.put("inspected", buildDistribution(pageInfo, DISTRIBUTION_INSPECTED));
        outputObject.setBean(panel);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private void querySummaryPage(InputObject inputObject, OutputObject outputObject, boolean onlyMissed) {
        EquipmentInspectionStatPageInfo pageInfo = equipmentInspectionStatQuerySupport.buildPageInfo(inputObject);
        if (equipmentInspectionStatQuerySupport.prepareEquipmentScope(pageInfo)) {
            outputObject.setBeans(Collections.emptyList());
            outputObject.settotal(0);
            return;
        }
        List<Map<String, Object>> allRows = queryEquipmentInspectionSummary(pageInfo, onlyMissed);
        List<Map<String, Object>> beans = paginateList(allRows, pageInfo.getPage(), pageInfo.getLimit());
        appendSummaryFarmMation(beans);
        outputObject.setBeans(beans);
        outputObject.settotal(allRows.size());
    }

    private List<Map<String, Object>> queryEquipmentInspectionSummary(EquipmentInspectionStatPageInfo pageInfo, boolean onlyMissed) {
        List<SummaryEquipmentContext> contexts = loadSummaryEquipmentContexts(pageInfo);
        if (CollectionUtil.isEmpty(contexts)) {
            return Collections.emptyList();
        }
        Map<String, Integer> inspectedCountMap = countInspectedByEquipment(pageInfo, contexts);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (SummaryEquipmentContext ctx : contexts) {
            int requiredCount = equipmentInspectionPlanService.calcRequiredInspectionCount(ctx.plan, pageInfo.getStartTime(), pageInfo.getEndTime());
            int inspectedCount = inspectedCountMap.getOrDefault(ctx.equipmentId, 0);
            int missedCount = Math.max(0, requiredCount - inspectedCount);
            if (onlyMissed && missedCount <= 0) {
                continue;
            }
            rows.add(buildSummaryRow(ctx, requiredCount, inspectedCount, missedCount));
        }
        rows.sort(Comparator.comparing(row -> MapUtil.getStr(row, "equipmentName"), Comparator.nullsLast(String::compareTo)));
        return rows;
    }

    private Map<String, Object> buildDistribution(EquipmentInspectionStatPageInfo pageInfo, String distributionType) {
        EquipmentInspectionStatPageInfo typePageInfo = copyPageInfoForDistribution(pageInfo, distributionType);
        List<Map<String, Object>> rows = queryEquipmentTypeDistribution(typePageInfo);
        Map<String, Object> result = new HashMap<>(2);
        result.put("rows", rows);
        result.put("total", rows.stream().mapToInt(row -> MapUtil.getInt(row, "equipmentCount", 0)).sum());
        return result;
    }

    private List<Map<String, Object>> queryEquipmentTypeDistribution(EquipmentInspectionStatPageInfo pageInfo) {
        List<SummaryEquipmentContext> contexts = loadSummaryEquipmentContexts(pageInfo);
        if (CollectionUtil.isEmpty(contexts)) {
            return Collections.emptyList();
        }
        Map<String, Integer> inspectedCountMap = countInspectedByEquipment(pageInfo, contexts);
        Map<String, Long> grouped = new LinkedHashMap<>();
        for (SummaryEquipmentContext ctx : contexts) {
            int requiredCount = equipmentInspectionPlanService.calcRequiredInspectionCount(ctx.plan, pageInfo.getStartTime(), pageInfo.getEndTime());
            int inspectedCount = inspectedCountMap.getOrDefault(ctx.equipmentId, 0);
            boolean match = StrUtil.equals(DISTRIBUTION_UNINSPECTED, pageInfo.getDistributionType())
                ? inspectedCount < requiredCount
                : inspectedCount >= requiredCount;
            if (!match) {
                continue;
            }
            String typeName = StrUtil.blankToDefault(ctx.equipmentTypeName, "其他设备");
            grouped.put(typeName, grouped.getOrDefault(typeName, 0L) + 1);
        }
        return grouped.entrySet().stream()
            .map(entry -> {
                Map<String, Object> row = new HashMap<>(2);
                row.put("equipmentTypeName", entry.getKey());
                row.put("equipmentCount", entry.getValue());
                return row;
            })
            .sorted(Comparator.comparing(row -> -MapUtil.getLong(row, "equipmentCount", 0L)))
            .collect(Collectors.toList());
    }

    private List<SummaryEquipmentContext> loadSummaryEquipmentContexts(EquipmentInspectionStatPageInfo pageInfo) {
        Map<String, EquipmentArchiveBizRecord> patrolRecordMap = equipmentArchiveBizRecordService
            .selectLatestMapByBizType(EquipmentArchiveBizType.PATROL.getKey());
        if (CollectionUtil.isEmpty(patrolRecordMap)) {
            return Collections.emptyList();
        }
        List<String> archiveIds = new ArrayList<>(patrolRecordMap.keySet());
        Map<String, EquipmentArchive> archiveMap = equipmentArchiveService.selectByIds(archiveIds.toArray(new String[0])).stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(EquipmentArchive::getId, item -> item, (a, b) -> a));
        List<String> planIds = patrolRecordMap.values().stream()
            .map(this::parsePlanId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Map<String, EquipmentInspectionPlan> planMap = CollectionUtil.isEmpty(planIds)
            ? Collections.emptyMap()
            : equipmentInspectionPlanService.selectByIds(planIds.toArray(new String[0])).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(EquipmentInspectionPlan::getId, item -> item, (a, b) -> a));

        List<SummaryEquipmentContext> contexts = new ArrayList<>();
        for (Map.Entry<String, EquipmentArchiveBizRecord> entry : patrolRecordMap.entrySet()) {
            EquipmentArchive archive = archiveMap.get(entry.getKey());
            if (archive == null) {
                continue;
            }
            String planId = parsePlanId(entry.getValue());
            EquipmentInspectionPlan plan = planMap.get(planId);
            if (StrUtil.isBlank(planId) || plan == null) {
                continue;
            }
            SummaryEquipmentContext ctx = new SummaryEquipmentContext();
            ctx.equipmentId = archive.getId();
            ctx.equipmentName = archive.getName();
            ctx.equipmentCode = archive.getOddNumber();
            ctx.equipmentTypeId = archive.getEquipmentTypeId();
            ctx.equipmentTypeName = resolveArchiveTypeName(archive);
            ctx.useFarm = archive.getUseFarm();
            ctx.installAddress = archive.getInstallAddress();
            ctx.planId = planId;
            ctx.plan = plan;
            contexts.add(ctx);
        }
        if (CollectionUtil.isNotEmpty(pageInfo.getEquipmentIdList())) {
            return contexts.stream()
                .filter(ctx -> pageInfo.getEquipmentIdList().contains(ctx.equipmentId))
                .collect(Collectors.toList());
        }
        return contexts;
    }

    private Map<String, Object> emptyDistribution() {
        Map<String, Object> result = new HashMap<>(2);
        result.put("rows", Collections.emptyList());
        result.put("total", 0);
        return result;
    }

    private Map<String, Integer> countInspectedByEquipment(EquipmentInspectionStatPageInfo pageInfo, List<SummaryEquipmentContext> contexts) {
        List<String> equipmentIds = contexts.stream().map(ctx -> ctx.equipmentId).collect(Collectors.toList());
        QueryWrapper<EquipmentInspectionOrder> wrapper = new QueryWrapper<>();
        wrapper.in(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), equipmentIds);
        wrapper.ge(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), pageInfo.getStartTime());
        wrapper.le(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), pageInfo.getEndTime());
        wrapper.select(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId));
        List<EquipmentInspectionOrder> orders = equipmentInspectionOrderService.list(wrapper);
        Map<String, Integer> countMap = new HashMap<>();
        for (EquipmentInspectionOrder order : orders) {
            countMap.merge(order.getEquipmentId(), 1, Integer::sum);
        }
        return countMap;
    }

    private Map<String, Object> buildSummaryRow(SummaryEquipmentContext ctx, int requiredCount, int inspectedCount, int missedCount) {
        Map<String, Object> row = new HashMap<>(16);
        row.put("equipmentId", ctx.equipmentId);
        row.put("equipmentName", ctx.equipmentName);
        row.put("equipmentCode", ctx.equipmentCode);
        row.put("equipmentTypeId", ctx.equipmentTypeId);
        row.put("equipmentTypeName", ctx.equipmentTypeName);
        row.put("useFarm", ctx.useFarm);
        row.put("installAddress", ctx.installAddress);
        row.put("planId", ctx.planId);
        row.put("requiredCount", requiredCount);
        row.put("inspectedCount", inspectedCount);
        row.put("missedCount", missedCount);
        return row;
    }

    private EquipmentInspectionStatPageInfo copyPageInfoForDistribution(EquipmentInspectionStatPageInfo source, String distributionType) {
        EquipmentInspectionStatPageInfo target = new EquipmentInspectionStatPageInfo();
        target.setStartTime(source.getStartTime());
        target.setEndTime(source.getEndTime());
        target.setObjectId(source.getObjectId());
        target.setKeyword(source.getKeyword());
        target.setHolderId(source.getHolderId());
        target.setEquipmentIdList(source.getEquipmentIdList());
        target.setDistributionType(distributionType);
        return target;
    }

    private String parsePlanId(EquipmentArchiveBizRecord record) {
        if (record == null || StrUtil.isBlank(record.getExtJson())) {
            return StrUtil.EMPTY;
        }
        return MapUtil.getStr(JSONUtil.toBean(record.getExtJson(), Map.class), "planId");
    }

    private String resolveArchiveTypeName(EquipmentArchive archive) {
        if (archive == null) {
            return "其他设备";
        }
        String typeName = EquipmentArchiveType.getNameByKey(archive.getEquipmentTypeId());
        if (StrUtil.isNotBlank(typeName)) {
            return typeName;
        }
        return StrUtil.blankToDefault(archive.getEquipmentTypeName(), StrUtil.blankToDefault(archive.getName(), "其他设备"));
    }

    private void appendSummaryFarmMation(List<Map<String, Object>> beans) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        for (Map<String, Object> bean : beans) {
            if (StrUtil.isNotBlank(MapUtil.getStr(bean, "useFarm"))) {
                Farm farm = new Farm();
                farm.setName(MapUtil.getStr(bean, "useFarm"));
                bean.put("farmMation", farm);
            }
        }
    }

    private List<Map<String, Object>> paginateList(List<Map<String, Object>> rows, int page, int limit) {
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        int fromIndex = Math.max(0, (page - 1) * limit);
        if (fromIndex >= rows.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(fromIndex + limit, rows.size());
        return rows.subList(fromIndex, toIndex);
    }

    private static class SummaryEquipmentContext {
        private String equipmentId;
        private String equipmentName;
        private String equipmentCode;
        private String equipmentTypeId;
        private String equipmentTypeName;
        private String useFarm;
        private String installAddress;
        private String planId;
        private EquipmentInspectionPlan plan;
    }
}
