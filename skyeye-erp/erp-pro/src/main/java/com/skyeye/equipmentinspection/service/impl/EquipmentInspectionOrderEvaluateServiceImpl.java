/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionOrderState;
import com.skyeye.equipmentinspection.dao.EquipmentInspectionOrderEvaluateDao;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrder;
import com.skyeye.equipmentinspection.entity.EquipmentInspectionOrderEvaluate;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderEvaluateService;
import com.skyeye.equipmentinspection.service.EquipmentInspectionOrderService;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionOrderEvaluateServiceImpl
 * @Description: 设备巡检单评价服务层
 */
@Service
@SkyeyeService(name = "设备巡检单评价", groupName = "设备巡检", manageShow = false)
public class EquipmentInspectionOrderEvaluateServiceImpl
    extends SkyeyeBusinessServiceImpl<EquipmentInspectionOrderEvaluateDao, EquipmentInspectionOrderEvaluate>
    implements EquipmentInspectionOrderEvaluateService {

    /** 系统自动评价 */
    public static final int EVALUATE_TYPE_SYSTEM = 1;

    /** 自动好评内容 */
    public static final String AUTO_EVALUATE_CONTENT = "系统默认好评";

    /**
     * 自动好评评价类型占位
     */
    public static final String AUTO_EVALUATE_TYPE_ID = "system-auto-good";

    @Autowired
    private EquipmentInspectionOrderService equipmentInspectionOrderService;

    @Autowired
    private IQuartzService iQuartzService;

    @Override
    protected QueryWrapper<EquipmentInspectionOrderEvaluate> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentInspectionOrderEvaluate> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrderEvaluate::getObjectId),
                commonPageInfo.getObjectId());
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentInspectionOrderEvaluate::getCreateTime));
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iSysDictDataService.setMationForMap(beans, "typeId", "typeMation");
        return beans;
    }

    @Override
    protected void validatorEntity(EquipmentInspectionOrderEvaluate entity) {
        EquipmentInspectionOrder order = equipmentInspectionOrderService.selectById(entity.getObjectId());
        if (StrUtil.isBlank(order.getId())) {
            throw new CustomException("巡检单不存在");
        }
        if (!ObjectUtil.equal(order.getState(), EquipmentInspectionOrderState.COMPLETED.getKey())) {
            throw new CustomException("仅已完成的巡检单可以评价");
        }
        QueryWrapper<EquipmentInspectionOrderEvaluate> existWrapper = new QueryWrapper<>();
        existWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrderEvaluate::getObjectId), entity.getObjectId());
        if (count(existWrapper) > 0) {
            throw new CustomException("该巡检单已经评价。");
        }
        if (StrUtil.isBlank(entity.getObjectKey())) {
            entity.setObjectKey(EquipmentInspectionOrderServiceImpl.class.getName());
        }
    }

    @Override
    protected void createPostpose(EquipmentInspectionOrderEvaluate entity, String userId) {
        // 评价落库后取消「30 天自动好评」延时任务
        iQuartzService.stopAndDeleteTaskQuartz(entity.getObjectId());
    }

    @Override
    public void autoEvaluateByOrderId(String orderId, String userId) {
        if (StrUtil.isBlank(orderId)) {
            return;
        }
        EquipmentInspectionOrder order = equipmentInspectionOrderService.selectById(orderId);
        if (StrUtil.isBlank(order.getId())
            || !ObjectUtil.equal(order.getState(), EquipmentInspectionOrderState.COMPLETED.getKey())) {
            return;
        }
        QueryWrapper<EquipmentInspectionOrderEvaluate> existWrapper = new QueryWrapper<>();
        existWrapper.eq(MybatisPlusUtil.toColumns(EquipmentInspectionOrderEvaluate::getObjectId), orderId);
        if (count(existWrapper) > 0) {
            return;
        }
        EquipmentInspectionOrderEvaluate evaluate = new EquipmentInspectionOrderEvaluate();
        evaluate.setObjectId(orderId);
        evaluate.setObjectKey(EquipmentInspectionOrderServiceImpl.class.getName());
        evaluate.setType(EVALUATE_TYPE_SYSTEM);
        evaluate.setTypeId(AUTO_EVALUATE_TYPE_ID);
        evaluate.setContent(AUTO_EVALUATE_CONTENT);
        createEntity(evaluate, StrUtil.blankToDefault(userId, order.getCreateId()));
    }

}
