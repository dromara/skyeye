/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.repair.dao.EquipmentSparePartUsageDetailDao;
import com.skyeye.repair.entity.EquipmentSparePartUsageDetail;
import com.skyeye.repair.service.EquipmentSparePartUsageDetailService;
import com.skyeye.rest.sealservice.service.IServiceUserStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 维修工单备件使用明细
 */
@Service
@SkyeyeService(name = "维修工单备件使用明细", groupName = "设备维修", manageShow = false)
public class EquipmentSparePartUsageDetailServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentSparePartUsageDetailDao, EquipmentSparePartUsageDetail> implements EquipmentSparePartUsageDetailService {

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private IServiceUserStockService iServiceUserStockService;

    @Override
    public void saveLinkList(String parentId, List<EquipmentSparePartUsageDetail> detailList) {
        if (CollectionUtil.isEmpty(detailList)) {
            deleteByParentId(parentId);
            return;
        }
        checkLinkList(parentId, detailList);
        calcDetailPrice(detailList);
        deleteByParentId(parentId);
        for (EquipmentSparePartUsageDetail item : detailList) {
            item.setParentId(parentId);
        }
        createEntity(detailList, StrUtil.EMPTY);
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<EquipmentSparePartUsageDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentSparePartUsageDetail::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<EquipmentSparePartUsageDetail> selectByParentId(String parentId) {
        QueryWrapper<EquipmentSparePartUsageDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentSparePartUsageDetail::getParentId), parentId);
        return list(queryWrapper);
    }

    @Override
    public void calcDetailPrice(List<EquipmentSparePartUsageDetail> detailList) {
        if (CollectionUtil.isEmpty(detailList)) {
            return;
        }
        List<String> materialIds = detailList.stream()
            .map(EquipmentSparePartUsageDetail::getMaterialId)
            .collect(Collectors.toList());
        Map<String, List<MaterialNorms>> normsMap = materialNormsService.queryMaterialNormsList(StrUtil.EMPTY, materialIds.toArray(new String[]{}));
        for (EquipmentSparePartUsageDetail detail : detailList) {
            List<MaterialNorms> normsList = normsMap.get(detail.getMaterialId());
            if (CollectionUtil.isEmpty(normsList)) {
                throw new CustomException("数据中包含不存在的备件规格信息.");
            }
            MaterialNorms matchedNorms = normsList.stream()
                .filter(norms -> StrUtil.equals(norms.getId(), detail.getNormsId()))
                .findFirst()
                .orElseThrow(() -> new CustomException("数据中包含不存在的备件规格信息."));
            if (StrUtil.isBlank(matchedNorms.getRetailPrice())) {
                throw new CustomException("备件规格未维护零售价.");
            }
            String unitPrice = matchedNorms.getRetailPrice();
            String rowAllPrice = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, detail.getOperNumber(), unitPrice);
            detail.setUnitPrice(unitPrice);
            detail.setAllPrice(rowAllPrice);
        }
    }

    @Override
    public void checkDetailList(String parentId, List<EquipmentSparePartUsageDetail> beans) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        if (StrUtil.isBlank(parentId)) {
            throw new CustomException("单据ID不能为空.");
        }
        boolean missingMaterialId = beans.stream().anyMatch(bean -> bean == null || StrUtil.isBlank(bean.getMaterialId()));
        if (missingMaterialId) {
            throw new CustomException("请为每条明细选择备件");
        }
        List<String> normsIds = beans.stream()
            .map(EquipmentSparePartUsageDetail::getNormsId)
            .distinct()
            .collect(Collectors.toList());
        if (normsIds.size() != beans.size()) {
            throw new CustomException("备件明细中存在未选择规格的行，或存在重复规格.");
        }
        boolean missingOperNumber = beans.stream().anyMatch(bean -> bean == null || StrUtil.isBlank(bean.getOperNumber())
            || CalculationUtil.compareTo(bean.getOperNumber(), "0", CommonNumConstants.NUM_TWO, RoundingMode.UP) <= 0);
        if (missingOperNumber) {
            throw new CustomException("请为每条明细填写有效的数量");
        }
    }

    @Override
    public void deductStockByParentId(String parentId) {
        List<EquipmentSparePartUsageDetail> detailList = selectByParentId(parentId);
        if (CollectionUtil.isEmpty(detailList)) {
            return;
        }
        for (EquipmentSparePartUsageDetail detail : detailList) {
            if (StrUtil.isBlank(detail.getCreateId())) {
                throw new CustomException("备件使用人信息缺失，无法扣减配件库存！");
            }
            iServiceUserStockService.editMaterialNormsUserStock(
                detail.getCreateId(),
                detail.getMaterialId(),
                detail.getNormsId(),
                detail.getOperNumber(),
                DepotPutOutType.OUT.getKey());
        }
    }

    /**
     * 校验当前登录人配件库存
     */
    private void validateLoginUserStock(List<EquipmentSparePartUsageDetail> detailList) {
        if (CollectionUtil.isEmpty(detailList)) {
            return;
        }
        List<String> normsIds = detailList.stream()
            .map(EquipmentSparePartUsageDetail::getNormsId)
            .collect(Collectors.toList());
        Map<String, Map<String, Object>> userStockMap = iServiceUserStockService.queryUserStock(normsIds);
        for (EquipmentSparePartUsageDetail detail : detailList) {
            Map<String, Object> stockMation = userStockMap.get(detail.getNormsId());
            if (ObjectUtil.isEmpty(stockMation) || stockMation.get("stock") == null) {
                throw new CustomException("部分配件库存不足，请重新选择配件！");
            }
            String stockStr = stockMation.get("stock").toString();
            if (CalculationUtil.compareTo(detail.getOperNumber(), stockStr, CommonNumConstants.NUM_TWO, RoundingMode.UP) > 0) {
                throw new CustomException("部分配件库存不足，请重新选择配件！");
            }
        }
    }

    private void checkLinkList(String parentId, List<EquipmentSparePartUsageDetail> beans) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        checkDetailList(parentId, beans);
        String stockUserId = InputObject.getLogParamsStatic().get("id").toString();
        validateLoginUserStock(beans);
        String now = DateUtil.getTimeAndToString();
        beans.forEach(bean -> {
            bean.setCreateId(stockUserId);
            bean.setCreateTime(now);
        });
    }
}
