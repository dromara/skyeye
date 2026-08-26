/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.knowledge.classenum.KnowledgeSyncResultEnum;
import com.skyeye.knowledge.dao.KnowledgeSyncHistoryDao;
import com.skyeye.knowledge.entity.KnowledgeSyncHistory;
import com.skyeye.knowledge.service.KnowledgeSyncHistoryService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "AI知识库同步历史", groupName = "AI知识库", allowDynamicAttrKey = false)
public class KnowledgeSyncHistoryServiceImpl
    extends SkyeyeBusinessServiceImpl<KnowledgeSyncHistoryDao, KnowledgeSyncHistory>
    implements KnowledgeSyncHistoryService {

    private static final int ERROR_MSG_LIMIT = 1000;

    @Override
    protected QueryWrapper<KnowledgeSyncHistory> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<KnowledgeSyncHistory> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.apply("1 = 0");
            return queryWrapper;
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeSyncHistory::getKnowledgeId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public void saveHistory(String knowledgeId, Integer triggerType, Integer status, Integer syncCount,
                            String startTime, String endTime, String errorMsg) {
        if (StrUtil.isBlank(knowledgeId)) {
            return;
        }
        KnowledgeSyncHistory history = new KnowledgeSyncHistory();
        history.setKnowledgeId(knowledgeId);
        history.setTriggerType(triggerType);
        history.setStatus(status);
        history.setSyncCount(syncCount == null ? 0 : syncCount);
        history.setStartTime(startTime);
        history.setEndTime(endTime);
        if (StrUtil.isNotBlank(errorMsg)) {
            history.setErrorMsg(errorMsg.length() > ERROR_MSG_LIMIT
                ? errorMsg.substring(0, ERROR_MSG_LIMIT) : errorMsg);
        }
        createEntity(history, StrUtil.EMPTY);
    }

    @Override
    public String createRunningHistory(String knowledgeId, Integer triggerType, String startTime) {
        if (StrUtil.isBlank(knowledgeId)) {
            return StrUtil.EMPTY;
        }
        KnowledgeSyncHistory history = new KnowledgeSyncHistory();
        history.setKnowledgeId(knowledgeId);
        history.setTriggerType(triggerType);
        history.setStatus(KnowledgeSyncResultEnum.RUNNING.getKey());
        history.setSyncCount(0);
        history.setStartTime(startTime);
        history.setEndTime(StrUtil.EMPTY);
        createEntity(history, StrUtil.EMPTY);
        return history.getId();
    }

    @Override
    public void finishHistory(String historyId, Integer status, Integer syncCount, String endTime, String errorMsg) {
        if (StrUtil.isBlank(historyId)) {
            return;
        }
        UpdateWrapper<KnowledgeSyncHistory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, historyId);
        updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeSyncHistory::getStatus), status);
        updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeSyncHistory::getSyncCount), syncCount == null ? 0 : syncCount);
        updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeSyncHistory::getEndTime), endTime);
        if (StrUtil.isNotBlank(errorMsg)) {
            String msg = errorMsg.length() > ERROR_MSG_LIMIT ? errorMsg.substring(0, ERROR_MSG_LIMIT) : errorMsg;
            updateWrapper.set(MybatisPlusUtil.toColumns(KnowledgeSyncHistory::getErrorMsg), msg);
        }
        update(updateWrapper);
    }

    @Override
    public boolean hasRunning(String knowledgeId) {
        if (StrUtil.isBlank(knowledgeId)) {
            return false;
        }
        QueryWrapper<KnowledgeSyncHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeSyncHistory::getKnowledgeId), knowledgeId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeSyncHistory::getStatus), KnowledgeSyncResultEnum.RUNNING.getKey());
        return count(queryWrapper) > 0;
    }

    @Override
    public void deleteByKnowledgeId(String knowledgeId) {
        QueryWrapper<KnowledgeSyncHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgeSyncHistory::getKnowledgeId), knowledgeId);
        remove(queryWrapper);
    }

}
