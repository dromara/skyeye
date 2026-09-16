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
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialInOrderType;
import com.skyeye.material.classenum.MaterialItemCode;
import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.entity.MaterialNormsCode;
import com.skyeye.other.dao.AllocationDao;
import com.skyeye.other.entity.Allocation;
import com.skyeye.other.service.AllocationService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: AllocationServiceImpl
 * @Description: 调拨单管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:04
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "调拨单", groupName = "调拨单模块", flowable = true, orderApproval = true)
public class AllocationServiceImpl extends SkyeyeErpOrderServiceImpl<AllocationDao, Allocation> implements AllocationService {

    @Override
    public void validatorEntity(Allocation entity) {
        check(entity);
        checkNormsCodeAndAllocation(entity, true);
    }

    @Override
    public void createPrepose(Allocation entity) {
        super.createPrepose(entity);
        entity.setType(DepotPutOutType.OTHER.getKey());
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            erpOrderItem.setMType(MaterialInOrderType.GENERAL.getKey());
        });
    }

    @Override
    public void writePostpose(Allocation entity, String userId) {
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
    public Allocation getDataFromDb(String id) {
        Allocation allocation = super.getDataFromDb(id);
        // 查询单据子表关联的条形码编号信息
        queryErpOrderItemCodeById(allocation);
        return allocation;
    }

    private static void check(Allocation entity) {
        entity.getErpOrderItemList().forEach(erpOrderItem -> {
            if (StrUtil.isEmpty(erpOrderItem.getDepotId()) || StrUtil.isEmpty(erpOrderItem.getAnotherDepotId())) {
                throw new CustomException("出入库仓库不能为空。");
            }
        });
    }

    @Override
    public void approvalEndIsSuccess(Allocation entity) {
        entity = selectById(entity.getId());
        // 校验商品规格条形码明细并进行仓库修改
        checkNormsCodeAndAllocation(entity, false);
        // 修改库存
        for (ErpOrderItem erpOrderItem : entity.getErpOrderItemList()) {
            String depotId = erpOrderItem.getDepotId();
            String materialId = erpOrderItem.getMaterialId();
            String normsId = erpOrderItem.getNormsId();
            String operNumber = erpOrderItem.getOperNumber();
            // 调拨单
            String anotherDepotId = erpOrderItem.getAnotherDepotId();
            // 当前仓库出库
            erpCommonService.editMaterialNormsDepotStock(depotId, materialId, normsId, operNumber, DepotPutOutType.OUT.getKey(), MaterialNormsStockType.ORDER_STOCK.getKey());
            // 调入仓库入库
            erpCommonService.editMaterialNormsDepotStock(anotherDepotId, materialId, normsId, operNumber, DepotPutOutType.PUT.getKey(), MaterialNormsStockType.ORDER_STOCK.getKey());
        }
    }

    /**
     * 校验商品规格条形码与单据明细的参数是否匹配
     * 调拨单
     *
     * @param entity
     * @param onlyCheck 是否只进行校验，true：是；false：否
     */
    protected void checkNormsCodeAndAllocation(Allocation entity, Boolean onlyCheck) {
        List<String> materialIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getMaterialId).distinct().collect(Collectors.toList());
        List<String> normsIdList = entity.getErpOrderItemList().stream().map(ErpOrderItem::getNormsId).distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = materialService.selectMapByIds(materialIdList);
        Map<String, MaterialNorms> normsMap = materialNormsService.selectMapByIds(normsIdList);
        // 所有需要进行调拨的条形码编码
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
                    String.format(Locale.ROOT, "编码【%s】不存在或未入库/已经出库，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
            }
            // 判断条形码是否就在出库仓库里面
            Map<String, MaterialNormsCode> materialNormsCodeMap = materialNormsCodeList.stream()
                .collect(Collectors.toMap(MaterialNormsCode::getCodeNum, bean -> bean));
            // 条形码对应的新的仓库信息
            Map<String, String> allocationMap = new HashMap<>();
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
                        if (!onlyCheck) {
                            allocationMap.put(normsCode, erpOrderItem.getAnotherDepotId());
                        }
                    });
                }
            }
            if (!onlyCheck) {
                // 批量修改条形码信息
                materialNormsCodeList.forEach(materialNormsCode -> {
                    materialNormsCode.setDepotId(allocationMap.get(materialNormsCode.getCodeNum()));
                });
                materialNormsCodeService.updateEntity(materialNormsCodeList, StrUtil.EMPTY);
            }
        }
    }

}
