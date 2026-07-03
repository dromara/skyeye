/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.support;

import cn.hutool.core.util.StrUtil;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionFrequencyType;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 设备巡检方案频次：CRON 注册表达式 + 自定义频次槽位推算
 */
@Slf4j
public final class EquipmentInspectionPlanCronBuilder {

    private static final String DEFAULT_PATROL_TIME = "09:00";
    private static final int MAX_CRON_PROBE = 200;
    private static final String[] ISO_DOW_TO_QUARTZ = {"", "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

    private EquipmentInspectionPlanCronBuilder() {
    }

    public static String buildScheduleConf(EquipmentInspectionPlan plan) {
        if (plan == null || plan.getFrequencyType() == null) {
            return null;
        }
        Integer freq = plan.getFrequencyType();
        LocalTime t = parsePatrolTime(plan.getPatrolTime());
        int second = 0;
        int minute = t.getMinute();
        int hour = t.getHour();

        if (EquipmentInspectionFrequencyType.DAY.getKey().equals(freq)) {
            return String.format("%d %d %d * * ?", second, minute, hour);
        }
        if (EquipmentInspectionFrequencyType.WEEK.getKey().equals(freq)) {
            List<String> quartzDows = parseWeekDaysToQuartz(plan.getWeekDays());
            if (quartzDows.isEmpty()) {
                return null;
            }
            return String.format("%d %d %d ? * %s", second, minute, hour, String.join(",", quartzDows));
        }
        if (EquipmentInspectionFrequencyType.MONTH.getKey().equals(freq)) {
            String dom = parseMonthDaysDom(plan.getMonthDays());
            if (StrUtil.isBlank(dom)) {
                return null;
            }
            return String.format("%d %d %d %s * ?", second, minute, hour, dom);
        }
        if (EquipmentInspectionFrequencyType.QUARTER.getKey().equals(freq)) {
            String dom = StrUtil.blankToDefault(parseMonthDaysDom(plan.getMonthDays()), "1");
            return String.format("%d %d %d %s 1,4,7,10 ?", second, minute, hour, dom);
        }
        if (EquipmentInspectionFrequencyType.YEAR.getKey().equals(freq)) {
            String dom = StrUtil.blankToDefault(parseMonthDaysDom(plan.getMonthDays()), "1");
            return String.format("%d %d %d %s 1 ?", second, minute, hour, dom);
        }
        if (EquipmentInspectionFrequencyType.CUSTOM.getKey().equals(freq)) {
            return StrUtil.trimToNull(plan.getCustomCron());
        }
        return null;
    }

    /** 自定义频次：推算某自然日内的触发时刻 */
    public static List<LocalDateTime> resolveDaySlots(String cron, LocalDate day, ZoneId zone) {
        if (StrUtil.isBlank(cron)) {
            return Collections.emptyList();
        }
        try {
            CronExpression ce = CronExpression.parse(cron.trim());
            Set<LocalDateTime> uniq = new HashSet<>();
            List<LocalDateTime> out = new ArrayList<>();
            ZonedDateTime probe = day.atStartOfDay(zone).minusNanos(1);
            for (int i = 0; i < MAX_CRON_PROBE; i++) {
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

    /** 统计区间内自定义 Cron 触发次数（仅设备漏检统计使用） */
    public static int countRangeSlots(String cron, LocalDate rangeStart, LocalDate rangeEnd, ZoneId zone) {
        if (StrUtil.isBlank(cron) || rangeStart == null || rangeEnd == null || rangeStart.isAfter(rangeEnd)) {
            return 0;
        }
        int total = 0;
        for (LocalDate day = rangeStart; !day.isAfter(rangeEnd); day = day.plusDays(1)) {
            total += resolveDaySlots(cron, day, zone).size();
        }
        return total;
    }

    private static LocalTime parsePatrolTime(String patrolTime) {
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

    private static List<String> parseWeekDaysToQuartz(String weekDays) {
        List<String> out = new ArrayList<>();
        if (StrUtil.isBlank(weekDays)) {
            return out;
        }
        for (String p : weekDays.split(",")) {
            String t = p.trim();
            if (StrUtil.isEmpty(t)) {
                continue;
            }
            try {
                int iso = Integer.parseInt(t);
                if (iso >= 1 && iso <= 7) {
                    out.add(ISO_DOW_TO_QUARTZ[iso]);
                }
            } catch (NumberFormatException ignored) {
                // skip
            }
        }
        return out.stream().distinct().collect(Collectors.toList());
    }

    private static String parseMonthDaysDom(String monthDays) {
        if (StrUtil.isBlank(monthDays)) {
            return null;
        }
        List<String> parts = new ArrayList<>();
        for (String p : monthDays.split(",")) {
            String t = p.trim();
            if (StrUtil.isEmpty(t)) {
                continue;
            }
            try {
                int d = Integer.parseInt(t);
                if (d >= 1 && d <= 31) {
                    parts.add(String.valueOf(d));
                }
            } catch (NumberFormatException ignored) {
                // skip
            }
        }
        if (parts.isEmpty()) {
            return null;
        }
        return String.join(",", parts.stream().distinct().collect(Collectors.toList()));
    }
}
