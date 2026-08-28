/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.history.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.history.classenum.AutoHistoryCaseExecuteResult;
import com.skyeye.history.dao.AutoHistoryCaseDao;
import com.skyeye.history.entity.AutoHistoryCase;
import com.skyeye.history.entity.AutoHistoryStep;
import com.skyeye.history.service.AutoHistoryCaseService;
import com.skyeye.history.service.AutoHistoryStepService;
import com.skyeye.module.service.AutoModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AutoHistoryCaseServiceImpl
 * @Description: 用例执行历史服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/16 20:22
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用例执行历史管理", groupName = "用例执行历史管理")
public class AutoHistoryCaseServiceImpl extends SkyeyeBusinessServiceImpl<AutoHistoryCaseDao, AutoHistoryCase> implements AutoHistoryCaseService {

    @Autowired
    private AutoHistoryStepService autoHistoryStepService;

    @Autowired
    private AutoModuleService autoModuleService;

    /**
     * 定时任务执行详情：按批次 id 查关联用例历史（见 queryScheduleTaskHistoryCaseList）
     */
    private static final String QUERY_TYPE_SCHEDULE_TASK_HISTORY = "scheduleTaskHistory";

    @Override
    protected QueryWrapper<AutoHistoryCase> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AutoHistoryCase> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (QUERY_TYPE_SCHEDULE_TASK_HISTORY.equals(commonPageInfo.getType())) {
            // objectId 传 auto_schedule_task_history.id
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoHistoryCase::getScheduleTaskHistoryId),
                commonPageInfo.getObjectId());
            // state：2 成功 / 3 失败（AutoHistoryCaseExecuteResult）
            if (StrUtil.isNotBlank(commonPageInfo.getState())) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(AutoHistoryCase::getExecuteResult),
                    Integer.parseInt(commonPageInfo.getState()));
            }
            if (StrUtil.isNotBlank(commonPageInfo.getKeyword())) {
                queryWrapper.like(MybatisPlusUtil.toColumns(AutoHistoryCase::getName),
                    commonPageInfo.getKeyword().trim());
            }
        } else {
            // 默认：按用例 id 查该用例的全部执行历史
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoHistoryCase::getCaseId), commonPageInfo.getObjectId());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AutoHistoryCase::getExecuteStartTime));
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        if (QUERY_TYPE_SCHEDULE_TASK_HISTORY.equals(pageInfo.getType())) {
            autoModuleService.setMationForMap(beans, "moduleId", "moduleMation");
        }
        return beans;
    }

    @Override
    public void createPrepose(AutoHistoryCase entity) {
        entity.setExecuteResult(AutoHistoryCaseExecuteResult.IN_PROGRESS.getKey());
        entity.setExecuteStartTime(DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS));
    }

    @Override
    public void updatePostpose(AutoHistoryCase autoHistoryCase, String userId) {
        List<AutoHistoryStep> autoHistoryStepList = autoHistoryCase.getStepList();
        autoHistoryStepList.forEach(autoStep -> {
            autoStep.setHistoryCaseId(autoHistoryCase.getId());
        });
        autoHistoryStepService.createEntity(autoHistoryCase.getStepList(), userId);
    }

    @Override
    public AutoHistoryCase getDataFromDb(String id) {
        AutoHistoryCase autoHistoryCase = super.getDataFromDb(id);
        autoHistoryCase.setStepList(autoHistoryStepService.queryAutoStepListByCaseId(id));
        return autoHistoryCase;
    }

    @Override
    public void deletePostpose(String objectId) {
        autoHistoryStepService.deleteByObjectId(objectId);
    }

    @Override
    public Boolean checkUserCaseRuning(String caseId) {
        QueryWrapper<AutoHistoryCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoHistoryCase::getCaseId), caseId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoHistoryCase::getExecuteResult), AutoHistoryCaseExecuteResult.IN_PROGRESS.getKey());
        List<AutoHistoryCase> autoHistoryCases = list(queryWrapper);
        if (CollectionUtil.isEmpty(autoHistoryCases)) {
            return false;
        }
        return true;
    }

    @Override
    public void finishAutoCaseHistoryById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        finishAutoCaseHistoryById(id, AutoHistoryCaseExecuteResult.EXECUTION_FAILED.getKey());
    }

    @Override
    public void finishAutoCaseHistoryById(String id, Integer result) {
        UpdateWrapper<AutoHistoryCase> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoHistoryCase::getExecuteResult), result);
        String endTime = DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
        AutoHistoryCase autoHistoryCase = selectById(id);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoHistoryCase::getExecuteEndTime), endTime);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoHistoryCase::getExecuteTime), String.valueOf(DateUtil.getDistanceMillisecondHMS(autoHistoryCase.getExecuteStartTime(), endTime, DateUtil.YYYY_MM_DD_HH_MM_SS_SSS)));
        update(updateWrapper);
        refreshCache(id);
    }

    /**
     * 定时任务执行详情-用例明细分页。
     * 前端 objectId=批次 id，state=2/3 过滤成功/失败。
     */
    @Override
    public void queryScheduleTaskHistoryCaseList(InputObject inputObject, OutputObject outputObject) {
        inputObject.getParams().put("type", QUERY_TYPE_SCHEDULE_TASK_HISTORY);
        queryPageList(inputObject, outputObject);
    }

    /**
     * 删除批次时级联删除关联的 auto_history_case
     */
    @Override
    public void deleteByScheduleTaskHistoryIds(List<String> scheduleTaskHistoryIds) {
        if (CollectionUtil.isEmpty(scheduleTaskHistoryIds)) {
            return;
        }
        QueryWrapper<AutoHistoryCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(AutoHistoryCase::getScheduleTaskHistoryId), scheduleTaskHistoryIds);
        List<AutoHistoryCase> historyCases = list(queryWrapper);
        if (CollectionUtil.isEmpty(historyCases)) {
            return;
        }
        for (AutoHistoryCase historyCase : historyCases) {
            deleteById(historyCase.getId());
        }
    }
}
