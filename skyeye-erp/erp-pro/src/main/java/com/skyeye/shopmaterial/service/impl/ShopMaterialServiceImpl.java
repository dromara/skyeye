/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shopmaterial.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.brand.entity.Brand;
import com.skyeye.brand.service.BrandService;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.ShopMaterialDeliveryMethod;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.material.classenum.MaterialShelvesState;
import com.skyeye.material.entity.Material;
import com.skyeye.material.service.MaterialService;
import com.skyeye.rest.shop.service.IShopStoreService;
import com.skyeye.shopmaterial.dao.ShopMaterialDao;
import com.skyeye.shopmaterial.entity.ShopMaterial;
import com.skyeye.shopmaterial.entity.ShopMaterialNorms;
import com.skyeye.shopmaterial.entity.ShopMaterialStore;
import com.skyeye.shopmaterial.enums.ShopMaterialPcTipCode;
import com.skyeye.shopmaterial.enums.ShopMaterialStoreCoverage;
import com.skyeye.shopmaterial.service.ShopMaterialNormsService;
import com.skyeye.shopmaterial.service.ShopMaterialService;
import com.skyeye.shopmaterial.service.ShopMaterialStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: ShopMaterialServiceImpl
 * @Description: 商城商品服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/4 17:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商城商品", groupName = "商城商品")
public class ShopMaterialServiceImpl extends SkyeyeBusinessServiceImpl<ShopMaterialDao, ShopMaterial> implements ShopMaterialService {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private ShopMaterialNormsService shopMaterialNormsService;

    @Autowired
    private ShopMaterialStoreService shopMaterialStoreService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private IShopStoreService iShopStoreService;

    @Override
    public void createPrepose(ShopMaterial entity) {
        entity.setRealSales(CommonNumConstants.NUM_ZERO.toString());
    }

    @Override
    public void writePostpose(ShopMaterial entity, String userId) {
        // 保存商城商品规格
        if (CollectionUtil.isNotEmpty(entity.getShopMaterialNormsList())) {
            entity.getShopMaterialNormsList().forEach(norms -> {
                norms.setBigTypeId(entity.getBigTypeId());
            });
        }
        shopMaterialNormsService.saveList(entity.getMaterialId(), entity.getShopMaterialNormsList());

        // 更新商品的上架状态
        Material material = materialService.selectById(entity.getMaterialId());
        if (CollectionUtil.isEmpty(entity.getShopMaterialNormsList())) {
            // 未上架或者取消上架
            materialService.setShelvesState(material.getId(), MaterialShelvesState.NOT_ON_SHELVE.getKey());
            shopMaterialStoreService.deleteByMaterialId(entity.getMaterialId());
        } else if (material.getMaterialNorms().size() > entity.getShopMaterialNormsList().size()) {
            // 部分上架
            materialService.setShelvesState(material.getId(), MaterialShelvesState.PART_ON_SHELVE.getKey());
            shopMaterialStoreService.addAllStoreForMaterial(entity.getMaterialId(), entity.getStoreCoverage(), entity.getStoreIds(), entity.getBigTypeId());
        } else if (material.getMaterialNorms().size() == entity.getShopMaterialNormsList().size()) {
            // 全部上架
            materialService.setShelvesState(material.getId(), MaterialShelvesState.ON_SHELVE.getKey());
            shopMaterialStoreService.addAllStoreForMaterial(entity.getMaterialId(), entity.getStoreCoverage(), entity.getStoreIds(), entity.getBigTypeId());
        }
    }

    @Override
    public void queryTransMaterialById(InputObject inputObject, OutputObject outputObject) {
        String materialId = inputObject.getParams().get("id").toString();
        ShopMaterial shopMaterial = queryShopMaterialByMaterialId(materialId);
        if (ObjectUtil.isEmpty(shopMaterial) || StrUtil.isEmpty(shopMaterial.getId())) {
            shopMaterial = new ShopMaterial();
            Material material = materialService.selectById(materialId);
            shopMaterial.setMaterialId(materialId);
            shopMaterial.setMaterialMation(material);
        }
        if (ShopMaterialStoreCoverage.SPECIFIED_STORE.getKey().equals(shopMaterial.getStoreCoverage())) {
            // 门店适用范围--指定门店
            List<ShopMaterialStore> shopMaterialStores = shopMaterialStoreService.selectByMaterialId(materialId);
            if (CollectionUtil.isNotEmpty(shopMaterialStores)) {
                List<String> storeIds = shopMaterialStores.stream().map(ShopMaterialStore::getStoreId).distinct().collect(Collectors.toList());
                shopMaterial.setStoreIds(storeIds);
            }
        }
        outputObject.setBean(shopMaterial);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public ShopMaterial getDataFromDb(String id) {
        ShopMaterial shopMaterial = super.getDataFromDb(id);
        if (ObjectUtil.isEmpty(shopMaterial) || StrUtil.isEmpty(shopMaterial.getId())) {
            return shopMaterial;
        }
        List<ShopMaterialNorms> shopMaterialNormsList = shopMaterialNormsService.selectByMaterialId(shopMaterial.getMaterialId());
        shopMaterial.setShopMaterialNormsList(shopMaterialNormsList);
        return shopMaterial;
    }

    @Override
    public ShopMaterial selectById(String id) {
        ShopMaterial shopMaterial = super.selectById(id);
        if (StrUtil.isEmpty(shopMaterial.getId())) {
            return null;
        }
        materialService.setDataMation(shopMaterial, ShopMaterial::getMaterialId);
        return shopMaterial;
    }

    @Override
    public List<ShopMaterial> getDataFromDb(List<String> idList) {
        List<ShopMaterial> shopMaterialList = super.getDataFromDb(idList);
        if (CollectionUtil.isEmpty(shopMaterialList)) {
            return shopMaterialList;
        }
        List<String> materialIdList = shopMaterialList.stream().map(ShopMaterial::getMaterialId).distinct().collect(Collectors.toList());
        Map<String, List<ShopMaterialNorms>> collectMap = shopMaterialNormsService.selectByMaterialId(materialIdList);
        shopMaterialList.forEach(shopMaterial -> {
            shopMaterial.setShopMaterialNormsList(collectMap.get(shopMaterial.getMaterialId()));
        });
        return shopMaterialList;
    }

    @Override
    public List<ShopMaterial> selectByIds(String... ids) {
        List<ShopMaterial> shopMaterialList = super.selectByIds(ids);
        materialService.setDataMation(shopMaterialList, ShopMaterial::getMaterialId);
        return shopMaterialList;
    }

    @Override
    @IgnoreTenant
    public void queryShopMaterialList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        // 门店 + 优惠券场景：仅同城商品需在 shopMaterial 上打 returnCode
        boolean couponAndStoreQuery = StrUtil.isNotBlank(commonPageInfo.getObjectId())
            && StrUtil.isNotBlank(commonPageInfo.getCustomParamsMapStr("couponId"));
        List<ShopMaterialStore> shopMaterialStoreList = shopMaterialStoreService.queryShopMaterialList(inputObject, outputObject);
        if (CollectionUtil.isEmpty(shopMaterialStoreList)) {
            return;
        }
        List<String> materialIdList = shopMaterialStoreList.stream().map(ShopMaterialStore::getMaterialId).collect(Collectors.toList());
        // 根据商品id查询上架的商品信息
        QueryWrapper<ShopMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ShopMaterial::getMaterialId), materialIdList);
        List<ShopMaterial> shopMaterialList = list(queryWrapper);
        // 根据id批量查询详细的商品信息
        List<String> idList = shopMaterialList.stream().map(ShopMaterial::getId).collect(Collectors.toList());
        List<ShopMaterial> shopMaterials = selectByIds(idList.toArray(new String[]{}));
        // 如果遇到materialId相同的商品，则只保留一个
        Map<String, ShopMaterial> materialMap = shopMaterials.stream().collect(
            Collectors.toMap(ShopMaterial::getMaterialId, Function.identity(), (v1, v2) -> v1));
        String expressKey = String.valueOf(ShopMaterialDeliveryMethod.EXPRESS_DELIVERY.getKey());
        String localKey = String.valueOf(ShopMaterialDeliveryMethod.LOCAL_DELIVERY.getKey());
        shopMaterialStoreList.forEach(shopMaterialStore -> {
            ShopMaterial shopMaterial = materialMap.get(shopMaterialStore.getMaterialId());
            if (ObjectUtil.isNotEmpty(shopMaterial) && ObjectUtil.isNotEmpty(shopMaterial.getMaterialMation())) {
                shopMaterial.getMaterialMation().setMaterialNorms(null);
                shopMaterial.getMaterialMation().setUnitGroupMation(null);
                shopMaterial.getMaterialMation().setMaterialProcedure(null);
                shopMaterial.getMaterialMation().setNormsSpec(null);
            }
            // 门店+优惠券：仅同城（含3无1）的商品打编码，接口头 returnCode 仍为成功
            if (couponAndStoreQuery && ObjectUtil.isNotEmpty(shopMaterial)) {
                List<String> deliveryMethod = shopMaterialStore.getDeliveryMethod();
                if (CollectionUtil.isNotEmpty(deliveryMethod)
                    && deliveryMethod.contains(localKey) && !deliveryMethod.contains(expressKey)) {
                    shopMaterial.setReturnCode(ShopMaterialPcTipCode.LOCAL_DELIVERY_ONLY.getKey());
                }
            }
            shopMaterialStore.setShopMaterial(shopMaterial);
        });

        outputObject.setBeans(shopMaterialStoreList);
    }

    @Override
    @IgnoreTenant
    public void queryShopMaterialListForStore(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        ResultEntity resultEntity = iShopStoreService.queryStoreListFoServer(commonPageInfo);
        // 分页查询门店信息
        List<Map<String, Object>> storeList = resultEntity.getRows();
        if (CollectionUtil.isEmpty(storeList)) {
            return;
        }
        List<String> storeIdList = storeList.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        Map<String, List<ShopMaterialStore>> stringListMap = shopMaterialStoreService.queryShopMaterialListByStoreIds(storeIdList, commonPageInfo);
        storeList.forEach(store -> {
            store.put("shopMaterialList", stringListMap.get(store.get("id").toString()));
        });
        outputObject.setBeans(storeList);
        outputObject.settotal(resultEntity.getTotal());
    }

    @Override
    @IgnoreTenant
    public void queryShopMaterialByNormsIdList(InputObject inputObject, OutputObject outputObject) {
        List<String> normsIdList = Arrays.asList(inputObject.getParams().get("normsIds").toString()
                .split(CommonCharConstants.COMMA_MARK))
            .stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(normsIdList)) {
            return;
        }
        List<ShopMaterialNorms> shopMaterialNormsList = shopMaterialNormsService.queryShopMaterialByNormsIdList(normsIdList);
        outputObject.setBeans(shopMaterialNormsList);
        outputObject.settotal(shopMaterialNormsList.size());
    }

    @Override
    @IgnoreTenant
    public void queryBrandShopMaterialList(InputObject inputObject, OutputObject outputObject) {
        MPJLambdaWrapper<ShopMaterial> wrapper = new MPJLambdaWrapper<ShopMaterial>()
            .innerJoin(Material.class, Material::getId, ShopMaterial::getMaterialId)
            .innerJoin(ShopMaterialStore.class, ShopMaterialStore::getMaterialId, ShopMaterial::getMaterialId)
            .innerJoin(Brand.class, Brand::getId, Material::getBrandId)
            .eq(Brand::getEnabled, EnableEnum.ENABLE_USING.getKey())
            // 商品上架
            .in(Material::getShelvesState, Arrays.asList(MaterialShelvesState.ON_SHELVE.getKey(), MaterialShelvesState.PART_ON_SHELVE.getKey()))
            // 已经添加到门店
            .eq(ShopMaterialStore::getIsLaunchStore, WhetherEnum.ENABLE_USING.getKey())
            // 上架到商城
            .eq(ShopMaterialStore::getIsLaunchShop, WhetherEnum.ENABLE_USING.getKey())
            // 门店是启用状态的
            .eq(ShopMaterialStore::getStoreEnabled, EnableEnum.ENABLE_USING.getKey());
        List<ShopMaterial> shopMaterialList = skyeyeBaseMapper.selectJoinList(ShopMaterial.class, wrapper);
        // 根据id批量查询详细的商品信息
        List<String> idList = shopMaterialList.stream().map(ShopMaterial::getId).collect(Collectors.toList());
        // 批量查询商品所属门店信息(门店随机)
        List<String> materialIds = shopMaterialList.stream().map(ShopMaterial::getMaterialId).distinct().collect(Collectors.toList());
        Map<String, ShopMaterialStore> stringShopMaterialStoreMap = shopMaterialStoreService.queryShopMaterialStoreByMaterialIds(materialIds.toArray(new String[]{}));
        shopMaterialList = selectByIds(idList.toArray(new String[]{}));
        // 过滤掉没有商品信息的商品
        shopMaterialList = shopMaterialList.stream().filter(bean -> ObjectUtil.isNotEmpty(bean.getMaterialMation())).collect(Collectors.toList());
        shopMaterialList.forEach(shopMaterial -> {
            shopMaterial.getMaterialMation().setMaterialNorms(null);
            shopMaterial.getMaterialMation().setUnitGroupMation(null);
            shopMaterial.getMaterialMation().setMaterialProcedure(null);
            shopMaterial.getMaterialMation().setNormsSpec(null);
            // 设置门店信息
            shopMaterial.setShopMaterialStore(stringShopMaterialStoreMap.get(shopMaterial.getMaterialId()));
            if (ObjectUtil.isNotEmpty(shopMaterial.getShopMaterialStore())) {
                shopMaterial.setDefaultStoreId(shopMaterial.getShopMaterialStore().getStoreId());
            }
        });

        // 根据品牌id进行分组，并且每个品牌下只取8条数据
        Map<String, List<ShopMaterial>> collectMap = shopMaterialList.stream().collect(Collectors.groupingBy(bean -> bean.getMaterialMation().getBrandId(), Collectors.collectingAndThen(
            Collectors.toList(), // 分组的 downstream
            list -> {
                if (list.size() > 8) {
                    return list.subList(0, 8); // 只取前8个元素
                }
                return list;
            }
        )));
        Map<String, Object> brandMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(collectMap)) {
            List<String> brandIdList = shopMaterialList.stream()
                .filter(bean -> StrUtil.isNotEmpty(bean.getMaterialMation().getBrandId()))
                .map(bean -> bean.getMaterialMation().getBrandId()).distinct().collect(Collectors.toList());
            brandMap = brandService.selectByIds(brandIdList.toArray(new String[]{})).stream()
                .collect(Collectors.toMap(Brand::getId, bean -> bean.getName()));
        }
        outputObject.setBean(collectMap);
        outputObject.setCustomBean("brandMap", brandMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @IgnoreTenant
    public ShopMaterial queryShopMaterialByMaterialId(String materialId) {
        QueryWrapper<ShopMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterial::getMaterialId), materialId);
        ShopMaterial shopMaterial = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(shopMaterial)) {
            return null;
        }
        return selectById(shopMaterial.getId());
    }

    @Override
    @IgnoreTenant
    public Map<String, ShopMaterial> queryShopMaterialByMaterialId(List<String> materialIds) {
        materialIds = materialIds.stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(materialIds)) {
            return MapUtil.empty();
        }
        QueryWrapper<ShopMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ShopMaterial::getMaterialId), materialIds);
        queryWrapper.select(CommonConstants.ID);
        List<ShopMaterial> shopMaterialList = list(queryWrapper);
        if (CollectionUtil.isEmpty(shopMaterialList)) {
            return MapUtil.empty();
        }
        List<String> ids = shopMaterialList.stream().map(ShopMaterial::getId).collect(Collectors.toList());
        List<ShopMaterial> shopMaterials = selectByIds(ids.toArray(new String[]{}));
        return shopMaterials.stream().collect(Collectors.toMap(ShopMaterial::getMaterialId, Function.identity(), (v1, v2) -> v1));
    }

    @Override
    public void queryAllShopMaterialListForChoose(InputObject inputObject, OutputObject outputObject) {
        List<ShopMaterial> shopMaterialList = list();
        // 根据id批量查询详细的商品信息
        List<String> idList = shopMaterialList.stream().map(ShopMaterial::getId).collect(Collectors.toList());
        List<ShopMaterial> shopMaterials = selectByIds(idList.toArray(new String[]{}));
        List<Map<String, Object>> result = shopMaterials.stream().map(bean -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", bean.getMaterialId());
            item.put("name", ObjectUtil.isEmpty(bean.getMaterialMation()) ? "" : StrUtil.isEmpty(bean.getMaterialMation().getName()) ? "" : bean.getMaterialMation().getName());
            return item;
        }).collect(Collectors.toList());
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void queryShopMaterialByMaterialIdList(InputObject inputObject, OutputObject outputObject) {
        List<String> materialIdList = Arrays.asList(inputObject.getParams().get("materialIds").toString()
                .split(CommonCharConstants.COMMA_MARK))
            .stream().filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(materialIdList)) {
            return;
        }
        QueryWrapper<ShopMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ShopMaterial::getMaterialId), materialIdList);
        List<ShopMaterial> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public List<ShopMaterial> queryShopMaterialListByStoreCoverage(Integer storeCoverage, String storeId) {
        QueryWrapper<ShopMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterial::getStoreCoverage), storeCoverage);
        if (ShopMaterialStoreCoverage.SPECIFIED_STORE.getKey().equals(storeCoverage)) {
            // 指定门店
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMaterial::getStoreCoverage), storeId);
        }
        List<ShopMaterial> list = list(queryWrapper);
        return list;
    }

}
