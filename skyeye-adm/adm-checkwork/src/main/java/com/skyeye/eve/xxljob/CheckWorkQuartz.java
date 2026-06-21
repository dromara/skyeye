/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.xxljob;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.checkwork.service.CheckWorkService;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateAfterSpacePointTime;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.checkwork.entity.CheckWork;
import com.skyeye.eve.service.IScheduleDayService;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.leave.entity.LeaveTimeSlot;
import com.skyeye.leave.service.LeaveService;
import com.skyeye.scheduling.service.SchedulingService;
import com.skyeye.worktime.entity.CheckWorkTime;
import com.skyeye.worktime.service.CheckWorkTimeService;
import com.skyeye.worktime.util.CheckWorkTimePeriodUtil;
import com.skyeye.worktime.util.CheckWorkTimeWeekUtil;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CheckWorkQuartz
 * @Description: 定时器填充打卡信息, 每天凌晨一点执行一次
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/25 21:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class CheckWorkQuartz {

    private static Logger log = LoggerFactory.getLogger(CheckWorkQuartz.class);

    @Autowired
    private CheckWorkService checkWorkService;

    @Autowired
    private CheckWorkTimeService checkWorkTimeService;

    @Autowired
    private LeaveService checkWorkLeaveService;

    @Autowired
    private IScheduleDayService iScheduleDayService;

    @Autowired
    private ITenantService iTenantService;

    @Autowired
    private SchedulingService schedulingService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    /**
     * 定时器填充打卡信息,每天凌晨一点执行一次
     */
    @XxlJob("checkWorkQuartz")
    public void editCheckWorkMation() {
        log.info("填充打卡信息定时任务执行");
        try {
            if (tenantEnable) {
                //  开启多租户
                List<Map<String, Object>> tenantList = iTenantService.queryAllTenantList();
                if (CollectionUtil.isEmpty(tenantList)) {
                    return;
                }
                tenantList.forEach(tenant -> {
                    String tenantId = tenant.get("id").toString();
                    TenantContext.setTenantId(tenantId);
                    calcCheckWork();
                });
            } else {
                calcCheckWork();
            }
        } catch (Exception e) {
            log.warn("CheckWorkQuartz error.", e);
        }
        log.info("填充打卡信息定时任务 end");
    }

    private void calcCheckWork() {
        // 1.获取所有的考勤班次信息
        List<CheckWorkTime> workTime = checkWorkTimeService.queryAllData().stream()
            .filter(item -> EnableEnum.ENABLE_USING.getKey().equals(item.getEnabled()))
            .collect(Collectors.toList());

        String yesterdayTime = getYesterdayTime();
        String dayBeforeYesterdayTime = DateAfterSpacePointTime.getSpecifiedTime(
            DateAfterSpacePointTime.ONE_DAY.getType(), yesterdayTime, DateUtil.YYYY_MM_DD,
            DateAfterSpacePointTime.AroundType.BEFORE);

        // 2.同日班次：结算昨天
        if (!iScheduleDayService.judgeISHoliday(yesterdayTime)) {
            log.info("Fill in same-day shift clocking information, checkDate={}", yesterdayTime);
            if (CollectionUtil.isNotEmpty(workTime)) {
                settleShiftsForDate(yesterdayTime, workTime, false);
            }
            // 排班员工：timeId 为 schedulingTimeId，与固定班次并行结算
            settleSchedulingForDate(yesterdayTime, false);
        }

        // 3.跨天班次：结算前天（班次在前天开始、昨天凌晨结束，需等结束后再处理）
        if (!iScheduleDayService.judgeISHoliday(dayBeforeYesterdayTime)) {
            log.info("Fill in cross-day shift clocking information, checkDate={}", dayBeforeYesterdayTime);
            if (CollectionUtil.isNotEmpty(workTime)) {
                settleShiftsForDate(dayBeforeYesterdayTime, workTime, true);
            }
            // 跨天排班：checkDate 为班次归属日（开始日），须等次日凌晨下班后再结算
            settleSchedulingForDate(dayBeforeYesterdayTime, true);
        }

        // 4.处理所有昨天加班只打早卡没有打晚卡的记录id
        handleNotCheckWorkEndMember(yesterdayTime, "-");
    }

    private String getYesterdayTime() {
        return DateAfterSpacePointTime.getSpecifiedTime(
            DateAfterSpacePointTime.ONE_DAY.getType(), DateUtil.getTimeAndToString(), DateUtil.YYYY_MM_DD,
            DateAfterSpacePointTime.AroundType.BEFORE);
    }

    /**
     * 按考勤日结算缺卡/旷工
     *
     * @param checkDate  考勤归属日 yyyy-MM-dd
     * @param workTime   启用中的班次
     * @param crossDayOnly true 仅跨天班次；false 仅同日班次
     */
    private void settleShiftsForDate(String checkDate, List<CheckWorkTime> workTime, boolean crossDayOnly) {
        int weekDay = DateUtil.getWeek(checkDate);
        int weekType = DateUtil.getWeekType(checkDate);
        List<CheckWorkTime> shouldCheckTime = getShouldCheckTime(weekDay, weekType, workTime);
        if (CollectionUtil.isEmpty(shouldCheckTime)) {
            return;
        }
        for (CheckWorkTime bean : shouldCheckTime) {
            boolean crossDay = CheckWorkTimePeriodUtil.isCrossDay(bean.getStartTime(), bean.getEndTime());
            if (crossDayOnly != crossDay) {
                continue;
            }
            if (crossDay && !CheckWorkTimePeriodUtil.isCrossDaySettleReady(checkDate, bean.getEndTime())) {
                log.info("Cross-day shift {} on {} is not ready for settlement, skip.", bean.getId(), checkDate);
                continue;
            }
            try {
                handleNotCheckWorkEndMember(checkDate, bean.getId());
                handleNotCheckWorkMember(checkDate, bean.getId());
            } catch (Exception e) {
                log.info("Handling abnormal attendance information, message is {}.", e);
            }
        }
    }

    /**
     * 排班考勤日结算缺卡/旷工（与固定班次 {@link #settleShiftsForDate} 分两路：同日 / 跨天）
     * <p>
     * timeId 使用 schedulingTimeId，与排班打卡写入一致；请假判断不限班次。
     *
     * @param checkDate    考勤归属日 yyyy-MM-dd
     * @param crossDayOnly true 仅跨天排班；false 仅同日排班
     */
    private void settleSchedulingForDate(String checkDate, boolean crossDayOnly) {
        List<Map<String, Object>> targets = schedulingService.queryScheduleCheckTargetsForDate(checkDate);
        if (CollectionUtil.isEmpty(targets)) {
            return;
        }
        for (Map<String, Object> target : targets) {
            String schedulingTimeId = target.get("schedulingTimeId").toString();
            String userId = target.get("userId").toString();
            String startTime = target.get("startTime").toString();
            String endTime = target.get("endTime").toString();
            // isNextDay 或 start>end 均视为跨天排班
            boolean crossDay = WhetherEnum.ENABLE_USING.getKey().equals(target.get("isNextDay"))
                || CheckWorkTimePeriodUtil.isCrossDay(startTime, endTime);
            if (crossDayOnly != crossDay) {
                continue;
            }
            if (crossDay && !CheckWorkTimePeriodUtil.isCrossDaySettleReady(checkDate, endTime)) {
                log.info("Cross-day schedule {} on {} is not ready for settlement, skip.", schedulingTimeId, checkDate);
                continue;
            }
            try {
                handleScheduleNotCheckEndMember(checkDate, schedulingTimeId);
                handleScheduleNotCheckMember(checkDate, schedulingTimeId, userId);
            } catch (Exception e) {
                log.info("Handling schedule abnormal attendance, message is {}.", e);
            }
        }
    }

    /** 排班：补录「只打上班卡、未打下班卡」为缺晚卡 state=5 */
    private void handleScheduleNotCheckEndMember(String checkDate, String schedulingTimeId) {
        List<Map<String, Object>> beans = checkWorkService.queryScheduleNotCheckEndWorkId(schedulingTimeId, checkDate);
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        for (Map<String, Object> b : beans) {
            Map<String, Object> checkWorkMation = new HashMap<>();
            checkWorkMation.put("id", b.get("id").toString());
            checkWorkMation.put("state", "5");
            checkWorkMation.put("clockOutState", "3");
            checkWorkMation.put("workHours", "0:0:0");
            checkWorkService.editCheckWorkBySystem(checkWorkMation);
        }
    }

    /**
     * 排班：全天无打卡记录且当日无请假 → 系统写入旷工 state=2
     * timeId 使用 schedulingTimeId，与移动端/网站排班打卡一致
     */
    private void handleScheduleNotCheckMember(String checkDate, String schedulingTimeId, String userId) {
        CheckWork existing = checkWorkService.queryAlreadyCheck(checkDate, userId, schedulingTimeId);
        if (ObjectUtil.isNotEmpty(existing)) {
            return;
        }
        if (checkWorkLeaveService.hasApprovedLeaveOnDay(userId, checkDate)) {
            return;
        }
        List<Map<String, Object>> listBeans = new ArrayList<>();
        listBeans.add(getNoCheckWorkObject(schedulingTimeId, userId, checkDate));
        checkWorkService.insertCheckWorkBySystem(listBeans);
    }

    /**
     * 获取昨天应该打卡的班次信息
     *
     * @param weekDay  周几
     * @param weekType 1是双周，0是单周
     * @param workTime 考勤班次
     * @return
     */
    private List<CheckWorkTime> getShouldCheckTime(int weekDay, int weekType, List<CheckWorkTime> workTime) {
        List<CheckWorkTime> shouldCheckTime = new ArrayList<>();
        for (CheckWorkTime bean : workTime) {
            if (CheckWorkTimeWeekUtil.isWorkDay(weekDay, weekType, bean.getCheckWorkTimeWeekList())) {
                shouldCheckTime.add(bean);
            }
        }
        log.info("shouldCheckTime is {}", JSONUtil.toJsonStr(shouldCheckTime));
        return shouldCheckTime;
    }

    /**
     * 处理所有昨天只打早卡没有打晚卡的记录id
     *
     * @param yesterdayTime
     * @param timeId
     */
    private void handleNotCheckWorkEndMember(String yesterdayTime, String timeId) {
        List<Map<String, Object>> beans = checkWorkService.queryNotCheckEndWorkId(timeId, yesterdayTime);
        if (!beans.isEmpty()) {
            for (Map<String, Object> b : beans) {
                Map<String, Object> checkWorkMation = new HashMap<>();
                checkWorkMation.put("id", b.get("id").toString());
                checkWorkMation.put("state", "5");
                checkWorkMation.put("clockOutState", "3");
                checkWorkMation.put("workHours", "0:0:0");
                // 填充打晚卡信息
                checkWorkService.editCheckWorkBySystem(checkWorkMation);
            }
        }
    }

    /**
     * 处理所有昨天没有打卡的用户
     *
     * @param yesterdayTime 昨天的日期,格式为：yyyy-MM-dd
     * @param timeId        班次id
     */
    private void handleNotCheckWorkMember(String yesterdayTime, String timeId) {
        // 获取所有昨天没有打卡的用户
        List<Map<String, Object>> beans = checkWorkService.queryNotCheckMember(timeId, yesterdayTime);
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<Map<String, Object>> listBeans = new ArrayList<>();
        for (Map<String, Object> b : beans) {
            String createId = b.get("createId").toString();
            // 判断昨天是否有请假记录,如果有，则不填充这条记录
            LeaveTimeSlot leaveMation = checkWorkLeaveService.queryCheckWorkLeaveByMation(timeId, createId, yesterdayTime);
            if (ObjectUtil.isEmpty(leaveMation)) {
                // 找不到该员工这个班次在这一天的请假记录，则记为旷工
                listBeans.add(getNoCheckWorkObject(timeId, createId, yesterdayTime));
            }
        }
        if (CollectionUtil.isNotEmpty(listBeans)) {
            checkWorkService.insertCheckWorkBySystem(listBeans);
        }
    }

    private Map<String, Object> getNoCheckWorkObject(String timeId, String createId, String yesterdayTime) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", ToolUtil.getSurFaceId());
        item.put("createId", createId);
        item.put("checkDate", yesterdayTime);
        item.put("state", "2");
        item.put("clockInState", "3");
        item.put("clockOutState", "3");
        item.put("timeId", timeId);
        item.put("workHours", "0:0:0");
        return item;
    }

}
