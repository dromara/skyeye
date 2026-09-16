/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.production.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.bom.entity.Bom;
import com.skyeye.bom.service.BomService;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialFromType;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.production.classenum.ProductionFromType;
import com.skyeye.production.classenum.ProductionPlanFromType;
import com.skyeye.production.classenum.ProductionPlanProduceState;
import com.skyeye.production.classenum.ProductionPlanPurchaseState;
import com.skyeye.production.dao.ProductionPlanDao;
import com.skyeye.production.entity.Production;
import com.skyeye.production.entity.ProductionPlan;
import com.skyeye.production.entity.ProductionPlanChild;
import com.skyeye.production.service.ProductionPlanChildService;
import com.skyeye.production.service.ProductionPlanService;
import com.skyeye.production.service.ProductionService;
import com.skyeye.purchase.classenum.PurchaseOrderFromType;
import com.skyeye.purchase.entity.PurchaseOrder;
import com.skyeye.purchase.service.PurchaseOrderService;
import com.skyeye.seal.entity.SalesOrder;
import com.skyeye.seal.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ProductionPlanServiceImpl
 * @Description: 出货计划单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/21 20:30
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "出货计划单", groupName = "出货计划单", flowable = true, orderApproval = true)
public class ProductionPlanServiceImpl extends SkyeyeBusinessServiceImpl<ProductionPlanDao, ProductionPlan> implements ProductionPlanService {

    @Autowired
    private ProductionPlanChildService productionPlanChildService;

    @Autowired
    private ProductionService productionService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private BomService bomService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        salesOrderService.setOrderMationByFromId(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public void validatorEntity(ProductionPlan entity) {
        checkMaterialNorms(entity, false);
    }

    @Override
    public void createPrepose(ProductionPlan entity) {
        super.createPrepose(entity);
        setOtherMation(entity);
    }

    @Override
    public void updatePrepose(ProductionPlan entity) {
        super.updatePrepose(entity);
        setOtherMation(entity);
    }

    @Override
    public void writePostpose(ProductionPlan entity, String userId) {
        // 保存子单据信息
        productionPlanChildService.saveList(entity.getId(), entity.getProductionPlanChildList());
        super.writePostpose(entity, userId);
    }

    private void setOtherMation(ProductionPlan entity) {
        // 设置采购状态
        Integer purchaseState = ProductionPlanPurchaseState.NOT_NEED.getKey();
        Integer produceState = ProductionPlanProduceState.NOT_NEED.getKey();
        List<String> materialIds = entity.getProductionPlanChildList().stream()
            .map(ProductionPlanChild::getMaterialId).distinct().collect(Collectors.toList());
        List<Material> materials = materialService.selectByIds(materialIds.toArray(new String[]{}));
        for (Material material : materials) {
            if (material.getFromType() == MaterialFromType.OUTSOURCING.getKey()) {
                // 外购件
                purchaseState = ProductionPlanPurchaseState.NEED.getKey();
                break;
            }
            if (material.getFromType() == MaterialFromType.SELF_PRODUCED.getKey()) {
                // 自产件
                produceState = ProductionPlanProduceState.NEED.getKey();
            }
        }
        entity.setPurchaseState(purchaseState);
        entity.setProduceState(produceState);
    }

    @Override
    public ProductionPlan getDataFromDb(String id) {
        ProductionPlan productionPlan = super.getDataFromDb(id);
        // 查询子单据信息
        productionPlan.setProductionPlanChildList(productionPlanChildService.selectByParentId(productionPlan.getId()));
        return productionPlan;
    }

    @Override
    public ProductionPlan selectById(String id) {
        ProductionPlan productionPlan = super.selectById(id);
        // 查询子单据产品信息
        materialService.setDataMation(productionPlan.getProductionPlanChildList(), ProductionPlanChild::getMaterialId);
        materialNormsService.setDataMation(productionPlan.getProductionPlanChildList(), ProductionPlanChild::getNormsId);
        if (productionPlan.getFromTypeId() == ProductionPlanFromType.SEAL_ORDER.getKey()) {
            // 销售订单
            salesOrderService.setDataMation(productionPlan, ProductionPlan::getFromId);
        }
        return productionPlan;
    }

    @Override
    public void deletePreExecution(String id) {
        ProductionPlan productionPlan = selectById(id);
        if (!FlowableStateEnum.DRAFT.getKey().equals(productionPlan.getState())
            && !FlowableStateEnum.REJECT.getKey().equals(productionPlan.getState())
            && !FlowableStateEnum.REVOKE.getKey().equals(productionPlan.getState())) {
            throw new CustomException("只有草稿、驳回、撤销状态的可删除.");
        }
    }

    @Override
    public void deletePostpose(String id) {
        // 删除子单据信息
        productionPlanChildService.deleteByParentId(id);
    }

    @Override
    public void setOrderMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> ids = beans.stream().filter(bean -> !MapUtil.checkKeyIsNull(bean, idKey))
            .map(bean -> bean.get(idKey).toString()).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        QueryWrapper<ProductionPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        List<ProductionPlan> productionPlanList = list(queryWrapper);
        Map<String, ProductionPlan> productionPlanMap = productionPlanList.stream()
            .collect(Collectors.toMap(ProductionPlan::getId, bean -> bean));
        for (Map<String, Object> bean : beans) {
            if (!MapUtil.checkKeyIsNull(bean, idKey)) {
                ProductionPlan entity = productionPlanMap.get(bean.get(idKey).toString());
                if (ObjectUtil.isEmpty(entity)) {
                    continue;
                }
                bean.put(mationKey, entity);
            }
        }
    }

    private void checkMaterialNorms(ProductionPlan entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前出货计划单的商品数量
        Map<String, String> orderNormsNum = entity.getProductionPlanChildList().stream()
            .collect(Collectors.toMap(ProductionPlanChild::getNormsId, ProductionPlanChild::getOperNumber));
        // 获取同一个来源单据下已经审批通过的出货计划单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == ProductionPlanFromType.SEAL_ORDER.getKey()) {
            // 销售订单
            SalesOrder salesOrder = salesOrderService.selectById(entity.getFromId());
            List<String> fromNormsIds = salesOrder.getErpOrderItemList().stream()
                .map(ErpOrderItem::getNormsId).collect(Collectors.toList());
            // 求差集(销售订单不包含的商品)
            List<String> diffList = inSqlNormsId.stream()
                .filter(num -> !fromNormsIds.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                List<MaterialNorms> materialNormsList = materialNormsService.selectByIds(diffList.toArray(new String[]{}));
                List<String> normsNames = materialNormsList.stream().map(MaterialNorms::getName).collect(Collectors.toList());
                throw new CustomException(String.format(Locale.ROOT, "该销售订单下未包含如下商品规格：【%s】.",
                    Joiner.on(CommonCharConstants.COMMA_MARK).join(normsNames)));
            }
            salesOrder.getErpOrderItemList().forEach(erpOrderItem -> {
                // 销售订单数量 - 当前出货计划单数量 - 已经审批通过的出货计划单数量
                String orderNum = orderNormsNum.containsKey(erpOrderItem.getNormsId()) 
                    ? orderNormsNum.get(erpOrderItem.getNormsId()) 
                    : CommonNumConstants.NUM_ZERO.toString();
                String execNum = executeNum.containsKey(erpOrderItem.getNormsId()) 
                    ? executeNum.get(erpOrderItem.getNormsId()) 
                    : CommonNumConstants.NUM_ZERO.toString();
                String tempNum = CalculationUtil.subtract(erpOrderItem.getOperNumber(), orderNum, ErpConstants.NUM_AFTER_DOT);
                String surplusNum = CalculationUtil.subtract(tempNum, execNum, ErpConstants.NUM_AFTER_DOT);
                if (CalculationUtil.compareTo(surplusNum, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
                    throw new CustomException("超出销售订单的商品数量.");
                }
                if (setData) {
                    erpOrderItem.setOperNumber(surplusNum);
                }
            });
            if (setData) {
                // TODO 该销售订单的商品是否已经全部下达了出货计划单-----目前先不做任何操作
            }
        }
    }

    @Override
    public void approvalEndIsSuccess(ProductionPlan entity) {
        entity = selectById(entity.getId());
        // 修改来源单据的状态信息
        checkMaterialNorms(entity, true);
    }

    @Override
    public Map<String, String> calcMaterialNormsNumByFromId(String fromId) {
        QueryWrapper<ProductionPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(CommonConstants.ID);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductionPlan::getFromId), fromId);
        // 只查询审批通过的
        List<String> stateList = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey()});
        queryWrapper.in(MybatisPlusUtil.toColumns(ProductionPlan::getState), stateList);
        List<ProductionPlan> productionList = list(queryWrapper);
        List<String> ids = productionList.stream().map(ProductionPlan::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        List<ProductionPlanChild> productionPlanChildList = productionPlanChildService.selectByParentId(ids);
        Map<String, String> collect = productionPlanChildList.stream()
            .collect(Collectors.groupingBy(
                ProductionPlanChild::getNormsId,
                Collectors.reducing(
                    CommonNumConstants.NUM_ZERO.toString(),
                    ProductionPlanChild::getOperNumber,
                    (sum, operNumber) -> CalculationUtil.add(
                        ErpConstants.NUM_AFTER_DOT,
                        StrUtil.isEmpty(sum) ? CommonNumConstants.NUM_ZERO.toString() : sum,
                        StrUtil.isEmpty(operNumber) ? CommonNumConstants.NUM_ZERO.toString() : operNumber
                    )
                )
            ));
        return collect;
    }

    @Override
    public void editPurchaseState(String id, Integer purchaseState) {
        UpdateWrapper<ProductionPlan> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ProductionPlan::getPurchaseState), purchaseState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void editProduceState(String id, Integer produceState) {
        UpdateWrapper<ProductionPlan> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ProductionPlan::getProduceState), produceState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void queryProductionPlanTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        ProductionPlan productionPlan = selectById(id);
        // 只查询自产商品
        List<ProductionPlanChild> productionPlanChildList = productionPlan.getProductionPlanChildList().stream()
            .filter(productionPlanChild -> productionPlanChild.getMaterialMation().getFromType() == MaterialFromType.SELF_PRODUCED.getKey())
            .collect(Collectors.toList());
        // 获取已经下达生产计划单的数量
        Map<String, String> normsNum = productionService.calcMaterialNormsNumByFromId(id);
        productionPlanChildList.forEach(productionPlanChild -> {
            // 订单数量 - 已经下达生产计划单的数量
            String normsNumValue = normsNum.containsKey(productionPlanChild.getNormsId()) 
                ? normsNum.get(productionPlanChild.getNormsId()) 
                : CommonNumConstants.NUM_ZERO.toString();
            String surplusNum = CalculationUtil.subtract(productionPlanChild.getOperNumber(), normsNumValue, ErpConstants.NUM_AFTER_DOT);
            // 设置未下达生产计划单的商品数量
            productionPlanChild.setOperNumber(surplusNum);
        });
        // 过滤掉数量为0的进行生成生产计划单
        productionPlanChildList = productionPlanChildList.stream()
            .filter(productionPlanChild -> CalculationUtil.compareTo(productionPlanChild.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList());
        // 获取规格对应的所有bom信息
        List<String> normsId = productionPlanChildList.stream()
            .map(ProductionPlanChild::getNormsId).distinct().collect(Collectors.toList());
        Map<String, List<Bom>> listMap = bomService.getBomListByNormsId(normsId.toArray(new String[]{}));
        // 设置生产类型信息
        productionPlanChildList.forEach(productionPlanChild -> {
            productionPlanChild.setBomList(listMap.get(productionPlanChild.getNormsId()));
        });
        productionPlan.setProductionPlanChildList(productionPlanChildList);
        outputObject.setBean(productionPlan);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertProductionPlanToProduction(InputObject inputObject, OutputObject outputObject) {
        Production production = inputObject.getParams(Production.class);
        // 获取出货计划单状态
        ProductionPlan order = selectById(production.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过 && 生产状态为待生产/部分生产 的可以转生产计划单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState()) &&
            (ProductionPlanProduceState.NEED.getKey().equals(order.getProduceState())
                || ProductionPlanProduceState.PARTIAL.getKey().equals(order.getProduceState()))) {
            String userId = inputObject.getLogParams().get("id").toString();
            production.setFromId(production.getId());
            production.setFromTypeId(ProductionFromType.DELIVERY_PLAN.getKey());
            production.setId(StrUtil.EMPTY);
            productionService.createEntity(production, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法生成生产计划单.");
        }
    }

    @Override
    public void queryProductionPlanTransPurchaseOrderById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        ProductionPlan productionPlan = selectById(id);
        // 只查询外购商品
        List<ProductionPlanChild> productionPlanChildList = productionPlan.getProductionPlanChildList().stream()
            .filter(productionPlanChild -> productionPlanChild.getMaterialMation().getFromType() == MaterialFromType.OUTSOURCING.getKey())
            .collect(Collectors.toList());
        // 获取已经下达采购订单的数量
        Map<String, String> normsNum = purchaseOrderService.calcMaterialNormsNumByFromId(id);
        productionPlanChildList.forEach(productionPlanChild -> {
            // 订单数量 - 已经下达采购订单的数量
            String normsNumValue = normsNum.containsKey(productionPlanChild.getNormsId()) 
                ? normsNum.get(productionPlanChild.getNormsId()) 
                : CommonNumConstants.NUM_ZERO.toString();
            String surplusNum = CalculationUtil.subtract(productionPlanChild.getOperNumber(), normsNumValue, ErpConstants.NUM_AFTER_DOT);
            // 设置未下达采购订单的商品数量
            productionPlanChild.setOperNumber(surplusNum);
        });
        // 过滤掉数量为0的进行生成采购订单
        productionPlan.setProductionPlanChildList(productionPlanChildList.stream()
            .filter(productionPlanChild -> CalculationUtil.compareTo(productionPlanChild.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        outputObject.setBean(productionPlan);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertProductionPlanToPurchaseOrder(InputObject inputObject, OutputObject outputObject) {
        PurchaseOrder purchaseOrder = inputObject.getParams(PurchaseOrder.class);
        // 获取出货计划单状态
        ProductionPlan order = selectById(purchaseOrder.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过 && 采购状态为待生产/部分生产 的可以转生产计划单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState()) &&
            (ProductionPlanPurchaseState.NEED.getKey().equals(order.getPurchaseState())
                || ProductionPlanPurchaseState.PARTIAL.getKey().equals(order.getPurchaseState()))) {
            String userId = inputObject.getLogParams().get("id").toString();
            purchaseOrder.setFromId(purchaseOrder.getId());
            purchaseOrder.setFromTypeId(PurchaseOrderFromType.DELIVERY_PLAN.getKey());
            purchaseOrder.setId(StrUtil.EMPTY);
            purchaseOrderService.createEntity(purchaseOrder, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法生成采购订单.");
        }
    }
}
