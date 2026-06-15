/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
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
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionStatService;
import com.skyeye.farm.entity.Farm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        fillDefaultMonthRange(pageInfo);
        List<SummaryEquipmentContext> contexts = loadSummaryEquipmentContexts(pageInfo, null);
        Map<String, Object> panel = new HashMap<>(4);
        panel.put("uninspected", buildDistribution(pageInfo, contexts, DISTRIBUTION_UNINSPECTED));
        panel.put("inspected", buildDistribution(pageInfo, contexts, DISTRIBUTION_INSPECTED));
        outputObject.setBean(panel);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private void querySummaryPage(InputObject inputObject, OutputObject outputObject, boolean onlyMissed) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        fillDefaultMonthRange(pageInfo);
        List<SummaryEquipmentContext> allContexts = loadSummaryEquipmentContexts(pageInfo, null);
        if (CollectionUtil.isEmpty(allContexts)) {
            outputObject.setBeans(Collections.emptyList());
            outputObject.settotal(0);
            return;
        }
        Map<String, Integer> inspectedCountMap = countInspectedByEquipment(pageInfo, allContexts);
        List<String> targetArchiveIds = new ArrayList<>();
        for (SummaryEquipmentContext ctx : allContexts) {
            int requiredCount = equipmentInspectionPlanService.calcRequiredInspectionCount(ctx.plan, pageInfo.getStartTime(), pageInfo.getEndTime());
            int inspectedCount = inspectedCountMap.getOrDefault(ctx.equipmentId, 0);
            int missedCount = Math.max(0, requiredCount - inspectedCount);
            if (onlyMissed && missedCount <= 0) {
                continue;
            }
            targetArchiveIds.add(ctx.equipmentId);
        }
        if (CollectionUtil.isEmpty(targetArchiveIds)) {
            outputObject.setBeans(Collections.emptyList());
            outputObject.settotal(0);
            return;
        }
        Page<Object> page = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        QueryWrapper<EquipmentArchive> archiveWrapper = new QueryWrapper<>();
        archiveWrapper.in(MybatisPlusUtil.toColumns(EquipmentArchive::getId), targetArchiveIds);
        archiveWrapper.orderByAsc(MybatisPlusUtil.toColumns(EquipmentArchive::getName));
        List<EquipmentArchive> archives = equipmentArchiveService.list(archiveWrapper);
        Map<String, SummaryEquipmentContext> contextMap = allContexts.stream()
            .collect(Collectors.toMap(ctx -> ctx.equipmentId, ctx -> ctx, (a, b) -> a));
        List<Map<String, Object>> rows = new ArrayList<>();
        for (EquipmentArchive archive : archives) {
            SummaryEquipmentContext ctx = contextMap.get(archive.getId());
            if (ctx == null) {
                continue;
            }
            int requiredCount = equipmentInspectionPlanService.calcRequiredInspectionCount(ctx.plan, pageInfo.getStartTime(), pageInfo.getEndTime());
            int inspectedCount = inspectedCountMap.getOrDefault(ctx.equipmentId, 0);
            int missedCount = Math.max(0, requiredCount - inspectedCount);
            rows.add(buildSummaryRow(ctx, requiredCount, inspectedCount, missedCount));
        }
        appendSummaryFarmMation(rows);
        outputObject.setBeans(rows);
        outputObject.settotal(page.getTotal());
    }

    private Map<String, Object> buildDistribution(CommonPageInfo pageInfo, List<SummaryEquipmentContext> contexts, String distributionType) {
        List<Map<String, Object>> rows = queryEquipmentTypeDistribution(pageInfo, contexts, distributionType);
        Map<String, Object> result = new HashMap<>(2);
        result.put("rows", rows);
        result.put("total", rows.stream().mapToInt(row -> MapUtil.getInt(row, "equipmentCount", 0)).sum());
        return result;
    }

    private List<Map<String, Object>> queryEquipmentTypeDistribution(CommonPageInfo pageInfo, List<SummaryEquipmentContext> contexts,
                                                                     String distributionType) {
        if (CollectionUtil.isEmpty(contexts)) {
            return Collections.emptyList();
        }
        Map<String, Integer> inspectedCountMap = countInspectedByEquipment(pageInfo, contexts);
        Map<String, Long> grouped = new LinkedHashMap<>();
        for (SummaryEquipmentContext ctx : contexts) {
            int requiredCount = equipmentInspectionPlanService.calcRequiredInspectionCount(ctx.plan, pageInfo.getStartTime(), pageInfo.getEndTime());
            int inspectedCount = inspectedCountMap.getOrDefault(ctx.equipmentId, 0);
            boolean match = StrUtil.equals(DISTRIBUTION_UNINSPECTED, distributionType)
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

    private List<SummaryEquipmentContext> loadSummaryEquipmentContexts(CommonPageInfo pageInfo, List<String> equipmentIdList) {
        Map<String, EquipmentArchiveBizRecord> patrolRecordMap = equipmentArchiveBizRecordService
            .selectLatestMapByBizType(EquipmentArchiveBizType.PATROL.getKey());
        if (CollectionUtil.isEmpty(patrolRecordMap)) {
            return Collections.emptyList();
        }
        Set<String> scopedArchiveIds = resolveArchiveIds(pageInfo, patrolRecordMap.keySet());
        if (CollectionUtil.isNotEmpty(equipmentIdList)) {
            scopedArchiveIds.retainAll(new LinkedHashSet<>(equipmentIdList));
        }
        if (CollectionUtil.isEmpty(scopedArchiveIds)) {
            return Collections.emptyList();
        }
        Map<String, EquipmentArchive> archiveMap = equipmentArchiveService.selectByIds(scopedArchiveIds.toArray(new String[0])).stream()
            .filter(archive -> StrUtil.isNotBlank(archive.getId()))
            .collect(Collectors.toMap(EquipmentArchive::getId, item -> item, (a, b) -> a));
        List<String> planIds = scopedArchiveIds.stream()
            .map(archiveId -> parsePlanId(patrolRecordMap.get(archiveId)))
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Map<String, EquipmentInspectionPlan> planMap = CollectionUtil.isEmpty(planIds)
            ? Collections.emptyMap()
            : equipmentInspectionPlanService.selectByIds(planIds.toArray(new String[0])).stream()
                .filter(plan -> StrUtil.isNotBlank(plan.getId()))
                .collect(Collectors.toMap(EquipmentInspectionPlan::getId, item -> item, (a, b) -> a));

        List<SummaryEquipmentContext> contexts = new ArrayList<>();
        for (String archiveId : scopedArchiveIds) {
            EquipmentArchive archive = archiveMap.get(archiveId);
            if (archive == null) {
                continue;
            }
            String planId = parsePlanId(patrolRecordMap.get(archiveId));
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
        contexts.sort(Comparator.comparing(ctx -> ctx.equipmentName, Comparator.nullsLast(String::compareTo)));
        return contexts;
    }

    private Set<String> resolveArchiveIds(CommonPageInfo pageInfo, Set<String> patrolArchiveIds) {
        Set<String> ids = new LinkedHashSet<>(patrolArchiveIds);
        List<String> objectIdList = splitObjectIds(pageInfo.getObjectId());
        if (CollectionUtil.isNotEmpty(objectIdList)) {
            ids.retainAll(objectIdList);
        }
        if (StrUtil.isNotBlank(pageInfo.getHolderId()) || StrUtil.isNotBlank(pageInfo.getKeyword())) {
            QueryWrapper<EquipmentArchive> archiveWrapper = new QueryWrapper<>();
            archiveWrapper.in(MybatisPlusUtil.toColumns(EquipmentArchive::getId), ids);
            if (StrUtil.isNotBlank(pageInfo.getHolderId())) {
                archiveWrapper.eq(MybatisPlusUtil.toColumns(EquipmentArchive::getUseFarm), pageInfo.getHolderId());
            }
            if (StrUtil.isNotBlank(pageInfo.getKeyword())) {
                archiveWrapper.and(w -> w.like(MybatisPlusUtil.toColumns(EquipmentArchive::getName), pageInfo.getKeyword())
                    .or().like(MybatisPlusUtil.toColumns(EquipmentArchive::getOddNumber), pageInfo.getKeyword()));
            }
            ids = equipmentArchiveService.list(archiveWrapper).stream()
                .map(EquipmentArchive::getId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return ids;
    }

    private List<String> splitObjectIds(String objectId) {
        if (StrUtil.isBlank(objectId)) {
            return Collections.emptyList();
        }
        return Arrays.stream(objectId.split(","))
            .map(String::trim)
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.toList());
    }

    private void fillDefaultMonthRange(CommonPageInfo pageInfo) {
        Date now = new Date();
        if (StrUtil.isBlank(pageInfo.getStartTime())) {
            pageInfo.setStartTime(DateUtil.format(DateUtil.beginOfMonth(now), "yyyy-MM-dd HH:mm:ss"));
        }
        if (StrUtil.isBlank(pageInfo.getEndTime())) {
            pageInfo.setEndTime(DateUtil.format(DateUtil.endOfMonth(now), "yyyy-MM-dd HH:mm:ss"));
        }
    }

    private Map<String, Integer> countInspectedByEquipment(CommonPageInfo pageInfo, List<SummaryEquipmentContext> contexts) {
        List<String> equipmentIds = contexts.stream().map(ctx -> ctx.equipmentId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(equipmentIds)) {
            return Collections.emptyMap();
        }
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

    private String parsePlanId(EquipmentArchiveBizRecord record) {
        if (record == null || StrUtil.isBlank(record.getExtJson())) {
            return StrUtil.EMPTY;
        }
        return MapUtil.getStr(JSONUtil.toBean(record.getExtJson(), Map.class), "planId");
    }

    private String resolveArchiveTypeName(EquipmentArchive archive) {
        if (StrUtil.isBlank(archive.getId())) {
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
