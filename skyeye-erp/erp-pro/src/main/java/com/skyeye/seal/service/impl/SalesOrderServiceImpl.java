/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.seal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.classenum.ErpOrderStateEnum;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.crm.service.IContractService;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialFromType;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.material.entity.Material;
import com.skyeye.production.classenum.ProductionPlanFromType;
import com.skyeye.production.entity.ProductionPlan;
import com.skyeye.production.service.ProductionPlanService;
import com.skyeye.purchase.classenum.PurchaseOrderFromType;
import com.skyeye.purchase.entity.PurchaseOrder;
import com.skyeye.purchase.service.PurchaseOrderService;
import com.skyeye.seal.classenum.SalesExchangesFromType;
import com.skyeye.seal.classenum.SalesOrderPurchaseState;
import com.skyeye.seal.classenum.SealOrderFromType;
import com.skyeye.seal.classenum.SealOutLetFromType;
import com.skyeye.seal.classenum.SealReturnFromType;
import com.skyeye.seal.dao.SalesOrderDao;
import com.skyeye.seal.entity.SalesExchanges;
import com.skyeye.seal.entity.SalesOrder;
import com.skyeye.seal.entity.SalesOutLet;
import com.skyeye.seal.entity.SalesReturns;
import com.skyeye.seal.service.SalesExchangesService;
import com.skyeye.seal.service.SalesOrderService;
import com.skyeye.seal.service.SalesOutLetService;
import com.skyeye.seal.service.SalesReturnsService;
import com.skyeye.util.ErpOrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SalesOrderServiceImpl
 * @Description: 销售订单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "销售订单", groupName = "销售模块", flowable = true)
public class SalesOrderServiceImpl extends SkyeyeErpOrderServiceImpl<SalesOrderDao, SalesOrder> implements SalesOrderService {

    @Autowired
    private SalesOutLetService salesOutLetService;

    @Autowired
    private SalesReturnsService salesReturnsService;

    @Autowired
    private IContractService iContractService;

    @Autowired
    private ProductionPlanService productionPlanService;

    @Autowired
    private SalesExchangesService salesExchangesService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Override
    public QueryWrapper<SalesOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SalesOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(SalesOrder::getHolderId), commonPageInfo.getHolderId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getFromId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(SalesOrder::getFromId), commonPageInfo.getFromId());
        }
        return queryWrapper;
    }

    @Override
    public void validatorEntity(SalesOrder entity) {
        checkMaterialNorms(entity, false);
    }

    @Override
    public void createPrepose(SalesOrder entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.OUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
        setPurchaseState(entity);
    }

    @Override
    public void updatePrepose(SalesOrder entity) {
        super.updatePrepose(entity);
        setPurchaseState(entity);
    }

    /**
     * 根据明细物料来源设置采购状态：含外购则为待采购，否则无需采购
     */
    private void setPurchaseState(SalesOrder entity) {
        Integer purchaseState = SalesOrderPurchaseState.NOT_NEED.getKey();
        List<String> materialIds = entity.getErpOrderItemList().stream()
            .map(ErpOrderItem::getMaterialId).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(materialIds)) {
            List<Material> materials = materialService.selectByIds(materialIds.toArray(new String[]{}));
            for (Material material : materials) {
                if (material.getFromType() == MaterialFromType.OUTSOURCING.getKey()) {
                    purchaseState = SalesOrderPurchaseState.NEED.getKey();
                    break;
                }
            }
        }
        entity.setPurchaseState(purchaseState);
    }

    @Override
    public void editPurchaseState(String id, Integer purchaseState) {
        UpdateWrapper<SalesOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(SalesOrder::getPurchaseState), purchaseState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iContractService.setMationForMap(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public SalesOrder selectById(String id) {
        SalesOrder salesOrder = super.selectById(id);
        salesOrder.setName(salesOrder.getOddNumber());
        return salesOrder;
    }

    private void checkMaterialNorms(SalesOrder entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前销售订单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId, 
                item -> StrUtil.isEmpty(item.getOperNumber()) ? CommonNumConstants.NUM_ZERO.toString() : item.getOperNumber()));
        // 获取已经下达订单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == SealOrderFromType.CUSTOMER_CONTRACT.getKey()) {
            Map<String, Object> crmContract = iContractService.queryDataMationById(entity.getFromId());
            String crmContractChildListStr = MapUtil.checkKeyIsNull(crmContract, "crmContractChildList") ?
                StrUtil.EMPTY : crmContract.get("crmContractChildList").toString();
            if (StrUtil.isEmpty(crmContractChildListStr)) {
                throw new CustomException("该销售合同下未包含商品.");
            }
            List<Map<String, Object>> contractChildList = JSONUtil.toList(crmContractChildListStr, null);
            List<String> fromNormsIds = contractChildList.stream()
                .map(bean -> bean.get("normsId").toString()).collect(Collectors.toList());
            super.checkIdFromOrderMaterialNorms(fromNormsIds, inSqlNormsId);

            contractChildList.forEach(contractChild -> {
                String operNumber = contractChild.get("operNumber").toString();
                if (StrUtil.isEmpty(operNumber)) {
                    operNumber = CommonNumConstants.NUM_ZERO.toString();
                }
                String normsId = contractChild.get("normsId").toString();
                String surplusNum = ErpOrderUtil.checkOperNumber(operNumber, normsId, orderNormsNum, executeNum);
                if (setData) {
                    contractChild.put("operNumber", surplusNum);
                }
            });
            if (setData) {
                // 过滤掉剩余数量为0的商品
                contractChildList = contractChildList.stream()
                    .filter(contractChild -> {
                        String operNumber = contractChild.get("operNumber").toString();
                        if (StrUtil.isEmpty(operNumber)) {
                            operNumber = CommonNumConstants.NUM_ZERO.toString();
                        }
                        return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
                    }).collect(Collectors.toList());
                // 如果该合同的商品已经全部下达订单，那说明已经完成了合同的内容
                if (CollectionUtil.isEmpty(contractChildList)) {
                    iContractService.editCrmContractChildState(entity.getFromId(), "allIssued");
                } else {
                    iContractService.editCrmContractChildState(entity.getFromId(), "partialRelease");
                }
            }
        }
    }

    @Override
    public void approvalEndIsSuccess(SalesOrder entity) {
        entity = selectById(entity.getId());
        checkMaterialNorms(entity, true);
        // 增加已分配量库存
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpCommonService.editMaterialNormsDepotStock(MaterialNormsStockType.ALLOCATED_STOCK.getDefaultDepotId(), erpOrderItem.getMaterialId(),
                erpOrderItem.getNormsId(), erpOrderItem.getOperNumber(), DepotPutOutType.PUT.getKey(), MaterialNormsStockType.ALLOCATED_STOCK.getKey());
        });
    }

    /**
     * 获取审核通过的销售单列表展示为树
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySalesOrderListToTree(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<SalesOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SalesOrder::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(SalesOrder::getIdKey), getServiceClassName());
        List<SalesOrder> salesOrderList = list(queryWrapper);
        salesOrderList.forEach(salesOrder -> {
            salesOrder.setName(salesOrder.getOddNumber());
            salesOrder.setPId("0");
        });
        outputObject.setBeans(salesOrderList);
        outputObject.settotal(salesOrderList.size());
    }

    /**
     * 根据销售单id获取子单据列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySalesOrderMaterialListByOrderId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        SalesOrder salesOrder = selectById(id);
        outputObject.setBean(salesOrder);
        outputObject.setBeans(salesOrder.getErpOrderItemList());
        outputObject.settotal(salesOrder.getErpOrderItemList().size());
    }

    @Override
    public void querySealsOrderTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        SalesOrder salesOrder = selectById(id);
        // 获取已经入库的数量
        Map<String, String> normsNum = salesOutLetService.calcMaterialNormsNumByFromId(id);
        // 获取已经退货的数量
        Map<String, String> normsReturnsNum = salesReturnsService.calcMaterialNormsNumByFromId(id);
        // 获取已经换货的数量
        Map<String, String> normsExchangeNum = salesExchangesService.calcMaterialNormsNumByFromId(id);
        // 设置未下达销售出库单/销售退货单的商品数量-----订单数量 - 已入库的数量 - 已退货的数量 - 已换货的数量
        super.setOrCheckOperNumber(salesOrder.getErpOrderItemList(), true, normsNum, normsReturnsNum, normsExchangeNum);
        // 过滤掉数量为0的进行生成销售出库单/销售退货单
        salesOrder.setErpOrderItemList(salesOrder.getErpOrderItemList().stream()
            .filter(erpOrderItem -> {
                String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber()) 
                    ? CommonNumConstants.NUM_ZERO.toString() 
                    : erpOrderItem.getOperNumber();
                return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
            }).collect(Collectors.toList()));
        outputObject.setBean(salesOrder);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertSalesOrderToTurnPut(InputObject inputObject, OutputObject outputObject) {
        SalesOutLet salesOutLet = inputObject.getParams(SalesOutLet.class);
        // 获取销售单状态
        SalesOrder order = selectById(salesOutLet.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过/部分完成的可以进行出库
        if (FlowableStateEnum.PASS.getKey().equals(order.getState()) || ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            salesOutLet.setFromId(salesOutLet.getId());
            salesOutLet.setFromTypeId(SealOutLetFromType.SEAL_ORDER.getKey());
            salesOutLet.setId(StrUtil.EMPTY);
            salesOutLetService.createEntity(salesOutLet, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法出库.");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertSealsOrderToSealsReturns(InputObject inputObject, OutputObject outputObject) {
        SalesReturns salesReturns = inputObject.getParams(SalesReturns.class);
        // 获取销售单状态
        SalesOrder order = selectById(salesReturns.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过/部分完成的可以进行退货
        if (FlowableStateEnum.PASS.getKey().equals(order.getState()) || ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            salesReturns.setFromId(salesReturns.getId());
            salesReturns.setFromTypeId(SealReturnFromType.SEAL_ORDER.getKey());
            salesReturns.setId(StrUtil.EMPTY);
            salesReturnsService.createEntity(salesReturns, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法出库.");
        }
    }

    @Override
    public void insertSealsOrderToSealExchanges(InputObject inputObject, OutputObject outputObject) {
        SalesExchanges salesExchanges = inputObject.getParams(SalesExchanges.class);
        // 获取销售单状态
        SalesOrder order = selectById(salesExchanges.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过/部分完成的可以进行退货
        if (FlowableStateEnum.PASS.getKey().equals(order.getState()) || ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            salesExchanges.setFromId(salesExchanges.getId());
            salesExchanges.setFromTypeId(SalesExchangesFromType.SEAL_ORDER.getKey());
            salesExchanges.setId(StrUtil.EMPTY);
            salesExchangesService.createEntity(salesExchanges, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法操作.");
        }
    }

    @Override
    public void queryCrmContractTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        Map<String, Object> crmContract = iContractService.queryDataMationById(id);
        if (CollectionUtil.isEmpty(crmContract)) {
            throw new CustomException("该销售合同数据不存在.");
        }
        String crmContractChildListStr = MapUtil.checkKeyIsNull(crmContract, "crmContractChildList") ?
            StrUtil.EMPTY : crmContract.get("crmContractChildList").toString();
        if (StrUtil.isEmpty(crmContractChildListStr)) {
            throw new CustomException("该销售合同下未包含商品.");
        }
        List<Map<String, Object>> contractChildList = JSONUtil.toList(crmContractChildListStr, null);
        // 获取已经下达销售订单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(id);
        contractChildList.forEach(contractChild -> {
            String operNumber = contractChild.get("operNumber").toString();
            if (StrUtil.isEmpty(operNumber)) {
                operNumber = CommonNumConstants.NUM_ZERO.toString();
            }
            String normsId = contractChild.get("normsId").toString();
            String executeNumValue = executeNum.getOrDefault(normsId, CommonNumConstants.NUM_ZERO.toString());
            if (StrUtil.isEmpty(executeNumValue)) {
                executeNumValue = CommonNumConstants.NUM_ZERO.toString();
            }
            String surplusNum = CalculationUtil.subtract(operNumber, executeNumValue, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
            // 设置未下达销售订单的商品数量
            contractChild.put("operNumber", surplusNum);
        });
        // 过滤掉剩余数量为0的商品
        contractChildList = contractChildList.stream()
            .filter(contractChild -> {
                String operNumber = contractChild.get("operNumber").toString();
                if (StrUtil.isEmpty(operNumber)) {
                    operNumber = CommonNumConstants.NUM_ZERO.toString();
                }
                return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
            }).collect(Collectors.toList());

        crmContract.put("crmContractChildList", contractChildList);
        outputObject.setBean(crmContract);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertCrmContractToSealsOrder(InputObject inputObject, OutputObject outputObject) {
        SalesOrder salesOrder = inputObject.getParams(SalesOrder.class);
        Map<String, Object> crmContract = iContractService.queryDataMationById(salesOrder.getId());
        if (CollectionUtil.isEmpty(crmContract)) {
            throw new CustomException("该销售合同数据不存在.");
        }
        salesOrder.setFromId(salesOrder.getId());
        salesOrder.setFromTypeId(SealOrderFromType.CUSTOMER_CONTRACT.getKey());
        salesOrder.setId(null);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 保存销售订单
        createEntity(salesOrder, userId);
    }

    @Override
    public void querySealsOrderTransProductionPlanById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        SalesOrder salesOrder = selectById(id);
        // 获取已经下达出货计划单的数量
        Map<String, String> normsNum = productionPlanService.calcMaterialNormsNumByFromId(id);
        // 设置未下达销售出库单/销售退货单的商品数量-----订单数量 - 已经下达出货计划单的数量
        super.setOrCheckOperNumber(salesOrder.getErpOrderItemList(), true, normsNum);
        // 过滤掉数量为0的进行生成出货计划单
        salesOrder.setErpOrderItemList(salesOrder.getErpOrderItemList().stream()
            .filter(erpOrderItem -> {
                String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber()) 
                    ? CommonNumConstants.NUM_ZERO.toString() 
                    : erpOrderItem.getOperNumber();
                return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
            }).collect(Collectors.toList()));
        outputObject.setBean(salesOrder);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertSealsOrderToProductionPlan(InputObject inputObject, OutputObject outputObject) {
        ProductionPlan productionPlan = inputObject.getParams(ProductionPlan.class);
        // 获取销售单状态
        SalesOrder order = selectById(productionPlan.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过/部分完成的可以进行下达出货计划单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState()) || ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            productionPlan.setFromId(productionPlan.getId());
            productionPlan.setFromTypeId(ProductionPlanFromType.SEAL_ORDER.getKey());
            productionPlan.setId(StrUtil.EMPTY);
            productionPlanService.createEntity(productionPlan, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法出库.");
        }
    }

    @Override
    public void querySealsOrderTransPurchaseOrderById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        SalesOrder salesOrder = selectById(id);
        // 只转外购商品
        List<ErpOrderItem> erpOrderItemList = salesOrder.getErpOrderItemList().stream()
            .filter(erpOrderItem -> erpOrderItem.getMaterialMation().getFromType() == MaterialFromType.OUTSOURCING.getKey())
            .collect(Collectors.toList());
        // 获取已经下达采购订单的数量
        Map<String, String> normsNum = purchaseOrderService.calcMaterialNormsNumByFromId(id);
        // 设置未下达采购订单的商品数量-----订单数量 - 已经下达采购订单的数量
        super.setOrCheckOperNumber(erpOrderItemList, true, normsNum);
        // 过滤掉数量为0的进行生成采购订单
        salesOrder.setErpOrderItemList(erpOrderItemList.stream()
            .filter(erpOrderItem -> {
                String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : erpOrderItem.getOperNumber();
                return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
            }).collect(Collectors.toList()));
        outputObject.setBean(salesOrder);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertSealsOrderToPurchaseOrder(InputObject inputObject, OutputObject outputObject) {
        PurchaseOrder purchaseOrder = inputObject.getParams(PurchaseOrder.class);
        // 获取销售单状态
        SalesOrder order = selectById(purchaseOrder.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过/部分完成 && 待采购/部分采购 的可以转采购订单
        if ((FlowableStateEnum.PASS.getKey().equals(order.getState()) || ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey().equals(order.getState()))
            && (SalesOrderPurchaseState.NEED.getKey().equals(order.getPurchaseState())
            || SalesOrderPurchaseState.PARTIAL.getKey().equals(order.getPurchaseState()))) {
            String userId = inputObject.getLogParams().get("id").toString();
            purchaseOrder.setFromId(purchaseOrder.getId());
            purchaseOrder.setFromTypeId(PurchaseOrderFromType.SEAL_ORDER.getKey());
            purchaseOrder.setId(StrUtil.EMPTY);
            purchaseOrderService.createEntity(purchaseOrder, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法生成采购订单.");
        }
    }

}
