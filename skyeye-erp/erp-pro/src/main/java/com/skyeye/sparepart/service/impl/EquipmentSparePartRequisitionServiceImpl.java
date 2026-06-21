/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.sparepart.dao.EquipmentSparePartRequisitionDao;
import com.skyeye.sparepart.entity.EquipmentSparePartRequisition;
import com.skyeye.sparepart.entity.EquipmentSparePartRequisitionDetail;
import com.skyeye.repair.classenum.EquipmentRepairOrderState;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import com.skyeye.sparepart.classenum.EquipmentSparePartRequisitionPurpose;
import com.skyeye.sparepart.classenum.EquipmentUserStockPutOutType;
import com.skyeye.sparepart.service.EquipmentSparePartRequisitionDetailService;
import com.skyeye.sparepart.service.EquipmentSparePartRequisitionService;
import com.skyeye.sparepart.entity.EquipmentUserStock;
import com.skyeye.sparepart.service.EquipmentUserStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 备件领用单：支持从我的库存发起领用，参考售后工单故障配件(SealFault)模式。
 */
@Service
@SkyeyeService(name = "备件领用单", groupName = "设备备件")
public class EquipmentSparePartRequisitionServiceImpl extends SkyeyeLinkDataServiceImpl<EquipmentSparePartRequisitionDao, EquipmentSparePartRequisition>
    implements EquipmentSparePartRequisitionService {

    @Autowired
    private EquipmentSparePartRequisitionDetailService equipmentSparePartRequisitionDetailService;

    @Autowired
    private EquipmentRepairOrderService equipmentRepairOrderService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private EquipmentUserStockService equipmentUserStockService;

    @Override
    public void validatorEntity(EquipmentSparePartRequisition entity) {
        if (CollectionUtil.isEmpty(entity.getDetailList())) {
            throw new CustomException("请至少填写一条领用明细");
        }
        String allPrice = equipmentSparePartRequisitionDetailService.calcOrderAllTotalPrice(entity.getDetailList());
        entity.setTotalAmount(new BigDecimal(allPrice));
    }

    @Override
    public void createPrepose(EquipmentSparePartRequisition entity) {
        check(entity);
        if (StrUtil.isNotBlank(entity.getRepairOrderId())) {
            entity.setParentId(entity.getRepairOrderId());
        }
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
    }

    @Override
    public void updatePrepose(EquipmentSparePartRequisition entity) {
        check(entity);
        if (StrUtil.isNotBlank(entity.getRepairOrderId())) {
            entity.setParentId(entity.getRepairOrderId());
        }
        // 回退数量
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        revertNum(entity.getId(), userId);
    }

    @Override
    public void writePostpose(EquipmentSparePartRequisition entity, String userId) {
        if (CollectionUtil.isNotEmpty(entity.getDetailList())) {
            equipmentSparePartRequisitionDetailService.saveLinkList(entity.getId(), entity.getDetailList());
            // 出库
            for (EquipmentSparePartRequisitionDetail detail : entity.getDetailList()) {
                equipmentUserStockService.editMaterialNormsUserStock(userId, detail.getMaterialId(), detail.getNormsId(),
                    String.valueOf(detail.getOperNumber()), EquipmentUserStockPutOutType.OUT.getKey());
            }
        }
        super.writePostpose(entity, userId);
    }

    @Override
    public EquipmentSparePartRequisition getDataFromDb(String id) {
        EquipmentSparePartRequisition bean = super.getDataFromDb(id);
        bean.setDetailList(equipmentSparePartRequisitionDetailService.selectByPId(bean.getId()));
        return bean;
    }

    @Override
    public EquipmentSparePartRequisition selectById(String id) {
        EquipmentSparePartRequisition entity = super.selectById(id);
        if (entity == null) {
            return null;
        }
        if (StrUtil.isNotBlank(entity.getRepairOrderId())) {
            entity.setSourceOrderMation(BeanUtil.beanToMap(equipmentRepairOrderService.selectById(entity.getRepairOrderId())));
            if (entity.getSourceOrderMation() != null) {
                entity.getSourceOrderMation().remove("sparePartRequisitionList");
            }
        }
        erpDepotService.setDataMation(entity, EquipmentSparePartRequisition::getDepotId);
        iAuthUserService.setDataMation(entity, EquipmentSparePartRequisition::getUserId);
        if (CollectionUtil.isNotEmpty(entity.getDetailList())) {
            materialService.setDataMation(entity.getDetailList(), EquipmentSparePartRequisitionDetail::getMaterialId);
            materialNormsService.setDataMation(entity.getDetailList(), EquipmentSparePartRequisitionDetail::getNormsId);
            List<String> normsIds = entity.getDetailList().stream()
                .map(EquipmentSparePartRequisitionDetail::getNormsId)
                .collect(Collectors.toList());
            // 获取我的库存信息
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            Map<String, EquipmentUserStock> userStockMap = equipmentUserStockService.queryUserStock(userId, normsIds);
            entity.getDetailList().forEach(detail -> detail.setEquipmentUserStockMation(userStockMap.get(detail.getNormsId())));
        }
        return entity;
    }

    @Override
    public void deletePreExecution(EquipmentSparePartRequisition entity) {
        // 回退数量
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        revertNum(entity.getId(), userId);
        equipmentSparePartRequisitionDetailService.deleteByPId(entity.getId());
    }

    @Override
    public List<EquipmentSparePartRequisition> selectByPId(String pId) {
        List<EquipmentSparePartRequisition> requisitionList = super.selectByPId(pId);
        if (CollectionUtil.isEmpty(requisitionList)) {
            return requisitionList;
        }
        List<String> requisitionIds = requisitionList.stream()
            .map(EquipmentSparePartRequisition::getId)
            .collect(Collectors.toList());
        List<EquipmentSparePartRequisitionDetail> allDetails = equipmentSparePartRequisitionDetailService.selectByPIds(requisitionIds);
        Map<String, List<EquipmentSparePartRequisitionDetail>> detailsMap = allDetails.stream()
            .collect(Collectors.groupingBy(EquipmentSparePartRequisitionDetail::getParentId));
        requisitionList.forEach(bean -> bean.setDetailList(detailsMap.getOrDefault(bean.getId(), new java.util.ArrayList<>())));
        return requisitionList;
    }

    private static void check(EquipmentSparePartRequisition entity) {
        if (CollectionUtil.isEmpty(entity.getDetailList())) {
            return;
        }
        List<String> normsIds = entity.getDetailList().stream()
            .map(EquipmentSparePartRequisitionDetail::getNormsId)
            .distinct()
            .collect(Collectors.toList());
        if (entity.getDetailList().size() != normsIds.size()) {
            throw new CustomException("单据中不允许存在重复的产品规格信息");
        }
    }

    private void revertNum(String id, String userId) {
        EquipmentSparePartRequisition requisition = selectById(id);
        if (CollectionUtil.isEmpty(requisition.getDetailList())) {
            return;
        }
        requisition.getDetailList().forEach(detail ->
            equipmentUserStockService.editMaterialNormsUserStock(userId, detail.getMaterialId(), detail.getNormsId(),
                String.valueOf(detail.getOperNumber()), EquipmentUserStockPutOutType.PUT.getKey()));
    }

}
