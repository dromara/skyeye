/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service.impl;

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
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.exception.CustomException;
import com.skyeye.maintenance.dao.MaintenancePlanDao;
import com.skyeye.maintenance.entity.MaintenancePlan;
import com.skyeye.maintenance.service.MaintenancePlanItemService;
import com.skyeye.maintenance.service.MaintenancePlanService;
import com.skyeye.maintenance.support.MaintenancePlanCronBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description: 保养计划服务层
 */
@Service
@SkyeyeService(name = "保养计划", groupName = "设备保养")
public class MaintenancePlanServiceImpl extends SkyeyeBusinessServiceImpl<MaintenancePlanDao, MaintenancePlan>
    implements MaintenancePlanService {

    @Autowired
    private MaintenancePlanItemService maintenancePlanItemService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private IQuartzService iQuartzService;

    @Override
    protected QueryWrapper<MaintenancePlan> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<MaintenancePlan> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (commonPageInfo.getEnabled() != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(MaintenancePlan::getEnabled), commonPageInfo.getEnabled());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(MaintenancePlan::getEquipmentId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public void createPrepose(MaintenancePlan entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        entity.setOddNumber(iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business));
    }

    @Override
    public void writePostpose(MaintenancePlan entity, String userId) {
        iQuartzService.stopAndDeleteTaskQuartz(entity.getId());
        maintenancePlanItemService.saveList(entity.getId(), entity.getMaintenancePlanItemList());
        super.writePostpose(entity, userId);
        if (EnableEnum.ENABLE_USING.getKey().equals(entity.getEnabled())) {
            String cron = MaintenancePlanCronBuilder.buildScheduleConf(entity);
            if (StrUtil.isEmpty(cron)) {
                throw new CustomException("定时Cron生成失败");
            }
            SysQuartzMation quartz = new SysQuartzMation();
            quartz.setName(entity.getId());
            quartz.setTitle(StrUtil.isNotEmpty(entity.getOddNumber()) ? entity.getOddNumber() : entity.getId());
            quartz.setScheduleConf(cron);
            quartz.setGroupId(QuartzConstants.QuartzMateMationJobType.MAINTENANCE_PLAN_ORDER_GENERATE.getTaskType());
            iQuartzService.startUpTaskQuartz(quartz);
        }
    }

    @Override
    protected void deletePostpose(MaintenancePlan entity) {
        maintenancePlanItemService.deleteByParentId(entity.getId());
        iQuartzService.stopAndDeleteTaskQuartz(entity.getId());
    }

    @Override
    public MaintenancePlan getDataFromDb(String id) {
        MaintenancePlan plan = super.getDataFromDb(id);
        plan.setMaintenancePlanItemList(maintenancePlanItemService.selectByParentId(id));
        return plan;
    }

    @Override
    public MaintenancePlan selectById(String id) {
        MaintenancePlan plan = super.selectById(id);
        if (plan == null) {
            return null;
        }
        equipmentService.setDataMation(plan, MaintenancePlan::getEquipmentId);
        return plan;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        equipmentService.setMationForMap(beans, "equipmentId", "equipmentMation");
        return beans;
    }

    @Override
    public void queryAllMaintenancePlanList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String enabled = params.get("enabled").toString();
        QueryWrapper<MaintenancePlan> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(enabled)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(MaintenancePlan::getEnabled), enabled);
        }
        List<MaintenancePlan> planList = list(queryWrapper);
        outputObject.setBeans(planList);
        outputObject.settotal(planList.size());
    }
}
