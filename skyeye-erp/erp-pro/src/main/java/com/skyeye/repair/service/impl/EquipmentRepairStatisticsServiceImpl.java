/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

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
import com.skyeye.eve.service.ISysDictDataService;
import com.skyeye.repair.classenum.EquipmentFaultCategory;
import com.skyeye.repair.classenum.EquipmentRepairFromType;
import com.skyeye.repair.classenum.EquipmentRepairOrderState;
import com.skyeye.repair.dao.EquipmentRepairOrderDao;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.entity.EquipmentSparePartUsageDetail;
import com.skyeye.repair.service.EquipmentRepairStatisticsService;
import com.skyeye.repair.service.EquipmentSparePartUsageDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * @author skyeye云系列--卫志强
 * @Copyright 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 */
@Service
public class EquipmentRepairStatisticsServiceImpl implements EquipmentRepairStatisticsService {

    private static final long HOUR_MILLIS = TimeUnit.HOURS.toMillis(1);

    private static final String OTHER_LABEL = "其他";

    private static final EquipmentRepairOrderState[] STATE_ORDER = {
        EquipmentRepairOrderState.BE_DISPATCHED,
        EquipmentRepairOrderState.PENDING_ORDERS,
        EquipmentRepairOrderState.BE_COMPLETED,
        EquipmentRepairOrderState.BE_EVALUATED,
        EquipmentRepairOrderState.AUDIT,
        EquipmentRepairOrderState.COMPLATE
    };

    @Autowired
    private EquipmentRepairOrderDao equipmentRepairOrderDao;

    @Autowired
    private EquipmentSparePartUsageDetailService equipmentSparePartUsageDetailService;

    @Autowired
    private ISysDictDataService iSysDictDataService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public void queryEquipmentRepairOrderTrendStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        if (StrUtil.isEmpty(tableSelectInfo.getStartTime()) || StrUtil.isEmpty(tableSelectInfo.getEndTime())) {
            tableSelectInfo.setStartTime(DateUtil.formatDate2Str(
                DateUtil.getAfDate(DateUtil.getPointTime(DateUtil.getYmdTimeAndToString(), DateUtil.YYYY_MM_DD), -30, "d"),
                DateUtil.YYYY_MM_DD));
            tableSelectInfo.setEndTime(DateUtil.getYmdTimeAndToString());
        }

        List<String> dayList = DateUtil.getDays(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        QueryWrapper<EquipmentRepairOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        // 1. 新增的维修单
        List<EquipmentRepairOrder> orderList = equipmentRepairOrderDao.selectList(queryWrapper);
        Map<String, Long> collect = orderList.stream()
            .filter(order -> StrUtil.isNotEmpty(order.getCreateTime()))
            .collect(Collectors.groupingBy(order -> {
                Date pointTime = DateUtil.getPointTime(order.getCreateTime(), DateUtil.YYYY_MM_DD);
                return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM_DD);
            }, Collectors.counting()));
        // 2. 完工的维修单
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.COMPLATE.getKey());
        List<EquipmentRepairOrder> completedList = equipmentRepairOrderDao.selectList(queryWrapper);
        Map<String, Long> collect2 = completedList.stream()
            .filter(order -> StrUtil.isNotEmpty(order.getCreateTime()))
            .collect(Collectors.groupingBy(order -> {
                Date pointTime = DateUtil.getPointTime(order.getCreateTime(), DateUtil.YYYY_MM_DD);
                return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM_DD);
            }, Collectors.counting()));

        List<Long> allNewOrders = new ArrayList<>();
        List<Long> completedOrders = new ArrayList<>();
        Long defaultValue = Long.valueOf(CommonNumConstants.NUM_ZERO);
        for (String day : dayList) {
            allNewOrders.add(collect.getOrDefault(day, defaultValue) - collect2.getOrDefault(day, defaultValue));
            completedOrders.add(collect2.getOrDefault(day, defaultValue));
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("allNewOrders", allNewOrders);
        resultMap.put("completedOrders", completedOrders);
        resultMap.put("dayList", dayList);

        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryOverviewEquipmentRepairOrder(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<EquipmentRepairOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        Long totalOrders = equipmentRepairOrderDao.selectCount(queryWrapper);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalOrders", totalOrders);

        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getState), EquipmentRepairOrderState.COMPLATE.getKey());
        Long completedOrders = equipmentRepairOrderDao.selectCount(queryWrapper);
        resultMap.put("completedOrders", completedOrders);

        List<EquipmentRepairOrder> completedList = completedOrders > CommonNumConstants.NUM_ZERO
            ? equipmentRepairOrderDao.selectList(queryWrapper) : new ArrayList<>();
        resultMap.put("useCount", querySparePartUseCount(completedList));

        if (completedOrders > CommonNumConstants.NUM_ZERO) {
            String totalHours = sumCompletedProcessHours(completedList);
            resultMap.put("avgProcessTime", CalculationUtil.divide(totalHours,
                String.valueOf(completedOrders), CommonNumConstants.NUM_TWO));
        } else {
            resultMap.put("avgProcessTime", CommonNumConstants.NUM_ZERO);
        }

        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryRepairOrderStateStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<EquipmentRepairOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<EquipmentRepairOrder> list = equipmentRepairOrderDao.selectList(queryWrapper);
        long total = list.size();
        Map<Integer, Long> stateCountMap = list.stream()
            .filter(o -> o.getState() != null)
            .collect(Collectors.groupingBy(EquipmentRepairOrder::getState, Collectors.counting()));

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (EquipmentRepairOrderState state : STATE_ORDER) {
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
    public void queryRepairOrderStatsByUrgency(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<EquipmentRepairOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<EquipmentRepairOrder> list = equipmentRepairOrderDao.selectList(queryWrapper);
        long total = list.size();
        iSysDictDataService.setDataMation(list, EquipmentRepairOrder::getUrgencyId);

        Map<String, Long> urgencyStats = list.stream()
            .collect(Collectors.groupingBy(
                o -> StrUtil.isNotEmpty(o.getUrgencyId()) ? o.getUrgencyId() : OTHER_LABEL,
                Collectors.counting()));

        Map<String, String> urgencyIdToName = new HashMap<>();
        urgencyIdToName.put(OTHER_LABEL, OTHER_LABEL);
        for (EquipmentRepairOrder bean : list) {
            if (StrUtil.isEmpty(bean.getUrgencyId()) || urgencyIdToName.containsKey(bean.getUrgencyId())) {
                continue;
            }
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
    public void queryRepairOrderStatsByServiceUser(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<EquipmentRepairOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<EquipmentRepairOrder> list = equipmentRepairOrderDao.selectList(queryWrapper);
        long total = list.size();
        iAuthUserService.setDataMation(list, EquipmentRepairOrder::getServiceUserId);

        Map<String, Long> serviceUserStats = list.stream()
            .collect(Collectors.groupingBy(
                o -> StrUtil.isNotEmpty(o.getServiceUserId()) ? o.getServiceUserId() : OTHER_LABEL,
                Collectors.counting()));

        Map<String, String> serviceUserIdToName = new HashMap<>();
        serviceUserIdToName.put(OTHER_LABEL, OTHER_LABEL);
        for (EquipmentRepairOrder bean : list) {
            if (StrUtil.isEmpty(bean.getServiceUserId()) || serviceUserIdToName.containsKey(bean.getServiceUserId())) {
                continue;
            }
            String name = null;
            if (bean.getServiceUserMation() != null && bean.getServiceUserMation().get("name") != null) {
                name = bean.getServiceUserMation().get("name").toString();
            }
            serviceUserIdToName.put(bean.getServiceUserId(), StrUtil.isNotBlank(name) ? name : bean.getServiceUserId());
        }

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : serviceUserStats.entrySet()) {
            xAxisData.add(serviceUserIdToName.getOrDefault(entry.getKey(), entry.getKey()));
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
    public void queryRepairOrderStatsByFaultType(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<EquipmentRepairOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        List<EquipmentRepairOrder> list = equipmentRepairOrderDao.selectList(queryWrapper);
        Map<Integer, Long> faultTypeCountMap = list.stream()
            .collect(Collectors.groupingBy(
                o -> o.getFaultType() != null
                    ? o.getFaultType()
                    : EquipmentFaultCategory.OTHER.getKey(),
                Collectors.counting()));
        List<Map<String, Object>> rows = new ArrayList<>();
        for (EquipmentFaultCategory faultCategory : EquipmentFaultCategory.values()) {
            Map<String, Object> row = new HashMap<>(2);
            row.put("name", faultCategory.getValue());
            row.put("value", String.valueOf(faultTypeCountMap.getOrDefault(faultCategory.getKey(), 0L)));
            rows.add(row);
        }

        outputObject.setBeans(rows);
        outputObject.settotal(rows.size());
    }

    @Override
    public void queryRepairOrderStatsByFromType(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<EquipmentRepairOrder> queryWrapper = buildTimeRangeWrapper(
            tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        List<EquipmentRepairOrder> list = equipmentRepairOrderDao.selectList(queryWrapper);

        Map<String, String> fromTypeIdToName = new HashMap<>();
        fromTypeIdToName.put(OTHER_LABEL, OTHER_LABEL);
        for (EquipmentRepairFromType fromType : EquipmentRepairFromType.values()) {
            fromTypeIdToName.put(String.valueOf(fromType.getKey()), fromType.getValue());
        }
        Map<String, Long> fromTypeStats = list.stream()
            .collect(Collectors.groupingBy(
                o -> o.getFromTypeId() != null && fromTypeIdToName.containsKey(String.valueOf(o.getFromTypeId()))
                    ? String.valueOf(o.getFromTypeId())
                    : OTHER_LABEL,
                Collectors.counting()));

        List<Map<String, Object>> rows = new ArrayList<>();
        for (EquipmentRepairFromType fromType : EquipmentRepairFromType.values()) {
            Map<String, Object> row = new HashMap<>(2);
            row.put("name", fromType.getValue());
            row.put("value", String.valueOf(fromTypeStats.getOrDefault(String.valueOf(fromType.getKey()), 0L)));
            rows.add(row);
        }
        Map<String, Object> otherRow = new HashMap<>(2);
        otherRow.put("name", OTHER_LABEL);
        otherRow.put("value", String.valueOf(fromTypeStats.getOrDefault(OTHER_LABEL, 0L)));
        rows.add(otherRow);

        outputObject.setBeans(rows);
        outputObject.settotal(rows.size());
    }

    private QueryWrapper<EquipmentRepairOrder> buildTimeRangeWrapper(String startTime, String endTime) {
        QueryWrapper<EquipmentRepairOrder> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getCreateTime), startTime)
                .le(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getCreateTime), endTime);
        }
        return queryWrapper;
    }

    private String querySparePartUseCount(List<EquipmentRepairOrder> completedList) {
        String total = CommonNumConstants.NUM_ZERO.toString();
        if (CollectionUtil.isEmpty(completedList)) {
            return total;
        }
        List<String> orderIds = completedList.stream().map(EquipmentRepairOrder::getId).collect(Collectors.toList());
        QueryWrapper<EquipmentSparePartUsageDetail> detailWrapper = new QueryWrapper<>();
        detailWrapper.in(MybatisPlusUtil.toColumns(EquipmentSparePartUsageDetail::getParentId), orderIds);
        List<EquipmentSparePartUsageDetail> detailList = equipmentSparePartUsageDetailService.list(detailWrapper);
        if (CollectionUtil.isEmpty(detailList)) {
            return total;
        }
        for (EquipmentSparePartUsageDetail detail : detailList) {
            if (StrUtil.isNotEmpty(detail.getOperNumber())) {
                total = CalculationUtil.add(CommonNumConstants.NUM_TWO, total, detail.getOperNumber());
            }
        }
        return total;
    }

    private String sumCompletedProcessHours(List<EquipmentRepairOrder> completedList) {
        String totalHours = CommonNumConstants.NUM_ZERO.toString();
        for (EquipmentRepairOrder order : completedList) {
            String finishTime = order.getRepairFinishTime();
            String startTime = order.getServiceTime();
            if (StrUtil.isEmpty(finishTime) || StrUtil.isEmpty(startTime)) {
                continue;
            }
            try {
                Date finish = DateUtil.getPointTime(finishTime, DateUtil.YYYY_MM_DD_HH_MM_SS);
                Date start = DateUtil.getPointTime(startTime, DateUtil.YYYY_MM_DD_HH_MM_SS);
                if (finish == null || start == null || finish.before(start)) {
                    continue;
                }
                double hours = (finish.getTime() - start.getTime()) * 1.0 / HOUR_MILLIS;
                totalHours = CalculationUtil.add(CommonNumConstants.NUM_TWO, totalHours, String.valueOf(hours));
            } catch (Exception ignored) {
                // 时间格式异常的工单跳过
            }
        }
        return totalHours;
    }
}
