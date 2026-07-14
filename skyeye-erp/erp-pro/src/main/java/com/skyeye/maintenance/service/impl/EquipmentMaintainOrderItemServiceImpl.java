/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.maintenance.dao.EquipmentMaintainOrderItemDao;
import com.skyeye.maintenance.entity.EquipmentMaintainOrderItem;
import com.skyeye.maintenance.entity.MaintenancePlanItem;
import com.skyeye.maintenance.service.EquipmentMaintainOrderItemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 设备保养单明细服务层
 */
@Service
@SkyeyeService(name = "设备保养单明细", groupName = "设备保养", manageShow = false)
public class EquipmentMaintainOrderItemServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentMaintainOrderItemDao, EquipmentMaintainOrderItem>
    implements EquipmentMaintainOrderItemService {

    @Override
    public void saveList(String parentId, List<EquipmentMaintainOrderItem> beans) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (EquipmentMaintainOrderItem item : beans) {
                item.setParentId(parentId);
            }
            createEntity(beans, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<EquipmentMaintainOrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentMaintainOrderItem::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<EquipmentMaintainOrderItem> selectByParentId(String parentId) {
        QueryWrapper<EquipmentMaintainOrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentMaintainOrderItem::getParentId), parentId);
        return list(queryWrapper);
    }

    @Override
    public List<EquipmentMaintainOrderItem> copyFromPlanItems(List<MaintenancePlanItem> planItems) {
        if (CollectionUtil.isEmpty(planItems)) {
            return new ArrayList<>();
        }
        List<EquipmentMaintainOrderItem> orderItems = new ArrayList<>();
        for (MaintenancePlanItem planItem : planItems) {
            EquipmentMaintainOrderItem orderItem = new EquipmentMaintainOrderItem();
            orderItem.setMaintainItem(planItem.getMaintainItem());
            orderItem.setMaintainContent(planItem.getMaintainContent());
            orderItems.add(orderItem);
        }
        return orderItems;
    }
}
