/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.patrol.classenum.PatrolTaskState;
import com.skyeye.patrol.dao.PatrolTaskDao;
import com.skyeye.patrol.entity.PatrolItem;
import com.skyeye.patrol.entity.PatrolPoint;
import com.skyeye.patrol.entity.PatrolTask;
import com.skyeye.patrol.service.PatrolItemService;
import com.skyeye.patrol.service.PatrolPlanService;
import com.skyeye.patrol.service.PatrolPointService;
import com.skyeye.patrol.service.PatrolTaskItemService;
import com.skyeye.patrol.service.PatrolTaskPointService;
import com.skyeye.patrol.service.PatrolTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PatrolTaskServiceImpl
 * @Description: 巡检任务服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "巡检任务", groupName = "巡检任务")
public class PatrolTaskServiceImpl extends SkyeyeBusinessServiceImpl<PatrolTaskDao, PatrolTask> implements PatrolTaskService {

    @Autowired
    private PatrolPlanService patrolPlanService;

    @Autowired
    private PatrolPointService patrolPointService;

    @Autowired
    private PatrolItemService patrolItemService;

    @Autowired
    private PatrolTaskPointService patrolTaskPointService;

    @Autowired
    private PatrolTaskItemService patrolTaskItemService;

    @Override
    public void createPrepose(PatrolTask entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
        // 新创建的任务默认为待执行状态
        if (entity.getState() == null) {
            entity.setState(PatrolTaskState.PENDING.getKey());
        }
    }

    @Override
    protected void createPrepose(List<PatrolTask> list) {
        list.forEach(patrolTask -> {
            Map<String, Object> business = BeanUtil.beanToMap(patrolTask);
            String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
            patrolTask.setOddNumber(oddNumber);
            // 新创建的任务默认为待执行状态
            if (patrolTask.getState() == null) {
                patrolTask.setState(PatrolTaskState.PENDING.getKey());
            }
        });
    }

    @Override
    protected void updatePrepose(PatrolTask entity) {
        PatrolTask oldPatrolTask = selectById(entity.getId());
        if (PatrolTaskState.CANCELLED.getKey().equals(oldPatrolTask.getState())) {
            // 编辑时，已取消状态的修改为待执行
            entity.setState(PatrolTaskState.PENDING.getKey());
        }
    }

    @Override
    public void writePostpose(PatrolTask entity, String userId) {
        patrolTaskPointService.saveList(entity.getId(), entity.getPointId());
        patrolTaskItemService.saveList(entity.getId(), entity.getItemId());
        super.writePostpose(entity, userId);
    }

    @Override
    protected void createPostpose(List<PatrolTask> entityList, String userId) {
        if (CollectionUtil.isEmpty(entityList)) {
            return;
        }
        entityList.forEach(entity -> {
            patrolTaskPointService.saveList(entity.getId(), entity.getPointId());
            patrolTaskItemService.saveList(entity.getId(), entity.getItemId());
        });
    }

    @Override
    protected void deletePostpose(PatrolTask entity) {
        patrolTaskPointService.deleteByParentId(entity.getId());
        patrolTaskItemService.deleteByParentId(entity.getId());
    }

    @Override
    public PatrolTask getDataFromDb(String id) {
        PatrolTask patrolTask = super.getDataFromDb(id);
        patrolTask.setPointId(patrolTaskPointService.selectByParentId(id));
        patrolTask.setItemId(patrolTaskItemService.selectByParentId(id));
        return patrolTask;
    }

    @Override
    public List<PatrolTask> getDataFromDb(List<String> idList) {
        List<PatrolTask> taskList = super.getDataFromDb(idList);
        if (CollectionUtil.isEmpty(taskList)) {
            return taskList;
        }
        List<String> taskIdList = taskList.stream().map(PatrolTask::getId).collect(Collectors.toList());
        Map<String, List<String>> pointIdMap = patrolTaskPointService.selectMapByParentId(taskIdList);
        Map<String, List<String>> itemIdMap = patrolTaskItemService.selectMapByParentId(taskIdList);
        taskList.forEach(task -> {
            task.setPointId(pointIdMap.getOrDefault(task.getId(), Collections.emptyList()));
            task.setItemId(itemIdMap.getOrDefault(task.getId(), Collections.emptyList()));
        });
        return taskList;
    }

    @Override
    protected QueryWrapper<PatrolTask> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PatrolTask> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolTask::getState), commonPageInfo.getState());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolTask::getPlanId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置计划信息
        patrolPlanService.setMationForMap(beans, "planId", "planMation");
        // 设置点位、项目ID列表
        List<String> taskIds = beans.stream()
            .map(bean -> bean.get("id").toString())
            .collect(Collectors.toList());
        Map<String, List<String>> pointIdMap = patrolTaskPointService.selectMapByParentId(taskIds);
        Map<String, List<String>> itemIdMap = patrolTaskItemService.selectMapByParentId(taskIds);
        List<String> allPointIds = pointIdMap.values().stream()
            .filter(CollectionUtil::isNotEmpty)
            .flatMap(List::stream)
            .distinct()
            .collect(Collectors.toList());
        List<String> allItemIds = itemIdMap.values().stream()
            .filter(CollectionUtil::isNotEmpty)
            .flatMap(List::stream)
            .distinct()
            .collect(Collectors.toList());
        Map<String, PatrolPoint> pointMap = CollectionUtil.isEmpty(allPointIds)
            ? Collections.emptyMap()
            : patrolPointService.selectByIds(allPointIds.toArray(new String[]{})).stream()
            .collect(Collectors.toMap(PatrolPoint::getId, p -> p, (a, b) -> a));
        Map<String, PatrolItem> itemMap = CollectionUtil.isEmpty(allItemIds)
            ? Collections.emptyMap()
            : patrolItemService.selectByIds(allItemIds.toArray(new String[]{})).stream()
            .collect(Collectors.toMap(PatrolItem::getId, i -> i, (a, b) -> a));
        beans.forEach(bean -> {
            String id = bean.get("id").toString();
            List<String> pointIds = pointIdMap.get(id);
            List<String> itemIds = itemIdMap.get(id);
            bean.put("pointId", pointIds);
            bean.put("itemId", itemIds);
            // 关联表无点位/项目时跳过，避免空指针
            if (CollectionUtil.isNotEmpty(pointIds)) {
                bean.put("pointMation", pointIds.stream().map(pointMap::get).collect(Collectors.toList()));
            }
            if (CollectionUtil.isNotEmpty(itemIds)) {
                bean.put("itemMation", itemIds.stream().map(itemMap::get).collect(Collectors.toList()));
            }
        });
        // 设置执行人信息
        List<String> executorIds = beans.stream()
            .filter(bean -> bean.get("executorId") != null)
            .map(bean -> bean.get("executorId").toString())
            .filter(id -> StrUtil.isNotEmpty(id))
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(executorIds)) {
            Map<String, Map<String, Object>> executorMap = iAuthUserService.queryUserMationListByStaffIds(executorIds);
            beans.forEach(bean -> {
                if (bean.get("executorId") != null) {
                    String executorId = bean.get("executorId").toString();
                    bean.put("executorMation", executorMap.get(executorId));
                }
            });
        }
        return beans;
    }

    @Override
    public PatrolTask selectById(String id) {
        PatrolTask patrolTask = super.selectById(id);
        if (patrolTask == null) {
            return null;
        }
        // 设置计划信息
        patrolPlanService.setDataMation(patrolTask, PatrolTask::getPlanId);
        // 设置点位信息
        if (CollectionUtil.isNotEmpty(patrolTask.getPointId())) {
            List<PatrolPoint> points = patrolPointService.selectByIds(patrolTask.getPointId().toArray(new String[]{}));
            patrolTask.setPointMation(points);
        }
        // 设置项目信息
        if (CollectionUtil.isNotEmpty(patrolTask.getItemId())) {
            List<PatrolItem> items = patrolItemService.selectByIds(patrolTask.getItemId().toArray(new String[]{}));
            patrolTask.setItemMation(items);
        }
        // 设置执行人信息
        if (StrUtil.isNotEmpty(patrolTask.getExecutorId())) {
            Map<String, Map<String, Object>> executorMap = iAuthUserService.queryUserMationListByStaffIds(
                java.util.Collections.singletonList(patrolTask.getExecutorId()));
            patrolTask.setExecutorMation(executorMap.get(patrolTask.getExecutorId()));
        }
        return patrolTask;
    }

    @Override
    public void startTask(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        PatrolTask task = selectById(id);
        if (task == null) {
            throw new CustomException("任务不存在");
        }
        if (!PatrolTaskState.PENDING.getKey().equals(task.getState())) {
            throw new CustomException("只有待执行状态的任务才能开始执行");
        }
        String userId = inputObject.getLogParamsStatic().get("id").toString();
        task.setState(PatrolTaskState.IN_PROGRESS.getKey());
        task.setActualStartTime(DateUtil.getTimeAndToString());
        updateEntity(task, userId);
        outputObject.setBean(task);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void completeTask(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        PatrolTask task = selectById(id);
        if (task == null) {
            throw new CustomException("任务不存在");
        }
        if (!PatrolTaskState.IN_PROGRESS.getKey().equals(task.getState())) {
            throw new CustomException("只有执行中状态的任务才能完成");
        }
        String userId = inputObject.getLogParamsStatic().get("id").toString();
        task.setState(PatrolTaskState.COMPLETED.getKey());
        task.setActualEndTime(DateUtil.getTimeAndToString());
        updateEntity(task, userId);
        outputObject.setBean(task);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void cancelTask(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        PatrolTask task = selectById(id);
        if (task == null) {
            throw new CustomException("任务不存在");
        }
        if (!PatrolTaskState.PENDING.getKey().equals(task.getState())
            && !PatrolTaskState.IN_PROGRESS.getKey().equals(task.getState())
            && !PatrolTaskState.TIMEOUT.getKey().equals(task.getState())) {
            throw new CustomException("只有待执行、执行中或已超时状态的任务才能取消");
        }
        String userId = inputObject.getLogParamsStatic().get("id").toString();
        // 如果正在执行中，设置结束时间
        if (PatrolTaskState.IN_PROGRESS.getKey().equals(task.getState())) {
            task.setActualEndTime(DateUtil.getTimeAndToString());
        }
        task.setState(PatrolTaskState.CANCELLED.getKey());
        updateEntity(task, userId);
        outputObject.setBean(task);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void reassignTimeoutTask(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        PatrolTask task = selectById(id);
        if (task == null) {
            throw new CustomException("任务不存在");
        }
        if (!PatrolTaskState.TIMEOUT.getKey().equals(task.getState())) {
            throw new CustomException("只有已超时状态的任务才能重新分配");
        }
        String userId = inputObject.getLogParamsStatic().get("id").toString();
        // 重新分配执行人（如果传入了新的执行人ID）
        if (inputObject.getParams().get("executorId") != null) {
            task.setExecutorId(inputObject.getParams().get("executorId").toString());
        }
        // 更新计划开始执行时间（如果传入了新的计划时间）
        if (inputObject.getParams().get("plannedStartTime") != null) {
            task.setPlannedStartTime(inputObject.getParams().get("plannedStartTime").toString());
        }
        // 重置为待执行状态
        task.setState(PatrolTaskState.PENDING.getKey());
        // 清空实际开始和结束时间
        task.setActualStartTime(null);
        task.setActualEndTime(null);
        updateEntity(task, userId);
        outputObject.setBean(task);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

}

