/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.trip.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.CheckDayType;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.trip.dao.BusinessTripDao;
import com.skyeye.trip.entity.BusinessTrip;
import com.skyeye.trip.entity.BusinessTripTimeSlot;
import com.skyeye.trip.service.BusinessTripService;
import com.skyeye.trip.service.BusinessTripTimeSlotService;
import com.skyeye.worktime.entity.CheckWorkTime;
import com.skyeye.worktime.service.CheckWorkTimeService;
import com.skyeye.worktime.util.CheckWorkHourCalcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CheckWorkBusinessTripServiceImpl
 * @Description: 出差申请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/6 22:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "出差申请", groupName = "出差申请", flowable = true)
public class BusinessTripServiceImpl extends SkyeyeBusinessServiceImpl<BusinessTripDao, BusinessTrip> implements BusinessTripService {

    @Autowired
    private BusinessTripTimeSlotService businessTripTimeSlotService;

    @Autowired
    private CheckWorkTimeService checkWorkTimeService;

    @Override
    protected QueryWrapper<BusinessTrip> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<BusinessTrip> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(BusinessTrip::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public void validatorEntity(BusinessTrip entity) {
        checkOrderItem(entity.getTripTimeSlotList());
    }

    @Override
    public void writePostpose(BusinessTrip entity, String userId) {
        // 保存前服务端重算 travelHour（支持跨天整班/部分时段）
        recalcTripTimeSlotHours(entity.getTripTimeSlotList());
        businessTripTimeSlotService.saveLinkList(entity.getId(), entity.getTripTimeSlotList());
        super.writePostpose(entity, userId);
    }

    /**
     * 按班次配置重算各出差明细的 travelHour
     */
    private void recalcTripTimeSlotHours(List<BusinessTripTimeSlot> tripTimeSlotList) {
        if (CollectionUtil.isEmpty(tripTimeSlotList)) {
            return;
        }
        List<String> timeIds = tripTimeSlotList.stream().map(BusinessTripTimeSlot::getTimeId).distinct().collect(Collectors.toList());
        Map<String, CheckWorkTime> checkWorkTimeMap = checkWorkTimeService.selectMapByIds(timeIds);
        for (BusinessTripTimeSlot slot : tripTimeSlotList) {
            CheckWorkTime workTime = checkWorkTimeMap.get(slot.getTimeId());
            if (workTime != null && StrUtil.isNotEmpty(slot.getTravelDay())
                && StrUtil.isNotEmpty(slot.getStartTime()) && StrUtil.isNotEmpty(slot.getEndTime())) {
                slot.setTravelHour(CheckWorkHourCalcUtil.calcTravelHour(
                    slot.getTravelDay(), slot.getStartTime(), slot.getEndTime(), workTime));
            }
        }
    }

    private void checkOrderItem(List<BusinessTripTimeSlot> businessTripTimeSlots) {
        List<String> travelDay = businessTripTimeSlots.stream()
            .map(bean -> String.format(Locale.ROOT, "%s-%s", bean.getTimeId(), bean.getTravelDay())).distinct()
            .collect(Collectors.toList());
        if (businessTripTimeSlots.size() != travelDay.size()) {
            throw new CustomException("同一班次中不允许出现相同的出差日期");
        }
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        businessTripTimeSlotService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public BusinessTrip getDataFromDb(String id) {
        BusinessTrip businessTrip = super.getDataFromDb(id);
        List<BusinessTripTimeSlot> tripTimeSlotList = businessTripTimeSlotService.selectByPId(businessTrip.getId());
        businessTrip.setTripTimeSlotList(tripTimeSlotList);
        return businessTrip;
    }

    @Override
    public BusinessTrip selectById(String id) {
        BusinessTrip businessTrip = super.selectById(id);
        // 获取考勤班次信息
        List<String> timeIds = businessTrip.getTripTimeSlotList().stream()
            .map(BusinessTripTimeSlot::getTimeId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(timeIds)) {
            Map<String, CheckWorkTime> checkWorkTimeMap = checkWorkTimeService.selectMapByIds(timeIds);
            businessTrip.getTripTimeSlotList().forEach(businessTripTimeSlot -> {
                businessTripTimeSlot.setTimeMation(checkWorkTimeMap.get(businessTripTimeSlot.getTimeId()));
                businessTripTimeSlot.setStateName(FlowableChildStateEnum.getStateName(businessTripTimeSlot.getState()));
            });
        }
        businessTrip.setStateName(FlowableStateEnum.getStateName(businessTrip.getState()));
        iAuthUserService.setName(businessTrip, "createId", "createName");
        return businessTrip;
    }

    @Override
    public void revokePostpose(BusinessTrip entity) {
        super.revokePostpose(entity);
        businessTripTimeSlotService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    protected void approvalEndIsSuccess(BusinessTrip entity) {
        businessTripTimeSlotService.editStateByPId(entity.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
    }

    @Override
    protected void approvalEndIsFailed(BusinessTrip entity) {
        businessTripTimeSlotService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的出差申请数据
     *
     * @param userId 用户id
     * @param timeId 班次id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     */
    @Override
    public List<Map<String, Object>> queryStateIsSuccessBusinessTripDayByUserIdAndMonths(String userId, String timeId, List<String> months) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryStateIsSuccessBusinessTripDay(userId, timeId, months,
            FlowableChildStateEnum.ADEQUATE.getKey(), tenantId);
        // 获取考勤班次信息
        List<String> timeIds = beans.stream()
            .map(bean -> bean.get("timeId").toString()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(timeIds)) {
            beans.forEach(bean -> {
                bean.put("title", CheckDayType.DAY_IS_BUSINESS_TRAVEL.getValue());
                bean.put("type", CheckDayType.DAY_IS_BUSINESS_TRAVEL.getKey());
                bean.put("className", CheckDayType.DAY_IS_BUSINESS_TRAVEL.getClassName());
                bean.put("allDay", "1");
                bean.put("showBg", "2");
                bean.put("editable", false);
            });
            return beans;
        }
        return new ArrayList<>();
    }

    @Override
    public void queryYesDoTime(String employeeId) {
        QueryWrapper<BusinessTrip> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(BusinessTrip::getCreateId), employeeId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(BusinessTrip::getState), FlowableChildStateEnum.ADEQUATE.getKey());

    }

    @Override
    public Map<String, List<BusinessTripTimeSlot>> queryStateIsSuccessBusinessTripDayByUserId(String startTime, String endTime, List<Map<String, Object>> staffListWithUserId) {
        // 获取正式员工信息
        List<String> allIds = staffListWithUserId.stream().map(map -> map.get("userId").toString()).collect(Collectors.toList());
        // 所有员工的出差信息
        List<BusinessTrip> businessTrips = queryAllBusinessTripListByStaffId(allIds);
        // 所有员工的出差表Id
        List<String> allBusinessTripIds = businessTrips.stream().map(BusinessTrip::getId).collect(Collectors.toList());
        // 根据出差的主键Id拿到对应的出差时间
        Map<String, List<BusinessTripTimeSlot>> businessTripTimeSlots = businessTripTimeSlotService.queryBusinessTripTimeSlotListByBusinessTripIds(allBusinessTripIds);
        Map<String, String> businessTripTimeSlotMap = businessTrips.stream().collect(Collectors.toMap(BusinessTrip::getId, BusinessTrip::getCreateId));
        Map<String, List<BusinessTripTimeSlot>> stringListMap = businessTripTimeSlots.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> businessTripTimeSlotMap.get(entry.getKey()),
                Map.Entry::getValue,
                (existing, replacement) -> existing
            ));
        return stringListMap;
    }

    @Override
    public List<BusinessTrip> queryBusinessTripByUserIds(List<String> formalUserIds) {
        QueryWrapper<BusinessTrip> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(BusinessTrip::getCreateId), formalUserIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(BusinessTrip::getState), FlowableStateEnum.PASS.getKey());
        return list(queryWrapper);
    }

    private List<BusinessTrip> queryAllBusinessTripListByStaffId(List<String> allIds) {
        if (CollectionUtil.isEmpty(allIds)) {
            return new ArrayList<>();
        }
        QueryWrapper<BusinessTrip> businessTripWrapper = new QueryWrapper<>();
        businessTripWrapper.in(MybatisPlusUtil.toColumns(BusinessTrip::getCreateId), allIds);
        List<BusinessTrip> businessTripList = list(businessTripWrapper);
        return businessTripList;
    }
}
