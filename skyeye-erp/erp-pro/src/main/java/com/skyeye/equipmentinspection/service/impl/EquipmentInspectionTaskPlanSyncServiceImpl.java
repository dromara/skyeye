/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionFrequencyType;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionTask;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionTaskPlanSyncService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionTaskService;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 设备巡检方案与系统生成巡检任务的同步实现
 */
@Slf4j
@Service
public class EquipmentInspectionTaskPlanSyncServiceImpl implements EquipmentInspectionTaskPlanSyncService {

    private static final DateTimeFormatter PLANNED_TIME_FORMAT = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD_HH_MM_SS);
    private static final DateTimeFormatter PLAN_DATE_PREFIX = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD);
    private static final int ROLLING_DAYS = 7;
    private static final String DEFAULT_PATROL_TIME = "09:00";

    @Autowired
    private EquipmentInspectionPlanService equipmentInspectionPlanService;

    @Autowired
    private EquipmentInspectionTaskService equipmentInspectionTaskService;

    @Override
    public void generateTasksForPlan(String planId) {
        if (StrUtil.isBlank(planId)) {
            return;
        }
        EquipmentInspectionPlan plan = equipmentInspectionPlanService.getDataFromDb(planId);
        if (StrUtil.isBlank(plan.getId()) || EnableEnum.DISABLE_USING.getKey().equals(plan.getEnabled())) {
            return;
        }
        List<String> equipmentIds = plan.getEquipmentId();
        if (CollectionUtil.isEmpty(equipmentIds)) {
            return;
        }
        if (CollectionUtil.isEmpty(plan.getItemId())) {
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
        List<EquipmentInspectionTask> candidates = new ArrayList<>();
        List<String> itemIds = plan.getItemId();
        for (LocalDate day = rangeStart; !day.isAfter(rangeEnd); day = day.plusDays(1)) {
            List<LocalDateTime> slots = resolveSlotsForDay(plan, day, zone);
            for (LocalDateTime slotStart : slots) {
                String planned = slotStart.format(PLANNED_TIME_FORMAT);
                if (planned.compareTo(nowStr) < 0) {
                    continue;
                }
                for (String equipmentId : equipmentIds) {
                    for (String itemId : itemIds) {
                        EquipmentInspectionTask task = new EquipmentInspectionTask();
                        task.setPlanId(plan.getId());
                        task.setEquipmentId(equipmentId);
                        task.setItemId(itemId);
                        task.setPlannedStartTime(planned);
                        candidates.add(task);
                    }
                }
            }
        }
        if (CollectionUtil.isEmpty(candidates)) {
            return;
        }
        Map<String, EquipmentInspectionTask> uniqCandidates = new LinkedHashMap<>();
        for (EquipmentInspectionTask t : candidates) {
            uniqCandidates.putIfAbsent(taskDedupeKey(t), t);
        }
        candidates = new ArrayList<>(uniqCandidates.values());
        Set<String> existedKeys = loadExistingTaskKeys(plan.getId(), rangeStart, rangeEnd);
        List<EquipmentInspectionTask> toInsert = candidates.stream()
            .filter(t -> !existedKeys.contains(taskDedupeKey(t)))
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(toInsert)) {
            return;
        }
        try {
            equipmentInspectionTaskService.createEntity(toInsert, CommonConstants.ADMIN_USER_ID);
            log.info("设备巡检方案[{}]本次批量生成任务条数={}", planId, toInsert.size());
        } catch (Exception e) {
            log.warn("设备巡检方案[{}]批量生成任务失败 err={}", planId, e.getMessage(), e);
        }
    }

    @Override
    public int countRangeSlots(EquipmentInspectionPlan plan, String startTime, String endTime) {
        if (StrUtil.isBlank(plan.getId())) {
            return 0;
        }
        java.util.Date start = cn.hutool.core.date.DateUtil.parse(startTime);
        java.util.Date end = cn.hutool.core.date.DateUtil.parse(endTime);
        ZoneId zone = ZoneId.systemDefault();
        LocalDate rangeStart = start.toInstant().atZone(zone).toLocalDate();
        LocalDate rangeEnd = end.toInstant().atZone(zone).toLocalDate();
        if (rangeStart.isAfter(rangeEnd)) {
            return 0;
        }
        int slots = 0;
        for (LocalDate day = rangeStart; !day.isAfter(rangeEnd); day = day.plusDays(1)) {
            slots += resolveSlotsForDay(plan, day, zone).size();
        }
        int itemCount = CollectionUtil.isEmpty(plan.getItemId()) ? 0 : plan.getItemId().size();
        return slots * itemCount;
    }

    private static String taskDedupeKey(EquipmentInspectionTask t) {
        return t.getEquipmentId() + "|" + t.getItemId() + "|" + t.getPlannedStartTime();
    }

    private Set<String> loadExistingTaskKeys(String planId, LocalDate rangeStart, LocalDate rangeEnd) {
        String tMin = rangeStart.format(PLAN_DATE_PREFIX) + " 00:00:00";
        String tMax = rangeEnd.format(PLAN_DATE_PREFIX) + " 23:59:59";
        QueryWrapper<EquipmentInspectionTask> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(EquipmentInspectionTask::getPlanId), planId);
        qw.ge(MybatisPlusUtil.toColumns(EquipmentInspectionTask::getPlannedStartTime), tMin);
        qw.le(MybatisPlusUtil.toColumns(EquipmentInspectionTask::getPlannedStartTime), tMax);
        List<EquipmentInspectionTask> list = equipmentInspectionTaskService.list(qw);
        if (CollectionUtil.isEmpty(list)) {
            return new HashSet<>();
        }
        return list.stream().map(EquipmentInspectionTaskPlanSyncServiceImpl::taskDedupeKey).collect(Collectors.toSet());
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
        if (freq == null) {
            return java.util.Collections.emptyList();
        }
        if (EquipmentInspectionFrequencyType.DAILY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, day)) {
                return java.util.Collections.emptyList();
            }
            return singleSlotFromPatrolTime(day, plan.getPatrolTime());
        }
        if (EquipmentInspectionFrequencyType.WEEKLY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, day)) {
                return java.util.Collections.emptyList();
            }
            if (!matchesWeekDays(plan.getWeekDays(), day)) {
                return java.util.Collections.emptyList();
            }
            return singleSlotFromPatrolTime(day, plan.getPatrolTime());
        }
        if (EquipmentInspectionFrequencyType.MONTHLY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, day)) {
                return java.util.Collections.emptyList();
            }
            if (!matchesMonthDays(plan.getMonthDays(), day)) {
                return java.util.Collections.emptyList();
            }
            return singleSlotFromPatrolTime(day, plan.getPatrolTime());
        }
        if (EquipmentInspectionFrequencyType.CUSTOM.getKey().equals(freq)) {
            return slotsFromCron(plan.getCustomCron(), day, zone);
        }
        return java.util.Collections.emptyList();
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
        LocalTime t = parsePatrolTime(patrolTime);
        return java.util.Collections.singletonList(LocalDateTime.of(day, t));
    }

    private LocalTime parsePatrolTime(String patrolTime) {
        String s = StrUtil.isBlank(patrolTime) ? DEFAULT_PATROL_TIME : patrolTime.trim();
        try {
            if (s.length() == 5 && s.charAt(2) == ':') {
                return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm"));
            }
            return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (Exception e1) {
            try {
                return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception e2) {
                return LocalTime.parse(DEFAULT_PATROL_TIME, DateTimeFormatter.ofPattern("HH:mm"));
            }
        }
    }

    private boolean matchesWeekDays(String weekDays, LocalDate day) {
        if (StrUtil.isBlank(weekDays)) {
            return false;
        }
        int dow = day.getDayOfWeek().getValue();
        return parseIntCsv(weekDays).contains(dow);
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
            return java.util.Collections.emptyList();
        }
        try {
            CronExpression ce = CronExpression.parse(cron.trim());
            Set<LocalDateTime> uniq = new HashSet<>();
            List<LocalDateTime> out = new ArrayList<>();
            ZonedDateTime probe = day.atStartOfDay(zone).minusNanos(1);
            for (int i = 0; i < 200; i++) {
                ZonedDateTime next = ce.next(probe);
                if (next == null) {
                    break;
                }
                if (!next.toLocalDate().equals(day)) {
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
            return java.util.Collections.emptyList();
        }
    }
}
