/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.schedule.dao.AutoScheduleTaskCaseDao;
import com.skyeye.schedule.entity.AutoScheduleTaskCase;
import com.skyeye.schedule.service.AutoScheduleTaskCaseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 定时任务与用例关联服务层
 */
@Service
@SkyeyeService(name = "定时任务用例关联", groupName = "定时任务用例关联", manageShow = false)
public class AutoScheduleTaskCaseServiceImpl
    extends SkyeyeBusinessServiceImpl<AutoScheduleTaskCaseDao, AutoScheduleTaskCase>
    implements AutoScheduleTaskCaseService {

    @Override
    public void deleteByParentId(String taskId) {
        if (StrUtil.isEmpty(taskId)) {
            return;
        }
        QueryWrapper<AutoScheduleTaskCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTaskCase::getTaskId), taskId);
        remove(queryWrapper);
    }

    @Override
    public List<String> selectByParentId(String taskId) {
        QueryWrapper<AutoScheduleTaskCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTaskCase::getTaskId), taskId);
        List<AutoScheduleTaskCase> list = list(queryWrapper);
        return list.stream().map(AutoScheduleTaskCase::getCaseId).collect(Collectors.toList());
    }

    @Override
    public void saveList(String taskId, List<String> caseIds) {
        deleteByParentId(taskId);
        if (CollectionUtil.isNotEmpty(caseIds)) {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            List<AutoScheduleTaskCase> list = caseIds.stream().map(caseId -> {
                AutoScheduleTaskCase bean = new AutoScheduleTaskCase();
                bean.setTaskId(taskId);
                bean.setCaseId(caseId);
                return bean;
            }).collect(Collectors.toList());
            createEntity(list, userId);
        }
    }
}
