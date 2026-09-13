/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.seal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.classenum.ErpOrderStateEnum;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.seal.entity.SalesOrder;
import com.skyeye.seal.service.SalesOrderService;
import com.skyeye.seal.service.SalesOrderStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 「已签署」= 审核通过及之后的履约状态；草稿/审批中/驳回/撤销不计入。
 * 总金额、签单客户数、签署数量为全量，不按时间筛选；日度趋势按单据日期 operTime。
 */
@Service
public class SalesOrderStatisticsServiceImpl implements SalesOrderStatisticsService {

    @Autowired
    private SalesOrderService salesOrderService;

    /**
     * 已签署订单 totalPrice 合计（保留两位小数）。无时间条件。
     */
    @Override
    public void querySalesOrderTotalPrice(InputObject inputObject, OutputObject outputObject) {
        List<SalesOrder> orderList = querySignedSalesOrderList();
        Map<String, Object> result = new HashMap<>();
        result.put("totalPrice", sumTotalPrice(orderList));
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 签单客户数 = 已签署订单中 holderId（客户）去重个数，空客户不计入。同一客户多张单只算 1。无时间条件。
     */
    @Override
    public void querySalesOrderCustomerCount(InputObject inputObject, OutputObject outputObject) {
        List<SalesOrder> orderList = querySignedSalesOrderList();
        Map<String, Object> result = new HashMap<>();
        result.put("customerCount", countSignedCustomer(orderList));
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 签署数量 = 已签署订单张数，不去重客户。无时间条件。
     */
    @Override
    public void querySalesOrderSignCount(InputObject inputObject, OutputObject outputObject) {
        List<SalesOrder> orderList = querySignedSalesOrderList();
        Map<String, Object> result = new HashMap<>();
        result.put("orderCount", orderList.size());
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 按天输出已签署订单数量和金额。未传起止日期时默认近 30 天。
     * 先铺满日期轴，当天无单则数量 0、金额 "0"，保证趋势图连续。
     */
    @Override
    public void querySalesOrderTrend(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        fillDefaultTrendTime(tableSelectInfo);
        List<String> dayList = DateUtil.getDays(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        Map<String, List<SalesOrder>> groupByDay = querySignedSalesOrderGroupByDay(tableSelectInfo);
        List<Long> orderCountList = new ArrayList<>();
        List<String> moneyList = new ArrayList<>();
        Long defaultValue = Long.valueOf(CommonNumConstants.NUM_ZERO);
        for (String day : dayList) {
            List<SalesOrder> dayOrders = groupByDay.get(day);
            if (CollectionUtil.isEmpty(dayOrders)) {
                orderCountList.add(defaultValue);
                moneyList.add("0");
            } else {
                orderCountList.add(Long.valueOf(dayOrders.size()));
                moneyList.add(sumTotalPrice(dayOrders));
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("dayList", dayList);
        resultMap.put("orderCount", orderCountList);
        resultMap.put("money", moneyList);
        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 未传起止日期时，默认近 30 天（含今天），对齐设备巡检日度趋势。
     */
    private void fillDefaultTrendTime(TableSelectInfo tableSelectInfo) {
        if (StrUtil.isEmpty(tableSelectInfo.getStartTime()) || StrUtil.isEmpty(tableSelectInfo.getEndTime())) {
            tableSelectInfo.setStartTime(DateUtil.formatDate2Str(
                DateUtil.getAfDate(DateUtil.getPointTime(DateUtil.getYmdTimeAndToString(), DateUtil.YYYY_MM_DD), -30, "d"),
                DateUtil.YYYY_MM_DD));
            tableSelectInfo.setEndTime(DateUtil.getYmdTimeAndToString());
        }
    }

    /**
     * 已签署订单按 operTime 截到天分组。结束日若只到 yyyy-MM-dd，补 23:59:59 以免漏当天。
     */
    private Map<String, List<SalesOrder>> querySignedSalesOrderGroupByDay(TableSelectInfo tableSelectInfo) {
        QueryWrapper<SalesOrder> queryWrapper = buildSignedQueryWrapper();
        String operTime = MybatisPlusUtil.toColumns(SalesOrder::getOperTime);
        if (StrUtil.isNotEmpty(tableSelectInfo.getStartTime())) {
            queryWrapper.ge(operTime, tableSelectInfo.getStartTime());
        }
        if (StrUtil.isNotEmpty(tableSelectInfo.getEndTime())) {
            String endTime = tableSelectInfo.getEndTime();
            queryWrapper.le(operTime, endTime.length() == CommonNumConstants.NUM_TEN ? endTime + " 23:59:59" : endTime);
        }
        List<SalesOrder> orderList = salesOrderService.list(queryWrapper);
        if (CollectionUtil.isEmpty(orderList)) {
            return new HashMap<>();
        }
        return orderList.stream()
            .filter(item -> StrUtil.isNotEmpty(item.getOperTime()) && item.getOperTime().length() >= CommonNumConstants.NUM_TEN)
            .collect(Collectors.groupingBy(item -> {
                Date pointTime = DateUtil.getPointTime(item.getOperTime(), DateUtil.YYYY_MM_DD);
                return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM_DD);
            }));
    }

    /**
     * 已签署状态：审核通过，以及通过后的履约态（部分完成/已完成/待出库/部分出库/全部出库）。
     */
    private List<String> getSignedStateList() {
        return Arrays.asList(
            FlowableStateEnum.PASS.getKey(),
            ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey(),
            ErpOrderStateEnum.COMPLETED.getKey(),
            ErpOrderStateEnum.NEED_Out.getKey(),
            ErpOrderStateEnum.PARTIAL_Out.getKey(),
            ErpOrderStateEnum.All_Out.getKey()
        );
    }

    /**
     * 销售订单与出库/退货等同表 erp_depothead，必须用 id_key 限定本服务，再筛已签署状态。
     */
    private QueryWrapper<SalesOrder> buildSignedQueryWrapper() {
        QueryWrapper<SalesOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SalesOrder::getIdKey), salesOrderService.getServiceClassName());
        queryWrapper.in(MybatisPlusUtil.toColumns(SalesOrder::getState), getSignedStateList());
        return queryWrapper;
    }

    /**
     * 全量已签署销售订单，卡片指标共用这一批数据。
     */
    private List<SalesOrder> querySignedSalesOrderList() {
        List<SalesOrder> orderList = salesOrderService.list(buildSignedQueryWrapper());
        if (CollectionUtil.isEmpty(orderList)) {
            return new ArrayList<>();
        }
        return orderList;
    }

    /**
     * 订单金额合计，空金额当 0，精度两位。
     */
    private String sumTotalPrice(List<SalesOrder> orderList) {
        String totalPrice = "0";
        if (CollectionUtil.isEmpty(orderList)) {
            return totalPrice;
        }
        for (SalesOrder order : orderList) {
            totalPrice = CalculationUtil.add(CommonNumConstants.NUM_TWO, totalPrice,
                StrUtil.isEmpty(order.getTotalPrice()) ? "0" : order.getTotalPrice());
        }
        return totalPrice;
    }

    /**
     * 按客户 id 去重计数，空 holderId 丢掉。
     */
    private long countSignedCustomer(List<SalesOrder> orderList) {
        if (CollectionUtil.isEmpty(orderList)) {
            return CommonNumConstants.NUM_ZERO;
        }
        return orderList.stream()
            .map(SalesOrder::getHolderId)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .count();
    }

}
