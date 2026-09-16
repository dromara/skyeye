/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.purchase.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.classenum.OrderItemQualityInspectionType;
import com.skyeye.business.classenum.OrderQualityInspectionType;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.inspection.classenum.QualityInspectionFromType;
import com.skyeye.inspection.entity.QualityInspection;
import com.skyeye.inspection.service.QualityInspectionService;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.purchase.classenum.*;
import com.skyeye.purchase.dao.PurchaseDeliveryDao;
import com.skyeye.purchase.entity.PurchaseDelivery;
import com.skyeye.purchase.entity.PurchaseExchange;
import com.skyeye.purchase.entity.PurchaseOrder;
import com.skyeye.purchase.entity.PurchasePut;
import com.skyeye.purchase.service.PurchaseDeliveryService;
import com.skyeye.purchase.service.PurchaseExchangesService;
import com.skyeye.purchase.service.PurchaseOrderService;
import com.skyeye.purchase.service.PurchasePutService;
import com.skyeye.whole.entity.WholeOrderOut;
import com.skyeye.whole.service.WholeOrderOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PurchaseDeliveryServiceImpl
 * @Description: 到货单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/21 22:09
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "到货单", groupName = "采购模块", flowable = true, orderApproval = true)
public class PurchaseDeliveryServiceImpl extends SkyeyeErpOrderServiceImpl<PurchaseDeliveryDao, PurchaseDelivery> implements PurchaseDeliveryService {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private QualityInspectionService qualityInspectionService;

    @Autowired
    private PurchasePutService purchasePutService;

    @Autowired
    private WholeOrderOutService wholeOrderOutService;

    @Autowired
    private PurchaseExchangesService purchaseExchangesService;

    @Override
    public QueryWrapper<PurchaseDelivery> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PurchaseDelivery> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PurchaseDelivery::getHolderId), commonPageInfo.getHolderId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getFromId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PurchaseDelivery::getFromId), commonPageInfo.getFromId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置采购订单
        purchaseOrderService.setOrderMationByFromId(beans, "fromId", "fromMation");
        // 设置整单委外单
        wholeOrderOutService.setOrderMationByFromId(beans, "fromId", "fromMation");
        // 设置采购换货单
        purchaseExchangesService.setOrderMationByFromId(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public void validatorEntity(PurchaseDelivery entity) {
        checkMaterialNorms(entity, false);
    }

    @Override
    public void createPrepose(PurchaseDelivery entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.PUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
        setOtherMation(entity);
    }

    @Override
    public void updatePrepose(PurchaseDelivery entity) {
        super.updatePrepose(entity);
        setOtherMation(entity);
    }

    @Override
    public PurchaseDelivery selectById(String id) {
        PurchaseDelivery purchaseDelivery = super.selectById(id);
        if (purchaseDelivery.getFromTypeId() == PurchaseDeliveryFromType.PURCHASE_ORDER.getKey()) {
            // 采购订单
            purchaseOrderService.setDataMation(purchaseDelivery, PurchaseDelivery::getFromId);
        } else if (purchaseDelivery.getFromTypeId() == PurchaseDeliveryFromType.WHOLE_ORDER_OUT.getKey()) {
            // 整单委外单
            wholeOrderOutService.setDataMation(purchaseDelivery, PurchaseDelivery::getFromId);
        } else if (purchaseDelivery.getFromTypeId() == PurchaseDeliveryFromType.PURCHASE_EXCHANGES.getKey()) {
            // 换货单
            purchaseExchangesService.setDataMation(purchaseDelivery, PurchaseDelivery::getFromId);
        }
        purchaseDelivery.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setQualityInspectionMation(OrderItemQualityInspectionType.getMation(erpOrderItem.getQualityInspection()));
        });
        return purchaseDelivery;
    }

    private static void setOtherMation(PurchaseDelivery entity) {
        // 设置质检类型
        Integer qualityInspection = OrderQualityInspectionType.NOT_NEED_QUALITYINS_INS.getKey();
        for (ErpOrderItem erpOrderItem : entity.getErpOrderItemList()) {
            qualityInspection = setQualityInspection(erpOrderItem, qualityInspection);
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        }
        entity.setQualityInspection(qualityInspection);
        // 获取所有免检的商品
        List<ErpOrderItem> erpOrderItemList = entity.getErpOrderItemList().stream()
            .filter(bean -> bean.getQualityInspection() == OrderItemQualityInspectionType.NOT_NEED_QUALITYINS_INS.getKey())
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(erpOrderItemList)) {
            entity.setOtherState(DeliveryPutState.NOT_NEED_PUT.getKey());
        } else {
            entity.setOtherState(DeliveryPutState.NEED_PUT.getKey());
        }

    }

    private void checkMaterialNorms(PurchaseDelivery entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前到货单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId, ErpOrderItem::getOperNumber));
        // 获取已经下达到货单(审批通过)的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == PurchaseDeliveryFromType.PURCHASE_ORDER.getKey()) {
            // 采购订单
            checkAndUpdatePurchaseOrderState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        } else if (entity.getFromTypeId() == PurchaseDeliveryFromType.WHOLE_ORDER_OUT.getKey()) {
            // 整单委外单
            checkAndUpdateWholeOrderOutState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        } else if (entity.getFromTypeId() == PurchaseDeliveryFromType.PURCHASE_EXCHANGES.getKey()) {
            // 采购换货单
            checkAndUpdatePurchaseExchangesState(entity, setData, orderNormsNum, executeNum, inSqlNormsId);
        }
    }

    private void checkAndUpdateWholeOrderOutState(PurchaseDelivery entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        WholeOrderOut wholeOrderOut = wholeOrderOutService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(wholeOrderOut.getErpOrderItemList())) {
            throw new CustomException("该整单委外单下未包含商品.");
        }
        super.checkFromOrderMaterialNorms(wholeOrderOut.getErpOrderItemList(), inSqlNormsId);
        // 来源单据的商品数量 - 当前单据的商品数量 - 已经到货的商品数量
        super.setOrCheckOperNumber(wholeOrderOut.getErpOrderItemList(), setData, orderNormsNum, executeNum);
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<ErpOrderItem> erpOrderItemList = wholeOrderOut.getErpOrderItemList().stream()
                .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_ZERO, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 如果该整单委外单的商品已经全部下达了到货单，那说明已经完成了订单的【到货内容】
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                wholeOrderOutService.editArrivalState(wholeOrderOut.getId(), OrderArrivalState.COMPLATE_ARRIVAL.getKey());
            } else {
                wholeOrderOutService.editArrivalState(wholeOrderOut.getId(), OrderArrivalState.PARTIAL_ARRIVAL.getKey());
            }
        }
    }

    private void checkAndUpdatePurchaseExchangesState(PurchaseDelivery entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId){
        PurchaseExchange purchaseExchange = purchaseExchangesService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(purchaseExchange.getErpOrderItemList())) {
            throw new CustomException("该采购退货单下未包含商品.");
        }
        // 校验传进来的商品信息是否都在来源单中
        super.checkFromOrderMaterialNorms(purchaseExchange.getErpOrderItemList(), inSqlNormsId);
        // 来源单据的商品数量 - 当前单据的商品数量 - 已经到货的商品数量
        super.setOrCheckOperNumber(purchaseExchange.getErpOrderItemList(), setData, orderNormsNum, executeNum);
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<ErpOrderItem> erpOrderItemList = purchaseExchange.getErpOrderItemList().stream()
                    .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_ZERO, RoundingMode.UP) > 0)
                    .collect(Collectors.toList());
            // 如果该整单委外单的商品已经全部下达了到货单，那说明已经完成了订单的【到货内容】
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                purchaseExchangesService.editArrivalState(purchaseExchange.getId(), OrderArrivalState.COMPLATE_ARRIVAL.getKey());
            } else {
                purchaseExchangesService.editArrivalState(purchaseExchange.getId(), OrderArrivalState.PARTIAL_ARRIVAL.getKey());
            }
        }
    }


    private void checkAndUpdatePurchaseOrderState(PurchaseDelivery entity, boolean setData, Map<String, String> orderNormsNum, Map<String, String> executeNum, List<String> inSqlNormsId) {
        PurchaseOrder purchaseOrder = purchaseOrderService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(purchaseOrder.getErpOrderItemList())) {
            throw new CustomException("该采购订单下未包含商品.");
        }
        super.checkFromOrderMaterialNorms(purchaseOrder.getErpOrderItemList(), inSqlNormsId);
        // 获取已经下达采购换货单的商品信息
        Map<String, String> normsExchangeNumMap = purchaseExchangesService.calcMaterialNormsNumByFromId(entity.getFromId());
        // 来源单据的商品数量 - 当前单据的商品数量 - 已经到货的商品数量 - 已经换货的商品数量
        super.setOrCheckOperNumber(purchaseOrder.getErpOrderItemList(), setData, orderNormsNum, executeNum, normsExchangeNumMap);
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<ErpOrderItem> erpOrderItemList = purchaseOrder.getErpOrderItemList().stream()
                .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_ZERO, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 如果该订单的商品已经全部下达了到货单，那说明已经完成了订单的【到货内容】
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                purchaseOrderService.editOtherState(purchaseOrder.getId(), OrderArrivalState.COMPLATE_ARRIVAL.getKey());
            } else {
                purchaseOrderService.editOtherState(purchaseOrder.getId(), OrderArrivalState.PARTIAL_ARRIVAL.getKey());
            }
        }
    }

    @Override
    public void approvalEndIsSuccess(PurchaseDelivery entity) {
        entity = selectById(entity.getId());
        checkMaterialNorms(entity, true);
    }

    @Override
    public void editQualityInspection(String id, Integer qualityInspection) {
        UpdateWrapper<PurchaseDelivery> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(PurchaseDelivery::getQualityInspection), qualityInspection);
        update(updateWrapper);
        refreshCache(id);
        // 设置父节点质检状态
        PurchaseDelivery purchaseDelivery = selectById(id);
        if (StrUtil.isEmpty(purchaseDelivery.getFromId())) {
            return;
        }
        // 获取同一个单据下的所有已经质检的商品数量
        Map<String, String> qualityInspectionNumMap = queryDeliveryQualityNumByParentId(purchaseDelivery.getFromId());
        if (purchaseDelivery.getFromTypeId() == PurchaseDeliveryFromType.PURCHASE_ORDER.getKey()) {
            // 来源-采购订单
            PurchaseOrder purchaseOrder = purchaseOrderService.selectById(purchaseDelivery.getFromId());
            // 过滤掉【采购订单】中免检的商品
            List<ErpOrderItem> erpOrderItemList = purchaseOrder.getErpOrderItemList().stream()
                .filter(bean -> bean.getQualityInspection() != OrderItemQualityInspectionType.NOT_NEED_QUALITYINS_INS.getKey())
                .collect(Collectors.toList());
            erpOrderItemList.forEach(erpOrderItem -> {
                String qualityNum = qualityInspectionNumMap.containsKey(erpOrderItem.getNormsId()) 
                    ? qualityInspectionNumMap.get(erpOrderItem.getNormsId()) 
                    : CommonNumConstants.NUM_ZERO.toString();
                String surplusNum = CalculationUtil.subtract(erpOrderItem.getOperNumber(), qualityNum, ErpConstants.NUM_AFTER_DOT);
                erpOrderItem.setOperNumber(surplusNum);
            });
            // 过滤掉剩余数量为0的商品
            erpOrderItemList = erpOrderItemList.stream()
                .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_ZERO, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 该采购订单的商品已经全部进行了质检
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                purchaseOrderService.editQualityInspection(purchaseDelivery.getFromId(), OrderQualityInspectionType.COMPLATE_QUALITY_INSPECTION.getKey());
            } else {
                purchaseOrderService.editQualityInspection(purchaseDelivery.getFromId(), OrderQualityInspectionType.PARTIAL_QUALITY_INSPECTION.getKey());
            }
        } else if (purchaseDelivery.getFromTypeId() == PurchaseDeliveryFromType.WHOLE_ORDER_OUT.getKey()) {
            // 整单委外单
            WholeOrderOut wholeOrderOut = wholeOrderOutService.selectById(purchaseDelivery.getFromId());
            // 过滤掉【整单委外单】中免检的商品
            List<ErpOrderItem> erpOrderItemList = wholeOrderOut.getErpOrderItemList().stream()
                .filter(bean -> bean.getQualityInspection() != OrderItemQualityInspectionType.NOT_NEED_QUALITYINS_INS.getKey())
                .collect(Collectors.toList());
            erpOrderItemList.forEach(erpOrderItem -> {
                String qualityNum = qualityInspectionNumMap.containsKey(erpOrderItem.getNormsId()) 
                    ? qualityInspectionNumMap.get(erpOrderItem.getNormsId()) 
                    : CommonNumConstants.NUM_ZERO.toString();
                String surplusNum = CalculationUtil.subtract(erpOrderItem.getOperNumber(), qualityNum,ErpConstants.NUM_AFTER_DOT);
                erpOrderItem.setOperNumber(surplusNum);
            });
            // 过滤掉剩余数量为0的商品
            erpOrderItemList = erpOrderItemList.stream()
                .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_ZERO, RoundingMode.UP) > 0)
                .collect(Collectors.toList());
            // 该整单委外单的商品已经全部进行了质检
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                wholeOrderOutService.editQualityInspection(purchaseDelivery.getFromId(), OrderQualityInspectionType.COMPLATE_QUALITY_INSPECTION.getKey());
            } else {
                wholeOrderOutService.editQualityInspection(purchaseDelivery.getFromId(), OrderQualityInspectionType.PARTIAL_QUALITY_INSPECTION.getKey());
            }
        }
    }

    /**
     * 获取采购订单下所有已经质检的商品
     *
     * @param parentId 采购订单id
     * @return
     */
    private Map<String, String> queryDeliveryQualityNumByParentId(String parentId) {
        QueryWrapper<PurchaseDelivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PurchaseDelivery::getFromId), parentId);
        // 只查询审批通过 && 已经质检(部分质检/质检完成) 的到货单
        List<String> stateList = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey()});
        queryWrapper.in(MybatisPlusUtil.toColumns(PurchaseDelivery::getState), stateList);
        List<Integer> qualityInspectionList = Arrays.asList(new Integer[]{OrderQualityInspectionType.PARTIAL_QUALITY_INSPECTION.getKey(),
            OrderQualityInspectionType.COMPLATE_QUALITY_INSPECTION.getKey()});
        queryWrapper.in(MybatisPlusUtil.toColumns(PurchaseDelivery::getQualityInspection), qualityInspectionList);
        List<PurchaseDelivery> purchaseOrderList = list(queryWrapper);
        // 获取所有到货单id
        List<String> purchaseDeliveryIdList = purchaseOrderList.stream().map(PurchaseDelivery::getId).collect(Collectors.toList());
        // 获取到货单下所有已经质检的商品数量
        return qualityInspectionService.calcMaterialNormsNumByFromId(purchaseDeliveryIdList.toArray(new String[]{}));
    }

    @Override
    public void queryPurchaseDeliveryTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        PurchaseDelivery purchaseDelivery = selectById(id);
        Map<String, String> normsNum = qualityInspectionService.calcMaterialNormsNumByFromId(id);
        // 查询被转到换货单的商品
        Map<String, String> normsNumMap = purchaseExchangesService.calcMaterialNormsNumByFromId(id);
        // 过滤掉【采购到货单】中免检的商品
        List<ErpOrderItem> erpOrderItemList = purchaseDelivery.getErpOrderItemList().stream()
            .filter(bean -> bean.getQualityInspection() != OrderItemQualityInspectionType.NOT_NEED_QUALITYINS_INS.getKey())
            .collect(Collectors.toList());
        super.setOrCheckOperNumber(erpOrderItemList, true, normsNum, normsNumMap);
        // 过滤掉数量为0的进行生成质检单
        purchaseDelivery.setErpOrderItemList(erpOrderItemList.stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_ZERO, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(purchaseDelivery);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void deliveryToQualityInspection(InputObject inputObject, OutputObject outputObject) {
        QualityInspection qualityInspection = inputObject.getParams(QualityInspection.class);
        // 获取到货单状态
        PurchaseDelivery purchaseDelivery = selectById(qualityInspection.getId());
        if (ObjectUtil.isEmpty(purchaseDelivery)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的并且需要质检的可以进行质检单
        if (FlowableStateEnum.PASS.getKey().equals(purchaseDelivery.getState()) &&
            (purchaseDelivery.getQualityInspection() == OrderQualityInspectionType.NEED_QUALITYINS_INS.getKey()
                || purchaseDelivery.getQualityInspection() == OrderQualityInspectionType.PARTIAL_QUALITY_INSPECTION.getKey())) {
            String userId = inputObject.getLogParams().get("id").toString();
            qualityInspection.setFromId(qualityInspection.getId());
            qualityInspection.setFromTypeId(QualityInspectionFromType.PURCHASE_DELIVERY.getKey());
            qualityInspection.setId(StrUtil.EMPTY);
            qualityInspectionService.createEntity(qualityInspection, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达质检单.");
        }
    }

    @Override
    public void queryPurchaseDeliveryTransPurchasePutById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        PurchaseDelivery purchaseDelivery = selectById(id);
        Map<String, String> normsNum = purchasePutService.calcMaterialNormsNumByFromId(id);
        // 查询被转到换货单的商品
        Map<String, String> normsNumMap = purchaseExchangesService.calcMaterialNormsNumByFromId(id);
        // 获取【采购到货单】中免检的商品
        List<ErpOrderItem> erpOrderItemList = purchaseDelivery.getErpOrderItemList().stream()
            .filter(bean -> bean.getQualityInspection() == OrderItemQualityInspectionType.NOT_NEED_QUALITYINS_INS.getKey())
            .collect(Collectors.toList());
        super.setOrCheckOperNumber(erpOrderItemList, true, normsNum, normsNumMap);
        // 过滤掉数量为0的进行生成采购入库单
        purchaseDelivery.setErpOrderItemList(erpOrderItemList.stream()
            .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), CommonNumConstants.NUM_ZERO, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(purchaseDelivery);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void deliveryToPurchasePut(InputObject inputObject, OutputObject outputObject) {
        PurchasePut purchasePut = inputObject.getParams(PurchasePut.class);
        // 获取到货单状态
        PurchaseDelivery purchaseDelivery = selectById(purchasePut.getId());
        if (ObjectUtil.isEmpty(purchaseDelivery)) {
            throw new CustomException("该数据不存在.");
        }
        // 【审核通过】的并且【免检商品入库状态为待入库，部分入库】的可以进行采购入库
        if (FlowableStateEnum.PASS.getKey().equals(purchaseDelivery.getState()) &&
            (purchaseDelivery.getOtherState() == DeliveryPutState.NEED_PUT.getKey()
                || purchaseDelivery.getOtherState() == DeliveryPutState.PARTIAL_PUT.getKey())) {
            String userId = inputObject.getLogParams().get("id").toString();
            purchasePut.setFromId(purchasePut.getId());
            purchasePut.setFromTypeId(PurchasePutFromType.PURCHASE_DELIVERY.getKey());
            purchasePut.setId(StrUtil.EMPTY);
            purchasePutService.createEntity(purchasePut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法转采购入库单.");
        }
    }

}
