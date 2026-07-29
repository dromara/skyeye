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
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionCheckResult;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionOrderState;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        if (total > 0) {
            result.put("completionRate", CalculationUtil.divide(
                String.valueOf(completed), String.valueOf(total), CommonNumConstants.NUM_TWO));
        } else {
            result.put("completionRate", CommonNumConstants.NUM_ZERO);
        }

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
