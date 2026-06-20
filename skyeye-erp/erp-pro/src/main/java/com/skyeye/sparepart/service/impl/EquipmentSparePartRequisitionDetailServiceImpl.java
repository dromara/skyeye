/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.sparepart.dao.EquipmentSparePartRequisitionDetailDao;
import com.skyeye.sparepart.entity.EquipmentSparePartRequisitionDetail;
import com.skyeye.sparepart.entity.EquipmentUserStock;
import com.skyeye.sparepart.service.EquipmentSparePartRequisitionDetailService;
import com.skyeye.sparepart.service.EquipmentUserStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 备件领用明细（校验并扣减我的库存）
 */
@Service
@SkyeyeService(name = "备件领用明细", groupName = "设备备件", manageShow = false)
public class EquipmentSparePartRequisitionDetailServiceImpl extends SkyeyeLinkDataServiceImpl<EquipmentSparePartRequisitionDetailDao, EquipmentSparePartRequisitionDetail>
    implements EquipmentSparePartRequisitionDetailService {

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private EquipmentUserStockService equipmentUserStockService;

    @Override
    protected void checkLinkList(String pId, List<EquipmentSparePartRequisitionDetail> beans) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        if (StrUtil.isBlank(pId)) {
            throw new CustomException("领用单ID不能为空.");
        }
        List<String> normsIds = beans.stream()
            .map(EquipmentSparePartRequisitionDetail::getNormsId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        if (normsIds.size() != beans.size()) {
            throw new CustomException("领用明细中存在未选择规格的行，或存在重复规格.");
        }
        boolean missingOperNumber = beans.stream().anyMatch(bean -> bean == null || bean.getOperNumber() == null || bean.getOperNumber() <= 0);
        if (missingOperNumber) {
            throw new CustomException("请为每条明细填写有效的领用数量");
        }
        String stockUserId = InputObject.getLogParamsStatic().get("id").toString();
        Map<String, EquipmentUserStock> userStockMap = equipmentUserStockService.queryUserStock(stockUserId, normsIds);
        beans.forEach(bean -> {
            EquipmentUserStock userStock = userStockMap.get(bean.getNormsId());
            if (ObjectUtil.isEmpty(userStock) || StrUtil.isBlank(userStock.getStock())) {
                throw new CustomException("部分配件库存不足，请重新选择配件！");
            }
            String stockStr = userStock.getStock();
            if (CalculationUtil.compareTo(String.valueOf(bean.getOperNumber()), stockStr, CommonNumConstants.NUM_TWO, RoundingMode.UP) > 0) {
                throw new CustomException("部分配件库存不足，请重新选择配件！");
            }
        });
    }

    @Override
    public String calcOrderAllTotalPrice(List<EquipmentSparePartRequisitionDetail> detailList) {
        if (CollectionUtil.isEmpty(detailList)) {
            return CommonNumConstants.NUM_ZERO.toString();
        }
        List<String> materialIds = detailList.stream()
            .map(EquipmentSparePartRequisitionDetail::getMaterialId)
            .collect(Collectors.toList());
        Map<String, List<MaterialNorms>> normsMap = materialNormsService.queryMaterialNormsList(StrUtil.EMPTY, materialIds.toArray(new String[]{}));
        String allPrice = CommonNumConstants.NUM_ZERO.toString();
        for (EquipmentSparePartRequisitionDetail detail : detailList) {
            List<MaterialNorms> normsList = normsMap.get(detail.getMaterialId());
            if (CollectionUtil.isEmpty(normsList)) {
                throw new CustomException("数据中包含不存在的备件规格信息.");
            }
            MaterialNorms firstNorms = normsList.stream()
                .filter(norms -> StrUtil.isNotBlank(norms.getRetailPrice()))
                .findFirst()
                .orElseThrow(() -> new CustomException("备件规格未维护零售价."));
            String unitPrice = firstNorms.getRetailPrice();
            String rowAllPrice = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, String.valueOf(detail.getOperNumber()), unitPrice);
            detail.setUnitPrice(new BigDecimal(unitPrice));
            detail.setAllPrice(new BigDecimal(rowAllPrice));
            allPrice = CalculationUtil.add(rowAllPrice, allPrice);
        }
        return allPrice;
    }

    @Override
    public List<EquipmentSparePartRequisitionDetail> selectByPIds(List<String> pIds) {
        if (CollectionUtil.isEmpty(pIds)) {
            return new ArrayList<>();
        }
        QueryWrapper<EquipmentSparePartRequisitionDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(EquipmentSparePartRequisitionDetail::getParentId), pIds);
        return list(queryWrapper);
    }

}
