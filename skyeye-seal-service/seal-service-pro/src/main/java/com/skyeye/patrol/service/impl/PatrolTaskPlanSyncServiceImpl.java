/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.enumeration.ScheduleFrequency;
import com.skyeye.patrol.entity.PatrolPlan;
import com.skyeye.patrol.entity.PatrolTask;
import com.skyeye.patrol.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 巡检计划与系统生成巡检任务的同步实现。
 * <p>
 * 由 XXL 按计划在触发点调用 {@link #generatePatrolTasksForPlan(String)}，在「今天起若干天」内按频次算出应执行的时段，
 * 为每个时段生成一条待执行任务，任务上挂载计划关联的全部点位与项目（多选）；收集后调用
 * {@link PatrolTaskService#createEntity(java.util.List, String)} 批量落库。
 * 同一计划+计划开始时间重复调用不会重复插入（幂等：按窗口内已有任务键过滤）。
 * 计划保存/删除时在 {@link com.skyeye.patrol.service.impl.PatrolPlanServiceImpl} 中联动取消未结束任务。
 *
 * @author skyeye云系列--卫志强
 * @date 2026/03/28
 */
@Slf4j
@Service
public class PatrolTaskPlanSyncServiceImpl implements PatrolTaskPlanSyncService {

    /**
     * 任务表 planned_start_time 与当前时间比较的字符串格式（字典序可比）
     */
    private static final DateTimeFormatter PLANNED_TIME_FORMAT = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD_HH_MM_SS);
    /**
     * 计划起止日期只取前 10 位 yyyy-MM-dd
     */
    private static final DateTimeFormatter PLAN_DATE_PREFIX = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD);
    /**
     * 每次生成向前覆盖的自然日天数（含当天）
     */
    private static final int ROLLING_DAYS = 7;
    /**
     * 未配置 patrolTime 时的默认时刻
     */
    private static final String DEFAULT_PATROL_TIME = "09:00";

    @Autowired
    private PatrolPlanService patrolPlanService;

    @Autowired
    private PatrolPlanPointService patrolPlanPointService;

    @Autowired
    private PatrolPlanItemService patrolPlanItemService;

    @Autowired
    private PatrolTaskService patrolTaskService;

    /**
     * 针对单个启用中的计划，在滚动窗口内生成待执行巡检任务（由动态注册的 XXL Job 按 CRON 触发）。
     */
    @Override
    public void generatePatrolTasksForPlan(String planId) {
        if (StrUtil.isBlank(planId)) {
            return;
        }
        PatrolPlan plan = patrolPlanService.selectById(planId);
        if (plan == null || EnableEnum.DISABLE_USING.getKey().equals(plan.getEnabled())) {
            // 计划不存在或已禁用,不生成任务
            return;
        }
        List<String> pointIds = patrolPlanPointService.selectByParentId(planId);
        if (CollectionUtil.isEmpty(pointIds)) {
            // 计划没有点位,不生成任务
            return;
        }
        List<String> itemIds = patrolPlanItemService.selectByParentId(planId);
        if (CollectionUtil.isEmpty(itemIds)) {
            return;
        }

        // 获取系统时区
        ZoneId zone = ZoneId.systemDefault();
        // 获取当前日期
        LocalDate today = LocalDate.now(zone);
        // 窗口终点：今天 + (ROLLING_DAYS-1) 天
        LocalDate end = today.plusDays(ROLLING_DAYS - 1);

        // 获取计划开始日期
        LocalDate planStart = parsePlanDate(plan.getStartTime());
        // 获取计划结束日期
        LocalDate planEnd = parsePlanDate(plan.getEndTime());
        // 计划开始日期为空,不生成任务
        if (planStart == null) {
            return;
        }
        // 实际遍历区间 = [max(计划开始日, 今天), min(计划结束日, 窗口末日)]
        LocalDate rangeStart = planStart.isAfter(today) ? planStart : today;
        // 计划结束日期为空,不生成任务
        LocalDate rangeEnd = planEnd != null && planEnd.isBefore(end) ? planEnd : end;
        // 计划开始日期大于计划结束日期,不生成任务
        if (rangeStart.isAfter(rangeEnd)) {
            return;
        }
        // 获取当前时间
        String nowStr = LocalDateTime.now(zone).format(PLANNED_TIME_FORMAT);
        // 先收集本窗口内「应生成」的任务实体（未判重）
        List<PatrolTask> candidates = new ArrayList<>();
        for (LocalDate day = rangeStart; !day.isAfter(rangeEnd); day = day.plusDays(1)) {
            List<LocalDateTime> slots = resolveSlotsForDay(plan, day, zone);
            for (LocalDateTime slotStart : slots) {
                String planned = slotStart.format(PLANNED_TIME_FORMAT);
                if (planned.compareTo(nowStr) < 0) {
                    continue;
                }
                PatrolTask task = new PatrolTask();
                task.setPlanId(plan.getId());
                task.setPointId(pointIds);
                task.setItemId(itemIds);
                task.setPlannedStartTime(planned);
                candidates.add(task);
            }
        }
        if (CollectionUtil.isEmpty(candidates)) {
            return;
        }
        // 候选集按「计划开始时间」去重，避免同键多条进入批量插入
        Map<String, PatrolTask> uniqCandidates = new LinkedHashMap<>();
        for (PatrolTask t : candidates) {
            uniqCandidates.putIfAbsent(patrolTaskDedupeKey(t), t);
        }
        candidates = new ArrayList<>(uniqCandidates.values());
        // 一次查询本计划在日期窗口内已有任务，内存过滤（避免逐条 count）
        Set<String> existedKeys = loadExistingPatrolTaskKeys(plan.getId(), rangeStart, rangeEnd);
        List<PatrolTask> toInsert = candidates.stream()
            .filter(t -> !existedKeys.contains(patrolTaskDedupeKey(t)))
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(toInsert)) {
            return;
        }
        try {
            patrolTaskService.createEntity(toInsert, CommonConstants.ADMIN_USER_ID);
            log.info("巡检计划[{}]本次批量生成任务条数={}", planId, toInsert.size());
        } catch (Exception e) {
            log.warn("巡检计划[{}]批量生成任务失败 err={}", planId, e.getMessage(), e);
        }
    }

    /**
     * 幂等键：同一计划下同一计划开始时间唯一（点位/项目已整包挂在任务上）
     */
    private static String patrolTaskDedupeKey(PatrolTask t) {
        return t.getPlannedStartTime();
    }

    /**
     * 查询该计划在 [rangeStart 00:00:00, rangeEnd 23:59:59] 内已存在的任务，返回幂等键集合
     */
    private Set<String> loadExistingPatrolTaskKeys(String planId, LocalDate rangeStart, LocalDate rangeEnd) {
        String tMin = rangeStart.format(PLAN_DATE_PREFIX) + " 00:00:00";
        String tMax = rangeEnd.format(PLAN_DATE_PREFIX) + " 23:59:59";
        QueryWrapper<PatrolTask> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(PatrolTask::getPlanId), planId);
        qw.ge(MybatisPlusUtil.toColumns(PatrolTask::getPlannedStartTime), tMin);
        qw.le(MybatisPlusUtil.toColumns(PatrolTask::getPlannedStartTime), tMax);
        List<PatrolTask> list = patrolTaskService.list(qw);
        if (CollectionUtil.isEmpty(list)) {
            return new HashSet<>();
        }
        return list.stream().map(PatrolTaskPlanSyncServiceImpl::patrolTaskDedupeKey).collect(Collectors.toSet());
    }

    /**
     * 时间字段取日期部分；解析失败返回 null
     */
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

    /**
     * 根据计划频次，解析某一天内应生成的「计划开始执行时刻」列表（可能多条，如自定义 Cron 当天多次触发）。
     */
    private List<LocalDateTime> resolveSlotsForDay(PatrolPlan plan, LocalDate day, ZoneId zone) {
        Integer freq = plan.getFrequency();
        if (freq == null) {
            return java.util.Collections.emptyList();
        }
        if (ScheduleFrequency.DAILY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, day)) {
                return java.util.Collections.emptyList();
            }
            return singleSlotFromPatrolTime(day, plan.getPatrolTime());
        }
        if (ScheduleFrequency.WEEKLY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, day)) {
                return java.util.Collections.emptyList();
            }
            if (!matchesWeekDays(plan.getWeekDays(), day)) {
                return java.util.Collections.emptyList();
            }
            return singleSlotFromPatrolTime(day, plan.getPatrolTime());
        }
        if (ScheduleFrequency.MONTHLY.getKey().equals(freq)) {
            if (!isDayInPlanWindow(plan, day)) {
                return java.util.Collections.emptyList();
            }
            if (!matchesMonthDays(plan.getMonthDays(), day)) {
                return java.util.Collections.emptyList();
            }
            return singleSlotFromPatrolTime(day, plan.getPatrolTime());
        }
        if (ScheduleFrequency.CUSTOM.getKey().equals(freq)) {
            return slotsFromCron(plan.getCustomCron(), day, zone);
        }
        return java.util.Collections.emptyList();
    }

    /**
     * 自然日 day 是否落在计划配置的起止日期范围内
     */
    private boolean isDayInPlanWindow(PatrolPlan plan, LocalDate day) {
        LocalDate ps = parsePlanDate(plan.getStartTime());
        if (ps != null && day.isBefore(ps)) {
            return false;
        }
        LocalDate pe = parsePlanDate(plan.getEndTime());
        return pe == null || !day.isAfter(pe);
    }

    /**
     * 每天/周/月频次：当天在 patrolTime 对应时刻有一条槽位
     */
    private List<LocalDateTime> singleSlotFromPatrolTime(LocalDate day, String patrolTime) {
        LocalTime t = parsePatrolTime(patrolTime);
        return java.util.Collections.singletonList(LocalDateTime.of(day, t));
    }

    /**
     * 解析 HH:mm 或 HH:mm:ss，失败则用默认 09:00
     */
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

    /**
     * weekDays：1-7 表示周一至周日，与 {@link java.time.DayOfWeek#getValue()} 一致
     */
    private boolean matchesWeekDays(String weekDays, LocalDate day) {
        if (StrUtil.isBlank(weekDays)) {
            return false;
        }
        int dow = day.getDayOfWeek().getValue();
        Set<Integer> set = parseIntCsv(weekDays);
        return set.contains(dow);
    }

    /**
     * monthDays：当月几号，1-31，逗号分隔
     */
    private boolean matchesMonthDays(String monthDays, LocalDate day) {
        if (StrUtil.isBlank(monthDays)) {
            return false;
        }
        int dom = day.getDayOfMonth();
        Set<Integer> set = parseIntCsv(monthDays);
        return set.contains(dom);
    }

    /**
     * 逗号分隔整数，非法片段跳过
     */
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
                // 跳过非法数字
            }
        }
        return set;
    }

    /**
     * 自定义频次：用 Spring {@link CronExpression} 推算「给定自然日」内的所有触发时刻（上限 200 次防死循环）。
     */
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
