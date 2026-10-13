/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.assets.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.assets.classenum.AssetReportState;
import com.skyeye.eve.assets.classenum.PurchaseOrderStateEnum;
import com.skyeye.eve.assets.dao.AssetPurchaseReturnDao;
import com.skyeye.eve.assets.entity.*;
import com.skyeye.eve.assets.service.*;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.event.classenum.BizAcctEventType;
import com.skyeye.finance.event.service.BizAcctEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: AssetPurchaseReturnServiceImpl
 * @Description: 资产采购退货服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/19 20:32
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "采购退货", groupName = "资产模块", flowable = true)
public class AssetPurchaseReturnServiceImpl extends SkyeyeBusinessServiceImpl<AssetPurchaseReturnDao, AssetPurchaseReturn> implements AssetPurchaseReturnService {

    @Autowired
    private AssetPurchaseLinkService assetPurchaseLinkService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetPurchaseService assetPurchaseService;

    @Autowired
    private AssetPurchasePutService assetPurchasePutService;

    @Autowired
    private AssetReportService assetReportService;

    @Autowired
    private BizAcctEventService bizAcctEventService;

    @Override
    public QueryWrapper<AssetPurchaseReturn> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AssetPurchaseReturn> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssetPurchaseReturn::getIdKey), getServiceClassName());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        assetPurchaseService.setOrderMationByFromId(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public void validatorEntity(AssetPurchaseReturn entity) {
        checkOrderItem(entity.getPurchaseLinks());
        checkMaterialNorms(entity, false);
        getTotalPrice(entity);
        if (entity.getNeedDepot() == WhetherEnum.ENABLE_USING.getKey()) {
            // 修改条形码编码信息
            checkAndEditAssetBarCodeDepotMaiton(entity, false);
        }
    }

    @Override
    public void createPrepose(AssetPurchaseReturn entity) {
        entity.setIdKey(getServiceClassName());
        super.createPrepose(entity);
    }

    @Override
    public void writePostpose(AssetPurchaseReturn entity, String userId) {
        assetPurchaseLinkService.saveLinkList(entity.getId(), entity.getPurchaseLinks());
        super.writePostpose(entity, userId);
    }

    private void checkOrderItem(List<AssetPurchaseLink> assetPurchaseLinks) {
        List<String> assetIds = assetPurchaseLinks.stream()
            .map(AssetPurchaseLink::getAssetId).distinct()
            .collect(Collectors.toList());
        if (assetPurchaseLinks.size() != assetIds.size()) {
            throw new CustomException("单据中不允许出现同一资产信息");
        }
    }

    private void getTotalPrice(AssetPurchaseReturn entity) {
        String totalPrice = "0";
        List<String> assetIdList = entity.getPurchaseLinks().stream().map(AssetPurchaseLink::getAssetId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(assetIdList)) {
            return;
        }
        Map<String, Asset> assetMap = assetService.selectMapByIds(assetIdList);
        // 计算关联的资产总价
        for (AssetPurchaseLink purchaseLink : entity.getPurchaseLinks()) {
            Asset asset = assetMap.get(purchaseLink.getAssetId());
            // 计算总价
            String amountOfMoney = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, String.valueOf(purchaseLink.getPurchaseNum()), purchaseLink.getUnitPrice());
            purchaseLink.setAmountOfMoney(amountOfMoney);
            totalPrice = CalculationUtil.add(totalPrice, amountOfMoney);
            if (entity.getNeedDepot() == WhetherEnum.ENABLE_USING.getKey()) {
                // 计算条形码
                List<String> normsCodeList = Arrays.asList(purchaseLink.getNormsCode().split("\n")).stream()
                    .filter(str -> StrUtil.isNotEmpty(str)).distinct().collect(Collectors.toList());
                if (purchaseLink.getPurchaseNum() != normsCodeList.size()) {
                    throw new CustomException(
                        String.format(Locale.ROOT, "产品【%s】的条形码数量与明细数量不一致，请确认", asset.getName()));
                }
                purchaseLink.setNormsCodeList(normsCodeList);
            }
        }
        entity.setAllPrice(totalPrice);
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        assetPurchaseLinkService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public AssetPurchaseReturn getDataFromDb(String id) {
        AssetPurchaseReturn assetPurchaseReturn = super.getDataFromDb(id);
        List<AssetPurchaseLink> assetPurchaseLinks = assetPurchaseLinkService.selectByPId(assetPurchaseReturn.getId());
        assetPurchaseReturn.setPurchaseLinks(assetPurchaseLinks);
        return assetPurchaseReturn;
    }

    @Override
    public AssetPurchaseReturn selectById(String id) {
        AssetPurchaseReturn assetPurchaseReturn = super.selectById(id);
        // 获取资产信息
        assetService.setDataMation(assetPurchaseReturn.getPurchaseLinks(), AssetPurchaseLink::getAssetId);
        assetPurchaseReturn.getPurchaseLinks().forEach(bean -> {
            bean.setStateName(FlowableChildStateEnum.getStateName(bean.getState()));
        });
        // 资产来源信息
        iSysDictDataService.setDataMation(assetPurchaseReturn.getPurchaseLinks(), AssetPurchaseLink::getFromId);
        assetPurchaseReturn.setStateName(FlowableStateEnum.getStateName(assetPurchaseReturn.getState()));
        iAuthUserService.setName(assetPurchaseReturn, "createId", "createName");
        assetPurchaseService.setDataMation(assetPurchaseReturn, AssetPurchaseReturn::getFromId);
        return assetPurchaseReturn;
    }

    @Override
    public void revokePostpose(AssetPurchaseReturn entity) {
        super.revokePostpose(entity);
        assetPurchaseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    public void approvalEndIsSuccess(AssetPurchaseReturn entity) {
        entity = selectById(entity.getId());
        // 修改来源单据信息
        checkMaterialNorms(entity, true);
        if (entity.getNeedDepot() == WhetherEnum.ENABLE_USING.getKey()) {
            // 修改条形码编码信息
            checkAndEditAssetBarCodeDepotMaiton(entity, true);
        }
        // 修改子单据状态
        assetPurchaseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
        // 业财一体：资产退货审批通过后冲固定资产
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", BizAcctEventType.FIXED_ASSET_RETURN.getKey());
            event.put("sourceType", "ADM_ASSET_PURCHASE_RETURN");
            event.put("sourceId", entity.getId());
            event.put("sourceNo", entity.getOddNumber());
            event.put("amount", StrUtil.blankToDefault(entity.getAllPrice(), "0"));
            event.put("summary", "固定资产退货-" + entity.getOddNumber());
            bizAcctEventService.acceptEvent(event);
        } catch (Exception ignored) {
            // 失败事件可在业财事件台重试
        }
    }

    @Override
    public void approvalEndIsFailed(AssetPurchaseReturn entity) {
        assetPurchaseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

    private void checkAndEditAssetBarCodeDepotMaiton(AssetPurchaseReturn entity, boolean saveData) {
        // 获取所有的条形码信息
        List<String> normsCodeList = entity.getPurchaseLinks().stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getNormsCodeList()))
            .flatMap(bean -> bean.getNormsCodeList().stream()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(normsCodeList)) {
            throw new CustomException("单据信息中条形码为空.");
        }
        // 获取所有已入库的编码
        List<AssetReport> assetReportList = assetReportService.queryAssetReportListByCodeNum(normsCodeList, true);
        List<String> inSqlNormsCodeList = assetReportList.stream().map(AssetReport::getAssetNum).collect(Collectors.toList());
        // 求差集(在入参中有，但是在数据库中不包含的条形码信息)
        List<String> diffList = normsCodeList.stream()
            .filter(num -> !inSqlNormsCodeList.contains(num)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(diffList)) {
            throw new CustomException(
                String.format(Locale.ROOT, "编码【%s】未入库/不存在，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
        }

        Map<String, AssetPurchaseLink> returnLinkMap = entity.getPurchaseLinks().stream()
            .collect(Collectors.toMap(AssetPurchaseLink::getAssetId, bean -> bean));
        assetReportList.forEach(assetReport -> {
            AssetPurchaseLink assetReturnLink = returnLinkMap.get(assetReport.getAssetId());
            if (assetReturnLink == null || !StrUtil.equals(assetReport.getAssetId(), assetReturnLink.getAssetId())) {
                throw new CustomException(String.format(Locale.ROOT, "条形码【%s】与资产不匹配，请确认", assetReport.getAssetNum()));
            }
        });

        if (saveData) {
            // 批量修改条形码信息
            String returnTime = DateUtil.getTimeAndToString();
            assetReportList.forEach(assetReport -> {
                assetReport.setReturnId(entity.getId());
                assetReport.setReturnTime(returnTime);
                assetReport.setState(AssetReportState.RETURN.getKey());
            });
            assetReportService.updateEntity(assetReportList, entity.getCreateId());
        }
    }

    @Override
    public Map<String, Integer> calcAssetNumByFromId(String fromId) {
        QueryWrapper<AssetPurchaseReturn> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(CommonConstants.ID);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssetPurchaseReturn::getFromId), fromId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssetPurchaseReturn::getIdKey), getServiceClassName());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssetPurchaseReturn::getState), FlowableStateEnum.PASS.getKey());
        List<AssetPurchaseReturn> purchaseReturnList = list(queryWrapper);
        List<String> ids = purchaseReturnList.stream().map(AssetPurchaseReturn::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        List<AssetPurchaseLink> assetPurchaseLinkList = assetPurchaseLinkService.queryAssetPurchaseLinkByPIds(ids);
        Map<String, Integer> collect = assetPurchaseLinkList.stream()
            .collect(Collectors.groupingBy(AssetPurchaseLink::getAssetId, Collectors.summingInt(AssetPurchaseLink::getPurchaseNum)));
        return collect;
    }

    private void checkMaterialNorms(AssetPurchaseReturn entity, boolean setData) {
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 当前采购入库单的商品数量
        Map<String, Integer> orderNormsNum = entity.getPurchaseLinks().stream()
            .collect(Collectors.toMap(AssetPurchaseLink::getAssetId, AssetPurchaseLink::getPurchaseNum));
        // 获取已经下达采购入库单的商品信息
        Map<String, Integer> executeNum = calcAssetNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        AssetPurchase assetPurchase = assetPurchaseService.selectById(entity.getFromId());
        if (CollectionUtil.isEmpty(assetPurchase.getPurchaseLinks())) {
            throw new CustomException("该采购订单下未包含商品.");
        }
        List<String> fromAssetIds = assetPurchase.getPurchaseLinks().stream()
            .map(AssetPurchaseLink::getAssetId).collect(Collectors.toList());
        // 求差集(采购订单中不包含的商品)
        List<String> diffList = inSqlNormsId.stream()
            .filter(num -> !fromAssetIds.contains(num)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(diffList)) {
            List<Asset> assetList = assetService.selectByIds(diffList.toArray(new String[]{}));
            List<String> assetNames = assetList.stream().map(Asset::getName).collect(Collectors.toList());
            throw new CustomException(String.format(Locale.ROOT, "该来源采购订单下未包含如下产品：【%s】.",
                Joiner.on(CommonCharConstants.COMMA_MARK).join(assetNames)));
        }
        // 获取已经下达采购入库单的商品信息
        Map<String, Integer> putExecuteNum = assetPurchasePutService.calcAssetNumByFromId(entity.getFromId());
        assetPurchase.getPurchaseLinks().forEach(purchaseLink -> {
            // 来源单据的商品数量 - 当前单据的商品数量 - 已经入库的商品数量 - 已经退货的商品数量
            Integer surplusNum = purchaseLink.getPurchaseNum()
                - (orderNormsNum.containsKey(purchaseLink.getAssetId()) ? orderNormsNum.get(purchaseLink.getAssetId()) : 0)
                - (executeNum.containsKey(purchaseLink.getAssetId()) ? executeNum.get(purchaseLink.getAssetId()) : 0)
                - (putExecuteNum.containsKey(purchaseLink.getAssetId()) ? putExecuteNum.get(purchaseLink.getAssetId()) : 0);
            if (surplusNum < 0) {
                throw new CustomException("超出采购订单的商品数量.");
            }
            if (setData) {
                purchaseLink.setPurchaseNum(surplusNum);
            }
        });
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<AssetPurchaseLink> erpOrderItemList = assetPurchase.getPurchaseLinks().stream()
                .filter(purchaseLink -> purchaseLink.getPurchaseNum() > 0).collect(Collectors.toList());
            // 如果该采购订单的商品已经全部生成了采购入库单/采购退货单，那说明已经完成了采购订单的内容
            if (CollectionUtil.isEmpty(erpOrderItemList)) {
                assetPurchaseService.editStateById(assetPurchase.getId(), PurchaseOrderStateEnum.COMPLETED.getKey());
            } else {
                assetPurchaseService.editStateById(assetPurchase.getId(), PurchaseOrderStateEnum.PARTIALLY_COMPLETED.getKey());
            }
        }
    }

}
