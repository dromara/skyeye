/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.classenum.EquipmentState;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.exception.CustomException;
import com.skyeye.repair.dao.EquipmentScrapOrderDao;
import com.skyeye.repair.entity.EquipmentScrapOrder;
import com.skyeye.repair.service.EquipmentScrapOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @ClassName: EquipmentScrapOrderServiceImpl
 * @Description: 设备报废单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/04/30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "设备报废单", groupName = "设备维修", flowable = true)
public class EquipmentScrapOrderServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentScrapOrderDao, EquipmentScrapOrder> implements EquipmentScrapOrderService {

    @Autowired
    private EquipmentService equipmentService;

    @Override
    protected QueryWrapper<EquipmentScrapOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentScrapOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String equipmentId = commonPageInfo.getCustomParamsMapStr("equipmentId");
        if (StrUtil.isNotEmpty(equipmentId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentScrapOrder::getEquipmentId), equipmentId);
        }
        return queryWrapper;
    }

    @Override
    public EquipmentScrapOrder selectById(String id) {
        EquipmentScrapOrder entity = super.selectById(id);
        if (entity == null) {
            return null;
        }
        equipmentService.setDataMation(entity, EquipmentScrapOrder::getEquipmentId);
        iAuthUserService.setDataMation(entity, EquipmentScrapOrder::getUserId);
        if (StrUtil.isNotEmpty(entity.getStaffId())) {
            Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(
                Collections.singletonList(entity.getStaffId()));
            entity.setStaffMation(staffMap.get(entity.getStaffId()));
        }
        return entity;
    }

    @Override
    public void createPrepose(EquipmentScrapOrder entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
    }

    @Override
    public void validatorEntity(EquipmentScrapOrder entity) {
        super.validatorEntity(entity);
        validateEquipmentForScrap(entity.getEquipmentId());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void approvalEndIsSuccess(EquipmentScrapOrder entity) {
        EquipmentScrapOrder scrapOrder = selectById(entity.getId());
        if (scrapOrder == null || StrUtil.isEmpty(scrapOrder.getEquipmentId())) {
            throw new CustomException("报废单设备信息不存在");
        }
        validateEquipmentForScrap(scrapOrder.getEquipmentId());
        equipmentService.editEquipmentStateById(scrapOrder.getEquipmentId(), EquipmentState.SCRAPPED.getKey());
    }

    /**
     * 校验设备是否存在且未报废（设备状态以 erp_equipment 表为准）
     */
    private void validateEquipmentForScrap(String equipmentId) {
        if (StrUtil.isEmpty(equipmentId)) {
            throw new CustomException("设备不能为空");
        }
        Equipment equipment = equipmentService.selectById(equipmentId);
        if (equipment == null || StrUtil.isEmpty(equipment.getId())) {
            throw new CustomException("设备不存在: " + equipmentId);
        }
        if (EquipmentState.SCRAPPED.getKey().equals(equipment.getEquipmentState())) {
            throw new CustomException("设备已报废，无法重复申请");
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        equipmentService.setMationForMap(beans, "equipmentId", "equipmentMation");
        iAuthUserService.setMationForMap(beans, "userId", "userMation");
        List<String> staffIds = beans.stream()
            .map(bean -> bean.get("staffId"))
            .filter(Objects::nonNull)
            .map(Object::toString)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(staffIds)) {
            Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
            beans.forEach(bean -> {
                Object staffId = bean.get("staffId");
                if (staffId != null && StrUtil.isNotEmpty(staffId.toString())) {
                    bean.put("staffMation", staffMap.get(staffId.toString()));
                }
            });
        }
        return beans;
    }

    @Override
    public void queryAllEquipmentScrapOrderList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<EquipmentScrapOrder> queryWrapper = new QueryWrapper<>();
        List<EquipmentScrapOrder> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}
