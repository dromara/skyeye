/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.cancleleave.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cancleleave.dao.CancelLeaveDao;
import com.skyeye.cancleleave.entity.CancelLeave;
import com.skyeye.cancleleave.entity.CancelLeaveTimeSlot;
import com.skyeye.cancleleave.service.CancelLeaveService;
import com.skyeye.cancleleave.service.CancelLeaveTimeSlotService;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.centerrest.entity.checkwork.UserStaffHolidayRest;
import com.skyeye.eve.centerrest.user.SysEveUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.leave.classenum.UseYearHolidayType;
import com.skyeye.leave.entity.LeaveTimeSlot;
import com.skyeye.leave.service.LeaveService;
import com.skyeye.worktime.entity.CheckWorkTime;
import com.skyeye.worktime.service.CheckWorkTimeService;
import com.skyeye.worktime.util.CheckWorkHourCalcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CheckWorkCancelLeaveServiceImpl
 * @Description: 销假申请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/11 9:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "销假申请", groupName = "销假申请", flowable = true)
public class CancelLeaveServiceImpl extends SkyeyeBusinessServiceImpl<CancelLeaveDao, CancelLeave> implements CancelLeaveService {

    @Autowired
    private CheckWorkTimeService checkWorkTimeService;

    @Autowired
    private CancelLeaveTimeSlotService cancelLeaveTimeSlotService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private SysEveUserService sysEveUserService;

    @Override
    protected QueryWrapper<CancelLeave> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<CancelLeave> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CancelLeave::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public void validatorEntity(CancelLeave entity) {
        checkOrderItem(entity.getCancelLeaveTimeSlotList());
    }

    @Override
    public void writePostpose(CancelLeave entity, String userId) {
        // 保存前服务端重算 cancelHour（与请假算法一致，支持跨天）
        recalcCancelLeaveTimeSlotHours(entity.getCancelLeaveTimeSlotList());
        cancelLeaveTimeSlotService.saveLinkList(entity.getId(), entity.getCancelLeaveTimeSlotList());
        super.writePostpose(entity, userId);
    }

    /**
     * 按 cancelDay + 销假时段重算 cancelHour
     */
    private void recalcCancelLeaveTimeSlotHours(List<CancelLeaveTimeSlot> cancelLeaveTimeSlots) {
        if (CollectionUtil.isEmpty(cancelLeaveTimeSlots)) {
            return;
        }
        List<String> timeIds = cancelLeaveTimeSlots.stream().map(CancelLeaveTimeSlot::getTimeId).distinct().collect(Collectors.toList());
        Map<String, CheckWorkTime> checkWorkTimeMap = checkWorkTimeService.selectMapByIds(timeIds);
        for (CancelLeaveTimeSlot slot : cancelLeaveTimeSlots) {
            CheckWorkTime workTime = checkWorkTimeMap.get(slot.getTimeId());
            if (workTime != null && StrUtil.isNotEmpty(slot.getCancelDay())
                && StrUtil.isNotEmpty(slot.getCancelStartTime()) && StrUtil.isNotEmpty(slot.getCancelEndTime())) {
                slot.setCancelHour(CheckWorkHourCalcUtil.calcCancelHour(
                    slot.getCancelDay(), slot.getCancelStartTime(), slot.getCancelEndTime(), workTime));
            }
        }
    }

    private void checkOrderItem(List<CancelLeaveTimeSlot> cancelLeaveTimeSlots) {
        List<String> cancelDay = cancelLeaveTimeSlots.stream()
            .map(bean -> String.format(Locale.ROOT, "%s-%s", bean.getTimeId(), bean.getCancelDay())).distinct()
            .collect(Collectors.toList());
        if (cancelLeaveTimeSlots.size() != cancelDay.size()) {
            throw new CustomException("同一班次中不允许出现相同的销假日期");
        }
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        cancelLeaveTimeSlotService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public CancelLeave getDataFromDb(String id) {
        CancelLeave cancelLeave = super.getDataFromDb(id);
        List<CancelLeaveTimeSlot> cancelLeaveTimeSlotList = cancelLeaveTimeSlotService.selectByPId(cancelLeave.getId());
        cancelLeave.setCancelLeaveTimeSlotList(cancelLeaveTimeSlotList);
        return cancelLeave;
    }

    @Override
    public CancelLeave selectById(String id) {
        CancelLeave cancelLeave = super.selectById(id);
        // 获取考勤班次信息
        List<String> timeIds = cancelLeave.getCancelLeaveTimeSlotList().stream()
            .map(CancelLeaveTimeSlot::getTimeId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(timeIds)) {
            Map<String, CheckWorkTime> checkWorkTimeMap = checkWorkTimeService.selectMapByIds(timeIds);
            cancelLeave.getCancelLeaveTimeSlotList().forEach(cancelLeaveTimeSlot -> {
                cancelLeaveTimeSlot.setTimeMation(checkWorkTimeMap.get(cancelLeaveTimeSlot.getTimeId()));
                cancelLeaveTimeSlot.setStateName(FlowableChildStateEnum.getStateName(cancelLeaveTimeSlot.getState()));
            });
        }
        cancelLeave.setStateName(FlowableStateEnum.getStateName(cancelLeave.getState()));
        iAuthUserService.setName(cancelLeave, "createId", "createName");
        return cancelLeave;
    }

    @Override
    public void revokePostpose(CancelLeave entity) {
        super.revokePostpose(entity);
        cancelLeaveTimeSlotService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    protected void approvalEndIsSuccess(CancelLeave entity) {
        calcUserStaffCancelLeaveMation(entity.getId(), entity.getCreateId());
    }

    @Override
    protected void approvalEndIsFailed(CancelLeave entity) {
        cancelLeaveTimeSlotService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

    /**
     * 计算销假中关联的年假信息，更新员工年假
     *
     * @param cancelLeaveId 销假信息id
     * @param createId      创建人id
     */
    private void calcUserStaffCancelLeaveMation(String cancelLeaveId, String createId) {
        // 用户信息
        Map<String, Object> user = iAuthUserService.queryDataMationById(createId);
        // 员工id
        String staffId = user.get("staffId").toString();
        // 员工当前剩余年假
        String annualLeave = user.get("annualLeave").toString();
        // 员工当前剩余补休
        String holidayNumber = user.get("holidayNumber").toString();
        // 员工当前已休补休
        String retiredHolidayNumber = user.get("retiredHolidayNumber").toString();
        // 获取销假天数信息
        List<CancelLeaveTimeSlot> cancelLeaveTimeSlotList = cancelLeaveTimeSlotService.selectByPId(cancelLeaveId);
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        for (CancelLeaveTimeSlot day : cancelLeaveTimeSlotList) {
            String cancelLeaveTimeId = day.getId();
            String cancelDay = day.getCancelDay();
            String timeId = day.getTimeId();
            String cancelHour = day.getCancelHour();
            CheckWorkTime workTime = checkWorkTimeService.selectById(timeId);
            // 审批通过时再次重算，确保年假/补休退还数量准确
            if (workTime != null && StrUtil.isNotEmpty(day.getCancelStartTime()) && StrUtil.isNotEmpty(day.getCancelEndTime())) {
                cancelHour = CheckWorkHourCalcUtil.calcCancelHour(
                    cancelDay, day.getCancelStartTime(), day.getCancelEndTime(), workTime);
            }
            // 判断该员工在这一天是否有销假成功的记录，如果有，则审核失败，如果没有，则继续操作
            Map<String, Object> mation = skyeyeBaseMapper.queryCheckWorkCancelLeaveByMation(createId, cancelDay, FlowableChildStateEnum.ADEQUATE.getKey(), tenantId);
            if (CollectionUtil.isEmpty(mation)) {
                // 判断该员工在这一天是否有请假记录，如果没有，则审核失败，如果有，则继续操作
                LeaveTimeSlot leaveDayMation = leaveService.queryCheckWorkLeaveByMation(timeId, createId, cancelDay);
                if (leaveDayMation != null) {
                    if (leaveDayMation.getUseYearHoliday().equals(UseYearHolidayType.USE_ANNUAL_LEAVE.getKey())) {
                        // 如果之前请假使用的是年假，则恢复年假
                        annualLeave = CalculationUtil.add(annualLeave, cancelHour, CommonNumConstants.NUM_TWO);
                    } else if (leaveDayMation.getUseYearHoliday().equals(UseYearHolidayType.USE_COMPENSATORY_LEAVE.getKey())) {
                        // 如果之前请假使用的是补休，则恢复补休
                        retiredHolidayNumber = CalculationUtil.subtract(retiredHolidayNumber, cancelHour, CommonNumConstants.NUM_TWO);
                        holidayNumber = CalculationUtil.add(holidayNumber, cancelHour, CommonNumConstants.NUM_TWO);
                    }
                    // 修改销假记录为成功
                    cancelLeaveTimeSlotService.editStateById(cancelLeaveTimeId, FlowableChildStateEnum.ADEQUATE.getKey());
                    continue;
                }
            }
            cancelLeaveTimeSlotService.editStateById(cancelLeaveTimeId, FlowableChildStateEnum.INSUFFICIENT.getKey());
        }
        // 修改员工剩余年假信息
        UserStaffHolidayRest sysUserStaffHoliday = new UserStaffHolidayRest();
        sysUserStaffHoliday.setStaffId(staffId);
        sysUserStaffHoliday.setQuarterYearHour(annualLeave);
        sysUserStaffHoliday.setAnnualLeaveStatisTime(DateUtil.getTimeAndToString());
        ExecuteFeignClient.get(() -> sysEveUserService.editSysUserStaffAnnualLeaveById(sysUserStaffHoliday)).getBean();
        // 修改员工剩余补休信息
        sysUserStaffHoliday.setHolidayNumber(holidayNumber);
        sysUserStaffHoliday.setHolidayStatisTime(DateUtil.getTimeAndToString());
        ExecuteFeignClient.get(() -> sysEveUserService.updateSysUserStaffHolidayNumberById(sysUserStaffHoliday)).getBean();
        // 修改员工已休补休信息
        sysUserStaffHoliday.setRetiredHolidayNumber(retiredHolidayNumber);
        sysUserStaffHoliday.setRetiredHolidayStatisTime(DateUtil.getTimeAndToString());
        ExecuteFeignClient.get(() -> sysEveUserService.updateSysUserStaffRetiredHolidayNumberById(sysUserStaffHoliday)).getBean();
    }

}
