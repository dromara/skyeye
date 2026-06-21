/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.leave.service;

import com.skyeye.base.business.service.SkyeyeLinkDataService;
import com.skyeye.leave.entity.LeaveTimeSlot;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: LeaveTimeSlotService
 * @Description: 请假申请请假时间段服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/5 8:40
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface LeaveTimeSlotService extends SkyeyeLinkDataService<LeaveTimeSlot> {

    void editStateById(String id, String state, Integer useYearHoliday);

    /**
     * 根据请假单 id 列表查询对应的请假时间段，并按请假单 id 分组
     *
     * @param leaveIds 请假单 id 列表
     * @return key 为请假单 id，value 为该单下的请假时间段列表
     */
    Map<String, List<LeaveTimeSlot>> queryLeaveTimeSlotListByLeaveIds(List<String> leaveIds);

    /**
     * 根据请假单 id 列表和日期范围，查询与该范围有交集的请假时间段
     *
     * @param leaveIds  请假单 id 列表
     * @param startTime 开始日期，格式：yyyy-MM-dd
     * @param endTime   结束日期，格式：yyyy-MM-dd
     * @return 与 [startTime, endTime] 有重叠的请假时间段列表
     */
    List<LeaveTimeSlot> queryTimeAndIds(List<String> leaveIds, String startTime, String endTime);

    /**
     * 获取上个月指定员工的所有审批通过请假记录信息
     *
     * @param staffId            员工id
     * @param lastMonthDate      上个月的年月，格式：yyyy-MM
     * @param leaveTimeState     请假记录状态  {@link com.skyeye.common.enumeration.FlowableStateEnum}
     * @param leaveTimeSlotState 请假时间段状态 {@link com.skyeye.common.enumeration.FlowableChildStateEnum}
     * @param tenantId           租户id
     * @return 请假记录列表
     */
    List<LeaveTimeSlot> queryLastMonthLeaveTime(String staffId, String lastMonthDate,
                                                String leaveTimeState, String leaveTimeSlotState, String tenantId);

    /**
     * 获取指定日期已经审核通过的请假时间段信息
     *
     * @param timeId        班次id
     * @param createId      创建人/申请人id
     * @param leaveStartDay 指定日期，格式：yyyy-MM-dd
     * @param tenantId      租户id
     * @return 请假时间段列表
     */
    List<LeaveTimeSlot> queryCheckWorkLeaveSlotByMation(String timeId, String createId, String leaveStartDay, String tenantId);

    /**
     * 指定自然日是否存在审核通过的请假时间段（不限班次）
     */
    List<LeaveTimeSlot> queryApprovedLeaveSlotByUserAndDay(String createId, String day, String tenantId);

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的请假时间段
     *
     * @param userId   用户id
     * @param timeId   班次id
     * @param months   指定月份，月格式（yyyy-MM）
     * @param tenantId 租户id
     * @return 请假时间段列表
     */
    List<LeaveTimeSlot> queryStateIsSuccessLeaveDayByUserIdAndMonths(String userId, String timeId, List<String> months, String tenantId);
}
