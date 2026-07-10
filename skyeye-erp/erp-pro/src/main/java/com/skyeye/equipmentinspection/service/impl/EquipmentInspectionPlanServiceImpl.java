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
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionPlanDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionItem;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionPlan;
import com.skyeye.equipmentinspection.service.EquipmentInspectionItemService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanEquipmentService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanItemService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionPlanService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionTeamService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionTaskPlanSyncService;
import com.skyeye.equipmentinspection.support.EquipmentInspectionPlanCronBuilder;
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentInspectionPlanServiceImpl
 * @Description: 设备巡检方案服务实现类
 */
@Service
@SkyeyeService(name = "设备巡检方案", groupName = "设备巡检", allowDynamicAttrKey = false)
public class EquipmentInspectionPlanServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentInspectionPlanDao, EquipmentInspectionPlan>
    implements EquipmentInspectionPlanService {

    @Autowired
    private EquipmentInspectionPlanItemService equipmentInspectionPlanItemService;

    @Autowired
    private EquipmentInspectionPlanEquipmentService equipmentInspectionPlanEquipmentService;

    @Autowired
    private EquipmentInspectionItemService equipmentInspectionItemService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EquipmentInspectionTeamService equipmentInspectionTeamService;

    @Autowired
    private IQuartzService iQuartzService;

    @Lazy
    @Autowired
    private EquipmentInspectionTaskPlanSyncService equipmentInspectionTaskPlanSyncService;

    @Override
    protected QueryWrapper<EquipmentInspectionPlan> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentInspectionPlan> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (commonPageInfo.getEnabled() != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionPlan::getEnabled), commonPageInfo.getEnabled());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionPlan::getTeamId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public void createPrepose(EquipmentInspectionPlan entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        entity.setOddNumber(iCodeRuleService.getNextCodeByClassName(getClass().getName(), business));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writePostpose(EquipmentInspectionPlan entity, String userId) {
        iQuartzService.stopAndDeleteTaskQuartz(entity.getId());
        equipmentInspectionPlanEquipmentService.saveList(entity.getId(), entity.getEquipmentId());
        equipmentInspectionPlanItemService.saveList(entity.getId(), entity.getItemId());
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
    public EquipmentInspectionPlan selectById(String id) {
        EquipmentInspectionPlan plan = super.selectById(id);
        plan.setEquipmentId(equipmentInspectionPlanEquipmentService.selectByParentId(id));
        plan.setItemId(equipmentInspectionPlanItemService.selectByParentId(id));
        equipmentInspectionTeamService.setDataMation(plan, EquipmentInspectionPlan::getTeamId);
        if (CollectionUtil.isNotEmpty(plan.getEquipmentId())) {
            plan.setEquipmentMation(equipmentService.selectByIds(plan.getEquipmentId().toArray(new String[]{})));
        }
        if (CollectionUtil.isNotEmpty(plan.getItemId())) {
            List<EquipmentInspectionItem> items = equipmentInspectionItemService.selectByIds(plan.getItemId().toArray(new String[]{}));
            plan.setItemMation(items);
        }
        return plan;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isNotEmpty(beans)) {
            equipmentInspectionTeamService.setMationForMap(beans, "teamId", "teamMation");
        }
        return beans;
    }

    @Override
    public EquipmentInspectionPlan getDataFromDb(String id) {
        EquipmentInspectionPlan entity = super.getDataFromDb(id);
        entity.setEquipmentId(equipmentInspectionPlanEquipmentService.selectByParentId(id));
        entity.setItemId(equipmentInspectionPlanItemService.selectByParentId(id));
        return entity;
    }

    @Override
    public List<EquipmentInspectionPlan> getDataFromDb(List<String> idList) {
        List<EquipmentInspectionPlan> planList = super.getDataFromDb(idList);
        if (CollectionUtil.isEmpty(planList)) {
            return planList;
        }
        List<String> planIdList = planList.stream().map(EquipmentInspectionPlan::getId).collect(Collectors.toList());
        Map<String, List<String>> equipmentIdMap = equipmentInspectionPlanEquipmentService.selectMapByParentId(planIdList);
        Map<String, List<String>> itemIdMap = equipmentInspectionPlanItemService.selectMapByParentId(planIdList);
        planList.forEach(plan -> {
            plan.setEquipmentId(equipmentIdMap.get(plan.getId()));
            plan.setItemId(itemIdMap.get(plan.getId()));
        });
        return planList;
    }

    @Override
    public void deletePostpose(String id) {
        equipmentInspectionPlanEquipmentService.deleteByParentId(id);
        equipmentInspectionPlanItemService.deleteByParentId(id);
        iQuartzService.stopAndDeleteTaskQuartz(id);
    }

    @Override
    public int calcRequiredInspectionCount(EquipmentInspectionPlan plan, String startTime, String endTime) {
        return equipmentInspectionTaskPlanSyncService.countRangeSlots(plan, startTime, endTime);
    }

    @Override
    public void queryAllEquipmentInspectionPlanList(InputObject inputObject, OutputObject outputObject) {
        List<EquipmentInspectionPlan> planList = list();
        outputObject.setBeans(planList);
        outputObject.settotal(planList.size());
    }

}
