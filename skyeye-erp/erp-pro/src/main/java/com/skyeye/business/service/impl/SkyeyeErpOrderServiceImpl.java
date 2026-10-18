/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.business.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.business.classenum.OrderItemQualityInspectionType;
import com.skyeye.business.classenum.OrderQualityInspectionType;
import com.skyeye.business.service.ErpOrderItemCodeService;
import com.skyeye.business.service.SkyeyeErpOrderItemService;
import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.classenum.ErpOrderStateEnum;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.CorrespondentEnterEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.crm.service.ICustomerService;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.entity.*;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.exception.CustomException;
import com.skyeye.holder.entity.HolderNorms;
import com.skyeye.holder.service.HolderNormsService;
import com.skyeye.ifs.service.IAccountService;
import com.skyeye.material.classenum.MaterialItemCode;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsCodeService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.purchase.service.impl.PurchasePutServiceImpl;
import com.skyeye.retail.service.impl.RetailOutLetServiceImpl;
import com.skyeye.seal.service.impl.SalesOutLetServiceImpl;
import com.skyeye.service.ErpCommonService;
import com.skyeye.shop.service.IMemberService;
import com.skyeye.supplier.entity.Supplier;
import com.skyeye.supplier.service.SupplierService;
import com.skyeye.util.ErpOrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SkyeyeErpOrderServiceImpl
 * @Description: ERP单据的service服务
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/24 20:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class SkyeyeErpOrderServiceImpl<D extends SkyeyeBaseMapper<T>, T extends ErpOrderCommon> extends SkyeyeBusinessServiceImpl<D, T> implements SkyeyeErpOrderService<T> {

    @Autowired
    protected SkyeyeErpOrderItemService skyeyeErpOrderItemService;

    @Autowired
    protected ErpCommonService erpCommonService;

    @Autowired
    protected SupplierService supplierService;

    @Autowired
    protected IMemberService iMemberService;

    @Autowired
    protected MaterialService materialService;

    @Autowired
    protected IAccountService iAccountService;

    @Autowired
    protected ICustomerService iCustomerService;

    @Autowired
    protected ErpDepotService erpDepotService;

    @Autowired
    protected HolderNormsService holderNormsService;

    @Autowired
    protected MaterialNormsService materialNormsService;

    @Autowired
    protected MaterialNormsCodeService materialNormsCodeService;

    @Autowired
    protected ErpOrderItemCodeService erpOrderItemCodeService;

    /**
     * 会员
     */
    private static final String MEMBER_KEY = "com.skyeye.service.impl.MemberServiceImpl";

    @Override
    public QueryWrapper<T> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<T> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderCommon::getIdKey), getServiceClassName());
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderHead::getHolderId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    public QueryWrapper<T> getGrandFatherQueryWrapper(CommonPageInfo commonPageInfo) {
        return super.getQueryWrapper(commonPageInfo);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        setHolderMation(beans);
        return beans;
    }

    private void setHolderMation(List<Map<String, Object>> beans) {
        if (!ErpOrderHead.class.isAssignableFrom(clazz)) {
            return;
        }
        // 供应商
        List<String> supplierIds = beans.stream()
            .filter(bean -> StrUtil.equals(CorrespondentEnterEnum.SUPPLIER.getKey(), bean.get("holderKey").toString()))
            .map(bean -> bean.get("holderId").toString()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(supplierIds)) {
            Map<String, Supplier> supplierMap = supplierService.selectMapByIds(supplierIds);
            beans.forEach(bean -> {
                if (StrUtil.equals(CorrespondentEnterEnum.SUPPLIER.getKey(), bean.get("holderKey").toString())) {
                    bean.put("holderMation", supplierMap.get(bean.get("holderId").toString()));
                }
            });
        }
        // 客户
        List<String> customerIds = beans.stream()
            .filter(bean -> StrUtil.equals(CorrespondentEnterEnum.CUSTOM.getKey(), bean.get("holderKey").toString()))
            .map(bean -> bean.get("holderId").toString()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(customerIds)) {
            Map<String, Map<String, Object>> customerMap = iCustomerService.queryDataMationForMapByIds(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(customerIds));
            beans.forEach(bean -> {
                if (StrUtil.equals(CorrespondentEnterEnum.CUSTOM.getKey(), bean.get("holderKey").toString())) {
                    bean.put("holderMation", customerMap.get(bean.get("holderId").toString()));
                }
            });
        }

        // 会员
        List<String> memberIds = beans.stream()
            .filter(bean -> StrUtil.equals(MEMBER_KEY, bean.get("holderKey").toString()))
            .map(bean -> bean.get("holderId").toString()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(memberIds)) {
            Map<String, Map<String, Object>> supplierMap = iMemberService.queryDataMationForMapByIds(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(memberIds));
            beans.forEach(bean -> {
                if (StrUtil.equals(MEMBER_KEY, bean.get("holderKey").toString())) {
                    bean.put("holderMation", supplierMap.get(bean.get("holderId").toString()));
                }
            });
        }
    }

    @Override
    public void createPrepose(T entity) {
        chectErpOrderItem(entity.getErpOrderItemList());
        entity.setIdKey(getServiceClassName());
        getTotalPrice(entity);
        // 设置商品为使用中
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            materialService.setUsed(erpOrderItem.getMaterialId());
        });
        super.createPrepose(entity);
    }

    private void chectErpOrderItem(List<ErpOrderItem> erpOrderItemList) {
        if (CollectionUtil.isEmpty(erpOrderItemList)) {
            throw new CustomException("请最少选择一条产品信息");
        }
        List<String> normsIds = erpOrderItemList.stream().map(ErpOrderItem::getNormsId).distinct().collect(Collectors.toList());
        if (erpOrderItemList.size() != normsIds.size()) {
            throw new CustomException("单据中不允许存在重复的产品规格信息");
        }
    }

    private void setHolderMation(T entity) {
        if (!ErpOrderHead.class.isAssignableFrom(clazz)) {
            return;
        }
        String holderId = StrUtil.toString(ReflectUtil.getFieldValue(entity, MybatisPlusUtil.toFieldName(ErpOrderHead::getHolderId)));
        if (StrUtil.isNotEmpty(holderId)) {
            String holderKey = StrUtil.toString(ReflectUtil.getFieldValue(entity, MybatisPlusUtil.toFieldName(ErpOrderHead::getHolderKey)));
            if (StrUtil.equals(CorrespondentEnterEnum.SUPPLIER.getKey(), holderKey)) {
                Map<String, Object> supplier = supplierService.selectMapById(holderId);
                ReflectUtil.setFieldValue(entity, MybatisPlusUtil.toFieldName(ErpOrderHead::getHolderMation), supplier);
            } else if (StrUtil.equals(CorrespondentEnterEnum.CUSTOM.getKey(), holderKey)) {
                Map<String, Object> customer = iCustomerService.queryDataMationById(holderId);
                ReflectUtil.setFieldValue(entity, MybatisPlusUtil.toFieldName(ErpOrderHead::getHolderMation), customer);
            } else if (StrUtil.equals(MEMBER_KEY, holderKey)) {
                Map<String, Object> member = iMemberService.queryDataMationById(holderId);
                ReflectUtil.setFieldValue(entity, MybatisPlusUtil.toFieldName(ErpOrderHead::getHolderMation), member);
            }
        }
    }

    protected static Integer setQualityInspection(ErpOrderItem erpOrderItem, Integer qualityInspection) {
        if (erpOrderItem.getQualityInspection() == OrderItemQualityInspectionType.SAMPLING_INS.getKey()
            || erpOrderItem.getQualityInspection() == OrderItemQualityInspectionType.FULL_INSPECTION.getKey()) {
            qualityInspection = OrderQualityInspectionType.NEED_QUALITYINS_INS.getKey();
        }
        if (erpOrderItem.getQualityInspection() == OrderItemQualityInspectionType.SAMPLING_INS.getKey()) {
            // 抽检
            String qualityInspectionRatio = erpOrderItem.getQualityInspectionRatio();
            if (StrUtil.isEmpty(qualityInspectionRatio)) {
                throw new CustomException("抽检比例不能为空.");
            }
            if (CommonNumConstants.NUM_ZERO.equals(Integer.parseInt(qualityInspectionRatio))) {
                throw new CustomException("抽检比例不能为0.");
            }
            if (Integer.parseInt(qualityInspectionRatio) > 100) {
                throw new CustomException("抽检比例不能大于100.");
            }
        } else if (erpOrderItem.getQualityInspection() == OrderItemQualityInspectionType.FULL_INSPECTION.getKey()) {
            // 全检
            erpOrderItem.setQualityInspectionRatio(StrUtil.EMPTY);
        }
        return qualityInspection;
    }

    @Override
    public void writePostpose(T entity, String userId) {
        skyeyeErpOrderItemService.saveLinkList(entity.getId(), entity.getErpOrderItemList());
        super.writePostpose(entity, userId);
    }

    /**
     * 保存单据子表关联的条形码编号信息
     *
     * @param entity
     */
    protected void saveErpOrderItemCode(T entity) {
        List<String> materialIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getMaterialId).distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = materialService.selectMapByIds(materialIdList);
        // 保存单据子表关联的条形码编号信息
        List<ErpOrderItemCode> erpOrderItemCodeList = new ArrayList<>();
        for (ErpOrderItem erpOrderItem : entity.getErpOrderItemList()) {
            Material material = materialMap.get(erpOrderItem.getMaterialId());
            if (material.getItemCode() == MaterialItemCode.ONE_ITEM_CODE.getKey()) {
                // 一物一码
                erpOrderItem.getNormsCodeList().forEach(normsCode -> {
                    ErpOrderItemCode erpOrderItemCode = new ErpOrderItemCode();
                    erpOrderItemCode.setNormsCode(normsCode);
                    erpOrderItemCode.setMaterialId(erpOrderItem.getMaterialId());
                    erpOrderItemCode.setNormsId(erpOrderItem.getNormsId());
                    erpOrderItemCodeList.add(erpOrderItemCode);
                });
            }
        }
        erpOrderItemCodeService.saveList(entity.getId(), erpOrderItemCodeList);
    }

    /**
     * 删除关联的编码信息
     *
     * @param id
     */
    protected void deleteErpOrderItemCodeById(String id) {
        // 删除关联的编码信息
        erpOrderItemCodeService.deleteByParentId(id);
    }

    /**
     * 查询单据子表关联的条形码编号信息
     *
     * @param entity
     */
    protected void queryErpOrderItemCodeById(T entity) {
        String id = entity.getId();
        // 查询单据子表关联的条形码编号信息
        List<ErpOrderItemCode> erpOrderItemCodeList = erpOrderItemCodeService.selectByParentId(id);
        Map<String, List<ErpOrderItemCode>> collect = erpOrderItemCodeList.stream().collect(Collectors.groupingBy(ErpOrderItemCode::getNormsId));
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            List<ErpOrderItemCode> erpOrderItemCodes = collect.get(erpOrderItem.getNormsId());
            if (CollectionUtil.isNotEmpty(erpOrderItemCodes)) {
                List<String> normsCodeList = erpOrderItemCodes.stream().map(ErpOrderItemCode::getNormsCode).collect(Collectors.toList());
                erpOrderItem.setNormsCodeList(normsCodeList);
                erpOrderItem.setNormsCode(Joiner.on("\n").join(normsCodeList));
            }
        });
    }

    @Override
    public void updatePrepose(T entity) {
        chectErpOrderItem(entity.getErpOrderItemList());
        getTotalPrice(entity);
    }

    private void getTotalPrice(T entity) {
        if (ErpOrderHead.class.isAssignableFrom(clazz)) {
            String totalPrice = "0";
            TransmitObject object = new TransmitObject();
            // 计算关联的产品总价
            List<ErpOrderItem> erpOrderItemList = skyeyeErpOrderItemService.calcOrderAllTotalPrice(object, entity.getErpOrderItemList());
            entity.setErpOrderItemList(erpOrderItemList);
            totalPrice = CalculationUtil.add(totalPrice, object.getTaxLastMoneyPrice());
            // 减去优惠金额
            String discountMoney = String.valueOf(ReflectUtil.getFieldValue(entity, MybatisPlusUtil.toFieldName(ErpOrderHead::getDiscountMoney)));
            if (NumberUtil.isNumber(discountMoney)) {
                totalPrice = CalculationUtil.subtract(totalPrice, discountMoney);
            }
            ReflectUtil.setFieldValue(entity, MybatisPlusUtil.toFieldName(ErpOrderHead::getTotalPrice), totalPrice);
        }
    }

    @Override
    public T getDataFromDb(String id) {
        T erpOrderHead = super.getDataFromDb(id);
        List<ErpOrderItem> erpOrderItemList = skyeyeErpOrderItemService.selectByPId(id);
        erpOrderHead.setErpOrderItemList(erpOrderItemList);
        return erpOrderHead;
    }

    @Override
    public T selectById(String id) {
        T erpOrderHead = super.selectById(id);
        // 设置关联的客户/供应商/会员信息
        setHolderMation(erpOrderHead);
        // 设置产品信息
        materialService.setDataMation(erpOrderHead.getErpOrderItemList(), ErpOrderItem::getMaterialId);
        erpOrderHead.getErpOrderItemList().forEach(erpOrderItem -> {
            MaterialNorms norms = erpOrderItem.getMaterialMation().getMaterialNorms()
                .stream().filter(bean -> StrUtil.equals(erpOrderItem.getNormsId(), bean.getId())).findFirst().orElse(null);
            erpOrderItem.setNormsMation(norms);
        });
        // 账户信息
        setAccountMation(erpOrderHead);
        // 仓库信息
        erpDepotService.setDataMation(erpOrderHead.getErpOrderItemList(), ErpOrderItem::getDepotId);
        erpDepotService.setDataMation(erpOrderHead.getErpOrderItemList(), ErpOrderItem::getAnotherDepotId);
        // 业务员信息
        iAuthUserService.setDataMation(erpOrderHead, ErpOrderCommon::getSalesman);
        return erpOrderHead;
    }

    private void setAccountMation(T entity) {
        if (!ErpOrderHead.class.isAssignableFrom(clazz)) {
            return;
        }
        String accountId = StrUtil.toString(ReflectUtil.getFieldValue(entity, MybatisPlusUtil.toFieldName(ErpOrderHead::getAccountId)));
        if (StrUtil.isNotEmpty(accountId) && !StrUtil.equals("null", accountId)) {
            ReflectUtil.setFieldValue(entity, MybatisPlusUtil.toFieldName(ErpOrderHead::getAccountMation),
                iAccountService.queryDataMationById(accountId));
        }
    }

    @Override
    public void deletePostpose(String id) {
        // 删除子单据信息
        skyeyeErpOrderItemService.deleteByPId(id);
    }

    protected void depotOutOrPutSuccess(String holderId, String holderKey, List<ErpOrderItem> erpOrderItemList, int type,
                                        String orderId, String orderIdKey) {
        List<HolderNorms> holderNormsList = new ArrayList<>();
        // 采购入库单，销售出库单，零售出库单
        List<String> orderTypes = Arrays.asList(PurchasePutServiceImpl.class.getName(), SalesOutLetServiceImpl.class.getName(), RetailOutLetServiceImpl.class.getName());
        String createTime = DateUtil.getTimeAndToString();
        // 修改库存&&保存客户/供应商/会员关联的商品信息
        for (ErpOrderItem bean : erpOrderItemList) {
            erpCommonService.editMaterialNormsDepotStock(bean.getDepotId(), bean.getMaterialId(), bean.getNormsId(), bean.getOperNumber(), type, MaterialNormsStockType.ORDER_STOCK.getKey());

            if (StrUtil.isNotEmpty(holderId) && StrUtil.isNotEmpty(holderKey) && orderTypes.contains(orderIdKey)) {
                // 记录客户/供应商/会员关联的商品
                HolderNorms holderNorms = new HolderNorms();
                holderNorms.setHolderId(holderId);
                holderNorms.setHolderKey(holderKey);
                holderNorms.setMaterialId(bean.getMaterialId());
                holderNorms.setNormsId(bean.getNormsId());
                holderNorms.setCreateTime(createTime);
                holderNorms.setOperNumber(bean.getOperNumber());
                holderNorms.setNormsCodeList(bean.getNormsCodeList());
                holderNorms.setOrderId(orderId);
                holderNorms.setOrderKey(orderIdKey);
                holderNorms.setDepotId(bean.getDepotId());
                holderNormsList.add(holderNorms);
            }
        }
        holderNormsService.createEntity(holderNormsList, StrUtil.EMPTY);
    }

    /**
     * 校验单据商品是否存在来源单据中不包含的商品
     *
     * @param erpOrderItemList 来源单据的子单据信息
     * @param inSqlNormsId
     */
    protected void checkFromOrderMaterialNorms(List<ErpOrderItem> erpOrderItemList, List<String> inSqlNormsId) {
        List<String> fromNormsIds = erpOrderItemList.stream()
            .map(ErpOrderItem::getNormsId).collect(Collectors.toList());
        checkIdFromOrderMaterialNorms(fromNormsIds, inSqlNormsId);
    }

    protected void checkIdFromOrderMaterialNorms(List<String> fromNormsIds, List<String> inSqlNormsId) {
        // 求差集(当前单据在来源单据中不包含的商品)
        List<String> diffList = inSqlNormsId.stream()
            .filter(num -> !fromNormsIds.contains(num)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(diffList)) {
            List<MaterialNorms> materialNormsList = materialNormsService.selectByIds(diffList.toArray(new String[]{}));
            List<String> normsNames = materialNormsList.stream().map(MaterialNorms::getName).collect(Collectors.toList());
            throw new CustomException(String.format(Locale.ROOT, "该来源单据下未包含如下商品规格：【%s】.",
                Joiner.on(CommonCharConstants.COMMA_MARK).join(normsNames)));
        }
    }

    /**
     * 计算来源单据剩余未操作的数量
     *
     * @param erpOrderItemList
     * @param setData
     * @param nums
     */
    protected void setOrCheckOperNumber(List<ErpOrderItem> erpOrderItemList, boolean setData, Map<String, String>... nums) {
        erpOrderItemList.forEach(erpOrderItem -> {
            String surplusNum = ErpOrderUtil.checkOperNumber(erpOrderItem.getOperNumber(), erpOrderItem.getNormsId(), nums);
            if (setData) {
                erpOrderItem.setOperNumber(surplusNum);
            }
        });
    }

    protected int checkErpOrderItemDetail(T entity, Map<String, Material> materialMap, Map<String, MaterialNorms> normsMap, List<String> allNormsCodeList) {
        int allCodeNum = 0;
        for (ErpOrderItem erpOrderItem : entity.getErpOrderItemList()) {
            Material material = materialMap.get(erpOrderItem.getMaterialId());
            MaterialNorms norms = normsMap.get(erpOrderItem.getNormsId());
            if (CalculationUtil.compareTo(erpOrderItem.getOperNumber(), CommonNumConstants.NUM_ZERO.toString(), 0, RoundingMode.UP) == 0) {
                throw new CustomException(
                    String.format(Locale.ROOT, "商品【%s】【%s】的数量不能为0，请确认", material.getName(), norms.getName()));
            }
            if (material.getItemCode() == MaterialItemCode.ONE_ITEM_CODE.getKey()) {
                // 一物一码
                // 过滤掉空的，并且去重
                List<String> normsCodeList = Arrays.asList(erpOrderItem.getNormsCode().split("\n")).stream()
                    .filter(str -> StrUtil.isNotEmpty(str)).distinct().collect(Collectors.toList());
                // 如果开启了一物一码，那么这个数量就默认是整型的，将数量转换为整数进行比较（处理小数情况，取整）
                int operNumberInt = NumberUtil.parseInt(erpOrderItem.getOperNumber());
                if (operNumberInt != normsCodeList.size()) {
                    throw new CustomException(
                        String.format(Locale.ROOT, "商品【%s】【%s】的条形码数量与明细数量不一致，请确认", material.getName(), norms.getName()));
                }
                allCodeNum += normsCodeList.size();
                erpOrderItem.setNormsCodeList(normsCodeList);
                allNormsCodeList.addAll(normsCodeList);
            }
        }
        return allCodeNum;
    }

    @Override
    public Map<String, String> calcMaterialNormsNumByFromId(String fromId) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(CommonConstants.ID);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderCommon::getFromId), fromId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderCommon::getIdKey), getServiceClassName());
        // 只查询审批通过，部分出入库，已完成的单据
        List<String> stateList = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey(),
            ErpOrderStateEnum.COMPLETED.getKey()});
        queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderCommon::getState), stateList);
        List<T> entityList = list(queryWrapper);
        List<String> ids = entityList.stream().map(ErpOrderCommon::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        List<ErpOrderItem> erpOrderItemList = skyeyeErpOrderItemService.queryErpOrderItemByPIds(ids);
        Map<String, String> collect = erpOrderItemList.stream()
            .collect(Collectors.groupingBy(
                ErpOrderItem::getNormsId,
                Collectors.reducing(
                    CommonNumConstants.NUM_ZERO.toString(),
                    ErpOrderItem::getOperNumber,
                    (sum, operNumber) -> {
                        String operNum = StrUtil.isEmpty(operNumber) ? CommonNumConstants.NUM_ZERO.toString() : operNumber;
                        String sumValue = StrUtil.isEmpty(sum) ? CommonNumConstants.NUM_ZERO.toString() : sum;
                        return CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, sumValue, operNum);
                    }
                )
            ));
        return collect;
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
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        List<T> entityList = list(queryWrapper);
        Map<String, T> entityMap = entityList.stream().collect(Collectors.toMap(ErpOrderCommon::getId, bean -> bean));
        for (Map<String, Object> bean : beans) {
            if (!MapUtil.checkKeyIsNull(bean, idKey)) {
                T entity = entityMap.get(bean.get(idKey).toString());
                if (ObjectUtil.isEmpty(entity)) {
                    continue;
                }
                bean.put(mationKey, entity);
            }
        }
    }

    @Override
    public void editOtherState(String id, Integer otherState) {
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ErpOrderCommon::getOtherState), otherState);
        update(updateWrapper);
        refreshCache(id);
    }

    /**
     * 业财成本金额：按规格「采购价/成本价」× 数量汇总；无成本价时该行记 0。
     * 完整移动加权待存货成本台账落地后替换。
     */
    protected String resolveCostAmountByNorms(List<ErpOrderItem> items) {
        if (CollectionUtil.isEmpty(items)) {
            return CommonNumConstants.NUM_ZERO.toString();
        }
        List<String> normsIds = items.stream()
            .map(ErpOrderItem::getNormsId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        Map<String, MaterialNorms> normsMap = CollectionUtil.isEmpty(normsIds)
            ? Collections.emptyMap()
            : materialNormsService.selectByIds(normsIds.toArray(new String[0])).stream()
            .filter(n -> n != null && StrUtil.isNotBlank(n.getId()))
            .collect(Collectors.toMap(MaterialNorms::getId, n -> n, (a, b) -> a));
        String total = CommonNumConstants.NUM_ZERO.toString();
        for (ErpOrderItem item : items) {
            if (item == null || StrUtil.isBlank(item.getNormsId())) {
                continue;
            }
            MaterialNorms norms = normsMap.get(item.getNormsId());
            String unitCost = norms == null ? null : norms.getEstimatePurchasePrice();
            if (StrUtil.isBlank(unitCost)
                || CalculationUtil.compareTo(unitCost, "0", CommonNumConstants.NUM_TWO, RoundingMode.HALF_UP) <= 0) {
                continue;
            }
            String qty = StrUtil.blankToDefault(item.getOperNumber(), CommonNumConstants.NUM_ZERO.toString());
            String lineCost = CalculationUtil.multiply(qty, unitCost, CommonNumConstants.NUM_TWO);
            total = CalculationUtil.add(total, lineCost, CommonNumConstants.NUM_TWO);
        }
        return total;
    }
}
