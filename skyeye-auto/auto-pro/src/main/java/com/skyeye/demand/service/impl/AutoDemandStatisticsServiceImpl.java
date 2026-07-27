/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.demand.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.StatQueryUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.demand.classenum.AutoDemandStateEnum;
import com.skyeye.demand.dao.AutoDemandDao;
import com.skyeye.demand.entity.AutoDemand;
import com.skyeye.demand.service.AutoDemandStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoDemandStatisticsServiceImpl
 * @Description: 需求管理统计服务实现层
 * @author: skyeye云系列--卫志强
 * @Copyright: https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AutoDemandStatisticsServiceImpl implements AutoDemandStatisticsService {

    private static final int DEFAULT_STAT_DAYS = 30;

    private static final AutoDemandStateEnum[] STATE_ORDER = {
        AutoDemandStateEnum.WAIT_RESEARCH,
        AutoDemandStateEnum.RESEARCH,
        AutoDemandStateEnum.WAIT_TEST,
        AutoDemandStateEnum.FINISH,
        AutoDemandStateEnum.INVALID
    };

    @Autowired
    private AutoDemandDao autoDemandDao;

    @Override
    public void queryOverviewAutoDemand(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AutoDemand> demandWrapper = buildDemandWrapper(tableSelectInfo);
        List<AutoDemand> demandList = autoDemandDao.selectList(demandWrapper);

        long totalDemands = demandList.size();
        Map<String, Long> stateCountMap = demandList.stream()
            .filter(demand -> StrUtil.isNotEmpty(demand.getState()))
            .collect(Collectors.groupingBy(AutoDemand::getState, Collectors.counting()));

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalDemands", totalDemands);
        for (AutoDemandStateEnum state : STATE_ORDER) {
            resultMap.put(state.getKey(), stateCountMap.getOrDefault(state.getKey(), 0L));
        }

        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryAutoDemandTrendStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        String[] range = StatQueryUtil.resolveStatTimeRange(params, DEFAULT_STAT_DAYS);
        String fromTime = range[0];
        String endTime = range[1];
        String fromDate = fromTime.substring(0, 10);
        String toDate = endTime.substring(0, 10);

        String createTimeColumn = MybatisPlusUtil.toColumns(AutoDemand::getCreateTime);
        QueryWrapper<AutoDemand> queryWrapper = new QueryWrapper<>();
        applyObjectScope(queryWrapper, params.getObjectId(), params.getObjectKey());
        queryWrapper.select(
                "substr(" + createTimeColumn + ", 1, 10) as statDate",
                "count(1) as totalCount")
            .ge(createTimeColumn, fromTime)
            .le(createTimeColumn, endTime)
            .groupBy("substr(" + createTimeColumn + ", 1, 10)")
            .orderByAsc("statDate");

        List<Map<String, Object>> rows = autoDemandDao.selectMaps(queryWrapper);
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
    public void queryDemandStatsByState(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AutoDemand> demandWrapper = buildDemandWrapper(tableSelectInfo);
        List<AutoDemand> demandList = autoDemandDao.selectList(demandWrapper);
        long total = demandList.size();

        Map<String, Long> stateCountMap = demandList.stream()
            .filter(demand -> StrUtil.isNotEmpty(demand.getState()))
            .collect(Collectors.groupingBy(AutoDemand::getState, Collectors.counting()));

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (AutoDemandStateEnum state : STATE_ORDER) {
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

    private QueryWrapper<AutoDemand> buildDemandWrapper(TableSelectInfo tableSelectInfo) {
        QueryWrapper<AutoDemand> queryWrapper = new QueryWrapper<>();
        applyObjectScope(queryWrapper, tableSelectInfo.getObjectId(), tableSelectInfo.getObjectKey());
        applyCreateTimeRange(queryWrapper, MybatisPlusUtil.toColumns(AutoDemand::getCreateTime),
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        return queryWrapper;
    }

    private void applyObjectScope(QueryWrapper<AutoDemand> queryWrapper, String objectId, String objectKey) {
        if (StrUtil.isNotEmpty(objectId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getObjectId), objectId);
        }
        if (StrUtil.isNotEmpty(objectKey)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDemand::getObjectKey), objectKey);
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
