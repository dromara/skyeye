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
import com.skyeye.maintenance.dao.MaintenancePlanItemDao;
import com.skyeye.maintenance.entity.MaintenancePlanItem;
import com.skyeye.maintenance.service.MaintenancePlanItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 保养计划明细服务层
 */
@Service
@SkyeyeService(name = "保养计划明细", groupName = "设备保养", manageShow = false)
public class MaintenancePlanItemServiceImpl extends SkyeyeBusinessServiceImpl<MaintenancePlanItemDao, MaintenancePlanItem>
    implements MaintenancePlanItemService {

    @Override
    public void saveList(String parentId, List<MaintenancePlanItem> beans) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (MaintenancePlanItem item : beans) {
                item.setParentId(parentId);
            }
            createEntity(beans, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<MaintenancePlanItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MaintenancePlanItem::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<MaintenancePlanItem> selectByParentId(String parentId) {
        QueryWrapper<MaintenancePlanItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MaintenancePlanItem::getParentId), parentId);
        return list(queryWrapper);
    }
}
