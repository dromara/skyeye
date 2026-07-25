/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionPlanEquipmentDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlanEquipment;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanEquipmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentInspectionPlanEquipmentServiceImpl
 * @Description: 设备巡检方案设备关联服务层
 */
@Service
@SkyeyeService(name = "设备巡检方案设备关联", groupName = "设备巡检", manageShow = false)
public class EquipmentInspectionPlanEquipmentServiceImpl
    extends SkyeyeBusinessServiceImpl<EquipmentInspectionPlanEquipmentDao, EquipmentInspectionPlanEquipment>
    implements EquipmentInspectionPlanEquipmentService {

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteByParentId(String planId) {
        QueryWrapper<EquipmentInspectionPlanEquipment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionPlanEquipment::getPlanId), planId);
        remove(queryWrapper);
    }

    @Override
    public List<String> selectByParentId(String planId) {
        QueryWrapper<EquipmentInspectionPlanEquipment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionPlanEquipment::getPlanId), planId);
        List<EquipmentInspectionPlanEquipment> list = list(queryWrapper);
        return list.stream().map(EquipmentInspectionPlanEquipment::getEquipmentId).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> selectMapByParentId(List<String> planIds) {
        if (CollectionUtil.isEmpty(planIds)) {
            return Collections.emptyMap();
        }
        QueryWrapper<EquipmentInspectionPlanEquipment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(EquipmentInspectionPlanEquipment::getPlanId), planIds);
        List<EquipmentInspectionPlanEquipment> list = list(queryWrapper);
        return list.stream().collect(Collectors.groupingBy(
            EquipmentInspectionPlanEquipment::getPlanId,
            Collectors.mapping(EquipmentInspectionPlanEquipment::getEquipmentId, Collectors.toList())
        ));
    }

    @Override
    public void saveList(String planId, List<String> equipmentIds) {
        deleteByParentId(planId);
        if (CollectionUtil.isNotEmpty(equipmentIds)) {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            List<EquipmentInspectionPlanEquipment> planEquipmentList = equipmentIds.stream().map(equipmentId -> {
                EquipmentInspectionPlanEquipment planEquipment = new EquipmentInspectionPlanEquipment();
                planEquipment.setPlanId(planId);
                planEquipment.setEquipmentId(equipmentId);
                return planEquipment;
            }).collect(Collectors.toList());
            createEntity(planEquipmentList, userId);
        }
    }

}
