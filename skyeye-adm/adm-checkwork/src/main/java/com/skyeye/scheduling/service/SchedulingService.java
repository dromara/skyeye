package com.skyeye.scheduling.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.scheduling.entity.Scheduling;

import java.util.List;
import java.util.Map;

public interface SchedulingService extends SkyeyeBusinessService<Scheduling> {

    void autoComputeScheduling(InputObject inputObject, OutputObject outputObject);

    void querySchedulingByStaffId(InputObject inputObject, OutputObject outputObject);

    void deleteSchedulingByIds(InputObject inputObject, OutputObject outputObject);

    void querySchedulingList(InputObject inputObject, OutputObject outputObject);

    void querySchedulingByStaffIdAndMouths(InputObject inputObject, OutputObject outputObject);

    List<String> querySchedulingByStaffIdAndMouths(String staffId, List<String> mouthList);

    /**
     * 员工在指定月份内、指定排班班次下的出勤日期（与打卡校验 shiftId 一致）
     */
    List<String> querySchedulingWorkDaysByStaffAndShift(String staffId, String shiftId, List<String> mouthList);

    void querySchedulingByStaffIdAndOneDay(InputObject inputObject, OutputObject outputObject);

    void querySchedulingByStaffIdAndDays(InputObject inputObject, OutputObject outputObject);

    List<Scheduling> querySchedulingByIdList(List<String> schedulingIdList);

    /**
     * 指定考勤日应打卡的排班人员列表（供 CheckWorkQuartz 缺勤结算）
     * <p>
     * 返回字段：userId、schedulingTimeId、startTime、endTime、isNextDay
     *
     * @param checkDate 考勤归属日 yyyy-MM-dd
     */
    List<Map<String, Object>> queryScheduleCheckTargetsForDate(String checkDate);

    /**
     * 员工在指定日期是否安排了某排班班次（SchedulingShifts.id）
     *
     * @param staffId 员工 id
     * @param shiftId 排班班次模板 id（SchedulingShifts.id）
     * @param day     自然日 yyyy-MM-dd
     */
    boolean isStaffScheduledForShiftOnDate(String staffId, String shiftId, String day);

}
