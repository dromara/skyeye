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
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.assets.classenum.AssetReportState;
import com.skyeye.eve.assets.classenum.PurchaseOrderStateEnum;
import com.skyeye.eve.assets.dao.AssetPurchasePutDao;
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
 * @ClassName: AssetPurchasePutServiceImpl
 * @Description: 资产采购入库服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/19 19:25
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "资产入库", groupName = "资产模块", flowable = true)
public class AssetPurchasePutServiceImpl extends SkyeyeBusinessServiceImpl<AssetPurchasePutDao, AssetPurchasePut> implements AssetPurchasePutService {

    @Autowired
    private AssetPurchaseLinkService assetPurchaseLinkService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AssetPurchaseService assetPurchaseService;

    @Autowired
    private AssetPurchaseReturnService assetPurchaseReturnService;

    @Autowired
    private AssetReportService assetReportService;

    @Autowired
    private BizAcctEventService bizAcctEventService;

    @Override
    public QueryWrapper<AssetPurchasePut> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AssetPurchasePut> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssetPurchasePut::getIdKey), getServiceClassName());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        assetPurchaseService.setOrderMationByFromId(beans, "fromId", "fromMation");
        return beans;
    }

    @Override
    public void validatorEntity(AssetPurchasePut entity) {
        checkOrderItem(entity.getPurchaseLinks());
        checkMaterialNorms(entity, false);
        getTotalPrice(entity);
        // 校验资产编号信息
        checkAndEditAssetBarCodeDepotMaiton(entity, false);
    }

    @Override
    public void createPrepose(AssetPurchasePut entity) {
        entity.setIdKey(getServiceClassName());
        super.createPrepose(entity);
    }

    @Override
    public void writePostpose(AssetPurchasePut entity, String userId) {
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

    private void getTotalPrice(AssetPurchasePut entity) {
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
            // 计算条形码
            List<String> normsCodeList = Arrays.asList(purchaseLink.getNormsCode().split("\n")).stream()
                .filter(str -> StrUtil.isNotEmpty(str)).distinct().collect(Collectors.toList());
            if (purchaseLink.getPurchaseNum() != normsCodeList.size()) {
                throw new CustomException(
                    String.format(Locale.ROOT, "产品【%s】的条形码数量与明细数量不一致，请确认", asset.getName()));
            }
            purchaseLink.setNormsCodeList(normsCodeList);
        }
        entity.setAllPrice(totalPrice);
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        assetPurchaseLinkService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public AssetPurchasePut getDataFromDb(String id) {
        AssetPurchasePut assetPurchase = super.getDataFromDb(id);
        List<AssetPurchaseLink> assetPurchaseLinks = assetPurchaseLinkService.selectByPId(assetPurchase.getId());
        assetPurchase.setPurchaseLinks(assetPurchaseLinks);
        return assetPurchase;
    }

    @Override
    public AssetPurchasePut selectById(String id) {
        AssetPurchasePut assetPurchasePut = super.selectById(id);
        // 获取资产信息
        assetService.setDataMation(assetPurchasePut.getPurchaseLinks(), AssetPurchaseLink::getAssetId);
        assetPurchasePut.getPurchaseLinks().forEach(bean -> {
            bean.setStateName(FlowableChildStateEnum.getStateName(bean.getState()));
        });
        // 资产来源信息
        iSysDictDataService.setDataMation(assetPurchasePut.getPurchaseLinks(), AssetPurchaseLink::getFromId);
        assetPurchasePut.setStateName(FlowableStateEnum.getStateName(assetPurchasePut.getState()));
        iAuthUserService.setName(assetPurchasePut, "createId", "createName");
        assetPurchaseService.setDataMation(assetPurchasePut, AssetPurchasePut::getFromId);
        return assetPurchasePut;
    }

    @Override
    public void revokePostpose(AssetPurchasePut entity) {
        super.revokePostpose(entity);
        assetPurchaseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    public void approvalEndIsSuccess(AssetPurchasePut entity) {
        entity = selectById(entity.getId());
        // 修改来源单据信息
        checkMaterialNorms(entity, true);
        // 修改条形码编码信息
        checkAndEditAssetBarCodeDepotMaiton(entity, true);
        // 修改子单据状态
        assetPurchaseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
        // 业财一体：资产入库审批通过后推固定资产购入
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", BizAcctEventType.FIXED_ASSET_PURCHASE.getKey());
            event.put("sourceType", "ADM_ASSET_PURCHASE_PUT");
            event.put("sourceId", entity.getId());
            event.put("sourceNo", entity.getOddNumber());
            event.put("amount", StrUtil.blankToDefault(entity.getAllPrice(), "0"));
            event.put("summary", "固定资产购入-" + entity.getOddNumber());
            bizAcctEventService.acceptEvent(event);
        } catch (Exception ignored) {
            // 失败事件可在业财事件台重试
        }
    }

    @Override
    public void approvalEndIsFailed(AssetPurchasePut entity) {
        assetPurchaseLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

    private void checkAndEditAssetBarCodeDepotMaiton(AssetPurchasePut entity, boolean saveData) {
        // 获取所有的条形码信息
        List<String> normsCodeList = entity.getPurchaseLinks().stream()
            .filter(bean -> CollectionUtil.isNotEmpty(bean.getNormsCodeList()))
            .flatMap(bean -> bean.getNormsCodeList().stream()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(normsCodeList)) {
            throw new CustomException("单据信息中条形码为空.");
        }
        // 获取所有未入库的编码
        List<AssetReport> assetReportList = assetReportService.queryAssetReportListByCodeNum(normsCodeList, false);
        List<String> inSqlNormsCodeList = assetReportList.stream().map(AssetReport::getAssetNum).collect(Collectors.toList());
        // 求差集(在入参中有，但是在数据库中不包含的条形码信息)
        List<String> diffList = normsCodeList.stream()
            .filter(num -> !inSqlNormsCodeList.contains(num)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(diffList)) {
            throw new CustomException(
                String.format(Locale.ROOT, "编码【%s】不存在或已被使用，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
        }

        Map<String, AssetPurchaseLink> purchaseLinkMap = entity.getPurchaseLinks().stream()
            .collect(Collectors.toMap(AssetPurchaseLink::getAssetId, bean -> bean));
        assetReportList.forEach(assetReport -> {
            AssetPurchaseLink assetPurchaseLink = purchaseLinkMap.get(assetReport.getAssetId());
            if (assetPurchaseLink == null || !StrUtil.equals(assetReport.getAssetId(), assetPurchaseLink.getAssetId())) {
                throw new CustomException(String.format(Locale.ROOT, "条形码【%s】与资产不匹配，请确认", assetReport.getAssetNum()));
            }
        });
        if (saveData) {
            // 批量修改条形码信息
            String warehousingTime = DateUtil.getTimeAndToString();
            assetReportList.forEach(assetReport -> {
                AssetPurchaseLink assetPurchaseLink = purchaseLinkMap.get(assetReport.getAssetId());
                assetReport.setUnitPrice(assetPurchaseLink.getUnitPrice());
                assetReport.setFromId(assetPurchaseLink.getFromId());
                if (StrUtil.isNotEmpty(entity.getFromId())) {
                    assetReport.setPurchaseId(entity.getFromId());
                    assetReport.setPurchaseTime(entity.getFromMation().get("createTime").toString());
                }
                assetReport.setWarehousingId(entity.getId());
                assetReport.setWarehousingTime(warehousingTime);
                assetReport.setState(AssetReportState.NORMAL.getKey());
                // 月结折旧默认政策：60月、残值率5%、次月起提
                if (assetReport.getUsefulLifeMonths() == null || assetReport.getUsefulLifeMonths() <= 0) {
                    assetReport.setUsefulLifeMonths(60);
                }
                if (StrUtil.isBlank(assetReport.getResidualRate())) {
                    assetReport.setResidualRate("5");
                }
                if (StrUtil.isBlank(assetReport.getAccumDeprecAmount())) {
                    assetReport.setAccumDeprecAmount("0");
                }
                if (StrUtil.isBlank(assetReport.getDeprecStartMonth()) && warehousingTime.length() >= 7) {
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DateUtil.YYYY_MM);
                        java.util.Calendar cal = java.util.Calendar.getInstance();
                        cal.setTime(sdf.parse(warehousingTime.substring(0, 7)));
                        cal.add(java.util.Calendar.MONTH, 1);
                        assetReport.setDeprecStartMonth(sdf.format(cal.getTime()));
                    } catch (Exception ignored) {
                    }
                }
            });
            assetReportService.updateEntity(assetReportList, entity.getCreateId());
        }
    }

    @Override
    public Map<String, Integer> calcAssetNumByFromId(String fromId) {
        QueryWrapper<AssetPurchasePut> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(CommonConstants.ID);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssetPurchasePut::getFromId), fromId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssetPurchasePut::getIdKey), getServiceClassName());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssetPurchasePut::getState), FlowableStateEnum.PASS.getKey());
        List<AssetPurchasePut> purchasePutList = list(queryWrapper);
        List<String> ids = purchasePutList.stream().map(AssetPurchasePut::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        List<AssetPurchaseLink> assetPurchaseLinkList = assetPurchaseLinkService.queryAssetPurchaseLinkByPIds(ids);
        Map<String, Integer> collect = assetPurchaseLinkList.stream()
            .collect(Collectors.groupingBy(AssetPurchaseLink::getAssetId, Collectors.summingInt(AssetPurchaseLink::getPurchaseNum)));
        return collect;
    }

    private void checkMaterialNorms(AssetPurchasePut entity, boolean setData) {
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
        // 获取已经下达采购退货单的商品信息
        Map<String, Integer> returnExecuteNum = assetPurchaseReturnService.calcAssetNumByFromId(entity.getFromId());
        assetPurchase.getPurchaseLinks().forEach(purchaseLink -> {
            // 来源单据的商品数量 - 当前单据的商品数量 - 已经入库的商品数量 - 已经退货的商品数量
            Integer surplusNum = purchaseLink.getPurchaseNum()
                - (orderNormsNum.containsKey(purchaseLink.getAssetId()) ? orderNormsNum.get(purchaseLink.getAssetId()) : 0)
                - (executeNum.containsKey(purchaseLink.getAssetId()) ? executeNum.get(purchaseLink.getAssetId()) : 0)
                - (returnExecuteNum.containsKey(purchaseLink.getAssetId()) ? returnExecuteNum.get(purchaseLink.getAssetId()) : 0);
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
