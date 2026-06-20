/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.repair.dao.EquipmentRepairOrderDao;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import com.skyeye.repair.service.EquipmentRepairStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报修维修统计服务层（月度趋势按派工时间 {@link EquipmentRepairOrder#getDispatchTime()} 统计）
 *
 * @author skyeye云系列--卫志强
 * @Copyright 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 */
@Service
public class EquipmentRepairStatisticsServiceImpl implements EquipmentRepairStatisticsService {

    @Autowired
    private EquipmentRepairOrderDao equipmentRepairOrderDao;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EquipmentRepairOrderService equipmentRepairOrderService;

    @Override
    public void queryRepairMonthlyTrendStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime;
        String endTime;
        if (StrUtil.isNotEmpty(tableSelectInfo.getStartTime()) && StrUtil.isNotEmpty(tableSelectInfo.getEndTime())) {
            startTime = tableSelectInfo.getStartTime();
            endTime = tableSelectInfo.getEndTime();
        } else {
            startTime = DateUtil.formatDate2Str(
                DateUtil.getAfDate(DateUtil.getPointTime(DateUtil.getYmdTimeAndToString(), DateUtil.YYYY_MM_DD), -30, "d"),
                DateUtil.YYYY_MM_DD);
            endTime = DateUtil.getYmdTimeAndToString();
        }

        String startMonth = DateUtil.formatDate2Str(DateUtil.getPointTime(startTime, DateUtil.YYYY_MM_DD), DateUtil.YYYY_MM);
        String endMonth = DateUtil.formatDate2Str(DateUtil.getPointTime(endTime, DateUtil.YYYY_MM_DD), DateUtil.YYYY_MM);
        List<String> monthList = DateUtil.getMonth(startMonth, endMonth);

        QueryWrapper<EquipmentRepairOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getDispatchTime), startTime)
            .le(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getDispatchTime), endTime);

        List<EquipmentRepairOrder> orderList = equipmentRepairOrderDao.selectList(queryWrapper);
        long total = orderList.size();

        Map<String, Long> monthCountMap = orderList.stream()
            .filter(order -> StrUtil.isNotEmpty(order.getDispatchTime()))
            .collect(Collectors.groupingBy(order -> {
                Date pointTime = DateUtil.getPointTime(order.getDispatchTime(), DateUtil.YYYY_MM_DD);
                return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM);
            }, Collectors.counting()));

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        Long defaultValue = Long.valueOf(CommonNumConstants.NUM_ZERO);
        for (String ym : monthList) {
            Date ymDate = DateUtil.getPointTime(ym, DateUtil.YYYY_MM);
            xAxisData.add(DateUtil.formatDate2Str(ymDate, "yyyy年MM月"));
            seriesData.add(monthCountMap.getOrDefault(ym, defaultValue));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryRepairStatsByEquipmentName(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<EquipmentRepairOrder> queryWrapper = new QueryWrapper<>();

        List<EquipmentRepairOrder> orderList = equipmentRepairOrderDao.selectList(queryWrapper);
        long total = orderList.size();

        equipmentService.setDataMation(orderList, EquipmentRepairOrder::getEquipmentId);

        // 按 equipmentId 分组统计
        Map<String, Long> equipmentStats = orderList.stream()
            .collect(Collectors.groupingBy(EquipmentRepairOrder::getEquipmentId, Collectors.counting()));

        Map<String, String> equipmentIdToName = new HashMap<>();
        for (EquipmentRepairOrder order : orderList) {
            if (equipmentIdToName.containsKey(order.getEquipmentId())) {
                continue;
            }
            Map<String, Object> equipmentMation = order.getEquipmentMation();
            String name = equipmentMation != null && StrUtil.isNotEmpty(String.valueOf(equipmentMation.get("name")))
                ? String.valueOf(equipmentMation.get("name"))
                : order.getEquipmentId();
            equipmentIdToName.put(order.getEquipmentId(), name);
        }

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : equipmentStats.entrySet()) {
            xAxisData.add(equipmentIdToName.getOrDefault(entry.getKey(), entry.getKey()));
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
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.queryPageList(inputObject, outputObject);
    }
}
