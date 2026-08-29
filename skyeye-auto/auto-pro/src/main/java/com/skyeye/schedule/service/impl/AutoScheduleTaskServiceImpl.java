/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.ScheduleFrequency;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.QuartzCronUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.exception.CustomException;
import com.skyeye.history.classenum.AutoHistoryCaseExecuteResult;
import com.skyeye.history.entity.AutoHistoryCase;
import com.skyeye.history.service.AutoHistoryCaseService;
import com.skyeye.module.service.AutoModuleService;
import com.skyeye.schedule.classenum.AutoScheduleAuthEnum;
import com.skyeye.schedule.classenum.AutoScheduleExecuteResult;
import com.skyeye.schedule.classenum.AutoScheduleExecuteType;
import com.skyeye.schedule.dao.AutoScheduleTaskDao;
import com.skyeye.schedule.entity.AutoScheduleTask;
import com.skyeye.schedule.entity.AutoScheduleTaskHistory;
import com.skyeye.schedule.service.AutoScheduleTaskCaseService;
import com.skyeye.schedule.service.AutoScheduleTaskHistoryService;
import com.skyeye.schedule.service.AutoScheduleTaskModuleService;
import com.skyeye.schedule.service.AutoScheduleTaskService;
import com.skyeye.usercase.entity.AutoCase;
import com.skyeye.usercase.service.AutoCaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
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
@Slf4j
public class AutoScheduleTaskServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoScheduleTaskDao, AutoScheduleTask> implements AutoScheduleTaskService {

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

    @Autowired
    private IQuartzService iQuartzService;

    /**
     * 定时任务用例执行线程池，Bean 定义见 ExecutorConfig#scheduleTaskExecutor
     */
    @Autowired
    private Executor scheduleTaskExecutor;

    @Autowired
    private Executor scheduleCaseExecutor;

    private static final int EXECUTE_STATUS_RUNNING = 1;
    private static final int EXECUTE_STATUS_IDLE = 2;

    private final ConcurrentHashMap<String, Object> taskExecuteLocks = new ConcurrentHashMap<>();

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        for (Map<String, Object> bean : beans) {
            Object taskId = bean.get("id");
            if (taskId == null) {
                continue;
            }
            boolean running = autoScheduleTaskHistoryService.checkScheduleTaskRuning(taskId.toString());
            bean.put("executeStatus", running ? EXECUTE_STATUS_RUNNING : EXECUTE_STATUS_IDLE);
            bean.put("executeStatusName", running ? "执行中" : "空闲");
        }
        return beans;
    }

    @Override
    public Class getAuthEnumClass() {
        return AutoScheduleAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(AutoScheduleAuthEnum.ADD.getKey(), AutoScheduleAuthEnum.EDIT.getKey(),
            AutoScheduleAuthEnum.DELETE.getKey());
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
        if (ScheduleFrequency.WEEKLY.getKey().equals(entity.getFrequency())
            && StrUtil.isBlank(entity.getWeekDays())) {
            throw new CustomException("每周执行时，请至少选择一个星期");
        }
        if (ScheduleFrequency.MONTHLY.getKey().equals(entity.getFrequency())
            && StrUtil.isBlank(entity.getMonthDays())) {
            throw new CustomException("每月执行时，请至少选择一个日期");
        }
        if (ScheduleFrequency.CUSTOM.getKey().equals(entity.getFrequency())
            && StrUtil.isBlank(entity.getCustomCron())) {
            throw new CustomException("自定义频次时，请填写 Cron 表达式");
        }
        // 启用时提前校验 Cron 可生成，避免写库后注册失败
        if (EnableEnum.ENABLE_USING.getKey().equals(entity.getEnabled())
            && StrUtil.isEmpty(QuartzCronUtil.buildScheduleConf(
            entity.getFrequency(), entity.getExecuteTime(),
            entity.getWeekDays(), entity.getMonthDays(), entity.getCustomCron()))) {
            throw new CustomException("定时Cron生成失败，请检查执行时间与频次配置");
        }
    }

    @Override
    public void writePostpose(AutoScheduleTask entity, String userId) {
        // 先停旧 XXL 子任务，再保存关联，最后按启用状态重新注册（对齐保养/巡检计划）
        iQuartzService.stopAndDeleteTaskQuartz(entity.getId());
        autoScheduleTaskModuleService.deleteByParentId(entity.getId());
        autoScheduleTaskCaseService.deleteByParentId(entity.getId());
        if (AutoScheduleExecuteType.MODULE.getKey().equals(entity.getExecuteType())) {
            List<String> moduleIds = autoModuleService.queryAllChildIdsByParentId(entity.getModuleIdList());
            autoScheduleTaskModuleService.saveList(entity.getId(), moduleIds);
        } else if (AutoScheduleExecuteType.CASE.getKey().equals(entity.getExecuteType())) {
            autoScheduleTaskCaseService.saveList(entity.getId(), entity.getCaseIdList());
        }
        super.writePostpose(entity, userId);
        if (EnableEnum.ENABLE_USING.getKey().equals(entity.getEnabled())) {
            String cron = QuartzCronUtil.buildScheduleConf(
                entity.getFrequency(), entity.getExecuteTime(),
                entity.getWeekDays(), entity.getMonthDays(), entity.getCustomCron());
            if (StrUtil.isEmpty(cron)) {
                throw new CustomException("定时Cron生成失败");
            }
            SysQuartzMation quartz = new SysQuartzMation();
            quartz.setName(entity.getId());
            quartz.setTitle(entity.getName());
            quartz.setScheduleConf(cron);
            quartz.setGroupId(QuartzConstants.QuartzMateMationJobType.AUTO_SCHEDULE_TASK_EXECUTE.getTaskType());
            iQuartzService.startUpTaskQuartz(quartz);
        }
    }

    @Override
    protected void deletePostpose(AutoScheduleTask entity) {
        autoScheduleTaskModuleService.deleteByParentId(entity.getId());
        autoScheduleTaskCaseService.deleteByParentId(entity.getId());
        autoScheduleTaskHistoryService.deleteByScheduleTaskId(entity.getId());
        iQuartzService.stopAndDeleteTaskQuartz(entity.getId());
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
    public void executeScheduleTask(String id) {
        startScheduleTaskExecution(id, false);
    }

    @Override
    public void executeScheduleTaskById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        startScheduleTaskExecution(id, true);
        outputObject.setBean(Collections.singletonMap("message", "已开始执行"));
        outputObject.settotal(1);
    }

    /**
     * 校验并创建执行记录后异步跑用例；定时触发与手动执行共用。
     */
    private void startScheduleTaskExecution(String id, boolean throwIfEmpty) {
        AutoScheduleTask task = selectById(id);
        List<String> caseIds = resolveCaseIds(task);
        if (CollectionUtil.isEmpty(caseIds)) {
            if (throwIfEmpty) {
                throw new CustomException("暂无可执行的用例");
            }
            return;
        }

        final AutoScheduleTaskHistory history;
        Object lock = taskExecuteLocks.computeIfAbsent(id, key -> new Object());
        synchronized (lock) {
            if (autoScheduleTaskHistoryService.checkScheduleTaskRuning(id)) {
                throw new CustomException("任务正在执行中，请稍后");
            }
            history = buildScheduleHistory(task, caseIds.size());
            autoScheduleTaskHistoryService.createEntity(history, StrUtil.EMPTY);
        }

        final String scheduleHistoryId = history.getId();
        final String tenantId = tenantEnable ? TenantContext.getTenantId() : null;
        final TenantEnum isolationType = tenantEnable ? TenantContext.getIsolationType() : null;
        final List<String> executeCaseIds = caseIds.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
        final int caseTotal = executeCaseIds.size();

        scheduleTaskExecutor.execute(() -> {
            try {
                runWithTenantContext(tenantId, isolationType, () -> {
                    try {
                        runScheduleTaskCases(scheduleHistoryId, executeCaseIds, tenantId, isolationType);
                    } catch (Exception e) {
                        log.error("定时任务[{}]异步执行异常，historyId={}", id, scheduleHistoryId, e);
                        finishScheduleFromHistories(scheduleHistoryId, caseTotal);
                    }
                });
            } finally {
                taskExecuteLocks.remove(id);
            }
        });
    }

    /**
     * 异步线程传播租户上下文（ThreadLocal 不会随线程切换自动传递）
     */
    private void runWithTenantContext(String tenantId, TenantEnum isolationType, Runnable runnable) {
        if (!tenantEnable) {
            runnable.run();
            return;
        }
        try {
            if (StrUtil.isNotBlank(tenantId)) {
                TenantContext.setTenantId(tenantId);
            }
            TenantContext.setIsolationType(isolationType != null ? isolationType : TenantEnum.STRONG_ISOLATION);
            runnable.run();
        } finally {
            TenantContext.clear();
        }
    }

    private void safeFinishScheduleHistory(String scheduleHistoryId, int totalNum, int successNum, int failNum) {
        try {
            double successRate = calcSuccessRate(successNum, totalNum);
            Integer result = failNum > 0
                ? AutoScheduleExecuteResult.FAILED.getKey()
                : AutoScheduleExecuteResult.SUCCESS.getKey();
            autoScheduleTaskHistoryService.finishAutoScheduleTaskHistoryById(
                scheduleHistoryId, result, totalNum, successNum, failNum, successRate);
        } catch (Exception ex) {
            log.error("定时任务执行记录回写失败，historyId={}", scheduleHistoryId, ex);
        }
    }

    private AutoScheduleTaskHistory buildScheduleHistory(AutoScheduleTask task, int totalNum) {
        AutoScheduleTaskHistory history = new AutoScheduleTaskHistory();
        history.setScheduleTaskId(task.getId());
        history.setName(task.getName());
        history.setObjectId(task.getObjectId());
        history.setObjectKey(task.getObjectKey());
        history.setExecuteType(task.getExecuteType());
        history.setTotalNum(totalNum);
        history.setSuccessNum(0);
        history.setFailNum(0);
        history.setSuccessRate(0D);
        return history;
    }

    private void runScheduleTaskCases(String scheduleHistoryId, List<String> caseIds, String tenantId,
                                      TenantEnum isolationType) {
        int size = caseIds.size();
        // 记录已产生执行历史的用例，汇总前对缺失项补失败记录，避免 成功+失败 < 总数
        Set<String> recordedCaseIds = ConcurrentHashMap.newKeySet();
        CompletableFuture<?>[] futures = new CompletableFuture[size];
        for (int i = 0; i < size; i++) {
            final String caseId = caseIds.get(i);
            futures[i] = CompletableFuture.runAsync(
                () -> runCaseWithContext(caseId, scheduleHistoryId, tenantId, isolationType, recordedCaseIds),
                scheduleCaseExecutor);
        }
        CompletableFuture.allOf(futures).join();
        ensureMissingCasesRecorded(caseIds, scheduleHistoryId, recordedCaseIds, tenantId, isolationType);
        finishScheduleFromHistories(scheduleHistoryId, size);
    }

    /**
     * 对未落库的用例补一条「执行失败」历史，保证明细条数与汇总一致
     */
    private void ensureMissingCasesRecorded(List<String> caseIds, String scheduleHistoryId,
                                            Set<String> recordedCaseIds, String tenantId,
                                            TenantEnum isolationType) {
        for (String caseId : caseIds) {
            if (recordedCaseIds.contains(caseId)) {
                continue;
            }
            try {
                runWithTenantContext(tenantId, isolationType,
                    () -> createFailedHistoryStub(caseId, scheduleHistoryId, recordedCaseIds));
            } catch (Exception e) {
                log.warn("定时任务补写失败用例记录异常，caseId={}, historyId={}", caseId, scheduleHistoryId, e);
            }
        }
    }

    /**
     * 按库表汇总成功/失败。不强制结束「执行中」的用例（可能只是耗时长仍会成功）。
     * 线程都返回后短暂等待事务落库；若仍有执行中则暂不结束批次，交由僵死恢复逻辑处理。
     */
    private void finishScheduleFromHistories(String scheduleHistoryId, int expectedTotal) {
        int[] stats = waitForCaseResultsSettled(scheduleHistoryId);
        int success = stats[0];
        int fail = stats[1];
        int inProgress = stats[2];
        if (inProgress > 0) {
            log.warn("定时任务批次[{}]仍有{}个用例执行中，暂不回写结束状态，等待其自行完成",
                scheduleHistoryId, inProgress);
            return;
        }
        int executed = success + fail;
        if (executed < expectedTotal) {
            log.warn("定时任务批次[{}]应有{}个用例，实际产生{}条执行记录，差额按失败计",
                scheduleHistoryId, expectedTotal, executed);
            fail = expectedTotal - success;
        } else if (executed > expectedTotal) {
            expectedTotal = executed;
        }
        safeFinishScheduleHistory(scheduleHistoryId, expectedTotal, success, fail);
    }

    /**
     * 等待用例历史从「执行中」落成终态（仅等待，不改写结果）
     *
     * @return [success, fail, inProgress]
     */
    private int[] waitForCaseResultsSettled(String scheduleHistoryId) {
        final int maxRetry = 15;
        final long intervalMs = 200L;
        int[] stats = countScheduleCaseResults(scheduleHistoryId);
        for (int i = 0; i < maxRetry && stats[2] > 0; i++) {
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            stats = countScheduleCaseResults(scheduleHistoryId);
        }
        return stats;
    }

    /**
     * @return [success, fail, inProgress]
     */
    private int[] countScheduleCaseResults(String scheduleHistoryId) {
        QueryWrapper<AutoHistoryCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoHistoryCase::getScheduleTaskHistoryId), scheduleHistoryId);
        List<AutoHistoryCase> histories = autoHistoryCaseService.list(queryWrapper);
        int success = 0;
        int fail = 0;
        int inProgress = 0;
        for (AutoHistoryCase history : histories) {
            Integer executeResult = history.getExecuteResult();
            if (AutoHistoryCaseExecuteResult.EXECUTION_SUCCESSFUL.getKey().equals(executeResult)) {
                success++;
            } else if (AutoHistoryCaseExecuteResult.IN_PROGRESS.getKey().equals(executeResult)) {
                inProgress++;
            } else {
                fail++;
            }
        }
        return new int[]{success, fail, inProgress};
    }

    /**
     * 子线程并行执行用例：复用用例已有 updateHistoryCase（与 MQ 消费者相同），不改用例模块
     */
    private void runCaseWithContext(String caseId, String scheduleTaskHistoryId, String tenantId,
                                    TenantEnum isolationType, Set<String> recordedCaseIds) {
        try {
            runWithTenantContext(tenantId, isolationType,
                () -> executeCaseAndCollectResult(caseId, scheduleTaskHistoryId, recordedCaseIds));
        } catch (Exception e) {
            log.warn("定时任务用例[{}]执行异常，historyId={}", caseId, scheduleTaskHistoryId, e);
            try {
                runWithTenantContext(tenantId, isolationType,
                    () -> createFailedHistoryStub(caseId, scheduleTaskHistoryId, recordedCaseIds));
            } catch (Exception ex) {
                log.warn("定时任务用例[{}]补写失败记录异常，historyId={}", caseId, scheduleTaskHistoryId, ex);
            }
        }
    }

    /**
     * 建用例历史 + updateHistoryCase；异常时尽量落一条失败历史，供汇总与明细展示
     */
    private void executeCaseAndCollectResult(String caseId, String scheduleTaskHistoryId,
                                             Set<String> recordedCaseIds) {
        AutoCase autoCase = autoCaseService.selectById(caseId);
        if (autoCase == null) {
            createFailedHistoryStub(caseId, scheduleTaskHistoryId, recordedCaseIds);
            return;
        }
        AutoHistoryCase autoHistoryCase = new AutoHistoryCase();
        autoHistoryCase.setName(autoCase.getName());
        autoHistoryCase.setModuleId(autoCase.getModuleId());
        autoHistoryCase.setResultKey(autoCase.getResultKey());
        autoHistoryCase.setCaseId(autoCase.getId());
        autoHistoryCase.setScheduleTaskHistoryId(scheduleTaskHistoryId);
        try {
            autoHistoryCaseService.createEntity(autoHistoryCase, StrUtil.EMPTY);
            recordedCaseIds.add(caseId);
            autoCaseService.updateHistoryCase(autoCase, true, autoHistoryCase);
        } catch (Exception e) {
            if (StrUtil.isNotEmpty(autoHistoryCase.getId())) {
                recordedCaseIds.add(caseId);
                try {
                    // 仅在本用例执行抛异常时收尾为失败（线程已结束，不是打断耗时中的成功路径）
                    autoHistoryCaseService.finishAutoCaseHistoryById(
                        autoHistoryCase.getId(), AutoHistoryCaseExecuteResult.EXECUTION_FAILED.getKey());
                } catch (Exception finishEx) {
                    log.warn("用例历史收尾失败，caseHistoryId={}", autoHistoryCase.getId(), finishEx);
                }
            } else {
                createFailedHistoryStub(caseId, scheduleTaskHistoryId, recordedCaseIds);
            }
            throw e;
        }
    }

    /**
     * 用例未正常落库时补写失败历史（同 caseId 已记录则跳过）
     */
    private void createFailedHistoryStub(String caseId, String scheduleTaskHistoryId,
                                         Set<String> recordedCaseIds) {
        if (recordedCaseIds.contains(caseId)) {
            return;
        }
        AutoCase autoCase = null;
        try {
            autoCase = autoCaseService.selectById(caseId);
        } catch (Exception ignored) {
            // ignore
        }
        AutoHistoryCase autoHistoryCase = new AutoHistoryCase();
        if (autoCase != null) {
            autoHistoryCase.setName(autoCase.getName());
            autoHistoryCase.setModuleId(autoCase.getModuleId());
            autoHistoryCase.setResultKey(autoCase.getResultKey());
        } else {
            autoHistoryCase.setName("用例执行失败");
            autoHistoryCase.setResultKey(StrUtil.EMPTY);
        }
        autoHistoryCase.setCaseId(caseId);
        autoHistoryCase.setScheduleTaskHistoryId(scheduleTaskHistoryId);
        autoHistoryCaseService.createEntity(autoHistoryCase, StrUtil.EMPTY);
        recordedCaseIds.add(caseId);
        autoHistoryCaseService.finishAutoCaseHistoryById(
            autoHistoryCase.getId(), AutoHistoryCaseExecuteResult.EXECUTION_FAILED.getKey());
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
        queryWrapper.select(CommonConstants.ID);
        return autoCaseService.list(queryWrapper).stream()
            .map(AutoCase::getId)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * 成功率 0-1，保留四位小数
     */
    private double calcSuccessRate(int successNum, int totalNum) {
        if (totalNum <= 0) {
            return 0D;
        }
        return Double.parseDouble(CalculationUtil.divide(
            String.valueOf(successNum), String.valueOf(totalNum), CommonNumConstants.NUM_FOUR));
    }
}
