/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.support;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 巡检单列表/统计共用查询条件（时间、设备名称编码）
 */
@Component
public class EquipmentInspectionOrderQuerySupport {

    @Autowired
    private EquipmentService equipmentService;

    /**
     * 时间：优先 planned_start_time，无则 create_time
     */
    public void applyStatTimeRange(QueryWrapper<EquipmentInspectionOrder> queryWrapper, String startTime, String endTime) {
        if (StrUtil.isBlank(startTime) && StrUtil.isBlank(endTime)) {
            return;
        }
        String plannedCol = MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getPlannedStartTime);
        String createTimeCol = MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getCreateTime);
        queryWrapper.and(w -> {
            w.nested(n -> {
                n.isNotNull(plannedCol).ne(plannedCol, StrUtil.EMPTY);
                if (StrUtil.isNotBlank(startTime)) {
                    n.ge(plannedCol, startTime);
                }
                if (StrUtil.isNotBlank(endTime)) {
                    n.le(plannedCol, endTime);
                }
            });
            w.or(n -> {
                n.and(a -> a.isNull(plannedCol).or().eq(plannedCol, StrUtil.EMPTY));
                if (StrUtil.isNotBlank(startTime)) {
                    n.ge(createTimeCol, startTime);
                }
                if (StrUtil.isNotBlank(endTime)) {
                    n.le(createTimeCol, endTime);
                }
            });
        });
    }

    /**
     * 设备名称/编码：先查设备 id 再 in
     */
    public void applyEquipmentNameCodeFilter(QueryWrapper<EquipmentInspectionOrder> queryWrapper,
                                             CommonPageInfo commonPageInfo) {
        String equipmentName = commonPageInfo.getCustomParamsMapStr("equipmentName");
        String equipmentCode = commonPageInfo.getCustomParamsMapStr("equipmentCode");
        if (StrUtil.isBlank(equipmentName) && StrUtil.isBlank(equipmentCode)) {
            return;
        }
        QueryWrapper<Equipment> equipmentWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(equipmentName)) {
            equipmentWrapper.like(MybatisPlusUtil.toColumns(Equipment::getName), equipmentName);
        }
        if (StrUtil.isNotBlank(equipmentCode)) {
            equipmentWrapper.like(MybatisPlusUtil.toColumns(Equipment::getOddNumber), equipmentCode);
        }
        List<Equipment> equipmentList = equipmentService.list(equipmentWrapper);
        if (CollectionUtil.isEmpty(equipmentList)) {
            queryWrapper.apply("1 = 0");
            return;
        }
        List<String> ids = equipmentList.stream().map(Equipment::getId).filter(StrUtil::isNotBlank).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            queryWrapper.apply("1 = 0");
            return;
        }
        queryWrapper.in(MybatisPlusUtil.toColumns(EquipmentInspectionOrder::getEquipmentId), ids);
    }

}
