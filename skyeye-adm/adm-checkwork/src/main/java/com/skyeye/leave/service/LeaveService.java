/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.leave.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.leave.entity.Leave;
import com.skyeye.leave.entity.LeaveTimeSlot;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: LeaveService
 * @Description: 请假申请服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/4 13:17
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface LeaveService extends SkyeyeBusinessService<Leave> {

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的请假申请数据
     *
     * @param userId 用户id
     * @param timeId 班次id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     */
    List<Map<String, Object>> queryStateIsSuccessLeaveDayByUserIdAndMonths(String userId, String timeId, List<String> months);

    Map<String, List<LeaveTimeSlot>> queryStateIsSuccessLeaveDayByUserId(String startTime, String endTime, List<Map<String, Object>> staffListWithUserId);

    LeaveTimeSlot queryCheckWorkLeaveByMation(String timeId, String createId, String leaveStartDay);

    /**
     * 指定自然日是否存在审核通过的请假（不限班次，供排班定时任务使用）
     */
    boolean hasApprovedLeaveOnDay(String createId, String day);

    void getLeaveTypeList(InputObject inputObject, OutputObject outputObject);

    List<Leave> queryLeaveByFormalUserIds(List<String> formalUserIds);
}
