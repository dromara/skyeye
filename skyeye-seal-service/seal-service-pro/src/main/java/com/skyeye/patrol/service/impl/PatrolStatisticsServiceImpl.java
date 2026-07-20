/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.patrol.classenum.PatrolItemSummaryType;
import com.skyeye.patrol.classenum.PatrolTaskState;
import com.skyeye.patrol.dao.PatrolRecordDao;
import com.skyeye.patrol.dao.PatrolTaskDao;
import com.skyeye.patrol.entity.PatrolItem;
import com.skyeye.patrol.entity.PatrolPlan;
import com.skyeye.patrol.entity.PatrolPoint;
import com.skyeye.patrol.entity.PatrolRecord;
import com.skyeye.patrol.entity.PatrolTask;
import com.skyeye.patrol.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: PatrolStatisticsServiceImpl
 * @Description: 巡检统计服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class PatrolStatisticsServiceImpl implements PatrolStatisticsService {

    /**
     * 空值或缺失数据归类显示名称
     */
    private static final String OTHER_LABEL = "其他";

    @Autowired
    private PatrolTaskDao patrolTaskDao;

    @Autowired
    private PatrolTaskService patrolTaskService;

    @Autowired
    private PatrolRecordDao patrolRecordDao;

    @Autowired
    private PatrolPlanService patrolPlanService;

    @Autowired
    private PatrolItemService patrolItemService;

    @Autowired
    private PatrolTeamService patrolTeamService;

    @Autowired
    private PatrolPointService patrolPointService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public void queryTaskCompletionStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<PatrolTask> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), endTime);
        }

        // 总数
        long total = patrolTaskDao.selectCount(queryWrapper);

        // 按状态统计，state 为空归「其他」
        Map<Integer, Long> stateCountMap = new HashMap<>();
        List<PatrolTask> taskList = patrolTaskDao.selectList(queryWrapper);
        for (PatrolTask task : taskList) {
            Integer state = task.getState();
            stateCountMap.put(state, stateCountMap.getOrDefault(state, 0L) + 1);
        }

        PatrolTaskState[] stateOrder = PatrolTaskState.values();
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (PatrolTaskState state : stateOrder) {
            xAxisData.add(state.getValue());
            seriesData.add(stateCountMap.getOrDefault(state.getKey(), 0L));
        }
        Set<Integer> knownKeys = Arrays.stream(stateOrder).map(PatrolTaskState::getKey).collect(Collectors.toSet());
        long otherCount = stateCountMap.entrySet().stream()
            .filter(e -> e.getKey() == null || !knownKeys.contains(e.getKey()))
            .mapToLong(Map.Entry::getValue)
            .sum();
        if (otherCount > 0) {
            xAxisData.add(OTHER_LABEL);
            seriesData.add(otherCount);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryAbnormalStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<PatrolRecord> queryWrapper = new QueryWrapper<>();
        // 只统计已审批通过的记录
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolRecord::getState), FlowableStateEnum.PASS.getKey());
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolRecord::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(PatrolRecord::getCreateTime), endTime);
        }

        List<PatrolRecord> recordList = patrolRecordDao.selectList(queryWrapper);
        long total = recordList.size();

        // 按检查结果统计，checkResult 为空或未知归「其他」
        Map<Integer, Long> resultCountMap = new HashMap<>();
        for (PatrolRecord record : recordList) {
            Integer checkResult = record.getCheckResult();
            resultCountMap.put(checkResult, resultCountMap.getOrDefault(checkResult, 0L) + 1);
        }

        PatrolItemSummaryType[] typeOrder = PatrolItemSummaryType.values();
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (PatrolItemSummaryType type : typeOrder) {
            xAxisData.add(type.getValue());
            seriesData.add(resultCountMap.getOrDefault(type.getKey(), 0L));
        }
        Set<Integer> knownKeys = Arrays.stream(typeOrder).map(PatrolItemSummaryType::getKey).collect(Collectors.toSet());
        long otherCount = resultCountMap.entrySet().stream()
            .filter(e -> e.getKey() == null || !knownKeys.contains(e.getKey()))
            .mapToLong(Map.Entry::getValue)
            .sum();
        if (otherCount > 0) {
            xAxisData.add(OTHER_LABEL);
            seriesData.add(otherCount);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryStatsByTeam(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<PatrolTask> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), endTime);
        }

        List<PatrolTask> taskList = patrolTaskDao.selectList(queryWrapper);
        long total = taskList.size();

        Set<String> planIds = taskList.stream()
            .filter(task -> StrUtil.isNotEmpty(task.getPlanId()))
            .map(PatrolTask::getPlanId)
            .collect(Collectors.toSet());

        if (CollectionUtil.isEmpty(planIds)) {
            Map<String, Object> result = new HashMap<>();
            result.put("total", total);
            result.put("xAxisData", new ArrayList<>());
            result.put("seriesData", new ArrayList<>());
            outputObject.setBean(result);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
            return;
        }

        List<PatrolPlan> planList = patrolPlanService.selectByIds(planIds.toArray(new String[]{}));
        patrolTeamService.setDataMation(planList, PatrolPlan::getTeamId);

        // planId -> teamId，teamId 为空归「其他」
        Map<String, String> planTeamMap = new HashMap<>();
        for (PatrolPlan plan : planList) {
            if (plan == null) continue;
            String tid = StrUtil.isNotEmpty(plan.getTeamId()) ? plan.getTeamId() : OTHER_LABEL;
            planTeamMap.put(plan.getId(), tid);
        }

        // 按 teamId 分组统计（空归其他）
        Map<String, Long> teamStats = new HashMap<>();
        for (PatrolTask task : taskList) {
            String teamId = planTeamMap.get(task.getPlanId());
            if (StrUtil.isEmpty(teamId)) {
                teamId = OTHER_LABEL;
            }
            teamStats.put(teamId, teamStats.getOrDefault(teamId, 0L) + 1);
        }

        // teamId -> 班组名称（优先 teamMation.name）
        Map<String, String> teamIdToName = new HashMap<>();
        teamIdToName.put(OTHER_LABEL, OTHER_LABEL);
        for (PatrolPlan plan : planList) {
            if (plan == null || StrUtil.isEmpty(plan.getTeamId()) || OTHER_LABEL.equals(plan.getTeamId())) continue;
            if (teamIdToName.containsKey(plan.getTeamId())) continue;
            String name = plan.getTeamMation() != null && StrUtil.isNotEmpty(plan.getTeamMation().getName())
                ? plan.getTeamMation().getName()
                : plan.getTeamId();
            teamIdToName.put(plan.getTeamId(), name);
        }

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : teamStats.entrySet()) {
            xAxisData.add(teamIdToName.getOrDefault(entry.getKey(), entry.getKey()));
            seriesData.add(entry.getValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryStatsByPoint(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<PatrolTask> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), endTime);
        }

        List<PatrolTask> taskList = patrolTaskDao.selectList(queryWrapper);
        long total = taskList.size();
        List<String> taskIds = taskList.stream().map(PatrolTask::getId).collect(Collectors.toList());
        taskList = patrolTaskService.selectByIds(taskIds.toArray(new String[0]));

        Set<String> pointIds = taskList.stream()
            .filter(task -> CollectionUtil.isNotEmpty(task.getPointId()))
            .flatMap(task -> task.getPointId().stream())
            .filter(StrUtil::isNotEmpty)
            .collect(Collectors.toSet());
        Map<String, PatrolPoint> pointMap = patrolPointService.selectByIds(pointIds.toArray(new String[0])).stream()
            .collect(Collectors.toMap(PatrolPoint::getId, p -> p, (a, b) -> a));

        // 按任务上的巡检点位分组（多点位展开；无效归「其他」）
        Map<String, Long> pointStats = new HashMap<>();
        for (PatrolTask task : taskList) {
            // 没有点位 → 记到「其他」
            if (CollectionUtil.isEmpty(task.getPointId())) {
                pointStats.put(OTHER_LABEL, pointStats.getOrDefault(OTHER_LABEL, 0L) + 1);
                continue;
            }
            // 任务上每个点位各计 1 次
            for (String pointId : task.getPointId()) {
                PatrolPoint point = pointMap.get(pointId);
                if (point == null || StrUtil.isEmpty(point.getName())) {
                    pointStats.put(OTHER_LABEL, pointStats.getOrDefault(OTHER_LABEL, 0L) + 1);
                    continue;
                }
                pointStats.put(pointId, pointStats.getOrDefault(pointId, 0L) + 1);
            }
        }

        Map<String, String> pointIdToName = new HashMap<>();
        pointIdToName.put(OTHER_LABEL, OTHER_LABEL);
        for (PatrolTask task : taskList) {
            if (CollectionUtil.isEmpty(task.getPointId())) {
                continue;
            }
            for (String pointId : task.getPointId()) {
                if (pointIdToName.containsKey(pointId)) {
                    continue;
                }
                PatrolPoint point = pointMap.get(pointId);
                if (point == null) {
                    continue;
                }
                String name = StrUtil.isNotEmpty(point.getName())
                    ? point.getName()
                    : (StrUtil.isNotEmpty(point.getPointCode()) ? point.getPointCode() : pointId);
                pointIdToName.put(pointId, name);
            }
        }

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : pointStats.entrySet()) {
            xAxisData.add(pointIdToName.getOrDefault(entry.getKey(), entry.getKey()));
            seriesData.add(entry.getValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryStatsByItem(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<PatrolRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolRecord::getState), FlowableStateEnum.PASS.getKey());
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolRecord::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(PatrolRecord::getCreateTime), endTime);
        }

        List<PatrolRecord> recordList = patrolRecordDao.selectList(queryWrapper);
        List<String> taskIds = recordList.stream()
            .map(PatrolRecord::getTaskId)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        List<PatrolTask> tasks = patrolTaskService.selectByIds(taskIds.toArray(new String[0]));
        Map<String, PatrolTask> taskMap = tasks.stream()
            .collect(Collectors.toMap(PatrolTask::getId, t -> t, (a, b) -> a));

        Set<String> itemIds = tasks.stream()
            .filter(task -> CollectionUtil.isNotEmpty(task.getItemId()))
            .flatMap(task -> task.getItemId().stream())
            .filter(StrUtil::isNotEmpty)
            .collect(Collectors.toSet());
        Map<String, PatrolItem> itemMap = patrolItemService.selectByIds(itemIds.toArray(new String[0])).stream()
            .collect(Collectors.toMap(PatrolItem::getId, i -> i, (a, b) -> a));

        long total = recordList.size();
        // 按关联任务上的巡检项目分组（多项目展开；无效归「其他」）
        Map<String, Long> itemStats = new HashMap<>();
        for (PatrolRecord record : recordList) {
            PatrolTask task = taskMap.get(record.getTaskId());
            // 找不到任务或没有项目 → 记到「其他」
            if (task == null || CollectionUtil.isEmpty(task.getItemId())) {
                itemStats.put(OTHER_LABEL, itemStats.getOrDefault(OTHER_LABEL, 0L) + 1);
                continue;
            }
            // 任务上每个项目各计 1 次
            for (String itemId : task.getItemId()) {
                PatrolItem item = itemMap.get(itemId);
                if (item == null || StrUtil.isEmpty(item.getName())) {
                    itemStats.put(OTHER_LABEL, itemStats.getOrDefault(OTHER_LABEL, 0L) + 1);
                    continue;
                }
                itemStats.put(itemId, itemStats.getOrDefault(itemId, 0L) + 1);
            }
        }

        Map<String, String> itemIdToName = new HashMap<>();
        itemIdToName.put(OTHER_LABEL, OTHER_LABEL);
        for (PatrolTask task : tasks) {
            if (CollectionUtil.isEmpty(task.getItemId())) {
                continue;
            }
            for (String itemId : task.getItemId()) {
                if (itemIdToName.containsKey(itemId)) {
                    continue;
                }
                PatrolItem item = itemMap.get(itemId);
                if (item == null || StrUtil.isEmpty(item.getName())) {
                    continue;
                }
                itemIdToName.put(itemId, item.getName());
            }
        }

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : itemStats.entrySet()) {
            xAxisData.add(itemIdToName.getOrDefault(entry.getKey(), entry.getKey()));
            seriesData.add(entry.getValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryStatsByExecutor(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<PatrolTask> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), endTime);
        }

        List<PatrolTask> taskList = patrolTaskDao.selectList(queryWrapper);
        long total = taskList.size();

        // 收集执行人ID并获取执行人名称
        List<String> executorIds = taskList.stream()
            .map(PatrolTask::getExecutorId)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        Map<String, String> executorIdToName = new HashMap<>();
        executorIdToName.put(OTHER_LABEL, OTHER_LABEL);
        if (CollectionUtil.isNotEmpty(executorIds)) {
            Map<String, Map<String, Object>> executorMap = iAuthUserService.queryUserMationListByStaffIds(executorIds);
            executorMap.forEach((id, mation) -> {
                executorIdToName.put(id, mation.get("name").toString());
            });
        }

        // 有有效执行人的按 executorId 分组，executorId 为空或查不到执行人的归为「其他」
        Map<String, Long> executorStats = taskList.stream()
            .collect(Collectors.groupingBy(task -> {
                if (StrUtil.isEmpty(task.getExecutorId())) {
                    return OTHER_LABEL;
                }
                return executorIdToName.containsKey(task.getExecutorId()) ? task.getExecutorId() : OTHER_LABEL;
            }, Collectors.counting()));

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : executorStats.entrySet()) {
            xAxisData.add(executorIdToName.getOrDefault(entry.getKey(), entry.getKey()));
            seriesData.add(entry.getValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryTimeTrendStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime, endTime;
        if (StrUtil.isNotEmpty(tableSelectInfo.getStartTime()) && StrUtil.isNotEmpty(tableSelectInfo.getEndTime())) {
            startTime = tableSelectInfo.getStartTime();
            endTime = tableSelectInfo.getEndTime();
        } else {
            startTime = DateUtil.formatDate2Str(DateUtil.getAfDate(DateUtil.getPointTime(DateUtil.getYmdTimeAndToString(), DateUtil.YYYY_MM_DD), -30, "d"),
                DateUtil.YYYY_MM_DD);
            endTime = DateUtil.getYmdTimeAndToString();
        }

        List<String> dayList = DateUtil.getDays(startTime, endTime);

        QueryWrapper<PatrolTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), startTime)
            .le(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), endTime);

        List<PatrolTask> taskList = patrolTaskDao.selectList(queryWrapper);
        Map<String, Long> taskCountMap = taskList.stream().collect(Collectors.groupingBy(task -> {
            Date pointTime = DateUtil.getPointTime(task.getCreateTime(), DateUtil.YYYY_MM_DD);
            return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM_DD);
        }, Collectors.counting()));

        // 已完成任务统计
        Map<String, Long> completedCountMap = taskList.stream()
            .filter(task -> PatrolTaskState.COMPLETED.getKey().equals(task.getState()))
            .collect(Collectors.groupingBy(task -> {
                Date pointTime = DateUtil.getPointTime(task.getCreateTime(), DateUtil.YYYY_MM_DD);
                return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM_DD);
            }, Collectors.counting()));

        Map<String, Object> result = new HashMap<>();
        List<Long> allTasks = new ArrayList<>();
        List<Long> completedTasks = new ArrayList<>();
        Long defaultValue = Long.valueOf(CommonNumConstants.NUM_ZERO);

        for (String day : dayList) {
            allTasks.add(taskCountMap.getOrDefault(day, defaultValue));
            completedTasks.add(completedCountMap.getOrDefault(day, defaultValue));
        }

        result.put("allTasks", allTasks);
        result.put("completedTasks", completedTasks);
        result.put("dayList", dayList);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryCompletionRateStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<PatrolTask> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), endTime);
        }

        List<PatrolTask> taskList = patrolTaskDao.selectList(queryWrapper);
        long total = taskList.size();
        long completed = taskList.stream()
            .filter(task -> PatrolTaskState.COMPLETED.getKey().equals(task.getState()))
            .count();

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("completed", completed);
        if (total > 0) {
            result.put("completionRate", CalculationUtil.divide(String.valueOf(completed), String.valueOf(total), CommonNumConstants.NUM_TWO));
        } else {
            result.put("completionRate", CommonNumConstants.NUM_ZERO);
        }

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

}

