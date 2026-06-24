/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
        return queryWrapper;
    }

    @Override
    public void createPrepose(EquipmentInspectionItem entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        entity.setOddNumber(iCodeRuleService.getNextCodeByClassName(getClass().getName(), business));
    }

    @Override
    public void queryAllEquipmentInspectionItemList(InputObject inputObject, OutputObject outputObject) {
        List<EquipmentInspectionItem> itemList = list();
        outputObject.setBeans(itemList);
        outputObject.settotal(itemList.size());
    }

}
