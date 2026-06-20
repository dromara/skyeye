/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.sparepart.dao.EquipmentSparePartApplyLinkDao;
import com.skyeye.sparepart.entity.EquipmentSparePartApplyLink;
import com.skyeye.sparepart.service.EquipmentSparePartApplyLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备备件-申领明细
 */
@Service
@SkyeyeService(name = "备件申领明细", groupName = "设备备件", manageShow = false)
public class EquipmentSparePartApplyLinkServiceImpl extends SkyeyeLinkDataServiceImpl<EquipmentSparePartApplyLinkDao, EquipmentSparePartApplyLink>
    implements EquipmentSparePartApplyLinkService {

    @Autowired
    private MaterialNormsService materialNormsService;

    @Override
    protected void checkLinkList(String pId, List<EquipmentSparePartApplyLink> beans) {
        List<String> checkIds = beans.stream()
            .map(bean -> String.format(Locale.ROOT, "%s_%s", bean.getNormsId(), bean.getDepotId()))
            .distinct()
            .collect(Collectors.toList());
        if (checkIds.size() != beans.size()) {
            throw new CustomException("存在来源为相同仓库的重复备件规格信息.");
        }
    }

    @Override
    public String calcOrderAllTotalPrice(List<EquipmentSparePartApplyLink> applyLinkList) {
        List<String> materialIds = applyLinkList.stream().map(EquipmentSparePartApplyLink::getMaterialId).collect(Collectors.toList());
        Map<String, List<com.skyeye.material.entity.MaterialNorms>> normsMap = materialNormsService.queryMaterialNormsList(
            StrUtil.EMPTY, materialIds.toArray(new String[]{}));
        String allPrice = CommonNumConstants.NUM_ZERO.toString();
        for (EquipmentSparePartApplyLink applyLink : applyLinkList) {
            List<com.skyeye.material.entity.MaterialNorms> normsList = normsMap.get(applyLink.getMaterialId());
            if (CollectionUtil.isEmpty(normsList)) {
                throw new CustomException("数据中包含不存在的备件规格信息.");
            }
            com.skyeye.material.entity.MaterialNorms norms = normsList.stream()
                .filter(item -> StrUtil.equals(item.getId(), applyLink.getNormsId()))
                .findFirst()
                .orElseThrow(() -> new CustomException("数据中包含不存在的备件规格信息."));
            applyLink.setUnitPrice(norms.getRetailPrice());
            applyLink.setAllPrice(
                CalculationUtil.multiply(CommonNumConstants.NUM_TWO, String.valueOf(applyLink.getOperNumber()), applyLink.getUnitPrice()));
            allPrice = CalculationUtil.add(applyLink.getAllPrice(), allPrice);
        }
        return allPrice;
    }

}
