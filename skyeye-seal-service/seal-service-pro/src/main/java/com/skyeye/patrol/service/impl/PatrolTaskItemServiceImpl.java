/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.patrol.dao.PatrolTaskItemDao;
import com.skyeye.patrol.entity.PatrolTaskItem;
import com.skyeye.patrol.service.PatrolTaskItemService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PatrolTaskItemServiceImpl
 * @Description: 巡检任务项目关联服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "巡检任务项目关联", groupName = "巡检任务项目关联", manageShow = false)
public class PatrolTaskItemServiceImpl extends SkyeyeBusinessServiceImpl<PatrolTaskItemDao, PatrolTaskItem> implements PatrolTaskItemService {

    @Override
    public void deleteByParentId(String taskId) {
        if (StrUtil.isEmpty(taskId)) {
            return;
        }
        QueryWrapper<PatrolTaskItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolTaskItem::getTaskId), taskId);
        remove(queryWrapper);
    }

    @Override
    public List<String> selectByParentId(String taskId) {
        QueryWrapper<PatrolTaskItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolTaskItem::getTaskId), taskId);
        List<PatrolTaskItem> list = list(queryWrapper);
        return list.stream().map(PatrolTaskItem::getItemId).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> selectMapByParentId(List<String> taskIds) {
        if (CollectionUtil.isEmpty(taskIds)) {
            return Collections.emptyMap();
        }
        QueryWrapper<PatrolTaskItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(PatrolTaskItem::getTaskId), taskIds);
        List<PatrolTaskItem> list = list(queryWrapper);
        return list.stream().collect(Collectors.groupingBy(
            PatrolTaskItem::getTaskId,
            Collectors.mapping(PatrolTaskItem::getItemId, Collectors.toList())
        ));
    }

    @Override
    public void saveList(String taskId, List<String> itemIds) {
        deleteByParentId(taskId);
        if (CollectionUtil.isNotEmpty(itemIds)) {
            List<PatrolTaskItem> taskItemList = itemIds.stream().map(itemId -> {
                PatrolTaskItem taskItem = new PatrolTaskItem();
                taskItem.setTaskId(taskId);
                taskItem.setItemId(itemId);
                return taskItem;
            }).collect(Collectors.toList());
            createEntity(taskItemList, StrUtil.EMPTY);
        }
    }
}
