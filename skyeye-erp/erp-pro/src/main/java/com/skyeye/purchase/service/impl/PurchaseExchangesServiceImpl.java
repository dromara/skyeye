/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/dromara/skyeye
 ******************************************************************************/

package com.skyeye.purchase.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.classenum.OrderItemQualityInspectionType;
import com.skyeye.business.classenum.OrderQualityInspectionType;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.classenum.ErpOrderStateEnum;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.inspection.classenum.QualityInspectionExchangesState;
import com.skyeye.inspection.entity.QualityInspection;
import com.skyeye.inspection.entity.QualityInspectionItem;
import com.skyeye.inspection.service.QualityInspectionService;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.purchase.classenum.OrderArrivalState;
import com.skyeye.purchase.classenum.PurchaseDeliveryFromType;
import com.skyeye.purchase.classenum.PurchaseExchangesFromType;
import com.skyeye.purchase.dao.PurchaseExchangesDao;
import com.skyeye.purchase.entity.PurchaseDelivery;
import com.skyeye.purchase.entity.PurchaseExchange;
import com.skyeye.purchase.entity.PurchaseOrder;
import com.skyeye.purchase.service.*;
import com.skyeye.util.ErpOrderUtil;
import com.skyeye.whole.entity.WholeOrderOut;
import com.skyeye.whole.service.WholeOrderOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "采购换货单", groupName = "采购模块", flowable = true, orderApproval = true)
public class PurchaseExchangesServiceImpl extends SkyeyeErpOrderServiceImpl<PurchaseExchangesDao, PurchaseExchange> implements PurchaseExchangesService {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private WholeOrderOutService wholeOrderOutService;

    @Autowired
    private QualityInspectionService qualityInspectionService;

    @Autowired
    private PurchaseDeliveryService purchaseDeliveryService;

    @Autowired
    private PurchasePutService purchasePutService;

    @Autowired
    private PurchaseReturnsService purchaseReturnsService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置采购订单
        purchaseOrderService.setOrderMationByFromId(beans, "fromId", "fromMation");
        // 设置质检订单
        qualityInspectionService.setQualityInspectionMationByFromId(beans, "fromId", "fromMation");
        // 设置整单委外单
        wholeOrderOutService.setOrderMationByFromId(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public void validatorEntity(PurchaseExchange entity) {
        checkMaterialNorms(entity, false);
    }

    @Override
    public void createPrepose(PurchaseExchange entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.PUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
        setOtherMation(entity);
    }

    @Override
    public void updatePrepose(PurchaseExchange entity) {
        super.updatePrepose(entity);
        setOtherMation(entity);
    }

    private static void setOtherMation(PurchaseExchange entity) {
        // 设置质检类型
        Integer qualityInspection = OrderQualityInspectionType.NOT_NEED_QUALITYINS_INS.getKey();
        for (ErpOrderItem erpOrderItem : entity.getErpOrderItemList()) {
            qualityInspection = setQualityInspection(erpOrderItem, qualityInspection);
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        }
        entity.setQualityInspection(qualityInspection);
    }

    @Override
    public PurchaseExchange selectById(String id) {
        PurchaseExchange purchaseExchange = super.selectById(id);
        if (purchaseExchange.getFromTypeId() == PurchaseExchangesFromType.PURCHASE_ORDER.getKey()) {
            // 采购订单
            purchaseOrderService.setDataMation(purchaseExchange, PurchaseExchange::getFromId);
        } else if (purchaseExchange.getFromTypeId() == PurchaseExchangesFromType.QUALITY_INSPECTION.getKey()) {
            // 质检单
            qualityInspectionService.setDataMation(purchaseExchange, PurchaseExchange::getFromId);
        } else if (purchaseExchange.getFromTypeId() == PurchaseExchangesFromType.WHOLE_ORDER_OUT.getKey()) {
            // 整单委外单
            wholeOrderOutService.setDataMation(purchaseExchange, PurchaseExchange::getFromId);
        }
        purchaseExchange.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setQualityInspectionMation(OrderItemQualityInspectionType.getMation(erpOrderItem.getQualityInspection()));
        });
        return purchaseExchange;
    }

    private void checkMaterialNorms(PurchaseExchange entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前采购换货单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId, ErpOrderItem::getOperNumber));
        // 获取已经下达采购换货单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == PurchaseExchangesFromType.PURCHASE_ORDER.getKey()) {
            // 采购订单
            checkAndUpdatePurchaseOrderState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        } else if (entity.getFromTypeId() == PurchaseExchangesFromType.QUALITY_INSPECTION.getKey()) {
            // 质检单
            checkAndUpdateQualityInspectionPutState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        } else if (entity.getFromTypeId() == PurchaseExchangesFromType.WHOLE_ORDER_OUT.getKey()) {
            // 整单委外单
            checkAndUpdateWholeOrderOutState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        }
    }

    private void checkAndUpdateWholeOrderOutState(PurchaseExchange entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        WholeOrderOut wholeOrderOut = wholeOrderOutService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(wholeOrderOut.getErpOrderItemList())) {
            throw new CustomException("该整单委外单下未包含商品.");
        }
        super.checkFromOrderMaterialNorms(wholeOrderOut.getErpOrderItemList(), inSqlNormsId);
        // 获取已经下达采购入库单的商品信息
        Map<String, String> putExecuteNum = purchasePutService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 获取已经下达采购退货单的商品信息
        Map<String, String> returnExecuteNum = purchaseReturnsService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 来源单据的商品数量 - 当前单据的商品数量 - 已经换货的商品数量 - 已经入库的商品数量 - 已经退货的商品数量
        super.setOrCheckOperNumber(wholeOrderOut.getErpOrderItemList(), setData, orderNormsNum, executeNum, putExecuteNum, returnExecuteNum);
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<ErpOrderItem> erpOrderItemList = wholeOrderOut.getErpOrderItemList().stream()
                .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_ZERO, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 如果该整单委外单的商品(免检)已经全部退货完成，那说明已经完成了整单委外单的入库内容
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                wholeOrderOutService.editStateById(wholeOrderOut.getId(), ErpOrderStateEnum.COMPLETED.getKey());
            } else {
                wholeOrderOutService.editStateById(wholeOrderOut.getId(), ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey());
            }
        }
    }

    private void checkAndUpdateQualityInspectionPutState(PurchaseExchange entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        QualityInspection qualityInspection = qualityInspectionService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(qualityInspection.getQualityInspectionItemList())) {
            throw new CustomException("该质检单下未包含商品.");
        }
        List<String> fromNormsIds = qualityInspection.getQualityInspectionItemList().stream()
            .map(QualityInspectionItem::getNormsId).collect(Collectors.toList());
        super.checkIdFromOrderMaterialNorms(fromNormsIds, inSqlNormsId);
        qualityInspection.getQualityInspectionItemList().forEach(qualityInspectionItem -> {
            // 验收换货的商品数量 - 当前单据的商品数量 - 已经换货的商品数量
            String surplusNum = ErpOrderUtil.checkOperNumber(qualityInspectionItem.getExchangesNumber(),
                qualityInspectionItem.getNormsId(), orderNormsNum, executeNum);
            if (setData) {
                qualityInspectionItem.setOperNumber(surplusNum);
            }
        });
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<QualityInspectionItem> qualityInspectionItemList = qualityInspection.getQualityInspectionItemList().stream()
                .filter(qualityInspectionItem -> CalculationUtil.compareTo(qualityInspectionItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_ZERO, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 如果该质检单的商品已经退货完成，那说明已经完成了质检单的内容
            if (CollectionUtil.isEmpty(qualityInspectionItemList)) {
                qualityInspectionService.editExchangesState(qualityInspection.getId(), QualityInspectionExchangesState.COMPLATE_EXCHANGES.getKey());
            } else {
                qualityInspectionService.editExchangesState(qualityInspection.getId(), QualityInspectionExchangesState.PARTIAL_EXCHANGES.getKey());
            }
        }
    }

    private void checkAndUpdatePurchaseOrderState(PurchaseExchange entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        PurchaseOrder purchaseOrder = purchaseOrderService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(purchaseOrder.getErpOrderItemList())) {
            throw new CustomException("该采购订单下未包含商品.");
        }
        super.checkFromOrderMaterialNorms(purchaseOrder.getErpOrderItemList(), inSqlNormsId);
        // 获取已经下达采购入库单的商品信息
        Map<String, String> putExecuteNum = purchasePutService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 获取已经下达退货单的商品信息

        // 来源单据的商品数量 - 当前单据的商品数量 - 已经换货的商品数量 - 已经入库的商品数量
        super.setOrCheckOperNumber(purchaseOrder.getErpOrderItemList(), setData, orderNormsNum, executeNum, putExecuteNum);
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<ErpOrderItem> erpOrderItemList = purchaseOrder.getErpOrderItemList().stream()
                .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_ZERO, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 如果该采购订单的商品已经退货完成，那说明已经完成了采购订单的内容
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                purchaseOrderService.editStateById(purchaseOrder.getId(), ErpOrderStateEnum.COMPLETED.getKey());
            } else {
                purchaseOrderService.editStateById(purchaseOrder.getId(), ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey());
            }
        }
    }

    @Override
    public void approvalEndIsSuccess(PurchaseExchange entity) {
        entity = selectById(entity.getId());
        // 修改来源单据信息
        checkMaterialNorms(entity, true);
    }

    @Override
    public void queryPurchaseExchangesTransToDeliveryById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        PurchaseExchange purchaseExchange = selectById(id);
        // 该采购换货单下的已经换货到达到货单(审核通过)的数量
        Map<String, String> deliveryNumMap = purchaseDeliveryService.calcMaterialNormsNumByFromId(purchaseExchange.getId());
        // 设置未下达商品数量       采购换货单数量 - 已到货数量
        super.setOrCheckOperNumber(purchaseExchange.getErpOrderItemList(), true, deliveryNumMap);
        // 过滤掉数量为0的商品信息
        purchaseExchange.setErpOrderItemList(purchaseExchange.getErpOrderItemList().stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_ZERO, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(purchaseExchange);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertPurchaseExchangesToDelivery(InputObject inputObject, OutputObject outputObject) {
        PurchaseDelivery purchaseDelivery = inputObject.getParams(PurchaseDelivery.class);
        // 获取采购退货单状态
        PurchaseExchange purchaseExchange = selectById(purchaseDelivery.getId());
        if (ObjectUtil.isEmpty(purchaseExchange)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库出库单
        if (FlowableStateEnum.PASS.getKey().equals(purchaseExchange.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            purchaseDelivery.setFromId(purchaseDelivery.getId());
            purchaseDelivery.setFromTypeId(PurchaseDeliveryFromType.PURCHASE_EXCHANGES.getKey());
            purchaseDelivery.setId(StrUtil.EMPTY);
            purchaseDeliveryService.createEntity(purchaseDelivery, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库出库单.");
        }
    }

    @Override
    public void editArrivalState(String id, Integer arrivalState) {
        UpdateWrapper<PurchaseExchange> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(PurchaseExchange::getOtherState), arrivalState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void editQualityInspection(String id, Integer qualityInspection) {
        UpdateWrapper<PurchaseExchange> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(PurchaseExchange::getQualityInspection), qualityInspection);
        update(updateWrapper);
        refreshCache(id);
    }
}
