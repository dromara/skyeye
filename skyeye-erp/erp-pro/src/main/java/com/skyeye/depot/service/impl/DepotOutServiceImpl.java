/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.classenum.ErpOrderStateEnum;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotOutFromType;
import com.skyeye.depot.classenum.DepotOutOtherState;
import com.skyeye.depot.classenum.DepotOutState;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.dao.DepotOutDao;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.depot.service.DepotOutPutRecordService;
import com.skyeye.depot.service.DepotOutService;
import com.skyeye.entity.ErpOrderCommon;
import com.skyeye.entity.ErpOrderHead;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.holder.classenum.HolderNormsChildState;
import com.skyeye.holder.service.HolderNormsChildService;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.material.classenum.MaterialItemCode;
import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.entity.MaterialNormsCode;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.other.service.OtherOutLetsService;
import com.skyeye.otherwise.service.OtherWiseOrderService;
import com.skyeye.pick.service.PatchOutLetService;
import com.skyeye.pick.service.RequisitionOutLetService;
import com.skyeye.pickconfirm.classenum.ConfirmFromType;
import com.skyeye.pickconfirm.entity.ConfirmPut;
import com.skyeye.pickconfirm.entity.ConfirmReturn;
import com.skyeye.pickconfirm.service.ConfirmPutService;
import com.skyeye.pickconfirm.service.ConfirmReturnService;
import com.skyeye.product.entity.ProductLeadOutStock;
import com.skyeye.product.service.ProductLeadOutStockService;
import com.skyeye.product.service.ProductLeadService;
import com.skyeye.purchase.service.PurchaseReturnsService;
import com.skyeye.sparepart.entity.EquipmentSparePartApplyChangeStock;
import com.skyeye.sparepart.entity.EquipmentSparePartApplyLink;
import com.skyeye.sparepart.service.EquipmentSparePartApplyService;
import com.skyeye.rest.sealservice.rest.IServiceApplyRest;
import com.skyeye.rest.shop.service.IShopStoreService;
import com.skyeye.retail.service.RetailOutLetService;
import com.skyeye.seal.service.SalesOutLetService;
import com.skyeye.shop.classenum.ShopConfirmFromType;
import com.skyeye.shop.entity.ShopConfirmPut;
import com.skyeye.shop.entity.ShopConfirmReturn;
import com.skyeye.shop.service.ShopConfirmPutService;
import com.skyeye.shop.service.ShopConfirmReturnService;
import com.skyeye.shop.service.ShopOutLetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: DepotOutServiceImpl
 * @Description: 仓库出库单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/26 9:00
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "仓库出库单", groupName = "仓库出入库", flowable = true)
public class DepotOutServiceImpl extends SkyeyeErpOrderServiceImpl<DepotOutDao, DepotOut> implements DepotOutService {

    private static final List<String> OUT_ORDER_TYPE = DepotOutFromType.getAllIdKeys();

    @Autowired
    private HolderNormsChildService holderNormsChildService;

    @Autowired
    private ConfirmPutService confirmPutService;

    @Autowired
    private ConfirmReturnService confirmReturnService;

    @Autowired
    private ShopConfirmPutService shopConfirmPutService;

    @Autowired
    private ShopConfirmReturnService shopConfirmReturnService;

    @Autowired
    private PurchaseReturnsService purchaseReturnsService;

    @Autowired
    private SalesOutLetService salesOutLetService;

    @Autowired
    private RetailOutLetService retailOutLetService;

    @Autowired
    private OtherOutLetsService otherOutLetsService;

    @Autowired
    private RequisitionOutLetService requisitionOutLetService;

    @Autowired
    private PatchOutLetService patchOutLetService;

    @Autowired
    private FarmService farmService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private IServiceApplyRest iServiceApplyRest;

    @Autowired
    private EquipmentSparePartApplyService equipmentSparePartApplyService;

    @Autowired
    private OtherWiseOrderService otherWiseOrderService;

    @Autowired
    private ShopOutLetsService shopOutLetsService;

    @Autowired
    private IShopStoreService iShopStoreService;

    @Autowired
    private ProductLeadOutStockService productLeadOutStockService;

    @Autowired
    private DepotOutPutRecordService depotOutPutRecordService;

    @Autowired
    private ProductLeadService productLeadService;


    @Override
    public QueryWrapper<DepotOut> getQueryWrapper(CommonPageInfo commonPageInfo) {
        if (StrUtil.isEmpty(commonPageInfo.getType())) {
            throw new CustomException("type不能为空");
        }
        QueryWrapper<DepotOut> queryWrapper;
        if (StrUtil.equals(commonPageInfo.getType(), "AllWait")) {
            // 所有待出库的单据信息
            queryWrapper = super.getGrandFatherQueryWrapper(commonPageInfo);
            queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderCommon::getIdKey), OUT_ORDER_TYPE);
            List<Integer> otherStateList = Arrays.asList(DepotOutState.NEED_OUT.getKey(), DepotOutState.PARTIAL_OUT.getKey());
            queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderCommon::getOtherState), otherStateList);
            // 只查询审批通过，部分完成，已完成的
            List<String> stateList = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey(),
                ErpOrderStateEnum.COMPLETED.getKey()});
            queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderCommon::getState), stateList);
        } else if (StrUtil.equals(commonPageInfo.getType(), "AllComplate")) {
            // 所有已出库的单据信息
            queryWrapper = super.getGrandFatherQueryWrapper(commonPageInfo);
            queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderCommon::getIdKey), OUT_ORDER_TYPE);
            queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderCommon::getOtherState), DepotOutState.COMPLATE_OUT.getKey());
        } else {
            // 查询仓库出库单
            queryWrapper = super.getQueryWrapper(commonPageInfo);
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (!StrUtil.equals(commonPageInfo.getType(), "AllWait") && !StrUtil.equals(commonPageInfo.getType(), "AllComplate")) {
            // 查询仓库出库单
            // 采购退货单
            purchaseReturnsService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 销售出库单
            salesOutLetService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 零售出库单
            retailOutLetService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 其他出库单
            otherOutLetsService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 领料出库单
            requisitionOutLetService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 补料出库单
            patchOutLetService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 配件申领单
            otherWiseOrderService.setOrderMationByFromId(beans, "fromId", "fromMation");
            // 门店申领单
            shopOutLetsService.setOrderMationByFromId(beans, "fromId", "fromMation");
        }
        return beans;
    }

    @Override
    public void validatorEntity(DepotOut entity) {
        String fromTypeIdKey;
        if (StrUtil.isNotEmpty(entity.getId())) {
            DepotOut depotOut = selectById(entity.getId());
            entity.setFromTypeId(depotOut.getFromTypeId());
            entity.setFromId(depotOut.getFromId());
            fromTypeIdKey = DepotOutFromType.getItemIdKey(depotOut.getFromTypeId());
        } else {
            fromTypeIdKey = DepotOutFromType.getItemIdKey(entity.getFromTypeId());
        }
        checkMaterialNorms(entity, fromTypeIdKey, false);
        checkNormsCodeAndOutbound(entity, true);
        if (StrUtil.equals(fromTypeIdKey, DepotOutFromType.REQUISITION_OUTLET.getIdKey())
            || StrUtil.equals(fromTypeIdKey, DepotOutFromType.PATCH_OUTLET.getIdKey())
            || StrUtil.equals(fromTypeIdKey, DepotOutFromType.SHOP_OUTLET.getIdKey())) {
            // 领料出库单/补料出库单/门店申领单
            entity.setOtherState(DepotOutOtherState.NEED_CONFIRM.getKey());
        }
    }

    @Override
    public void createPrepose(DepotOut entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.OUT.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public void updatePrepose(DepotOut entity) {
        super.updatePrepose(entity);
        // 查询数据库里的值
        DepotOut oldDepotOut = selectById(entity.getId());
        if (oldDepotOut.getFromTypeId() == DepotOutFromType.REQUISITION_OUTLET.getKey()
            || oldDepotOut.getFromTypeId() == DepotOutFromType.PATCH_OUTLET.getKey()) {
            // 领料出库单/补料出库单
            entity.setFarmId(oldDepotOut.getFarmId());
            entity.setDepartmentId(oldDepotOut.getDepartmentId());
            entity.setSalesman(oldDepotOut.getSalesman());
        } else if (oldDepotOut.getFromTypeId() == DepotOutFromType.SHOP_OUTLET.getKey()) {
            // 门店申领单
            entity.setStoreId(oldDepotOut.getStoreId());
            entity.setSalesman(oldDepotOut.getSalesman());
        }
    }

    @Override
    public void writePostpose(DepotOut entity, String userId) {
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
    public DepotOut getDataFromDb(String id) {
        DepotOut depotOut = super.getDataFromDb(id);
        // 查询单据子表关联的条形码编号信息
        queryErpOrderItemCodeById(depotOut);
        return depotOut;
    }

    @Override
    public DepotOut selectById(String id) {
        DepotOut depotOut = super.selectById(id);
        if (depotOut.getFromTypeId() == DepotOutFromType.PURCHASE_RETURNS.getKey()) {
            // 采购退货单
            purchaseReturnsService.setDataMation(depotOut, DepotOut::getFromId);
        } else if (depotOut.getFromTypeId() == DepotOutFromType.SEAL_OUTLET.getKey()) {
            // 销售出库单
            salesOutLetService.setDataMation(depotOut, DepotOut::getFromId);
        } else if (depotOut.getFromTypeId() == DepotOutFromType.RETAIL_OUTLET.getKey()) {
            // 零售出库单
            retailOutLetService.setDataMation(depotOut, DepotOut::getFromId);
        } else if (depotOut.getFromTypeId() == DepotOutFromType.OTHER_OUTLET.getKey()) {
            // 其他出库单
            otherOutLetsService.setDataMation(depotOut, DepotOut::getFromId);
        } else if (depotOut.getFromTypeId() == DepotOutFromType.REQUISITION_OUTLET.getKey()) {
            // 领料出库单
            requisitionOutLetService.setDataMation(depotOut, DepotOut::getFromId);
        } else if (depotOut.getFromTypeId() == DepotOutFromType.PATCH_OUTLET.getKey()) {
            // 补料出库单
            patchOutLetService.setDataMation(depotOut, DepotOut::getFromId);
        } else if (depotOut.getFromTypeId() == DepotOutFromType.SEAL_APPLY.getKey()) {
            // 配件申领单
            otherWiseOrderService.setDataMation(depotOut, DepotOut::getFromId);
        } else if (depotOut.getFromTypeId() == DepotOutFromType.EQUIPMENT_SPARE_PART_APPLY.getKey()) {
            // 设备备件申领单
            otherWiseOrderService.setDataMation(depotOut, DepotOut::getFromId);
        } else if (depotOut.getFromTypeId() == DepotOutFromType.SHOP_OUTLET.getKey()) {
            // 门店申领单
            shopOutLetsService.setDataMation(depotOut, DepotOut::getFromId);
        }
        return depotOut;
    }

    @Override
    public void approvalEndIsSuccess(DepotOut entity) {
        entity = selectById(entity.getId());
        if (StrUtil.isEmpty(entity.getFromId())) {
            return;
        }
        String fromTypeIdKey = DepotOutFromType.getItemIdKey(entity.getFromTypeId());
        // 修改来源单据信息
        boolean result = checkMaterialNorms(entity, fromTypeIdKey, true);
        // 需要出库
        // 校验并修改条形码信息
        List<String> normsCodeList = checkNormsCodeAndOutbound(entity, false);
        if (entity.getFromTypeId() == DepotOutFromType.PURCHASE_RETURNS.getKey()) {
            // 修改采购退货的商品状态为退货状态
            holderNormsChildService.editHolderNormsChildState(entity.getHolderId(), normsCodeList, HolderNormsChildState.RETURN_OF_GOODS.getKey());
        } else if (entity.getFromTypeId() == DepotOutFromType.SEAL_APPLY.getKey()) {
            // 配件申领单
            updateSealApply(entity, normsCodeList, result);
        } else if (entity.getFromTypeId() == DepotOutFromType.EQUIPMENT_SPARE_PART_APPLY.getKey()) {
            // 设备备件申领单
            updateEquipmentSparePartApply(entity, result);
        } else if (entity.getFromTypeId() == DepotOutFromType.LOANOUT.getKey()) {
            depotOutPutRecordService.writeOutPutRecord(entity, entity.getFromTypeId());
        }
        // 修改库存信息以及记录客户/供应商/会员关联的商品
        super.depotOutOrPutSuccess(entity.getHolderId(), entity.getHolderKey(), entity.getErpOrderItemList(), DepotPutOutType.OUT.getKey(),
            entity.getFromId(), fromTypeIdKey);
    }

    private void updateSealApply(DepotOut entity, List<String> normsCodeList, boolean result) {
        List<MaterialNormsCode> materialNormsCodeList = materialNormsCodeService.queryMaterialNormsCodeByCodeNum(StrUtil.EMPTY, normsCodeList);
        Map<String, List<MaterialNormsCode>> listMap = materialNormsCodeList.stream()
            .collect(Collectors.groupingBy(MaterialNormsCode::getNormsId));
        List<Map<String, Object>> applyLinkList = new ArrayList<>();
        for (ErpOrderItem erpOrderItem : entity.getErpOrderItemList()) {
            String normsId = erpOrderItem.getNormsId();
            Map<String, Object> applyLink = BeanUtil.beanToMap(erpOrderItem);
            List<String> list = listMap.containsKey(normsId) ? listMap.get(normsId).stream()
                .map(MaterialNormsCode::getCodeNum).collect(Collectors.toList()) : new ArrayList<>();
            applyLink.put("normsCodeList", list);
            applyLinkList.add(applyLink);
        }

        Map<String, Object> outNumMap = new HashMap<>();
        outNumMap.put("id", entity.getFromId());
        outNumMap.put("createId", entity.getCreateId());
        outNumMap.put("applyLinkList", applyLinkList);
        ExecuteFeignClient.get(() -> iServiceApplyRest.editSealApplyOutNum(outNumMap));

        // 修改配件申领单出库状态
        Map<String, Object> outStateMap = new HashMap<>();
        outStateMap.put("id", entity.getFromId());
        if (result) {
            outStateMap.put("otherState", DepotOutState.COMPLATE_OUT.getKey());
        } else {
            outStateMap.put("otherState", DepotOutState.PARTIAL_OUT.getKey());
        }
        ExecuteFeignClient.get(() -> iServiceApplyRest.editSealApplyOtherState(outStateMap));
    }

    private void updateEquipmentSparePartApply(DepotOut entity, boolean result) {
        List<EquipmentSparePartApplyLink> applyLinkList = new ArrayList<>();
        for (ErpOrderItem erpOrderItem : entity.getErpOrderItemList()) {
            applyLinkList.add(BeanUtil.copyProperties(erpOrderItem, EquipmentSparePartApplyLink.class));
        }
        EquipmentSparePartApplyChangeStock changeStock = new EquipmentSparePartApplyChangeStock();
        changeStock.setId(entity.getFromId());
        changeStock.setCreateId(entity.getCreateId());
        changeStock.setApplyLinkList(applyLinkList);
        equipmentSparePartApplyService.editApplyOutNum(changeStock);
        if (result) {
            equipmentSparePartApplyService.editApplyOtherState(entity.getFromId(), DepotOutState.COMPLATE_OUT.getKey());
        } else {
            equipmentSparePartApplyService.editApplyOtherState(entity.getFromId(), DepotOutState.PARTIAL_OUT.getKey());
        }
    }

    private boolean checkMaterialNorms(DepotOut entity, String fromTypeIdKey, boolean setData) {
        // 获取来源单据信息
        SkyeyeErpOrderService skyeyeErpOrderService = null;
        ErpOrderHead fromMation;
        if (entity.getFromTypeId() == DepotOutFromType.SEAL_APPLY.getKey()) {
            // 配件申领单
            fromMation = otherWiseOrderService.selectById(entity.getFromId());
        } else if (entity.getFromTypeId() == DepotOutFromType.EQUIPMENT_SPARE_PART_APPLY.getKey()) {
            // 设备备件申领单
            fromMation = otherWiseOrderService.selectById(entity.getFromId());
        } else if (entity.getFromTypeId() == DepotOutFromType.LOANOUT.getKey()) {
            // 借出出库单
            fromMation = productLeadOutStockService.selectById(entity.getFromId());
        } else {
            try {
                Class<?> clazz = Class.forName(fromTypeIdKey);
                skyeyeErpOrderService = (SkyeyeErpOrderService) SpringUtils.getBean(clazz);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            fromMation = (ErpOrderHead) skyeyeErpOrderService.selectById(entity.getFromId());
        }
        // 当前出库单的商品数量
        Map<String, String> orderNormsNum = entity.getErpOrderItemList().stream()
            .collect(Collectors.toMap(ErpOrderItem::getNormsId,
                item -> StrUtil.isEmpty(item.getOperNumber()) ? CommonNumConstants.NUM_ZERO.toString() : item.getOperNumber()));
        // 获取已经下达出库单的商品信息
        Map<String, String> executeNum = calcMaterialNormsNumByFromId(entity.getFromId());
        List<String> inSqlNormsId = new ArrayList<>(executeNum.keySet());
        super.checkFromOrderMaterialNorms(fromMation.getErpOrderItemList(), inSqlNormsId);

        // 来源单据的商品数量 - 当前单据的商品数量 - 已经出库的商品数量
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
            // 该来源单据的商品数量已经全部出库
            if (entity.getFromTypeId() == DepotOutFromType.SEAL_APPLY.getKey()) {
                // 配件申领单
                if (CollectionUtil.isEmpty(erpOrderItemList)) {
                    otherWiseOrderService.editOtherState(entity.getFromId(), DepotOutState.COMPLATE_OUT.getKey());
                    return true;
                } else {
                    otherWiseOrderService.editOtherState(entity.getFromId(), DepotOutState.PARTIAL_OUT.getKey());
                }
            } else if (entity.getFromTypeId() == DepotOutFromType.EQUIPMENT_SPARE_PART_APPLY.getKey()) {
                // 设备备件申领单
                if (CollectionUtil.isEmpty(erpOrderItemList)) {
                    equipmentSparePartApplyService.editApplyOtherState(entity.getFromId(), DepotOutState.COMPLATE_OUT.getKey());
                    return true;
                } else {
                    equipmentSparePartApplyService.editApplyOtherState(entity.getFromId(), DepotOutState.PARTIAL_OUT.getKey());
                }
            } else if (entity.getFromTypeId() == DepotOutFromType.LOANOUT.getKey()) {
                // 借出出库单
                if (CollectionUtil.isEmpty(erpOrderItemList)) {
                    productLeadOutStockService.editOtherState(entity.getFromId(), DepotOutState.COMPLATE_OUT.getKey());
                    return true;
                } else {
                    productLeadOutStockService.editOtherState(entity.getFromId(), DepotOutState.PARTIAL_OUT.getKey());
                }
            } else {
                if (CollectionUtil.isEmpty(erpOrderItemList)) {
                    skyeyeErpOrderService.editOtherState(entity.getFromId(), DepotOutState.COMPLATE_OUT.getKey());
                    return true;
                } else {
                    skyeyeErpOrderService.editOtherState(entity.getFromId(), DepotOutState.PARTIAL_OUT.getKey());
                }
            }
        }
        return false;
    }

    /**
     * 校验商品规格条形码与单据明细的参数是否匹配
     * 出库
     *
     * @param entity
     * @param onlyCheck 是否只进行校验，true：是；false：否
     */
    protected List<String> checkNormsCodeAndOutbound(DepotOut entity, Boolean onlyCheck) {
        // 设备备件申领单按数量出库，不走条形码逻辑，不影响其他来源类型的校验流程
        if (entity.getFromTypeId() == DepotOutFromType.EQUIPMENT_SPARE_PART_APPLY.getKey()) {
            return Collections.emptyList();
        }
        List<String> materialIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getMaterialId).distinct().collect(Collectors.toList());
        List<String> normsIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getNormsId).distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = materialService.selectMapByIds(materialIdList);
        Map<String, MaterialNorms> normsMap = materialNormsService.selectMapByIds(normsIdList);
        // 所有需要进行出库的条形码编码
        List<String> allNormsCodeList = new ArrayList<>();
        int allCodeNum = checkErpOrderItemDetail(entity, materialMap, normsMap, allNormsCodeList);
        if (CollectionUtil.isNotEmpty(allNormsCodeList)) {
            allNormsCodeList = allNormsCodeList.stream().distinct().collect(Collectors.toList());
            if (allCodeNum != allNormsCodeList.size()) {
                throw new CustomException("商品明细中存在相同的条形码编号，请确认");
            }
            // 从数据库查询入库状态的条形码信息
            List<MaterialNormsCode> materialNormsCodeList = materialNormsCodeService.queryMaterialNormsCodeByCodeNum(StrUtil.EMPTY, allNormsCodeList,
                MaterialNormsCodeInDepot.WAREHOUSING.getKey());
            List<String> inSqlNormsCodeList = materialNormsCodeList.stream().map(MaterialNormsCode::getCodeNum).collect(Collectors.toList());
            // 获取所有前端传递过来的条形码信息，求差集(在入参中有，但是在数据库中不包含的条形码信息)
            List<String> diffList = allNormsCodeList.stream()
                .filter(num -> !inSqlNormsCodeList.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                throw new CustomException(
                    String.format(Locale.ROOT, "编码【%s】不存在/未入库/已经出库，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
            }
            // 判断条形码是否就在出库仓库里面
            Map<String, MaterialNormsCode> materialNormsCodeMap = materialNormsCodeList.stream()
                .collect(Collectors.toMap(MaterialNormsCode::getCodeNum, bean -> bean));
            for (ErpOrderItem erpOrderItem : entity.getErpOrderItemList()) {
                Material material = materialMap.get(erpOrderItem.getMaterialId());
                if (material.getItemCode() == MaterialItemCode.ONE_ITEM_CODE.getKey()) {
                    // 一物一码
                    erpOrderItem.getNormsCodeList().forEach(normsCode -> {
                        MaterialNormsCode materialNormsCode = materialNormsCodeMap.get(normsCode);
                        if (!StrUtil.equals(materialNormsCode.getDepotId(), erpOrderItem.getDepotId())) {
                            throw new CustomException(
                                String.format(Locale.ROOT, "条形码【%s】不在指定出库仓库，请确认", normsCode));
                        }
                        if (!StrUtil.equals(materialNormsCode.getNormsId(), erpOrderItem.getNormsId())) {
                            throw new CustomException(String.format(Locale.ROOT, "条形码【%s】与商品规格不匹配，请确认", normsCode));
                        }
                    });
                }
            }
            if (!onlyCheck) {
                // 批量修改条形码信息
                String outboundTime = DateUtil.getTimeAndToString();
                String serviceClassName = getServiceClassName();
                materialNormsCodeList.forEach(materialNormsCode -> {
                    materialNormsCode.setInDepot(MaterialNormsCodeInDepot.OUTBOUND.getKey());
                    materialNormsCode.setOutboundTime(outboundTime);
                    materialNormsCode.setToObjectId(entity.getId());
                    materialNormsCode.setToObjectKey(serviceClassName);
                });
                materialNormsCodeService.updateEntity(materialNormsCodeList, StrUtil.EMPTY);
            }
        }
        return allNormsCodeList;
    }

    @Override
    public void queryNeedConfirmDepotOutList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DepotOut> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // 只查询来源类型是领料出库单/补料出库单
        List<Integer> fromTypeIdList = Arrays.asList(new Integer[]{DepotOutFromType.REQUISITION_OUTLET.getKey(), DepotOutFromType.PATCH_OUTLET.getKey()});
        queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderCommon::getFromTypeId), fromTypeIdList);
        // 只查询审批通过的单据
        queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderCommon::getState), FlowableStateEnum.PASS.getKey());
        if (StrUtil.equals(commonPageInfo.getType(), "waitConfirm")) {
            // 所有待确认的单据信息
            List<Integer> otherStateList = Arrays.asList(new Integer[]{DepotOutOtherState.NEED_CONFIRM.getKey(), DepotOutOtherState.PARTIAL_CONFIRM.getKey()});
            queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderCommon::getOtherState), otherStateList);
        } else if (StrUtil.equals(commonPageInfo.getType(), "confirm")) {
            // 所有已确认的单据信息
            queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderCommon::getOtherState), DepotOutOtherState.COMPLATE_CONFIRM.getKey());
        }
        List<DepotOut> list = list(queryWrapper);
        List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(list), null);
        // 领料出库单
        requisitionOutLetService.setOrderMationByFromId(beans, "fromId", "fromMation");
        // 补料出库单
        patchOutLetService.setOrderMationByFromId(beans, "fromId", "fromMation");
        // 车间
        farmService.setMationForMap(beans, "farmId", "farmMation");
        // 部门
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        // 业务员
        iAuthUserService.setMationForMap(beans, "salesman", "salesmanMation");
        iAuthUserService.setNameForMap(beans, "createId", "createName");
        iAuthUserService.setNameForMap(beans, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void queryDepotOutTransById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        DepotOut depotOut = selectById(id);
        // 车间
        farmService.setDataMation(depotOut, DepotOut::getFarmId);
        // 部门
        iDepmentService.setDataMation(depotOut, DepotOut::getDepartmentId);
        // 获取物料接收单的数量
        Map<String, String> normsNum = confirmPutService.calcMaterialNormsNumByFromId(id);
        // 获取物料退货单的数量
        Map<String, String> normsReturnsNum = confirmReturnService.calcMaterialNormsNumByFromId(id);
        // 设置未下达物料接收单/物料退货单的商品数量-----订单数量 - 物料接收单的数量 - 物料退货单的数量
        super.setOrCheckOperNumber(depotOut.getErpOrderItemList(), true, normsNum, normsReturnsNum);

        // 获取物料接收单的商品编码
        Map<String, List<String>> putCode = confirmPutService.calcMaterialNormsCodeByFromId(id);
        // 获取物料退货单的商品编码
        Map<String, List<String>> returnCode = confirmReturnService.calcMaterialNormsCodeByFromId(id);
        // 减去物料接收单/物料退货单的商品编码
        for (ErpOrderItem erpOrderItem : depotOut.getErpOrderItemList()) {
            List<String> putCodeList = putCode.get(erpOrderItem.getNormsId());
            List<String> returnCodeList = returnCode.get(erpOrderItem.getNormsId());
            if (CollectionUtil.isEmpty(putCodeList) && CollectionUtil.isEmpty(returnCodeList)) {
                continue;
            }
            // 求差集
            List<String> codeList = erpOrderItem.getNormsCodeList();
            if (CollectionUtil.isNotEmpty(putCodeList)) {
                codeList = codeList.stream()
                    .filter(num -> !putCodeList.contains(num)).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(returnCodeList)) {
                codeList = codeList.stream()
                    .filter(num -> !returnCodeList.contains(num)).collect(Collectors.toList());
            }
            erpOrderItem.setNormsCode(Joiner.on("\n").join(codeList));
            erpOrderItem.setNormsCodeList(codeList);
        }

        // 过滤掉数量为0的商品
        depotOut.setErpOrderItemList(depotOut.getErpOrderItemList().stream()
            .filter(erpOrderItem -> {
                String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : erpOrderItem.getOperNumber();
                return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
            }).collect(Collectors.toList()));
        outputObject.setBean(depotOut);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertDepotOutToTurnPut(InputObject inputObject, OutputObject outputObject) {
        ConfirmPut confirmPut = inputObject.getParams(ConfirmPut.class);
        DepotOut depotOut = selectById(confirmPut.getId());
        if (ObjectUtil.isEmpty(depotOut)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转
        if (FlowableStateEnum.PASS.getKey().equals(depotOut.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            confirmPut.setFromId(confirmPut.getId());
            confirmPut.setFromTypeId(ConfirmFromType.DEPOT_OUT.getKey());
            confirmPut.setDepartmentId(depotOut.getDepartmentId());
            confirmPut.setFarmId(depotOut.getFarmId());
            confirmPut.setSalesman(depotOut.getSalesman());
            confirmPut.setId(StrUtil.EMPTY);
            confirmPutService.createEntity(confirmPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法转物料接收单.");
        }
    }

    @Override
    public void insertDepotOutToSealsReturns(InputObject inputObject, OutputObject outputObject) {
        ConfirmReturn confirmReturn = inputObject.getParams(ConfirmReturn.class);
        DepotOut depotOut = selectById(confirmReturn.getId());
        if (ObjectUtil.isEmpty(depotOut)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转
        if (FlowableStateEnum.PASS.getKey().equals(depotOut.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            confirmReturn.setFromId(confirmReturn.getId());
            confirmReturn.setFromTypeId(ConfirmFromType.DEPOT_OUT.getKey());
            confirmReturn.setDepartmentId(depotOut.getDepartmentId());
            confirmReturn.setFarmId(depotOut.getFarmId());
            confirmReturn.setSalesman(depotOut.getSalesman());
            confirmReturn.setId(StrUtil.EMPTY);
            confirmReturnService.createEntity(confirmReturn, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法转物料退货单.");
        }
    }

    @Override
    public void queryNeedStoreConfirmDepotOutList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DepotOut> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // 只查询来源类型是门店申领单
        queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderCommon::getFromTypeId), DepotOutFromType.SHOP_OUTLET.getKey());
        // 只查询审批通过的单据
        queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderCommon::getState), FlowableStateEnum.PASS.getKey());
        if (StrUtil.isEmpty(commonPageInfo.getObjectId())) {
            commonPageInfo.setObjectId("-");
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderCommon::getStoreId), commonPageInfo.getObjectId());
        if (StrUtil.equals(commonPageInfo.getType(), "waitConfirm")) {
            // 所有待确认的单据信息
            List<Integer> otherStateList = Arrays.asList(new Integer[]{DepotOutOtherState.NEED_CONFIRM.getKey(), DepotOutOtherState.PARTIAL_CONFIRM.getKey()});
            queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderCommon::getOtherState), otherStateList);
        } else if (StrUtil.equals(commonPageInfo.getType(), "confirm")) {
            // 所有已确认的单据信息
            queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderCommon::getOtherState), DepotOutOtherState.COMPLATE_CONFIRM.getKey());
        }
        List<DepotOut> list = list(queryWrapper);
        List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(list), null);
        // 门店申领单
        shopOutLetsService.setOrderMationByFromId(beans, "fromId", "fromMation");
        // 门店
        iShopStoreService.setMationForMap(beans, "storeId", "storeMation");
        // 业务员
        iAuthUserService.setMationForMap(beans, "salesman", "salesmanMation");
        iAuthUserService.setNameForMap(beans, "createId", "createName");
        iAuthUserService.setNameForMap(beans, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void queryDepotOutTransStoreById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        DepotOut depotOut = selectById(id);
        // 门店
        iShopStoreService.setDataMation(depotOut, DepotOut::getStoreId);
        // 获取物料接收单的数量
        Map<String, String> normsNum = shopConfirmPutService.calcMaterialNormsNumByFromId(id);
        // 获取物料退货单的数量
        Map<String, String> normsReturnsNum = shopConfirmReturnService.calcMaterialNormsNumByFromId(id);
        // 设置未下达物料接收单/物料退货单的商品数量-----订单数量 - 物料接收单的数量 - 物料退货单的数量
        super.setOrCheckOperNumber(depotOut.getErpOrderItemList(), true, normsNum, normsReturnsNum);

        // 获取物料接收单的商品编码
        Map<String, List<String>> putCode = shopConfirmPutService.calcMaterialNormsCodeByFromId(id);
        // 获取物料退货单的商品编码
        Map<String, List<String>> returnCode = shopConfirmReturnService.calcMaterialNormsCodeByFromId(id);
        // 减去物料接收单/物料退货单的商品编码
        for (ErpOrderItem erpOrderItem : depotOut.getErpOrderItemList()) {
            List<String> putCodeList = putCode.get(erpOrderItem.getNormsId());
            List<String> returnCodeList = returnCode.get(erpOrderItem.getNormsId());
            if (CollectionUtil.isEmpty(putCodeList) && CollectionUtil.isEmpty(returnCodeList)) {
                continue;
            }
            // 求差集
            List<String> codeList = erpOrderItem.getNormsCodeList();
            if (CollectionUtil.isNotEmpty(putCodeList)) {
                codeList = codeList.stream()
                    .filter(num -> !putCodeList.contains(num)).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(returnCodeList)) {
                codeList = codeList.stream()
                    .filter(num -> !returnCodeList.contains(num)).collect(Collectors.toList());
            }
            erpOrderItem.setNormsCode(Joiner.on("\n").join(codeList));
            erpOrderItem.setNormsCodeList(codeList);
        }

        // 过滤掉数量为0的商品
        depotOut.setErpOrderItemList(depotOut.getErpOrderItemList().stream()
            .filter(erpOrderItem -> {
                String operNumber = StrUtil.isEmpty(erpOrderItem.getOperNumber())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : erpOrderItem.getOperNumber();
                return CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0;
            }).collect(Collectors.toList()));
        outputObject.setBean(depotOut);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void insertDepotOutToTurnStorePut(InputObject inputObject, OutputObject outputObject) {
        ShopConfirmPut shopConfirmPut = inputObject.getParams(ShopConfirmPut.class);
        DepotOut depotOut = selectById(shopConfirmPut.getId());
        if (ObjectUtil.isEmpty(depotOut)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转
        if (FlowableStateEnum.PASS.getKey().equals(depotOut.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            shopConfirmPut.setFromId(shopConfirmPut.getId());
            shopConfirmPut.setFromTypeId(ShopConfirmFromType.DEPOT_OUT.getKey());
            shopConfirmPut.setDepartmentId(depotOut.getDepartmentId());
            shopConfirmPut.setFarmId(depotOut.getFarmId());
            shopConfirmPut.setSalesman(depotOut.getSalesman());
            shopConfirmPut.setId(StrUtil.EMPTY);
            shopConfirmPutService.createEntity(shopConfirmPut, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法转物料接收单.");
        }
    }

    @Override
    public void insertDepotOutToStoreSealsReturns(InputObject inputObject, OutputObject outputObject) {
        ShopConfirmReturn shopConfirmReturn = inputObject.getParams(ShopConfirmReturn.class);
        DepotOut depotOut = selectById(shopConfirmReturn.getId());
        if (ObjectUtil.isEmpty(depotOut)) {
            throw new CustomException("该数据不存在.");
        }
        // 审核通过的可以转
        if (FlowableStateEnum.PASS.getKey().equals(depotOut.getState())) {
            String userId = inputObject.getLogParams().get("id").toString();
            shopConfirmReturn.setFromId(shopConfirmReturn.getId());
            shopConfirmReturn.setFromTypeId(ConfirmFromType.DEPOT_OUT.getKey());
            shopConfirmReturn.setDepartmentId(depotOut.getDepartmentId());
            shopConfirmReturn.setFarmId(depotOut.getFarmId());
            shopConfirmReturn.setSalesman(depotOut.getSalesman());
            shopConfirmReturn.setId(StrUtil.EMPTY);
            shopConfirmReturnService.createEntity(shopConfirmReturn, userId);
        } else {
            outputObject.setreturnMessage("状态错误，无法转物料退货单.");
        }
    }

    @Override
    public List<DepotOut> queryLeadByHolderId(String holderId) {
        QueryWrapper<DepotOut> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotOut::getHolderId), holderId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepotOut::getState), FlowableStateEnum.PASS.getKey());
        List<DepotOut> depotOutList = list(queryWrapper);
        if (CollectionUtil.isEmpty(depotOutList)) {
            return null;
        }
        List<String> framIds = depotOutList.stream().map(DepotOut::getFromId).collect(Collectors.toList());
        List<String> Ids = productLeadOutStockService.queryByIds(framIds).stream().map(ProductLeadOutStock::getId).collect(Collectors.toList());
        List<DepotOut> depotOuts = depotOutList.stream().filter(depotOut -> Ids.contains(depotOut.getFromId())).collect(Collectors.toList());
        List<String> collect = depotOuts.stream().map(DepotOut::getId).collect(Collectors.toList());
        List<DepotOut> depot = new ArrayList<>();
        collect.forEach(
            id -> {
                DepotOut depotOut = super.selectById(id);
                depot.add(depotOut);
            }
        );
        return depot;
    }

}
