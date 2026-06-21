/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.leave.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.leave.dao.LeaveTimeSlotDao;
import com.skyeye.leave.entity.Leave;
import com.skyeye.leave.entity.LeaveTimeSlot;
import com.skyeye.leave.service.LeaveTimeSlotService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: LeaveTimeSlotServiceImpl
 * @Description: 请假申请请假时间段服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/5 8:42
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "请假申请请假时间段", groupName = "请假申请", manageShow = false)
public class LeaveTimeSlotServiceImpl extends SkyeyeLinkDataServiceImpl<LeaveTimeSlotDao, LeaveTimeSlot> implements LeaveTimeSlotService {

    @Override
    public void editStateById(String id, String state, Integer useYearHoliday) {
        UpdateWrapper<LeaveTimeSlot> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(LeaveTimeSlot::getState), state);
        updateWrapper.set(MybatisPlusUtil.toColumns(LeaveTimeSlot::getUseYearHoliday), useYearHoliday);
        update(updateWrapper);
    }

    @Override
    public Map<String, List<LeaveTimeSlot>> queryLeaveTimeSlotListByLeaveIds(List<String> leaveIds) {
        if (CollectionUtil.isEmpty(leaveIds)) {
            return new HashMap<>();
        }
        QueryWrapper<LeaveTimeSlot> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(LeaveTimeSlot::getParentId), leaveIds);
        Map<String, List<LeaveTimeSlot>> listMap = list(queryWrapper).stream()
            .collect(Collectors.groupingBy(LeaveTimeSlot::getParentId));
        return listMap;
    }

    @Override
    public List<LeaveTimeSlot> queryTimeAndIds(List<String> leaveIds, String startTime, String endTime) {
        QueryWrapper<LeaveTimeSlot> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(LeaveTimeSlot::getParentId), leaveIds);
        queryWrapper.le(MybatisPlusUtil.toColumns(LeaveTimeSlot::getLeaveStartTime), endTime + " 23:59:59");
        queryWrapper.ge(MybatisPlusUtil.toColumns(LeaveTimeSlot::getLeaveEndTime), startTime + " 00:00:00");
        return list(queryWrapper);
    }

    @Override
    @IgnoreTenant
    public List<LeaveTimeSlot> queryLastMonthLeaveTime(String staffId, String lastMonthDate,
                                                       String leaveTimeState, String leaveTimeSlotState, String tenantId) {
        MPJLambdaWrapper<LeaveTimeSlot> wrapper = JoinWrappers.lambda("b", LeaveTimeSlot.class)
            .innerJoin(Leave.class, "a", Leave::getId, LeaveTimeSlot::getParentId)
            .eq("a." + MybatisPlusUtil.toColumns(Leave::getCreateId), staffId)
            .eq("a." + MybatisPlusUtil.toColumns(Leave::getState), leaveTimeState)
            .eq("b." + MybatisPlusUtil.toColumns(LeaveTimeSlot::getState), leaveTimeSlotState)
            .apply("(b.leave_start_time <= LAST_DAY(CONCAT({0}, '-01')) AND b.leave_end_time >= CONCAT({0}, '-01'))", lastMonthDate);
        if (StrUtil.isNotEmpty(tenantId)) {
            wrapper.eq("a." + CommonConstants.TENANT_ID_FIELD, tenantId);
            wrapper.eq("b." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        return skyeyeBaseMapper.selectJoinList(LeaveTimeSlot.class, wrapper);
    }

    @Override
    @IgnoreTenant
    public List<LeaveTimeSlot> queryCheckWorkLeaveSlotByMation(String timeId, String createId, String leaveStartDay, String tenantId) {
        MPJLambdaWrapper<LeaveTimeSlot> wrapper = JoinWrappers.lambda("b", LeaveTimeSlot.class)
            .innerJoin(Leave.class, "a", Leave::getId, LeaveTimeSlot::getParentId)
            .eq("a." + MybatisPlusUtil.toColumns(Leave::getCreateId), createId)
            .eq("b." + MybatisPlusUtil.toColumns(LeaveTimeSlot::getState), FlowableChildStateEnum.ADEQUATE.getKey())
            .le("b.leave_start_time", leaveStartDay + " 23:59:59")
            .ge("b.leave_end_time", leaveStartDay + " 00:00:00")
            .eq("b." + MybatisPlusUtil.toColumns(LeaveTimeSlot::getTimeId), timeId);
        if (StrUtil.isNotEmpty(tenantId)) {
            wrapper.eq("a." + CommonConstants.TENANT_ID_FIELD, tenantId);
            wrapper.eq("b." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        return skyeyeBaseMapper.selectJoinList(LeaveTimeSlot.class, wrapper);
    }

    /**
     * 查询用户在某自然日已审批通过的请假时段（不限 timeId，排班缺勤结算用）
     */
    @Override
    @IgnoreTenant
    public List<LeaveTimeSlot> queryApprovedLeaveSlotByUserAndDay(String createId, String day, String tenantId) {
        // leaveStart~leaveEnd 覆盖 day 即命中
        MPJLambdaWrapper<LeaveTimeSlot> wrapper = JoinWrappers.lambda("b", LeaveTimeSlot.class)
            .innerJoin(Leave.class, "a", Leave::getId, LeaveTimeSlot::getParentId)
            .eq("a." + MybatisPlusUtil.toColumns(Leave::getCreateId), createId)
            .eq("b." + MybatisPlusUtil.toColumns(LeaveTimeSlot::getState), FlowableChildStateEnum.ADEQUATE.getKey())
            .le("b.leave_start_time", day + " 23:59:59")
            .ge("b.leave_end_time", day + " 00:00:00");
        if (StrUtil.isNotEmpty(tenantId)) {
            wrapper.eq("a." + CommonConstants.TENANT_ID_FIELD, tenantId);
            wrapper.eq("b." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        return skyeyeBaseMapper.selectJoinList(LeaveTimeSlot.class, wrapper);
    }

    @Override
    @IgnoreTenant
    public List<LeaveTimeSlot> queryStateIsSuccessLeaveDayByUserIdAndMonths(String userId, String timeId, List<String> months, String tenantId) {
        MPJLambdaWrapper<LeaveTimeSlot> wrapper = JoinWrappers.lambda("a", LeaveTimeSlot.class)
            .innerJoin(Leave.class, "b", Leave::getId, LeaveTimeSlot::getParentId)
            .eq("b." + MybatisPlusUtil.toColumns(Leave::getCreateId), userId)
            .eq("a." + MybatisPlusUtil.toColumns(LeaveTimeSlot::getState), FlowableChildStateEnum.ADEQUATE.getKey())
            .eq("a." + MybatisPlusUtil.toColumns(LeaveTimeSlot::getTimeId), timeId);
        if (CollectionUtil.isNotEmpty(months)) {
            String conds = months.stream()
                .map(m -> "(a.leave_start_time <= LAST_DAY('" + m + "-01') AND a.leave_end_time >= '" + m + "-01')")
                .collect(Collectors.joining(" OR "));
            wrapper.apply("(" + conds + ")");
        }
        if (StrUtil.isNotEmpty(tenantId)) {
            wrapper.eq("a." + CommonConstants.TENANT_ID_FIELD, tenantId);
            wrapper.eq("b." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        return skyeyeBaseMapper.selectJoinList(LeaveTimeSlot.class, wrapper);
    }
}
