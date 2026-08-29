/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.history.classenum.AutoHistoryCaseExecuteResult;
import com.skyeye.history.entity.AutoHistoryCase;
import com.skyeye.schedule.classenum.AutoScheduleExecuteResult;
import com.skyeye.schedule.dao.AutoScheduleTaskHistoryDao;
import com.skyeye.schedule.entity.AutoScheduleTaskHistory;
import com.skyeye.schedule.service.AutoScheduleTaskHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoScheduleTaskHistoryServiceImpl
 * @Description: 定时任务执行记录服务层
 */
@Service
@SkyeyeService(name = "定时任务执行记录", groupName = "定时任务执行记录", allowDynamicAttrKey = false)
@Slf4j
public class AutoScheduleTaskHistoryServiceImpl extends SkyeyeBusinessServiceImpl<AutoScheduleTaskHistoryDao, AutoScheduleTaskHistory> implements AutoScheduleTaskHistoryService {

    @Autowired
    private com.skyeye.history.service.AutoHistoryCaseService autoHistoryCaseService;

    /**
     * 超过该时长仍为执行中，视为异常中断并自动回写失败
     */
    private static final long STALE_IN_PROGRESS_MS = 6L * 60 * 60 * 1000;

    /**
     * 用例均已结束但批次未回写时，超过该时长自动补全
     */
    private static final long RECOVER_AFTER_CASES_DONE_MS = 3L * 60 * 1000;

    @Override
    public void createPrepose(AutoScheduleTaskHistory entity) {
        entity.setExecuteResult(AutoScheduleExecuteResult.IN_PROGRESS.getKey());
        entity.setExecuteStartTime(DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS));
    }

    @Override
    protected QueryWrapper<AutoScheduleTaskHistory> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AutoScheduleTaskHistory> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // CommonPageInfo.objectId 传定时任务id（对齐用例历史 objectId=用例id）
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getScheduleTaskId),
                commonPageInfo.getObjectId());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getExecuteStartTime));
        return queryWrapper;
    }

    @Override
    public Boolean checkScheduleTaskRuning(String scheduleTaskId) {
        recoverStaleInProgressHistories(scheduleTaskId);
        QueryWrapper<AutoScheduleTaskHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getScheduleTaskId), scheduleTaskId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getExecuteResult),
            AutoScheduleExecuteResult.IN_PROGRESS.getKey());
        List<AutoScheduleTaskHistory> list = list(queryWrapper);
        return CollectionUtil.isNotEmpty(list);
    }

    private void recoverStaleInProgressHistories(String scheduleTaskId) {
        QueryWrapper<AutoScheduleTaskHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getScheduleTaskId), scheduleTaskId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getExecuteResult),
            AutoScheduleExecuteResult.IN_PROGRESS.getKey());
        List<AutoScheduleTaskHistory> inProgressList = list(queryWrapper);
        if (CollectionUtil.isEmpty(inProgressList)) {
            return;
        }
        String now = DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
        for (AutoScheduleTaskHistory history : inProgressList) {
            try {
                recoverStaleHistoryIfNeeded(history, now);
            } catch (Exception e) {
                log.warn("定时任务执行记录[{}]异常状态恢复失败", history.getId(), e);
            }
        }
    }

    private void recoverStaleHistoryIfNeeded(AutoScheduleTaskHistory history, String now) {
        if (history == null || StrUtil.isEmpty(history.getExecuteStartTime())) {
            return;
        }
        long elapsedMs = DateUtil.getDistanceMillisecondHMS(
            history.getExecuteStartTime(), now, DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
        if (elapsedMs >= STALE_IN_PROGRESS_MS) {
            forceFinishHistory(history, AutoScheduleExecuteResult.FAILED.getKey());
            return;
        }
        if (elapsedMs < RECOVER_AFTER_CASES_DONE_MS) {
            return;
        }
        if (hasRunningCaseHistory(history.getId())) {
            return;
        }
        recalculateAndFinishHistory(history);
    }

    private boolean hasRunningCaseHistory(String scheduleTaskHistoryId) {
        QueryWrapper<AutoHistoryCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoHistoryCase::getScheduleTaskHistoryId), scheduleTaskHistoryId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoHistoryCase::getExecuteResult),
            AutoHistoryCaseExecuteResult.IN_PROGRESS.getKey());
        return autoHistoryCaseService.count(queryWrapper) > 0;
    }

    private void recalculateAndFinishHistory(AutoScheduleTaskHistory history) {
        QueryWrapper<AutoHistoryCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoHistoryCase::getScheduleTaskHistoryId), history.getId());
        List<AutoHistoryCase> caseHistories = autoHistoryCaseService.list(queryWrapper);
        int totalNum = history.getTotalNum() != null && history.getTotalNum() > 0
            ? history.getTotalNum()
            : caseHistories.size();
        if (totalNum == 0) {
            forceFinishHistory(history, AutoScheduleExecuteResult.FAILED.getKey());
            return;
        }
        int successNum = 0;
        int failNum = 0;
        for (AutoHistoryCase caseHistory : caseHistories) {
            if (AutoHistoryCaseExecuteResult.EXECUTION_SUCCESSFUL.getKey().equals(caseHistory.getExecuteResult())) {
                successNum++;
            } else if (!AutoHistoryCaseExecuteResult.IN_PROGRESS.getKey().equals(caseHistory.getExecuteResult())) {
                failNum++;
            }
        }
        if (successNum + failNum < totalNum) {
            failNum = totalNum - successNum;
        } else if (successNum + failNum > totalNum) {
            totalNum = successNum + failNum;
        }
        double successRate = calcSuccessRate(successNum, totalNum);
        Integer result = failNum > 0
            ? AutoScheduleExecuteResult.FAILED.getKey()
            : AutoScheduleExecuteResult.SUCCESS.getKey();
        finishAutoScheduleTaskHistoryById(history.getId(), result, totalNum, successNum, failNum, successRate);
        log.info("定时任务执行记录[{}]已自动补全结束状态", history.getId());
    }

    private void forceFinishHistory(AutoScheduleTaskHistory history, Integer result) {
        int totalNum = history.getTotalNum() != null ? history.getTotalNum() : 0;
        finishAutoScheduleTaskHistoryById(history.getId(), result, totalNum, 0,
            totalNum > 0 ? totalNum : 0, 0D);
        log.info("定时任务执行记录[{}]超时，已标记为结束", history.getId());
    }

    @Override
    public void deleteByScheduleTaskId(String scheduleTaskId) {
        QueryWrapper<AutoScheduleTaskHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getScheduleTaskId), scheduleTaskId);
        queryWrapper.select(CommonConstants.ID);
        List<String> historyIds = list(queryWrapper).stream()
            .map(AutoScheduleTaskHistory::getId)
            .collect(Collectors.toList());
        autoHistoryCaseService.deleteByScheduleTaskHistoryIds(historyIds);
        QueryWrapper<AutoScheduleTaskHistory> removeWrapper = new QueryWrapper<>();
        removeWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getScheduleTaskId), scheduleTaskId);
        remove(removeWrapper);
    }

    @Override
    public void finishAutoScheduleTaskHistoryById(String id, Integer result, Integer totalNum,
                                                  Integer successNum, Integer failNum, Double successRate) {
        AutoScheduleTaskHistory history = selectById(id);
        if (history == null) {
            log.warn("定时任务执行记录[{}]不存在，跳过回写", id);
            return;
        }
        UpdateWrapper<AutoScheduleTaskHistory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getExecuteResult), result);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getTotalNum), totalNum);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getSuccessNum), successNum);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getFailNum), failNum);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getSuccessRate), successRate);
        String endTime = DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getExecuteEndTime), endTime);
        if (StrUtil.isNotEmpty(history.getExecuteStartTime())) {
            updateWrapper.set(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getExecuteTime),
                String.valueOf(DateUtil.getDistanceMillisecondHMS(history.getExecuteStartTime(), endTime,
                    DateUtil.YYYY_MM_DD_HH_MM_SS_SSS)));
        }
        update(updateWrapper);
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
