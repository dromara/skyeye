/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.usercase.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.StatQueryUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.module.dao.AutoModuleDao;
import com.skyeye.module.entity.AutoModule;
import com.skyeye.usercase.dao.AutoCaseDao;
import com.skyeye.usercase.dao.AutoStepDao;
import com.skyeye.usercase.entity.AutoCase;
import com.skyeye.usercase.entity.AutoStep;
import com.skyeye.usercase.service.AutoCaseStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoCaseStatisticsServiceImpl
 * @Description: 用例管理统计服务实现层
 * @author: skyeye云系列--卫志强
 * @Copyright: https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AutoCaseStatisticsServiceImpl implements AutoCaseStatisticsService {

    private static final int DEFAULT_STAT_DAYS = 30;

    private static final String OTHER_LABEL = "其他";

    @Autowired
    private AutoCaseDao autoCaseDao;

    @Autowired
    private AutoStepDao autoStepDao;

    @Autowired
    private AutoModuleDao autoModuleDao;

    @Override
    public void queryOverviewAutoCase(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AutoCase> caseWrapper = buildCaseWrapper(tableSelectInfo);
        List<AutoCase> caseList = autoCaseDao.selectList(caseWrapper);

        long totalCases = caseList.size();
        long totalSteps = 0L;
        long emptyCases = totalCases;
        Object avgSteps = CommonNumConstants.NUM_ZERO;

        if (CollectionUtil.isNotEmpty(caseList)) {
            List<String> caseIds = caseList.stream().map(AutoCase::getId).collect(Collectors.toList());
            QueryWrapper<AutoStep> stepWrapper = new QueryWrapper<>();
            stepWrapper.in(MybatisPlusUtil.toColumns(AutoStep::getCaseId), caseIds);
            List<AutoStep> stepList = autoStepDao.selectList(stepWrapper);
            totalSteps = stepList.size();

            Set<String> caseIdsWithStep = stepList.stream()
                .map(AutoStep::getCaseId)
                .filter(StrUtil::isNotEmpty)
                .collect(Collectors.toSet());
            emptyCases = caseList.stream()
                .filter(c -> !caseIdsWithStep.contains(c.getId()))
                .count();

            if (totalCases > CommonNumConstants.NUM_ZERO) {
                avgSteps = CalculationUtil.divide(String.valueOf(totalSteps), String.valueOf(totalCases),
                    CommonNumConstants.NUM_TWO);
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalCases", totalCases);
        resultMap.put("totalSteps", totalSteps);
        resultMap.put("emptyCases", emptyCases);
        resultMap.put("avgSteps", avgSteps);

        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryAutoCaseTrendStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        String[] range = StatQueryUtil.resolveStatTimeRange(params, DEFAULT_STAT_DAYS);
        String fromTime = range[0];
        String endTime = range[1];
        String fromDate = fromTime.substring(0, 10);
        String toDate = endTime.substring(0, 10);

        String createTimeColumn = MybatisPlusUtil.toColumns(AutoCase::getCreateTime);
        QueryWrapper<AutoCase> queryWrapper = new QueryWrapper<>();
        applyObjectScope(queryWrapper, params.getObjectId(), params.getObjectKey());
        queryWrapper.select(
                "substr(" + createTimeColumn + ", 1, 10) as statDate",
                "count(1) as totalCount")
            .ge(createTimeColumn, fromTime)
            .le(createTimeColumn, endTime)
            .groupBy("substr(" + createTimeColumn + ", 1, 10)")
            .orderByAsc("statDate");

        List<Map<String, Object>> rows = autoCaseDao.selectMaps(queryWrapper);
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
    public void queryAutoCaseStatsByModule(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AutoCase> caseWrapper = buildCaseWrapper(tableSelectInfo);
        List<AutoCase> caseList = autoCaseDao.selectList(caseWrapper);
        long total = caseList.size();

        Map<String, Long> moduleCountMap = caseList.stream()
            .collect(Collectors.groupingBy(
                o -> StrUtil.isNotEmpty(o.getModuleId()) ? o.getModuleId() : OTHER_LABEL,
                Collectors.counting()));

        Set<String> moduleIds = new HashSet<>(moduleCountMap.keySet());
        moduleIds.remove(OTHER_LABEL);
        Map<String, String> moduleIdToName = new HashMap<>();
        moduleIdToName.put(OTHER_LABEL, OTHER_LABEL);
        if (CollectionUtil.isNotEmpty(moduleIds)) {
            List<AutoModule> modules = autoModuleDao.selectBatchIds(moduleIds);
            for (AutoModule module : modules) {
                moduleIdToName.put(module.getId(),
                    StrUtil.isNotBlank(module.getName()) ? module.getName() : module.getId());
            }
        }

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : moduleCountMap.entrySet()) {
            xAxisData.add(moduleIdToName.getOrDefault(entry.getKey(), entry.getKey()));
            seriesData.add(entry.getValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private QueryWrapper<AutoCase> buildCaseWrapper(TableSelectInfo tableSelectInfo) {
        QueryWrapper<AutoCase> queryWrapper = new QueryWrapper<>();
        applyObjectScope(queryWrapper, tableSelectInfo.getObjectId(), tableSelectInfo.getObjectKey());
        applyCreateTimeRange(queryWrapper, MybatisPlusUtil.toColumns(AutoCase::getCreateTime),
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        return queryWrapper;
    }

    private void applyObjectScope(QueryWrapper<AutoCase> queryWrapper, String objectId, String objectKey) {
        if (StrUtil.isNotEmpty(objectId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoCase::getObjectId), objectId);
        }
        if (StrUtil.isNotEmpty(objectKey)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(AutoCase::getObjectKey), objectKey);
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
