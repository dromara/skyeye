/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.purchase.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.classenum.ErpOrderStateEnum;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotOutFromType;
import com.skyeye.depot.classenum.DepotOutState;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.depot.service.DepotOutService;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.inspection.classenum.QualityInspectionReturnState;
import com.skyeye.inspection.entity.QualityInspection;
import com.skyeye.inspection.entity.QualityInspectionItem;
import com.skyeye.inspection.service.QualityInspectionService;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.purchase.classenum.PurchaseReturnsFromType;
import com.skyeye.purchase.dao.PurchaseReturnsDao;
import com.skyeye.purchase.entity.PurchaseOrder;
import com.skyeye.purchase.entity.PurchaseReturn;
import com.skyeye.purchase.service.PurchaseExchangesService;
import com.skyeye.purchase.service.PurchaseOrderService;
import com.skyeye.purchase.service.PurchasePutService;
import com.skyeye.purchase.service.PurchaseReturnsService;
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

/**
 * @ClassName: PurchaseReturnsServiceImpl
 * @Description: 采购退货单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "采购退货单", groupName = "采购模块", flowable = true, orderApproval = true)
public class PurchaseReturnsServiceImpl extends SkyeyeErpOrderServiceImpl<PurchaseReturnsDao, PurchaseReturn> implements PurchaseReturnsService {

    @Autowired
    private QualityInspectionService qualityInspectionService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private PurchasePutService purchasePutService;

    @Autowired
    private WholeOrderOutService wholeOrderOutService;

    @Autowired
    private DepotOutService depotOutService;

    @Autowired
    private PurchaseExchangesService purchaseExchangesService;

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
    public void validatorEntity(PurchaseReturn entity) {
        if (entity.getNeedDepot() == WhetherEnum.ENABLE_USING.getKey()) {
            entity.setOtherState(DepotOutState.NEED_OUT.getKey());
            entity.getErpOrderItemList().forEach(erpOrderItem -> {
                if (StrUtil.isEmpty(erpOrderItem.getDepotId())) {
                    throw new CustomException("请选择出库仓库.");
                }
            });
        } else {
            entity.setOtherState(DepotOutState.NOT_NEED_OUT.getKey());
        }
        checkMaterialNorms(entity, false);
    }

    @Override
    public void createPrepose(PurchaseReturn entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.OUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public PurchaseReturn selectById(String id) {
        PurchaseReturn purchaseReturn = super.selectById(id);
        if (purchaseReturn.getFromTypeId() == PurchaseReturnsFromType.PURCHASE_ORDER.getKey()) {
            // 采购订单
            purchaseOrderService.setDataMation(purchaseReturn, PurchaseReturn::getFromId);
        } else if (purchaseReturn.getFromTypeId() == PurchaseReturnsFromType.QUALITY_INSPECTION.getKey()) {
            // 质检单
            qualityInspectionService.setDataMation(purchaseReturn, PurchaseReturn::getFromId);
        } else if (purchaseReturn.getFromTypeId() == PurchaseReturnsFromType.WHOLE_ORDER_OUT.getKey()) {
            // 整单委外单
            wholeOrderOutService.setDataMation(purchaseReturn, PurchaseReturn::getFromId);
        }
        return purchaseReturn;
    }

    private void checkMaterialNorms(PurchaseReturn entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前采购退货单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId, ErpOrderItem::getOperNumber));
        // 获取已经下达采购退货单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == PurchaseReturnsFromType.PURCHASE_ORDER.getKey()) {
            // 采购订单
            checkAndUpdatePurchaseOrderState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        } else if (entity.getFromTypeId() == PurchaseReturnsFromType.QUALITY_INSPECTION.getKey()) {
            // 质检单
            checkAndUpdateQualityInspectionPutState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        } else if (entity.getFromTypeId() == PurchaseReturnsFromType.WHOLE_ORDER_OUT.getKey()) {
            // 整单委外单
            checkAndUpdateWholeOrderOutState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        }
    }

    private void checkAndUpdateWholeOrderOutState(PurchaseReturn entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        WholeOrderOut wholeOrderOut = wholeOrderOutService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(wholeOrderOut.getErpOrderItemList())) {
            throw new CustomException("该整单委外单下未包含商品.");
        }
        super.checkFromOrderMaterialNorms(wholeOrderOut.getErpOrderItemList(), inSqlNormsId);
        // 获取已经下达采购入库单的商品信息
        Map<String, String> putExecuteNum = purchasePutService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 获取已经下达采购换货单的商品信息
        Map<String, String> exchangeExecuteNum = purchaseExchangesService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 来源单据的商品数量 - 当前单据的商品数量 - 已经退货的商品数量 - 已经入库的商品数量 - 已经换货的商品数量
        super.setOrCheckOperNumber(wholeOrderOut.getErpOrderItemList(), setData, orderNormsNum, executeNum, putExecuteNum, exchangeExecuteNum);
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<ErpOrderItem> erpOrderItemList = wholeOrderOut.getErpOrderItemList().stream()
                .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 如果该整单委外单的商品(免检)已经全部退货完成，那说明已经完成了整单委外单的入库内容
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                wholeOrderOutService.editStateById(wholeOrderOut.getId(), ErpOrderStateEnum.COMPLETED.getKey());
            } else {
                wholeOrderOutService.editStateById(wholeOrderOut.getId(), ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey());
            }
        }
    }

    private void checkAndUpdateQualityInspectionPutState(PurchaseReturn entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        QualityInspection qualityInspection = qualityInspectionService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(qualityInspection.getQualityInspectionItemList())) {
            throw new CustomException("该质检单下未包含商品.");
        }
        List<String> fromNormsIds = qualityInspection.getQualityInspectionItemList().stream()
            .map(QualityInspectionItem::getNormsId).collect(Collectors.toList());
        super.checkIdFromOrderMaterialNorms(fromNormsIds, inSqlNormsId);
        qualityInspection.getQualityInspectionItemList().forEach(qualityInspectionItem -> {
            // 验收退回的商品数量 - 当前单据的商品数量 - 已经退货的商品数量
            String surplusNum = ErpOrderUtil.checkOperNumber(qualityInspectionItem.getReturnNumber(),
                qualityInspectionItem.getNormsId(), orderNormsNum, executeNum);
            if (setData) {
                qualityInspectionItem.setOperNumber(surplusNum);
            }
        });
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<QualityInspectionItem> qualityInspectionItemList = qualityInspection.getQualityInspectionItemList().stream()
                .filter(qualityInspectionItem -> CalculationUtil.compareTo(qualityInspectionItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 如果该质检单的商品已经退货完成，那说明已经完成了质检单的内容
            if (CollectionUtil.isEmpty(qualityInspectionItemList)) {
                qualityInspectionService.editReturnState(qualityInspection.getId(), QualityInspectionReturnState.COMPLATE_RETURN.getKey());
            } else {
                qualityInspectionService.editReturnState(qualityInspection.getId(), QualityInspectionReturnState.PARTIAL_RETURN.getKey());
            }
        }
    }

    private void checkAndUpdatePurchaseOrderState(PurchaseReturn entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        PurchaseOrder purchaseOrder = purchaseOrderService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(purchaseOrder.getErpOrderItemList())) {
            throw new CustomException("该采购订单下未包含商品.");
        }
        super.checkFromOrderMaterialNorms(purchaseOrder.getErpOrderItemList(), inSqlNormsId);
        // 获取已经下达采购入库单的商品信息
        Map<String, String> putExecuteNum = purchasePutService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 获取已经下达采购换货单的商品信息
        Map<String, String> exchangeExecuteNum = purchaseExchangesService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 来源单据的商品数量 - 当前单据的商品数量 - 已经退货的商品数量 - 已经入库的商品数量 - 已经换货的商品数量
        super.setOrCheckOperNumber(purchaseOrder.getErpOrderItemList(), setData, orderNormsNum, executeNum, putExecuteNum, exchangeExecuteNum);
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<ErpOrderItem> erpOrderItemList = purchaseOrder.getErpOrderItemList().stream()
                .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
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
    public void approvalEndIsSuccess(PurchaseReturn entity) {
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
    public void queryPurchaseReturnsTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        PurchaseReturn purchaseReturn = selectById(id);
        if (purchaseReturn.getNeedDepot() == WhetherEnum.DISABLE_USING.getKey()) {
            throw new CustomException("该采购退货单无需进行转出库操作");
        }
        // 该采购退货单下的已经下达仓库出库单(审核通过)的数量
        Map<String, String> depotNumMap = depotOutService.calcMaterialNormsNumByFromId(purchaseReturn.getId());
        // 设置未下达商品数量-----采购退货单数量 - 已出库数量
        super.setOrCheckOperNumber(purchaseReturn.getErpOrderItemList(), true, depotNumMap);
        // 过滤掉数量为0的商品信息
        purchaseReturn.setErpOrderItemList(purchaseReturn.getErpOrderItemList().stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(purchaseReturn);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertPurchaseReturnsToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        DepotOut depotOut = inputObject.getParams(DepotOut.class);
        // 获取采购退货单状态
        PurchaseReturn purchaseReturn = selectById(depotOut.getId());
        if (ObjectUtil.isEmpty(purchaseReturn)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转到仓库出库单
        if (FlowableStateEnum.PASS.getKey().equals(purchaseReturn.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            depotOut.setFromId(depotOut.getId());
            depotOut.setFromTypeId(DepotOutFromType.PURCHASE_RETURNS.getKey());
            depotOut.setId(StrUtil.EMPTY);
            depotOutService.createEntity(depotOut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达仓库出库单.");
        }
    }
}
