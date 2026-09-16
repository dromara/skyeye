/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.purchase.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.classenum.OrderItemQualityInspectionType;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.classenum.ErpOrderStateEnum;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.classenum.DepotPutState;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.depot.service.DepotPutService;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.inspection.classenum.QualityInspectionPutState;
import com.skyeye.inspection.entity.QualityInspection;
import com.skyeye.inspection.entity.QualityInspectionItem;
import com.skyeye.inspection.service.QualityInspectionService;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.purchase.classenum.DeliveryPutState;
import com.skyeye.purchase.classenum.PurchasePutFromType;
import com.skyeye.purchase.dao.PurchasePutDao;
import com.skyeye.purchase.entity.PurchaseDelivery;
import com.skyeye.purchase.entity.PurchaseOrder;
import com.skyeye.purchase.entity.PurchasePut;
import com.skyeye.purchase.service.*;
import com.skyeye.util.ErpOrderUtil;
import com.skyeye.whole.entity.WholeOrderOut;
import com.skyeye.whole.service.WholeOrderOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PurchasePutServiceImpl
 * @Description: 采购入库单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:12
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "采购入库单", groupName = "采购模块", flowable = true, orderApproval = true)
public class PurchasePutServiceImpl extends SkyeyeErpOrderServiceImpl<PurchasePutDao, PurchasePut> implements PurchasePutService {

    @Autowired
    private QualityInspectionService qualityInspectionService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private PurchaseReturnsService purchaseReturnsService;

    @Autowired
    private PurchaseDeliveryService purchaseDeliveryService;

    @Autowired
    private WholeOrderOutService wholeOrderOutService;

    @Autowired
    private DepotPutService depotPutService;

    @Autowired
    private PurchaseExchangesService purchaseExchangesService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置采购订单
        purchaseOrderService.setOrderMationByFromId(beans, "fromId", "fromMation");
        // 设置质检订单
        qualityInspectionService.setQualityInspectionMationByFromId(beans, "fromId", "fromMation");
        // 设置到货单
        purchaseDeliveryService.setOrderMationByFromId(beans, "fromId", "fromMation");
        // 设置整单委外单
        wholeOrderOutService.setOrderMationByFromId(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public void validatorEntity(PurchasePut entity) {
        entity.setOtherState(DepotPutState.NEED_PUT.getKey());
        checkMaterialNorms(entity, false);
    }

    @Override
    public void createPrepose(PurchasePut entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.PUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public PurchasePut selectById(String id) {
        PurchasePut purchasePut = super.selectById(id);
        if (purchasePut.getFromTypeId() == PurchasePutFromType.PURCHASE_ORDER.getKey()) {
            // 采购订单
            purchaseOrderService.setDataMation(purchasePut, PurchasePut::getFromId);
        } else if (purchasePut.getFromTypeId() == PurchasePutFromType.QUALITY_INSPECTION.getKey()) {
            // 质检单
            qualityInspectionService.setDataMation(purchasePut, PurchasePut::getFromId);
        } else if (purchasePut.getFromTypeId() == PurchasePutFromType.PURCHASE_DELIVERY.getKey()) {
            // 到货单
            purchaseDeliveryService.setDataMation(purchasePut, PurchasePut::getFromId);
        } else if (purchasePut.getFromTypeId() == PurchasePutFromType.WHOLE_ORDER_OUT.getKey()) {
            // 整单委外单
            wholeOrderOutService.setDataMation(purchasePut, PurchasePut::getFromId);
        }

        return purchasePut;
    }

    private void checkMaterialNorms(PurchasePut entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前采购入库单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId, ErpOrderItem::getOperNumber));
        // 获取已经下达采购入库单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == PurchasePutFromType.PURCHASE_ORDER.getKey()) {
            // 采购订单
            checkAndUpdatePurchaseOrderState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        } else if (entity.getFromTypeId() == PurchasePutFromType.QUALITY_INSPECTION.getKey()) {
            // 质检单
            checkAndUpdateQualityInspectionPutState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        } else if (entity.getFromTypeId() == PurchasePutFromType.PURCHASE_DELIVERY.getKey()) {
            // 到货单
            checkAndUpdatePurchaseDeliveryPutState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        } else if (entity.getFromTypeId() == PurchasePutFromType.WHOLE_ORDER_OUT.getKey()) {
            // 整单委外单
            checkAndUpdateWholeOrderOutState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        }
    }

    private void checkAndUpdateWholeOrderOutState(PurchasePut entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        WholeOrderOut wholeOrderOut = wholeOrderOutService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(wholeOrderOut.getErpOrderItemList())) {
            throw new CustomException("该整单委外单下未包含商品.");
        }
        // 获取所有免检的商品进行采购入库
        List<ErpOrderItem> erpOrderItemList = wholeOrderOut.getErpOrderItemList().stream()
            .filter(bean -> bean.getQualityInspection() == OrderItemQualityInspectionType.NOT_NEED_QUALITYINS_INS.getKey())
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(erpOrderItemList)) {
            throw new CustomException("该整单委外单未包含需要免检的商品，请走质检流程");
        }
        super.checkFromOrderMaterialNorms(erpOrderItemList, inSqlNormsId);
        // 获取已经下达采购退货单的商品信息
        Map<String, String> returnExecuteNum = purchaseReturnsService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 获取已经下达采购换货单的商品信息
        Map<String, String> exchangeExecuteNum = purchaseExchangesService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 来源单据的商品数量 - 当前单据的商品数量 - 已经入库的商品数量 - 已经退货的商品数量 - 已经换货的商品数量
        super.setOrCheckOperNumber(erpOrderItemList, setData, orderNormsNum, executeNum, returnExecuteNum, exchangeExecuteNum);
        if (setData) {
            // 过滤掉剩余数量为0的商品
            erpOrderItemList = erpOrderItemList.stream()
                .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 如果该整单委外单的商品(免检)已经全部生成了采购入库单，那说明已经完成了整单委外单的入库内容
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                wholeOrderOutService.editStateById(wholeOrderOut.getId(), ErpOrderStateEnum.COMPLETED.getKey());
            } else {
                wholeOrderOutService.editStateById(wholeOrderOut.getId(), ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey());
            }
        }
    }

    private void checkAndUpdatePurchaseDeliveryPutState(PurchasePut entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        PurchaseDelivery purchaseDelivery = purchaseDeliveryService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(purchaseDelivery.getErpOrderItemList())) {
            throw new CustomException("该到货单下未包含商品.");
        }
        // 获取所有免检的商品进行采购入库
        List<ErpOrderItem> erpOrderItemList = purchaseDelivery.getErpOrderItemList().stream()
            .filter(bean -> bean.getQualityInspection() == OrderItemQualityInspectionType.NOT_NEED_QUALITYINS_INS.getKey())
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(erpOrderItemList)) {
            throw new CustomException("该到货单下未包含需要免检的商品，请走质检流程");
        }
        super.checkFromOrderMaterialNorms(erpOrderItemList, inSqlNormsId);
        // 来源单据的商品数量 - 当前单据的商品数量 - 已经入库的商品数量
        super.setOrCheckOperNumber(erpOrderItemList, setData, orderNormsNum, executeNum);
        if (setData) {
            // 过滤掉剩余数量为0的商品
            erpOrderItemList = erpOrderItemList.stream()
                .filter(qualityInspectionItem -> CalculationUtil.compareTo(qualityInspectionItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 如果该到货单的商品(免检)已经全部生成了采购入库单，那说明已经完成了到货单的入库内容
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                purchaseDeliveryService.editOtherState(purchaseDelivery.getId(), DeliveryPutState.COMPLATE_PUT.getKey());
            } else {
                purchaseDeliveryService.editOtherState(purchaseDelivery.getId(), DeliveryPutState.PARTIAL_PUT.getKey());
            }
        }
    }

    private void checkAndUpdateQualityInspectionPutState(PurchasePut entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        QualityInspection qualityInspection = qualityInspectionService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(qualityInspection.getQualityInspectionItemList())) {
            throw new CustomException("该质检单下未包含商品.");
        }
        List<String> fromNormsIds = qualityInspection.getQualityInspectionItemList().stream()
            .map(QualityInspectionItem::getNormsId).collect(Collectors.toList());
        super.checkIdFromOrderMaterialNorms(fromNormsIds, inSqlNormsId);
        qualityInspection.getQualityInspectionItemList().forEach(qualityInspectionItem -> {
            // 合格数量 + 让步接收数量 - 当前订单数量 - 采购入库单的数量
            String qualifiedAndConcession = CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, qualityInspectionItem.getQualifiedNumber(), qualityInspectionItem.getConcessionNumber());
            String surplusNum = ErpOrderUtil.checkOperNumber(qualifiedAndConcession, qualityInspectionItem.getNormsId(), orderNormsNum, executeNum);
            if (setData) {
                qualityInspectionItem.setOperNumber(surplusNum);
            }
        });
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<QualityInspectionItem> qualityInspectionItemList = qualityInspection.getQualityInspectionItemList().stream()
                .filter(qualityInspectionItem -> CalculationUtil.compareTo(qualityInspectionItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 如果该质检单的商品已经全部生成了采购入库单，那说明已经完成了质检单的内容
            if (CollectionUtil.isEmpty(qualityInspectionItemList)) {
                qualityInspectionService.editPutState(qualityInspection.getId(), QualityInspectionPutState.COMPLATE_PUT.getKey());
            } else {
                qualityInspectionService.editPutState(qualityInspection.getId(), QualityInspectionPutState.PARTIAL_PUT.getKey());
            }
        }
    }

    private void checkAndUpdatePurchaseOrderState(PurchasePut entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        PurchaseOrder purchaseOrder = purchaseOrderService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(purchaseOrder.getErpOrderItemList())) {
            throw new CustomException("该采购订单下未包含商品.");
        }
        super.checkFromOrderMaterialNorms(purchaseOrder.getErpOrderItemList(), inSqlNormsId);
        // 获取已经下达采购退货单的商品信息
        Map<String, String> returnExecuteNum = purchaseReturnsService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 来源单据的商品数量 - 当前单据的商品数量 - 已经入库的商品数量 - 已经退货的商品数量
        super.setOrCheckOperNumber(purchaseOrder.getErpOrderItemList(), setData, orderNormsNum, executeNum, returnExecuteNum);
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<ErpOrderItem> erpOrderItemList = purchaseOrder.getErpOrderItemList().stream()
                .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 如果该采购订单的商品已经全部生成了采购入库单，那说明已经完成了采购订单的内容
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                purchaseOrderService.editStateById(purchaseOrder.getId(), ErpOrderStateEnum.COMPLETED.getKey());
            } else {
                purchaseOrderService.editStateById(purchaseOrder.getId(), ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey());
            }
        }
    }

    @Override
    public void approvalEndIsSuccess(PurchasePut entity) {
        entity = selectById(entity.getId());
        // 修改来源单据信息
        checkMaterialNorms(entity, true);
        // 减少在途库存
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpCommonService.editMaterialNormsDepotStock(MaterialNormsStockType.IN_TRANSIT_STOCK.getDefaultDepotId(), erpOrderItem.getMaterialId(),
                erpOrderItem.getNormsId(), erpOrderItem.getOperNumber(), DepotPutOutType.OUT.getKey(), MaterialNormsStockType.IN_TRANSIT_STOCK.getKey());
        });
    }

    @Override
    public void queryPurchasePutTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        PurchasePut purchasePut = selectById(id);
        // 该采购入库单下的已经下达仓库入库单(审核通过)的数量
        Map<String, String> depotNumMap = depotPutService.calcMaterialNormsNumByFromId(purchasePut.getId());
        // 设置未下达商品数量-----采购入库单数量 - 已入库数量
        super.setOrCheckOperNumber(purchasePut.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        purchasePut.setErpOrderItemList(purchasePut.getErpOrderItemList().stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(purchasePut);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertPurchasePutToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotPut depotPut = inputObject.getParams(DepotPut.class);
        // 获取采购入库单状态
        PurchasePut purchasePut = selectById(depotPut.getId());
        if (ObjectUtil.isEmpty(purchasePut)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库入库单
        if (FlowableStateEnum.PASS.getKey().equals(purchasePut.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotPut.setFromId(depotPut.getId());
            depotPut.setFromTypeId(DepotPutFromType.PURCHASE_PUT.getKey());
            depotPut.setId(StrUtil.EMPTY);
            depotPutService.createEntity(depotPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库入库单.");
        }
    }
}
