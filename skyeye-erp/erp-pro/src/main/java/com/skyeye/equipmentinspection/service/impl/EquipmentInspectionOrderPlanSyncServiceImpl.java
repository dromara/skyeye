/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.ScheduleFrequency;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.QuartzCronUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderPlanSyncService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import com.skyeye.equipmentinspection.support.EquipmentInspectionOrderBatchCreateSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 设备巡检方案与系统生成巡检单的同步实现（对齐工单巡检/保养：按计划开始时刻生单）。
 * <p>
 * XXL 触发后，在「今天起若干天」内按频次算出时段，为每个「设备 × 计划开始时刻」生成待派工单；
 * 已过时刻跳过；幂等键 planId + equipmentId + plannedStartTime。
 * inspectionsPerDay 不参与生单数。
 */
@Slf4j
@Service
public class EquipmentInspectionOrderPlanSyncServiceImpl implements EquipmentInspectionOrderPlanSyncService {

    private static final DateTimeFormatter PLANNED_TIME_FORMAT = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD_HH_MM_SS);
    private static final DateTimeFormatter PLAN_DATE_PREFIX = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD);
    private static final int ROLLING_DAYS = 7;

    @Autowired
    private EquipmentInspectionPlanService equipmentInspectionPlanService;

    @Autowired
    private EquipmentInspectionOrderService equipmentInspectionOrderService;

    @Autowired
    private EquipmentInspectionOrderBatchCreateSupport orderBatchCreateSupport;

    @Override
    public void generateInspectionOrdersForPlan(String planId) {
        if (StrUtil.isBlank(planId)) {
            return;
        }
        EquipmentInspectionPlan plan = equipmentInspectionPlanService.selectById(planId);
        if (StrUtil.isBlank(plan.getId())
            || EnableEnum.DISABLE_USING.getKey().equals(plan.getEnabled())) {
            return;
        }
        List<String> equipmentIds = CollectionUtil.isEmpty(plan.getEquipmentId())
            ? Collections.emptyList()
            : plan.getEquipmentId().stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(equipmentIds)) {
            return;
        }

        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        LocalDate end = today.plusDays(ROLLING_DAYS - 1);

        LocalDate planStart = parsePlanDate(plan.getStartTime());
        LocalDate planEnd = parsePlanDate(plan.getEndTime());
        if (planStart == null) {
            return;
        }
        LocalDate rangeStart = planStart.isAfter(today) ? planStart : today;
        LocalDate rangeEnd = planEnd != null && planEnd.isBefore(end) ? planEnd : end;
        if (rangeStart.isAfter(rangeEnd)) {
            return;
        }

        String nowStr = LocalDateTime.now(zone).format(PLANNED_TIME_FORMAT);
        List<EquipmentInspectionOrder> candidates = new ArrayList<>();
        for (LocalDate day = rangeStart; !day.isAfter(rangeEnd); day = day.plusDays(1)) {
            List<LocalDateTime> slots = resolveSlotsForDay(plan, day, zone);
            int slotIndex = 0;
            for (LocalDateTime slotStart : slots) {
                slotIndex++;
                String planned = slotStart.format(PLANNED_TIME_FORMAT);
                if (planned.compareTo(nowStr) < 0) {
                    continue;
                }
                String planDate = day.format(PLAN_DATE_PREFIX);
                for (String equipmentId : equipmentIds) {
                    EquipmentInspectionOrder order = new EquipmentInspectionOrder();
                    order.setPlanId(plan.getId());
                    order.setEquipmentId(equipmentId);
                    order.setPlanDate(planDate);
                    order.setPlannedStartTime(planned);
                    order.setSlotIndex(slotIndex);
                    candidates.add(order);
                }
            }
        }
        if (CollectionUtil.isEmpty(candidates)) {
            return;
        }
        Map<String, EquipmentInspectionOrder> uniqCandidates = new LinkedHashMap<>();
        for (EquipmentInspectionOrder o : candidates) {
            uniqCandidates.putIfAbsent(orderDedupeKey(o), o);
        }
        candidates = new ArrayList<>(uniqCandidates.values());
        Set<String> existedKeys = loadExistingOrderKeys(plan.getId(), rangeStart, rangeEnd);
        List<EquipmentInspectionOrder> toInsert = candidates.stream()
            .filter(o -> !existedKeys.contains(orderDedupeKey(o)))
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(toInsert)) {
            return;
        }
        try {
            orderBatchCreateSupport.createEntityBatchForPlanGenerate(toInsert, CommonConstants.ADMIN_USER_ID);
            log.info("设备巡检方案[{}]本次批量生成巡检单条数={}", planId, toInsert.size());
        } catch (Exception e) {
            log.warn("设备巡检方案[{}]批量生成巡检单失败 err={}", planId, e.getMessage(), e);
        }
    }

    private static String orderDedupeKey(EquipmentInspectionOrder o) {
        return o.getEquipmentId() + "|" + o.getPlannedStartTime();
    }

    private Set<String> loadExistingOrderKeys(String planId, LocalDate rangeStart, LocalDate rangeEnd) {
        String tMin = rangeStart.format(PLAN_DATE_PREFIX) + " 00:00:00";
        String tMax = rangeEnd.format(PLAN_DATE_PREFIX) + " 23:59:59";
        QueryWrapper<EquipmentInspectionOrder> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getPlanId), planId);
        qw.ge(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getPlannedStartTime), tMin);
        qw.le(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getPlannedStartTime), tMax);
        List<EquipmentInspectionOrder> list = equipmentInspectionOrderService.list(qw);
        if (CollectionUtil.isEmpty(list)) {
            return new HashSet<>();
        }
        return list.stream().map(EquipmentInspectionOrderPlanSyncServiceImpl::orderDedupeKey).collect(Collectors.toSet());
    }

    private LocalDate parsePlanDate(String time) {
        if (StrUtil.isBlank(time) || time.length() < 10) {
            return null;
        }
        try {
            return LocalDate.parse(time.substring(0, 10), PLAN_DATE_PREFIX);
        } catch (Exception e) {
            return null;
        }
    }

    private List<LocalDateTime> resolveSlotsForDay(EquipmentInspectionPlan plan, LocalDate day, ZoneId zone) {
        Integer freq = plan.getFrequency();
        if (ScheduleFrequency.DAILY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, day)) {
                return Collections.emptyList();
            }
            return singleSlotFromPatrolTime(day, plan.getPatrolTime());
        }
        if (ScheduleFrequency.WEEKLY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, day)) {
                return Collections.emptyList();
            }
            if (!matchesWeekDays(plan.getWeekDays(), day)) {
                return Collections.emptyList();
            }
            return singleSlotFromPatrolTime(day, plan.getPatrolTime());
        }
        if (ScheduleFrequency.MONTHLY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, day)) {
                return Collections.emptyList();
            }
            if (!matchesMonthDays(plan.getMonthDays(), day)) {
                return Collections.emptyList();
            }
            return singleSlotFromPatrolTime(day, plan.getPatrolTime());
        }
        if (ScheduleFrequency.CUSTOM.getKey().equals(freq)) {
            return slotsFromCron(plan.getCustomCron(), day, zone);
        }
        return Collections.emptyList();
    }

    private boolean isDayInPlanWindow(EquipmentInspectionPlan plan, LocalDate day) {
        LocalDate ps = parsePlanDate(plan.getStartTime());
        if (ps != null && day.isBefore(ps)) {
            return false;
        }
        LocalDate pe = parsePlanDate(plan.getEndTime());
        return pe == null || !day.isAfter(pe);
    }

    private List<LocalDateTime> singleSlotFromPatrolTime(LocalDate day, String patrolTime) {
        LocalTime t = QuartzCronUtil.parseExecuteTime(patrolTime);
        return Collections.singletonList(LocalDateTime.of(day, t));
    }

    private boolean matchesWeekDays(String weekDays, LocalDate day) {
        if (StrUtil.isBlank(weekDays)) {
            return false;
        }
        return parseIntCsv(weekDays).contains(day.getDayOfWeek().getValue());
    }

    private boolean matchesMonthDays(String monthDays, LocalDate day) {
        if (StrUtil.isBlank(monthDays)) {
            return false;
        }
        return parseIntCsv(monthDays).contains(day.getDayOfMonth());
    }

    private static Set<Integer> parseIntCsv(String csv) {
        Set<Integer> set = new HashSet<>();
        for (String p : csv.split(",")) {
            String t = p.trim();
            if (StrUtil.isEmpty(t)) {
                continue;
            }
            try {
                set.add(Integer.parseInt(t));
            } catch (NumberFormatException ignored) {
                // skip
            }
        }
        return set;
    }

    private List<LocalDateTime> slotsFromCron(String cron, LocalDate day, ZoneId zone) {
        if (StrUtil.isBlank(cron)) {
            return Collections.emptyList();
        }
        try {
            CronExpression ce = CronExpression.parse(cron.trim());
            Set<LocalDateTime> uniq = new HashSet<>();
            List<LocalDateTime> out = new ArrayList<>();
            ZonedDateTime probe = day.atStartOfDay(zone).minusNanos(1);
            for (int i = 0; i < 200; i++) {
                ZonedDateTime next = ce.next(probe);
                if (next == null || !next.toLocalDate().equals(day)) {
                    break;
                }
                LocalDateTime ldt = next.toLocalDateTime();
                if (uniq.add(ldt)) {
                    out.add(ldt);
                }
                probe = next;
            }
            return out;
        } catch (Exception e) {
            log.warn("解析自定义 Cron 失败 cron={} err={}", cron, e.getMessage());
            return Collections.emptyList();
        }
    }

}
