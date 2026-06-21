/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.shopstock.service.IShopStockService;
import com.skyeye.store.dao.ProductTransferDao;
import com.skyeye.store.entity.ProductTransfer;
import com.skyeye.store.entity.ProductTransferLink;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.service.ProductTransferLinkService;
import com.skyeye.store.service.ProductTransferService;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ProductTransferServiceImpl
 * @Description: 门店产品调拨申请服务层（参照 SealApplyServiceImpl）
 */
@Service
@SkyeyeService(name = "门店产品调拨", groupName = "门店产品调拨", flowable = true)
public class ProductTransferServiceImpl extends SkyeyeBusinessServiceImpl<ProductTransferDao, ProductTransfer> implements ProductTransferService {

    @Autowired
    private ProductTransferLinkService productTransferLinkService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private IShopStockService iShopStockService;

    @Override
    public QueryWrapper<ProductTransfer> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ProductTransfer> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductTransfer::getCreateId), userId);
        return queryWrapper;
    }

    @Override
    public void validatorEntity(ProductTransfer entity) {
        if (CollectionUtil.isEmpty(entity.getApplyLinkList())) {
            throw new CustomException("请至少填写一条调拨明细");
        }
        if (StrUtil.equals(entity.getFromStoreId(), entity.getToStoreId())) {
            throw new CustomException("原门店和目标门店相同，无需调拨");
        }
    }

    @Override
    public void writePostpose(ProductTransfer entity, String userId) {
        productTransferLinkService.saveLinkList(entity.getId(), entity.getApplyLinkList());
        super.writePostpose(entity, userId);
    }

    @Override
    protected void deletePreExecution(ProductTransfer entity) {
        if (!checkState(entity)) {
            throw new CustomException("该数据状态已改变，删除失败.");
        }
        productTransferLinkService.deleteByPId(entity.getId());
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        productTransferLinkService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public ProductTransfer getDataFromDb(String id) {
        ProductTransfer productTransfer = super.getDataFromDb(id);
        productTransfer.setApplyLinkList(productTransferLinkService.selectByPId(productTransfer.getId()));
        return productTransfer;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isNotEmpty(beans)) {
            List<String> storeIds = beans.stream()
                .flatMap(bean -> {
                    List<String> ids = new ArrayList<>();
                    ids.add(bean.get("fromStoreId").toString());
                    ids.add(bean.get("toStoreId").toString());
                    return ids.stream();
                }).distinct().collect(Collectors.toList());
            List<ShopStore> shopStores = shopStoreService.selectByIds(storeIds.toArray(new String[]{}));
            Map<String, ShopStore> storeMap = shopStores.stream()
                .collect(Collectors.toMap(ShopStore::getId, shopStore -> shopStore));
            beans.forEach(bean -> {
                bean.put("fromStoreMation", storeMap.get(bean.get("fromStoreId").toString()));
                bean.put("toStoreMation", storeMap.get(bean.get("toStoreId").toString()));
            });
        }
        return beans;
    }

    @Override
    public ProductTransfer selectById(String id) {
        ProductTransfer productTransfer = super.selectById(id);
        if (productTransfer == null) {
            return productTransfer;
        }
        if (CollectionUtil.isNotEmpty(productTransfer.getApplyLinkList())) {
            iMaterialService.setDataMation(productTransfer.getApplyLinkList(), ProductTransferLink::getMaterialId);
            iMaterialNormsService.setDataMation(productTransfer.getApplyLinkList(), ProductTransferLink::getNormsId);
        }
        ShopStore fromStore = shopStoreService.selectById(productTransfer.getFromStoreId());
        if (fromStore != null) {
            productTransfer.setFromStoreMation(fromStore);
        }
        ShopStore toStore = shopStoreService.selectById(productTransfer.getToStoreId());
        if (toStore != null) {
            productTransfer.setToStoreMation(toStore);
        }
        return productTransfer;
    }

    @Override
    public void revokePostpose(ProductTransfer entity) {
        super.revokePostpose(entity);
        productTransferLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    protected void approvalEndIsSuccess(ProductTransfer entity) {
        ProductTransfer productTransfer = getDataFromDb(entity.getId());
        executeStoreProductTransfer(productTransfer);
        productTransferLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
    }

    @Override
    protected void approvalEndIsFailed(ProductTransfer entity) {
        productTransferLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

    private void executeStoreProductTransfer(ProductTransfer productTransfer) {
        Map<String, Object> params = new HashMap<>();
        params.put("fromStoreId", productTransfer.getFromStoreId());
        params.put("toStoreId", productTransfer.getToStoreId());
        List<Map<String, Object>> applyLinkList = new ArrayList<>();
        productTransfer.getApplyLinkList().forEach(applyLink -> {
            Map<String, Object> item = new HashMap<>();
            item.put("materialId", applyLink.getMaterialId());
            item.put("normsId", applyLink.getNormsId());
            item.put("operNumber", applyLink.getOperNumber());
            applyLinkList.add(item);
        });
        params.put("applyLinkList", JSONUtil.toJsonStr(applyLinkList));
        iShopStockService.executeStoreProductTransfer(params);
    }

}
