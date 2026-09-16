/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.classenum.ErpOrderStateEnum;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.classenum.DepotPutState;
import com.skyeye.depot.dao.DepotPutDao;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.depot.service.DepotOutPutRecordService;
import com.skyeye.depot.service.DepotPutService;
import com.skyeye.entity.ErpOrderCommon;
import com.skyeye.entity.ErpOrderHead;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.holder.classenum.HolderNormsChildState;
import com.skyeye.holder.service.HolderNormsChildService;
import com.skyeye.machin.service.MachinPutService;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.material.classenum.MaterialItemCode;
import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.material.classenum.MaterialNormsCodeType;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.entity.MaterialNormsCode;
import com.skyeye.material.service.MaterialService;
import com.skyeye.other.service.OtherWareHousService;
import com.skyeye.pick.service.ReturnPutService;
import com.skyeye.pickconfirm.service.ConfirmReturnService;
import com.skyeye.product.service.ProductReturnInStockService;
import com.skyeye.purchase.service.PurchasePutService;
import com.skyeye.retail.service.RetailReturnsService;
import com.skyeye.seal.service.SalesExchangesService;
import com.skyeye.seal.service.SalesReturnsService;
import com.skyeye.shop.service.ShopConfirmReturnService;
import com.skyeye.shop.service.ShopReturnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: DepotPutServiceImpl
 * @Description: 仓库入库单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/26 8:53
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "仓库入库单", groupName = "仓库出入库", flowable = true, orderApproval = true)
public class DepotPutServiceImpl extends SkyeyeErpOrderServiceImpl<DepotPutDao, DepotPut> implements DepotPutService {

    private static final List<String> PUT_ORDER_TYPE = DepotPutFromType.getAllIdKeys();

    @Autowired
    private HolderNormsChildService holderNormsChildService;

    @Autowired
    private PurchasePutService purchasePutService;

    @Autowired
    private SalesReturnsService salesReturnsService;

    @Autowired
    private RetailReturnsService retailReturnsService;

    @Autowired
    private OtherWareHousService otherWareHousService;

    @Autowired
    private ReturnPutService returnPutService;

    @Autowired
    private ConfirmReturnService confirmReturnService;

    @Autowired
    private MachinPutService machinPutService;

    @Autowired
    private ShopReturnsService shopReturnsService;

    @Autowired
    private ShopConfirmReturnService shopConfirmReturnService;

    @Autowired
    private ProductReturnInStockService productReturnInStockService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private DepotOutPutRecordService depotOutPutRecordService;

    @Autowired
    private SalesExchangesService salesExchangesService;

    @Override
    public QueryWrapper<DepotPut> getQueryWrapper(CommonPageInfo commonPageInfo) {
        if (StrUtil.isEmpty(commonPageInfo.getType())) {
            throw new CustomException("type不能为空");
        }
        QueryWrapper<DepotPut> queryWrapper;
        if (StrUtil.equals(commonPageInfo.getType(), "AllWait")) {
            // 所有待入库的单据信息
            queryWrapper = super.getGrandFatherQueryWrapper(commonPageInfo);
            queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderCommon::getIdKey), PUT_ORDER_TYPE);
            List<Integer> otherStateList = Arrays.asList(DepotPutState.NEED_PUT.getKey(), DepotPutState.PARTIAL_PUT.getKey());
            queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderCommon::getOtherState), otherStateList);
            // 只查询审批通过，部分完成，已完成的
            List<String> stateList = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey(),
                ErpOrderStateEnum.COMPLETED.getKey()});
            queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderCommon::getState), stateList);
        } else if (StrUtil.equals(commonPageInfo.getType(), "AllComplate")) {
            // 所有已入库的单据信息
            queryWrapper = super.getGrandFatherQueryWrapper(commonPageInfo);
            queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderCommon::getIdKey), PUT_ORDER_TYPE);
            queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderCommon::getOtherState), DepotPutState.COMPLATE_PUT.getKey());
        } else {
            // 查询仓库入库单
            queryWrapper = super.getQueryWrapper(commonPageInfo);
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (!StrUtil.equals(commonPageInfo.getType(), "AllWait") && !StrUtil.equals(commonPageInfo.getType(), "AllComplate")) {
            // 查询仓库入库单
            // 采购入库单
            purchasePutService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 销售退货单
            salesReturnsService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 零售退货单
            retailReturnsService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 其他入库单
            otherWareHousService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 退料入库单
            returnPutService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 物料退货单
            confirmReturnService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 加工入库单
            machinPutService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 门店退货单
            shopReturnsService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 门店物料退货单
            shopConfirmReturnService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 销售换货单
            salesExchangesService.setOrderMationByFromId(beans, "fromId", "fromMation");
        }
        return beans;
    }

    @Override
    public void validatorEntity(DepotPut entity) {
        String fromTypeIdKey;
        if (StrUtil.isNotEmpty(entity.getId())) {
            DepotPut depotPut = selectById(entity.getId());
            entity.setFromTypeId(depotPut.getFromTypeId());
            entity.setFromId(depotPut.getFromId());
            fromTypeIdKey = DepotPutFromType.getItemIdKey(depotPut.getFromTypeId());
        } else {
            fromTypeIdKey = DepotPutFromType.getItemIdKey(entity.getFromTypeId());
        }
        checkMaterialNorms(entity, fromTypeIdKey, false);
        checkNormsCodeAndWarehousing(entity, true);
    }

    @Override
    public void createPrepose(DepotPut entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.PUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public void updatePrepose(DepotPut entity) {
        super.updatePrepose(entity);
        // 查询数据库里的值
        DepotPut oldDepotPut = selectById(entity.getId());
        if (oldDepotPut.getFromTypeId() == DepotPutFromType.CONFIRM_RETURN.getKey()) {
            // 物料退货单
            entity.setFarmId(oldDepotPut.getFarmId());
            entity.setDepartmentId(oldDepotPut.getDepartmentId());
            entity.setSalesman(oldDepotPut.getSalesman());
        } else if (oldDepotPut.getFromTypeId() == DepotPutFromType.SHOP_RETURNS.getKey()
            || oldDepotPut.getFromTypeId() == DepotPutFromType.SHOP_CONFIRM_RETURNS.getKey()) {
            // 门店退货单/门店物料退货单
            entity.setStoreId(oldDepotPut.getStoreId());
        }
    }

    @Override
    public void writePostpose(DepotPut entity, String userId) {
        // 保存单据子表关联的条形码编号信息
        super.saveErpOrderItemCode(entity);
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        // 删除关联的编码信息
        super.deleteErpOrderItemCodeById(id);
    }

    @Override
    public DepotPut getDataFromDb(String id) {
        DepotPut depotPut = super.getDataFromDb(id);
        // 查询单据子表关联的条形码编号信息
        queryErpOrderItemCodeById(depotPut);
        return depotPut;
    }

    @Override
    public DepotPut selectById(String id) {
        DepotPut depotPut = super.selectById(id);
        if (depotPut.getFromTypeId() == DepotPutFromType.PURCHASE_PUT.getKey()) {
            // 采购入库单
            purchasePutService.setDataMation(depotPut, DepotPut::getFromId);
        } else if (depotPut.getFromTypeId() == DepotPutFromType.SEAL_RETURNS.getKey()) {
            // 销售退货单
            salesReturnsService.setDataMation(depotPut, DepotPut::getFromId);
        } else if (depotPut.getFromTypeId() == DepotPutFromType.RETAIL_RETURNS.getKey()) {
            // 零售退货单
            retailReturnsService.setDataMation(depotPut, DepotPut::getFromId);
        } else if (depotPut.getFromTypeId() == DepotPutFromType.OTHER_WARE_HOUS.getKey()) {
            // 其他入库单
            otherWareHousService.setDataMation(depotPut, DepotPut::getFromId);
        } else if (depotPut.getFromTypeId() == DepotPutFromType.RETURN_PUT.getKey()) {
            // 退料入库单
            returnPutService.setDataMation(depotPut, DepotPut::getFromId);
        } else if (depotPut.getFromTypeId() == DepotPutFromType.CONFIRM_RETURN.getKey()) {
            // 物料退货单
            confirmReturnService.setDataMation(depotPut, DepotPut::getFromId);
        } else if (depotPut.getFromTypeId() == DepotPutFromType.MACHIN_PUT.getKey()) {
            // 加工入库单
            machinPutService.setDataMation(depotPut, DepotPut::getFromId);
        } else if (depotPut.getFromTypeId() == DepotPutFromType.SHOP_RETURNS.getKey()) {
            // 门店退货单
            shopReturnsService.setDataMation(depotPut, DepotPut::getFromId);
        } else if (depotPut.getFromTypeId() == DepotPutFromType.SHOP_CONFIRM_RETURNS.getKey()) {
            // 门店物料退货单
            shopConfirmReturnService.setDataMation(depotPut, DepotPut::getFromId);
        } else if (depotPut.getFromTypeId() == DepotPutFromType.SALES_EXCHANGES.getKey()) {
            // 销售换货单
            salesExchangesService.setDataMation(depotPut, DepotPut::getFromId);
        }
        return depotPut;
    }

    @Override
    public void approvalEndIsSuccess(DepotPut entity) {
        entity = selectById(entity.getId());
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        // 修改归还入库单的状态
        productReturnInStockService.updateOtherState(entity.getFromId());
        String fromTypeIdKey = DepotPutFromType.getItemIdKey(entity.getFromTypeId());
        // 修改来源单据信息
        checkMaterialNorms(entity, fromTypeIdKey, true);
        // 需要入库
        // 校验并修改条形码信息
        List<String> normsCodeList = checkNormsCodeAndWarehousing(entity, false);
        if (entity.getFromTypeId() == DepotPutFromType.SEAL_RETURNS.getKey()
            || entity.getFromTypeId() == DepotPutFromType.RETAIL_RETURNS.getKey()) {
            // 修改销售退货/零售退货单据的商品状态为退货状态
            holderNormsChildService.editHolderNormsChildState(entity.getHolderId(), normsCodeList, HolderNormsChildState.RETURN_OF_GOODS.getKey());
        } else if (entity.getFromTypeId() == DepotPutFromType.LOANIN.getKey()) {
            depotOutPutRecordService.writeOutPutRecord(entity, entity.getFromTypeId());
        }
        // 修改库存信息以及记录客户/供应商/会员关联的商品
        super.depotOutOrPutSuccess(entity.getHolderId(), entity.getHolderKey(), entity.getErpOrderItemList(), DepotPutOutType.PUT.getKey(),
            entity.getFromId(), fromTypeIdKey);
    }

    private void checkMaterialNorms(DepotPut entity, String fromTypeIdKey, boolean setData) {
        // 获取来源单据信息
        SkyeyeErpOrderService skyeyeErpOrderService = null;
        ErpOrderHead fromMation;
        if (entity.getFromTypeId() == DepotPutFromType.LOANIN.getKey()) {
            fromMation = productReturnInStockService.selectById(entity.getFromId());
        } else {
            try {
                Class<?> clazz = Class.forName(fromTypeIdKey);
                skyeyeErpOrderService = (SkyeyeErpOrderService) SpringUtils.getBean(clazz);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            fromMation = (ErpOrderHead) skyeyeErpOrderService.selectById(entity.getFromId());
        }
        // 当前入库单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId,
                item -> StrUtil.isEmpty(item.getOperNumber()) ? CommonNumConstants.NUM_ZERO.toString() : item.getOperNumber()));
        // 获取已经下达入库单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        super.checkFromOrderMaterialNorms(fromMation.getErpOrderItemList(), inSqlNormsId);
        // 来源单据的商品数量 - 当前单据的商品数量 - 已经入库的商品数量
        super.setOrCheckOperNumber(fromMation.getErpOrderItemList(), setData, orderNormsNum, executeNum);
        if (setData) {
            // 过滤掉剩余数量为0的商品
            List<ErpOrderItem> erpOrderItemList = fromMation.getErpOrderItemList().stream()
                .filter(erpOrderItem -> {
                    String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber())
                        ? CommonNumConstants.NUM_ZERO.toString()
                        : erpOrderItem.getOperNumber();
                    return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
                }).collect(Collectors.toList());
            if (entity.getFromTypeId().equals(DepotPutFromType.LOANIN.getKey())) {
                if (CollectionUtil.isEmpty(erpOrderItemList)) {
                    productReturnInStockService.editOtherState(entity.getFromId(), DepotPutState.COMPLATE_PUT.getKey());
                } else {
                    productReturnInStockService.editOtherState(entity.getFromId(), DepotPutState.PARTIAL_PUT.getKey());
                }

            } else {
                // 该来源单据的商品数量已经全部入库
                if (CollectionUtil.isEmpty(erpOrderItemList)) {
                    skyeyeErpOrderService.editOtherState(entity.getFromId(), DepotPutState.COMPLATE_PUT.getKey());
                } else {
                    skyeyeErpOrderService.editOtherState(entity.getFromId(), DepotPutState.PARTIAL_PUT.getKey());
                }
            }
        }
    }

    /**
     * 校验商品规格条形码与单据明细的参数是否匹配
     * 入库
     *
     * @param entity
     * @param onlyCheck 是否只进行校验，true：是；false：否
     */
    protected List<String> checkNormsCodeAndWarehousing(DepotPut entity, Boolean onlyCheck) {
        List<String> materialIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getMaterialId).distinct().collect(Collectors.toList());
        List<String> normsIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getNormsId).distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = materialService.selectMapByIds(materialIdList);
        Map<String, MaterialNorms> normsMap = materialNormsService.selectMapByIds(normsIdList);
        // 所有需要进行入库的条形码编码
        List<String> allNormsCodeList = new ArrayList<>();
        int allCodeNum = checkErpOrderItemDetail(entity, materialMap, normsMap, allNormsCodeList);
        if (CollectionUtil.isNotEmpty(allNormsCodeList)) {
            allNormsCodeList = allNormsCodeList.stream().distinct().collect(Collectors.toList());
            if (allCodeNum != allNormsCodeList.size()) {
                throw new CustomException("商品明细中存在相同的条形码编号，请确认");
            }
            // 从数据库查询未入库/已经出库的条形码信息
            List<MaterialNormsCode> materialNormsCodeList = materialNormsCodeService.queryMaterialNormsCodeByCodeNum(StrUtil.EMPTY, allNormsCodeList,
                MaterialNormsCodeInDepot.NOT_IN_STOCK.getKey(), MaterialNormsCodeInDepot.OUTBOUND.getKey());
            List<String> inSqlNormsCodeList = materialNormsCodeList.stream().map(MaterialNormsCode::getCodeNum).collect(Collectors.toList());
            // 获取所有前端传递过来的条形码信息，求差集(在入参中有，但是在数据库中不包含的条形码信息)
            List<String> diffList = allNormsCodeList.stream()
                .filter(num -> !inSqlNormsCodeList.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                throw new CustomException(
                    String.format(Locale.ROOT, "编码【%s】不存在或已被使用，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
            }
            // 判断条形码是否符合规范
            Map<String, MaterialNormsCode> materialNormsCodeMap = materialNormsCodeList.stream()
                .collect(Collectors.toMap(MaterialNormsCode::getCodeNum, bean -> bean));
            for (ErpOrderItem erpOrderItem : entity.getErpOrderItemList()) {
                Material material = materialMap.get(erpOrderItem.getMaterialId());
                if (material.getItemCode() == MaterialItemCode.ONE_ITEM_CODE.getKey()) {
                    // 一物一码
                    erpOrderItem.getNormsCodeList().forEach(normsCode -> {
                        MaterialNormsCode materialNormsCode = materialNormsCodeMap.get(normsCode);
                        if (!StrUtil.equals(materialNormsCode.getNormsId(), erpOrderItem.getNormsId())) {
                            throw new CustomException(String.format(Locale.ROOT, "条形码【%s】与商品规格不匹配，请确认", normsCode));
                        }
                    });
                }
            }
            if (!onlyCheck) {
                // 批量修改条形码信息
                Map<String, ErpOrderItem> erpOrderItemMap = entity.getErpOrderItemList().stream()
                    .collect(Collectors.toMap(bean -> String.format("%s-%s", bean.getMaterialId(), bean.getNormsId()), bean -> bean));
                String warehousingTime = DateUtil.getTimeAndToString();
                String serviceClassName = getServiceClassName();
                materialNormsCodeList.forEach(materialNormsCode -> {
                    String key = String.format("%s-%s", materialNormsCode.getMaterialId(), materialNormsCode.getNormsId());
                    ErpOrderItem erpOrderItem = erpOrderItemMap.get(key);
                    materialNormsCode.setInDepot(MaterialNormsCodeInDepot.WAREHOUSING.getKey());
                    materialNormsCode.setDepotId(erpOrderItem.getDepotId());
                    materialNormsCode.setWarehousingTime(warehousingTime);
                    materialNormsCode.setFromObjectId(entity.getId());
                    materialNormsCode.setFromObjectKey(serviceClassName);
                    materialNormsCode.setType(MaterialNormsCodeType.AUTHENTIC.getKey());
                    if (DepotPutFromType.SEAL_RETURNS.getKey() == entity.getFromTypeId()
                        || DepotPutFromType.RETAIL_RETURNS.getKey() == entity.getFromTypeId()) {
                        // 来源单据类型是零售退货/销售退货，则将商品规格类型修改为二手商品
                        materialNormsCode.setType(MaterialNormsCodeType.SECOND_HAND_GOODS.getKey());
                    }
                });
                materialNormsCodeService.updateEntity(materialNormsCodeList, StrUtil.EMPTY);
            }
        }
        return allNormsCodeList;
    }
}
