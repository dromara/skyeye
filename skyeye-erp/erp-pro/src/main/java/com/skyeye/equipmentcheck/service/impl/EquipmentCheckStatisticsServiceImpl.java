/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentcheck.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.NumberParseUtil;
import com.skyeye.common.util.StatQueryUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentcheck.classenum.EquipmentCheckResult;
import com.skyeye.equipmentcheck.dao.EquipmentCheckOrderDao;
import com.skyeye.equipmentcheck.entity.EquipmentCheckOrder;
import com.skyeye.equipmentcheck.service.EquipmentCheckOrderService;
import com.skyeye.equipmentcheck.service.EquipmentCheckStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentCheckStatisticsServiceImpl
 * @Description: 设备点检统计服务实现层
 */
@Service
public class EquipmentCheckStatisticsServiceImpl implements EquipmentCheckStatisticsService {

    private static final int DEFAULT_STAT_DAYS = 7;

    private static final FlowableStateEnum[] STATE_ORDER = {
        FlowableStateEnum.DRAFT,
        FlowableStateEnum.IN_EXAMINE,
        FlowableStateEnum.PASS,
        FlowableStateEnum.REJECT,
        FlowableStateEnum.INVALID,
        FlowableStateEnum.REVOKE
    };

    @Autowired
    private EquipmentCheckOrderDao equipmentCheckOrderDao;

    @Autowired
    private EquipmentCheckOrderService equipmentCheckOrderService;

    @Override
    public void queryTodayCheckedTotal(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", countTodayOrders(null));
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryTodayAbnormalCheckTotal(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", countTodayOrders(EquipmentCheckResult.ABNORMAL.getKey()));
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryCheckStatsByCheckTime(InputObject inputObject, OutputObject outputObject) {
        outputCheckDayTrend(inputObject, outputObject, null);
    }

    @Override
    public void queryAbnormalCheckStatsByCheckTime(InputObject inputObject, OutputObject outputObject) {
        outputCheckDayTrend(inputObject, outputObject, EquipmentCheckResult.ABNORMAL.getKey());
    }

    @Override
    public void queryCheckOrderStateStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<EquipmentCheckOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<EquipmentCheckOrder> list = equipmentCheckOrderService.list(queryWrapper);
        long total = list.size();
        Map<String, Long> stateCountMap = list.stream()
            .filter(o -> StrUtil.isNotEmpty(o.getState()))
            .collect(Collectors.groupingBy(EquipmentCheckOrder::getState, Collectors.counting()));

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (FlowableStateEnum state : STATE_ORDER) {
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
    public void queryCheckOrderToRepairRateStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<EquipmentCheckOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        // 仅统计异常点检单；正常单不入转修饼图
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentCheckOrder::getCheckResult),
            EquipmentCheckResult.ABNORMAL.getKey());

        List<EquipmentCheckOrder> list = equipmentCheckOrderService.list(queryWrapper);
        long toRepairCount = list.stream()
            .filter(o -> StrUtil.isNotEmpty(o.getRepairOrderId()))
            .count();
        long notRepairCount = list.size() - toRepairCount;

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> toRepairRow = new HashMap<>(2);
        toRepairRow.put("name", "已转维修");
        toRepairRow.put("value", String.valueOf(toRepairCount));
        rows.add(toRepairRow);5

        Map<String, Object> notRepairRow = new HashMap<>(2);
        notRepairRow.put("name", "未转维修");
        notRepairRow.put("value", String.valueOf(notRepairCount));
        rows.add(notRepairRow);

        outputObject.setBeans(rows);
        outputObject.settotal(rows.size());
    }

    private Long countTodayOrders(Integer checkResult) {
        String today = LocalDate.now().toString();
        QueryWrapper<EquipmentCheckOrder> wrapper = new QueryWrapper<>();
        wrapper.likeRight(MybatisPlusUtil.toColumns(EquipmentCheckOrder::getCheckTime), today);
        if (checkResult != null) {
            wrapper.eq(MybatisPlusUtil.toColumns(EquipmentCheckOrder::getCheckResult), checkResult);
        }
        return equipmentCheckOrderDao.selectCount(wrapper);
    }

    /**
     * 按点检时间日趋势（对齐仓库 StatQueryUtil + DateUtil.getDays 写法）
     */
    private void outputCheckDayTrend(InputObject inputObject, OutputObject outputObject, Integer checkResult) {
        TableSelectInfo params = inputObject.getParams(TableSelectInfo.class);
        String[] range = StatQueryUtil.resolveStatTimeRange(params, DEFAULT_STAT_DAYS);
        String fromTime = range[0];
        String endTime = range[1];
        String fromDate = fromTime.substring(0, 10);
        String toDate = endTime.substring(0, 10);

        String checkTimeColumn = MybatisPlusUtil.toColumns(EquipmentCheckOrder::getCheckTime);
        QueryWrapper<EquipmentCheckOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                "substr(" + checkTimeColumn + ", 1, 10) as statDate",
                "count(1) as totalCount")
            .ge(checkTimeColumn, fromTime)
            .le(checkTimeColumn, endTime)
            .groupBy("substr(" + checkTimeColumn + ", 1, 10)")
            .orderByAsc("statDate");
        if (checkResult != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentCheckOrder::getCheckResult), checkResult);
        }

        List<Map<String, Object>> rows = equipmentCheckOrderDao.selectMaps(queryWrapper);
        Map<String, Map<String, Object>> dayMap = new HashMap<>();
        if (rows != null) {
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

    private QueryWrapper<EquipmentCheckOrder> buildTimeRangeWrapper(String startTime, String endTime) {
        QueryWrapper<EquipmentCheckOrder> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(EquipmentCheckOrder::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(EquipmentCheckOrder::getCreateTime), endTime);
        }
        return queryWrapper;
    }

}
