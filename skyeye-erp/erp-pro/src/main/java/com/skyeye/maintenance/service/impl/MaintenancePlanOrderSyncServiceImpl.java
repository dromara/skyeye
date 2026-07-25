/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.enumeration.ScheduleFrequency;
import com.skyeye.maintenance.dao.EquipmentMaintainOrderDao;
import com.skyeye.maintenance.entity.EquipmentMaintainOrder;
import com.skyeye.maintenance.entity.EquipmentMaintainOrderItem;
import com.skyeye.maintenance.entity.MaintenancePlan;
import com.skyeye.maintenance.entity.MaintenancePlanItem;
import com.skyeye.maintenance.service.EquipmentMaintainOrderItemService;
import com.skyeye.maintenance.service.EquipmentMaintainOrderService;
import com.skyeye.maintenance.service.MaintenancePlanItemService;
import com.skyeye.maintenance.service.MaintenancePlanOrderSyncService;
import com.skyeye.maintenance.service.MaintenancePlanService;
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
 * 保养计划与设备保养单自动同步实现。
 * <p>
 * 由 XXL 按计划在触发点调用，在「今天起若干天」内按频次算出应执行的时段，
 * 为每个时段生成一条保养单（计划未配置明细时仍生成主单，明细为空）；同一计划+设备+保养时间重复调用不会重复插入（幂等）。
 */
@Slf4j
@Service
public class MaintenancePlanOrderSyncServiceImpl implements MaintenancePlanOrderSyncService {

    private static final DateTimeFormatter PLANNED_TIME_FORMAT = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD_HH_MM_SS);
    private static final DateTimeFormatter PLAN_DATE_PREFIX = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD);
    private static final int ROLLING_DAYS = 7;
    private static final String DEFAULT_MAINTAIN_TIME = "09:00";

    @Autowired
    private MaintenancePlanService maintenancePlanService;

    @Autowired
    private MaintenancePlanItemService maintenancePlanItemService;

    @Autowired
    private EquipmentMaintainOrderService equipmentMaintainOrderService;

    @Autowired
    private EquipmentMaintainOrderItemService equipmentMaintainOrderItemService;

    @Autowired
    private EquipmentMaintainOrderDao equipmentMaintainOrderDao;

    @Override
    public void generateMaintainOrdersForPlan(String planId) {
        if (StrUtil.isBlank(planId)) {
            return;
        }
        MaintenancePlan plan = maintenancePlanService.selectById(planId);
        if (plan == null || EnableEnum.DISABLE_USING.getKey().equals(plan.getEnabled())) {
            return;
        }
        if (StrUtil.isBlank(plan.getEquipmentId())) {
            return;
        }
        List<MaintenancePlanItem> planItems = maintenancePlanItemService.selectByParentId(planId);

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
        List<EquipmentMaintainOrder> candidates = new ArrayList<>();
        for (LocalDate day = rangeStart; !day.isAfter(rangeEnd); day = day.plusDays(1)) {
            List<LocalDateTime> slots = resolveSlotsForDay(plan, day, zone);
            for (LocalDateTime slotStart : slots) {
                String planned = slotStart.format(PLANNED_TIME_FORMAT);
                if (planned.compareTo(nowStr) < 0) {
                    continue;
                }
                EquipmentMaintainOrder task = new EquipmentMaintainOrder();
                task.setPlanId(plan.getId());
                task.setEquipmentId(plan.getEquipmentId());
                task.setPlannedStartTime(planned);
                if (CollectionUtil.isNotEmpty(planItems)) {
                    task.setMaintainOrderItemList(equipmentMaintainOrderItemService.copyFromPlanItems(planItems));
                }
                candidates.add(task);
            }
        }
        if (CollectionUtil.isEmpty(candidates)) {
            return;
        }

        Map<String, EquipmentMaintainOrder> uniqCandidates = new LinkedHashMap<>();
        for (EquipmentMaintainOrder order : candidates) {
            uniqCandidates.putIfAbsent(maintainOrderDedupeKey(order), order);
        }
        candidates = new ArrayList<>(uniqCandidates.values());

        Set<String> existedKeys = loadExistingMaintainOrderKeys(plan.getId(), rangeStart, rangeEnd);
        List<EquipmentMaintainOrder> toInsert = candidates.stream()
            .filter(order -> !existedKeys.contains(maintainOrderDedupeKey(order)))
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(toInsert)) {
            return;
        }

        try {
            equipmentMaintainOrderService.createEntity(toInsert, CommonConstants.ADMIN_USER_ID);
            // 批量 createEntity 走基类 writePostpose(List)，不会保存子表；主单落库后按 id 补写保养明细
            for (EquipmentMaintainOrder order : toInsert) {
                if (CollectionUtil.isNotEmpty(order.getMaintainOrderItemList())) {
                    equipmentMaintainOrderItemService.saveList(order.getId(), order.getMaintainOrderItemList());
                }
            }
            log.info("保养计划[{}]本次批量生成任务条数={}", planId, toInsert.size());
        } catch (Exception e) {
            log.warn("保养计划[{}]批量生成任务失败 err={}", planId, e.getMessage(), e);
        }
    }

    private static String maintainOrderDedupeKey(EquipmentMaintainOrder task) {
        return task.getEquipmentId() + "|" + task.getPlannedStartTime();
    }

    private Set<String> loadExistingMaintainOrderKeys(String planId, LocalDate rangeStart, LocalDate rangeEnd) {
        String tMin = rangeStart.format(PLAN_DATE_PREFIX) + " 00:00:00";
        String tMax = rangeEnd.format(PLAN_DATE_PREFIX) + " 23:59:59";
        QueryWrapper<EquipmentMaintainOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentMaintainOrder::getPlanId), planId);
        queryWrapper.ge(MybatisPlusUtil.toColumns(EquipmentMaintainOrder::getPlannedStartTime), tMin);
        queryWrapper.le(MybatisPlusUtil.toColumns(EquipmentMaintainOrder::getPlannedStartTime), tMax);
        List<EquipmentMaintainOrder> list = equipmentMaintainOrderDao.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return new HashSet<>();
        }
        return list.stream().map(MaintenancePlanOrderSyncServiceImpl::maintainOrderDedupeKey).collect(Collectors.toSet());
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

    private List<LocalDateTime> resolveSlotsForDay(MaintenancePlan plan, LocalDate day, ZoneId zone) {
        Integer freq = plan.getFrequency();
        if (freq == null) {
            return Collections.emptyList();
        }
        if (ScheduleFrequency.DAILY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, day)) {
                return Collections.emptyList();
            }
            return singleSlotFromMaintainTime(day, plan.getMaintainTime());
        }
        if (ScheduleFrequency.WEEKLY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, day)) {
                return Collections.emptyList();
            }
            if (!matchesWeekDays(plan.getWeekDays(), day)) {
                return Collections.emptyList();
            }
            return singleSlotFromMaintainTime(day, plan.getMaintainTime());
        }
        if (ScheduleFrequency.MONTHLY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, day)) {
                return Collections.emptyList();
            }
            if (!matchesMonthDays(plan.getMonthDays(), day)) {
                return Collections.emptyList();
            }
            return singleSlotFromMaintainTime(day, plan.getMaintainTime());
        }
        if (ScheduleFrequency.CUSTOM.getKey().equals(freq)) {
            return slotsFromCron(plan.getCustomCron(), day, zone);
        }
        return Collections.emptyList();
    }

    private boolean isDayInPlanWindow(MaintenancePlan plan, LocalDate day) {
        LocalDate ps = parsePlanDate(plan.getStartTime());
        if (ps != null && day.isBefore(ps)) {
            return false;
        }
        LocalDate pe = parsePlanDate(plan.getEndTime());
        return pe == null || !day.isAfter(pe);
    }

    private List<LocalDateTime> singleSlotFromMaintainTime(LocalDate day, String maintainTime) {
        LocalTime t = parseMaintainTime(maintainTime);
        return Collections.singletonList(LocalDateTime.of(day, t));
    }

    private LocalTime parseMaintainTime(String maintainTime) {
        String s = StrUtil.isBlank(maintainTime) ? DEFAULT_MAINTAIN_TIME : maintainTime.trim();
        try {
            if (s.length() == 5 && s.charAt(2) == ':') {
                return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm"));
            }
            return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (Exception e1) {
            try {
                return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception e2) {
                return LocalTime.parse(DEFAULT_MAINTAIN_TIME, DateTimeFormatter.ofPattern("HH:mm"));
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
