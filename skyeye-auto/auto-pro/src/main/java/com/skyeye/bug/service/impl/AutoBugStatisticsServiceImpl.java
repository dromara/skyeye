/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.bug.classenum.BugState;
import com.skyeye.bug.dao.AutoBugDao;
import com.skyeye.bug.entity.AutoBug;
import com.skyeye.bug.service.AutoBugStatisticsService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.StatQueryUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoBugStatisticsServiceImpl
 * @Description: bug管理统计服务实现层
 * @author: skyeye云系列--卫志强
 * @Copyright: https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AutoBugStatisticsServiceImpl implements AutoBugStatisticsService {

    private static final int DEFAULT_STAT_DAYS = 30;

    private static final BugState[] STATE_ORDER = {
        BugState.UNRESOLVED,
        BugState.TO_BE_RETURNED,
        BugState.RESOLVED
    };

    @Autowired
    private AutoBugDao autoBugDao;

    @Override
    public void queryOverviewAutoBug(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AutoBug> bugWrapper = buildBugWrapper(tableSelectInfo);
        List<AutoBug> bugList = autoBugDao.selectList(bugWrapper);

        long totalBugs = bugList.size();
        Map<String, Long> stateCountMap = bugList.stream()
            .filter(bug -> StrUtil.isNotEmpty(bug.getState()))
            .collect(Collectors.groupingBy(AutoBug::getState, Collectors.counting()));

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalBugs", totalBugs);
        resultMap.put(BugState.UNRESOLVED.getKey(),
            stateCountMap.getOrDefault(BugState.UNRESOLVED.getKey(), 0L));
        resultMap.put(BugState.TO_BE_RETURNED.getKey(),
            stateCountMap.getOrDefault(BugState.TO_BE_RETURNED.getKey(), 0L));
        resultMap.put(BugState.RESOLVED.getKey(),
            stateCountMap.getOrDefault(BugState.RESOLVED.getKey(), 0L));

        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryAutoBugTrendStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        String[] range = StatQueryUtil.resolveStatTimeRange(params, DEFAULT_STAT_DAYS);
        String fromTime = range[0];
        String endTime = range[1];
        String fromDate = fromTime.substring(0, 10);
        String toDate = endTime.substring(0, 10);

        String createTimeColumn = MybatisPlusUtil.toColumns(AutoBug::getCreateTime);
        QueryWrapper<AutoBug> queryWrapper = new QueryWrapper<>();
        applyObjectScope(queryWrapper, params.getObjectId(), params.getObjectKey());
        queryWrapper.select(
                "substr(" + createTimeColumn + ", 1, 10) as statDate",
                "count(1) as totalCount")
            .ge(createTimeColumn, fromTime)
            .le(createTimeColumn, endTime)
            .groupBy("substr(" + createTimeColumn + ", 1, 10)")
            .orderByAsc("statDate");

        List<Map<String, Object>> rows = autoBugDao.selectMaps(queryWrapper);
        Map<String, Map<String, Object>> dayMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(rows)) {
            for (Map<String, Object> row : rows) {
                dayMap.put(String.valueOf(row.get("statDate")), row);
            }
        }

        List<String> dayList = DateUtil.getDays(fromDate, toDate);
        List<Long> seriesData = new ArrayList<>();
        long total = 0L;
        for (String day : dayList) {
            Map<String, Object> row = dayMap.get(day);
            long totalCount = row == null ? 0L : NumberParseUtil.parseLong(row.get("totalCount"));
            total += totalCount;
            seriesData.add(totalCount);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", dayList);
        result.put("seriesData", seriesData);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryBugStatsByState(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AutoBug> bugWrapper = buildBugWrapper(tableSelectInfo);
        List<AutoBug> bugList = autoBugDao.selectList(bugWrapper);
        long total = bugList.size();

        Map<String, Long> stateCountMap = bugList.stream()
            .filter(bug -> StrUtil.isNotEmpty(bug.getState()))
            .collect(Collectors.groupingBy(AutoBug::getState, Collectors.counting()));

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (BugState state : STATE_ORDER) {
            xAxisData.add(state.getValue());
            seriesData.add(stateCountMap.getOrDefault(state.getKey(), 0L));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private QueryWrapper<AutoBug> buildBugWrapper(TableSelectInfo tableSelectInfo) {
        QueryWrapper<AutoBug> queryWrapper = new QueryWrapper<>();
        applyObjectScope(queryWrapper, tableSelectInfo.getObjectId(), tableSelectInfo.getObjectKey());
        applyCreateTimeRange(queryWrapper, MybatisPlusUtil.toColumns(AutoBug::getCreateTime),
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        return queryWrapper;
    }

    private void applyObjectScope(QueryWrapper<AutoBug> queryWrapper, String objectId, String objectKey) {
        if (StrUtil.isNotEmpty(objectId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getObjectId), objectId);
        }
        if (StrUtil.isNotEmpty(objectKey)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoBug::getObjectKey), objectKey);
        }
    }

    private void applyCreateTimeRange(QueryWrapper<?> queryWrapper, String createTimeColumn,
                                      String startTime, String endTime) {
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(createTimeColumn, startTime.length() == 10 ? startTime + " 00:00:00" : startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(createTimeColumn, endTime.length() == 10 ? endTime + " 23:59:59" : endTime);
        }
    }

}
