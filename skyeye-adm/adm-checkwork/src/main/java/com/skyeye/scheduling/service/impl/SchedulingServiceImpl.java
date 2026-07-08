package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DataCommonUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.leave.entity.Leave;
import com.skyeye.leave.entity.LeaveTimeSlot;
import com.skyeye.leave.service.LeaveService;
import com.skyeye.leave.service.LeaveTimeSlotService;
import com.skyeye.rest.erp.farm.service.IFarmService;
import com.skyeye.rest.erp.farm.service.IFarmStationService;
import com.skyeye.scheduling.classenum.SchedulePublishState;
import com.skyeye.scheduling.dao.SchedulingDao;
import com.skyeye.scheduling.entity.*;
import com.skyeye.scheduling.service.*;
import com.skyeye.trip.entity.BusinessTrip;
import com.skyeye.trip.entity.BusinessTripTimeSlot;
import com.skyeye.trip.service.BusinessTripService;
import com.skyeye.trip.service.BusinessTripTimeSlotService;
import com.skyeye.worktime.util.CheckWorkTimePeriodUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "排班管理", groupName = "排班管理")
public class SchedulingServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingDao, Scheduling> implements SchedulingService {

    @Autowired
    private BusinessTripService businessTripService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private SchedulingTimeService schedulingTimeService;

    @Autowired
    private SchedulingShiftsTimeService schedulingShiftsTimeService;

    @Autowired
    private SchedulingShiftsTimeWorkService schedulingShiftsTimeWorkService;

    @Autowired
    private SchedulingLeaveService schedulingLeaveService;

    @Autowired
    private BusinessTripTimeSlotService businessTripTimeSlotService;

    @Autowired
    private LeaveTimeSlotService leaveTimeSlotService;

    @Autowired
    private SchedulingTimeWorkPeopleService schedulingTimeWorkPeopleService;

    @Autowired
    private SchedulingTimeWorkService schedulingTimeWorkService;

    @Autowired
    private SchedulingShiftsService schedulingShiftsService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private IFarmStationService iFarmStationService;

    @Autowired
    private IFarmService iFarmService;

    @Override
    protected void createPrepose(Scheduling entity) {
        if (entity.getPublishState() == null) {
            entity.setPublishState(SchedulePublishState.DRAFT.getKey());
        }
        // 排班开始时间（yyyy-MM-dd）
        String startDateStr = entity.getStartTime();
        String endDateStr = entity.getEndTime();
        boolean compareTime = DateUtil.compareTime(startDateStr, endDateStr, "yyyy-MM-dd");
        if (!compareTime) {
            throw new CustomException("开始时间不能大于结束时间");
        }

        // 拿出本次排班所有时间段（时分秒）
        List<SchedulingTime> timeList = entity.getSchedulingTimeMation();
        if (CollectionUtil.isEmpty(timeList)) {
            return;
        }
        // 收集所有员工ID和时间段信息
        Set<String> allEmployeeIds = new HashSet<>();
        // key: schedulingTimeId(时分秒), value: 该时间段下所有员工id
        Map<String, Set<String>> timeSlotToEmployeeSet = new HashMap<>();
        // key: employeeId, value: 该员工在本次排班下所有时分秒时间段id
        Map<String, Set<String>> employeeToTimeSlotSet = new HashMap<>();
        // timeKey -> isNextDay，供跨天时段冲突检测
        Map<String, Integer> timeKeyToIsNextDay = new HashMap<>();

        for (SchedulingTime schedulingTime : timeList) {
            String timeKey = schedulingTime.getStartTime() + "-" + schedulingTime.getEndTime();
            timeKeyToIsNextDay.put(timeKey, schedulingTime.getIsNextDay());
            List<SchedulingTimeWork> timeWorkList = schedulingTime.getSchedulingTimeWorkMation();
            if (CollectionUtil.isEmpty(timeWorkList)) {
                continue;
            }
            for (SchedulingTimeWork timeWork : timeWorkList) {
                List<SchedulingTimeWorkPeople> workPeopleList = timeWork.getSchedulingTimeWorkPeopleMation();
                if (CollectionUtil.isEmpty(workPeopleList)) {
                    continue;
                }
                for (SchedulingTimeWorkPeople people : workPeopleList) {
                    String employeeId = people.getEmployeeId();
                    if (StrUtil.isEmpty(employeeId)) {
                        continue;
                    }
                    allEmployeeIds.add(employeeId);
                    // 统计本次新增的员工-时间段
                    timeSlotToEmployeeSet.computeIfAbsent(timeKey, k -> new HashSet<>()).add(employeeId);
                    employeeToTimeSlotSet.computeIfAbsent(employeeId, k -> new HashSet<>()).add(timeKey);
                }
            }
        }
        // 1. 校验本次新增数据内部：同一员工不能在同一年月日下的同一时分秒被多次排班
        for (Map.Entry<String, Set<String>> entry : timeSlotToEmployeeSet.entrySet()) {
            Set<String> employees = entry.getValue();
            if (employees.size() != new HashSet<>(employees).size()) {
                throw new CustomException("同一员工不能在同一时间段被多次排班");
            }
        }
        // 2. 校验数据库中是否有冲突
        if (!allEmployeeIds.isEmpty()) {
            List<SchedulingTimeWorkPeople> existingSchedules = schedulingTimeWorkPeopleService.findSchedulingTimeByEmployeeIdList(new ArrayList<>(allEmployeeIds));
            if (CollectionUtil.isNotEmpty(existingSchedules)) {
                // 查询这些排班的Scheduling，过滤出年月日范围重叠的
                List<String> schedulingIds = existingSchedules.stream().map(SchedulingTimeWorkPeople::getSchedulingId).collect(Collectors.toList());
                List<Scheduling> schedulingList = querySchedulingByIds(schedulingIds);
                Map<String, Scheduling> schedulingIdToEntity = schedulingList.stream().collect(Collectors.toMap(Scheduling::getId, s -> s));
                // 查询这些排班的SchedulingTime，获取时分秒段
                List<String> schedulingTimeIds = existingSchedules.stream().map(SchedulingTimeWorkPeople::getSchedulingTimeId).collect(Collectors.toList());
                List<SchedulingTime> schedulingTimes = schedulingTimeService.querySchedulingTimeByIds(schedulingTimeIds);
                Map<String, SchedulingTime> schedulingTimeIdToEntity = schedulingTimes.stream().collect(Collectors.toMap(SchedulingTime::getId, t -> t));
                for (SchedulingTimeWorkPeople exist : existingSchedules) {
                    String employeeId = exist.getEmployeeId();
                    Map<String, Map<String, Object>> stringMapMap = iAuthUserService.queryUserMationListByStaffIds(Collections.singletonList(employeeId));
                    Scheduling scheduling = schedulingIdToEntity.get(exist.getSchedulingId());
                    SchedulingTime schedulingTime = schedulingTimeIdToEntity.get(exist.getSchedulingTimeId());
                    if (scheduling == null || schedulingTime == null) continue;
                    // 校验年月日是否有重叠
                    boolean dateOverlap = !(endDateStr.compareTo(scheduling.getStartTime()) < 0 || startDateStr.compareTo(scheduling.getEndTime()) > 0);
                    if (!dateOverlap) continue;
                    // 校验时分秒是否有重叠
                    for (String timeKey : employeeToTimeSlotSet.getOrDefault(employeeId, Collections.emptySet())) {
                        String[] arr = timeKey.split("-");
                        String newStart = arr[0];
                        String newEnd = arr[1];
                        String existStart = schedulingTime.getStartTime();
                        String existEnd = schedulingTime.getEndTime();
                        Integer newIsNextDay = timeKeyToIsNextDay.get(timeKey);
                        // 与库内已有排班比较时段重叠（含 isNextDay 跨天）
                        boolean timeOverlap = isTimeOverlap(newStart, newEnd, newIsNextDay, existStart, existEnd,
                            schedulingTime.getIsNextDay());
                        if (timeOverlap) {
                            throw new CustomException("员工 " + stringMapMap.get(employeeId).get("userName") + " 在排班日期[" + scheduling.getStartTime() + "," + scheduling.getEndTime() + "]的时间段[" + existStart + "-" + existEnd + "]已被排班，请勿重复安排！");
                        }
                    }
                }
            }
        }
    }

    private List<Scheduling> querySchedulingByIds(List<String> schedulingIds) {
        if (CollectionUtil.isEmpty(schedulingIds)) {
            return new ArrayList<>();
        }
        QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, schedulingIds);
        return list(queryWrapper);
    }

    @Override
    protected void createPostpose(Scheduling entity, String userId) {
        List<SchedulingTime> schedulingTimeMation = entity.getSchedulingTimeMation();
        if (CollectionUtil.isEmpty(schedulingTimeMation)) {
            return;
        }
        for (SchedulingTime schedulingTime : schedulingTimeMation) {
            schedulingTime.setSchedulingId(entity.getId());
        }
        schedulingTimeService.createEntity(schedulingTimeMation, userId);
    }

    @Override
    protected void updatePrepose(Scheduling entity) {
        Scheduling existing = super.selectById(entity.getId());
        assertSchedulingEditable(existing);
        if (existing != null) {
            entity.setPublishState(existing.getPublishState());
        }
    }

    @Override
    protected void updatePostpose(Scheduling entity, String userId) {
        List<SchedulingTime> schedulingTimeMation = entity.getSchedulingTimeMation();
        if (CollectionUtil.isEmpty(schedulingTimeMation)) {
            return;
        }
        // 入参现在的排班时间段
        List<SchedulingTime> nonEmptyIdSchedulingTimes = schedulingTimeMation.stream()
            .filter(time -> time.getId() != null && !time.getId().isEmpty()).collect(Collectors.toList());
        List<String> schedulingTimeIds = nonEmptyIdSchedulingTimes.stream().map(SchedulingTime::getId).collect(Collectors.toList());
        // 查询数据库中的排班时间
        List<SchedulingTime> schedulingTimes = schedulingTimeService.querySchedulingTimeBySchedulingId(entity.getId());
        List<String> schedulingTimeIdList = schedulingTimes.stream().map(SchedulingTime::getId).collect(Collectors.toList());
        // 拿到数据库中不在入参中的时间段id
        List<String> deleteSchedulingTimeIds = schedulingTimeIdList.stream().filter(
            time -> !schedulingTimeIds.contains(time)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(deleteSchedulingTimeIds)) {
            schedulingTimeService.deleteBySchedulingTimeIds(deleteSchedulingTimeIds);
        }
        // 将列表分为 id 不为空和 id 为空的两组
        Map<Boolean, List<SchedulingTime>> partitioned = schedulingTimeMation.stream()
            .collect(Collectors.partitioningBy(time -> time.getId() != null && !time.getId().isEmpty()));

        // id不为空的数据
        List<SchedulingTime> nonEmptyIdSchedulingTime = partitioned.get(true);
        if (CollectionUtil.isNotEmpty(nonEmptyIdSchedulingTime)) {
            schedulingTimeService.updateEntity(nonEmptyIdSchedulingTime, userId);
        }
        // id为空的数据
        List<SchedulingTime> emptyIdSchedulingTimes = partitioned.get(false);
        if (CollectionUtil.isNotEmpty(emptyIdSchedulingTimes)) {
            schedulingTimeService.createEntity(emptyIdSchedulingTimes, userId);
        }
    }

    @Override
    public Scheduling selectById(String id) {
        Scheduling scheduling = super.selectById(id);
        List<SchedulingTime> schedulingTimes = schedulingTimeService.querySchedulingTimeBySchedulingId(id);
        if (CollectionUtil.isNotEmpty(schedulingTimes)) {
            scheduling.setSchedulingTimeMation(schedulingTimes);
        }
        return scheduling;
    }

    @Override
    public void autoComputeScheduling(InputObject inputObject, OutputObject outputObject) {
        SchedulingAuto schedulingAuto = inputObject.getParams(SchedulingAuto.class);
        // 1. 获取基本参数
        String farmId = schedulingAuto.getFarmId();
        String schedulingShiftsId = schedulingAuto.getSchedulingShiftsId();
        String employeeIds = schedulingAuto.getEmployeeIds();
        String schedulingShiftsTimeIds = schedulingAuto.getSchedulingShiftsTimeIds();
        String schedulingShiftsTimeWorkId = schedulingAuto.getSchedulingShiftsTimeWorkId();
        String startTime = schedulingAuto.getStartTime();
        String endTime = schedulingAuto.getEndTime();

        // 2. 解析员工ID和权重
        Map<String, Integer> employeeIdWeightMap = new HashMap<>();
        try {
            JSONArray employeeArray = JSON.parseArray(employeeIds);
            for (int i = 0; i < employeeArray.size(); i++) {
                JSONObject employee = employeeArray.getJSONObject(i);
                String id = employee.getString("id");
                if (StrUtil.isNotBlank(id)) {
                    if (employee.containsKey("weight") && StrUtil.isNotBlank(employee.getString("weight"))) {
                        employeeIdWeightMap.put(id, employee.getInteger("weight"));
                    } else {
                        employeeIdWeightMap.put(id, 50);
                    }
                }
            }
        } catch (Exception e) {
            throw new CustomException("解析员工ID和权重数据失败：" + e.getMessage());
        }
        List<Map<String, Object>> allStaffList = iAuthUserService.queryDataMationByIds(employeeIds);

        // 3.1 区分正式员工和临时员工
        List<String> formalUserIds = new ArrayList<>();
        List<String> informalEmployeeIds = new ArrayList<>();
        Map<String, String> userIdToStaffIdMap = new HashMap<>();

        for (Map<String, Object> staff : allStaffList) {
            String staffId = staff.get("id").toString();
            Object userId = staff.get("userId");
            if (userId != null && StrUtil.isNotBlank(userId.toString())) {
                formalUserIds.add(userId.toString());
                userIdToStaffIdMap.put(userId.toString(), staffId);
            } else {
                informalEmployeeIds.add(staffId);
            }
        }

        // 4. 获取班次时间段信息
        List<String> shiftsTimeIdList = Arrays.asList(schedulingShiftsTimeIds.split(CommonCharConstants.COMMA_MARK));
        List<SchedulingShiftsTime> shiftsTimeList = schedulingShiftsTimeService.queryShiftsTimeByIdList(shiftsTimeIdList);

        // 5. 获取指定工位信息
        List<String> workIds = Arrays.asList(schedulingShiftsTimeWorkId.split(CommonCharConstants.COMMA_MARK));

        // 获取所有班次时间段下的工位信息
        List<SchedulingShiftsTimeWork> allShiftsTimeWorks = schedulingShiftsTimeWorkService.queryShiftsTimeWorkByIds(workIds);
        if (CollectionUtil.isEmpty(allShiftsTimeWorks)) {
            throw new CustomException("未找到指定的工位信息");
        }

        // 建立时间段和工位的映射关系
        Map<String, List<SchedulingShiftsTimeWork>> timeSlotToWorkMap = new HashMap<>();
        for (SchedulingShiftsTime shiftsTime : shiftsTimeList) {
            List<SchedulingShiftsTimeWork> timeSlotWorks = allShiftsTimeWorks.stream()
                .filter(work -> shiftsTime.getId().equals(work.getShiftsTimeId()))
                .collect(Collectors.toList());
            timeSlotToWorkMap.put(shiftsTime.getId(), timeSlotWorks);
        }

        // 6. 获取请假和出差信息
        Map<String, List<LeaveTimeSlot>> formalLeaveMap = queryLeaveByEmployeeIds(formalUserIds, startTime, endTime);
        Map<String, List<BusinessTripTimeSlot>> tripMap = queryTripByEmployeeIds(formalUserIds, startTime, endTime);
        Map<String, List<SchedulingLeave>> informalLeaveMap = schedulingLeaveService.queryLeaveByEmployeeIds(informalEmployeeIds, startTime, endTime);

        // 7. 创建排班结果
        Scheduling scheduling = new Scheduling();
        scheduling.setShiftId(schedulingShiftsId);
        scheduling.setScheduleType(CommonNumConstants.NUM_ONE);
        scheduling.setFarmId(farmId);
        scheduling.setStartTime(startTime);
        scheduling.setEndTime(endTime);

        // 8. 生成日期范围
        List<LocalDate> dateRange = generateDateRange(startTime, endTime);

        // 9. 创建排班时间列表
        List<SchedulingTime> schedulingTimeList = new ArrayList<>();

        // 用于跟踪全局已分配的员工
        Set<String> globalAssignedStaffIds = new HashSet<>();

        // 为每个时间段创建排班信息
        for (SchedulingShiftsTime shiftsTime : shiftsTimeList) {
            // 创建时间段信息
            SchedulingTime schedulingTime = new SchedulingTime();
            schedulingTime.setStartTime(shiftsTime.getStartTime());
            schedulingTime.setEndTime(shiftsTime.getEndTime());
            schedulingTime.setIsNextDay(shiftsTime.getIsNextDay());
            schedulingTime.setColor(shiftsTime.getColor());
            schedulingTime.setName(shiftsTime.getName());
            schedulingTime.setMinStaff(shiftsTime.getMinStaff());
            schedulingTime.setMaxStaff(shiftsTime.getMaxStaff());
            schedulingTime.setSchedulingId(scheduling.getId());

            // 获取当前时间段下的工位列表
            List<SchedulingShiftsTimeWork> timeSlotWorks = timeSlotToWorkMap.get(shiftsTime.getId());
            if (timeSlotWorks != null && !timeSlotWorks.isEmpty()) {
                List<SchedulingTimeWork> timeWorkList = new ArrayList<>();

                // 获取当前时间段的所有可用员工
                List<Map<String, Object>> availableStaff = getAvailableStaffForTimeSlot(
                    allStaffList,
                    dateRange,
                    shiftsTime,
                    formalLeaveMap,
                    tripMap,
                    informalLeaveMap,
                    employeeIdWeightMap
                );

                // 按权重排序可用员工
                availableStaff.sort((a, b) -> {
                    String idA = a.get("id").toString();
                    String idB = b.get("id").toString();
                    int weightA = employeeIdWeightMap.getOrDefault(idA, 50);
                    int weightB = employeeIdWeightMap.getOrDefault(idB, 50);
                    return Integer.compare(weightB, weightA);
                });

                // 将员工分为已分配和未分配两组
                List<String> unassignedStaff = new ArrayList<>();
                List<String> assignedStaff = new ArrayList<>();

                for (String employeeId : employeeIdWeightMap.keySet()) {
                    if (globalAssignedStaffIds.contains(employeeId)) {
                        assignedStaff.add(employeeId);
                    } else {
                        unassignedStaff.add(employeeId);
                    }
                }

                // 优先使用未分配的员工
                List<String> staffToAssign = new ArrayList<>();
                staffToAssign.addAll(unassignedStaff);
                staffToAssign.addAll(assignedStaff);

                // 按最小需求人数对工位进行排序（从小到大）
                timeSlotWorks.sort((a, b) -> Integer.compare(a.getMinStaff(), b.getMinStaff()));

                // 为每个工位分配员工
                for (SchedulingShiftsTimeWork workInfo : timeSlotWorks) {
                    // 创建工位信息
                    SchedulingTimeWork timeWork = new SchedulingTimeWork();
                    timeWork.setWorkId(workInfo.getWorkId());
                    timeWork.setMinStaff(workInfo.getMinStaff());
                    timeWork.setMaxStaff(workInfo.getMaxStaff());
                    timeWork.setSchedulingId(scheduling.getId());
                    timeWork.setSchedulingTimeId(schedulingTime.getId());

                    List<SchedulingTimeWorkPeople> workPeopleList = new ArrayList<>();
                    Set<String> currentWorkAssignedStaffIds = new HashSet<>();

                    // 计算当前工位应该分配的员工数量
                    int minStaff = workInfo.getMinStaff();
                    int maxStaff = workInfo.getMaxStaff();
                    int availableStaffCount = staffToAssign.size();

                    // 如果是最后一个工位，分配所有剩余员工
                    if (timeSlotWorks.indexOf(workInfo) == timeSlotWorks.size() - 1) {
                        while (!staffToAssign.isEmpty() && workPeopleList.size() < maxStaff) {
                            String employeeId = staffToAssign.remove(0);
                            if (!currentWorkAssignedStaffIds.contains(employeeId)) {
                                SchedulingTimeWorkPeople workPeople = new SchedulingTimeWorkPeople();
                                workPeople.setEmployeeId(employeeId);
                                workPeopleList.add(workPeople);
                                currentWorkAssignedStaffIds.add(employeeId);
                                globalAssignedStaffIds.add(employeeId);
                            }
                        }
                    } else {
                        // 计算当前工位应该分配的员工数量
                        int staffToAssignCount;

                        // 如果剩余员工数量不足以满足最小需求，则优先满足最小需求人数较小的工位
                        if (availableStaffCount < minStaff) {
                            // 如果当前工位是最小需求人数最小的工位，则分配所有剩余员工
                            if (timeSlotWorks.indexOf(workInfo) == 0) {
                                staffToAssignCount = Math.min(maxStaff, availableStaffCount);
                            } else {
                                // 其他工位不分配员工
                                staffToAssignCount = 0;
                            }
                        } else {
                            // 如果剩余员工数量足够，则分配最小需求人数
                            staffToAssignCount = Math.min(maxStaff, minStaff);
                        }

                        // 分配员工到当前工位
                        for (int i = 0; i < staffToAssignCount && !staffToAssign.isEmpty(); i++) {
                            String employeeId = staffToAssign.remove(0);
                            if (!currentWorkAssignedStaffIds.contains(employeeId)) {
                                SchedulingTimeWorkPeople workPeople = new SchedulingTimeWorkPeople();
                                workPeople.setEmployeeId(employeeId);
                                workPeopleList.add(workPeople);
                                currentWorkAssignedStaffIds.add(employeeId);
                                globalAssignedStaffIds.add(employeeId);
                            }
                        }
                    }

                    timeWork.setSchedulingTimeWorkPeopleMation(workPeopleList);
                    timeWorkList.add(timeWork);
                }

                schedulingTime.setSchedulingTimeWorkMation(timeWorkList);
            }

            schedulingTimeList.add(schedulingTime);
        }

        scheduling.setSchedulingTimeMation(schedulingTimeList);
        outputObject.setBean(scheduling);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 分页查询当前登录员工参与的排班记录（我的排班考勤列表）。
     * <p>
     * 通过 MPJ 将排班实例与员工排班关系联表后再分页，保证 total 与 rows 均为当前员工数据。
     * 列表展示字段由 {@link #fillStaffSchedulingListMation} 补全。
     */
    @Override
    @IgnoreTenant
    public void querySchedulingByStaffId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        // 当前登录用户关联的员工 id
        String staffId = InputObject.getLogParamsStatic().get("staffId").toString();
        // 班次名称模糊搜索关键字
        String keyword = commonPageInfo.getKeyword();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;

        // 在联表结果集上分页，避免对全表排班分页后再过滤员工
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // s=排班实例，p=排班工位下员工；innerJoin 仅保留该员工参与过的排班
        MPJLambdaWrapper<Scheduling> wrapper = JoinWrappers.lambda("s", Scheduling.class)
            .innerJoin(SchedulingTimeWorkPeople.class, "p",
                SchedulingTimeWorkPeople::getSchedulingId, Scheduling::getId)
            .eq("p." + MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getEmployeeId), staffId)
            .eq("s." + MybatisPlusUtil.toColumns(Scheduling::getPublishState), SchedulePublishState.PUBLISHED.getKey())
            // 同一排班多时间段会产生多行，去重保证分页准确
            .distinct()
            .orderByDesc(Scheduling::getCreateTime);
        // 按班次定义名称模糊搜索
        if (StrUtil.isNotEmpty(keyword)) {
            wrapper.innerJoin(SchedulingShifts.class, "sh", SchedulingShifts::getId, Scheduling::getShiftId)
                .like("sh." + MybatisPlusUtil.toColumns(SchedulingShifts::getName), keyword);
        }
        // 多租户：联表涉及的每张表均加 tenant_id 条件
        if (StrUtil.isNotEmpty(tenantId)) {
            wrapper.eq("s." + CommonConstants.TENANT_ID_FIELD, tenantId);
            wrapper.eq("p." + CommonConstants.TENANT_ID_FIELD, tenantId);
            if (StrUtil.isNotEmpty(keyword)) {
                wrapper.eq("sh." + CommonConstants.TENANT_ID_FIELD, tenantId);
            }
        }

        List<Scheduling> schedulingList = skyeyeBaseMapper.selectJoinList(Scheduling.class, wrapper);
        if (CollectionUtil.isEmpty(schedulingList)) {
            outputObject.setBeans(Collections.emptyList());
            outputObject.settotal(page.getTotal());
            return;
        }
        // 补全 shiftMation、schedulingTimeMation 等前端展示字段
        fillStaffSchedulingListMation(schedulingList, staffId, tenantId);
        outputObject.setBeans(schedulingList);
        outputObject.settotal(page.getTotal());
    }

    /**
     * 补全排班列表展示所需的关联信息。
     * <ul>
     *   <li>shiftMation：排班班次名称（SchedulingShifts）</li>
     *   <li>farmMation：车间名称</li>
     *   <li>schedulingTimeMation：当前员工在该排班下参与的工作时间段及工位名称</li>
     * </ul>
     *
     * @param schedulingList 当前页排班实例
     * @param staffId        员工 id
     * @param tenantId       租户 id，为空时不加租户条件
     */
    private void fillStaffSchedulingListMation(List<Scheduling> schedulingList, String staffId, String tenantId) {
        List<String> schedulingIds = schedulingList.stream().map(Scheduling::getId).collect(Collectors.toList());
        // 批量查班次定义，用于列表「班次」列展示
        schedulingShiftsService.setDataMation(schedulingList, Scheduling::getShiftId);

        // 批量查车间信息
        iFarmService.setDataMation(schedulingList, Scheduling::getFarmId);

        // 查当前员工在本页各排班下被分配的时间段 id
        QueryWrapper<SchedulingTimeWorkPeople> peopleWrapper = new QueryWrapper<>();
        peopleWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getEmployeeId), staffId);
        peopleWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingId), schedulingIds);
        if (StrUtil.isNotEmpty(tenantId)) {
            peopleWrapper.eq(CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        List<SchedulingTimeWorkPeople> peopleList = schedulingTimeWorkPeopleService.list(peopleWrapper);
        Map<String, List<SchedulingTimeWorkPeople>> peopleBySchedulingId = peopleList.stream()
            .collect(Collectors.groupingBy(SchedulingTimeWorkPeople::getSchedulingId));

        List<String> timeWorkRowIds = peopleList.stream()
            .map(SchedulingTimeWorkPeople::getSchedulingTimeWorkId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Map<String, SchedulingTimeWork> timeWorkRowMap = schedulingTimeWorkService.querySchedulingTimeByIds(timeWorkRowIds).stream()
            .collect(Collectors.toMap(SchedulingTimeWork::getId, row -> row, (a, b) -> a));

        List<String> workStationIds = timeWorkRowMap.values().stream()
            .map(SchedulingTimeWork::getWorkId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Map<String, String> workStationNameMap = loadWorkStationNameMap(workStationIds);

        List<String> timeIds = peopleList.stream()
            .map(SchedulingTimeWorkPeople::getSchedulingTimeId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        // 按排班实例 id 分组，供「工作时间段」列展示
        Map<String, List<SchedulingTime>> timeMap = CollectionUtil.isEmpty(timeIds)
            ? Collections.emptyMap()
            : schedulingTimeService.querySchedulingTimeByTimeIds(timeIds).stream()
            .collect(Collectors.groupingBy(SchedulingTime::getSchedulingId));

        schedulingList.forEach(scheduling -> {
            List<SchedulingTimeWorkPeople> schedulingPeople = peopleBySchedulingId.getOrDefault(scheduling.getId(), Collections.emptyList());
            List<SchedulingTime> times = timeMap.getOrDefault(scheduling.getId(), Collections.emptyList());
            for (SchedulingTime time : times) {
                time.setWorkStationNameList(resolveWorkStationNamesForTime(
                    time.getId(), schedulingPeople, timeWorkRowMap, workStationNameMap));
            }
            // 同一员工可能被分配到多个工位，起止相同的时间段合并工位名称后只展示一次
            scheduling.setSchedulingTimeMation(dedupeSchedulingTimesByRange(times));
        });
    }

    private Map<String, String> loadWorkStationNameMap(List<String> workStationIds) {
        if (CollectionUtil.isEmpty(workStationIds)) {
            return Collections.emptyMap();
        }
        String workIdsStr = String.join(CommonCharConstants.COMMA_MARK, workStationIds);
        return iFarmStationService.queryFarmStationByIds(workIdsStr).stream()
            .filter(map -> ObjectUtil.isNotEmpty(map.get("id")))
            .collect(Collectors.toMap(
                map -> map.get("id").toString(),
                map -> ObjectUtil.defaultIfNull(map.get("name"), StrUtil.EMPTY).toString(),
                (a, b) -> a));
    }

    private List<String> resolveWorkStationNamesForTime(String schedulingTimeId,
                                                        List<SchedulingTimeWorkPeople> schedulingPeople,
                                                        Map<String, SchedulingTimeWork> timeWorkRowMap,
                                                        Map<String, String> workStationNameMap) {
        LinkedHashSet<String> names = new LinkedHashSet<>();
        for (SchedulingTimeWorkPeople people : schedulingPeople) {
            if (!schedulingTimeId.equals(people.getSchedulingTimeId())) {
                continue;
            }
            SchedulingTimeWork timeWork = timeWorkRowMap.get(people.getSchedulingTimeWorkId());
            if (timeWork == null || StrUtil.isBlank(timeWork.getWorkId())) {
                continue;
            }
            String workStationName = workStationNameMap.get(timeWork.getWorkId());
            if (StrUtil.isNotBlank(workStationName)) {
                names.add(workStationName);
            }
        }
        return new ArrayList<>(names);
    }

    /**
     * 按起止时间与跨天标识去重，并合并同一时间段下多个工位名称。
     */
    private List<SchedulingTime> dedupeSchedulingTimesByRange(List<SchedulingTime> times) {
        if (CollectionUtil.isEmpty(times)) {
            return times;
        }
        Map<String, SchedulingTime> uniqueMap = new LinkedHashMap<>();
        Map<String, LinkedHashSet<String>> workNamesByKey = new HashMap<>();
        for (SchedulingTime time : times) {
            String key = buildSchedulingTimeDedupeKey(time);
            uniqueMap.putIfAbsent(key, time);
            if (CollectionUtil.isNotEmpty(time.getWorkStationNameList())) {
                workNamesByKey.computeIfAbsent(key, item -> new LinkedHashSet<>())
                    .addAll(time.getWorkStationNameList());
            }
        }
        uniqueMap.forEach((key, time) -> {
            LinkedHashSet<String> names = workNamesByKey.get(key);
            if (CollectionUtil.isNotEmpty(names)) {
                time.setWorkStationNameList(new ArrayList<>(names));
            }
        });
        return uniqueMap.values().stream()
            .sorted(Comparator.comparing(item -> normalizeShiftHmForOverlap(item.getStartTime()),
                Comparator.nullsLast(String::compareTo)))
            .collect(Collectors.toList());
    }

    private String buildSchedulingTimeDedupeKey(SchedulingTime time) {
        String start = normalizeShiftHmForOverlap(time.getStartTime());
        String end = normalizeShiftHmForOverlap(time.getEndTime());
        String isNextDay = String.valueOf(ObjectUtil.defaultIfNull(time.getIsNextDay(), WhetherEnum.DISABLE_USING.getKey()));
        return start + "|" + end + "|" + isNextDay;
    }

    @Override
    public void querySchedulingByStaffIdAndMouths(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        String mouths = inputObject.getParams().get("mouths").toString();
        List<String> mouthList = Arrays.asList(mouths.split(CommonCharConstants.COMMA_MARK));
        List<String> sortedDates = querySchedulingByStaffIdAndMouths(staffId, mouthList);
        outputObject.setBeans(sortedDates);
        outputObject.settotal(sortedDates.size());
    }

    @Override
    public List<String> querySchedulingByStaffIdAndMouths(String staffId, List<String> mouthList) {
        return querySchedulingWorkDaysByStaffAndShift(staffId, null, mouthList);
    }

    @Override
    public List<String> querySchedulingWorkDaysByStaffAndShift(String staffId, String shiftId, List<String> mouthList) {
        List<Scheduling> schedulingList = listSchedulingsIntersectingMonths(mouthList);
        if (StrUtil.isNotBlank(shiftId)) {
            schedulingList = schedulingList.stream()
                .filter(scheduling -> StrUtil.equals(scheduling.getShiftId(), shiftId))
                .collect(Collectors.toList());
        }
        return expandWorkDaysForStaff(schedulingList, staffId);
    }

    private List<Scheduling> listSchedulingsIntersectingMonths(List<String> mouthList) {
        QueryWrapper<Scheduling> schedulingWrapper = new QueryWrapper<>();
        applyPublishedSchedulingCondition(schedulingWrapper);
        mouthList.forEach(month -> {
            String monthPattern = month + "%";
            schedulingWrapper.or(wrap -> wrap
                .like(MybatisPlusUtil.toColumns(Scheduling::getStartTime), monthPattern)
                .or()
                .like(MybatisPlusUtil.toColumns(Scheduling::getEndTime), monthPattern)
            );
        });
        return list(schedulingWrapper);
    }

    private List<String> expandWorkDaysForStaff(List<Scheduling> schedulingList, String staffId) {
        if (CollectionUtil.isEmpty(schedulingList)) {
            return new ArrayList<>();
        }
        List<String> schedulingIds = schedulingList.stream().map(Scheduling::getId).collect(Collectors.toList());
        List<SchedulingTimeWorkPeople> timeWorkPeople = schedulingTimeWorkPeopleService
            .querySchedulingByschedulingIdsAndStaffId(schedulingIds, staffId);

        if (CollectionUtil.isEmpty(timeWorkPeople)) {
            return new ArrayList<>();
        }

        List<Scheduling> filteredSchedulingList = schedulingList.stream()
            .filter(scheduling -> timeWorkPeople.stream()
                .anyMatch(people -> people.getSchedulingId().equals(scheduling.getId())
                    && people.getEmployeeId().equals(staffId)))
            .collect(Collectors.toList());
        Set<LocalDate> allDates = new HashSet<>();
        for (Scheduling scheduling : filteredSchedulingList) {
            LocalDateTime startDateTime = parseDateTime(scheduling.getStartTime());
            LocalDateTime endDateTime = parseDateTime(scheduling.getEndTime());
            LocalDate startDate = startDateTime.toLocalDate();
            LocalDate endDate = endDateTime.toLocalDate();
            while (!startDate.isAfter(endDate)) {
                allDates.add(startDate);
                startDate = startDate.plusDays(1);
            }
        }
        return allDates.stream().sorted()
            .map(LocalDate::toString)
            .collect(Collectors.toList());
    }

    /**
     * 查询员工在指定日期或指定月份内可打卡的排班班次列表。
     * <p>
     * 排班实例 {@link Scheduling} 有 startTime/endTime（年月日）范围限制，必须按时间过滤后再查员工排班。
     * <ul>
     *   <li>day（yyyy-MM-dd）：查该日有效的排班，考勤打卡默认传当天</li>
     *   <li>monthMation（yyyy-MM）：查与该月有交集的排班，考勤月历切换月份时使用</li>
     *   <li>均未传：默认按当天查询，兼容旧调用</li>
     * </ul>
     * 返回项 id 为 {@link SchedulingShifts} 主键，与 check_work.time_id 一致。
     */
    @Override
    public void querySchedulingByStaffIdAndOneDay(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        String day = map.containsKey("day") && map.get("day") != null ? map.get("day").toString().trim() : StrUtil.EMPTY;
        String monthMation = map.containsKey("monthMation") && map.get("monthMation") != null
            ? map.get("monthMation").toString().trim() : StrUtil.EMPTY;
        List<Map<String, Object>> result = queryStaffSchedulingShiftList(staffId, day, monthMation);
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    /**
     * 按员工 + 日期/月份查询排班班次（SchedulingShifts）列表。
     *
     * @param staffId     员工 id
     * @param day         指定日 yyyy-MM-dd，优先于 monthMation
     * @param monthMation 指定月 yyyy-MM，与排班实例时间范围求交集
     */
    private List<Map<String, Object>> queryStaffSchedulingShiftList(String staffId, String day, String monthMation) {
        QueryWrapper<Scheduling> schedulingWrapper = new QueryWrapper<>();
        applyPublishedSchedulingCondition(schedulingWrapper);
        if (StrUtil.isNotBlank(day)) {
            // 排班开始 <= 指定日 <= 排班结束
            schedulingWrapper.le(MybatisPlusUtil.toColumns(Scheduling::getStartTime), day)
                .ge(MybatisPlusUtil.toColumns(Scheduling::getEndTime), day);
        } else if (StrUtil.isNotBlank(monthMation)) {
            String monthStart = monthMation + "-01";
            String monthEnd = resolveMonthLastDay(monthMation);
            schedulingWrapper.le(MybatisPlusUtil.toColumns(Scheduling::getStartTime), monthEnd)
                .ge(MybatisPlusUtil.toColumns(Scheduling::getEndTime), monthStart);
        } else {
            String today = DateUtil.getYmdTimeAndToString();
            schedulingWrapper.le(MybatisPlusUtil.toColumns(Scheduling::getStartTime), today)
                .ge(MybatisPlusUtil.toColumns(Scheduling::getEndTime), today);
        }
        List<Scheduling> schedulingList = list(schedulingWrapper);
        if (CollectionUtil.isEmpty(schedulingList)) {
            return Collections.emptyList();
        }
        List<String> schedulingIds = schedulingList.stream().map(Scheduling::getId).collect(Collectors.toList());
        List<SchedulingTimeWorkPeople> timeWorkPeople = schedulingTimeWorkPeopleService.querySchedulingByschedulingIdsAndStaffId(schedulingIds, staffId);
        if (CollectionUtil.isEmpty(timeWorkPeople)) {
            return Collections.emptyList();
        }
        List<String> schedulingTimeList = timeWorkPeople.stream().map(SchedulingTimeWorkPeople::getSchedulingTimeId).collect(Collectors.toList());
        List<SchedulingTime> schedulingTimes = schedulingTimeService.querySchedulingTimeByIds(schedulingTimeList);
        Map<String, List<SchedulingTime>> timeMap = schedulingTimes.stream()
            .collect(Collectors.groupingBy(SchedulingTime::getSchedulingId));
        // 按 SchedulingShifts.id 去重，并收集各班次对应的时间段
        LinkedHashSet<String> shiftIds = new LinkedHashSet<>();
        Map<String, List<SchedulingTime>> shiftDayTimes = new HashMap<>();
        for (Scheduling scheduling : schedulingList) {
            if (StrUtil.isBlank(scheduling.getShiftId())) {
                continue;
            }
            List<SchedulingTime> times = timeMap.getOrDefault(scheduling.getId(), Collections.emptyList());
            for (SchedulingTime time : times) {
                if (timeWorkPeople.stream()
                    .anyMatch(p -> p.getSchedulingTimeId().equals(time.getId()))) {
                    shiftIds.add(scheduling.getShiftId());
                    shiftDayTimes.computeIfAbsent(scheduling.getShiftId(), key -> new ArrayList<>()).add(time);
                }
            }
        }
        if (CollectionUtil.isEmpty(shiftIds)) {
            return Collections.emptyList();
        }
        List<SchedulingShifts> shifts = schedulingShiftsService.querySchedulingShiftsByIds(new ArrayList<>(shiftIds));
        Map<String, SchedulingShifts> shiftMap = shifts.stream()
            .collect(Collectors.toMap(SchedulingShifts::getId, shift -> shift, (a, b) -> a));
        Map<String, List<SchedulingShiftsTime>> templateTimeMap = schedulingShiftsTimeService.queryTimeByIdListMap(new ArrayList<>(shiftIds));
        List<Map<String, Object>> result = new ArrayList<>();
        for (String shiftId : shiftIds) {
            SchedulingShifts shift = shiftMap.get(shiftId);
            if (shift == null) {
                continue;
            }
            result.add(buildStaffSchedulingShiftItem(shift, shiftDayTimes.get(shiftId), templateTimeMap.get(shiftId)));
        }
        return result;
    }

    /**
     * 根据 yyyy-MM 得到该月最后一天 yyyy-MM-dd
     */
    private String resolveMonthLastDay(String monthMation) {
        LocalDate firstDay = LocalDate.parse(monthMation + "-01");
        return firstDay.withDayOfMonth(firstDay.lengthOfMonth()).toString();
    }

    /**
     * 组装员工当日单个排班班次的下拉展示项。
     * id = SchedulingShifts.id；起止时间多段时取最早开始、最晚结束。
     */
    private Map<String, Object> buildStaffSchedulingShiftItem(SchedulingShifts shift, List<SchedulingTime> dayTimes, List<SchedulingShiftsTime> templateTimes) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", shift.getId());
        item.put("name", shift.getName());
        if (CollectionUtil.isNotEmpty(dayTimes)) {
            // 优先使用当日排班实例的实际时间段
            List<SchedulingTime> sortedTimes = dayTimes.stream()
                .sorted(Comparator.comparing(time -> normalizeShiftHmForOverlap(time.getStartTime()), Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());
            SchedulingTime first = sortedTimes.get(0);
            SchedulingTime last = sortedTimes.get(sortedTimes.size() - 1);
            item.put("startTime", first.getStartTime());
            item.put("endTime", last.getEndTime());
            item.put("isNextDay", resolveDayTimesCrossDay(sortedTimes, first, last));
        } else if (CollectionUtil.isNotEmpty(templateTimes)) {
            // 无当日实例时，回退到班次模板时间段
            List<SchedulingShiftsTime> sortedTimes = templateTimes.stream()
                .sorted(Comparator.comparing(time -> normalizeShiftHmForOverlap(time.getStartTime()), Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());
            SchedulingShiftsTime first = sortedTimes.get(0);
            SchedulingShiftsTime last = sortedTimes.get(sortedTimes.size() - 1);
            item.put("startTime", first.getStartTime());
            item.put("endTime", last.getEndTime());
            item.put("isNextDay", resolveTemplateTimesCrossDay(sortedTimes, first, last));
        }
        return item;
    }

    /**
     * 当日排班实例多段时间的整体跨天判定
     */
    private Integer resolveDayTimesCrossDay(List<SchedulingTime> sortedTimes, SchedulingTime first, SchedulingTime last) {
        boolean crossDay = sortedTimes.stream()
            .anyMatch(time -> WhetherEnum.ENABLE_USING.getKey().equals(time.getIsNextDay()));
        if (!crossDay) {
            crossDay = CheckWorkTimePeriodUtil.isCrossDay(
                normalizeShiftHmForOverlap(first.getStartTime()),
                normalizeShiftHmForOverlap(last.getEndTime()))
                || StrUtil.equals(normalizeShiftHmForOverlap(first.getStartTime()), normalizeShiftHmForOverlap(last.getEndTime()));
        }
        return crossDay ? WhetherEnum.ENABLE_USING.getKey() : WhetherEnum.DISABLE_USING.getKey();
    }

    /**
     * 班次模板多段时间的整体跨天判定
     */
    private Integer resolveTemplateTimesCrossDay(List<SchedulingShiftsTime> sortedTimes, SchedulingShiftsTime first, SchedulingShiftsTime last) {
        boolean crossDay = sortedTimes.stream()
            .anyMatch(time -> WhetherEnum.ENABLE_USING.getKey().equals(time.getIsNextDay()));
        if (!crossDay) {
            crossDay = CheckWorkTimePeriodUtil.isCrossDay(
                normalizeShiftHmForOverlap(first.getStartTime()),
                normalizeShiftHmForOverlap(last.getEndTime()))
                || StrUtil.equals(normalizeShiftHmForOverlap(first.getStartTime()), normalizeShiftHmForOverlap(last.getEndTime()));
        }
        return crossDay ? WhetherEnum.ENABLE_USING.getKey() : WhetherEnum.DISABLE_USING.getKey();
    }

    @Override
    public void querySchedulingByStaffIdAndDays(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        // 格式为 "yyyy-MM-dd",逗号分隔
        String daysStr = map.get("days").toString();
        List<String> specificDays = Arrays.asList(daysStr.split(CommonCharConstants.COMMA_MARK));
        // 每天对应的排班时间段
        Map<String, List<SchedulingTime>> resultMap = new LinkedHashMap<>();
        // 循环每天
        for (String day : specificDays) {
            // 查询该天的排班
            QueryWrapper<Scheduling> schedulingWrapper = new QueryWrapper<>();
            applyPublishedSchedulingCondition(schedulingWrapper);
            schedulingWrapper.le(MybatisPlusUtil.toColumns(Scheduling::getStartTime), day)
                .ge(MybatisPlusUtil.toColumns(Scheduling::getEndTime), day);
            List<Scheduling> schedulingList = list(schedulingWrapper);
            if (CollectionUtil.isEmpty(schedulingList)) {
                resultMap.put(day, new ArrayList<>());
                continue;
            }
            List<String> schedulingIds = schedulingList.stream().map(Scheduling::getId).collect(Collectors.toList());
            List<SchedulingTimeWorkPeople> timeWorkPeople = schedulingTimeWorkPeopleService.querySchedulingByschedulingIdsAndStaffId(schedulingIds, staffId);
            if (CollectionUtil.isEmpty(timeWorkPeople)) {
                resultMap.put(day, new ArrayList<>());
                continue;
            }
            List<String> schedulingTimeList = timeWorkPeople.stream().map(SchedulingTimeWorkPeople::getSchedulingTimeId).collect(Collectors.toList());
            List<SchedulingTime> schedulingTimes = schedulingTimeService.querySchedulingTimeByIds(schedulingTimeList);
            Map<String, List<SchedulingTime>> timeMap = schedulingTimes.stream()
                .collect(Collectors.groupingBy(SchedulingTime::getSchedulingId));
            Set<SchedulingTime> timeSegments = new HashSet<>();
            for (Scheduling scheduling : schedulingList) {
                List<SchedulingTime> times = timeMap.getOrDefault(scheduling.getId(), Collections.emptyList());
                for (SchedulingTime time : times) {
                    if (timeWorkPeople.stream().anyMatch(p -> p.getSchedulingTimeId().equals(time.getId()))) {
                        timeSegments.add(time);
                    }
                }
            }
            resultMap.put(day, new ArrayList<>(timeSegments));
        }
        outputObject.setBean(resultMap);
        outputObject.settotal(resultMap.values().stream().mapToInt(List::size).sum());
    }

    @Override
    public List<Scheduling> querySchedulingByIdList(List<String> schedulingIdList) {
        if (CollectionUtil.isEmpty(schedulingIdList)) {
            return null;
        }
        QueryWrapper<Scheduling> schedulingWrapper = new QueryWrapper<>();
        schedulingWrapper.in(CommonConstants.ID, schedulingIdList);
        return list(schedulingWrapper);
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr != null) {
            dateTimeStr = dateTimeStr.trim();
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateTimeStr + ":00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException e2) {
                return LocalDateTime.parse(dateTimeStr + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        }
    }

    private void getStaffMation(List<SchedulingTimeWorkPeople> timeWorkPeople) {
        List<String> employIdList = timeWorkPeople.stream().map(SchedulingTimeWorkPeople::getEmployeeId).collect(Collectors.toList());
        String employIds = String.join(CommonCharConstants.COMMA_MARK, employIdList);
        List<Map<String, Object>> allStaffList = iAuthUserService.queryDataMationByIds(employIds);
        timeWorkPeople.forEach(
            staff -> {
                String employeeId = staff.getEmployeeId();
                Map<String, Object> staffMap = allStaffList.stream().filter(map -> ObjectUtil.equal(map.get("id"), employeeId)).findFirst().orElse(null);
                if (ObjectUtil.isNotEmpty(staffMap)) {
                    staff.setStaffMation(staffMap);
                }
            }
        );
    }

    private List<Map<String, Object>> getAvailableStaffForTimeSlot(
        List<Map<String, Object>> staffList,
        List<LocalDate> dateRange,
        SchedulingShiftsTime shiftTime,
        Map<String, List<LeaveTimeSlot>> formalLeaveMap,
        Map<String, List<BusinessTripTimeSlot>> tripMap,
        Map<String, List<SchedulingLeave>> informalLeaveMap,
        Map<String, Integer> employeeIdWeightMap) {

        // 1. 分离正式员工和非正式员工
        List<Map<String, Object>> formalStaff = new ArrayList<>();
        List<Map<String, Object>> informalStaff = new ArrayList<>();

        for (Map<String, Object> staff : staffList) {
            String employeeId = staff.get("id").toString();
            if (employeeIdWeightMap.containsKey(employeeId)) {
                if (staff.get("type") != null && "formal".equals(staff.get("type"))) {
                    formalStaff.add(staff);
                } else {
                    informalStaff.add(staff);
                }
            }
        }

        // 2. 获取可用员工
        List<Map<String, Object>> availableFormalStaff = getAvailableFormalStaffForTimeSlot(
            formalStaff, dateRange, shiftTime, formalLeaveMap, tripMap);
        List<Map<String, Object>> availableInformalStaff = getAvailableTempStaffForTimeSlot(
            informalStaff, dateRange, informalLeaveMap);

        // 3. 合并并排序员工列表
        List<Map<String, Object>> allAvailableStaff = new ArrayList<>();
        allAvailableStaff.addAll(availableFormalStaff);
        allAvailableStaff.addAll(availableInformalStaff);

        // 4. 根据权重排序
        allAvailableStaff.sort((a, b) -> {
            String idA = a.get("id").toString();
            String idB = b.get("id").toString();
            int weightA = employeeIdWeightMap.getOrDefault(idA, 1);
            int weightB = employeeIdWeightMap.getOrDefault(idB, 1);
            return Integer.compare(weightB, weightA);
        });

        return allAvailableStaff;
    }

    /**
     * 获取指定时间段可用的正式员工（过滤与请假/出差时段冲突者，含跨天排班）
     */
    private List<Map<String, Object>> getAvailableFormalStaffForTimeSlot(
        List<Map<String, Object>> staffList,
        List<LocalDate> dateRange,
        SchedulingShiftsTime shiftTime,
        Map<String, List<LeaveTimeSlot>> leaveMap,
        Map<String, List<BusinessTripTimeSlot>> tripMap) {

        return staffList.stream()
            .filter(staff -> {
                String staffId = staff.get("id").toString();
                String shiftStartTime = shiftTime.getStartTime();
                String shiftEndTime = shiftTime.getEndTime();

                // 检查是否请假
                if (leaveMap.containsKey(staffId)) {
                    for (LeaveTimeSlot leave : leaveMap.get(staffId)) {
                        LocalDateTime leaveStart = DateUtil.parseLeaveDateTime(leave.getLeaveStartTime());
                        LocalDateTime leaveEnd = DateUtil.parseLeaveDateTime(leave.getLeaveEndTime());
                        if (leaveStart == null || leaveEnd == null) {
                            continue;
                        }
                        for (LocalDate d : dateRange) {
                            if (!d.isBefore(leaveStart.toLocalDate()) && !d.isAfter(leaveEnd.toLocalDate())) {
                                if (isDateTimeOverlap(shiftStartTime, shiftEndTime, shiftTime.getIsNextDay(), d,
                                    leaveStart, leaveEnd)) {
                                    return false;
                                }
                            }
                        }
                    }
                }

                // 检查是否出差
                if (tripMap.containsKey(staffId)) {
                    for (BusinessTripTimeSlot trip : tripMap.get(staffId)) {
                        if (dateRange.contains(LocalDate.parse(trip.getTravelDay()))) {
                            // 检查时间段是否冲突
                            String tripStartTime = trip.getStartTime();
                            String tripEndTime = trip.getEndTime();
                            if (isTimeOverlap(shiftStartTime, shiftEndTime, shiftTime.getIsNextDay(),
                                tripStartTime, tripEndTime, null)) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            })
            .collect(Collectors.toList());
    }

    /**
     * 检查两个时间段是否重叠（支持排班 isNextDay / start>end 跨天）
     */
    private boolean isTimeOverlap(String start1, String end1, Integer isNextDay1,
                                  String start2, String end2, Integer isNextDay2) {
        long[] interval1 = toShiftMinuteInterval(start1, end1, isNextDay1);
        long[] interval2 = toShiftMinuteInterval(start2, end2, isNextDay2);
        return interval1[0] < interval2[1] && interval2[0] < interval1[1];
    }

    /**
     * 将 HH:mm 时段转为「当日 0 点起算分钟区间」；跨天则 end 顺延 24h
     */
    private long[] toShiftMinuteInterval(String start, String end, Integer isNextDay) {
        String s1 = normalizeShiftHmForOverlap(start);
        String e1 = normalizeShiftHmForOverlap(end);
        DateTimeFormatter formatter = s1.length() > 5 ? DateTimeFormatter.ofPattern("HH:mm:ss") : DateTimeFormatter.ofPattern("HH:mm");
        long startMin = LocalTime.parse(s1, formatter).toSecondOfDay() / 60L;
        long endMin = LocalTime.parse(e1, formatter).toSecondOfDay() / 60L;
        if (isSchedulingCrossDay(start, end, isNextDay)) {
            endMin += 24 * 60L;
        }
        return new long[]{startMin, endMin};
    }

    /**
     * 排班是否跨天：isNextDay=1 或 startTime>endTime（与打卡模块 crossDay 判定一致）
     */
    private boolean isSchedulingCrossDay(String start, String end, Integer isNextDay) {
        if (isNextDay != null && WhetherEnum.ENABLE_USING.getKey().equals(isNextDay)) {
            return true;
        }
        String startHm = normalizeShiftHmForOverlap(start);
        String endHm = normalizeShiftHmForOverlap(end);
        if (StrUtil.isNotEmpty(startHm) && startHm.equals(endHm)) {
            return true;
        }
        return CheckWorkTimePeriodUtil.isCrossDay(start, end);
    }

    /**
     * 排班/请假冲突检测用：HH:mm 补零、去空格
     */
    private String normalizeShiftHmForOverlap(String time) {
        if (StrUtil.isEmpty(time)) {
            return "00:00";
        }
        String t = time.trim();
        if (t.length() == 7) {
            return "0" + t;
        }
        return t;
    }

    /**
     * 检查排班时间段与请假 datetime 区间是否重叠（跨天排班按归属日 day 起算）
     */
    private boolean isDateTimeOverlap(String shiftStartStr, String shiftEndStr, Integer isNextDay, LocalDate day,
                                      LocalDateTime leaveStart, LocalDateTime leaveEnd) {
        if (StrUtil.isEmpty(shiftStartStr) || StrUtil.isEmpty(shiftEndStr)) {
            return false;
        }
        String s1 = normalizeShiftHmForOverlap(shiftStartStr);
        String e1 = normalizeShiftHmForOverlap(shiftEndStr);
        DateTimeFormatter formatter = s1.length() > 5 ? DateTimeFormatter.ofPattern("HH:mm:ss") : DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime shiftStart = day.atTime(LocalTime.parse(s1, formatter));
        LocalDateTime shiftEnd = isSchedulingCrossDay(shiftStartStr, shiftEndStr, isNextDay)
            ? day.plusDays(1).atTime(LocalTime.parse(e1, formatter))
            : day.atTime(LocalTime.parse(e1, formatter));
        return shiftStart.isBefore(leaveEnd) && leaveStart.isBefore(shiftEnd);
    }

    /**
     * @deprecated 保留兼容，请使用带 isNextDay 的重载
     */
    private boolean isTimeOverlap(String start1, String end1, String start2, String end2) {
        return isTimeOverlap(start1, end1, null, start2, end2, null);
    }

    /**
     * @deprecated 已由 {@link #isDateTimeOverlap(String, String, Integer, LocalDate, LocalDateTime, LocalDateTime)} 替代
     */
    private boolean isDateTimeOverlap(String shiftStartStr, String shiftEndStr, LocalDate day,
                                      LocalDateTime leaveStart, LocalDateTime leaveEnd) {
        return isDateTimeOverlap(shiftStartStr, shiftEndStr, null, day, leaveStart, leaveEnd);
    }

    /**
     * 查询正式员工请假信息
     */
    private Map<String, List<LeaveTimeSlot>> queryLeaveByEmployeeIds(List<String> formalUserIds, String startTime, String endTime) {
        Map<String, List<LeaveTimeSlot>> result = new HashMap<>();
        if (CollectionUtil.isEmpty(formalUserIds)) {
            return result;
        }
        List<Leave> leaveList = leaveService.queryLeaveByFormalUserIds(formalUserIds);
        // 2. 获取所有请假ID
        List<String> leaveIds = leaveList.stream()
            .map(Leave::getId)
            .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(leaveIds)) {
            return result;
        }

        List<LeaveTimeSlot> timeSlotList = leaveTimeSlotService.queryTimeAndIds(leaveIds, startTime, endTime);
        // 4. 建立请假ID到员工ID的映射
        Map<String, String> leaveIdToEmployeeIdMap = leaveList.stream()
            .collect(Collectors.toMap(Leave::getId, Leave::getCreateId));

        // 5. 按员工ID分组
        for (LeaveTimeSlot timeSlot : timeSlotList) {
            String employeeId = leaveIdToEmployeeIdMap.get(timeSlot.getParentId());
            if (employeeId != null) {
                result.computeIfAbsent(employeeId, k -> new ArrayList<>()).add(timeSlot);
            }
        }

        return result;
    }

    /**
     * 查询正式员工出差信息
     */
    private Map<String, List<BusinessTripTimeSlot>> queryTripByEmployeeIds(List<String> formalUserIds, String startTime, String endTime) {
        Map<String, List<BusinessTripTimeSlot>> result = new HashMap<>();
        if (CollectionUtil.isEmpty(formalUserIds)) {
            return result;
        }
        List<BusinessTrip> tripList = businessTripService.queryBusinessTripByUserIds(formalUserIds);

        // 2. 获取所有出差ID
        List<String> tripIds = tripList.stream()
            .map(BusinessTrip::getId)
            .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(tripIds)) {
            return result;
        }
        List<BusinessTripTimeSlot> timeSlotList = businessTripTimeSlotService.queryBusinessTripTimeSlotByIdsAndTime(tripIds, startTime, endTime);

        // 4. 建立出差ID到员工ID的映射
        Map<String, String> tripIdToEmployeeIdMap = tripList.stream()
            .collect(Collectors.toMap(BusinessTrip::getId, BusinessTrip::getCreateId));

        // 5. 按员工ID分组
        for (BusinessTripTimeSlot timeSlot : timeSlotList) {
            String employeeId = tripIdToEmployeeIdMap.get(timeSlot.getParentId());
            if (employeeId != null) {
                result.computeIfAbsent(employeeId, k -> new ArrayList<>()).add(timeSlot);
            }
        }

        return result;
    }

    /**
     * 获取指定时间段可用的临时员工
     */
    private List<Map<String, Object>> getAvailableTempStaffForTimeSlot(
        List<Map<String, Object>> staffList,
        List<LocalDate> dateRange,
        Map<String, List<SchedulingLeave>> leaveMap) {

        return staffList.stream()
            .filter(staff -> {
                String staffId = staff.get("id").toString();
                // 检查是否请假
                if (leaveMap.containsKey(staffId)) {
                    for (SchedulingLeave leave : leaveMap.get(staffId)) {
                        LocalDate leaveStartDate = LocalDate.parse(leave.getStartTime().split(" ")[0]);
                        LocalDate leaveEndDate = LocalDate.parse(leave.getEndTime().split(" ")[0]);
                        if (!dateRange.contains(leaveStartDate) && !dateRange.contains(leaveEndDate)) {
                            return false;
                        }
                    }
                }
                return true;
            })
            .collect(Collectors.toList());
    }

    private List<LocalDate> generateDateRange(String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startTime, formatter);
        LocalDate end = LocalDate.parse(endTime, formatter);
        List<LocalDate> dates = new ArrayList<>();
        while (!start.isAfter(end)) {
            dates.add(start);
            start = start.plusDays(1);
        }
        return dates;
    }

    @Override
    public void deleteSchedulingByIds(InputObject inputObject, OutputObject outputObject) {
        String ids = inputObject.getParams().get("ids").toString();
        List<String> idList = Arrays.asList(ids.split(CommonCharConstants.COMMA_MARK));
        List<Scheduling> schedulingList = querySchedulingByIdList(idList);
        for (Scheduling scheduling : schedulingList) {
            assertSchedulingEditable(scheduling);
        }
        deleteById(idList);
    }

    @Override
    public void publishSchedulingById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        if (StrUtil.isBlank(id)) {
            throw new CustomException("排班id不能为空");
        }
        Scheduling scheduling = selectById(id);
        if (scheduling == null) {
            throw new CustomException("排班记录不存在");
        }
        if (isPublished(scheduling)) {
            throw new CustomException("该排班已发布，无需重复发布");
        }
        List<SchedulingTime> schedulingTimes = schedulingTimeService.querySchedulingTimeBySchedulingId(id);
        if (CollectionUtil.isEmpty(schedulingTimes)) {
            throw new CustomException("请先完善排班后再发布");
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Scheduling update = new Scheduling();
        update.setId(id);
        update.setPublishState(SchedulePublishState.PUBLISHED.getKey());
        DataCommonUtil.setCommonLastUpdateDataByGenericity(update, userId);
        updateById(update);
        outputObject.setBean(selectById(id));
    }

    private boolean isPublished(Scheduling scheduling) {
        if (scheduling == null) {
            return false;
        }
        return SchedulePublishState.PUBLISHED.getKey().equals(scheduling.getPublishState());
    }

    /**
     * 个人考勤、月历、定时缺卡结算等场景仅使用已发布排班。
     */
    private void applyPublishedSchedulingCondition(QueryWrapper<Scheduling> wrapper) {
        wrapper.eq(MybatisPlusUtil.toColumns(Scheduling::getPublishState), SchedulePublishState.PUBLISHED.getKey());
    }

    private void assertSchedulingEditable(Scheduling scheduling) {
        if (isPublished(scheduling)) {
            throw new CustomException("该排班已发布，不允许编辑或删除");
        }
    }

    @Override
    public void querySchedulingList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String holderId = commonPageInfo.getHolderId();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotEmpty(holderId), MybatisPlusUtil.toColumns(Scheduling::getFarmId), holderId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Scheduling::getCreateTime));
        List<Scheduling> schedulingList = list(queryWrapper);
        List<String> collect = schedulingList.stream().map(Scheduling::getShiftId).collect(Collectors.toList());
        List<SchedulingShifts> schedulingShifts = schedulingShiftsService.querySchedulingShiftsByIds(collect);
        Map<String, List<SchedulingShifts>> collect1 = schedulingShifts.stream().collect(Collectors.groupingBy(SchedulingShifts::getId));
        for (Scheduling scheduling : schedulingList) {
            if (scheduling != null && scheduling.getShiftId() != null) {
                List<SchedulingShifts> schedulingShifts1 = collect1.get(scheduling.getShiftId());
                if (CollectionUtil.isNotEmpty(schedulingShifts1)) {
                    SchedulingShifts schedulingShifts2 = schedulingShifts1.get(CommonNumConstants.NUM_ZERO);
                    if (ObjectUtil.isNotEmpty(schedulingShifts2)) {
                        scheduling.setShiftMation(schedulingShifts2);
                    }
                }
            }
        }
        iAuthUserService.setName(schedulingList, "createId", "createName");
        iAuthUserService.setName(schedulingList, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(schedulingList);
        outputObject.settotal(page.getTotal());
    }

    /**
     * 查询指定考勤日应打卡的排班人员（见 {@link SchedulingService#queryScheduleCheckTargetsForDate}）
     */
    @Override
    public List<Map<String, Object>> queryScheduleCheckTargetsForDate(String checkDate) {
        if (StrUtil.isBlank(checkDate)) {
            return Collections.emptyList();
        }
        // 1. 查出覆盖 checkDate 且已发布的排班计划
        QueryWrapper<Scheduling> schedulingWrapper = new QueryWrapper<>();
        applyPublishedSchedulingCondition(schedulingWrapper);
        schedulingWrapper.le(MybatisPlusUtil.toColumns(Scheduling::getStartTime), checkDate);
        schedulingWrapper.ge(MybatisPlusUtil.toColumns(Scheduling::getEndTime), checkDate);
        List<Scheduling> schedulingList = list(schedulingWrapper);
        if (CollectionUtil.isEmpty(schedulingList)) {
            return Collections.emptyList();
        }
        List<String> schedulingIds = schedulingList.stream().map(Scheduling::getId).collect(Collectors.toList());
        // 2. 排班计划下所有人员-时间段分配
        QueryWrapper<SchedulingTimeWorkPeople> peopleWrapper = new QueryWrapper<>();
        peopleWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingId), schedulingIds);
        List<SchedulingTimeWorkPeople> peopleList = schedulingTimeWorkPeopleService.list(peopleWrapper);
        if (CollectionUtil.isEmpty(peopleList)) {
            return Collections.emptyList();
        }
        List<String> schedulingTimeIds = peopleList.stream()
            .map(SchedulingTimeWorkPeople::getSchedulingTimeId).distinct().collect(Collectors.toList());
        Map<String, SchedulingTime> timeMap = schedulingTimeService.querySchedulingTimeByIds(schedulingTimeIds).stream()
            .collect(Collectors.toMap(SchedulingTime::getId, t -> t, (a, b) -> a));
        // 3. employeeId(staffId) → userId，供 check_work.create_id 使用
        List<String> staffIds = peopleList.stream().map(SchedulingTimeWorkPeople::getEmployeeId).distinct().collect(Collectors.toList());
        List<Map<String, Object>> staffList = iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(staffIds));
        Map<String, String> staffIdToUserId = new HashMap<>();
        if (CollectionUtil.isNotEmpty(staffList)) {
            for (Map<String, Object> staff : staffList) {
                if (staff.get("userId") != null && staff.get("id") != null) {
                    staffIdToUserId.put(staff.get("id").toString(), staff.get("userId").toString());
                }
            }
        }
        Set<String> dedupeKeys = new HashSet<>();
        List<Map<String, Object>> result = new ArrayList<>();
        for (SchedulingTimeWorkPeople people : peopleList) {
            // 同一员工同一排班时间段只结算一次
            String dedupeKey = people.getEmployeeId() + "_" + people.getSchedulingTimeId();
            if (!dedupeKeys.add(dedupeKey)) {
                continue;
            }
            SchedulingTime schedulingTime = timeMap.get(people.getSchedulingTimeId());
            String userId = staffIdToUserId.get(people.getEmployeeId());
            if (schedulingTime == null || StrUtil.isBlank(userId)) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("userId", userId);
            item.put("schedulingTimeId", schedulingTime.getId());
            item.put("startTime", schedulingTime.getStartTime());
            item.put("endTime", schedulingTime.getEndTime());
            item.put("isNextDay", schedulingTime.getIsNextDay());
            result.add(item);
        }
        return result;
    }

    @Override
    public boolean isStaffScheduledForShiftOnDate(String staffId, String shiftId, String day) {
        if (StrUtil.hasBlank(staffId, shiftId, day)) {
            return false;
        }
        LocalDate targetDay;
        try {
            targetDay = LocalDate.parse(day);
        } catch (DateTimeParseException e) {
            return false;
        }
        QueryWrapper<Scheduling> schedulingWrapper = new QueryWrapper<>();
        schedulingWrapper.eq(MybatisPlusUtil.toColumns(Scheduling::getShiftId), shiftId);
        applyPublishedSchedulingCondition(schedulingWrapper);
        List<Scheduling> schedulingList = list(schedulingWrapper);
        if (CollectionUtil.isEmpty(schedulingList)) {
            return false;
        }
        List<String> schedulingIds = schedulingList.stream()
            .filter(scheduling -> isDateInSchedulingRange(targetDay, scheduling.getStartTime(), scheduling.getEndTime()))
            .map(Scheduling::getId)
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(schedulingIds)) {
            return false;
        }
        List<SchedulingTimeWorkPeople> people = schedulingTimeWorkPeopleService
            .querySchedulingByschedulingIdsAndStaffId(schedulingIds, staffId);
        return CollectionUtil.isNotEmpty(people);
    }

    private boolean isDateInSchedulingRange(LocalDate targetDay, String startTime, String endTime) {
        LocalDate start = parseDateTime(startTime).toLocalDate();
        LocalDate end = parseDateTime(endTime).toLocalDate();
        return !targetDay.isBefore(start) && !targetDay.isAfter(end);
    }

    @Override
    protected void deletePreExecution(List<String> ids) {
        schedulingTimeService.deleteBySchedulingIds(ids);
    }
}
