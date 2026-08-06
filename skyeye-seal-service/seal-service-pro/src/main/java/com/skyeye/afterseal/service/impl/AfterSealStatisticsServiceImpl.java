/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.afterseal.classenum.AfterSealState;
import com.skyeye.afterseal.dao.AfterSealDao;
import com.skyeye.afterseal.entity.AfterSeal;
import com.skyeye.afterseal.service.AfterSealStatisticsService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.ISysDictDataService;
import com.skyeye.rest.project.service.IProProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工单统计服务实现：按状态、完成率、区域、紧急程度、项目等维度统计售后工单
 */
@Service
public class AfterSealStatisticsServiceImpl implements AfterSealStatisticsService {

    @Autowired
    private AfterSealDao afterSealDao;

    @Autowired
    private ISysDictDataService iSysDictDataService;

    @Autowired
    private IProProjectService iProProjectService;

    /**
     * 空值归类显示名称
     */
    private static final String OTHER_LABEL = "其他";

    /**
     * 柱状图固定顺序：与 AfterSealState 枚举顺序一致
     */
    private static final AfterSealState[] STATE_ORDER = {
        AfterSealState.BE_DISPATCHED,
        AfterSealState.PENDING_ORDERS,
        AfterSealState.BE_SIGNED,
        AfterSealState.BE_COMPLETED,
        AfterSealState.BE_EVALUATED,
        AfterSealState.AUDIT,
        AfterSealState.COMPLATE
    };

    @Override
    public void queryOrderStateStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AfterSeal> queryWrapper = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<AfterSeal> list = afterSealDao.selectList(queryWrapper);
        long total = list.size();
        Map<String, Long> stateCountMap = list.stream()
            .filter(o -> StrUtil.isNotEmpty(o.getState()))
            .collect(Collectors.groupingBy(AfterSeal::getState, Collectors.counting()));

        // 柱状图格式：横轴类目 + 纵轴数值，与 ECharts 等可直接使用
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (AfterSealState state : STATE_ORDER) {
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

    @Override
    public void queryOrderCompletionRateStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AfterSeal> queryWrapper = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<AfterSeal> list = afterSealDao.selectList(queryWrapper);
        long total = list.size();
        long completed = list.stream()
            .filter(o -> AfterSealState.COMPLATE.getKey().equals(o.getState()))
            .count();

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("completed", completed);
        double completionRate = total == 0 ? 0D : (completed * 100D / total);
        result.put("completionRate", Math.round(completionRate * 100D) / 100D);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryOrderStatsByRegion(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AfterSeal> queryWrapper = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<AfterSeal> list = afterSealDao.selectList(queryWrapper);
        long total = list.size();
        // 有省份的按省份分组，空的归为「其他」
        Map<String, Long> provinceStats = list.stream()
            .collect(Collectors.groupingBy(
                o -> StrUtil.isNotEmpty(o.getProvinceId()) ? o.getProvinceId() : OTHER_LABEL,
                Collectors.counting()));

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : provinceStats.entrySet()) {
            xAxisData.add(OTHER_LABEL.equals(entry.getKey()) ? OTHER_LABEL : entry.getKey());
            seriesData.add(entry.getValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryOrderStatsByUrgency(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AfterSeal> queryWrapper = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<AfterSeal> list = afterSealDao.selectList(queryWrapper);
        long total = list.size();
        // 填充紧急程度字典名称（urgencyMation.dictName）
        iSysDictDataService.setDataMation(list, AfterSeal::getUrgencyId);

        // 有紧急程度的按 id 分组，空的归为「其他」
        Map<String, Long> urgencyStats = list.stream()
            .collect(Collectors.groupingBy(
                o -> StrUtil.isNotEmpty(o.getUrgencyId()) ? o.getUrgencyId() : OTHER_LABEL,
                Collectors.counting()));

        // urgencyId -> 显示名称（字典 dictName，无则用 id）；「其他」单独映射
        Map<String, String> urgencyIdToName = new HashMap<>();
        urgencyIdToName.put(OTHER_LABEL, OTHER_LABEL);
        for (AfterSeal bean : list) {
            if (StrUtil.isEmpty(bean.getUrgencyId())) continue;
            if (urgencyIdToName.containsKey(bean.getUrgencyId())) continue;
            String name = null;
            if (bean.getUrgencyMation() != null && bean.getUrgencyMation().get("dictName") != null) {
                name = bean.getUrgencyMation().get("dictName").toString();
            }
            urgencyIdToName.put(bean.getUrgencyId(), StrUtil.isNotBlank(name) ? name : bean.getUrgencyId());
        }

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : urgencyStats.entrySet()) {
            xAxisData.add(urgencyIdToName.getOrDefault(entry.getKey(), entry.getKey()));
            seriesData.add(entry.getValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryOrderStatsByProject(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AfterSeal> queryWrapper = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<AfterSeal> list = afterSealDao.selectList(queryWrapper);
        iProProjectService.setDataMation(list, AfterSeal::getProjectId);

        long total = list.size();
        // 有项目的按 projectId 分组，空的归为「其他」
        Map<String, Long> projectStats = list.stream()
            .collect(Collectors.groupingBy(
                o -> StrUtil.isNotEmpty(o.getProjectId()) ? o.getProjectId() : OTHER_LABEL,
                Collectors.counting()));

        // projectId -> 显示名称（优先项目名称，无则 projectId）；「其他」单独映射
        Map<String, String> projectIdToName = new HashMap<>();
        projectIdToName.put(OTHER_LABEL, OTHER_LABEL);
        for (AfterSeal bean : list) {
            if (StrUtil.isEmpty(bean.getProjectId())) {
                continue;
            }
            if (projectIdToName.containsKey(bean.getProjectId())) {
                continue;
            }
            String name = bean.getProjectMation() != null && bean.getProjectMation().get("name") != null
                ? bean.getProjectMation().get("name").toString()
                : bean.getProjectId();
            projectIdToName.put(bean.getProjectId(), name);
        }

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : projectStats.entrySet()) {
            xAxisData.add(projectIdToName.getOrDefault(entry.getKey(), entry.getKey()));
            seriesData.add(entry.getValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private QueryWrapper<AfterSeal> buildTimeRangeWrapper(String startTime, String endTime) {
        QueryWrapper<AfterSeal> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime), endTime);
        }
        return queryWrapper;
    }
}
