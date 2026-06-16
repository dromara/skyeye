/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.worktime.dao.CheckWorkTimePointDao;
import com.skyeye.worktime.entity.CheckWorkTimePoint;
import com.skyeye.worktime.service.CheckWorkTimePointService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CheckWorkTimePointServiceImpl
 * @Description: 考勤班次线上打卡点位服务层
 */
@Service
@SkyeyeService(name = "考勤班次线上打卡点位", groupName = "考勤班次", manageShow = false)
public class CheckWorkTimePointServiceImpl extends SkyeyeBusinessServiceImpl<CheckWorkTimePointDao, CheckWorkTimePoint> implements CheckWorkTimePointService {

    @Override
    public void saveCheckWorkTimePointList(String timeId, List<CheckWorkTimePoint> beans, String userId) {
        deleteByTimeId(timeId);
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        int index = 1;
        for (CheckWorkTimePoint point : beans) {
            if (StrUtil.isBlank(point.getLongitude()) || StrUtil.isBlank(point.getLatitude())) {
                throw new CustomException("打卡点位经纬度不能为空。");
            }
            point.setTimeId(timeId);
            if (point.getRadius() == null || point.getRadius() <= 0) {
                point.setRadius(500);
            }
            if (point.getOrderBy() == null) {
                point.setOrderBy(index);
            }
            index++;
        }
        createEntity(beans, userId);
    }

    @Override
    public void deleteByTimeId(String timeId) {
        QueryWrapper<CheckWorkTimePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CheckWorkTimePoint::getTimeId), timeId);
        remove(queryWrapper);
    }

    @Override
    public List<CheckWorkTimePoint> selectByTimeId(String timeId) {
        QueryWrapper<CheckWorkTimePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CheckWorkTimePoint::getTimeId), timeId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(CheckWorkTimePoint::getOrderBy));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<CheckWorkTimePoint>> selectByTimeId(List<String> timeIds) {
        if (CollectionUtil.isEmpty(timeIds)) {
            return MapUtil.newHashMap();
        }
        QueryWrapper<CheckWorkTimePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(CheckWorkTimePoint::getTimeId), timeIds);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(CheckWorkTimePoint::getOrderBy));
        return list(queryWrapper).stream().collect(Collectors.groupingBy(CheckWorkTimePoint::getTimeId));
    }

}
