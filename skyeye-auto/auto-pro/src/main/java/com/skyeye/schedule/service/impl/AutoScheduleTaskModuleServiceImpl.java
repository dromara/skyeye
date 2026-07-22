/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.schedule.dao.AutoScheduleTaskModuleDao;
import com.skyeye.schedule.entity.AutoScheduleTaskModule;
import com.skyeye.schedule.service.AutoScheduleTaskModuleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 定时任务与模块关联服务层
 */
@Service
@SkyeyeService(name = "定时任务模块关联", groupName = "定时任务模块关联", manageShow = false)
public class AutoScheduleTaskModuleServiceImpl
    extends SkyeyeBusinessServiceImpl<AutoScheduleTaskModuleDao, AutoScheduleTaskModule>
    implements AutoScheduleTaskModuleService {

    @Override
    public void deleteByParentId(String parentId) {
        if (StrUtil.isEmpty(parentId)) {
            return;
        }
        QueryWrapper<AutoScheduleTaskModule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTaskModule::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<String> selectByParentId(String parentId) {
        QueryWrapper<AutoScheduleTaskModule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoScheduleTaskModule::getParentId), parentId);
        List<AutoScheduleTaskModule> list = list(queryWrapper);
        return list.stream().map(AutoScheduleTaskModule::getModuleId).collect(Collectors.toList());
    }

    @Override
    public void saveList(String parentId, List<String> moduleIds) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(moduleIds)) {
            List<AutoScheduleTaskModule> list = moduleIds.stream().map(moduleId -> {
                AutoScheduleTaskModule bean = new AutoScheduleTaskModule();
                bean.setParentId(parentId);
                bean.setModuleId(moduleId);
                return bean;
            }).collect(Collectors.toList());
            createEntity(list, StrUtil.EMPTY);
        }
    }
}
