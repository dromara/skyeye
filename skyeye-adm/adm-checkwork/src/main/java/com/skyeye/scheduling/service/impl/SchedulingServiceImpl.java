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
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.leave.entity.Leave;
import com.skyeye.leave.entity.LeaveTimeSlot;
import com.skyeye.leave.service.LeaveService;
import com.skyeye.leave.service.LeaveTimeSlotService;
import com.skyeye.scheduling.dao.SchedulingDao;
import com.skyeye.scheduling.entity.*;
import com.skyeye.scheduling.service.*;
import com.skyeye.trip.entity.BusinessTrip;
import com.skyeye.trip.entity.BusinessTripTimeSlot;
import com.skyeye.trip.service.BusinessTripService;
import com.skyeye.trip.service.BusinessTripTimeSlotService;
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

    @Override
    protected void createPrepose(Scheduling entity) {
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

        for (SchedulingTime schedulingTime : timeList) {
            String timeKey = schedulingTime.getStartTime() + "-" + schedulingTime.getEndTime();
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
                        boolean timeOverlap = isTimeOverlap(newStart, newEnd, existStart, existEnd);
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

    @Override
    public void querySchedulingByStaffId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 班次名称
        String staffId = InputObject.getLogParamsStatic().get("staffId").toString();
        // 1. 查询指定时间范围内的排班记录
        QueryWrapper<Scheduling> schedulingWrapper = new QueryWrapper<>();
        List<Scheduling> schedulingList = list(schedulingWrapper);
        String keyword = commonPageInfo.getKeyword();
        if (StrUtil.isNotEmpty(keyword)) {
            List<String> shiftIdList = schedulingList.stream().map(Scheduling::getShiftId).collect(Collectors.toList());
            List<SchedulingShifts> schedulingShifts = schedulingShiftsService.querySchedulingShiftsByIdName(shiftIdList, keyword);
            List<String> schedulingShiftIdList = schedulingShifts.stream().map(SchedulingShifts::getId).collect(Collectors.toList());
            schedulingList = schedulingList.stream()
                .filter(scheduling -> schedulingShiftIdList.contains(scheduling.getShiftId()))
                .collect(Collectors.toList());
        }

        if (CollectionUtil.isEmpty(schedulingList)) {
            outputObject.setBean(new ArrayList<>());
            outputObject.settotal(CommonNumConstants.NUM_ZERO);
            return;
        }

        List<String> schedulingIds = schedulingList.stream().map(Scheduling::getId).collect(Collectors.toList());
        List<SchedulingTimeWorkPeople> timeWorkPeople = schedulingTimeWorkPeopleService.querySchedulingByschedulingIdsAndStaffId(schedulingIds, staffId);
        if (CollectionUtil.isEmpty(timeWorkPeople)) {
            return;
        }
        getStaffMation(timeWorkPeople);
        iAuthUserService.setName(timeWorkPeople, "createId", "createName");
        iAuthUserService.setName(timeWorkPeople, "lastUpdateId", "lastUpdateName");
        // 获取所有相关的ID
        List<String> workIds = timeWorkPeople.stream()
            .map(SchedulingTimeWorkPeople::getSchedulingTimeWorkId).collect(Collectors.toList());
        List<String> timeIds = timeWorkPeople.stream()
            .map(SchedulingTimeWorkPeople::getSchedulingTimeId).collect(Collectors.toList());
        List<SchedulingTimeWork> workList = schedulingTimeWorkService.querySchedulingTimeByIds(workIds);
        List<SchedulingTime> timeList = schedulingTimeService.querySchedulingTimeByTimeIds(timeIds);

        // 构建四层嵌套结构
        List<Scheduling> resultList = new ArrayList<>();
        for (Scheduling scheduling : schedulingList) {
            if (!schedulingIds.contains(scheduling.getId())) {
                continue;
            }
            // 获取当前排班下的时间段
            List<SchedulingTime> filteredTimeList = timeList.stream()
                .filter(time -> time.getSchedulingId().equals(scheduling.getId()))
                .collect(Collectors.toList());
            for (SchedulingTime time : filteredTimeList) {
                // 获取当前时间段下的工位
                List<SchedulingTimeWork> filteredWorkList = workList.stream()
                    .filter(work -> work.getSchedulingTimeId().equals(time.getId()))
                    .collect(Collectors.toList());

                for (SchedulingTimeWork work : filteredWorkList) {
                    // 获取当前工位下的员工
                    List<SchedulingTimeWorkPeople> filteredPeopleList = timeWorkPeople.stream()
                        .filter(people -> people.getSchedulingTimeWorkId().equals(work.getId()))
                        .collect(Collectors.toList());
                    work.setSchedulingTimeWorkPeopleMation(filteredPeopleList);
                }
                time.setSchedulingTimeWorkMation(filteredWorkList);
            }
            scheduling.setSchedulingTimeMation(filteredTimeList);
            resultList.add(scheduling);
        }
        List<String> shiftIdList = resultList.stream().map(Scheduling::getShiftId).collect(Collectors.toList());
        Map<String, List<SchedulingShifts>> stringListMap = schedulingShiftsService.querySchedulingShiftsByIds(shiftIdList).stream().collect(Collectors.groupingBy(SchedulingShifts::getId));
        resultList.forEach(scheduling -> {
            scheduling.setShiftMation(stringListMap.get(scheduling.getShiftId()).get(CommonNumConstants.NUM_ZERO));
        });
        outputObject.setBeans(resultList);
        outputObject.settotal(page.getTotal());
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
        QueryWrapper<Scheduling> schedulingWrapper = new QueryWrapper<>();
        mouthList.forEach(month -> {
            String monthPattern = month + "%";
            schedulingWrapper.or(wrap -> wrap
                .like(MybatisPlusUtil.toColumns(Scheduling::getStartTime), monthPattern)
                .or()
                .like(MybatisPlusUtil.toColumns(Scheduling::getEndTime), monthPattern)
            );
        });
        List<Scheduling> schedulingList = list(schedulingWrapper);

        List<String> schedulingIds = schedulingList.stream().map(Scheduling::getId).collect(Collectors.toList());
        List<SchedulingTimeWorkPeople> timeWorkPeople = schedulingTimeWorkPeopleService.querySchedulingByschedulingIdsAndStaffId(schedulingIds, staffId);

        if (CollectionUtil.isEmpty(timeWorkPeople)) {
            return new ArrayList<>();
        }

        List<Scheduling> filteredSchedulingList = schedulingList.stream()
            .filter(scheduling -> timeWorkPeople.stream()
                .anyMatch(people -> people.getSchedulingId().equals(scheduling.getId()) && people.getEmployeeId().equals(staffId)))
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
        List<String> sortedDates = allDates.stream().sorted()
            .map(LocalDate::toString)
            .collect(Collectors.toList());
        return sortedDates;
    }

    @Override
    public void querySchedulingByStaffIdAndOneDay(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String staffId = map.get("staffId").toString();
        QueryWrapper<Scheduling> schedulingWrapper = new QueryWrapper<>();
        List<Scheduling> schedulingList = list(schedulingWrapper);
        List<String> schedulingIds = schedulingList.stream().map(Scheduling::getId).collect(Collectors.toList());
        List<SchedulingTimeWorkPeople> timeWorkPeople = schedulingTimeWorkPeopleService.querySchedulingByschedulingIdsAndStaffId(schedulingIds, staffId);
        if (CollectionUtil.isEmpty(timeWorkPeople)) {
            return;
        }
        List<String> schedulingTimeList = timeWorkPeople.stream().map(SchedulingTimeWorkPeople::getSchedulingTimeId).collect(Collectors.toList());
        List<SchedulingTime> schedulingTimes = schedulingTimeService.querySchedulingTimeByIds(schedulingTimeList);
        Map<String, List<SchedulingTime>> timeMap = schedulingTimes.stream()
            .collect(Collectors.groupingBy(SchedulingTime::getSchedulingId));
        Set<SchedulingTime> timeSegments = new HashSet<>();
        for (Scheduling scheduling : schedulingList) {
            List<SchedulingTime> times = timeMap.getOrDefault(scheduling.getId(), Collections.emptyList());
            for (SchedulingTime time : times) {
                if (timeWorkPeople.stream()
                    .anyMatch(p -> p.getSchedulingTimeId().equals(time.getId()))) {
                    timeSegments.add(time);
                }
            }
        }
        outputObject.setBeans(new ArrayList<>(timeSegments));
        outputObject.settotal(timeSegments.size());
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
     * 获取指定时间段可用的正式员工
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
                                if (isDateTimeOverlap(shiftStartTime, shiftEndTime, d, leaveStart, leaveEnd)) {
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
                            if (isTimeOverlap(shiftStartTime, shiftEndTime, tripStartTime, tripEndTime)) {
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
     * 检查两个时间段是否重叠
     */
    private boolean isTimeOverlap(String start1, String end1, String start2, String end2) {
        // 处理时间格式，确保小时是两位数
        start1 = start1.length() == 7 ? "0" + start1 : start1;
        end1 = end1.length() == 7 ? "0" + end1 : end1;
        start2 = start2.length() == 7 ? "0" + start2 : start2;
        end2 = end2.length() == 7 ? "0" + end2 : end2;

        // 如果时间包含秒，则使用 HH:mm:ss 格式，否则使用 HH:mm 格式
        DateTimeFormatter formatter = start1.length() > 5 ?
            DateTimeFormatter.ofPattern("HH:mm:ss") :
            DateTimeFormatter.ofPattern("HH:mm");

        LocalTime s1 = LocalTime.parse(start1, formatter);
        LocalTime e1 = LocalTime.parse(end1, formatter);
        LocalTime s2 = LocalTime.parse(start2, formatter);
        LocalTime e2 = LocalTime.parse(end2, formatter);

        // 检查时间段是否重叠
        return !(e1.isBefore(s2) || s1.isAfter(e2));
    }

    /**
     * 检查排班时间段与请假时间段（支持跨天）是否重叠
     */
    private boolean isDateTimeOverlap(String shiftStartStr, String shiftEndStr, LocalDate day,
                                      LocalDateTime leaveStart, LocalDateTime leaveEnd) {
        if (StrUtil.isEmpty(shiftStartStr) || StrUtil.isEmpty(shiftEndStr)) {
            return false;
        }
        String s1 = shiftStartStr.length() == 7 ? "0" + shiftStartStr : shiftStartStr;
        String e1 = shiftEndStr.length() == 7 ? "0" + shiftEndStr : shiftEndStr;
        DateTimeFormatter formatter = s1.length() > 5 ? DateTimeFormatter.ofPattern("HH:mm:ss") : DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime shiftStart = day.atTime(LocalTime.parse(s1, formatter));
        LocalDateTime shiftEnd = day.atTime(LocalTime.parse(e1, formatter));
        LocalDateTime leaveOnDayStart = leaveStart.toLocalDate().equals(day) ? leaveStart : day.atStartOfDay();
        LocalDateTime leaveOnDayEnd = leaveEnd.toLocalDate().equals(day) ? leaveEnd : day.atTime(23, 59, 59);
        return !shiftEnd.isBefore(leaveOnDayStart) && !shiftStart.isAfter(leaveOnDayEnd);
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
        deleteById(idList);
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
        // 1. 查出覆盖 checkDate 的排班计划
        QueryWrapper<Scheduling> schedulingWrapper = new QueryWrapper<>();
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
    protected void deletePreExecution(List<String> ids) {
        schedulingTimeService.deleteBySchedulingIds(ids);
    }
}
