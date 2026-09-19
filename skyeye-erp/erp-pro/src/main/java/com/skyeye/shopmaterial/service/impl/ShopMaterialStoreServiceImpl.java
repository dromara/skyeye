/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shopmaterial.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
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
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.enumeration.ShopMaterialDeliveryMethod;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantAopUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialFromType;
import com.skyeye.material.classenum.MaterialItemCode;
import com.skyeye.material.classenum.MaterialType;
import com.skyeye.material.classenum.MaterialUnit;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.rest.shop.service.IShopStoreService;
import com.skyeye.shopmaterial.dao.ShopMaterialDao;
import com.skyeye.shopmaterial.dao.ShopMaterialStoreDao;
import com.skyeye.shopmaterial.entity.ShopMaterial;
import com.skyeye.shopmaterial.entity.ShopMaterialNorms;
import com.skyeye.shopmaterial.entity.ShopMaterialStore;
import com.skyeye.shopmaterial.enums.ShopMaterialDistributionType;
import com.skyeye.shopmaterial.enums.ShopMaterialNormsLogoType;
import com.skyeye.shopmaterial.enums.ShopMaterialStoreCoverage;
import com.skyeye.shopmaterial.service.ShopMaterialService;
import com.skyeye.shopmaterial.service.ShopMaterialStoreService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private MaterialService materialService;

    @Autowired
    private ShopMaterialDao shopMaterialDao;

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
        if (tenantEnable && StrUtil.isNotEmpty(TenantContext.getTenantId())) {
            // 只限定门店商品这一侧。平台货可能来自别的租户，商品表不能再卡 tenant_id
            queryWrapper.eq("i." + CommonConstants.TENANT_ID_FIELD, TenantContext.getTenantId());
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
        // 先解析优惠券范围，避免分页后再过滤导致空页；也避免 PageHelper 提前占用线程变量
        CouponScopeFilter couponScopeFilter = resolveCouponScopeFilter(commonPageInfo);
        if (couponScopeFilter != null && couponScopeFilter.empty) {
            return Collections.emptyList();
        }
        // 门店id + 优惠券id 查适用商品：跳过 shopType 配送过滤；仅同城编码由上层写到 shopMaterial.returnCode
        boolean couponAndStoreQuery = couponScopeFilter != null && StrUtil.isNotBlank(commonPageInfo.getObjectId());

        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 商品名称，型号，门店，品牌
        MPJLambdaWrapper<ShopMaterialStore> wrapper = JoinWrappers.lambda("sms", ShopMaterialStore.class);
        wrapper.innerJoin(Material.class, "m", Material::getId, ShopMaterialStore::getMaterialId)
            .innerJoin(ShopMaterial.class, "sm", ShopMaterial::getMaterialId, ShopMaterialStore::getMaterialId);
        // 门店已删除或不存在时不返回商品。条件写进分页 SQL，避免查出后再过滤把某一页挤空
        wrapper.innerJoin("shop_store ss ON ss.id = sms.store_id");
        if (couponScopeFilter != null) {
            applyCouponScopeFilter(wrapper, couponScopeFilter);
        }
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
        // 普通商城列表仍按 shopType 过滤；门店+优惠券查适用商品不按配送方式过滤
        if (!couponAndStoreQuery) {
            queryShopSelType(commonPageInfo, wrapper);
        }
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

    @Override
    @IgnoreTenant
    public List<ShopMaterialStore> queryCouponApplicableMaterialStoreList(String couponId, List<String> storeIdList) {
        if (StrUtil.isBlank(couponId) || CollectionUtil.isEmpty(storeIdList)) {
            return Collections.emptyList();
        }
        List<String> requestStoreIdList = storeIdList.stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(requestStoreIdList)) {
            return Collections.emptyList();
        }
        CouponScopeFilter couponScopeFilter = resolveCouponScopeFilterForStores(couponId, requestStoreIdList);
        if (couponScopeFilter.empty || CollectionUtil.isEmpty(couponScopeFilter.storeIdList)) {
            return Collections.emptyList();
        }
        MPJLambdaWrapper<ShopMaterialStore> wrapper = JoinWrappers.lambda("sms", ShopMaterialStore.class);
        wrapper.innerJoin(Material.class, "m", Material::getId, ShopMaterialStore::getMaterialId)
            .innerJoin(ShopMaterial.class, "sm", ShopMaterial::getMaterialId, ShopMaterialStore::getMaterialId);
        applyCouponScopeFilter(wrapper, couponScopeFilter);
        wrapper.in(ShopMaterialStore::getStoreId, couponScopeFilter.storeIdList);
        // 已经添加到门店
        wrapper.eq(ShopMaterialStore::getIsLaunchStore, WhetherEnum.ENABLE_USING.getKey());
        // 上架到商城
        wrapper.eq(ShopMaterialStore::getIsLaunchShop, WhetherEnum.ENABLE_USING.getKey());
        // 门店是启用状态的
        wrapper.eq(ShopMaterialStore::getStoreEnabled, EnableEnum.ENABLE_USING.getKey());
        List<ShopMaterialStore> shopMaterialStoreList = skyeyeBaseMapper.selectJoinList(ShopMaterialStore.class, wrapper);
        return shopMaterialStoreList;
    }

    /**
     * 批量门店场景解析优惠券范围：与单店 resolveCouponScopeFilter 同源，门店做交集。
     */
    private CouponScopeFilter resolveCouponScopeFilterForStores(String couponId, List<String> requestStoreIdList) {
        Map<String, Object> coupon = iShopStoreService.queryCouponById(couponId);
        CouponScopeFilter filter = new CouponScopeFilter();
        if (MapUtil.isEmpty(coupon)) {
            filter.empty = true;
            return filter;
        }
        filter.allMaterial = Objects.equals(MapUtil.getInt(coupon, "productScope"), CommonNumConstants.NUM_ONE);
        filter.allStore = Objects.equals(MapUtil.getInt(coupon, "storeCoverage"), CommonNumConstants.NUM_ONE);
        if (!filter.allStore) {
            List<String> couponStoreIdList = Convert.toList(String.class, coupon.get("storeIdList"));
            if (CollectionUtil.isEmpty(couponStoreIdList)) {
                filter.empty = true;
                return filter;
            }
            List<String> intersectStoreIdList = requestStoreIdList.stream()
                .filter(couponStoreIdList::contains).distinct().collect(Collectors.toList());
            if (CollectionUtil.isEmpty(intersectStoreIdList)) {
                filter.empty = true;
                return filter;
            }
            filter.storeIdList = intersectStoreIdList;
        } else {
            filter.storeIdList = requestStoreIdList;
        }
        if (!filter.allMaterial) {
            List<Map> materialList = Convert.toList(Map.class, coupon.get("couponMaterialList"));
            List<String> materialIdList = CollectionUtil.isEmpty(materialList) ? Collections.emptyList()
                : materialList.stream()
                .map(item -> MapUtil.getStr(item, "materialId"))
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(materialIdList)) {
                filter.empty = true;
                return filter;
            }
            filter.materialIdList = materialIdList;
        }
        return filter;
    }

    /**
     * 解析 customParamsMap.couponId 对应的优惠券适用范围（通过 queryCouponById）。
     * null 表示未传 couponId；empty=true 表示无适用数据。
     */
    private CouponScopeFilter resolveCouponScopeFilter(CommonPageInfo commonPageInfo) {
        String couponId = commonPageInfo.getCustomParamsMapStr("couponId");
        if (StrUtil.isBlank(couponId)) {
            return null;
        }
        Map<String, Object> coupon = iShopStoreService.queryCouponById(couponId);
        CouponScopeFilter filter = new CouponScopeFilter();
        if (MapUtil.isEmpty(coupon)) {
            filter.empty = true;
            return filter;
        }
        // productScope=1 全部商品；storeCoverage=1 全部门店
        filter.allMaterial = Objects.equals(MapUtil.getInt(coupon, "productScope"), CommonNumConstants.NUM_ONE);
        filter.allStore = Objects.equals(MapUtil.getInt(coupon, "storeCoverage"), CommonNumConstants.NUM_ONE);
        String storeId = commonPageInfo.getObjectId();
        if (!filter.allStore) {
            List<String> storeIdList = Convert.toList(String.class, coupon.get("storeIdList"));
            if (CollectionUtil.isEmpty(storeIdList) || StrUtil.isBlank(storeId) || !storeIdList.contains(storeId)) {
                filter.empty = true;
                return filter;
            }
        }
        if (!filter.allMaterial) {
            List<Map> materialList = Convert.toList(Map.class, coupon.get("couponMaterialList"));
            List<String> materialIdList = CollectionUtil.isEmpty(materialList) ? Collections.emptyList()
                : materialList.stream()
                .map(item -> MapUtil.getStr(item, "materialId"))
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(materialIdList)) {
                filter.empty = true;
                return filter;
            }
            filter.materialIdList = materialIdList;
        }
        return filter;
    }

    private void applyCouponScopeFilter(MPJLambdaWrapper<ShopMaterialStore> wrapper, CouponScopeFilter filter) {
        if (CollectionUtil.isNotEmpty(filter.materialIdList)) {
            wrapper.in(ShopMaterialStore::getMaterialId, filter.materialIdList);
        }
    }

    private static class CouponScopeFilter {
        private boolean empty;
        private boolean allStore;
        private boolean allMaterial;
        private List<String> materialIdList;
        private List<String> storeIdList;
    }

    private static void queryShopSelType(CommonPageInfo commonPageInfo, MPJLambdaWrapper<ShopMaterialStore> wrapper) {
        String deliveryMethodColumn = MybatisPlusUtil.toColumns(ShopMaterial::getDeliveryMethod);
        if (StrUtil.equals(commonPageInfo.getCustomParamsMapStr("shopType"), "sameCity")) {
            // 同城的商品 - 配送方式包含"同城配送"（key=3）
            // deliveryMethod存储的是JSON字符串数组，如["1","2","3"]，使用LIKE查询字符串"3"
            Integer key = ShopMaterialDeliveryMethod.LOCAL_DELIVERY.getKey();
            wrapper.apply("sm." + deliveryMethodColumn + " LIKE {0}",
                "%\"" + key + "\"%");
            wrapper.apply("sms." + deliveryMethodColumn + " LIKE {0}",
                "%\"" + key + "\"%");
        } else if (StrUtil.equals(commonPageInfo.getCustomParamsMapStr("shopType"), "mallProducts")) {
            // 可以邮寄的商品 - 配送方式包含"快递发货"（key=1）
            Integer expressKey = ShopMaterialDeliveryMethod.EXPRESS_DELIVERY.getKey();
            wrapper.apply("sm." + deliveryMethodColumn + " LIKE {0}",
                "%\"" + expressKey + "\"%");
            wrapper.apply("sms." + deliveryMethodColumn + " LIKE {0}",
                "%\"" + expressKey + "\"%");
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
        // 同城只出上架时选了线下的商品；历史数据没填经营方式的仍保留
        queryWrapper.and(wra -> wra.isNull(ShopMaterialStore::getSaleChannel)
            .or().apply("JSON_CONTAINS(sms.sale_channel, '\"2\"')"));
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
    @IgnoreTenant
    public void queryShopMaterialMapByMaterialIdsAndStoreIds(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        List<String> materialIdList = JSONUtil.toList(params.get("materialId").toString(), null);
        List<String> storeIdList = JSONUtil.toList(params.get("storeId").toString(), null);
        materialIdList = materialIdList.stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        storeIdList = storeIdList.stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(materialIdList) || CollectionUtil.isEmpty(storeIdList)) {
            return;
        }
        QueryWrapper<ShopMaterialStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), materialIdList)
            .in(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), storeIdList);
        List<ShopMaterialStore> list = list(queryWrapper);
        Map<String, String> collect = list.stream()
            .collect(Collectors.toMap(bean -> String.format("%s_%s", bean.getMaterialId(), bean.getStoreId()),
                bean -> bean.getId(), (a, b) -> a));
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
        List<String> deliveryMethod = JSONUtil.toList(params.get("deliveryMethod").toString(), null);
        List<String> saleChannel = JSONUtil.toList(params.get("saleChannel").toString(), null);
        Map<String, Object> storeMation = iShopStoreService.queryDataMationById(storeId);
        if (storeMation == null) {
            throw new CustomException("门店不存在");
        }
        UpdateWrapper<ShopMaterialStore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), storeId)
            .in(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), materialIds);
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchShop), WhetherEnum.ENABLE_USING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreEnabled), storeMation.get("enabled"));
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getDeliveryMethod), JSONUtil.toJsonStr(deliveryMethod));
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getSaleChannel), JSONUtil.toJsonStr(saleChannel));
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

    private Map<String, Object> assertPersonalStoreOwner(String storeId) {
        if (StrUtil.isEmpty(storeId)) {
            throw new CustomException("请选择门店");
        }
        Map<String, Object> storeMation = iShopStoreService.queryDataMationById(storeId);
        if (CollectionUtil.isEmpty(storeMation) || StrUtil.isEmpty(MapUtil.getStr(storeMation, "id"))) {
            throw new CustomException("门店不存在");
        }
        String memberId = InputObject.getLogParamsStatic().get("id").toString();
        if (!memberId.equals(MapUtil.getStr(storeMation, "createId"))) {
            throw new CustomException("无权操作该门店");
        }
        if (!Integer.valueOf(2).equals(Convert.toInt(storeMation.get("storeNature")))) {
            throw new CustomException("只能操作个人门店");
        }
        return storeMation;
    }

    @Override
    @IgnoreTenant
    public void queryPersonalStoreMaterialList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        assertPersonalStoreOwner(commonPageInfo.getObjectId());
        getAddedShopMaterialList(inputObject, outputObject);
    }

    @Override
    @IgnoreTenant
    public void queryPlatformMaterialForPersonalStore(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String storeId = commonPageInfo.getObjectId();
        assertPersonalStoreOwner(storeId);
        List<ShopMaterialStore> addedList = selectByStoreId(storeId, WhetherEnum.ENABLE_USING.getKey(), null, null);
        List<String> addedMaterialIds = addedList.stream().map(ShopMaterialStore::getMaterialId)
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 跟商城列表同一口径：已加入门店、已上架、门店启用。不按当前租户收口，否则搜不到别的店已在卖的货
        MPJLambdaWrapper<ShopMaterialStore> wrapper = JoinWrappers.lambda("sms", ShopMaterialStore.class);
        wrapper.innerJoin(Material.class, "m", Material::getId, ShopMaterialStore::getMaterialId);
        wrapper.innerJoin(ShopMaterial.class, "sm", ShopMaterial::getMaterialId, ShopMaterialStore::getMaterialId);
        wrapper.eq(ShopMaterialStore::getIsLaunchStore, WhetherEnum.ENABLE_USING.getKey());
        wrapper.eq(ShopMaterialStore::getIsLaunchShop, WhetherEnum.ENABLE_USING.getKey());
        wrapper.eq(ShopMaterialStore::getStoreEnabled, EnableEnum.ENABLE_USING.getKey());
        wrapper.and(wra -> wra.isNull(ShopMaterial::getAllowPlatformSource)
            .or().eq(ShopMaterial::getAllowPlatformSource, WhetherEnum.ENABLE_USING.getKey()));
        wrapper.and(wra -> wra.eq(ShopMaterial::getAllowPlatformSource, WhetherEnum.ENABLE_USING.getKey())
            .or().isNull(ShopMaterial::getStoreSelfMade)
            .or().ne(ShopMaterial::getStoreSelfMade, WhetherEnum.ENABLE_USING.getKey()));
        if (CollectionUtil.isNotEmpty(addedMaterialIds)) {
            wrapper.notIn(ShopMaterialStore::getMaterialId, addedMaterialIds);
        }
        if (StrUtil.isNotBlank(commonPageInfo.getKeyword())) {
            wrapper.and(wra -> {
                wra.or().like("m." + MybatisPlusUtil.toColumns(Material::getName), commonPageInfo.getKeyword());
                wra.or().like("m." + MybatisPlusUtil.toColumns(Material::getModel), commonPageInfo.getKeyword());
            });
        }
        wrapper.groupBy(ShopMaterialStore::getMaterialId);
        wrapper.select(ShopMaterialStore::getMaterialId);
        List<ShopMaterialStore> pageList = skyeyeBaseMapper.selectJoinList(ShopMaterialStore.class, wrapper);
        if (CollectionUtil.isEmpty(pageList)) {
            outputObject.setBeans(new ArrayList<>());
            outputObject.settotal(pages.getTotal());
            return;
        }
        List<String> materialIds = pageList.stream().map(ShopMaterialStore::getMaterialId)
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        Map<String, ShopMaterial> shopMaterialMap = shopMaterialService.queryShopMaterialByMaterialId(materialIds);
        List<ShopMaterial> result = new ArrayList<>();
        materialIds.forEach(materialId -> {
            ShopMaterial shopMaterial = shopMaterialMap.get(materialId);
            if (shopMaterial != null) {
                result.add(shopMaterial);
            }
        });
        outputObject.setBeans(result);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void choosePlatformMaterialForPersonalStore(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = params.get("storeId").toString();
        String materialId = params.get("materialId").toString();
        Map<String, Object> storeMation = assertPersonalStoreOwner(storeId);
        ShopMaterial shopMaterial = shopMaterialService.queryShopMaterialByMaterialId(materialId);
        if (shopMaterial == null || StrUtil.isEmpty(shopMaterial.getId())) {
            throw new CustomException("平台货源不存在");
        }
        QueryWrapper<ShopMaterialStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), storeId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), materialId);
        ShopMaterialStore old = getOne(queryWrapper, false);
        if (old != null && WhetherEnum.ENABLE_USING.getKey().equals(old.getIsLaunchStore())) {
            throw new CustomException("该商品已在门店中");
        }
        List<String> deliveryMethod = shopMaterial.getDeliveryMethod();
        if (CollectionUtil.isEmpty(deliveryMethod)) {
            deliveryMethod = Collections.singletonList(String.valueOf(ShopMaterialDeliveryMethod.EXPRESS_DELIVERY.getKey()));
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Integer storeEnabled = Convert.toInt(storeMation.get("enabled"), EnableEnum.ENABLE_USING.getKey());
        if (old == null) {
            ShopMaterialStore shopMaterialStore = new ShopMaterialStore();
            shopMaterialStore.setStoreId(storeId);
            shopMaterialStore.setMaterialId(materialId);
            shopMaterialStore.setBigTypeId(shopMaterial.getBigTypeId());
            shopMaterialStore.setIsLaunchStore(WhetherEnum.ENABLE_USING.getKey());
            shopMaterialStore.setIsLaunchShop(WhetherEnum.DISABLE_USING.getKey());
            shopMaterialStore.setStoreEnabled(storeEnabled);
            shopMaterialStore.setDeliveryMethod(deliveryMethod);
            createEntity(shopMaterialStore, userId);
            return;
        }
        old.setIsLaunchStore(WhetherEnum.ENABLE_USING.getKey());
        old.setStoreEnabled(storeEnabled);
        old.setBigTypeId(shopMaterial.getBigTypeId());
        if (CollectionUtil.isEmpty(old.getDeliveryMethod())) {
            old.setDeliveryMethod(deliveryMethod);
        }
        updateById(old);
    }

    @Override
    public void launchPersonalStoreMaterial(InputObject inputObject, OutputObject outputObject) {
        assertPersonalStoreOwner(inputObject.getParams().get("storeId").toString());
        launchShopMaterialStore(inputObject, outputObject);
    }

    @Override
    public void unlaunchPersonalStoreMaterial(InputObject inputObject, OutputObject outputObject) {
        assertPersonalStoreOwner(inputObject.getParams().get("storeId").toString());
        unlaunchShopMaterialStore(inputObject, outputObject);
    }

    @Override
    public void removePersonalStoreMaterial(InputObject inputObject, OutputObject outputObject) {
        assertPersonalStoreOwner(inputObject.getParams().get("storeId").toString());
        deleteShopMaterialStore(inputObject, outputObject);
    }

    @Override
    public void queryPersonalStoreMaterialCategory(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> dictList = iSysDictDataService.queryDictDataListByDictTypeCode("ERP_MATERIAL_CATEGORY");
        List<Map<String, Object>> result = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dictList)) {
            dictList.forEach(row -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", row.get("id"));
                item.put("name", row.get("dictName"));
                result.add(item);
            });
        }
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    /**
     * 个人店自建商品。店主只填一次，后台按 ERP 商品、商城商品、本店货架这个顺序一次写完。
     * 规格数据跟 ERP 商品规格组件同一套：单规格写计量单位，多规格写单位组、出入库单位和规格行。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPersonalStoreMaterial(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = params.get("storeId").toString();
        String name = params.get("name").toString();
        String logo = params.get("logo").toString();
        String salePrice = params.get("salePrice").toString();
        String model = params.get("model").toString();
        String content = params.get("content").toString();
        String categoryId = params.get("categoryId").toString();
        Map<String, Object> storeMation = assertPersonalStoreOwner(storeId);
        checkSalePrice(salePrice);
        List<String> deliveryMethod = JSONUtil.toList(params.get("deliveryMethod").toString(), String.class);
        JSONObject sku = JSONUtil.parseObj(params.get("skuData").toString());
        Integer unit = Convert.toInt(sku.get("unit"));
        if (unit == null) {
            throw new CustomException("请选择规格类型");
        }
        boolean multiSpec = MaterialUnit.MULTI_SPECIFICATION.getKey().equals(unit);
        JSONArray normsArray = sku.getJSONArray("materialNorms");
        List<JSONObject> normsRows = new ArrayList<>();
        if (normsArray != null) {
            for (int i = 0; i < normsArray.size(); i++) {
                normsRows.add(normsArray.getJSONObject(i));
            }
        }
        if (normsRows.isEmpty()) {
            throw new CustomException("请填写销售规格");
        }
        if (!multiSpec && normsRows.size() > 1) {
            normsRows = normsRows.subList(0, 1);
        }
        if (multiSpec && CollectionUtil.isNotEmpty(sku.getJSONArray("normsSpec")) && normsRows.size() < 2) {
            throw new CustomException("多规格至少要两种售卖规格");
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String materialId = createErpMaterial(userId, name, model, categoryId, logo, sku, multiSpec, normsRows);
        createShopMaterial(userId, materialId, storeId, logo, content, deliveryMethod, normsRows);
        hangOnPersonalStore(userId, storeId, materialId, deliveryMethod, storeMation);
    }

    private void checkSalePrice(String salePrice) {
        double price;
        try {
            price = Double.parseDouble(salePrice);
        } catch (Exception e) {
            throw new CustomException("售价格式不对");
        }
        if (price <= 0) {
            throw new CustomException("售价要大于 0");
        }
    }

    private List<Map<String, Object>> parseNormsSpec(String normsSpecJson) {
        JSONArray specArray = JSONUtil.parseArray(normsSpecJson);
        List<Map<String, Object>> normsSpec = new ArrayList<>();
        for (int i = 0; i < specArray.size(); i++) {
            normsSpec.add(specArray.getJSONObject(i));
        }
        return normsSpec;
    }

    private String createErpMaterial(String userId, String name, String model, String categoryId, String logo,
                                     JSONObject sku, boolean multiSpec, List<JSONObject> normsRows) {
        Material material = new Material();
        material.setName(name);
        material.setModel(model);
        material.setCategoryId(categoryId);
        material.setEnabled(EnableEnum.ENABLE_USING.getKey());
        material.setFromType(MaterialFromType.SELF_PRODUCED.getKey());
        material.setType(MaterialType.FINISHED_PRODUCT.getKey());
        material.setItemCode(MaterialItemCode.DISABLE.getKey());
        if (multiSpec) {
            material.setUnit(MaterialUnit.MULTI_SPECIFICATION.getKey());
            material.setUnitGroupId(sku.getStr("unitGroupId"));
            material.setFirstInUnit(sku.getStr("firstInUnit"));
            material.setFirstOutUnit(sku.getStr("firstOutUnit"));
            JSONArray specArray = sku.getJSONArray("normsSpec");
            material.setNormsSpec(specArray == null ? new ArrayList<>() : parseNormsSpec(specArray.toString()));
        } else {
            String unitName = sku.getStr("unitName");
            if (StrUtil.isBlank(unitName)) {
                throw new CustomException("请填写计量单位");
            }
            material.setUnit(MaterialUnit.SINGLE_SPECIFICATION.getKey());
            material.setUnitName(unitName);
            material.setNormsSpec(new ArrayList<>());
        }

        List<MaterialNorms> materialNormsList = new ArrayList<>();
        for (int i = 0; i < normsRows.size(); i++) {
            JSONObject row = normsRows.get(i);
            String rowPrice = row.getStr("salePrice");
            checkSalePrice(rowPrice);
            String tableNum = row.getStr("tableNum");
            if (StrUtil.isEmpty(tableNum)) {
                tableNum = multiSpec ? String.valueOf(i + 1) : "simpleNorms";
                row.set("tableNum", tableNum);
            }
            MaterialNorms norms = new MaterialNorms();
            norms.setTableNum(tableNum);
            norms.setLogo(StrUtil.isEmpty(row.getStr("logo")) ? logo : row.getStr("logo"));
            String safetyTock = row.getStr("safetyTock");
            norms.setSafetyTock(StrUtil.isBlank(safetyTock) ? "0" : safetyTock);
            norms.setRetailPrice(priceOr(row, "retailPrice", rowPrice));
            norms.setLowPrice(priceOr(row, "lowPrice", rowPrice));
            norms.setEstimatePurchasePrice(priceOr(row, "estimatePurchasePrice", rowPrice));
            norms.setSalePrice(rowPrice);
            norms.setEnabled(Convert.toInt(row.get("enabled"), EnableEnum.ENABLE_USING.getKey()));
            norms.setOrderBy(Convert.toInt(row.get("orderBy"), i + 1));
            norms.setNormsStock(new ArrayList<>());
            materialNormsList.add(norms);
        }
        material.setMaterialNorms(materialNormsList);
        return materialService.createEntity(material, userId);
    }

    private String priceOr(JSONObject row, String field, String fallback) {
        String value = row.getStr(field);
        return StrUtil.isBlank(value) ? fallback : value;
    }

    private void createShopMaterial(String userId, String materialId, String storeId, String logo, String content,
                                    List<String> deliveryMethod, List<JSONObject> normsRows) {
        List<MaterialNorms> normsList = materialNormsService.queryNormsUnitListByMaterialId(materialId);
        if (CollectionUtil.isEmpty(normsList)) {
            throw new CustomException("ERP规格没有生成，商品建不下去");
        }
        Map<String, MaterialNorms> savedMap = normsList.stream().collect(Collectors.toMap(MaterialNorms::getTableNum, item -> item, (a, b) -> a));
        String defaultTableNum = normsRows.get(0).getStr("tableNum");
        for (JSONObject row : normsRows) {
            if ("1".equals(row.getStr("isDefault"))) {
                defaultTableNum = row.getStr("tableNum");
                break;
            }
        }
        List<ShopMaterialNorms> shopNormsList = new ArrayList<>();
        for (JSONObject row : normsRows) {
            MaterialNorms saved = savedMap.get(row.getStr("tableNum"));
            if (saved == null) {
                continue;
            }
            String rowPrice = row.getStr("salePrice");
            ShopMaterialNorms shopNorms = new ShopMaterialNorms();
            shopNorms.setNormsId(saved.getId());
            shopNorms.setIsDefault(row.getStr("tableNum").equals(defaultTableNum) ? IsDefaultEnum.IS_DEFAULT.getKey() : IsDefaultEnum.NOT_DEFAULT.getKey());
            shopNorms.setEstimatePurchasePrice(rowPrice);
            shopNorms.setSalePrice(rowPrice);
            shopNorms.setLogoType(StrUtil.isEmpty(row.getStr("logo")) ? ShopMaterialNormsLogoType.FOLLOW_GOODS.getKey() : ShopMaterialNormsLogoType.SINGLE_SET.getKey());
            if (StrUtil.isNotEmpty(row.getStr("logo"))) {
                shopNorms.setLogo(row.getStr("logo"));
            }
            shopNormsList.add(shopNorms);
        }
        ShopMaterial shopMaterial = new ShopMaterial();
        shopMaterial.setMaterialId(materialId);
        shopMaterial.setContent(content);
        shopMaterial.setLogo(logo);
        shopMaterial.setCarouselImg(logo);
        shopMaterial.setDistributionType(ShopMaterialDistributionType.DEFAULT_SET.getKey());
        shopMaterial.setDeliveryMethod(deliveryMethod);
        shopMaterial.setOrderBy(1);
        shopMaterial.setGiftPoint(0);
        shopMaterial.setVirtualSales("0");
        shopMaterial.setStoreCoverage(ShopMaterialStoreCoverage.SPECIFIED_STORE.getKey());
        shopMaterial.setStoreIds(Collections.singletonList(storeId));
        // 门店自制。选平台货源时排除，避免串到别的店
        shopMaterial.setStoreSelfMade(WhetherEnum.ENABLE_USING.getKey());
        shopMaterial.setShopMaterialNormsList(shopNormsList);
        shopMaterialService.createEntity(shopMaterial, userId);
    }

    private void hangOnPersonalStore(String userId, String storeId, String materialId, List<String> deliveryMethod, Map<String, Object> storeMation) {
        Integer storeEnabled = Convert.toInt(storeMation.get("enabled"), EnableEnum.ENABLE_USING.getKey());
        QueryWrapper<ShopMaterialStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), storeId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), materialId);
        ShopMaterialStore relation = getOne(queryWrapper, false);
        if (relation == null) {
            ShopMaterialStore row = new ShopMaterialStore();
            row.setStoreId(storeId);
            row.setMaterialId(materialId);
            row.setIsLaunchStore(WhetherEnum.ENABLE_USING.getKey());
            row.setIsLaunchShop(WhetherEnum.ENABLE_USING.getKey());
            row.setStoreEnabled(storeEnabled);
            row.setDeliveryMethod(deliveryMethod);
            createEntity(row, userId);
            return;
        }
        UpdateWrapper<ShopMaterialStore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getId), relation.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchStore), WhetherEnum.ENABLE_USING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchShop), WhetherEnum.ENABLE_USING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreEnabled), storeEnabled);
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getDeliveryMethod), JSONUtil.toJsonStr(deliveryMethod));
        update(updateWrapper);
    }

}
