/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.history.service.impl;

import cn.hutool.core.collection.CollectionUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    protected QueryWrapper<AutoHistoryCase> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AutoHistoryCase> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoHistoryCase::getCaseId), commonPageInfo.getObjectId());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(AutoHistoryCase::getExecuteStartTime));
        return queryWrapper;
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
}
