/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.api.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.api.dao.AutoApiDao;
import com.skyeye.api.entity.AutoApi;
import com.skyeye.api.service.AutoApiStatisticsService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.StatQueryUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.usercase.dao.AutoStepApiDao;
import com.skyeye.usercase.entity.AutoStepApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoApiStatisticsServiceImpl
 * @Description: 接口管理统计服务实现层
 * @author: skyeye云系列--卫志强
 * @Copyright: https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AutoApiStatisticsServiceImpl implements AutoApiStatisticsService {

    private static final int DEFAULT_STAT_DAYS = 30;

    private static final String OTHER_LABEL = "其他";

    @Autowired
    private AutoApiDao autoApiDao;

    @Autowired
    private AutoStepApiDao autoStepApiDao;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public void queryOverviewAutoApi(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AutoApi> apiWrapper = buildApiWrapper(tableSelectInfo);
        List<AutoApi> apiList = autoApiDao.selectList(apiWrapper);

        long totalApis = apiList.size();
        long boundModuleApis = apiList.stream()
            .filter(api -> StrUtil.isNotEmpty(api.getModuleId()))
            .count();
        long unboundModuleApis = totalApis - boundModuleApis;

        long usedApis = CommonNumConstants.NUM_ZERO;
        if (CollectionUtil.isNotEmpty(apiList)) {
            List<String> apiIds = apiList.stream().map(AutoApi::getId).collect(Collectors.toList());
            QueryWrapper<AutoStepApi> stepApiWrapper = new QueryWrapper<>();
            stepApiWrapper.in(MybatisPlusUtil.toColumns(AutoStepApi::getApiId), apiIds);
            List<AutoStepApi> stepApiList = autoStepApiDao.selectList(stepApiWrapper);
            usedApis = stepApiList.stream()
                .map(AutoStepApi::getApiId)
                .filter(StrUtil::isNotEmpty)
                .distinct()
                .count();
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalApis", totalApis);
        resultMap.put("boundModuleApis", boundModuleApis);
        resultMap.put("unboundModuleApis", unboundModuleApis);
        resultMap.put("usedApis", usedApis);

        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryAutoApiTrendStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        String[] range = StatQueryUtil.resolveStatTimeRange(params, DEFAULT_STAT_DAYS);
        String fromTime = range[0];
        String endTime = range[1];
        String fromDate = fromTime.substring(0, 10);
        String toDate = endTime.substring(0, 10);

        String createTimeColumn = MybatisPlusUtil.toColumns(AutoApi::getCreateTime);
        QueryWrapper<AutoApi> queryWrapper = new QueryWrapper<>();
        applyObjectScope(queryWrapper, params.getObjectId(), params.getObjectKey());
        queryWrapper.select(
                "substr(" + createTimeColumn + ", 1, 10) as statDate",
                "count(1) as totalCount")
            .ge(createTimeColumn, fromTime)
            .le(createTimeColumn, endTime)
            .groupBy("substr(" + createTimeColumn + ", 1, 10)")
            .orderByAsc("statDate");

        List<Map<String, Object>> rows = autoApiDao.selectMaps(queryWrapper);
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
    public void queryApiStatsByCreator(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AutoApi> apiWrapper = buildApiWrapper(tableSelectInfo);
        List<AutoApi> apiList = autoApiDao.selectList(apiWrapper);
        long total = apiList.size();

        List<String> createIds = apiList.stream()
            .map(AutoApi::getCreateId)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        Map<String, String> creatorIdToName = new HashMap<>();
        creatorIdToName.put(OTHER_LABEL, OTHER_LABEL);
        if (CollectionUtil.isNotEmpty(createIds)) {
            Map<String, Map<String, Object>> userMap = iAuthUserService.queryUserNameList(createIds);
            if (userMap != null) {
                userMap.forEach((id, mation) -> {
                    String name = null;
                    if (mation != null && mation.get("name") != null) {
                        name = mation.get("name").toString();
                    }
                    creatorIdToName.put(id, StrUtil.isNotBlank(name) ? name : id);
                });
            }
        }

        Map<String, Long> creatorStats = apiList.stream()
            .collect(Collectors.groupingBy(api -> {
                if (StrUtil.isEmpty(api.getCreateId())) {
                    return OTHER_LABEL;
                }
                return creatorIdToName.containsKey(api.getCreateId()) ? api.getCreateId() : OTHER_LABEL;
            }, Collectors.counting()));

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : creatorStats.entrySet()) {
            xAxisData.add(creatorIdToName.getOrDefault(entry.getKey(), entry.getKey()));
            seriesData.add(entry.getValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private QueryWrapper<AutoApi> buildApiWrapper(TableSelectInfo tableSelectInfo) {
        QueryWrapper<AutoApi> queryWrapper = new QueryWrapper<>();
        applyObjectScope(queryWrapper, tableSelectInfo.getObjectId(), tableSelectInfo.getObjectKey());
        applyCreateTimeRange(queryWrapper, MybatisPlusUtil.toColumns(AutoApi::getCreateTime),
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        return queryWrapper;
    }

    private void applyObjectScope(QueryWrapper<AutoApi> queryWrapper, String objectId, String objectKey) {
        if (StrUtil.isNotEmpty(objectId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoApi::getObjectId), objectId);
        }
        if (StrUtil.isNotEmpty(objectKey)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoApi::getObjectKey), objectKey);
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
