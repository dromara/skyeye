/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shopmaterial.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.ShopMaterialDeliveryMethod;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantAopUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.material.entity.Material;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.rest.shop.service.IShopStoreService;
import com.skyeye.shopmaterial.dao.ShopMaterialStoreDao;
import com.skyeye.shopmaterial.entity.ShopMaterial;
import com.skyeye.shopmaterial.entity.ShopMaterialNorms;
import com.skyeye.shopmaterial.entity.ShopMaterialStore;
import com.skyeye.shopmaterial.enums.ShopMaterialStoreCoverage;
import com.skyeye.shopmaterial.service.ShopMaterialService;
import com.skyeye.shopmaterial.service.ShopMaterialStoreService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ShopMaterialStoreServiceImpl
 * @Description: 商城商品上线的门店服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/18 14:10
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商城商品上线的门店", groupName = "商城商品上线的门店", manageShow = false)
public class ShopMaterialStoreServiceImpl extends SkyeyeBusinessServiceImpl<ShopMaterialStoreDao, ShopMaterialStore> implements ShopMaterialStoreService {

    @Autowired
    private IShopStoreService iShopStoreService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private ShopMaterialService shopMaterialService;

    @Override
    public void deleteByMaterialId(String materialId) {
        QueryWrapper<ShopMaterialStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), materialId);
        remove(queryWrapper);
    }

    @Override
    public List<ShopMaterialStore> selectByMaterialId(String materialId) {
        QueryWrapper<ShopMaterialStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), materialId);
        List<ShopMaterialStore> shopMaterialStoreList = list(queryWrapper);
        return shopMaterialStoreList;
    }

    @IgnoreTenant
    @Override
    public List<ShopMaterialStore> selectByStoreId(String storeId, Integer isLaunchStore, Integer isLunchShop, String keyword) {
        MPJLambdaWrapper<ShopMaterialStore> queryWrapper = JoinWrappers.lambda("i", ShopMaterialStore.class);
        queryWrapper.innerJoin(Material.class, "t", Material::getId, ShopMaterialStore::getMaterialId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), storeId);
        if (isLaunchStore != null) {
            // 是否添加到门店
            queryWrapper.eq("i." + MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchStore), isLaunchStore);
        }
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            queryWrapper.eq("t." + CommonConstants.TENANT_ID_FIELD, tenantId);
            queryWrapper.eq("i." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        if (isLunchShop != null) {
            // 是否上架到商城
            queryWrapper.eq("i." + MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchShop), isLunchShop);
        }
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(wra -> {
                wra.or().like("t." + MybatisPlusUtil.toColumns(Material::getName), keyword);
                wra.or().like("t." + MybatisPlusUtil.toColumns(Material::getModel), keyword);
            });
        }

        List<ShopMaterialStore> shopMaterialStoreList = skyeyeBaseMapper.selectJoinList(ShopMaterialStore.class, queryWrapper);
        return shopMaterialStoreList;
    }

    @Override
    public Map<String, List<ShopMaterialStore>> selectByMaterialId(List<String> materialId) {
        if (CollectionUtil.isEmpty(materialId)) {
            return MapUtil.empty();
        }
        QueryWrapper<ShopMaterialStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), materialId);
        List<ShopMaterialStore> shopMaterialStoreList = list(queryWrapper);
        Map<String, List<ShopMaterialStore>> collect = shopMaterialStoreList.stream().collect(Collectors.groupingBy(ShopMaterialStore::getMaterialId));
        return collect;
    }

    @Override
    public void addAllStoreForMaterial(String materialId, Integer storeCoverage, List<String> storeIds, String bigTypeId) {
        List<Map<String, Object>> storeList = iShopStoreService.queryStoreListByParams(StrUtil.EMPTY, null);
        if (CollectionUtil.isEmpty(storeList)) {
            return;
        }
        if (ShopMaterialStoreCoverage.ALL_STORE.getKey().equals(storeCoverage)) {
            // 使用全部门店
            storeIds = storeList.stream().map(store -> MapUtil.getStr(store, "id")).collect(Collectors.toList());
        }

        // 指定门店直接使用默认的storeIds
        if (CollectionUtil.isEmpty(storeIds)) {
            return;
        }

        // 获取原有的门店关联数据
        List<ShopMaterialStore> oldShopMaterialStores = selectByMaterialId(materialId);

        // 将原有数据转换为Map，便于查找
        Map<String, ShopMaterialStore> oldStoreMap = oldShopMaterialStores.stream()
            .collect(Collectors.toMap(ShopMaterialStore::getStoreId, store -> store));

        // 提取原有门店ID列表
        List<String> oldStoreIds = new ArrayList<>(oldStoreMap.keySet());

        // 找出新增的门店ID（新门店ID - 旧门店ID）
        List<String> newStoreIds = storeIds.stream()
            .filter(storeId -> !oldStoreIds.contains(storeId)).collect(Collectors.toList());

        // 找出删除的门店ID（旧门店ID - 新门店ID）
        List<String> finalStoreIds = storeIds;
        List<String> deletedStoreIds = oldStoreIds.stream()
            .filter(storeId -> !finalStoreIds.contains(storeId)).collect(Collectors.toList());

        // 找出需要编辑的门店ID（新门店ID ∩ 旧门店ID）
        List<String> updateStoreIds = storeIds.stream()
            .filter(storeId -> oldStoreIds.contains(storeId))
            .collect(Collectors.toList());

        // 删除不再关联的门店
        if (CollectionUtil.isNotEmpty(deletedStoreIds)) {
            QueryWrapper<ShopMaterialStore> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), materialId);
            deleteWrapper.in(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), deletedStoreIds);
            remove(deleteWrapper);
        }

        // 编辑已存在的门店关联（重新设置bigTypeId）
        if (CollectionUtil.isNotEmpty(updateStoreIds)) {
            List<ShopMaterialStore> updateShopMaterialStoreList = updateStoreIds.stream().map(storeId -> {
                ShopMaterialStore existingStore = oldStoreMap.get(storeId);
                // 重新设置bigTypeId
                existingStore.setBigTypeId(bigTypeId);
                return existingStore;
            }).collect(Collectors.toList());

            String userId = InputObject.getLogParamsStatic().get("id").toString();
            updateEntity(updateShopMaterialStoreList, userId);
        }

        // 新增门店关联
        if (CollectionUtil.isNotEmpty(newStoreIds)) {
            List<ShopMaterialStore> newShopMaterialStoreList = newStoreIds.stream().map(storeId -> {
                ShopMaterialStore shopMaterialStore = new ShopMaterialStore();
                shopMaterialStore.setStoreId(storeId);
                shopMaterialStore.setMaterialId(materialId);
                shopMaterialStore.setBigTypeId(bigTypeId);
                shopMaterialStore.setIsLaunchStore(WhetherEnum.DISABLE_USING.getKey());
                shopMaterialStore.setIsLaunchShop(WhetherEnum.DISABLE_USING.getKey());
                return shopMaterialStore;
            }).collect(Collectors.toList());

            String userId = InputObject.getLogParamsStatic().get("id").toString();
            createEntity(newShopMaterialStoreList, userId);
        }
    }

    @Override
    public void saveShopMaterialStore(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = MapUtil.getStr(params, "storeId");

        // 获取适用于所有门店的商品数据
        List<ShopMaterial> shopMaterialList = shopMaterialService.queryShopMaterialListByStoreCoverage(ShopMaterialStoreCoverage.ALL_STORE.getKey(), StrUtil.EMPTY);
        if (CollectionUtil.isEmpty(shopMaterialList)) {
            return;
        }
        List<String> materialIdList = shopMaterialList.stream().map(ShopMaterial::getId).collect(Collectors.toList());

        // 获取门店关联的老数据
        List<ShopMaterialStore> oldShopMaterialStores = TenantAopUtil.getSelf(this).selectByStoreId(storeId, null, null, null);
        List<String> oldMaterialIdList = oldShopMaterialStores.stream()
            .map(ShopMaterialStore::getMaterialId).collect(Collectors.toList());

        // 找出在老数据中没有的商品ID，用于新增
        List<String> newMaterialIdList = materialIdList.stream()
            .filter(materialId -> !oldMaterialIdList.contains(materialId)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newMaterialIdList)) {
            // 构造新的门店商品数据进行保存
            List<ShopMaterialStore> newList = materialIdList.stream().map(materialId -> {
                ShopMaterialStore shopMaterialStore = new ShopMaterialStore();
                shopMaterialStore.setMaterialId(materialId);
                shopMaterialStore.setStoreId(storeId);
                shopMaterialStore.setIsLaunchStore(WhetherEnum.DISABLE_USING.getKey());
                shopMaterialStore.setIsLaunchShop(WhetherEnum.DISABLE_USING.getKey());
                shopMaterialStore.setStoreEnabled(EnableEnum.ENABLE_USING.getKey());
                return shopMaterialStore;
            }).collect(Collectors.toList());
            // 保存门店商品数据
            createEntity(newList, InputObject.getLogParamsStatic().get("id").toString());
        }

        // 设置该门店的状态为启用，因为有一部分旧数据需要更新
        UpdateWrapper<ShopMaterialStore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), storeId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreEnabled), EnableEnum.ENABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    @IgnoreTenant
    public List<ShopMaterialStore> queryShopMaterialList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 商品名称，型号，门店，品牌
        MPJLambdaWrapper<ShopMaterialStore> wrapper = JoinWrappers.lambda("sms", ShopMaterialStore.class);
        wrapper.innerJoin(Material.class, "m", Material::getId, ShopMaterialStore::getMaterialId)
            .innerJoin(ShopMaterial.class, "sm", ShopMaterial::getMaterialId, ShopMaterialStore::getMaterialId);
        if (StrUtil.isNotBlank(commonPageInfo.getObjectId())) {
            wrapper.eq(ShopMaterialStore::getStoreId, commonPageInfo.getObjectId());
        }
        if (StrUtil.isNotBlank(commonPageInfo.getHolderId())) {
            wrapper.eq(ShopMaterialStore::getMaterialId, commonPageInfo.getHolderId());
        }
        if (StrUtil.isNotBlank(commonPageInfo.getType())) {
            wrapper.eq(Material::getBrandId, commonPageInfo.getType());
        }
        if (StrUtil.isNotBlank(commonPageInfo.getKeyword())) {
            wrapper.and(wra -> {
                wra.or().like(Material::getName, commonPageInfo.getKeyword());
                wra.or().like(Material::getModel, commonPageInfo.getKeyword());
            });
        }
        if (StrUtil.isNotBlank(commonPageInfo.getCustomParamsMapStr("bigTypeId"))) {
            // 商品大类ID
            wrapper.eq(ShopMaterialStore::getBigTypeId, commonPageInfo.getCustomParamsMapStr("bigTypeId"));
        }
        // 设置商品查询的类型
        queryShopSelType(commonPageInfo, wrapper);
        // 已经添加到门店
        wrapper.eq(ShopMaterialStore::getIsLaunchStore, WhetherEnum.ENABLE_USING.getKey());
        // 上架到商城
        wrapper.eq(ShopMaterialStore::getIsLaunchShop, WhetherEnum.ENABLE_USING.getKey());
        // 门店是启用状态的
        wrapper.eq(ShopMaterialStore::getStoreEnabled, EnableEnum.ENABLE_USING.getKey());

        List<ShopMaterialStore> shopMaterialStoreList = skyeyeBaseMapper.selectJoinList(ShopMaterialStore.class, wrapper);
        iShopStoreService.setDataMation(shopMaterialStoreList, ShopMaterialStore::getStoreId);
        outputObject.settotal(pages.getTotal());
        return shopMaterialStoreList;
    }

    private static void queryShopSelType(CommonPageInfo commonPageInfo, MPJLambdaWrapper<ShopMaterialStore> wrapper) {
        String deliveryMethodColumn = MybatisPlusUtil.toColumns(ShopMaterial::getDeliveryMethod);
        if (StrUtil.equals(commonPageInfo.getCustomParamsMapStr("shopType"), "sameCity")) {
            // 同城的商品 - 配送方式包含"同城配送"（key=3）
            // deliveryMethod存储的是JSON字符串数组，如["1","2","3"]，使用LIKE查询字符串"3"
            Integer key = ShopMaterialDeliveryMethod.LOCAL_DELIVERY.getKey();
            wrapper.apply("sm." + deliveryMethodColumn + " LIKE {0}",
                "%\"" + key + "\"%");
        } else if (StrUtil.equals(commonPageInfo.getCustomParamsMapStr("shopType"), "mallProducts")) {
            // 可以邮寄的商品 - 配送方式包含"快递发货"（key=1）
            // deliveryMethod存储的是JSON字符串数组，如["1","2","3"]，使用LIKE查询字符串"1"
            Integer key = ShopMaterialDeliveryMethod.EXPRESS_DELIVERY.getKey();
            wrapper.apply("sm." + deliveryMethodColumn + " LIKE {0}",
                "%\"" + key + "\"%");
        }
    }

    @Override
    @IgnoreTenant
    public Map<String, ShopMaterialStore> queryShopMaterialStoreByMaterialIds(String... materialIds) {
        List<String> idList = Arrays.asList(materialIds).stream()
            .filter(materialId -> StrUtil.isNotEmpty(materialId)).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(idList)) {
            return new HashMap<>();
        }
        QueryWrapper<ShopMaterialStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), idList);
        // 已经添加到门店
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchStore), WhetherEnum.ENABLE_USING.getKey());
        // 上架到商城
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchShop), WhetherEnum.ENABLE_USING.getKey());
        // 门店是启用状态的
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreEnabled), EnableEnum.ENABLE_USING.getKey());

        List<ShopMaterialStore> shopMaterialStoreList = list(queryWrapper);
        Map<String, ShopMaterialStore> collect = shopMaterialStoreList.stream()
            .collect(Collectors.toMap(ShopMaterialStore::getMaterialId, shopMaterialStore -> shopMaterialStore, (existingValue, newValue) -> existingValue));
        return collect;
    }

    @Override
    @IgnoreTenant
    public void queryShopMaterialById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        ShopMaterialStore shopMaterialStore = selectById(id);
        if (ObjectUtil.isNull(shopMaterialStore) || StrUtil.isBlank(shopMaterialStore.getId())) {
            throw new CustomException("未查询到该商品信息");
        }
        ShopMaterial shopMaterial = shopMaterialService.queryShopMaterialByMaterialId(shopMaterialStore.getMaterialId());
        shopMaterial.getMaterialMation().setMaterialNorms(null);
        shopMaterial.getMaterialMation().setUnitGroupMation(null);
        shopMaterial.getMaterialMation().setMaterialProcedure(null);
        shopMaterial.getMaterialMation().setNormsSpec(null);
        if (CollectionUtil.isNotEmpty(shopMaterial.getShopMaterialNormsList())) {
            materialNormsService.setDataMation(shopMaterial.getShopMaterialNormsList(), ShopMaterialNorms::getNormsId);
            shopMaterial.getShopMaterialNormsList().forEach(shopMaterialNorms -> {
                shopMaterialNorms.setEstimatePurchasePrice(null);
            });
        }
        shopMaterial.setShopMaterialStore(shopMaterialStore);
        shopMaterial.setDefaultStoreId(shopMaterialStore.getStoreId());
        iShopStoreService.setDataMation(shopMaterial, ShopMaterial::getDefaultStoreId);

        outputObject.setBean(shopMaterial);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @IgnoreTenant
    public void queryShopMaterialByIds(InputObject inputObject, OutputObject outputObject) {
        String ids = inputObject.getParams().get("ids").toString();
        List<String> idList = Arrays.asList(ids.split(CommonCharConstants.COMMA_MARK))
            .stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(idList)) {
            return;
        }
        List<ShopMaterialStore> shopMaterialStoreList = selectByIds(idList.toArray(new String[]{}));
        if (CollectionUtil.isEmpty(shopMaterialStoreList)) {
            return;
        }

        List<String> materialIds = shopMaterialStoreList.stream()
            .map(ShopMaterialStore::getMaterialId).distinct().collect(Collectors.toList());
        Map<String, ShopMaterial> shopMaterialMap = shopMaterialService.queryShopMaterialByMaterialId(materialIds);
        List<ShopMaterial> shopMaterialList = new ArrayList<>();
        shopMaterialStoreList.forEach(shopMaterialStore -> {
            ShopMaterial shopMaterialBean = shopMaterialMap.get(shopMaterialStore.getMaterialId());
            if (ObjectUtil.isEmpty(shopMaterialBean)) {
                return;
            }
            ShopMaterial shopMaterial = new ShopMaterial();
            BeanUtil.copyProperties(shopMaterialBean, shopMaterial);
            if (ObjectUtil.isNotEmpty(shopMaterial.getMaterialMation())) {
                shopMaterial.getMaterialMation().setMaterialNorms(null);
                shopMaterial.getMaterialMation().setUnitGroupMation(null);
                shopMaterial.getMaterialMation().setMaterialProcedure(null);
                shopMaterial.getMaterialMation().setNormsSpec(null);
            }
            if (CollectionUtil.isNotEmpty(shopMaterial.getShopMaterialNormsList())) {
                shopMaterial.getShopMaterialNormsList().forEach(shopMaterialNorms -> {
                    shopMaterialNorms.setEstimatePurchasePrice(null);
                });
            }
            // 门店商品数据
            shopMaterial.setShopMaterialStore(shopMaterialStore);
            shopMaterial.setDefaultStoreId(shopMaterialStore.getStoreId());
            shopMaterialList.add(shopMaterial);
        });
        outputObject.setBeans(shopMaterialList);
        outputObject.settotal(shopMaterialList.size());
    }

    @Override
    @IgnoreTenant
    public void queryShopMaterialByMaterialIdAndStoreId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String materialId = params.get("materialId").toString();
        String storeId = params.get("storeId").toString();
        ShopMaterialStore shopMaterialStore = getOne(new QueryWrapper<ShopMaterialStore>()
            .eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), materialId)
            .eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), storeId));
        outputObject.setBean(shopMaterialStore);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @IgnoreTenant
    public Map<String, List<ShopMaterialStore>> queryShopMaterialListByStoreIds(List<String> storeIds, CommonPageInfo commonPageInfo) {
        if (CollectionUtil.isEmpty(storeIds)) {
            return MapUtil.empty();
        }
        MPJLambdaWrapper<ShopMaterialStore> queryWrapper = JoinWrappers.lambda("sms", ShopMaterialStore.class);
        queryWrapper.innerJoin(ShopMaterial.class, "sm", ShopMaterial::getMaterialId, ShopMaterialStore::getMaterialId);
        queryWrapper.in(ShopMaterialStore::getStoreId, storeIds);
        // 已经添加到门店
        queryWrapper.eq(ShopMaterialStore::getIsLaunchStore, WhetherEnum.ENABLE_USING.getKey());
        // 上架到商城
        queryWrapper.eq(ShopMaterialStore::getIsLaunchShop, WhetherEnum.ENABLE_USING.getKey());
        // 门店是启用状态的
        queryWrapper.eq(ShopMaterialStore::getStoreEnabled, EnableEnum.ENABLE_USING.getKey());
        // 设置商品查询的类型
        queryShopSelType(commonPageInfo, queryWrapper);

        List<ShopMaterialStore> shopMaterialStoreList = list(queryWrapper);
        List<String> materialIds = shopMaterialStoreList.stream()
            .map(ShopMaterialStore::getMaterialId).distinct().collect(Collectors.toList());
        Map<String, ShopMaterial> shopMaterialMap = shopMaterialService.queryShopMaterialByMaterialId(materialIds);
        shopMaterialStoreList.forEach(shopMaterialStore -> {
            ShopMaterial shopMaterial = shopMaterialMap.get(shopMaterialStore.getMaterialId());
            if (ObjectUtil.isEmpty(shopMaterial)) {
                return;
            }
            shopMaterial.getMaterialMation().setMaterialNorms(null);
            shopMaterial.getMaterialMation().setBrandMation(null);
            shopMaterial.getMaterialMation().setUnitGroupMation(null);
            shopMaterial.getMaterialMation().setFirstInUnitMation(null);
            shopMaterial.getMaterialMation().setFirstOutUnitMation(null);
            shopMaterial.getMaterialMation().setNormsSpec(null);
            shopMaterial.setContent(null);
            shopMaterialStore.setShopMaterial(shopMaterial);
        });
        Map<String, List<ShopMaterialStore>> collect = shopMaterialStoreList.stream().collect(Collectors.groupingBy(ShopMaterialStore::getStoreId, Collectors.collectingAndThen(
            Collectors.toList(), // 分组的 downstream
            list -> {
                if (list.size() > 8) {
                    return list.subList(0, 8); // 只取前8个元素
                }
                return list;
            }
        )));
        return collect;
    }

    @Override
    @IgnoreTenant
    public void queryShopMaterialMapByMaterialIdAndStoreId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        List<String> materialIdList = JSONUtil.toList(params.get("materialId").toString(), null);
        List<String> storeIdList = JSONUtil.toList(params.get("storeId").toString(), null);
        materialIdList = materialIdList.stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
        storeIdList = storeIdList.stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(materialIdList) || CollectionUtil.isEmpty(storeIdList)) {
            return;
        }
        if (materialIdList.size() != storeIdList.size()) {
            throw new CustomException("参数错误，materialId与storeId数量不一致");
        }
        QueryWrapper<ShopMaterialStore> queryWrapper = new QueryWrapper<>();
        for (int i = 0; i < storeIdList.size(); i++) {
            List<String> finalMaterialIdList = materialIdList;
            List<String> finalStoreIdList = storeIdList;
            int finalI = i;
            queryWrapper.or(wrapper -> {
                wrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), finalMaterialIdList.get(finalI))
                    .eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), finalStoreIdList.get(finalI));
            });
        }
        List<ShopMaterialStore> list = list(queryWrapper);
        Map<String, String> collect = list.stream()
            .collect(Collectors.toMap(bean -> String.format("%s_%s", bean.getMaterialId(), bean.getStoreId()), bean -> bean.getId()));
        outputObject.setBean(collect);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void deleteShopMaterialStoreByStoreIds(InputObject inputObject, OutputObject outputObject) {
        List<String> storeIdList = Arrays.asList(inputObject.getParams().get("storeIds").toString()
                .split(CommonCharConstants.COMMA_MARK))
            .stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(storeIdList)) {
            return;
        }
        // 设置该门店的状态为禁用
        UpdateWrapper<ShopMaterialStore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), storeIdList);
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreEnabled), EnableEnum.DISABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    public void addShopMaterialStore(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = params.get("storeId").toString();
        String materialId = params.get("materialId").toString();

        UpdateWrapper<ShopMaterialStore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), storeId)
            .eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), materialId)
            .eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchStore), WhetherEnum.DISABLE_USING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchStore), WhetherEnum.ENABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    public void deleteShopMaterialStore(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = params.get("storeId").toString();
        String materialId = params.get("materialId").toString();

        UpdateWrapper<ShopMaterialStore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), storeId)
            .eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), materialId)
            .eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchStore), WhetherEnum.ENABLE_USING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchStore), WhetherEnum.DISABLE_USING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchShop), WhetherEnum.DISABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    public void launchShopMaterialStore(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = params.get("storeId").toString();
        List<String> materialIds = Arrays.asList(params.get("materialIds").toString()
                .split(CommonCharConstants.COMMA_MARK))
            .stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(materialIds)) {
            return;
        }
        Map<String, Object> storeMation = iShopStoreService.queryDataMationById(storeId);
        if (storeMation == null) {
            throw new CustomException("门店不存在");
        }
        UpdateWrapper<ShopMaterialStore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), storeId)
            .in(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), materialIds);
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchShop), WhetherEnum.ENABLE_USING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreEnabled), storeMation.get("enabled"));
        update(updateWrapper);
    }

    @Override
    public void unlaunchShopMaterialStore(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = params.get("storeId").toString();
        List<String> materialIds = Arrays.asList(params.get("materialIds").toString()
                .split(CommonCharConstants.COMMA_MARK))
            .stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(materialIds)) {
            return;
        }
        UpdateWrapper<ShopMaterialStore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), storeId)
            .in(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), materialIds);
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchShop), WhetherEnum.DISABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    @IgnoreTenant
    public void getAllowedShopMaterialList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 获取适用于指定门店的商品数据
        List<ShopMaterialStore> shopMaterialStoreList = selectByStoreId(commonPageInfo.getObjectId(), null, null,
            commonPageInfo.getKeyword());
        if (CollectionUtil.isEmpty(shopMaterialStoreList)) {
            return;
        }
        List<ShopMaterial> shopMaterialList = getShopMaterialList(shopMaterialStoreList);
        outputObject.setBeans(shopMaterialList);
        outputObject.settotal(pages.getTotal());
    }

    @NotNull
    private List<ShopMaterial> getShopMaterialList(List<ShopMaterialStore> shopMaterialStoreList) {
        List<String> materialIds = shopMaterialStoreList.stream()
            .map(ShopMaterialStore::getMaterialId).distinct().collect(Collectors.toList());
        Map<String, ShopMaterial> shopMaterialMap = shopMaterialService.queryShopMaterialByMaterialId(materialIds);
        List<ShopMaterial> shopMaterialList = new ArrayList<>();
        shopMaterialStoreList.forEach(shopMaterialStore -> {
            ShopMaterial shopMaterial = shopMaterialMap.get(shopMaterialStore.getMaterialId());
            if (ObjectUtil.isEmpty(shopMaterial)) {
                return;
            }
            shopMaterial.getMaterialMation().setMaterialNorms(null);
            shopMaterial.getMaterialMation().setBrandMation(null);
            shopMaterial.getMaterialMation().setUnitGroupMation(null);
            shopMaterial.getMaterialMation().setFirstInUnitMation(null);
            shopMaterial.getMaterialMation().setFirstOutUnitMation(null);
            shopMaterial.getMaterialMation().setNormsSpec(null);
            shopMaterial.setShopMaterialStore(shopMaterialStore);
            shopMaterial.setDefaultStoreId(shopMaterialStore.getStoreId());
            shopMaterialList.add(shopMaterial);
        });
        return shopMaterialList;
    }

    @Override
    @IgnoreTenant
    public void getAddedShopMaterialList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 获取该门店新增的商品数据
        List<ShopMaterialStore> shopMaterialStoreList = selectByStoreId(commonPageInfo.getObjectId(), WhetherEnum.ENABLE_USING.getKey(), null,
            commonPageInfo.getKeyword());
        if (CollectionUtil.isEmpty(shopMaterialStoreList)) {
            return;
        }
        List<ShopMaterial> shopMaterialList = getShopMaterialList(shopMaterialStoreList);
        outputObject.setBeans(shopMaterialList);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    @IgnoreTenant
    public void getLaunchedShopMaterialList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 获取该门店已经上架到商城的商品数据
        List<ShopMaterialStore> shopMaterialStoreList = selectByStoreId(commonPageInfo.getObjectId(), WhetherEnum.ENABLE_USING.getKey(), WhetherEnum.ENABLE_USING.getKey(),
            commonPageInfo.getKeyword());
        if (CollectionUtil.isEmpty(shopMaterialStoreList)) {
            return;
        }
        List<ShopMaterial> shopMaterialList = getShopMaterialList(shopMaterialStoreList);
        outputObject.setBeans(shopMaterialList);
        outputObject.settotal(pages.getTotal());
    }

}
