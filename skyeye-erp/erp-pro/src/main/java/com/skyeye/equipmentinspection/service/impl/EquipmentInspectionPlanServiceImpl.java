/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionPlanDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionFrequencyType;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanItemService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionPlanServiceImpl
 * @Description: 设备巡检方案服务实现类
 */
@Service
@SkyeyeService(name = "设备巡检方案", groupName = "设备巡检方案", allowDynamicAttrKey = false)
public class EquipmentInspectionPlanServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentInspectionPlanDao, EquipmentInspectionPlan>
    implements EquipmentInspectionPlanService {

    @Autowired
    private EquipmentInspectionPlanItemService equipmentInspectionPlanItemService;

    @Override
    protected QueryWrapper<EquipmentInspectionPlan> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentInspectionPlan> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentInspectionPlan::getCreateTime));
        return queryWrapper;
    }

    @Override
    public void validatorEntity(EquipmentInspectionPlan entity) {
        if (StrUtil.isBlank(entity.getId())) {
            assignPlanCode(entity);
            entity.setName(StrUtil.blankToDefault(entity.getName(), entity.getPlanCode()));
        }
        super.validatorEntity(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writePostpose(EquipmentInspectionPlan entity, String userId) {
        equipmentInspectionPlanItemService.saveLinkList(entity.getId(), entity.getEquipmentInspectionPlanItemList());
        super.writePostpose(entity, userId);
    }

    @Override
    public EquipmentInspectionPlan selectById(String id) {
        EquipmentInspectionPlan plan = super.selectById(id);
        plan.setFrequencyTypeMation(EquipmentInspectionFrequencyType.getMation(plan.getFrequencyType()));
        return plan;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isNotEmpty(beans)) {
            beans.forEach(bean -> bean.put("frequencyTypeMation",
                EquipmentInspectionFrequencyType.getMation(MapUtil.getInt(bean, "frequencyType"))));
        }
        return beans;
    }

    @Override
    public EquipmentInspectionPlan getDataFromDb(String id) {
        EquipmentInspectionPlan entity = super.getDataFromDb(id);
        entity.setEquipmentInspectionPlanItemList(equipmentInspectionPlanItemService.selectByPId(entity.getId()));
        return entity;
    }

    private void assignPlanCode(EquipmentInspectionPlan entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String planCode = iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business);
        if (StrUtil.isBlank(planCode)) {
            planCode = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        }
        entity.setPlanCode(planCode);
    }

    @Override
    public void deletePostpose(String id) {
        equipmentInspectionPlanItemService.deleteByPId(id);
    }

    @Override
    public int calcRequiredInspectionCount(EquipmentInspectionPlan plan, String startTime, String endTime) {
        if (StrUtil.isBlank(plan.getId())) {
            return 0;
        }
        int perDay = plan.getInspectionsPerDay() == null || plan.getInspectionsPerDay() < 1 ? 1 : plan.getInspectionsPerDay();
        java.util.Date start = cn.hutool.core.date.DateUtil.parse(startTime);
        java.util.Date end = cn.hutool.core.date.DateUtil.parse(endTime);
        long days = cn.hutool.core.date.DateUtil.betweenDay(start, end, true) + 1;
        Integer frequencyType = plan.getFrequencyType() == null
            ? EquipmentInspectionFrequencyType.DAY.getKey() : plan.getFrequencyType();
        switch (frequencyType) {
            case 2:
                return (int) ((days + 6) / 7 * perDay);
            case 3:
                return (int) ((cn.hutool.core.date.DateUtil.betweenMonth(start, end, true) + 1) * perDay);
            case 4:
                long months = cn.hutool.core.date.DateUtil.betweenMonth(start, end, true) + 1;
                return (int) ((months + 2) / 3 * perDay);
            case 5:
                return (int) ((cn.hutool.core.date.DateUtil.betweenYear(start, end, true) + 1) * perDay);
            case 1:
            default:
                return (int) (days * perDay);
        }
    }

}
