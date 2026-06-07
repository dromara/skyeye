/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionPlanItemDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlanItem;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: EquipmentInspectionPlanItemServiceImpl
 * @Description: 设备巡检方案子表服务实现类
 */
@Service
@SkyeyeService(name = "设备巡检方案子表", groupName = "设备巡检方案", manageShow = false)
public class EquipmentInspectionPlanItemServiceImpl extends SkyeyeLinkDataServiceImpl<EquipmentInspectionPlanItemDao, EquipmentInspectionPlanItem>
    implements EquipmentInspectionPlanItemService {

    @Override
    public List<EquipmentInspectionPlanItem> selectByPId(String pId) {
        QueryWrapper<EquipmentInspectionPlanItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionPlanItem::getParentId), pId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(EquipmentInspectionPlanItem::getLineNo));
        return list(queryWrapper);
    }

}
