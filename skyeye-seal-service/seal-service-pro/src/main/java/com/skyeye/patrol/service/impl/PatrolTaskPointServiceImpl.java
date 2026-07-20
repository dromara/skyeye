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
import com.skyeye.patrol.dao.PatrolTaskPointDao;
import com.skyeye.patrol.entity.PatrolTaskPoint;
import com.skyeye.patrol.service.PatrolTaskPointService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PatrolTaskPointServiceImpl
 * @Description: 巡检任务点位关联服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "巡检任务点位关联", groupName = "巡检任务点位关联", manageShow = false)
public class PatrolTaskPointServiceImpl extends SkyeyeBusinessServiceImpl<PatrolTaskPointDao, PatrolTaskPoint> implements PatrolTaskPointService {

    @Override
    public void deleteByParentId(String taskId) {
        if (StrUtil.isEmpty(taskId)) {
            return;
        }
        QueryWrapper<PatrolTaskPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolTaskPoint::getTaskId), taskId);
        remove(queryWrapper);
    }

    @Override
    public List<String> selectByParentId(String taskId) {
        QueryWrapper<PatrolTaskPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolTaskPoint::getTaskId), taskId);
        List<PatrolTaskPoint> list = list(queryWrapper);
        return list.stream().map(PatrolTaskPoint::getPointId).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> selectMapByParentId(List<String> taskIds) {
        if (CollectionUtil.isEmpty(taskIds)) {
            return Collections.emptyMap();
        }
        QueryWrapper<PatrolTaskPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(PatrolTaskPoint::getTaskId), taskIds);
        List<PatrolTaskPoint> list = list(queryWrapper);
        return list.stream().collect(Collectors.groupingBy(
            PatrolTaskPoint::getTaskId,
            Collectors.mapping(PatrolTaskPoint::getPointId, Collectors.toList())
        ));
    }

    @Override
    public void saveList(String taskId, List<String> pointIds) {
        deleteByParentId(taskId);
        if (CollectionUtil.isNotEmpty(pointIds)) {
            List<PatrolTaskPoint> taskPointList = pointIds.stream().map(pointId -> {
                PatrolTaskPoint taskPoint = new PatrolTaskPoint();
                taskPoint.setTaskId(taskId);
                taskPoint.setPointId(pointId);
                return taskPoint;
            }).collect(Collectors.toList());
            createEntity(taskPointList, StrUtil.EMPTY);
        }
    }
}
