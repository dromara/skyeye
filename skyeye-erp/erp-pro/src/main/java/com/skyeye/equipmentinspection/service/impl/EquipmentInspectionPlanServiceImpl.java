/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionPlanDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionFrequencyType;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanItemService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        normalizePlanFields(entity);
        if (StrUtil.isBlank(entity.getId())) {
            entity.setPlanCode(null);
            assignPlanCode(entity);
        }
        super.validatorEntity(entity);
        if (CollectionUtil.isEmpty(entity.getEquipmentInspectionPlanItemList())) {
            throw new CustomException("请至少配置一条巡检方案检查项.");
        }
    }

    @Override
    public void createPrepose(EquipmentInspectionPlan entity) {
        normalizePlanFields(entity);
        if (StrUtil.isBlank(entity.getPlanCode())) {
            assignPlanCode(entity);
        }
        if (StrUtil.isBlank(entity.getName())) {
            entity.setName(entity.getPlanCode());
        }
        super.createPrepose(entity);
    }

    @Override
    public void updatePrepose(EquipmentInspectionPlan entity) {
        normalizePlanFields(entity);
        super.updatePrepose(entity);
        if (StrUtil.isNotBlank(entity.getId())) {
            EquipmentInspectionPlan oldPlan = super.selectById(entity.getId());
            if (oldPlan == null) {
                throw new CustomException("巡检方案不存在.");
            }
            entity.setPlanCode(oldPlan.getPlanCode());
        }
    }

    @Override
    public EquipmentInspectionPlan getFromCache(String key) {
        try {
            return normalizePlanFields(super.getFromCache(key));
        } catch (Exception ex) {
            EquipmentInspectionPlan plan = getDataFromDb(key);
            if (plan != null) {
                refreshCache(key);
            }
            return plan;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writePostpose(EquipmentInspectionPlan entity, String userId) {
        equipmentInspectionPlanItemService.saveLinkList(entity.getId(), entity.getEquipmentInspectionPlanItemList());
        super.writePostpose(entity, userId);
    }

    @Override
    public EquipmentInspectionPlan getDataFromDb(String id) {
        EquipmentInspectionPlan entity = normalizePlanFields(super.getDataFromDb(id));
        if (entity == null) {
            return null;
        }
        entity.setEquipmentInspectionPlanItemList(equipmentInspectionPlanItemService.selectByPId(entity.getId()));
        return entity;
    }

    private EquipmentInspectionPlan normalizePlanFields(EquipmentInspectionPlan entity) {
        if (entity == null) {
            return null;
        }
        entity.setFrequencyType(EquipmentInspectionFrequencyType.parseKey(entity.getFrequencyType()));
        return entity;
    }

    private void assignPlanCode(EquipmentInspectionPlan entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        entity.setPlanCode(iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business));
    }

    @Override
    public void deletePostpose(String id) {
        equipmentInspectionPlanItemService.deleteByPId(id);
    }

    @Override
    public int calcRequiredInspectionCount(EquipmentInspectionPlan plan, String startTime, String endTime) {
        if (plan == null) {
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
