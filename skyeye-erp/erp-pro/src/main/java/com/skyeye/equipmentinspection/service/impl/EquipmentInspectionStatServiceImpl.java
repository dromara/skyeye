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
        PatrolStatBundle bundle = loadPatrolStatBundle(pageInfo);
        Map<String, Object> panel = new HashMap<>(2);
        panel.put("uninspected", buildTypeDistribution(bundle, false));
        panel.put("inspected", buildTypeDistribution(bundle, true));
        outputObject.setBean(panel);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private void querySummaryPage(InputObject inputObject, OutputObject outputObject, boolean onlyMissed) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        fillDefaultMonthRange(pageInfo);
        PatrolStatBundle bundle = loadPatrolStatBundle(pageInfo);
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
        QueryWrapper<EquipmentArchive> archiveWrapper = new QueryWrapper<>();
        archiveWrapper.in(MybatisPlusUtil.toColumns(EquipmentArchive::getId), targetIds);
        archiveWrapper.orderByAsc(MybatisPlusUtil.toColumns(EquipmentArchive::getName));
        List<Map<String, Object>> rows = new ArrayList<>();
        for (EquipmentArchive archive : equipmentArchiveService.list(archiveWrapper)) {
            int[] stat = bundle.statMap.get(archive.getId());
            rows.add(buildSummaryRow(archive, bundle.planIdMap.get(archive.getId()), stat));
        }
        appendSummaryFarmMation(rows);
        outputObject.setBeans(rows);
        outputObject.settotal(page.getTotal());
    }

    private PatrolStatBundle loadPatrolStatBundle(CommonPageInfo pageInfo) {
        Map<String, EquipmentArchiveBizRecord> patrolRecordMap = equipmentArchiveBizRecordService
            .selectLatestMapByBizType(EquipmentArchiveBizType.PATROL.getKey());
        Map<String, EquipmentArchive> archiveMap = loadPatrolArchiveMap(pageInfo, patrolRecordMap);
        Map<String, String> planIdMap = new HashMap<>();
        Map<String, Integer> inspectedMap = countInspected(pageInfo, archiveMap.keySet());
        Map<String, int[]> statMap = new LinkedHashMap<>();
        for (EquipmentArchive archive : archiveMap.values()) {
            String planId = parsePlanId(patrolRecordMap.get(archive.getId()));
            if (StrUtil.isBlank(planId)) {
                continue;
            }
            EquipmentInspectionPlan plan = equipmentInspectionPlanService.getDataFromDb(planId);
            if (StrUtil.isBlank(plan.getId())) {
                continue;
            }
            planIdMap.put(archive.getId(), planId);
            int required = equipmentInspectionPlanService.calcRequiredInspectionCount(plan, pageInfo.getStartTime(), pageInfo.getEndTime());
            int inspected = inspectedMap.getOrDefault(archive.getId(), 0);
            statMap.put(archive.getId(), new int[]{required, inspected, Math.max(0, required - inspected)});
        }
        PatrolStatBundle bundle = new PatrolStatBundle();
        bundle.archiveMap = archiveMap;
        bundle.planIdMap = planIdMap;
        bundle.statMap = statMap;
        return bundle;
    }

    private Map<String, Object> buildTypeDistribution(PatrolStatBundle bundle, boolean inspected) {
        Map<String, Long> grouped = new LinkedHashMap<>();
        for (Map.Entry<String, int[]> entry : bundle.statMap.entrySet()) {
            int[] stat = entry.getValue();
            if (inspected ? stat[1] < stat[0] : stat[1] >= stat[0]) {
                continue;
            }
            EquipmentArchive archive = bundle.archiveMap.get(entry.getKey());
            String typeName = resolveArchiveTypeName(archive);
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

    private Map<String, EquipmentArchive> loadPatrolArchiveMap(CommonPageInfo pageInfo,
                                                               Map<String, EquipmentArchiveBizRecord> patrolRecordMap) {
        if (CollectionUtil.isEmpty(patrolRecordMap)) {
            return Collections.emptyMap();
        }
        Set<String> scopedArchiveIds = resolveArchiveIds(pageInfo, patrolRecordMap.keySet());
        if (CollectionUtil.isEmpty(scopedArchiveIds)) {
            return Collections.emptyMap();
        }
        return equipmentArchiveService.selectByIds(scopedArchiveIds.toArray(new String[0])).stream()
            .filter(archive -> StrUtil.isNotBlank(archive.getId()))
            .collect(Collectors.toMap(EquipmentArchive::getId, item -> item, (a, b) -> a, LinkedHashMap::new));
    }

    private String parsePlanId(EquipmentArchiveBizRecord record) {
        if (StrUtil.isBlank(record.getExtJson())) {
            return StrUtil.EMPTY;
        }
        return MapUtil.getStr(JSONUtil.toBean(record.getExtJson(), Map.class), "planId");
    }

    private Set<String> resolveArchiveIds(CommonPageInfo pageInfo, Set<String> patrolArchiveIds) {
        Set<String> ids = new LinkedHashSet<>(patrolArchiveIds);
        if (StrUtil.isNotBlank(pageInfo.getObjectId())) {
            ids.retainAll(Arrays.stream(pageInfo.getObjectId().split(","))
                .map(String::trim).filter(StrUtil::isNotBlank).collect(Collectors.toSet()));
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

    private void fillDefaultMonthRange(CommonPageInfo pageInfo) {
        Date now = new Date();
        if (StrUtil.isBlank(pageInfo.getStartTime())) {
            pageInfo.setStartTime(DateUtil.format(DateUtil.beginOfMonth(now), "yyyy-MM-dd HH:mm:ss"));
        }
        if (StrUtil.isBlank(pageInfo.getEndTime())) {
            pageInfo.setEndTime(DateUtil.format(DateUtil.endOfMonth(now), "yyyy-MM-dd HH:mm:ss"));
        }
    }

    private Map<String, Integer> countInspected(CommonPageInfo pageInfo, Set<String> equipmentIds) {
        if (CollectionUtil.isEmpty(equipmentIds)) {
            return Collections.emptyMap();
        }
        QueryWrapper<EquipmentInspectionOrder> wrapper = new QueryWrapper<>();
        wrapper.in(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), equipmentIds);
        wrapper.ge(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), pageInfo.getStartTime());
        wrapper.le(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), pageInfo.getEndTime());
        wrapper.select(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId));
        Map<String, Integer> countMap = new HashMap<>();
        equipmentInspectionOrderService.list(wrapper).forEach(order -> countMap.merge(order.getEquipmentId(), 1, Integer::sum));
        return countMap;
    }

    private Map<String, Object> buildSummaryRow(EquipmentArchive archive, String planId, int[] stat) {
        Map<String, Object> row = new HashMap<>(12);
        row.put("equipmentId", archive.getId());
        row.put("equipmentName", archive.getName());
        row.put("equipmentCode", archive.getOddNumber());
        row.put("equipmentTypeId", archive.getEquipmentTypeId());
        row.put("equipmentTypeName", resolveArchiveTypeName(archive));
        row.put("useFarm", archive.getUseFarm());
        row.put("installAddress", archive.getInstallAddress());
        row.put("planId", planId);
        row.put("requiredCount", stat[0]);
        row.put("inspectedCount", stat[1]);
        row.put("missedCount", stat[2]);
        return row;
    }

    private String resolveArchiveTypeName(EquipmentArchive archive) {
        if (StrUtil.isBlank(archive.getId())) {
            return "其他设备";
        }
        return StrUtil.blankToDefault(EquipmentArchiveType.getNameByKey(archive.getEquipmentTypeId()),
            StrUtil.blankToDefault(archive.getEquipmentTypeName(), "其他设备"));
    }

    private void appendSummaryFarmMation(List<Map<String, Object>> beans) {
        beans.forEach(bean -> {
            String useFarm = MapUtil.getStr(bean, "useFarm");
            if (StrUtil.isNotBlank(useFarm)) {
                Farm farm = new Farm();
                farm.setName(useFarm);
                bean.put("farmMation", farm);
            }
        });
    }

    private static class PatrolStatBundle {
        private Map<String, EquipmentArchive> archiveMap = Collections.emptyMap();
        private Map<String, String> planIdMap = Collections.emptyMap();
        private Map<String, int[]> statMap = Collections.emptyMap();
    }

}
