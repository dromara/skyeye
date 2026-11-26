/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.checkwork.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.checkwork.entity.CheckWork;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CheckWorkService
 * @Description: 考勤打卡管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/6 15:00
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CheckWorkService extends SkyeyeBusinessService<CheckWork> {

    void insertCheckWorkStartWork(InputObject inputObject, OutputObject outputObject);

    void editCheckWorkEndWork(InputObject inputObject, OutputObject outputObject);

    void queryCheckWorkIdByAppealType(InputObject inputObject, OutputObject outputObject);

    void queryCheckWorkTimeToShowButton(InputObject inputObject, OutputObject outputObject);

    void queryCheckWorkMationByMonth(InputObject inputObject, OutputObject outputObject);

    void getUserOtherDayMation(InputObject inputObject, OutputObject outputObject);

    /**
     * 获取用户指定班次在指定月份的其他日期信息[审核通过的](例如：请假，出差，加班等)
     *
     * @param userId 用户id
     * @param timeId 班次id
     * @param months 指定月份，格式["2020-04", "2020-05"...]
     * @return
     */
    List<Map<String, Object>> getUserOtherDayMation(String userId, String timeId, List<String> months);

    /**
     * 获取节假日信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void queryDayWorkMation(InputObject inputObject, OutputObject outputObject);

    void queryCheckWorkReport(InputObject inputObject, OutputObject outputObject);

    /**
     * 查询单个员工在日期范围内的考勤汇总（应出勤、正常、旷工、迟到等）
     *
     * @param staffId   员工id
     * @param startTime 开始日期 yyyy-MM-dd
     * @param endTime   结束日期 yyyy-MM-dd
     * @return 汇总行；无数据时返回空 Map
     */
    Map<String, Object> queryStaffCheckWorkSummary(String staffId, String startTime, String endTime);

    void queryCheckWorkEcharts(InputObject inputObject, OutputObject outputObject);

    void queryReportDetail(InputObject inputObject, OutputObject outputObject);

    void queryDayWorkMation(List<Map<String, Object>> beans, List<String> months, String timeId, String shiftType, String staffId);

    List<Map<String, Object>> queryNotCheckMember(String timeId, String yesterdayTime);

    List<Map<String, Object>> queryNotCheckEndWorkId(String timeId, String yesterdayTime);

    void editCheckWorkBySystem(Map<String, Object> map);

    List<Map<String, Object>> queryCheckWorkOvertimeWaitSettlement();

    /**
     * 排班定时任务：查询指定排班时间段、考勤日只打上班卡未打下班卡的记录
     */
    List<Map<String, Object>> queryScheduleNotCheckEndWorkId(String schedulingTimeId, String checkDate);

    /**
     * 加班缺晚卡：time_id='-' 且只打了上班卡、未打下班卡（供 CheckWorkQuartz 定时补 state=5）
     */
    List<Map<String, Object>> queryOvertimeNotCheckEndWorkId(String checkDate);

    /**
     * 查询指定用户、班次、考勤日是否已有打卡记录（含系统补卡）
     */
    CheckWork queryAlreadyCheck(String checkDate, String userId, String timeId);

    void insertCheckWorkBySystem(List<Map<String, Object>> beans);

    void queryInfoByStaffIdsAndDates(InputObject inputObject, OutputObject outputObject);
}
