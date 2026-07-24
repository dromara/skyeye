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
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.history.classenum.AutoHistoryCaseExecuteResult;
import com.skyeye.history.entity.AutoHistoryCase;
import com.skyeye.history.service.AutoHistoryCaseService;
import com.skyeye.module.service.AutoModuleService;
import com.skyeye.schedule.classenum.AutoScheduleAuthEnum;
import com.skyeye.schedule.classenum.AutoScheduleExecuteResult;
import com.skyeye.schedule.classenum.AutoScheduleExecuteType;
import com.skyeye.schedule.classenum.AutoScheduleFrequency;
import com.skyeye.schedule.dao.AutoScheduleTaskDao;
import com.skyeye.schedule.entity.AutoScheduleTask;
import com.skyeye.schedule.entity.AutoScheduleTaskHistory;
import com.skyeye.schedule.service.AutoScheduleTaskCaseService;
import com.skyeye.schedule.service.AutoScheduleTaskHistoryService;
import com.skyeye.schedule.service.AutoScheduleTaskModuleService;
import com.skyeye.schedule.service.AutoScheduleTaskService;
import com.skyeye.schedule.support.AutoScheduleTaskCronBuilder;
import com.skyeye.usercase.entity.AutoCase;
import com.skyeye.usercase.service.AutoCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoScheduleTaskServiceImpl
 * @Description: 自动化定时任务服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/7/22
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
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

    @Autowired
    private AutoHistoryCaseService autoHistoryCaseService;

    @Autowired
    private AutoScheduleTaskHistoryService autoScheduleTaskHistoryService;

    /**
     * 定时任务用例执行线程池，Bean 定义见 ExecutorConfig#scheduleTaskExecutor
     */
    @Autowired
    private Executor scheduleTaskExecutor;

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
        if (AutoScheduleFrequency.WEEKLY.getKey().equals(entity.getFrequency())
            && StrUtil.isBlank(entity.getWeekDays())) {
            throw new CustomException("每周执行时，请至少选择一个星期");
        }
        if (AutoScheduleFrequency.MONTHLY.getKey().equals(entity.getFrequency())
            && StrUtil.isBlank(entity.getMonthDays())) {
            throw new CustomException("每月执行时，请至少选择一个日期");
        }
        if (AutoScheduleFrequency.CUSTOM.getKey().equals(entity.getFrequency())
            && StrUtil.isBlank(entity.getCustomCron())) {
            throw new CustomException("自定义频次时，请填写 Cron 表达式");
        }
        // 启用时提前校验 Cron 可生成，避免写库后注册失败
        if (EnableEnum.ENABLE_USING.getKey().equals(entity.getEnabled())
            && StrUtil.isEmpty(AutoScheduleTaskCronBuilder.buildScheduleConf(entity))) {
            throw new CustomException("定时Cron生成失败，请检查执行时间与频次配置");
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
        autoScheduleTaskHistoryService.deleteByScheduleTaskId(entity.getId());
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
        if (autoScheduleTaskHistoryService.checkScheduleTaskRuning(id)) {
            throw new CustomException("任务正在执行中，请稍后");
        }

        // 新建一条执行中记录（对齐用例执行：createEntity + createPrepose 写执行中/开始时间）
        AutoScheduleTaskHistory history = new AutoScheduleTaskHistory();
        history.setScheduleTaskId(task.getId());
        history.setName(task.getName());
        history.setObjectId(task.getObjectId());
        history.setObjectKey(task.getObjectKey());
        history.setExecuteType(task.getExecuteType());
        history.setTotalNum(caseIds.size());
        history.setSuccessNum(0);
        history.setFailNum(0);
        history.setSuccessRate(0D);
        autoScheduleTaskHistoryService.createEntity(history, StrUtil.EMPTY);

        // ThreadLocal 必须在主线程捕获：TenantContext 为 ThreadLocal，子线程无法读取
        final String tenantId = tenantEnable ? TenantContext.getTenantId() : null;
        final TenantEnum isolationType = tenantEnable ? TenantContext.getIsolationType() : null;

        AtomicInteger successNum = new AtomicInteger(0);
        AtomicInteger failNum = new AtomicInteger(0);
        int size = caseIds.size();
        try {
            CompletableFuture<?>[] futures = new CompletableFuture[size];
            for (int i = 0; i < size; i++) {
                final String caseId = caseIds.get(i);
                futures[i] = CompletableFuture.runAsync(
                    () -> runCaseWithContext(caseId, tenantId, isolationType, successNum, failNum),
                    scheduleTaskExecutor);
            }
            CompletableFuture.allOf(futures).join();
        } finally {
            // 无论正常结束还是异常，都回写记录，避免一直卡在「执行中」
            int success = successNum.get();
            int fail = failNum.get();
            // 未跑完的按失败计（异常中断时 success+fail 可能小于 size）
            if (success + fail < size) {
                fail = size - success;
            }
            double successRate = size == 0 ? 0D : Math.round(success * 100.0 / size) / 100.0;
            Integer result = fail > 0
                ? AutoScheduleExecuteResult.FAILED.getKey()
                : AutoScheduleExecuteResult.SUCCESS.getKey();
            autoScheduleTaskHistoryService.finishAutoScheduleTaskHistoryById(
                history.getId(), result, size, success, fail, successRate);
        }
    }

    /**
     * 子线程并行执行用例：复用用例已有 updateHistoryCase（与 MQ 消费者相同），不改用例模块
     */
    private void runCaseWithContext(String caseId, String tenantId, TenantEnum isolationType,
                                    AtomicInteger successNum, AtomicInteger failNum) {
        try {
            if (tenantEnable) {
                if (tenantId != null) {
                    TenantContext.setTenantId(tenantId);
                }
                if (isolationType != null) {
                    TenantContext.setIsolationType(isolationType);
                }
            }
            if (executeCaseAndCollectResult(caseId)) {
                successNum.incrementAndGet();
            } else {
                failNum.incrementAndGet();
            }
        } catch (Exception e) {
            failNum.incrementAndGet();
        } finally {
            if (tenantEnable) {
                TenantContext.clear();
            }
        }
    }

    /**
     * 对齐 ExecuteCaseServiceImpl：建用例历史 + updateHistoryCase 同步跑完，按历史结果判定
     */
    private boolean executeCaseAndCollectResult(String caseId) {
        AutoCase autoCase = autoCaseService.selectById(caseId);
        if (autoHistoryCaseService.checkUserCaseRuning(autoCase.getId())) {
            throw new CustomException("存在执行中的用例，请稍后执行");
        }
        AutoHistoryCase autoHistoryCase = new AutoHistoryCase();
        autoHistoryCase.setName(autoCase.getName());
        autoHistoryCase.setModuleId(autoCase.getModuleId());
        autoHistoryCase.setResultKey(autoCase.getResultKey());
        autoHistoryCase.setCaseId(autoCase.getId());
        autoHistoryCaseService.createEntity(autoHistoryCase, StrUtil.EMPTY);
        autoCaseService.updateHistoryCase(autoCase, true, autoHistoryCase);
        AutoHistoryCase finished = autoHistoryCaseService.selectById(autoHistoryCase.getId());
        return finished != null
            && AutoHistoryCaseExecuteResult.EXECUTION_SUCCESSFUL.getKey().equals(finished.getExecuteResult());
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
        return Collections.emptyList();
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
