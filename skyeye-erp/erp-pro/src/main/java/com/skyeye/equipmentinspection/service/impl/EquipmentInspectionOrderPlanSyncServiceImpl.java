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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 设备巡检方案与系统生成巡检单的同步实现（对齐工单巡检/保养：按计划开始时刻生单）。
 * <p>
 * 日期窗口、比较、周几/日号均用 {@link DateUtil}；执行时刻用 {@link QuartzCronUtil}。
 * 已过时刻跳过；幂等键 equipmentId + plannedStartTime。inspectionsPerDay 不参与生单数。
 */
@Slf4j
@Service
public class EquipmentInspectionOrderPlanSyncServiceImpl implements EquipmentInspectionOrderPlanSyncService {

    private static final int ROLLING_DAYS = 7;

    @Autowired
    private EquipmentInspectionPlanService equipmentInspectionPlanService;

    @Autowired
    private EquipmentInspectionOrderService equipmentInspectionOrderService;

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

        String today = DateUtil.getYmdTimeAndToString();
        Date endDate = DateUtil.getAfDate(DateUtil.getPointTime(today, DateUtil.YYYY_MM_DD), ROLLING_DAYS - 1, "d");
        String end = DateUtil.formatDate2Str(endDate, DateUtil.YYYY_MM_DD);

        String planStart = toYmd(plan.getStartTime());
        String planEnd = toYmd(plan.getEndTime());
        if (StrUtil.isBlank(planStart)) {
            return;
        }
        // compare：time1 不晚于 time2 为 true → planStart<=today 用 today，否则用 planStart
        String rangeStart = DateUtil.compare(planStart + " 00:00:00", today + " 00:00:00") ? today : planStart;
        String rangeEnd = StrUtil.isNotBlank(planEnd) && DateUtil.compare(planEnd + " 00:00:00", end + " 00:00:00")
            ? planEnd : end;
        if (DateUtil.compare(rangeEnd + " 00:00:00", rangeStart + " 00:00:00")
            && !StrUtil.equals(rangeStart, rangeEnd)) {
            return;
        }

        String nowStr = DateUtil.getTimeAndToString();
        List<EquipmentInspectionOrder> candidates = new ArrayList<>();
        for (String planDate : DateUtil.getDays(rangeStart, rangeEnd)) {
            List<String> plannedTimes = resolvePlannedTimesForDay(plan, planDate);
            int slotIndex = 0;
            for (String planned : plannedTimes) {
                slotIndex++;
                // 已过时刻跳过（planned < now）
                if (DateUtil.compare(planned, nowStr) && !StrUtil.equals(planned, nowStr)) {
                    continue;
                }
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
            equipmentInspectionOrderService.createEntity(toInsert, CommonConstants.ADMIN_USER_ID);
            log.info("设备巡检方案[{}]本次批量生成巡检单条数={}", planId, toInsert.size());
        } catch (Exception e) {
            log.warn("设备巡检方案[{}]批量生成巡检单失败 err={}", planId, e.getMessage(), e);
        }
    }

    private static String orderDedupeKey(EquipmentInspectionOrder o) {
        return o.getEquipmentId() + "|" + o.getPlannedStartTime();
    }

    private Set<String> loadExistingOrderKeys(String planId, String rangeStart, String rangeEnd) {
        QueryWrapper<EquipmentInspectionOrder> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getPlanId), planId);
        qw.ge(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getPlannedStartTime), rangeStart + " 00:00:00");
        qw.le(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getPlannedStartTime), rangeEnd + " 23:59:59");
        List<EquipmentInspectionOrder> list = equipmentInspectionOrderService.list(qw);
        if (CollectionUtil.isEmpty(list)) {
            return new HashSet<>();
        }
        return list.stream().map(EquipmentInspectionOrderPlanSyncServiceImpl::orderDedupeKey).collect(Collectors.toSet());
    }

    private static String toYmd(String dateTime) {
        if (StrUtil.isBlank(dateTime) || dateTime.length() < 10) {
            return null;
        }
        try {
            Date date = DateUtil.getPointTime(dateTime.substring(0, 10), DateUtil.YYYY_MM_DD);
            return DateUtil.formatDate2Str(date, DateUtil.YYYY_MM_DD);
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> resolvePlannedTimesForDay(EquipmentInspectionPlan plan, String planDate) {
        Integer freq = plan.getFrequency();
        if (ScheduleFrequency.DAILY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, planDate)) {
                return Collections.emptyList();
            }
            return singlePlannedTime(planDate, plan.getPatrolTime());
        }
        if (ScheduleFrequency.WEEKLY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, planDate)) {
                return Collections.emptyList();
            }
            if (!matchesWeekDays(plan.getWeekDays(), planDate)) {
                return Collections.emptyList();
            }
            return singlePlannedTime(planDate, plan.getPatrolTime());
        }
        if (ScheduleFrequency.MONTHLY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, planDate)) {
                return Collections.emptyList();
            }
            if (!matchesMonthDays(plan.getMonthDays(), planDate)) {
                return Collections.emptyList();
            }
            return singlePlannedTime(planDate, plan.getPatrolTime());
        }
        if (ScheduleFrequency.CUSTOM.getKey().equals(freq)) {
            return plannedTimesFromCron(plan.getCustomCron(), planDate);
        }
        return Collections.emptyList();
    }

    private boolean isDayInPlanWindow(EquipmentInspectionPlan plan, String planDate) {
        String ps = toYmd(plan.getStartTime());
        if (StrUtil.isNotBlank(ps)
            && DateUtil.compare(planDate + " 00:00:00", ps + " 00:00:00")
            && !StrUtil.equals(planDate, ps)) {
            return false;
        }
        String pe = toYmd(plan.getEndTime());
        if (StrUtil.isBlank(pe)) {
            return true;
        }
        return DateUtil.compare(planDate + " 00:00:00", pe + " 00:00:00");
    }

    private List<String> singlePlannedTime(String planDate, String patrolTime) {
        LocalTime t = QuartzCronUtil.parseExecuteTime(patrolTime);
        String planned = String.format("%s %02d:%02d:00", planDate, t.getHour(), t.getMinute());
        return Collections.singletonList(planned);
    }

    private boolean matchesWeekDays(String weekDays, String planDate) {
        if (StrUtil.isBlank(weekDays)) {
            return false;
        }
        return parseIntCsv(weekDays).contains(DateUtil.getWeek(planDate));
    }

    private boolean matchesMonthDays(String monthDays, String planDate) {
        if (StrUtil.isBlank(monthDays)) {
            return false;
        }
        int dayOfMonth = DateUtil.getTime(DateUtil.getPointTime(planDate, DateUtil.YYYY_MM_DD), "d");
        return parseIntCsv(monthDays).contains(dayOfMonth);
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

    /**
     * 自定义 Cron：Spring CronExpression 无 DateUtil 等价物，仅此处保留 java.time
     */
    private List<String> plannedTimesFromCron(String cron, String planDate) {
        if (StrUtil.isBlank(cron)) {
            return Collections.emptyList();
        }
        ZoneId zone = ZoneId.systemDefault();
        LocalDate day = DateUtil.getPointTime(planDate, DateUtil.YYYY_MM_DD).toInstant().atZone(zone).toLocalDate();
        try {
            CronExpression ce = CronExpression.parse(cron.trim());
            Set<String> uniq = new HashSet<>();
            List<String> out = new ArrayList<>();
            ZonedDateTime probe = day.atStartOfDay(zone).minusNanos(1);
            for (int i = 0; i < 200; i++) {
                ZonedDateTime next = ce.next(probe);
                if (next == null || !next.toLocalDate().equals(day)) {
                    break;
                }
                LocalDateTime ldt = next.toLocalDateTime();
                String planned = String.format("%s %02d:%02d:%02d",
                    planDate, ldt.getHour(), ldt.getMinute(), ldt.getSecond());
                if (uniq.add(planned)) {
                    out.add(planned);
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
