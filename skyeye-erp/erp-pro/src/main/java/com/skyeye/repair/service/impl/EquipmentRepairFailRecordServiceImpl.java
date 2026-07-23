/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.repair.dao.EquipmentRepairFailRecordDao;
import com.skyeye.repair.entity.EquipmentRepairFailRecord;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.service.EquipmentRepairFailRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备维修失败履历
 */
@Service
@SkyeyeService(name = "设备维修失败履历", groupName = "设备维修", manageShow = false)
public class EquipmentRepairFailRecordServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentRepairFailRecordDao, EquipmentRepairFailRecord> implements EquipmentRepairFailRecordService {

    @Override
    public void saveFailRecord(EquipmentRepairOrder order) {
        EquipmentRepairFailRecord record = new EquipmentRepairFailRecord();
        record.setParentId(order.getId());
        record.setServiceUserId(order.getServiceUserId());
        record.setFailTime(DateUtil.getTimeAndToString());
        record.setRepairDesc(order.getRepairDesc());
        createEntity(record, StrUtil.EMPTY);
    }

    @Override
    public List<EquipmentRepairFailRecord> selectByParentId(String parentId) {
        QueryWrapper<EquipmentRepairFailRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairFailRecord::getParentId), parentId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(EquipmentRepairFailRecord::getFailTime));
        return list(queryWrapper);
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<EquipmentRepairFailRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairFailRecord::getParentId), parentId);
        remove(queryWrapper);
    }

}
