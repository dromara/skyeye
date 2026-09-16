/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.inspection.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.business.classenum.OrderItemQualityInspectionType;
import com.skyeye.business.classenum.OrderQualityInspectionType;
import com.skyeye.common.constans.CommonCharConstants;
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
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.inspection.classenum.QualityInspectionExchangesState;
import com.skyeye.inspection.classenum.QualityInspectionFromType;
import com.skyeye.inspection.classenum.QualityInspectionPutState;
import com.skyeye.inspection.classenum.QualityInspectionReturnState;
import com.skyeye.inspection.dao.QualityInspectionDao;
import com.skyeye.inspection.entity.QualityInspection;
import com.skyeye.inspection.entity.QualityInspectionItem;
import com.skyeye.inspection.service.QualityInspectionItemService;
import com.skyeye.inspection.service.QualityInspectionService;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.purchase.classenum.PurchaseExchangesFromType;
import com.skyeye.purchase.classenum.PurchasePutFromType;
import com.skyeye.purchase.classenum.PurchaseReturnsFromType;
import com.skyeye.purchase.entity.PurchaseDelivery;
import com.skyeye.purchase.entity.PurchaseExchange;
import com.skyeye.purchase.entity.PurchasePut;
import com.skyeye.purchase.entity.PurchaseReturn;
import com.skyeye.purchase.service.PurchaseDeliveryService;
import com.skyeye.purchase.service.PurchaseExchangesService;
import com.skyeye.purchase.service.PurchasePutService;
import com.skyeye.purchase.service.PurchaseReturnsService;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: QualityInspectionServiceImpl
 * @Description: 质检单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 8:23
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "质检单", groupName = "质检单", flowable = true, orderApproval = true)
public class QualityInspectionServiceImpl extends SkyeyeBusinessServiceImpl<QualityInspectionDao, QualityInspection> implements QualityInspectionService {

    @Autowired
    private QualityInspectionItemService qualityInspectionItemService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private PurchaseDeliveryService purchaseDeliveryService;

    @Autowired
    private PurchasePutService purchasePutService;

    @Autowired
    private PurchaseReturnsService purchaseReturnsService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private PurchaseExchangesService purchaseExchangesService;

    @Override
    public QueryWrapper<QualityInspection> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<QualityInspection> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(QualityInspection::getHolderId), commonPageInfo.getHolderId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getFromId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(QualityInspection::getFromId), commonPageInfo.getFromId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        purchaseDeliveryService.setOrderMationByFromId(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public void validatorEntity(QualityInspection entity) {
        checkOrderItem(entity);
        checkMaterialNorms(entity, false);
    }

    @Override
    public void writePostpose(QualityInspection entity, String userId) {
        qualityInspectionItemService.saveList(entity.getId(), entity.getQualityInspectionItemList());
        super.writePostpose(entity, userId);
    }

    private void checkOrderItem(QualityInspection entity) {
        Integer putState = QualityInspectionPutState.NOT_NEED_PUT.getKey();
        Integer returnState = QualityInspectionReturnState.NOT_NEED_RETURN.getKey();
        Integer exchangesState = QualityInspectionExchangesState.NOT_NEED_EXCHANGES.getKey();

        if (CollectionUtil.isEmpty(entity.getQualityInspectionItemList())) {
            throw new CustomException("请最少选择一条产品信息");
        }
        List<String> normsIds = entity.getQualityInspectionItemList().stream().map(QualityInspectionItem::getNormsId).distinct().collect(Collectors.toList());
        if (entity.getQualityInspectionItemList().size() != normsIds.size()) {
            throw new CustomException("单据中不允许存在重复的产品规格信息");
        }

        for (QualityInspectionItem qualityInspectionItem : entity.getQualityInspectionItemList()) {
            // 实际验收总数量 = 合格数量 + 验收退回数量 + 让步接收数量 + 换货数量
            String tempNum = CalculationUtil.add(
                ErpConstants.NUM_AFTER_DOT,
                CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, qualityInspectionItem.getQualifiedNumber(), qualityInspectionItem.getReturnNumber()),
                CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, qualityInspectionItem.getConcessionNumber(), qualityInspectionItem.getExchangesNumber())
            );
            if (qualityInspectionItem.getQualityInspection() == OrderItemQualityInspectionType.FULL_INSPECTION.getKey()) {
                // 全检
                // 质检数量 != 实际验收总数量
                if (CalculationUtil.compareTo(qualityInspectionItem.getOperNumber(), tempNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) != 0) {
                    throw new CustomException("验收数量不等于【合格数量】 + 【验收退回数量】 + 【让步接收数量】 + 【验收换货数量】，请确认.");
                }
            } else if (qualityInspectionItem.getQualityInspection() == OrderItemQualityInspectionType.SAMPLING_INS.getKey()) {
                // 抽检
                if (StrUtil.isBlank(qualityInspectionItem.getQualityInspectionRatio())) {
                    throw new CustomException("抽检比例不能为空.");
                }
                // 计算抽检比例
                String samplingRatio = CalculationUtil.divide(qualityInspectionItem.getQualityInspectionRatio(), "100", CommonNumConstants.NUM_TWO);
                // 计算需要抽检的数量
                String samplingNum = CalculationUtil.multiply(ErpConstants.NUM_AFTER_DOT, samplingRatio, qualityInspectionItem.getOperNumber());
                // 实际验收总数量 < 需要抽检的数量
                if (CalculationUtil.compareTo(tempNum, samplingNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
                    throw new CustomException("抽检数量不足，请确认.");
                }
            }

            // 设置入库状态
            if (CalculationUtil.compareTo(qualityInspectionItem.getQualifiedNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0
                || CalculationUtil.compareTo(qualityInspectionItem.getConcessionNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0) {
                putState = QualityInspectionPutState.NEED_PUT.getKey();
            }
            // 设置退货状态
            if (CalculationUtil.compareTo(qualityInspectionItem.getReturnNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0) {
                returnState = QualityInspectionReturnState.NEED_RETURN.getKey();
            }
            // 设置换货状态
            if (CalculationUtil.compareTo(qualityInspectionItem.getExchangesNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0) {
                exchangesState = QualityInspectionExchangesState.NEED_EXCHANGES.getKey();
            }
        }
        entity.setPutState(putState);
        entity.setReturnState(returnState);
        entity.setExchangesState(exchangesState);
    }

    private void checkMaterialNorms(QualityInspection entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前质检单的商品数量
        Map<String, String> orderNormsNum = entity.getQualityInspectionItemList().stream()
            .collect(Collectors.toMap(QualityInspectionItem::getNormsId, QualityInspectionItem::getOperNumber));
        // 获取同一个来源单据下已经质检(审批通过)的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        if (entity.getFromTypeId() == QualityInspectionFromType.PURCHASE_DELIVERY.getKey()) {
            // 到货单
            PurchaseDelivery purchaseDelivery = purchaseDeliveryService.selectById(entity.getFromId());
            // 过滤掉到货单中免检的商品
            List<ErpOrderItem> erpOrderItemList = purchaseDelivery.getErpOrderItemList().stream()
                .filter(bean -> bean.getQualityInspection() != OrderItemQualityInspectionType.NOT_NEED_QUALITYINS_INS.getKey())
                .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                throw new CustomException("该到货单下未包含需要质检的商品.");
            }
            List<String> fromNormsIds = erpOrderItemList.stream()
                .map(ErpOrderItem::getNormsId).collect(Collectors.toList());
            // 求差集(到货单不包含的商品)
            List<String> diffList = inSqlNormsId.stream()
                .filter(num -> !fromNormsIds.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                List<MaterialNorms> materialNormsList = materialNormsService.selectByIds(diffList.toArray(new String[]{}));
                List<String> normsNames = materialNormsList.stream().map(MaterialNorms::getName).collect(Collectors.toList());
                throw new CustomException(String.format(Locale.ROOT, "该到货单下未包含如下商品规格：【%s】.",
                    Joiner.on(CommonCharConstants.COMMA_MARK).join(normsNames)));
            }
            erpOrderItemList.forEach(erpOrderItem -> {
                String orderNum = orderNormsNum.containsKey(erpOrderItem.getNormsId())
                    ? orderNormsNum.get(erpOrderItem.getNormsId())
                    : CommonNumConstants.NUM_ZERO.toString();
                String execNum = executeNum.containsKey(erpOrderItem.getNormsId())
                    ? executeNum.get(erpOrderItem.getNormsId())
                    : CommonNumConstants.NUM_ZERO.toString();
                String tempNum = CalculationUtil.subtract(erpOrderItem.getOperNumber(), orderNum, ErpConstants.NUM_AFTER_DOT);
                String surplusNum = CalculationUtil.subtract(tempNum, execNum, ErpConstants.NUM_AFTER_DOT);
                if (CalculationUtil.compareTo(surplusNum, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
                    throw new CustomException("超出到货单的商品数量.");
                }
                if (setData) {
                    erpOrderItem.setOperNumber(surplusNum);
                }
            });
            if (setData) {
                // 过滤掉剩余数量为0的商品
                erpOrderItemList = erpOrderItemList.stream()
                    .filter(erpOrderItem -> CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
                    .collect(Collectors.toList());
                // 该到货单的商品已经全部进行了质检
                if (CollectionUtil.isEmpty(erpOrderItemList)) {
                    purchaseDeliveryService.editQualityInspection(purchaseDelivery.getId(), OrderQualityInspectionType.COMPLATE_QUALITY_INSPECTION.getKey());
                } else {
                    purchaseDeliveryService.editQualityInspection(purchaseDelivery.getId(), OrderQualityInspectionType.PARTIAL_QUALITY_INSPECTION.getKey());
                }
            }
        }
    }

    @Override
    public QualityInspection getDataFromDb(String id) {
        QualityInspection qualityInspection = super.getDataFromDb(id);
        List<QualityInspectionItem> qualityInspectionItemList = qualityInspectionItemService.selectByParentId(qualityInspection.getId());
        qualityInspection.setQualityInspectionItemList(qualityInspectionItemList);
        return qualityInspection;
    }

    @Override
    public QualityInspection selectById(String id) {
        QualityInspection qualityInspection = super.selectById(id);
        // 设置产品信息
        materialService.setDataMation(qualityInspection.getQualityInspectionItemList(), QualityInspectionItem::getMaterialId);
        qualityInspection.getQualityInspectionItemList().forEach(qualityInspectionItem -> {
            MaterialNorms norms = qualityInspectionItem.getMaterialMation().getMaterialNorms()
                .stream().filter(bean -> StrUtil.equals(qualityInspectionItem.getNormsId(), bean.getId())).findFirst().orElse(null);
            qualityInspectionItem.setNormsMation(norms);
        });
        // 仓库信息
        erpDepotService.setDataMation(qualityInspection.getQualityInspectionItemList(), QualityInspectionItem::getDepotId);
        // 质检部门
        iDepmentService.setDataMation(qualityInspection, QualityInspection::getDepartmentId);
        // 质检员信息
        iAuthUserService.setDataMation(qualityInspection.getQualityInspectionItemList(), QualityInspectionItem::getInspectorId);
        if (qualityInspection.getFromTypeId() == QualityInspectionFromType.PURCHASE_DELIVERY.getKey()) {
            // 到货单
            purchaseDeliveryService.setDataMation(qualityInspection, QualityInspection::getFromId);
        } else if (qualityInspection.getFromTypeId() == QualityInspectionFromType.MACHIN_ORDER.getKey()) {
            // 生产加工单
        }

        // 供应商
        supplierService.setDataMation(qualityInspection, QualityInspection::getHolderId);

        qualityInspection.getQualityInspectionItemList().forEach(qualityInspectionItem -> {
            qualityInspectionItem.setQualityInspectionMation(OrderItemQualityInspectionType.getMation(qualityInspectionItem.getQualityInspection()));
        });
        return qualityInspection;
    }

    @Override
    public void approvalEndIsSuccess(QualityInspection entity) {
        entity = selectById(entity.getId());
        // 修改来源单据的质检信息
        checkMaterialNorms(entity, true);
    }

    @Override
    public Map<String, String> calcMaterialNormsNumByFromId(String... fromId) {
        List<String> fromIdList = Arrays.asList(fromId);
        if (CollectionUtil.isEmpty(fromIdList)) {
            return cn.hutool.core.map.MapUtil.newHashMap();
        }
        QueryWrapper<QualityInspection> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(QualityInspection::getFromId), fromIdList);
        // 只查询审批通过的质检单
        List<String> stateList = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey()});
        queryWrapper.in(MybatisPlusUtil.toColumns(QualityInspection::getState), stateList);
        List<QualityInspection> qualityInspectionList = list(queryWrapper);
        List<String> ids = qualityInspectionList.stream().map(QualityInspection::getId).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        // 获取所有的商品信息
        List<QualityInspectionItem> qualityInspectionItemList = qualityInspectionItemService.selectByParentId(ids);
        if (CollectionUtil.isNotEmpty(qualityInspectionItemList)) {
            // 分组计算已经质检的数量
            return qualityInspectionItemList.stream()
                .collect(Collectors.groupingBy(
                    QualityInspectionItem::getNormsId,
                    Collectors.reducing(
                        CommonNumConstants.NUM_ZERO.toString(),
                        QualityInspectionItem::getOperNumber,
                        (sum, operNumber) -> CalculationUtil.add(
                            ErpConstants.NUM_AFTER_DOT,
                            StrUtil.isEmpty(sum) ? CommonNumConstants.NUM_ZERO.toString() : sum,
                            StrUtil.isEmpty(operNumber) ? CommonNumConstants.NUM_ZERO.toString() : operNumber
                        )
                    )
                ));
        }
        return cn.hutool.core.map.MapUtil.newHashMap();
    }

    @Override
    public void editPutState(String id, Integer putState) {
        UpdateWrapper<QualityInspection> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(QualityInspection::getPutState), putState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void editReturnState(String id, Integer returnState) {
        UpdateWrapper<QualityInspection> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(QualityInspection::getReturnState), returnState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void editExchangesState(String id, Integer returnState) {
        UpdateWrapper<QualityInspection> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(QualityInspection::getExchangesState), returnState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void setQualityInspectionMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> ids = beans.stream().filter(bean -> !MapUtil.checkKeyIsNull(bean, idKey))
            .map(bean -> bean.get(idKey).toString()).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        QueryWrapper<QualityInspection> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        List<QualityInspection> qualityInspectionList = list(queryWrapper);
        Map<String, QualityInspection> qualityInspectionMap = qualityInspectionList.stream()
            .collect(Collectors.toMap(QualityInspection::getId, bean -> bean));
        for (Map<String, Object> bean : beans) {
            if (!MapUtil.checkKeyIsNull(bean, idKey)) {
                QualityInspection entity = qualityInspectionMap.get(bean.get(idKey).toString());
                if (ObjectUtil.isEmpty(entity)) {
                    continue;
                }
                bean.put(mationKey, entity);
            }
        }
    }

    @Override
    public void queryQualityInspectionTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        QualityInspection qualityInspection = selectById(id);
        Map<String, String> normsNum = purchasePutService.calcMaterialNormsNumByFromId(id);
        qualityInspection.getQualityInspectionItemList().forEach(qualityInspectionItem -> {
            String qualifiedAndConcession = CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, qualityInspectionItem.getQualifiedNumber(), qualityInspectionItem.getConcessionNumber());
            String normsNumValue = normsNum.containsKey(qualityInspectionItem.getNormsId())
                ? normsNum.get(qualityInspectionItem.getNormsId())
                : CommonNumConstants.NUM_ZERO.toString();
            String surplusNum = CalculationUtil.subtract(qualifiedAndConcession, normsNumValue, ErpConstants.NUM_AFTER_DOT);
            // 设置未下达采购入库单的商品数量
            qualityInspectionItem.setOperNumber(surplusNum);
        });
        // 过滤掉数量为0的进行生成采购入库单
        qualityInspection.setQualityInspectionItemList(qualityInspection.getQualityInspectionItemList().stream()
            .filter(qualityInspectionItem -> CalculationUtil.compareTo(qualityInspectionItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        // 供应商
        supplierService.setDataMation(qualityInspection, QualityInspection::getHolderId);
        outputObject.setBean(qualityInspection);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void qualityInspectionToPurchasePut(InputObject inputObject, OutputObject outputObject) {
        PurchasePut purchasePut = inputObject.getParams(PurchasePut.class);
        // 获取质检单状态
        QualityInspection qualityInspection = selectById(purchasePut.getId());
        if (ObjectUtil.isEmpty(qualityInspection)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以进行采购入库单
        if (FlowableStateEnum.PASS.getKey().equals(qualityInspection.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            purchasePut.setFromId(purchasePut.getId());
            purchasePut.setFromTypeId(PurchasePutFromType.QUALITY_INSPECTION.getKey());
            purchasePut.setId(StrUtil.EMPTY);
            purchasePutService.createEntity(purchasePut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达质检单.");
        }
    }

    @Override
    public void queryQualityInspectionTransReturnById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        QualityInspection qualityInspection = selectById(id);
        Map<String, String> normsNum = purchaseReturnsService.calcMaterialNormsNumByFromId(id);
        qualityInspection.getQualityInspectionItemList().forEach(qualityInspectionItem -> {
            // 退还数量 - 已退货数量
            String normsNumValue = normsNum.containsKey(qualityInspectionItem.getNormsId())
                ? normsNum.get(qualityInspectionItem.getNormsId())
                : CommonNumConstants.NUM_ZERO.toString();
            String surplusNum = CalculationUtil.subtract(qualityInspectionItem.getReturnNumber(), normsNumValue, ErpConstants.NUM_AFTER_DOT);
            // 设置未下达采购退货单的商品数量
            qualityInspectionItem.setOperNumber(surplusNum);
        });
        // 过滤掉数量为0的进行生成采购退货单
        qualityInspection.setQualityInspectionItemList(qualityInspection.getQualityInspectionItemList().stream()
            .filter(qualityInspectionItem -> CalculationUtil.compareTo(qualityInspectionItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        // 供应商
        supplierService.setDataMation(qualityInspection, QualityInspection::getHolderId);
        outputObject.setBean(qualityInspection);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void qualityInspectionToPurchaseReturn(InputObject inputObject, OutputObject outputObject) {
        PurchaseReturn purchaseReturn = inputObject.getParams(PurchaseReturn.class);
        // 获取质检单状态
        QualityInspection qualityInspection = selectById(purchaseReturn.getId());
        if (ObjectUtil.isEmpty(qualityInspection)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以进行采购退货单
        if (FlowableStateEnum.PASS.getKey().equals(qualityInspection.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            purchaseReturn.setFromId(purchaseReturn.getId());
            purchaseReturn.setFromTypeId(PurchaseReturnsFromType.QUALITY_INSPECTION.getKey());
            purchaseReturn.setId(StrUtil.EMPTY);
            purchaseReturnsService.createEntity(purchaseReturn, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达质检单.");
        }
    }

    @Override
    public void queryQualityInspectionTransExchangesById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        QualityInspection qualityInspection = selectById(id);
        Map<String, String> normsNum = purchaseExchangesService.calcMaterialNormsNumByFromId(id);
        qualityInspection.getQualityInspectionItemList().forEach(qualityInspectionItem -> {
            // 换货数量 - 已换货数量
            String normsNumValue = normsNum.containsKey(qualityInspectionItem.getNormsId())
                ? normsNum.get(qualityInspectionItem.getNormsId())
                : CommonNumConstants.NUM_ZERO.toString();
            String surplusNum = CalculationUtil.subtract(qualityInspectionItem.getExchangesNumber(), normsNumValue, ErpConstants.NUM_AFTER_DOT);
            // 设置未下达采购换货单的商品数量
            qualityInspectionItem.setOperNumber(surplusNum);
        });
        // 过滤掉数量为0的进行生成采购换货单
        qualityInspection.setQualityInspectionItemList(qualityInspection.getQualityInspectionItemList().stream()
            .filter(qualityInspectionItem -> CalculationUtil.compareTo(qualityInspectionItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0)
            .collect(Collectors.toList()));
        // 供应商
        supplierService.setDataMation(qualityInspection, QualityInspection::getHolderId);
        outputObject.setBean(qualityInspection);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void qualityInspectionToPurchaseExchanges(InputObject inputObject, OutputObject outputObject) {
        PurchaseExchange purchaseExchange = inputObject.getParams(PurchaseExchange.class);
        // 获取质检单状态
        QualityInspection qualityInspection = selectById(purchaseExchange.getId());
        if (ObjectUtil.isEmpty(qualityInspection)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以进行采购退货单
        if (FlowableStateEnum.PASS.getKey().equals(qualityInspection.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            purchaseExchange.setFromId(purchaseExchange.getId());
            purchaseExchange.setFromTypeId(PurchaseExchangesFromType.QUALITY_INSPECTION.getKey());
            purchaseExchange.setId(StrUtil.EMPTY);
            purchaseExchangesService.createEntity(purchaseExchange, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法下达质检单.");
        }
    }
}
