/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.maintenance.classenum.EquipmentMaintainTaskState;
import com.skyeye.maintenance.dao.EquipmentMaintainOrderDao;
import com.skyeye.maintenance.dao.EquipmentMaintainOrderSparePartDetailDao;
import com.skyeye.maintenance.entity.EquipmentMaintainOrder;
import com.skyeye.maintenance.entity.EquipmentMaintainOrderSparePartDetail;
import com.skyeye.maintenance.service.EquipmentMaintainStatisticsService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备保养单统计服务实现：按状态、完成率、备件、趋势、执行人等维度统计（仅支持时间范围筛选）
 */
@Service
public class EquipmentMaintainStatisticsServiceImpl implements EquipmentMaintainStatisticsService {

    private static final String OTHER_LABEL = "其他";

    @Autowired
    private EquipmentMaintainOrderDao equipmentMaintainOrderDao;

    @Autowired
    private EquipmentMaintainOrderSparePartDetailDao equipmentMaintainOrderSparePartDetailDao;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private IAuthUserService iAuthUserService;

    /**
     * 柱状图固定顺序：与 EquipmentMaintainTaskState 枚举顺序一致
     */
    private static final EquipmentMaintainTaskState[] STATE_ORDER = {
        EquipmentMaintainTaskState.PENDING,
        EquipmentMaintainTaskState.IN_PROGRESS,
        EquipmentMaintainTaskState.COMPLETED,
        EquipmentMaintainTaskState.CANCELLED,
        EquipmentMaintainTaskState.TIMEOUT
    };

    @Override
    public void queryMaintainOrderStateStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        List<EquipmentMaintainOrder> list = queryOrdersInTimeRange(tableSelectInfo);
        long total = list.size();
        Map<Integer, Long> stateCountMap = list.stream()
            .filter(o -> o.getState() != null)
            .collect(Collectors.groupingBy(EquipmentMaintainOrder::getState, Collectors.counting()));

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (EquipmentMaintainTaskState state : STATE_ORDER) {
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
    public void queryMaintainOrderCompletionRateStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        List<EquipmentMaintainOrder> list = queryOrdersInTimeRange(tableSelectInfo);
        long total = list.size();
        long completed = list.stream()
            .filter(o -> EquipmentMaintainTaskState.COMPLETED.getKey().equals(o.getState()))
            .count();

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("completed", completed);
        if (total > 0) {
            result.put("completionRate", CalculationUtil.divide(String.valueOf(completed), String.valueOf(total), CommonNumConstants.NUM_TWO));
        } else {
            result.put("completionRate", CommonNumConstants.NUM_ZERO);
        }

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryMaintainOrderSparePartStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        List<EquipmentMaintainOrder> orderList = queryOrdersInTimeRange(tableSelectInfo);
        long total = orderList.size();

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("useCount", CommonNumConstants.NUM_ZERO);
        result.put("useAmount", CommonNumConstants.NUM_ZERO);
        result.put("orderWithSparePartCount", CommonNumConstants.NUM_ZERO);
        result.put("orderWithoutSparePartCount", total);
        result.put("xAxisData", new ArrayList<>());
        result.put("seriesData", new ArrayList<>());

        if (CollectionUtil.isEmpty(orderList)) {
            outputObject.setBean(result);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
            return;
        }

        List<String> orderIds = orderList.stream()
            .map(EquipmentMaintainOrder::getId)
            .filter(StrUtil::isNotEmpty)
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(orderIds)) {
            outputObject.setBean(result);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
            return;
        }

        QueryWrapper<EquipmentMaintainOrderSparePartDetail> detailWrapper = new QueryWrapper<>();
        detailWrapper.in(MybatisPlusUtil.toColumns(EquipmentMaintainOrderSparePartDetail::getParentId), orderIds);
        List<EquipmentMaintainOrderSparePartDetail> detailList = equipmentMaintainOrderSparePartDetailDao.selectList(detailWrapper);
        if (CollectionUtil.isEmpty(detailList)) {
            outputObject.setBean(result);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
            return;
        }

        String useCount = CommonNumConstants.NUM_ZERO.toString();
        String useAmount = CommonNumConstants.NUM_ZERO.toString();
        for (EquipmentMaintainOrderSparePartDetail detail : detailList) {
            if (StrUtil.isNotEmpty(detail.getOperNumber())) {
                useCount = CalculationUtil.add(CommonNumConstants.NUM_TWO, useCount, detail.getOperNumber());
            }
            if (StrUtil.isNotEmpty(detail.getAllPrice())) {
                useAmount = CalculationUtil.add(CommonNumConstants.NUM_TWO, useAmount, detail.getAllPrice());
            }
        }

        long orderWithSparePartCount = detailList.stream()
            .map(EquipmentMaintainOrderSparePartDetail::getParentId)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .count();

        materialService.setDataMation(detailList, EquipmentMaintainOrderSparePartDetail::getMaterialId);
        materialNormsService.setDataMation(detailList, EquipmentMaintainOrderSparePartDetail::getNormsId);

        Map<String, String> groupKeyToLabel = new HashMap<>();
        Map<String, String> groupKeyToUseCount = new HashMap<>();
        for (EquipmentMaintainOrderSparePartDetail detail : detailList) {
            Map<String, String> groupInfo = resolveSparePartGroupInfo(detail);
            String groupKey = groupInfo.get("groupKey");
            groupKeyToLabel.putIfAbsent(groupKey, groupInfo.get("label"));
            String current = groupKeyToUseCount.getOrDefault(groupKey, CommonNumConstants.NUM_ZERO.toString());
            String operNumber = StrUtil.isNotEmpty(detail.getOperNumber()) ? detail.getOperNumber() : CommonNumConstants.NUM_ZERO.toString();
            groupKeyToUseCount.put(groupKey, CalculationUtil.add(CommonNumConstants.NUM_TWO, current, operNumber));
        }

        List<String> xAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        groupKeyToUseCount.entrySet().stream()
            .sorted((a, b) -> CalculationUtil.compareTo(b.getValue(), a.getValue(), CommonNumConstants.NUM_TWO, RoundingMode.UP))
            .forEach(entry -> {
                xAxisData.add(groupKeyToLabel.getOrDefault(entry.getKey(), entry.getKey()));
                seriesData.add(entry.getValue());
            });

        result.put("useCount", useCount);
        result.put("useAmount", useAmount);
        result.put("orderWithSparePartCount", orderWithSparePartCount);
        result.put("orderWithoutSparePartCount", total - orderWithSparePartCount);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryMaintainOrderTrendStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        if (StrUtil.isEmpty(tableSelectInfo.getStartTime()) || StrUtil.isEmpty(tableSelectInfo.getEndTime())) {
            tableSelectInfo.setStartTime(DateUtil.formatDate2Str(
                DateUtil.getAfDate(DateUtil.getPointTime(DateUtil.getYmdTimeAndToString(), DateUtil.YYYY_MM_DD), -30, "d"),
                DateUtil.YYYY_MM_DD));
            tableSelectInfo.setEndTime(DateUtil.getYmdTimeAndToString());
        }

        List<String> dayList = DateUtil.getDays(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        // 1. 新增的保养单
        List<EquipmentMaintainOrder> orderList = queryOrdersInTimeRange(tableSelectInfo);
        Map<String, Long> newOrderMap = orderList.stream()
            .filter(order -> StrUtil.isNotEmpty(order.getCreateTime()))
            .collect(Collectors.groupingBy(order -> {
                Date pointTime = DateUtil.getPointTime(order.getCreateTime(), DateUtil.YYYY_MM_DD);
                return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM_DD);
            }, Collectors.counting()));
        // 2. 已完成的保养单
        Map<String, Long> completedMap = orderList.stream()
            .filter(order -> EquipmentMaintainTaskState.COMPLETED.getKey().equals(order.getState()))
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

    @Override
    public void queryMaintainOrderStatsByExecutor(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        List<EquipmentMaintainOrder> list = queryOrdersInTimeRange(tableSelectInfo);

        long total = list.size();
        Map<String, Long> executorStats = list.stream()
            .collect(Collectors.groupingBy(
                o -> StrUtil.isNotEmpty(o.getExecutorId()) ? o.getExecutorId() : OTHER_LABEL,
                Collectors.counting()));

        List<String> executorIds = list.stream()
            .map(EquipmentMaintainOrder::getExecutorId)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        Map<String, Map<String, Object>> executorMap = CollectionUtil.isEmpty(executorIds)
            ? new HashMap<>()
            : iAuthUserService.queryUserMationListByStaffIds(executorIds);

        Map<String, String> executorIdToName = new HashMap<>();
        executorIdToName.put(OTHER_LABEL, OTHER_LABEL);
        for (String executorId : executorIds) {
            Map<String, Object> userMation = executorMap.get(executorId);
            String name = null;
            if (userMation != null && userMation.get("name") != null) {
                name = userMation.get("name").toString();
            }
            executorIdToName.put(executorId, StrUtil.isNotBlank(name) ? name : executorId);
        }

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : executorStats.entrySet()) {
            xAxisData.add(executorIdToName.getOrDefault(entry.getKey(), entry.getKey()));
            seriesData.add(entry.getValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private List<EquipmentMaintainOrder> queryOrdersInTimeRange(TableSelectInfo tableSelectInfo) {
        QueryWrapper<EquipmentMaintainOrder> queryWrapper = new QueryWrapper<>();
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(EquipmentMaintainOrder::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(EquipmentMaintainOrder::getCreateTime), endTime);
        }
        return equipmentMaintainOrderDao.selectList(queryWrapper);
    }


    private Map<String, String> resolveSparePartGroupInfo(EquipmentMaintainOrderSparePartDetail detail) {
        String groupKey = StrUtil.blankToDefault(detail.getMaterialId(), "") + "_" + StrUtil.blankToDefault(detail.getNormsId(), "");

        Map<String, Object> materialMation = detail.getMaterialMation();
        String materialName;
        if (materialMation != null && materialMation.get("name") != null && StrUtil.isNotEmpty(String.valueOf(materialMation.get("name")))) {
            materialName = String.valueOf(materialMation.get("name"));
        } else {
            materialName = StrUtil.isNotEmpty(detail.getMaterialId()) ? detail.getMaterialId() : "其他";
        }

        Map<String, Object> normsMation = detail.getNormsMation();
        String normsName;
        if (normsMation != null && normsMation.get("name") != null && StrUtil.isNotEmpty(String.valueOf(normsMation.get("name")))) {
            normsName = String.valueOf(normsMation.get("name"));
        } else {
            normsName = StrUtil.isNotEmpty(detail.getNormsId()) ? detail.getNormsId() : "";
        }

        String label = StrUtil.isNotEmpty(normsName) ? materialName + "-" + normsName : materialName;

        Map<String, String> groupInfo = new HashMap<>(2);
        groupInfo.put("groupKey", groupKey);
        groupInfo.put("label", label);
        return groupInfo;
    }
}
