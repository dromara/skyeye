/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.exception.CustomException;
import com.skyeye.maintenance.dao.EquipmentMaintainOrderSparePartDetailDao;
import com.skyeye.maintenance.entity.EquipmentMaintainOrderSparePartDetail;
import com.skyeye.maintenance.service.EquipmentMaintainOrderSparePartDetailService;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.rest.sealservice.service.IServiceUserStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 设备保养单备件领用明细服务层
 */
@Service
@SkyeyeService(name = "设备保养单备件领用明细", groupName = "设备保养", manageShow = false)
public class EquipmentMaintainOrderSparePartDetailServiceImpl
    extends SkyeyeBusinessServiceImpl<EquipmentMaintainOrderSparePartDetailDao, EquipmentMaintainOrderSparePartDetail>
    implements EquipmentMaintainOrderSparePartDetailService {

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private IServiceUserStockService iServiceUserStockService;

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void saveLinkList(String parentId, List<EquipmentMaintainOrderSparePartDetail> detailList) {
        deleteByParentId(parentId);
        calcDetailPrice(detailList);
        checkDetailList(parentId, detailList);
        detailList.forEach(item -> item.setParentId(parentId));
        createEntity(detailList, StrUtil.EMPTY);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deductStockByParentId(String parentId) {
        List<EquipmentMaintainOrderSparePartDetail> detailList = selectByParentId(parentId);
        if (CollectionUtil.isEmpty(detailList)) {
            return;
        }
        String stockUserId = InputObject.getLogParamsStatic().get("id").toString();
        validateUserStock(stockUserId, detailList);
        changeUserStock(stockUserId, detailList, DepotPutOutType.OUT.getKey());
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<EquipmentMaintainOrderSparePartDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentMaintainOrderSparePartDetail::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<EquipmentMaintainOrderSparePartDetail> selectByParentId(String parentId) {
        QueryWrapper<EquipmentMaintainOrderSparePartDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentMaintainOrderSparePartDetail::getParentId), parentId);
        return list(queryWrapper);
    }

    private void calcDetailPrice(List<EquipmentMaintainOrderSparePartDetail> detailList) {
        if (CollectionUtil.isEmpty(detailList)) {
            return;
        }
        List<String> materialIds = detailList.stream()
            .map(EquipmentMaintainOrderSparePartDetail::getMaterialId)
            .collect(Collectors.toList());
        Map<String, List<MaterialNorms>> normsMap = materialNormsService.queryMaterialNormsList(
            StrUtil.EMPTY, materialIds.toArray(new String[]{}));
        for (EquipmentMaintainOrderSparePartDetail detail : detailList) {
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
            detail.setUnitPrice(unitPrice);
            detail.setAllPrice(CalculationUtil.multiply(CommonNumConstants.NUM_TWO, detail.getOperNumber(), unitPrice));
        }
    }

    private void checkDetailList(String parentId, List<EquipmentMaintainOrderSparePartDetail> detailList) {
        if (CollectionUtil.isEmpty(detailList)) {
            return;
        }
        if (StrUtil.isBlank(parentId)) {
            throw new CustomException("单据ID不能为空.");
        }
        boolean missingMaterialId = detailList.stream()
            .anyMatch(bean -> bean == null || StrUtil.isBlank(bean.getMaterialId()));
        if (missingMaterialId) {
            throw new CustomException("请为每条明细选择备件");
        }
        List<String> normsIds = detailList.stream()
            .map(EquipmentMaintainOrderSparePartDetail::getNormsId)
            .distinct()
            .collect(Collectors.toList());
        if (normsIds.size() != detailList.size()) {
            throw new CustomException("备件明细中存在未选择规格的行，或存在重复规格.");
        }
        boolean missingOperNumber = detailList.stream().anyMatch(bean -> bean == null || StrUtil.isBlank(bean.getOperNumber())
            || CalculationUtil.compareTo(bean.getOperNumber(), "0", CommonNumConstants.NUM_TWO, RoundingMode.UP) <= 0);
        if (missingOperNumber) {
            throw new CustomException("请为每条明细填写有效的数量");
        }
    }

    private void validateUserStock(String userId, List<EquipmentMaintainOrderSparePartDetail> detailList) {
        if (CollectionUtil.isEmpty(detailList)) {
            return;
        }
        List<String> normsIds = detailList.stream()
            .map(EquipmentMaintainOrderSparePartDetail::getNormsId)
            .collect(Collectors.toList());
        Map<String, Map<String, Object>> userStockMap = iServiceUserStockService.queryUserStock(userId, normsIds);
        for (EquipmentMaintainOrderSparePartDetail detail : detailList) {
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

    private void changeUserStock(String stockUserId, List<EquipmentMaintainOrderSparePartDetail> detailList, int type) {
        if (StrUtil.isEmpty(stockUserId) || CollectionUtil.isEmpty(detailList)) {
            return;
        }
        detailList.forEach(detail -> iServiceUserStockService.editMaterialNormsUserStock(
            stockUserId,
            detail.getMaterialId(),
            detail.getNormsId(),
            detail.getOperNumber(),
            type));
    }

}
