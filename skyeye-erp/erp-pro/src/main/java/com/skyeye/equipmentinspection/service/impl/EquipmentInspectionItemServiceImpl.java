/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionItemDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionItem;
import com.skyeye.equipmentinspection.service.EquipmentInspectionItemService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionItemServiceImpl
 * @Description: 设备巡检项目服务实现类
 */
@Service
@SkyeyeService(name = "设备巡检项目", groupName = "设备巡检", allowDynamicAttrKey = false)
public class EquipmentInspectionItemServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentInspectionItemDao, EquipmentInspectionItem>
    implements EquipmentInspectionItemService {

    @Override
    protected QueryWrapper<EquipmentInspectionItem> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentInspectionItem> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (commonPageInfo.getEnabled() != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionItem::getEnabled), commonPageInfo.getEnabled());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentInspectionItem::getCreateTime));
        return queryWrapper;
    }

    @Override
    public void createPrepose(EquipmentInspectionItem entity) {
        assignItemCode(entity);
        entity.setName(StrUtil.blankToDefault(entity.getName(), entity.getItemCode()));
    }

    @Override
    public void queryAllEquipmentInspectionItemList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        QueryWrapper<EquipmentInspectionItem> queryWrapper = new QueryWrapper<>();
        if (params.get("enabled") != null && StrUtil.isNotBlank(params.get("enabled").toString())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionItem::getEnabled), params.get("enabled"));
        }
        List<EquipmentInspectionItem> itemList = list(queryWrapper);
        outputObject.setBeans(itemList);
        outputObject.settotal(itemList.size());
    }

    private void assignItemCode(EquipmentInspectionItem entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        business.remove("itemCode");
        String itemCode = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        if (StrUtil.isBlank(itemCode)) {
            itemCode = iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business);
        }
        if (StrUtil.isBlank(itemCode)) {
            throw new CustomException("巡检项目编码生成失败，请检查编码规则是否已绑定到业务对象.");
        }
        entity.setItemCode(itemCode);
    }

}
