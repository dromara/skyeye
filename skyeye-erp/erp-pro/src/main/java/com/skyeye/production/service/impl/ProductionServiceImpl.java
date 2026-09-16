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
import com.skyeye.exception.CustomException;
import com.skyeye.machin.classenum.MachinFromType;
import com.skyeye.machin.entity.Machin;
import com.skyeye.machin.service.MachinService;
import com.skyeye.material.classenum.MaterialFromType;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.production.classenum.*;
import com.skyeye.production.dao.ProductionDao;
import com.skyeye.production.entity.Production;
import com.skyeye.production.entity.ProductionChild;
import com.skyeye.production.entity.ProductionPlan;
import com.skyeye.production.entity.ProductionPlanChild;
import com.skyeye.production.service.ProductionChildService;
import com.skyeye.production.service.ProductionPlanService;
import com.skyeye.production.service.ProductionService;
import com.skyeye.util.ErpOrderUtil;
import com.skyeye.whole.classenum.WholeOrderOutFromType;
import com.skyeye.whole.entity.WholeOrderOut;
import com.skyeye.whole.service.WholeOrderOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ProductionServiceImpl
 * @Description: 生产计划单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "生产计划单管理", groupName = "生产计划单管理", flowable = true, orderApproval = true, allowDynamicAttrKey = false)
public class ProductionServiceImpl extends SkyeyeBusinessServiceImpl<ProductionDao, Production> implements ProductionService {

    @Autowired
    private ProductionChildService productionChildService;

    @Autowired
    private BomService bomService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MachinService machinService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private ProductionPlanService productionPlanService;

    @Autowired
    private WholeOrderOutService wholeOrderOutService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        productionPlanService.setOrderMationByFromId(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public void validatorEntity(Production entity) {
        checkOrderItem(entity);
        checkMaterialNorms(entity, false);
    }

    private void checkOrderItem(Production entity) {
        Integer outState = ProductionOutState.NOT_NEED_OUT.getKey();
        Integer machinOrderState = ProductionMachinOrderState.NOT_NEED_ISSUE.getKey();

        for (ProductionChild productionChild : entity.getProductionChildList()) {
            // 计划开始时间不能晚于计划结束时间（允许相等），格式：yyyy-MM-dd
            if (StrUtil.isNotEmpty(productionChild.getPlanStartTime())
                && StrUtil.isNotEmpty(productionChild.getPlanEndTime())
                && productionChild.getPlanStartTime().compareTo(productionChild.getPlanEndTime()) > 0) {
                throw new CustomException("子单据计划开始时间不能晚于计划结束时间.");
            }
            // 自制
            if (productionChild.getProductionType() == ProductionChildType.SELF_CONTROL.getKey()) {
                machinOrderState = ProductionMachinOrderState.NEED_ISSUE.getKey();
            }
            // 委外
            if (productionChild.getProductionType() == ProductionChildType.OUTSOURCING.getKey()) {
                outState = ProductionOutState.NEED_OUT.getKey();
            }
        }
        entity.setOutState(outState);
        entity.setMachinOrderState(machinOrderState);
    }

    @Override
    public void writePostpose(Production entity, String userId) {
        // 保存子单据信息
        productionChildService.saveList(entity.getId(), entity.getProductionChildList());
        super.writePostpose(entity, userId);
    }

    @Override
    public Production getDataFromDb(String id) {
        Production production = super.getDataFromDb(id);
        // 查询子单据信息
        production.setProductionChildList(productionChildService.selectByParentId(production.getId()));
        return production;
    }

    @Override
    public List<Production> getDataFromDb(List<String> idList) {
        List<Production> productionList = super.getDataFromDb(idList);
        List<ProductionChild> childList = productionChildService.selectByParentId(idList);
        Map<String, List<ProductionChild>> childMap = childList.stream()
            .collect(Collectors.groupingBy(ProductionChild::getParentId));
        productionList.forEach(production ->
            production.setProductionChildList(childMap.getOrDefault(production.getId(), Collections.emptyList())));
        return productionList;
    }

    @Override
    public Production selectById(String id) {
        Production production = super.selectById(id);
        // 查询方案信息
        bomService.setDataMation(production.getProductionChildList(), ProductionChild::getBomId);
        // 查询子单据产品信息
        materialService.setDataMation(production.getProductionChildList(), ProductionChild::getMaterialId);
        materialNormsService.setDataMation(production.getProductionChildList(), ProductionChild::getNormsId);
        if (production.getFromTypeId() == ProductionFromType.DELIVERY_PLAN.getKey()) {
            // 出货计划单
            productionPlanService.setDataMation(production, Production::getFromId);
        }
        // 获取规格对应的所有bom信息
        List<String> normsId = production.getProductionChildList().stream()
            .map(ProductionChild::getNormsId).distinct().collect(Collectors.toList());
        Map<String, List<Bom>> listMap = bomService.getBomListByNormsId(normsId.toArray(new String[]{}));
        // 设置生产类型信息
        production.getProductionChildList().forEach(productionChild -> {
            productionChild.setProductionTypeMation(ProductionChildType.getMation(productionChild.getProductionType()));
            productionChild.setBomList(listMap.get(productionChild.getNormsId()));
        });

        return production;
    }

    @Override
    public void deletePreExecution(String id) {
        Production production = selectById(id);
        if (!FlowableStateEnum.DRAFT.getKey().equals(production.getState())
            && !FlowableStateEnum.REJECT.getKey().equals(production.getState())
            && !FlowableStateEnum.REVOKE.getKey().equals(production.getState())) {
            throw new CustomException("只有草稿、驳回、撤销状态的可删除.");
        }
    }

    @Override
    public void deletePostpose(String id) {
        // 删除子单据信息
        productionChildService.deleteByParentId(id);
    }

    @Override
    public void approvalEndIsSuccess(Production entity) {
        entity = selectById(entity.getId());
        // 修改来源单据的状态信息
        checkMaterialNorms(entity, true);
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
        QueryWrapper<Production> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        List<Production> productionList = list(queryWrapper);
        Map<String, Production> productionMap = productionList.stream()
            .collect(Collectors.toMap(Production::getId, bean -> bean));
        for (Map<String, Object> bean : beans) {
            if (!MapUtil.checkKeyIsNull(bean, idKey)) {
                Production entity = productionMap.get(bean.get(idKey).toString());
                if (ObjectUtil.isEmpty(entity)) {
                    continue;
                }
                bean.put(mationKey, entity);
            }
        }
    }

    private void checkMaterialNorms(Production entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前生产计划单的商品数量
        Map<String, String> orderNormsNum = entity.getProductionChildList().stream()
            .collect(Collectors.toMap(ProductionChild::getNormsId, ProductionChild::getOperNumber));
        // 获取同一个来源单据下已经审批通过的生产计划单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == ProductionFromType.DELIVERY_PLAN.getKey()) {
            // 出货计划单
            ProductionPlan productionPlan = productionPlanService.selectById(entity.getFromId());
            // 只查询自产商品
            List<ProductionPlanChild> productionPlanChildList = productionPlan.getProductionPlanChildList().stream()
                .filter(productionPlanChild -> productionPlanChild.getMaterialMation().getFromType() == MaterialFromType.SELF_PRODUCED.getKey())
                .collect(Collectors.toList());
            List<String> fromNormsIds = productionPlanChildList.stream()
                .map(ProductionPlanChild::getNormsId).collect(Collectors.toList());
            // 求差集(出货计划单不包含的商品)
            List<String> diffList = inSqlNormsId.stream()
                .filter(num -> !fromNormsIds.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                List<MaterialNorms> materialNormsList = materialNormsService.selectByIds(diffList.toArray(new String[]{}));
                List<String> normsNames = materialNormsList.stream().map(MaterialNorms::getName).collect(Collectors.toList());
                throw new CustomException(String.format(Locale.ROOT, "该出货计划单下未包含如下商品规格：【%s】.",
                    Joiner.on(CommonCharConstants.COMMA_MARK).join(normsNames)));
            }
            productionPlanChildList.forEach(productionPlanChild -> {
                // 出货计划单数量 - 当前生产计划单数量 - 已经审批通过的生产计划单数量
                String surplusNum = ErpOrderUtil.checkOperNumber(productionPlanChild.getOperNumber(), productionPlanChild.getNormsId(),
                    orderNormsNum, executeNum);
                if (setData) {
                    productionPlanChild.setOperNumber(surplusNum);
                }
            });
            if (setData) {
                // 过滤掉剩余数量为0的商品
                List<ProductionPlanChild> list = productionPlanChildList.stream()
                    .filter(productionPlanChild -> CalculationUtil.compareTo(productionPlanChild.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
                    .collect(Collectors.toList());
                // 该出货计划单的商品已经全部下达了生产计划单
                if (CollectionUtil.isEmpty(list)) {
                    productionPlanService.editProduceState(productionPlan.getId(), ProductionPlanProduceState.COMPLATE.getKey());
                } else {
                    productionPlanService.editProduceState(productionPlan.getId(), ProductionPlanProduceState.PARTIAL.getKey());
                }
            }
        }
    }

    @Override
    public void editOutState(String id, Integer outState) {
        UpdateWrapper<Production> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Production::getOutState), outState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void editMachinOrderState(String id, Integer machinOrderState) {
        UpdateWrapper<Production> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Production::getMachinOrderState), machinOrderState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public Map<String, String> calcMaterialNormsNumByFromId(String fromId) {
        QueryWrapper<Production> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(CommonConstants.ID);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Production::getFromId), fromId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Production::getState), FlowableStateEnum.PASS.getKey());
        List<Production> productionList = list(queryWrapper);
        List<String> ids = productionList.stream().map(Production::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        List<ProductionChild> productionChildList = productionChildService.selectByParentId(ids);
        Map<String, String> collect = productionChildList.stream()
            .collect(Collectors.groupingBy(
                ProductionChild::getNormsId,
                Collectors.reducing(
                    CommonNumConstants.NUM_ZERO.toString(),
                    ProductionChild::getOperNumber,
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
    public void queryProductionTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        Production production = selectById(id);
        // 获取已经下达加工单的数量
        Map<String, String> normsNum = machinService.calcMaterialNormsNumByFromId(id);
        production.getProductionChildList().forEach(productionChild -> {
            // 生产计划单数量 - 已经下达加工单的数量
            String normsNumValue = normsNum.containsKey(productionChild.getNormsId())
                ? normsNum.get(productionChild.getNormsId())
                : CommonNumConstants.NUM_ZERO.toString();
            String surplusNum = CalculationUtil.subtract(productionChild.getOperNumber(), normsNumValue, ErpConstants.NUM_AFTER_DOT);
            // 设置未下达加工单的商品数量
            productionChild.setOperNumber(surplusNum);
        });
        // 过滤掉数量为0的进行生成加工单
        production.setProductionChildList(production.getProductionChildList().stream()
            .filter(productionChild -> CalculationUtil.compareTo(productionChild.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0
                && productionChild.getProductionType() == ProductionChildType.SELF_CONTROL.getKey())
            .collect(Collectors.toList()));
        // 获取规格对应的所有bom信息
        List<String> normsId = production.getProductionChildList().stream()
            .map(ProductionChild::getNormsId).distinct().collect(Collectors.toList());
        Map<String, List<Bom>> listMap = bomService.getBomListByNormsId(normsId.toArray(new String[]{}));
        // 设置生产类型信息
        production.getProductionChildList().forEach(productionChild -> {
            productionChild.setBomList(listMap.get(productionChild.getNormsId()));
        });
        outputObject.setBean(production);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertProductionToMachin(InputObject inputObject, OutputObject outputObject) {
        Machin machin = inputObject.getParams(Machin.class);
        // 获取生产计划单状态
        Production order = selectById(machin.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转加工单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            machin.setFromId(machin.getId());
            machin.setFromTypeId(MachinFromType.PRODUCTION.getKey());
            machin.setId(StrUtil.EMPTY);
            machinService.createEntity(machin, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法转加工单.");
        }
    }

    @Override
    public void queryProductionTransWholeById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        Production production = selectById(id);
        // 获取已经下达整单委外单的数量
        Map<String, String> normsNum = wholeOrderOutService.calcMaterialNormsNumByFromId(id);
        production.getProductionChildList().forEach(productionChild -> {
            // 生产计划单数量 - 已经下达整单委外单的数量
            String normsNumValue = normsNum.containsKey(productionChild.getNormsId())
                ? normsNum.get(productionChild.getNormsId())
                : CommonNumConstants.NUM_ZERO.toString();
            String surplusNum = CalculationUtil.subtract(productionChild.getOperNumber(), normsNumValue, ErpConstants.NUM_AFTER_DOT);
            // 设置未下达整单委外单的商品数量
            productionChild.setOperNumber(surplusNum);
        });
        // 过滤掉数量为0的进行生成整单委外单
        production.setProductionChildList(production.getProductionChildList().stream()
            .filter(productionChild -> CalculationUtil.compareTo(productionChild.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0
                && productionChild.getProductionType() == ProductionChildType.OUTSOURCING.getKey())
            .collect(Collectors.toList()));
        outputObject.setBean(production);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertProductionToWhole(InputObject inputObject, OutputObject outputObject) {
        WholeOrderOut wholeOrderOut = inputObject.getParams(WholeOrderOut.class);
        // 获取生产计划单状态
        Production order = selectById(wholeOrderOut.getId());
        if (ObjectUtil.isEmpty(order)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转整单委外单
        if (FlowableStateEnum.PASS.getKey().equals(order.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            wholeOrderOut.setFromId(wholeOrderOut.getId());
            wholeOrderOut.setFromTypeId(WholeOrderOutFromType.PRODUCTION.getKey());
            wholeOrderOut.setId(StrUtil.EMPTY);
            wholeOrderOutService.createEntity(wholeOrderOut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法转整单委外单.");
        }
    }

}
