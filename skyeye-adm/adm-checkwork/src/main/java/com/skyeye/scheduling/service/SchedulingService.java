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

}
