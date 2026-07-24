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
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.schedule.classenum.AutoScheduleExecuteResult;
import com.skyeye.schedule.dao.AutoScheduleTaskHistoryDao;
import com.skyeye.schedule.entity.AutoScheduleTaskHistory;
import com.skyeye.schedule.service.AutoScheduleTaskHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: AutoScheduleTaskHistoryServiceImpl
 * @Description: 定时任务执行记录服务层
 */
@Service
@SkyeyeService(name = "定时任务执行记录", groupName = "定时任务执行记录", manageShow = false, allowDynamicAttrKey = false)
public class AutoScheduleTaskHistoryServiceImpl
    extends SkyeyeBusinessServiceImpl<AutoScheduleTaskHistoryDao, AutoScheduleTaskHistory>
    implements AutoScheduleTaskHistoryService {

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
        QueryWrapper<AutoScheduleTaskHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getScheduleTaskId), scheduleTaskId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getExecuteResult),
            AutoScheduleExecuteResult.IN_PROGRESS.getKey());
        List<AutoScheduleTaskHistory> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return false;
        }
        return true;
    }

    @Override
    public void deleteByScheduleTaskId(String scheduleTaskId) {
        QueryWrapper<AutoScheduleTaskHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getScheduleTaskId), scheduleTaskId);
        remove(queryWrapper);
    }

    @Override
    public void finishAutoScheduleTaskHistoryById(String id, Integer result, Integer totalNum,
                                                  Integer successNum, Integer failNum, Double successRate) {
        UpdateWrapper<AutoScheduleTaskHistory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getExecuteResult), result);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getTotalNum), totalNum);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getSuccessNum), successNum);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getFailNum), failNum);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getSuccessRate), successRate);
        String endTime = DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
        AutoScheduleTaskHistory history = selectById(id);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getExecuteEndTime), endTime);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoScheduleTaskHistory::getExecuteTime),
            String.valueOf(DateUtil.getDistanceMillisecondHMS(history.getExecuteStartTime(), endTime,
                DateUtil.YYYY_MM_DD_HH_MM_SS_SSS)));
        update(updateWrapper);
    }
}
