/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.other.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.*;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.entity.MaterialNormsCode;
import com.skyeye.other.dao.SplitListDao;
import com.skyeye.other.entity.SplitList;
import com.skyeye.other.service.SplitListService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SplitListServiceImpl
 * @Description: 拆分单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "拆分单", groupName = "拆分单模块", flowable = true, orderApproval = true)
public class SplitListServiceImpl extends SkyeyeErpOrderServiceImpl<SplitListDao, SplitList> implements SplitListService {

    @Override
    public void validatorEntity(SplitList entity) {
        check(entity);
        checkNormsCodeAndSplitList(entity, true);
    }

    @Override
    public void createPrepose(SplitList entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.OTHER.getKey());
    }

    @Override
    public void writePostpose(SplitList entity, String userId) {
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
    public SplitList getDataFromDb(String id) {
        SplitList splitList = super.getDataFromDb(id);
        // 查询单据子表关联的条形码编号信息
        queryErpOrderItemCodeById(splitList);
        return splitList;
    }

    private static void check(SplitList entity) {
        // 组合件
        List<ErpOrderItem> assemblyList = entity.getErpOrderItemList().stream()
            .filter(erpOrderItem -> MaterialInOrderType.ASSEMBLY.getKey() == erpOrderItem.getMType()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(assemblyList)) {
            throw new CustomException("组合件不能为空");
        }
        if (assemblyList.size() != 1) {
            throw new CustomException("拆分单有且只有一件组合件");
        }

        // 普通子件
        List<ErpOrderItem> generalSubassemblyList = entity.getErpOrderItemList().stream()
            .filter(erpOrderItem -> MaterialInOrderType.GENERAL_SUBASSEMBLY.getKey() == erpOrderItem.getMType()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(generalSubassemblyList)) {
            throw new CustomException("普通子件不能为空");
        }
    }

    @Override
    public SplitList selectById(String id) {
        SplitList split = super.selectById(id);
        split.getErpOrderItemList().forEach(erpOrderItem -> {
            String name = MaterialInOrderType.getName(erpOrderItem.getMType());
            Map<String, Object> mTypeMation = new HashMap<>();
            mTypeMation.put("name", name);
            erpOrderItem.setMTypeMation(mTypeMation);
        });
        return split;
    }

    @Override
    public void approvalEndIsSuccess(SplitList entity) {
        entity = selectById(entity.getId());
        // 校验商品规格条形码明细并进行仓库修改
        checkNormsCodeAndSplitList(entity, false);

        // 修改库存
        for (ErpOrderItem erpOrderItem : entity.getErpOrderItemList()) {
            String depotId = erpOrderItem.getDepotId();
            String materialId = erpOrderItem.getMaterialId();
            String normsId = erpOrderItem.getNormsId();
            String operNumber = erpOrderItem.getOperNumber();
            Integer mType = erpOrderItem.getMType();
            if (MaterialInOrderType.ASSEMBLY.getKey().equals(mType)) {
                erpCommonService.editMaterialNormsDepotStock(depotId, materialId, normsId, operNumber, DepotPutOutType.OUT.getKey(), MaterialNormsStockType.ORDER_STOCK.getKey());
            } else if (MaterialInOrderType.GENERAL_SUBASSEMBLY.getKey().equals(mType)) {
                erpCommonService.editMaterialNormsDepotStock(depotId, materialId, normsId, operNumber, DepotPutOutType.PUT.getKey(), MaterialNormsStockType.ORDER_STOCK.getKey());
            }
        }
    }

    /**
     * 校验商品规格条形码与单据明细的参数是否匹配
     * 拆分单
     *
     * @param entity
     * @param onlyCheck 是否只进行校验，true：是；false：否
     */
    protected void checkNormsCodeAndSplitList(SplitList entity, Boolean onlyCheck) {
        List<String> materialIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getMaterialId).distinct().collect(Collectors.toList());
        List<String> normsIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getNormsId).distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = materialService.selectMapByIds(materialIdList);
        Map<String, MaterialNorms> normsMap = materialNormsService.selectMapByIds(normsIdList);
        // 所有需要进行拆分的条形码编码
        List<String> allNormsCodeList = new ArrayList<>();
        int allCodeNum = checkErpOrderItemDetail(entity, materialMap, normsMap, allNormsCodeList);
        if (CollectionUtil.isNotEmpty(allNormsCodeList)) {
            allNormsCodeList = allNormsCodeList.stream().distinct().collect(Collectors.toList());
            if (allCodeNum != allNormsCodeList.size()) {
                throw new CustomException("商品明细中存在相同的条形码编号，请确认");
            }
            // 从数据库查询未入库和入库状态的条形码信息
            List<MaterialNormsCode> materialNormsCodeList = materialNormsCodeService.queryMaterialNormsCodeByCodeNum(StrUtil.EMPTY, allNormsCodeList,
                MaterialNormsCodeInDepot.NOT_IN_STOCK.getKey(), MaterialNormsCodeInDepot.WAREHOUSING.getKey());
            List<String> inSqlNormsCodeList = materialNormsCodeList.stream().map(MaterialNormsCode::getCodeNum).collect(Collectors.toList());
            // 获取所有前端传递过来的条形码信息，求差集(在入参中有，但是在数据库中不包含的条形码信息)
            List<String> diffList = allNormsCodeList.stream()
                .filter(num -> !inSqlNormsCodeList.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                throw new CustomException(
                    String.format(Locale.ROOT, "编码【%s】不存在或已经出库，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
            }
            Map<String, MaterialNormsCode> materialNormsCodeMap = materialNormsCodeList.stream()
                .collect(Collectors.toMap(MaterialNormsCode::getCodeNum, bean -> bean));
            // 设置条形码新的状态信息
            Map<String, Integer> assemblyMap = new HashMap<>();
            Map<String, String> assemblyDepotMap = new HashMap<>();
            for (ErpOrderItem erpOrderItem : entity.getErpOrderItemList()) {
                Material material = materialMap.get(erpOrderItem.getMaterialId());
                if (material.getItemCode() == MaterialItemCode.ONE_ITEM_CODE.getKey()) {
                    // 一物一码
                    erpOrderItem.getNormsCodeList().forEach(normsCode -> {
                        Integer mType = erpOrderItem.getMType();
                        MaterialNormsCode materialNormsCode = materialNormsCodeMap.get(normsCode);
                        if (MaterialInOrderType.ASSEMBLY.getKey().equals(mType)) {
                            if (materialNormsCode.getInDepot() != MaterialNormsCodeInDepot.WAREHOUSING.getKey()) {
                                throw new CustomException(
                                    String.format(Locale.ROOT, "编码【%s】未入库或已出库，无法作为组合件，请确认", normsCode));
                            }
                            if (!StrUtil.equals(materialNormsCode.getDepotId(), erpOrderItem.getDepotId())) {
                                throw new CustomException(
                                    String.format(Locale.ROOT, "编码【%s】所在仓库与实际仓库不符，请确认", normsCode));
                            }
                            if (!onlyCheck) {
                                assemblyMap.put(normsCode, MaterialNormsCodeInDepot.OUTBOUND.getKey());
                            }
                        } else if (MaterialInOrderType.GENERAL_SUBASSEMBLY.getKey().equals(mType)) {
                            if (materialNormsCode.getInDepot() != MaterialNormsCodeInDepot.NOT_IN_STOCK.getKey()) {
                                throw new CustomException(
                                    String.format(Locale.ROOT, "编码【%s】已入库或已出库，无法作为普通子件，请确认", normsCode));
                            }
                            if (!onlyCheck) {
                                assemblyDepotMap.put(normsCode, erpOrderItem.getDepotId());
                                assemblyMap.put(normsCode, MaterialNormsCodeInDepot.WAREHOUSING.getKey());
                            }
                        }
                        if (!StrUtil.equals(materialNormsCode.getNormsId(), erpOrderItem.getNormsId())) {
                            throw new CustomException(String.format(Locale.ROOT, "条形码【%s】与商品规格不匹配，请确认", normsCode));
                        }
                    });
                }
            }
            if (!onlyCheck) {
                // 批量修改条形码信息
                String currentTime = DateUtil.getTimeAndToString();
                String serviceClassName = getServiceClassName();
                materialNormsCodeList.forEach(materialNormsCode -> {
                    Integer inDepot = assemblyMap.get(materialNormsCode.getCodeNum());
                    materialNormsCode.setInDepot(inDepot);
                    if (inDepot == MaterialNormsCodeInDepot.WAREHOUSING.getKey()) {
                        // 入库
                        materialNormsCode.setDepotId(assemblyDepotMap.get(materialNormsCode.getCodeNum()));
                        materialNormsCode.setWarehousingTime(currentTime);
                        materialNormsCode.setFromObjectId(entity.getId());
                        materialNormsCode.setFromObjectKey(serviceClassName);
                        materialNormsCode.setType(MaterialNormsCodeType.AUTHENTIC.getKey());
                    } else if (inDepot == MaterialNormsCodeInDepot.OUTBOUND.getKey()) {
                        // 出库
                        materialNormsCode.setOutboundTime(currentTime);
                        materialNormsCode.setToObjectId(entity.getId());
                        materialNormsCode.setToObjectKey(serviceClassName);
                    }
                });
                materialNormsCodeService.updateEntity(materialNormsCodeList, StrUtil.EMPTY);
            }
        }
    }

}
