/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.module.service.AutoModuleService;
import com.skyeye.schedule.classenum.AutoScheduleAuthEnum;
import com.skyeye.schedule.classenum.AutoScheduleExecuteType;
import com.skyeye.schedule.dao.AutoScheduleTaskDao;
import com.skyeye.schedule.entity.AutoScheduleTask;
import com.skyeye.schedule.service.AutoScheduleTaskCaseService;
import com.skyeye.schedule.service.AutoScheduleTaskModuleService;
import com.skyeye.schedule.service.AutoScheduleTaskService;
import com.skyeye.usercase.entity.AutoCase;
import com.skyeye.usercase.service.AutoCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 自动化定时任务服务层
 */
@Slf4j
@Service
@SkyeyeService(name = "定时任务", groupName = "定时任务", teamAuth = true)
public class AutoScheduleTaskServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoScheduleTaskDao, AutoScheduleTask>
    implements AutoScheduleTaskService {

    @Autowired
    private AutoScheduleTaskCaseService autoScheduleTaskCaseService;

    @Autowired
    private AutoScheduleTaskModuleService autoScheduleTaskModuleService;

    @Autowired
    private AutoCaseService autoCaseService;

    @Autowired
    private AutoModuleService autoModuleService;

    @Override
    public Class getAuthEnumClass() {
        return AutoScheduleAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(AutoScheduleAuthEnum.ADD.getKey(), AutoScheduleAuthEnum.EDIT.getKey(),
            AutoScheduleAuthEnum.DELETE.getKey(), AutoScheduleAuthEnum.EXECUTE.getKey());
    }

    @Override
    protected QueryWrapper<AutoScheduleTask> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AutoScheduleTask> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTask::getObjectId), commonPageInfo.getObjectId());
        }
        if (commonPageInfo.getEnabled() != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTask::getEnabled), commonPageInfo.getEnabled());
        }
        return queryWrapper;
    }

    @Override
    public void validatorEntity(AutoScheduleTask entity) {
        if (AutoScheduleExecuteType.MODULE.getKey().equals(entity.getExecuteType())
            && CollectionUtil.isEmpty(entity.getModuleIdList())) {
            throw new CustomException("按模块执行时，请至少选择一个模块");
        }
        if (AutoScheduleExecuteType.CASE.getKey().equals(entity.getExecuteType())
            && CollectionUtil.isEmpty(entity.getCaseIdList())) {
            throw new CustomException("按用例执行时，请至少选择一个用例");
        }
    }

    @Override
    public void writePostpose(AutoScheduleTask entity, String userId) {
        autoScheduleTaskModuleService.deleteByParentId(entity.getId());
        autoScheduleTaskCaseService.deleteByParentId(entity.getId());
        if (AutoScheduleExecuteType.MODULE.getKey().equals(entity.getExecuteType())) {
            List<String> moduleIds = autoModuleService.queryAllChildIdsByParentId(entity.getModuleIdList());
            autoScheduleTaskModuleService.saveList(entity.getId(), moduleIds);
        } else if (AutoScheduleExecuteType.CASE.getKey().equals(entity.getExecuteType())) {
            autoScheduleTaskCaseService.saveList(entity.getId(), entity.getCaseIdList());
        }
        super.writePostpose(entity, userId);
    }

    @Override
    protected void deletePostpose(AutoScheduleTask entity) {
        autoScheduleTaskModuleService.deleteByParentId(entity.getId());
        autoScheduleTaskCaseService.deleteByParentId(entity.getId());
    }

    @Override
    public AutoScheduleTask getDataFromDb(String id) {
        AutoScheduleTask task = super.getDataFromDb(id);
        task.setModuleIdList(autoScheduleTaskModuleService.selectByParentId(id));
        task.setCaseIdList(autoScheduleTaskCaseService.selectByParentId(id));
        return task;
    }

    @Override
    public AutoScheduleTask selectById(String id) {
        AutoScheduleTask task = super.selectById(id);
        if (task == null) {
            return null;
        }
        if (CollectionUtil.isNotEmpty(task.getModuleIdList())) {
            task.setModuleMationList(autoModuleService.selectByIds(task.getModuleIdList().toArray(new String[]{})));
        }
        if (CollectionUtil.isNotEmpty(task.getCaseIdList())) {
            task.setCaseMationList(autoCaseService.selectByIds(task.getCaseIdList().toArray(new String[]{})));
        }
        return task;
    }

    @Override
    public void executeScheduleTask(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        executeScheduleTask(id);
    }

    @Override
    public void executeScheduleTask(String id) {
        AutoScheduleTask task = selectById(id);
        List<String> caseIds = resolveCaseIds(task);
        if (CollectionUtil.isEmpty(caseIds)) {
            return;
        }
        for (String caseId : caseIds) {
            try {
                autoCaseService.executeCase(caseId, true);
            } catch (Exception e) {
                log.warn("定时任务[{}]执行用例[{}]失败: {}", id, caseId, e.getMessage());
            }
        }
    }

    /**
     * 按执行范围解析待执行用例id
     */
    private List<String> resolveCaseIds(AutoScheduleTask task) {
        Integer executeType = task.getExecuteType();
        if (AutoScheduleExecuteType.FULL.getKey().equals(executeType)) {
            return queryCaseIdsByObjectId(task.getObjectId(), null);
        }
        if (AutoScheduleExecuteType.MODULE.getKey().equals(executeType)) {
            if (CollectionUtil.isEmpty(task.getModuleIdList())) {
                return Collections.emptyList();
            }
            return queryCaseIdsByObjectId(task.getObjectId(), task.getModuleIdList());
        }
        if (AutoScheduleExecuteType.CASE.getKey().equals(executeType)) {
            return CollectionUtil.isEmpty(task.getCaseIdList()) ? new ArrayList<>() : task.getCaseIdList();
        }
        throw new CustomException("不支持的执行范围");
    }

    private List<String> queryCaseIdsByObjectId(String objectId, List<String> moduleIds) {
        QueryWrapper<AutoCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoCase::getObjectId), objectId);
        if (CollectionUtil.isNotEmpty(moduleIds)) {
            queryWrapper.in(MybatisPlusUtil.toColumns(AutoCase::getModuleId), moduleIds);
        }
        queryWrapper.select(MybatisPlusUtil.toColumns(AutoCase::getId));
        return autoCaseService.list(queryWrapper).stream()
            .map(AutoCase::getId)
            .collect(Collectors.toList());
    }
}
