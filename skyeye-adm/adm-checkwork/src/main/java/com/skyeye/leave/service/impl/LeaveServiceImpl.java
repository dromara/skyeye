/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.leave.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cancleleave.entity.CancelLeave;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.*;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.centerrest.entity.checkwork.UserStaffHolidayRest;
import com.skyeye.eve.centerrest.user.SysEveUserService;
import com.skyeye.eve.service.ISystemFoundationSettingsService;
import com.skyeye.exception.CustomException;
import com.skyeye.leave.classenum.UseYearHolidayType;
import com.skyeye.leave.dao.LeaveDao;
import com.skyeye.leave.entity.Leave;
import com.skyeye.leave.entity.LeaveTimeSlot;
import com.skyeye.leave.service.LeaveService;
import com.skyeye.leave.service.LeaveTimeSlotService;
import com.skyeye.worktime.entity.CheckWorkTime;
import com.skyeye.worktime.util.CheckWorkHourCalcUtil;
import com.skyeye.worktime.service.CheckWorkTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: LeaveServiceImpl
 * @Description: 请假申请服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/14 13:04
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "请假申请", groupName = "请假申请", flowable = true)
public class LeaveServiceImpl extends SkyeyeBusinessServiceImpl<LeaveDao, Leave> implements LeaveService {

    @Autowired
    private LeaveTimeSlotService leaveTimeSlotService;

    @Autowired
    private CheckWorkTimeService checkWorkTimeService;

    @Autowired
    private SysEveUserService sysEveUserService;

    @Autowired
    private ISystemFoundationSettingsService iSystemFoundationSettingsService;

    @Override
    protected QueryWrapper<Leave> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Leave> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CancelLeave::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public void validatorEntity(Leave entity) {
        chectOrderItem(entity.getLeaveTimeSlotList());
    }

    @Override
    public void writePostpose(Leave entity, String userId) {
        // 保存前按服务端口径重算 leaveHour，防止前端篡改或旧客户端提交错误值
        List<String> timeIds = entity.getLeaveTimeSlotList().stream().map(LeaveTimeSlot::getTimeId).distinct().collect(Collectors.toList());
        Map<String, CheckWorkTime> checkWorkTimeMap = CollectionUtil.isEmpty(timeIds) ? new HashMap<>() : checkWorkTimeService.selectMapByIds(timeIds);
        for (LeaveTimeSlot slot : entity.getLeaveTimeSlotList()) {
            CheckWorkTime wt = checkWorkTimeMap.get(slot.getTimeId());
            if (StrUtil.isNotEmpty(slot.getLeaveStartTime()) && StrUtil.isNotEmpty(slot.getLeaveEndTime()) && wt != null) {
                LocalDateTime start = DateUtil.parseLeaveDateTime(slot.getLeaveStartTime());
                LocalDateTime end = DateUtil.parseLeaveDateTime(slot.getLeaveEndTime());
                if (start != null && end != null) {
                    long mins = CheckWorkHourCalcUtil.calcLeaveMinutesInRange(start, end, wt);
                    slot.setLeaveHour(CalculationUtil.divide(String.valueOf(mins), "60", CommonNumConstants.NUM_TWO));
                }
            }
        }
        leaveTimeSlotService.saveLinkList(entity.getId(), entity.getLeaveTimeSlotList());
        super.writePostpose(entity, userId);
    }

    private void chectOrderItem(List<LeaveTimeSlot> leaveTimeSlots) {
        for (LeaveTimeSlot slot : leaveTimeSlots) {
            if (StrUtil.isEmpty(slot.getLeaveStartTime()) || StrUtil.isEmpty(slot.getLeaveEndTime())) {
                throw new CustomException("请假开始、结束时间不能为空");
            }
            LocalDateTime start = DateUtil.parseLeaveDateTime(slot.getLeaveStartTime());
            LocalDateTime end = DateUtil.parseLeaveDateTime(slot.getLeaveEndTime());
            if (start == null || end == null || !end.isAfter(start)) {
                throw new CustomException("请假结束时间必须晚于开始时间");
            }
        }
        for (int i = 0; i < leaveTimeSlots.size(); i++) {
            LeaveTimeSlot a = leaveTimeSlots.get(i);
            LocalDateTime aStart = DateUtil.parseLeaveDateTime(a.getLeaveStartTime());
            LocalDateTime aEnd = DateUtil.parseLeaveDateTime(a.getLeaveEndTime());
            for (int j = i + 1; j < leaveTimeSlots.size(); j++) {
                LeaveTimeSlot b = leaveTimeSlots.get(j);
                if (!a.getTimeId().equals(b.getTimeId())) {
                    continue;
                }
                LocalDateTime bStart = DateUtil.parseLeaveDateTime(b.getLeaveStartTime());
                LocalDateTime bEnd = DateUtil.parseLeaveDateTime(b.getLeaveEndTime());
                if (aStart != null && aEnd != null && bStart != null && bEnd != null
                    && !aEnd.isBefore(bStart) && !aStart.isAfter(bEnd)) {
                    throw new CustomException("同一班次中不允许出现时间重叠的请假时间段");
                }
            }
        }
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        leaveTimeSlotService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public Leave getDataFromDb(String id) {
        Leave leave = super.getDataFromDb(id);
        List<LeaveTimeSlot> leaveTimeSlotList = leaveTimeSlotService.selectByPId(leave.getId());
        leave.setLeaveTimeSlotList(leaveTimeSlotList);
        return leave;
    }

    @Override
    public Leave selectById(String id) {
        Leave leave = super.selectById(id);
        // 获取考勤班次信息
        List<String> timeIds = leave.getLeaveTimeSlotList().stream()
            .map(LeaveTimeSlot::getTimeId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(timeIds)) {
            // 请假类型
            List<Map<String, Object>> result = getLeaveTypeList();
            Map<String, Map<String, Object>> leaveTypeMap = result.stream()
                .collect(Collectors.toMap(bean -> bean.get("id").toString(), item -> item));
            // 考勤班次信息
            Map<String, CheckWorkTime> checkWorkTimeMap = checkWorkTimeService.selectMapByIds(timeIds);
            leave.getLeaveTimeSlotList().forEach(leaveTimeSlot -> {
                leaveTimeSlot.setStateName(FlowableChildStateEnum.getStateName(leaveTimeSlot.getState()));
                leaveTimeSlot.setUseYearHolidayName(UseYearHolidayType.getShowName(leaveTimeSlot.getUseYearHoliday()));
                leaveTimeSlot.setTimeMation(checkWorkTimeMap.get(leaveTimeSlot.getTimeId()));
                leaveTimeSlot.setLeaveTypeMation(leaveTypeMap.get(leaveTimeSlot.getLeaveType()));
            });
        }

        leave.setStateName(FlowableStateEnum.getStateName(leave.getState()));
        iAuthUserService.setName(leave, "createId", "createName");
        return leave;
    }

    @Override
    public void revokePostpose(Leave entity) {
        super.revokePostpose(entity);
        leaveTimeSlotService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    protected void approvalEndIsSuccess(Leave entity) {
        calcUserStaffYearMation(entity.getId(), entity.getCreateId());
    }

    @Override
    protected void approvalEndIsFailed(Leave entity) {
        leaveTimeSlotService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

    /**
     * 计算请假中关联的年假信息，更新员工年假
     *
     * @param leaveId  请假信息id
     * @param createId 创建人id
     */
    private void calcUserStaffYearMation(String leaveId, String createId) {
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
        // 获取请假扣薪制度
        List<Map<String, Object>> holidaysTypeMation = getSystemHolidaysTypeJsonMation();
        // 获取请假天数信息
        List<LeaveTimeSlot> leaveTimeSlotList = leaveTimeSlotService.selectByPId(leaveId);
        List<String> timeIds = leaveTimeSlotList.stream().map(LeaveTimeSlot::getTimeId).distinct().collect(Collectors.toList());
        Map<String, CheckWorkTime> checkWorkTimeMap = CollectionUtil.isEmpty(timeIds) ? new HashMap<>() : checkWorkTimeService.selectMapByIds(timeIds);
        for (LeaveTimeSlot day : leaveTimeSlotList) {
            day.setTimeMation(checkWorkTimeMap.get(day.getTimeId()));
            String leaveSoltId = day.getId();
            // 请假时长（从 leaveStartTime~leaveEndTime 与工作时间的交集计算）
            String leaveHour = getTotalLeaveHours(day, day.getTimeMation());
            // 假期类型
            String leaveType = day.getLeaveType();
            Map<String, Object> holiday = holidaysTypeMation.stream()
                .filter(bean -> leaveType.equals(bean.get("holidayNo").toString())).findFirst().orElse(null);
            if (CollectionUtil.isNotEmpty(holiday)) {
                // 是否使用年假
                Integer whetherYearHour = Integer.parseInt(holiday.get("whetherYearHour").toString());
                // 是否使用补休
                Integer whetherComLeave = Integer.parseInt(holiday.get("whetherComLeave").toString());
                if (whetherYearHour.equals(WhetherEnum.ENABLE_USING.getKey())) {
                    if (annualLeave.equals(CalculationUtil.getMax(annualLeave, leaveHour, CommonNumConstants.NUM_TWO))) {
                        // 剩余年假够用
                        annualLeave = CalculationUtil.subtract(annualLeave, leaveHour, CommonNumConstants.NUM_TWO);
                        leaveTimeSlotService.editStateById(leaveSoltId, FlowableChildStateEnum.ADEQUATE.getKey(), UseYearHolidayType.USE_ANNUAL_LEAVE.getKey());
                    } else {
                        // 剩余年假不够用，则默认审核不通过
                        leaveTimeSlotService.editStateById(leaveSoltId, FlowableChildStateEnum.INSUFFICIENT.getKey());
                    }
                    continue;
                }
                if (whetherComLeave.equals(WhetherEnum.ENABLE_USING.getKey())) {
                    if (holidayNumber.equals(CalculationUtil.getMax(holidayNumber, leaveHour, CommonNumConstants.NUM_TWO))) {
                        // 剩余补休够用
                        holidayNumber = CalculationUtil.subtract(holidayNumber, leaveHour, CommonNumConstants.NUM_TWO);
                        retiredHolidayNumber = CalculationUtil.add(retiredHolidayNumber, leaveHour, CommonNumConstants.NUM_TWO);
                        leaveTimeSlotService.editStateById(leaveSoltId, FlowableChildStateEnum.ADEQUATE.getKey(), UseYearHolidayType.USE_COMPENSATORY_LEAVE.getKey());
                    } else {
                        // 剩余补休不够用，则默认审核不通过
                        leaveTimeSlotService.editStateById(leaveSoltId, FlowableChildStateEnum.INSUFFICIENT.getKey());
                    }
                    continue;
                }
                leaveTimeSlotService.editStateById(leaveSoltId, FlowableChildStateEnum.ADEQUATE.getKey());
            } else {
                leaveTimeSlotService.editStateById(leaveSoltId, FlowableChildStateEnum.ADEQUATE.getKey());
            }
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

    /**
     * 获取请假时间段的总工时（leaveStartTime~leaveEndTime 与工作时间的交集，支持跨天）
     */
    private String getTotalLeaveHours(LeaveTimeSlot slot, CheckWorkTime workTime) {
        try {
            if (StrUtil.isEmpty(slot.getLeaveStartTime()) || StrUtil.isEmpty(slot.getLeaveEndTime()) || workTime == null) {
                return StrUtil.isNotEmpty(slot.getLeaveHour()) ? slot.getLeaveHour() : "0";
            }
            LocalDateTime start = DateUtil.parseLeaveDateTime(slot.getLeaveStartTime());
            LocalDateTime end = DateUtil.parseLeaveDateTime(slot.getLeaveEndTime());
            if (start == null || end == null) {
                return StrUtil.isNotEmpty(slot.getLeaveHour()) ? slot.getLeaveHour() : "0";
            }
            return CheckWorkHourCalcUtil.calcLeaveHour(start, end, workTime);
        } catch (Exception e) {
            return StrUtil.isNotEmpty(slot.getLeaveHour()) ? slot.getLeaveHour() : "0";
        }
    }

    /**
     * 获取请假扣薪制度
     *
     * @return 请假扣薪制度
     */
    private List<Map<String, Object>> getSystemHolidaysTypeJsonMation() {
        Map<String, Object> sysSetting = iSystemFoundationSettingsService.querySystemFoundationSettingsList();
        String holidaysTypeJson = sysSetting.get("holidaysTypeJson").toString();
        return JSONUtil.toList(holidaysTypeJson, null);
    }

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的请假申请数据
     *
     * @param userId 用户id
     * @param timeId 班次id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     */
    @Override
    public List<Map<String, Object>> queryStateIsSuccessLeaveDayByUserIdAndMonths(String userId, String timeId, List<String> months) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<LeaveTimeSlot> slotList = leaveTimeSlotService.queryStateIsSuccessLeaveDayByUserIdAndMonths(userId, timeId, months, tenantId);
        List<Map<String, Object>> beans = slotList.stream().map(slot -> {
            Map<String, Object> bean = new HashMap<>();
            bean.put("id", slot.getId());
            bean.put("start", StrUtil.isNotEmpty(slot.getLeaveStartTime()) ? slot.getLeaveStartTime() : "");
            bean.put("end", StrUtil.isNotEmpty(slot.getLeaveEndTime()) ? slot.getLeaveEndTime() : "");
            bean.put("title", CheckDayType.DAY_IS_BUSINESS_TRAVEL.getValue());
            bean.put("type", CheckDayType.DAY_IS_LEAVE.getKey());
            bean.put("className", CheckDayType.DAY_IS_LEAVE.getClassName());
            bean.put("allDay", "1");
            bean.put("showBg", "2");
            bean.put("editable", false);
            return bean;
        }).collect(Collectors.toList());
        return beans;
    }

    @Override
    public Map<String, List<LeaveTimeSlot>> queryStateIsSuccessLeaveDayByUserId(String startTime, String endTime, List<Map<String, Object>> staffListWithUserId) {
        // 获取正式员工的请假信息
        List<String> allIds = staffListWithUserId.stream().map(map -> map.get("userId").toString()).collect(Collectors.toList());
        List<Leave> leaves = queryAllLeaveListByStaffId(allIds);
        // 所有员工的请假表Id
        List<String> leaveIds = leaves.stream().map(Leave::getId).collect(Collectors.toList());
        // 根据请假的主键Id拿到对应的请假时间
        Map<String, List<LeaveTimeSlot>> leaveTimeSlots = leaveTimeSlotService.queryLeaveTimeSlotListByLeaveIds(leaveIds);
        // 获取所有请假的createId，并建立id到createId的映射
        Map<String, String> leaveIdToCreateIdMap = leaves.stream().collect(Collectors.toMap(Leave::getId, Leave::getCreateId));
        // 根据leaveId到createId的映射，重新组织leaveTimeSlots
        Map<String, List<LeaveTimeSlot>> leaveTimeSlotsByCreateId = leaveTimeSlots.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> leaveIdToCreateIdMap.get(entry.getKey()),
                Map.Entry::getValue,
                (existing, replacement) -> existing
            ));
        return leaveTimeSlotsByCreateId;
    }

    private List<Leave> queryAllLeaveListByStaffId(List<String> allIds) {
        if (CollectionUtil.isEmpty(allIds)) {
            return new ArrayList<>();
        }
        QueryWrapper<Leave> leaveWrapper = new QueryWrapper<>();
        leaveWrapper.in(MybatisPlusUtil.toColumns(Leave::getCreateId), allIds);
        List<Leave> leaveList = list(leaveWrapper);
        return leaveList;
    }

    /**
     * 获取指定日期已经审核通过的请假信息
     *
     * @param timeId        班次id
     * @param createId      申请人id
     * @param leaveStartDay 申请日期
     * @return
     */
    @Override
    public LeaveTimeSlot queryCheckWorkLeaveByMation(String timeId, String createId, String leaveStartDay) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 获取请假日期信息
        List<LeaveTimeSlot> list = leaveTimeSlotService.queryCheckWorkLeaveSlotByMation(timeId, createId, leaveStartDay, tenantId);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        LeaveTimeSlot slot = list.get(0);
        return slot;
    }

    /**
     * 指定自然日是否存在审核通过的请假（不限班次）
     */
    @Override
    public boolean hasApprovedLeaveOnDay(String createId, String day) {
        // 排班定时任务用：不限 timeId，只要当日有审核通过的请假即视为免打卡
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        return CollectionUtil.isNotEmpty(leaveTimeSlotService.queryApprovedLeaveSlotByUserAndDay(createId, day, tenantId));
    }

    /**
     * 获取基础设置中的请假类型
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void getLeaveTypeList(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> result = getLeaveTypeList();
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public List<Leave> queryLeaveByFormalUserIds(List<String> formalUserIds) {
        if (CollectionUtil.isEmpty(formalUserIds)) {
            return new ArrayList<>();
        }
        QueryWrapper<Leave> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Leave::getCreateId), formalUserIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Leave::getState), FlowableStateEnum.PASS.getKey());
        return list(queryWrapper);
    }

    private List<Map<String, Object>> getLeaveTypeList() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> holidaysTypeMation = getSystemHolidaysTypeJsonMation();
        holidaysTypeMation.forEach(holidaysType -> {
            Map<String, Object> bean = new HashMap<>();
            bean.put("id", holidaysType.get("holidayNo"));
            bean.put("name", holidaysType.get("holidayName"));
            result.add(bean);
        });
        return result;
    }

}
