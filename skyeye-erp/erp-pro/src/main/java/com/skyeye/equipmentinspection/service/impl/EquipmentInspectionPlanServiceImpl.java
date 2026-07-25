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
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.ScheduleFrequency;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionPlanDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanEquipmentService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import com.skyeye.equipmentinspection.support.EquipmentInspectionPlanCronBuilder;
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentInspectionPlanServiceImpl
 * @Description: 设备巡检方案服务层
 */
@Service
@SkyeyeService(name = "设备巡检方案", groupName = "设备巡检", allowDynamicAttrKey = false)
public class EquipmentInspectionPlanServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentInspectionPlanDao, EquipmentInspectionPlan>
    implements EquipmentInspectionPlanService {

    @Autowired
    private EquipmentInspectionPlanEquipmentService equipmentInspectionPlanEquipmentService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private IQuartzService iQuartzService;

    @Override
    protected QueryWrapper<EquipmentInspectionPlan> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentInspectionPlan> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (commonPageInfo.getEnabled() != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionPlan::getEnabled), commonPageInfo.getEnabled());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("frequency"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionPlan::getFrequency),
                commonPageInfo.getCustomParamsMapStr("frequency"));
        }
        return queryWrapper;
    }

    @Override
    public void validatorEntity(EquipmentInspectionPlan entity) {
        super.validatorEntity(entity);
        // equipmentId 实体已标 required,json；此处校验去重、存在性与频次附加字段
        List<String> equipmentIds = entity.getEquipmentId().stream()
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        long nonBlankCount = entity.getEquipmentId().stream().filter(StrUtil::isNotBlank).count();
        if (equipmentIds.size() != nonBlankCount) {
            throw new CustomException("存在相同的设备信息，请确认");
        }
        List<Equipment> equipmentList = equipmentService.selectByIds(equipmentIds.toArray(new String[]{}));
        Set<String> existIds = CollectionUtil.isEmpty(equipmentList)
            ? Collections.emptySet()
            : equipmentList.stream().map(Equipment::getId).filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        List<String> missingIds = equipmentIds.stream()
            .filter(id -> !existIds.contains(id))
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(missingIds)) {
            throw new CustomException("设备不存在: " + String.join(",", missingIds));
        }
        entity.setEquipmentId(equipmentIds);
        if (entity.getInspectionsPerDay() < 1) {
            throw new CustomException("当天规定巡检次数至少为1");
        }
        Integer freq = entity.getFrequency();
        if (ScheduleFrequency.WEEKLY.getKey().equals(freq) && StrUtil.isBlank(entity.getWeekDays())) {
            throw new CustomException("周检请填写周几");
        }
        if (ScheduleFrequency.MONTHLY.getKey().equals(freq) && StrUtil.isBlank(entity.getMonthDays())) {
            throw new CustomException("月检请填写日期");
        }
        if (ScheduleFrequency.CUSTOM.getKey().equals(freq) && StrUtil.isBlank(entity.getCustomCron())) {
            throw new CustomException("自定义频率请填写Cron表达式");
        }
    }

    @Override
    public void createPrepose(EquipmentInspectionPlan entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        entity.setOddNumber(oddNumber);
    }

    @Override
    public void writePostpose(EquipmentInspectionPlan entity, String userId) {
        iQuartzService.stopAndDeleteTaskQuartz(entity.getId());
        equipmentInspectionPlanEquipmentService.saveList(entity.getId(), entity.getEquipmentId());
        super.writePostpose(entity, userId);
        if (EnableEnum.ENABLE_USING.getKey().equals(entity.getEnabled())) {
            String cron = EquipmentInspectionPlanCronBuilder.buildScheduleConf(entity);
            if (StrUtil.isEmpty(cron)) {
                throw new CustomException("定时Cron生成失败");
            }
            SysQuartzMation quartz = new SysQuartzMation();
            quartz.setName(entity.getId());
            quartz.setTitle(entity.getName());
            quartz.setScheduleConf(cron);
            quartz.setGroupId(QuartzConstants.QuartzMateMationJobType.EQUIPMENT_INSPECTION_PLAN_TASK_GENERATE.getTaskType());
            iQuartzService.startUpTaskQuartz(quartz);
        }
    }

    @Override
    public EquipmentInspectionPlan getDataFromDb(String id) {
        EquipmentInspectionPlan plan = super.getDataFromDb(id);
        plan.setEquipmentId(equipmentInspectionPlanEquipmentService.selectByParentId(id));
        return plan;
    }

    @Override
    public List<EquipmentInspectionPlan> getDataFromDb(List<String> idList) {
        List<EquipmentInspectionPlan> planList = super.getDataFromDb(idList);
        if (CollectionUtil.isEmpty(planList)) {
            return planList;
        }
        List<String> planIdList = planList.stream().map(EquipmentInspectionPlan::getId).collect(Collectors.toList());
        Map<String, List<String>> equipmentIdMap = equipmentInspectionPlanEquipmentService.selectMapByParentId(planIdList);
        planList.forEach(plan -> plan.setEquipmentId(equipmentIdMap.get(plan.getId())));
        return planList;
    }

    @Override
    public EquipmentInspectionPlan selectById(String id) {
        EquipmentInspectionPlan plan = super.selectById(id);
        ScheduleFrequency frequency = ScheduleFrequency.getByKey(plan.getFrequency());
        Map<String, Object> frequencyMation = new HashMap<>();
        frequencyMation.put("id", frequency.getKey());
        frequencyMation.put("name", frequency.getValue());
        plan.setFrequencyMation(frequencyMation);
        if (CollectionUtil.isNotEmpty(plan.getEquipmentId())) {
            List<Equipment> equipmentList = equipmentService.selectByIds(plan.getEquipmentId().toArray(new String[]{}));
            List<Map<String, Object>> equipmentMation = CollectionUtil.isEmpty(equipmentList)
                ? Collections.emptyList()
                : equipmentList.stream().map(BeanUtil::beanToMap).collect(Collectors.toList());
            plan.setEquipmentMation(equipmentMation);
        }
        return plan;
    }

    @Override
    protected void deletePostpose(EquipmentInspectionPlan entity) {
        equipmentInspectionPlanEquipmentService.deleteByParentId(entity.getId());
        iQuartzService.stopAndDeleteTaskQuartz(entity.getId());
    }

    @Override
    public void queryAllEquipmentInspectionPlanList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        QueryWrapper<EquipmentInspectionPlan> queryWrapper = new QueryWrapper<>();
        Object enabledObj = params.get("enabled");
        if (enabledObj != null && StrUtil.isNotBlank(enabledObj.toString())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionPlan::getEnabled), enabledObj.toString());
        }
        List<EquipmentInspectionPlan> planList = list(queryWrapper);
        outputObject.setBeans(planList);
        outputObject.settotal(planList.size());
    }

}
