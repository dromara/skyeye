/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.support;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import org.springframework.stereotype.Component;

/**
 * 巡检单列表/统计共用查询条件（时间范围）
 */
@Component
public class EquipmentInspectionOrderQuerySupport {

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

}
