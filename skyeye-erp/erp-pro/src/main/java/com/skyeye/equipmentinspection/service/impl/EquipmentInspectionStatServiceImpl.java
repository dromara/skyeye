/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionCheckResult;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionOrderState;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionStatService;
import com.skyeye.eve.service.IAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentInspectionStatServiceImpl
 * @Description: 设备巡检统计
 */
@Service
public class EquipmentInspectionStatServiceImpl implements EquipmentInspectionStatService {

    private static final String OTHER_LABEL = "其他";

    private static final EquipmentInspectionOrderState[] STATE_ORDER = {
        EquipmentInspectionOrderState.BE_DISPATCHED,
        EquipmentInspectionOrderState.PENDING_ORDERS,
        EquipmentInspectionOrderState.BE_EXECUTED,
        EquipmentInspectionOrderState.BE_AUDITED,
        EquipmentInspectionOrderState.COMPLETED
    };

    private static final EquipmentInspectionCheckResult[] CHECK_RESULT_ORDER = {
        EquipmentInspectionCheckResult.NORMAL,
        EquipmentInspectionCheckResult.ABNORMAL
    };

    @Autowired
    private EquipmentInspectionOrderService equipmentInspectionOrderService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public void queryTodayInspectedTotal(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", countTodayOrders(null));
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryTodayAbnormalInspectionTotal(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", countTodayOrders(EquipmentInspectionCheckResult.ABNORMAL.getKey()));
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryInspectionOrderStateStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<EquipmentInspectionOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<EquipmentInspectionOrder> list = equipmentInspectionOrderService.list(queryWrapper);
        long total = list.size();
        Map<Integer, Long> stateCountMap = list.stream()
            .filter(o -> o.getState() != null)
            .collect(Collectors.groupingBy(EquipmentInspectionOrder::getState, Collectors.counting()));

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (EquipmentInspectionOrderState state : STATE_ORDER) {
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
    public void queryInspectionOrderCompletionRateStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<EquipmentInspectionOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<EquipmentInspectionOrder> list = equipmentInspectionOrderService.list(queryWrapper);
        long total = list.size();
        long completed = list.stream()
            .filter(o -> EquipmentInspectionOrderState.COMPLETED.getKey().equals(o.getState()))
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
    public void queryInspectionOrderStatsByCheckResult(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<EquipmentInspectionOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        // 仅统计已填检查结果的单；未完成/未填报不入图
        queryWrapper.isNotNull(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getCheckResult));

        List<EquipmentInspectionOrder> list = equipmentInspectionOrderService.list(queryWrapper);
        Map<Integer, Long> resultCountMap = list.stream()
            .collect(Collectors.groupingBy(EquipmentInspectionOrder::getCheckResult, Collectors.counting()));

        List<Map<String, Object>> rows = new ArrayList<>();
        for (EquipmentInspectionCheckResult checkResult : CHECK_RESULT_ORDER) {
            Map<String, Object> row = new HashMap<>(2);
            row.put("name", checkResult.getValue());
            row.put("value", String.valueOf(resultCountMap.getOrDefault(checkResult.getKey(), 0L)));
            rows.add(row);
        }

        outputObject.setBeans(rows);
        outputObject.settotal(rows.size());
    }

    @Override
    public void queryInspectionOrderStatsByEquipment(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<EquipmentInspectionOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<EquipmentInspectionOrder> list = equipmentInspectionOrderService.list(queryWrapper);
        equipmentService.setDataMation(list, EquipmentInspectionOrder::getEquipmentId);

        long total = list.size();
        Map<String, Long> equipmentStats = list.stream()
            .collect(Collectors.groupingBy(
                o -> StrUtil.isNotEmpty(o.getEquipmentId()) ? o.getEquipmentId() : OTHER_LABEL,
                Collectors.counting()));

        Map<String, String> equipmentIdToName = new HashMap<>();
        equipmentIdToName.put(OTHER_LABEL, OTHER_LABEL);
        for (EquipmentInspectionOrder bean : list) {
            if (StrUtil.isEmpty(bean.getEquipmentId()) || equipmentIdToName.containsKey(bean.getEquipmentId())) {
                continue;
            }
            String name = bean.getEquipmentMation() != null && StrUtil.isNotBlank(bean.getEquipmentMation().getName())
                ? bean.getEquipmentMation().getName()
                : bean.getEquipmentId();
            equipmentIdToName.put(bean.getEquipmentId(), name);
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
    public void queryInspectionOrderStatsByInspector(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<EquipmentInspectionOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        List<EquipmentInspectionOrder> list = equipmentInspectionOrderService.list(queryWrapper);
        iAuthUserService.setDataMation(list, EquipmentInspectionOrder::getServiceUserId);

        long total = list.size();
        Map<String, Long> inspectorStats = list.stream()
            .collect(Collectors.groupingBy(
                o -> StrUtil.isNotEmpty(o.getServiceUserId()) ? o.getServiceUserId() : OTHER_LABEL,
                Collectors.counting()));

        Map<String, String> inspectorIdToName = new HashMap<>();
        inspectorIdToName.put(OTHER_LABEL, OTHER_LABEL);
        for (EquipmentInspectionOrder bean : list) {
            if (StrUtil.isEmpty(bean.getServiceUserId()) || inspectorIdToName.containsKey(bean.getServiceUserId())) {
                continue;
            }
            Map<String, Object> userMation = bean.getServiceUserMation();
            String name = userMation != null && userMation.get("name") != null
                ? userMation.get("name").toString() : null;
            inspectorIdToName.put(bean.getServiceUserId(),
                StrUtil.isNotBlank(name) ? name : bean.getServiceUserId());
        }

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : inspectorStats.entrySet()) {
            xAxisData.add(inspectorIdToName.getOrDefault(entry.getKey(), entry.getKey()));
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
    public void queryInspectionOrderTrendStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        if (StrUtil.isEmpty(tableSelectInfo.getStartTime()) || StrUtil.isEmpty(tableSelectInfo.getEndTime())) {
            tableSelectInfo.setStartTime(DateUtil.formatDate2Str(
                DateUtil.getAfDate(DateUtil.getPointTime(DateUtil.getYmdTimeAndToString(), DateUtil.YYYY_MM_DD), -30, "d"),
                DateUtil.YYYY_MM_DD));
            tableSelectInfo.setEndTime(DateUtil.getYmdTimeAndToString());
        }

        List<String> dayList = DateUtil.getDays(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        QueryWrapper<EquipmentInspectionOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        // 新增的巡检单
        List<EquipmentInspectionOrder> orderList = equipmentInspectionOrderService.list(queryWrapper);
        Map<String, Long> newOrderMap = orderList.stream()
            .filter(order -> StrUtil.isNotEmpty(order.getCreateTime()))
            .collect(Collectors.groupingBy(order -> {
                Date pointTime = DateUtil.getPointTime(order.getCreateTime(), DateUtil.YYYY_MM_DD);
                return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM_DD);
            }, Collectors.counting()));
        // 已完成的巡检单
        Map<String, Long> completedMap = orderList.stream()
            .filter(order -> EquipmentInspectionOrderState.COMPLETED.getKey().equals(order.getState()))
            .filter(order -> StrUtil.isNotEmpty(order.getCreateTime()))
            .collect(Collectors.groupingBy(order -> {
                Date pointTime = DateUtil.getPointTime(order.getCreateTime(), DateUtil.YYYY_MM_DD);
                return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM_DD);
            }, Collectors.counting()));

        List<Long> allNewOrders = new ArrayList<>();
        List<Long> completedOrders = new ArrayList<>();
        Long defaultValue = Long.valueOf(CommonNumConstants.NUM_ZERO);
        for (String day : dayList) {
            allNewOrders.add(newOrderMap.getOrDefault(day, defaultValue) - completedMap.getOrDefault(day, defaultValue));
            completedOrders.add(completedMap.getOrDefault(day, defaultValue));
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("allNewOrders", allNewOrders);
        resultMap.put("completedOrders", completedOrders);
        resultMap.put("dayList", dayList);

        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 今日单量
     */
    private Long countTodayOrders(Integer checkResult) {
        String today = DateUtil.getYmdTimeAndToString();
        QueryWrapper<EquipmentInspectionOrder> wrapper = new QueryWrapper<>();
        wrapper.likeRight(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getInspectionTime), today);
        if (checkResult != null) {
            wrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getCheckResult), checkResult);
        }
        return equipmentInspectionOrderService.count(wrapper);
    }

    private QueryWrapper<EquipmentInspectionOrder> buildTimeRangeWrapper(String startTime, String endTime) {
        QueryWrapper<EquipmentInspectionOrder> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getCreateTime), endTime);
        }
        return queryWrapper;
    }

}
