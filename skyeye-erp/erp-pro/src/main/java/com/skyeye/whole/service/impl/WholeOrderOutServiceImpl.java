/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.whole.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.classenum.OrderItemQualityInspectionType;
import com.skyeye.business.classenum.OrderQualityInspectionType;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.classenum.ErpOrderStateEnum;
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
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.production.classenum.ProductionChildType;
import com.skyeye.production.classenum.ProductionOutState;
import com.skyeye.production.entity.Production;
import com.skyeye.production.entity.ProductionChild;
import com.skyeye.production.service.ProductionService;
import com.skyeye.purchase.classenum.*;
import com.skyeye.purchase.entity.PurchaseDelivery;
import com.skyeye.purchase.entity.PurchaseExchange;
import com.skyeye.purchase.entity.PurchasePut;
import com.skyeye.purchase.entity.PurchaseReturn;
import com.skyeye.purchase.service.PurchaseDeliveryService;
import com.skyeye.purchase.service.PurchaseExchangesService;
import com.skyeye.purchase.service.PurchasePutService;
import com.skyeye.purchase.service.PurchaseReturnsService;
import com.skyeye.util.ErpOrderUtil;
import com.skyeye.whole.classenum.WholeOrderOutFromType;
import com.skyeye.whole.dao.WholeOrderOutDao;
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
 * @ClassName: WholeOrderOutServiceImpl
 * @Description: 整单委外单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/22 20:36
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "整单委外单", groupName = "整单委外单", flowable = true)
public class WholeOrderOutServiceImpl extends SkyeyeErpOrderServiceImpl<WholeOrderOutDao, WholeOrderOut> implements WholeOrderOutService {

    @Autowired
    private ProductionService productionService;

    @Autowired
    private PurchasePutService purchasePutService;

    @Autowired
    private PurchaseDeliveryService purchaseDeliveryService;

    @Autowired
    private PurchaseReturnsService purchaseReturnsService;

    @Autowired
    private PurchaseExchangesService purchaseExchangesService;

    @Autowired
    private IDepmentService iDepmentService;

    @Override
    public QueryWrapper<WholeOrderOut> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<WholeOrderOut> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(WholeOrderOut::getHolderId), commonPageInfo.getHolderId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getFromId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(WholeOrderOut::getFromId), commonPageInfo.getFromId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 生产计划单
        productionService.setOrderMationByFromId(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public void validatorEntity(WholeOrderOut entity) {
        if (StrUtil.isEmpty(entity.getHolderId())) {
            throw new CustomException("请选择供应商.");
        }
        checkMaterialNorms(entity, false);
    }

    @Override
    public void createPrepose(WholeOrderOut entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.PUT.getKey());
        setOtherMation(entity);
    }

    @Override
    public void updatePrepose(WholeOrderOut entity) {
        super.updatePrepose(entity);
        setOtherMation(entity);
    }

    @Override
    public void approvalEndIsSuccess(WholeOrderOut entity) {
        entity = selectById(entity.getId());
        // 修改来源单据的状态信息
        checkMaterialNorms(entity, true);
    }

    @Override
    public WholeOrderOut selectById(String id) {
        WholeOrderOut wholeOrderOut = super.selectById(id);
        if (wholeOrderOut.getFromTypeId() == WholeOrderOutFromType.PRODUCTION.getKey()) {
            productionService.setDataMation(wholeOrderOut, WholeOrderOut::getFromId);
        }
        wholeOrderOut.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setQualityInspectionMation(OrderItemQualityInspectionType.getMation(erpOrderItem.getQualityInspection()));
        });
        // 部门
        iDepmentService.setDataMation(wholeOrderOut, WholeOrderOut::getDepartmentId);
        return wholeOrderOut;
    }

    private static void setOtherMation(WholeOrderOut entity) {
        // 设置质检类型
        Integer qualityInspection = OrderQualityInspectionType.NOT_NEED_QUALITYINS_INS.getKey();
        for (ErpOrderItem erpOrderItem : entity.getErpOrderItemList()) {
            qualityInspection = setQualityInspection(erpOrderItem, qualityInspection);
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        }
        entity.setQualityInspection(qualityInspection);
        // 设置到货状态
        if (qualityInspection == OrderQualityInspectionType.NEED_QUALITYINS_INS.getKey()) {
            // 如果需要质检，则需要先下【到货单】
            entity.setOtherState(OrderArrivalState.NEED_ARRIVAL.getKey());
        } else {
            entity.setOtherState(OrderArrivalState.NOT_NEED_ARRIVAL.getKey());
        }
    }

    private void checkMaterialNorms(WholeOrderOut entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前整单委外单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId,
                item -> StrUtil.isEmpty(item.getOperNumber()) ? CommonNumConstants.NUM_ZERO.toString() : item.getOperNumber()));
        // 获取已经下达整单委外单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == WholeOrderOutFromType.PRODUCTION.getKey()) {
            Production production = productionService.selectById(entity.getFromId());
            // 获取需要【委外】的商品
            List<ProductionChild> productionChildList = production.getProductionChildList().stream()
                .filter(bean -> bean.getProductionType() == ProductionChildType.OUTSOURCING.getKey()).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(productionChildList)) {
                throw new CustomException("该生产计划单下未包含需要委外的商品.");
            }
            List<String> fromNormsIds = productionChildList.stream()
                .map(ProductionChild::getNormsId).collect(Collectors.toList());
            super.checkIdFromOrderMaterialNorms(fromNormsIds, inSqlNormsId);
            productionChildList.forEach(productionChild -> {
                // 生产计划单数量 - 当前订单数量 - 已经下达整单委外单数量
                String productionChildOperNumber = StrUtil.isEmpty(productionChild.getOperNumber())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : productionChild.getOperNumber();
                String surplusNum = ErpOrderUtil.checkOperNumber(productionChildOperNumber, productionChild.getNormsId(), orderNormsNum, executeNum);
                if (setData) {
                    productionChild.setOperNumber(surplusNum);
                }
            });
            if (setData) {
                // 过滤掉剩余数量为0的商品
                productionChildList = productionChildList.stream()
                    .filter(productionChild -> {
                        String operNumber = StrUtil.isEmpty(productionChild.getOperNumber())
                            ? CommonNumConstants.NUM_ZERO.toString()
                            : productionChild.getOperNumber();
                        return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
                    }).collect(Collectors.toList());
                // 如果该生产计划单的商品已经全部下达整单委外单，那说明已经完成了生产计划单的内容
                if (CollectionUtil.isEmpty(productionChildList)) {
                    productionService.editOutState(production.getId(), ProductionOutState.COMPLATE_OUT.getKey());
                } else {
                    productionService.editOutState(production.getId(), ProductionOutState.PARTIAL_OUT.getKey());
                }
            }
        }
    }

    @Override
    public void editArrivalState(String id, Integer arrivalState) {
        UpdateWrapper<WholeOrderOut> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(WholeOrderOut::getOtherState), arrivalState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void editQualityInspection(String id, Integer qualityInspection) {
        UpdateWrapper<WholeOrderOut> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(WholeOrderOut::getQualityInspection), qualityInspection);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void queryWholeOrderOutTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        WholeOrderOut wholeOrderOut = selectById(id);
        // 该整单委外单下的已经下达采购退货单(审核通过)的数量
        Map<String, String> normsReturnMap = purchaseReturnsService.calcMaterialNormsNumByFromId(wholeOrderOut.getId());
        // 该整单委外单下的已经下达采购换货单(审核通过)的数量
        Map<String, String> normsExchangeMap = purchaseExchangesService.calcMaterialNormsNumByFromId(wholeOrderOut.getId());
        if (wholeOrderOut.getQualityInspection() == OrderQualityInspectionType.NEED_QUALITYINS_INS.getKey()) {
            // 需要质检，计算未到货数量
            Map<String, String> normsNum = purchaseDeliveryService.calcMaterialNormsNumByFromId(id);
            wholeOrderOut.getErpOrderItemList().forEach(erpOrderItem -> {
                // 整单委外单数量 - 已到货数量 - 已退货数量 - 已换货数量
                String erpOrderItemOperNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : erpOrderItem.getOperNumber();
                String surplusNum = ErpOrderUtil.checkOperNumber(erpOrderItemOperNumber, erpOrderItem.getNormsId(), normsNum, normsReturnMap, normsExchangeMap);
                // 设置未下达到货单的商品数量
                erpOrderItem.setOperNumber(surplusNum);
            });
        } else {
            // 免检，计算未入库的数量
            Map<String, String> normsNum = purchasePutService.calcMaterialNormsNumByFromId(id);
            wholeOrderOut.getErpOrderItemList().forEach(erpOrderItem -> {
                // 整单委外单数量 - 已入库数量 - 已退货数量 - 已换货数量
                String erpOrderItemOperNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : erpOrderItem.getOperNumber();
                String surplusNum = ErpOrderUtil.checkOperNumber(erpOrderItemOperNumber, erpOrderItem.getNormsId(), normsNum, normsReturnMap, normsExchangeMap);
                // 设置未下达采购入库单的商品数量
                erpOrderItem.setOperNumber(surplusNum);
            });
        }
        // 过滤掉数量为0的进行生成采购入库单/到货单/退货单
        wholeOrderOut.setErpOrderItemList(wholeOrderOut.getErpOrderItemList().stream()
            .filter(erpOrderItem -> {
                String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : erpOrderItem.getOperNumber();
                return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
            }).collect(Collectors.toList()));
        outputObject.setBean(wholeOrderOut);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertWholeOrderOutToTurnPut(InputObject inputObject, OutputObject outputObject) {
        PurchasePut purchasePut = inputObject.getParams(PurchasePut.class);
        // 获取整单委外单状态
        WholeOrderOut order = selectById(purchasePut.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过/部分完成的可以进行入库
        if (FlowableStateEnum.PASS.getKey().equals(order.getState()) || ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey().equals(order.getState())) {
            if (order.getQualityInspection() == OrderQualityInspectionType.NEED_QUALITYINS_INS.getKey()) {
                throw new CustomException("该订单需要进行质检，无法直接转采购入库，请先转【到货单】.");
            }
            String userId = inputObject.getLogParams().get("id").toString();
            purchasePut.setFromId(purchasePut.getId());
            purchasePut.setFromTypeId(PurchasePutFromType.WHOLE_ORDER_OUT.getKey());
            purchasePut.setId(StrUtil.EMPTY);
            purchasePutService.createEntity(purchasePut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达采购入库单.");
        }
    }

    @Override
    public void insertWholeOrderOutToTurnDelivery(InputObject inputObject, OutputObject outputObject) {
        PurchaseDelivery purchaseDelivery = inputObject.getParams(PurchaseDelivery.class);
        // 获取整单委外单状态
        WholeOrderOut order = selectById(purchaseDelivery.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过/部分完成的可以转到货单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState()) || ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey().equals(order.getState())) {
            if (order.getQualityInspection() == OrderQualityInspectionType.NOT_NEED_QUALITYINS_INS.getKey()) {
                throw new CustomException("该订单无需进行质检，请直接转采购入库.");
            }
            String userId = inputObject.getLogParams().get("id").toString();
            purchaseDelivery.setFromId(purchaseDelivery.getId());
            purchaseDelivery.setFromTypeId(PurchaseDeliveryFromType.WHOLE_ORDER_OUT.getKey());
            purchaseDelivery.setId(StrUtil.EMPTY);
            purchaseDeliveryService.createEntity(purchaseDelivery, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达到货单.");
        }
    }

    @Override
    public void insertWholeOrderOutToReturns(InputObject inputObject, OutputObject outputObject) {
        PurchaseReturn purchaseReturn = inputObject.getParams(PurchaseReturn.class);
        // 获取整单委外单状态
        WholeOrderOut order = selectById(purchaseReturn.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过/部分完成的可以转到采购退货单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState()) || ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            purchaseReturn.setFromId(purchaseReturn.getId());
            purchaseReturn.setFromTypeId(PurchaseReturnsFromType.WHOLE_ORDER_OUT.getKey());
            purchaseReturn.setId(StrUtil.EMPTY);
            purchaseReturnsService.createEntity(purchaseReturn, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达采购退货单.");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertWholeOrderOutToExchanges(InputObject inputObject, OutputObject outputObject) {
        PurchaseExchange purchaseExchange = inputObject.getParams(PurchaseExchange.class);
        // 获取整单委外单状态
        WholeOrderOut order = selectById(purchaseExchange.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过/部分完成的可以转到采购换货单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState()) || ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            purchaseExchange.setFromId(purchaseExchange.getId());
            purchaseExchange.setFromTypeId(PurchaseExchangesFromType.WHOLE_ORDER_OUT.getKey());
            purchaseExchange.setId(StrUtil.EMPTY);
            purchaseExchangesService.createEntity(purchaseExchange, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达采购换货单.");
        }
    }
}
