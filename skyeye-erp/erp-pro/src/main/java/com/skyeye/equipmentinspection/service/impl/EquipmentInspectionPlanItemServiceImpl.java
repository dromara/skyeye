/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionPlanItemDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlanItem;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanItemService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentInspectionPlanItemServiceImpl
 * @Description: 设备巡检方案项目关联服务实现类
 */
@Service
@SkyeyeService(name = "设备巡检方案项目关联", groupName = "设备巡检", manageShow = false)
public class EquipmentInspectionPlanItemServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentInspectionPlanItemDao, EquipmentInspectionPlanItem>
    implements EquipmentInspectionPlanItemService {

    @Override
    public void deleteByParentId(String planId) {
        if (StrUtil.isEmpty(planId)) {
            return;
        }
        QueryWrapper<EquipmentInspectionPlanItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionPlanItem::getPlanId), planId);
        remove(queryWrapper);
    }

    @Override
    public List<String> selectByParentId(String planId) {
        QueryWrapper<EquipmentInspectionPlanItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionPlanItem::getPlanId), planId);
        List<EquipmentInspectionPlanItem> list = list(queryWrapper);
        return list.stream().map(EquipmentInspectionPlanItem::getItemId).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> selectMapByParentId(List<String> planIds) {
        if (CollectionUtil.isEmpty(planIds)) {
            return Collections.emptyMap();
        }
        QueryWrapper<EquipmentInspectionPlanItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(EquipmentInspectionPlanItem::getPlanId), planIds);
        List<EquipmentInspectionPlanItem> list = list(queryWrapper);
        return list.stream().collect(Collectors.groupingBy(
            EquipmentInspectionPlanItem::getPlanId,
            Collectors.mapping(EquipmentInspectionPlanItem::getItemId, Collectors.toList())
        ));
    }

    @Override
    public void saveList(String planId, List<String> itemIds) {
        deleteByParentId(planId);
        if (CollectionUtil.isNotEmpty(itemIds)) {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            List<EquipmentInspectionPlanItem> planItemList = itemIds.stream().map(itemId -> {
                EquipmentInspectionPlanItem planItem = new EquipmentInspectionPlanItem();
                planItem.setPlanId(planId);
                planItem.setItemId(itemId);
                return planItem;
            }).collect(Collectors.toList());
            createEntity(planItemList, userId);
        }
    }
}
