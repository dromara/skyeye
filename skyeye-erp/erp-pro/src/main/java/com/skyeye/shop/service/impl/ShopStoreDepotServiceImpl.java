/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shop.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.depot.entity.Depot;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.entity.MaterialNormsStock;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialNormsStockService;
import com.skyeye.rest.shop.service.IShopStoreService;
import com.skyeye.shop.dao.ShopStoreDepotDao;
import com.skyeye.shop.entity.ShopStoreDepot;
import com.skyeye.shop.service.ShopStockService;
import com.skyeye.shop.service.ShopStoreDepotService;
import com.skyeye.shopmaterial.entity.ShopMaterial;
import com.skyeye.shopmaterial.entity.ShopMaterialNorms;
import com.skyeye.shopmaterial.entity.ShopMaterialStore;
import com.skyeye.shopmaterial.enums.ShopMaterialStockMode;
import com.skyeye.shopmaterial.enums.ShopMaterialStoreSourceType;
import com.skyeye.shopmaterial.service.ShopMaterialNormsService;
import com.skyeye.shopmaterial.service.ShopMaterialService;
import com.skyeye.shopmaterial.service.ShopMaterialStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ShopStoreDepotServiceImpl
 * @Description: 个人门店仓库关联与库存服务实现
 */
@Service
@SkyeyeService(name = "个人门店仓库", groupName = "门店", manageShow = false)
public class ShopStoreDepotServiceImpl extends SkyeyeBusinessServiceImpl<ShopStoreDepotDao, ShopStoreDepot>
    implements ShopStoreDepotService {

    private static final int DEFAULT_LOW_STOCK = 5;

    @Autowired
    private IShopStoreService iShopStoreService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Autowired
    private ShopStockService shopStockService;

    @Autowired
    private MaterialNormsStockService materialNormsStockService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private ShopMaterialStoreService shopMaterialStoreService;

    @Autowired
    private ShopMaterialService shopMaterialService;

    @Autowired
    private ShopMaterialNormsService shopMaterialNormsService;

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
    public List<ShopStoreDepot> listEnabledByStoreId(String storeId) {
        QueryWrapper<ShopStoreDepot> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(ShopStoreDepot::getStoreId), storeId);
        wrapper.eq(MybatisPlusUtil.toColumns(ShopStoreDepot::getEnabled), EnableEnum.ENABLE_USING.getKey());
        wrapper.orderByAsc(MybatisPlusUtil.toColumns(ShopStoreDepot::getPriority));
        return list(wrapper);
    }

    private List<ShopStoreDepot> listByStoreId(String storeId, String keyword) {
        QueryWrapper<ShopStoreDepot> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(ShopStoreDepot::getStoreId), storeId);
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(MybatisPlusUtil.toColumns(ShopStoreDepot::getDepotCode), keyword)
                .or().like(MybatisPlusUtil.toColumns(ShopStoreDepot::getAliasName), keyword));
        }
        wrapper.orderByAsc(MybatisPlusUtil.toColumns(ShopStoreDepot::getPriority));
        List<ShopStoreDepot> list = list(wrapper);
        if (CollectionUtil.isEmpty(list)) {
            return list;
        }
        List<String> depotIds = list.stream().map(ShopStoreDepot::getDepotId).filter(StrUtil::isNotEmpty)
            .distinct().collect(Collectors.toList());
        Map<String, Depot> depotMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(depotIds)) {
            List<Depot> depots = erpDepotService.selectByIds(depotIds.toArray(new String[0]));
            if (CollectionUtil.isNotEmpty(depots)) {
                depotMap = depots.stream().collect(Collectors.toMap(Depot::getId, d -> d, (a, b) -> a));
            }
        }
        Map<String, Depot> finalDepotMap = depotMap;
        list.forEach(item -> item.setDepotMation(finalDepotMap.get(item.getDepotId())));
        return list;
    }

    @Override
    @IgnoreTenant
    public void queryPersonalStoreDepotList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String storeId = pageInfo.getObjectId();
        assertPersonalStoreOwner(storeId);
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<ShopStoreDepot> list = listByStoreId(storeId, pageInfo.getKeyword());
        if (StrUtil.isNotBlank(pageInfo.getKeyword()) && CollectionUtil.isNotEmpty(list)) {
            String kw = pageInfo.getKeyword();
            list = list.stream().filter(item -> {
                Depot depot = item.getDepotMation();
                String name = depot == null ? StrUtil.EMPTY : StrUtil.blankToDefault(depot.getName(), StrUtil.EMPTY);
                return StrUtil.containsIgnoreCase(StrUtil.blankToDefault(item.getDepotCode(), ""), kw)
                    || StrUtil.containsIgnoreCase(StrUtil.blankToDefault(item.getAliasName(), ""), kw)
                    || StrUtil.containsIgnoreCase(name, kw);
            }).collect(Collectors.toList());
        }
        outputObject.setBeans(list);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void createPersonalStoreDepot(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = MapUtil.getStr(params, "storeId");
        assertPersonalStoreOwner(storeId);
        String depotCode = MapUtil.getStr(params, "depotCode");
        String name = MapUtil.getStr(params, "name");
        String aliasName = MapUtil.getStr(params, "aliasName");
        String provinceId = MapUtil.getStr(params, "provinceId");
        String cityId = MapUtil.getStr(params, "cityId");
        String areaId = MapUtil.getStr(params, "areaId");
        String townshipId = MapUtil.getStr(params, "townshipId");
        String absoluteAddress = StrUtil.trim(MapUtil.getStr(params, "absoluteAddress"));
        String contactName = MapUtil.getStr(params, "contactName");
        String contactPhone = MapUtil.getStr(params, "contactPhone");
        if (StrUtil.isBlank(depotCode) || !depotCode.matches("^[A-Za-z0-9_]{4,30}$")) {
            throw new CustomException("仓编码仅支持字母数字下划线，长度 4-30");
        }
        if (StrUtil.isBlank(name)) {
            throw new CustomException("请填写仓库名称");
        }
        if (StrUtil.isBlank(aliasName)) {
            aliasName = name;
        }
        if (StrUtil.isBlank(provinceId) || StrUtil.isBlank(absoluteAddress)) {
            throw new CustomException("请完善仓库地址");
        }
        if (StrUtil.isBlank(contactName)) {
            throw new CustomException("请填写联系人");
        }
        QueryWrapper<ShopStoreDepot> codeWrapper = new QueryWrapper<>();
        codeWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreDepot::getStoreId), storeId);
        codeWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreDepot::getDepotCode), depotCode);
        if (count(codeWrapper) > 0) {
            throw new CustomException("仓编码已存在");
        }

        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Depot depot = new Depot();
        depot.setName(name);
        // erp_depot.address 较短，仅存详细地址；省市区存关联表（与门店一致）
        depot.setAddress(absoluteAddress);
        depot.setStoreId(storeId);
        depot.setEnabled(EnableEnum.ENABLE_USING.getKey());
        depot.setIsDefault(IsDefaultEnum.NOT_DEFAULT.getKey());
        depot.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getKey());
        depot.setPrincipal(new ArrayList<>());
        String depotId = erpDepotService.createEntity(depot, userId);

        int nextPriority = 10;
        QueryWrapper<ShopStoreDepot> maxWrapper = new QueryWrapper<>();
        maxWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreDepot::getStoreId), storeId);
        maxWrapper.orderByDesc(MybatisPlusUtil.toColumns(ShopStoreDepot::getPriority));
        maxWrapper.last("LIMIT 1");
        ShopStoreDepot last = getOne(maxWrapper, false);
        if (last != null && last.getPriority() != null) {
            nextPriority = last.getPriority() + 10;
        }
        boolean firstDepot = count(new QueryWrapper<ShopStoreDepot>()
            .eq(MybatisPlusUtil.toColumns(ShopStoreDepot::getStoreId), storeId)) == 0;

        ShopStoreDepot link = new ShopStoreDepot();
        link.setStoreId(storeId);
        link.setDepotId(depotId);
        link.setDepotCode(depotCode);
        link.setAliasName(aliasName);
        link.setContactName(contactName);
        link.setContactPhone(contactPhone);
        link.setProvinceId(provinceId);
        link.setCityId(cityId);
        link.setAreaId(areaId);
        link.setTownshipId(townshipId);
        link.setAbsoluteAddress(absoluteAddress);
        link.setPriority(nextPriority);
        link.setEnabled(EnableEnum.ENABLE_USING.getKey());
        link.setIsDefault(firstDepot ? IsDefaultEnum.IS_DEFAULT.getKey() : IsDefaultEnum.NOT_DEFAULT.getKey());
        createEntity(link, userId);
        outputObject.setBean(link);
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void updatePersonalStoreDepot(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = MapUtil.getStr(params, "id");
        String storeId = MapUtil.getStr(params, "storeId");
        assertPersonalStoreOwner(storeId);
        ShopStoreDepot link = selectById(id);
        if (link == null || !storeId.equals(link.getStoreId())) {
            throw new CustomException("仓库不存在");
        }
        String name = MapUtil.getStr(params, "name");
        String aliasName = MapUtil.getStr(params, "aliasName");
        String provinceId = MapUtil.getStr(params, "provinceId");
        String cityId = MapUtil.getStr(params, "cityId");
        String areaId = MapUtil.getStr(params, "areaId");
        String townshipId = MapUtil.getStr(params, "townshipId");
        String absoluteAddress = StrUtil.trim(MapUtil.getStr(params, "absoluteAddress"));
        String contactName = MapUtil.getStr(params, "contactName");
        String contactPhone = MapUtil.getStr(params, "contactPhone");
        Integer enabled = Convert.toInt(params.get("enabled"), link.getEnabled());
        if (StrUtil.isNotBlank(name) || StrUtil.isNotBlank(absoluteAddress)) {
            Depot depot = erpDepotService.selectById(link.getDepotId());
            if (depot == null || !storeId.equals(depot.getStoreId())) {
                throw new CustomException("仓库不存在");
            }
            if (StrUtil.isNotBlank(name)) {
                depot.setName(name);
            }
            if (StrUtil.isNotBlank(absoluteAddress)) {
                depot.setAddress(absoluteAddress);
            }
            depot.setEnabled(enabled);
            erpDepotService.updateEntity(depot, InputObject.getLogParamsStatic().get("id").toString());
        }
        UpdateWrapper<ShopStoreDepot> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        if (StrUtil.isNotBlank(aliasName)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreDepot::getAliasName), aliasName);
        }
        if (StrUtil.isNotBlank(contactName)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreDepot::getContactName), contactName);
        }
        if (params.containsKey("contactPhone")) {
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreDepot::getContactPhone), contactPhone);
        }
        if (StrUtil.isNotBlank(provinceId)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreDepot::getProvinceId), provinceId);
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreDepot::getCityId), cityId);
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreDepot::getAreaId), areaId);
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreDepot::getTownshipId), townshipId);
        }
        if (StrUtil.isNotBlank(absoluteAddress)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreDepot::getAbsoluteAddress), absoluteAddress);
        }
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreDepot::getEnabled), enabled);
        update(updateWrapper);
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void setDefaultPersonalStoreDepot(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = MapUtil.getStr(params, "storeId");
        String id = MapUtil.getStr(params, "id");
        assertPersonalStoreOwner(storeId);
        ShopStoreDepot link = selectById(id);
        if (link == null || !storeId.equals(link.getStoreId())) {
            throw new CustomException("仓库不存在");
        }
        UpdateWrapper<ShopStoreDepot> clearWrapper = new UpdateWrapper<>();
        clearWrapper.eq(MybatisPlusUtil.toColumns(ShopStoreDepot::getStoreId), storeId);
        clearWrapper.set(MybatisPlusUtil.toColumns(ShopStoreDepot::getIsDefault), IsDefaultEnum.NOT_DEFAULT.getKey());
        update(clearWrapper);
        UpdateWrapper<ShopStoreDepot> setWrapper = new UpdateWrapper<>();
        setWrapper.eq(CommonConstants.ID, id);
        setWrapper.set(MybatisPlusUtil.toColumns(ShopStoreDepot::getIsDefault), IsDefaultEnum.IS_DEFAULT.getKey());
        update(setWrapper);
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deletePersonalStoreDepot(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = MapUtil.getStr(params, "storeId");
        String id = MapUtil.getStr(params, "id");
        assertPersonalStoreOwner(storeId);
        ShopStoreDepot link = selectById(id);
        if (link == null || !storeId.equals(link.getStoreId())) {
            throw new CustomException("仓库不存在");
        }
        if (IsDefaultEnum.IS_DEFAULT.getKey().equals(link.getIsDefault())) {
            throw new CustomException("默认仓不能删除，请先设置其他仓库为默认仓");
        }
        long remain = count(new QueryWrapper<ShopStoreDepot>()
            .eq(MybatisPlusUtil.toColumns(ShopStoreDepot::getStoreId), storeId)
            .ne(CommonConstants.ID, id));
        if (remain <= 0) {
            throw new CustomException("至少保留一个商家仓");
        }
        QueryWrapper<MaterialNormsStock> stockWrapper = new QueryWrapper<>();
        stockWrapper.eq(MybatisPlusUtil.toColumns(MaterialNormsStock::getDepotId), link.getDepotId());
        List<MaterialNormsStock> stockList = materialNormsStockService.list(stockWrapper);
        if (CollectionUtil.isNotEmpty(stockList)) {
            boolean hasStock = stockList.stream().anyMatch(s -> Convert.toInt(s.getStock(), 0) > 0);
            if (hasStock) {
                throw new CustomException("该仓库仍有库存，请先调整库存后再删除");
            }
        }
        String depotId = link.getDepotId();
        deleteById(id);
        Depot depot = erpDepotService.selectById(depotId);
        if (depot != null && storeId.equals(depot.getStoreId())) {
            erpDepotService.deleteById(depotId);
        }
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void savePersonalStoreDepotPriority(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = MapUtil.getStr(params, "storeId");
        assertPersonalStoreOwner(storeId);
        List<String> idOrder = JSONUtil.toList(MapUtil.getStr(params, "idOrder"), String.class);
        if (CollectionUtil.isEmpty(idOrder)) {
            throw new CustomException("请传入仓库排序");
        }
        int priority = 10;
        for (String id : idOrder) {
            ShopStoreDepot link = selectById(id);
            if (link == null || !storeId.equals(link.getStoreId())) {
                throw new CustomException("仓库不存在");
            }
            UpdateWrapper<ShopStoreDepot> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(ShopStoreDepot::getPriority), priority);
            update(updateWrapper);
            priority += 10;
        }
    }

    private Integer resolveStockMode(ShopMaterialStore store) {
        return store.getStockMode() == null ? ShopMaterialStockMode.NORMAL.getKey() : store.getStockMode();
    }

    private int sumDepotStock(List<String> depotIds, String normsId,
                              Map<String, Map<String, String>> depotNormsStockMap) {
        if (CollectionUtil.isEmpty(depotIds) || StrUtil.isBlank(normsId) || MapUtil.isEmpty(depotNormsStockMap)) {
            return 0;
        }
        int total = 0;
        for (String depotId : depotIds) {
            Map<String, String> stockMap = depotNormsStockMap.get(depotId);
            if (MapUtil.isEmpty(stockMap)) {
                continue;
            }
            total += Convert.toInt(stockMap.get(normsId), 0);
        }
        return total;
    }

    private int calcSaleableStock(String storeId, ShopMaterialStore relation, List<String> normsIds,
                                  List<ShopStoreDepot> enabledDepots) {
        if (CollectionUtil.isEmpty(normsIds)) {
            return 0;
        }
        // 平台货分销代销：可售库存读供货方门店
        String stockStoreId = storeId;
        ShopMaterialStore stockRelation = relation;
        if (isPlatformDropship(relation)) {
            stockStoreId = relation.getSourceStoreId();
            stockRelation = findStoreMaterialRelation(stockStoreId, relation.getMaterialId());
            if (stockRelation == null) {
                // 供货方无挂靠记录时按普通门店库存汇总
                Map<String, String> shopStock = shopStockService.queryNormsShopStock(stockStoreId, normsIds);
                int total = 0;
                for (String normsId : normsIds) {
                    total += Convert.toInt(shopStock.get(normsId), 0);
                }
                return total;
            }
            enabledDepots = listEnabledByStoreId(stockStoreId);
        }
        Integer mode = resolveStockMode(stockRelation);
        if (ShopMaterialStockMode.DEPOT_LINK.getKey().equals(mode)) {
            List<String> depotIds = enabledDepots.stream().map(ShopStoreDepot::getDepotId)
                .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
            Map<String, Map<String, String>> depotNormsStockMap =
                materialNormsStockService.queryMaterialNormsStockByDepotIds(normsIds, depotIds);
            int total = 0;
            for (String normsId : normsIds) {
                total += sumDepotStock(depotIds, normsId, depotNormsStockMap);
            }
            return total;
        }
        Map<String, String> shopStock = shopStockService.queryNormsShopStock(stockStoreId, normsIds);
        int total = 0;
        for (String normsId : normsIds) {
            total += Convert.toInt(shopStock.get(normsId), 0);
        }
        return total;
    }

    private boolean isPlatformDropship(ShopMaterialStore relation) {
        return relation != null
            && ShopMaterialStoreSourceType.PLATFORM.getKey().equals(relation.getSourceType())
            && StrUtil.isNotBlank(relation.getSourceStoreId());
    }

    private void assertNotPlatformDropship(ShopMaterialStore relation) {
        if (isPlatformDropship(relation)) {
            throw new CustomException("平台货源由供货方维护库存，个人门店不可调整");
        }
    }

    private ShopMaterialStore findStoreMaterialRelation(String storeId, String materialId) {
        if (StrUtil.isBlank(storeId) || StrUtil.isBlank(materialId)) {
            return null;
        }
        QueryWrapper<ShopMaterialStore> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getStoreId), storeId);
        wrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getMaterialId), materialId);
        wrapper.eq(MybatisPlusUtil.toColumns(ShopMaterialStore::getIsLaunchStore), WhetherEnum.ENABLE_USING.getKey());
        return shopMaterialStoreService.getOne(wrapper, false);
    }

    private List<String> normsIdsOfMaterial(String materialId) {
        List<ShopMaterialNorms> normsList = shopMaterialNormsService.selectByMaterialId(materialId);
        if (CollectionUtil.isEmpty(normsList)) {
            List<MaterialNorms> materialNorms = materialNormsService.queryNormsUnitListByMaterialId(materialId);
            if (CollectionUtil.isEmpty(materialNorms)) {
                return new ArrayList<>();
            }
            return materialNorms.stream().map(MaterialNorms::getId).filter(StrUtil::isNotEmpty).collect(Collectors.toList());
        }
        return normsList.stream().map(ShopMaterialNorms::getNormsId).filter(StrUtil::isNotEmpty).distinct()
            .collect(Collectors.toList());
    }

    private Map<String, Object> buildInventoryRow(ShopMaterial shopMaterial, ShopMaterialStore relation,
                                                  int saleable, String diagnoseType) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", relation.getId());
        row.put("storeId", relation.getStoreId());
        row.put("materialId", relation.getMaterialId());
        row.put("stockMode", resolveStockMode(relation));
        row.put("sourceType", relation.getSourceType());
        row.put("sourceStoreId", relation.getSourceStoreId());
        // 历史平台货未绑定供货方：前端提示需重新引入
        boolean needReChoose = ShopMaterialStoreSourceType.PLATFORM.getKey().equals(relation.getSourceType())
            && StrUtil.isBlank(relation.getSourceStoreId());
        row.put("needReChoosePlatformSource", needReChoose);
        row.put("isLaunchShop", relation.getIsLaunchShop());
        row.put("saleableStock", saleable);
        row.put("shopMaterial", shopMaterial);
        row.put("shopMaterialStore", relation);
        if (StrUtil.isNotBlank(diagnoseType)) {
            row.put("diagnoseType", diagnoseType);
        }
        return row;
    }

    @Override
    @IgnoreTenant
    public void queryPersonalStoreInventoryList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String storeId = pageInfo.getObjectId();
        assertPersonalStoreOwner(storeId);
        Map<String, Object> params = inputObject.getParams();
        Integer stockModeFilter = Convert.toInt(params.get("stockMode"), null);
        Integer launchFilter = Convert.toInt(params.get("isLaunchShop"), null);

        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        List<ShopMaterialStore> relations = shopMaterialStoreService.selectByStoreId(storeId,
            WhetherEnum.ENABLE_USING.getKey(), launchFilter, pageInfo.getKeyword());
        if (CollectionUtil.isEmpty(relations)) {
            outputObject.setBeans(new ArrayList<>());
            outputObject.settotal(0);
            return;
        }
        if (stockModeFilter != null) {
            relations = relations.stream()
                .filter(r -> Objects.equals(resolveStockMode(r), stockModeFilter))
                .collect(Collectors.toList());
        }
        List<ShopStoreDepot> enabledDepots = listEnabledByStoreId(storeId);
        List<String> materialIds = relations.stream().map(ShopMaterialStore::getMaterialId).distinct()
            .collect(Collectors.toList());
        Map<String, ShopMaterial> materialMap = shopMaterialService.queryShopMaterialByMaterialId(materialIds);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (ShopMaterialStore relation : relations) {
            ShopMaterial shopMaterial = materialMap.get(relation.getMaterialId());
            if (shopMaterial == null) {
                continue;
            }
            List<String> normsIds = normsIdsOfMaterial(relation.getMaterialId());
            int saleable = calcSaleableStock(storeId, relation, normsIds, enabledDepots);
            rows.add(buildInventoryRow(shopMaterial, relation, saleable, null));
        }
        outputObject.setBeans(rows);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void adjustPersonalStoreInventory(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = MapUtil.getStr(params, "storeId");
        assertPersonalStoreOwner(storeId);
        String relationId = MapUtil.getStr(params, "id");
        String normsId = MapUtil.getStr(params, "normsId");
        String depotId = MapUtil.getStr(params, "depotId");
        Integer adjustType = Convert.toInt(params.get("adjustType"));
        String count = MapUtil.getStr(params, "count");
        if (StrUtil.isBlank(relationId) || StrUtil.isBlank(normsId) || StrUtil.isBlank(count)) {
            throw new CustomException("参数不完整");
        }
        if (CalculationUtil.compareTo(count, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) <= 0) {
            throw new CustomException("调整数量必须大于0");
        }
        ShopMaterialStore relation = shopMaterialStoreService.selectById(relationId);
        if (relation == null || !storeId.equals(relation.getStoreId())) {
            throw new CustomException("商品不存在");
        }
        assertNotPlatformDropship(relation);
        Integer mode = resolveStockMode(relation);
        int putOutType = DepotPutOutType.PUT.getKey().equals(adjustType)
            ? DepotPutOutType.PUT.getKey() : DepotPutOutType.OUT.getKey();
        if (ShopMaterialStockMode.DEPOT_LINK.getKey().equals(mode)) {
            if (StrUtil.isBlank(depotId)) {
                throw new CustomException("请选择仓库");
            }
            List<ShopStoreDepot> enabled = listEnabledByStoreId(storeId);
            boolean owned = enabled.stream().anyMatch(d -> depotId.equals(d.getDepotId()));
            if (!owned) {
                throw new CustomException("仓库不属于当前门店或未启用");
            }
            Map<String, String> stockMap = materialNormsStockService.queryMaterialNormsStock(
                java.util.Collections.singletonList(normsId), depotId);
            int current = Convert.toInt(stockMap.get(normsId), 0);
            int delta = Convert.toInt(count, 0);
            int next = DepotPutOutType.PUT.getKey().equals(putOutType) ? current + delta : current - delta;
            if (next < 0) {
                throw new CustomException("仓库库存不足");
            }
            materialNormsStockService.saveMaterialNormsStock(relation.getMaterialId(), depotId, normsId,
                String.valueOf(next), MaterialNormsStockType.ORDER_STOCK.getKey());
        } else {
            shopStockService.updateShopStock(storeId, relation.getMaterialId(), normsId, count, putOutType);
        }
    }

    /**
     * 发货扣减门店库存。
     * <ul>
     *   <li>普通模式(stockMode=1)：校验 shop_stock 后出库</li>
     *   <li>关联仓模式(stockMode=2)：按商家仓 priority 从高到低（数值越小越优先）逐仓扣减</li>
     *   <li>平台货分销代销：扣供货方门店库存（sourceStoreId）</li>
     * </ul>
     */
    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deductShopStockOnShip(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = MapUtil.getStr(params, "storeId");
        String materialStoreId = MapUtil.getStr(params, "materialStoreId");
        String materialId = MapUtil.getStr(params, "materialId");
        String normsId = MapUtil.getStr(params, "normsId");
        String count = MapUtil.getStr(params, "count");
        if (CalculationUtil.compareTo(count, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) <= 0) {
            throw new CustomException("发货数量必须大于0");
        }
        ShopMaterialStore relation = null;
        if (StrUtil.isNotBlank(materialStoreId)) {
            relation = shopMaterialStoreService.selectById(materialStoreId);
        }
        // 扣库存门店：平台货走供货方，否则走订单门店
        String deductStoreId = storeId;
        ShopMaterialStore stockRelation = relation;
        if (isPlatformDropship(relation)) {
            deductStoreId = relation.getSourceStoreId();
            stockRelation = findStoreMaterialRelation(deductStoreId, relation.getMaterialId());
        } else if (relation != null && !storeId.equals(relation.getStoreId())) {
            throw new CustomException("门店商品关系与门店不匹配");
        }
        Integer mode = stockRelation == null ? ShopMaterialStockMode.NORMAL.getKey() : resolveStockMode(stockRelation);
        String useMaterialId = relation != null && StrUtil.isNotBlank(relation.getMaterialId())
            ? relation.getMaterialId() : materialId;
        if (ShopMaterialStockMode.DEPOT_LINK.getKey().equals(mode)) {
            deductDepotLinkStockOnShip(deductStoreId, useMaterialId, normsId, Convert.toInt(count, 0));
        } else {
            Map<String, String> stockMap = shopStockService.queryNormsShopStock(deductStoreId,
                java.util.Collections.singletonList(normsId));
            int current = Convert.toInt(stockMap.get(normsId), 0);
            int need = Convert.toInt(count, 0);
            if (current < need) {
                throw new CustomException("门店库存不足，当前可售 " + current + "，需要 " + need);
            }
            shopStockService.updateShopStock(deductStoreId, useMaterialId, normsId, count, DepotPutOutType.OUT.getKey());
        }
    }

    @Override
    @IgnoreTenant
    public void checkShopStockForSale(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String materialStoreId = MapUtil.getStr(params, "materialStoreId");
        String normsId = MapUtil.getStr(params, "normsId");
        int need = Convert.toInt(params.get("count"), 0);
        if (StrUtil.isBlank(materialStoreId) || StrUtil.isBlank(normsId) || need <= 0) {
            throw new CustomException("库存校验参数不完整");
        }
        ShopMaterialStore relation = shopMaterialStoreService.selectById(materialStoreId);
        if (relation == null) {
            throw new CustomException("门店商品不存在");
        }
        List<String> normsIds = Collections.singletonList(normsId);
        List<ShopStoreDepot> enabled = listEnabledByStoreId(relation.getStoreId());
        int saleable = calcSaleableStock(relation.getStoreId(), relation, normsIds, enabled);
        if (saleable < need) {
            throw new CustomException("库存不足，当前可售 " + saleable + "，需要 " + need);
        }
        Map<String, Object> bean = new HashMap<>();
        bean.put("saleableStock", saleable);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 关联仓模式发货扣库存：一次查出各仓库存，内存按 priority 计算扣减，再批量写回。
     *
     * @param storeId    门店 id
     * @param materialId 商品 id
     * @param normsId    规格 id
     * @param need       本次需要扣减的数量
     */
    private void deductDepotLinkStockOnShip(String storeId, String materialId, String normsId, int need) {
        List<ShopStoreDepot> enabled = listEnabledByStoreId(storeId);
        if (CollectionUtil.isEmpty(enabled)) {
            throw new CustomException("关联仓模式下请先启用至少一个商家仓");
        }
        List<String> depotIds = enabled.stream().map(ShopStoreDepot::getDepotId)
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(depotIds)) {
            throw new CustomException("关联仓模式下请先启用至少一个商家仓");
        }
        // 一次查出全部启用仓的该规格库存
        Map<String, Map<String, String>> depotNormsStockMap = materialNormsStockService
            .queryMaterialNormsStockByDepotIds(java.util.Collections.singletonList(normsId), depotIds);
        int remain = need;
        // depotId -> 扣减后的目标库存
        Map<String, String> deductResult = new HashMap<>();
        for (ShopStoreDepot depot : enabled) {
            if (remain <= 0) {
                break;
            }
            String depotId = depot.getDepotId();
            if (StrUtil.isBlank(depotId)) {
                continue;
            }
            Map<String, String> stockMap = depotNormsStockMap.get(depotId);
            int current = Convert.toInt(MapUtil.isEmpty(stockMap) ? null : stockMap.get(normsId), 0);
            if (current <= 0) {
                continue;
            }
            int take = Math.min(current, remain);
            deductResult.put(depotId, String.valueOf(current - take));
            remain -= take;
        }
        if (remain > 0) {
            throw new CustomException("仓库库存不足，仍缺 " + remain);
        }
        // 批量写回（一次查已有行 + 一条 CASE 更新 / saveBatch 新增）
        materialNormsStockService.batchSaveMaterialNormsStock(materialId, normsId, deductResult,
            MaterialNormsStockType.ORDER_STOCK.getKey());
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void switchPersonalStoreStockMode(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = MapUtil.getStr(params, "storeId");
        assertPersonalStoreOwner(storeId);
        String relationId = MapUtil.getStr(params, "id");
        Integer stockMode = Convert.toInt(params.get("stockMode"));
        if (!ShopMaterialStockMode.NORMAL.getKey().equals(stockMode)
            && !ShopMaterialStockMode.DEPOT_LINK.getKey().equals(stockMode)) {
            throw new CustomException("库存模式不正确");
        }
        ShopMaterialStore relation = shopMaterialStoreService.selectById(relationId);
        if (relation == null || !storeId.equals(relation.getStoreId())) {
            throw new CustomException("商品不存在");
        }
        assertNotPlatformDropship(relation);
        if (ShopMaterialStockMode.DEPOT_LINK.getKey().equals(stockMode)) {
            List<ShopStoreDepot> enabled = listEnabledByStoreId(storeId);
            if (CollectionUtil.isEmpty(enabled)) {
                throw new CustomException("请先新建并启用至少一个商家仓");
            }
        }
        UpdateWrapper<ShopMaterialStore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, relationId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ShopMaterialStore::getStockMode), stockMode);
        shopMaterialStoreService.update(updateWrapper);
    }

    private String diagnoseTypeOf(int saleable, int safety) {
        if (saleable <= 0) {
            return "oos";
        }
        if (saleable < safety) {
            return "low";
        }
        return "ok";
    }

    private int safetyOfMaterial(String materialId) {
        List<MaterialNorms> norms = materialNormsService.queryNormsUnitListByMaterialId(materialId);
        if (CollectionUtil.isEmpty(norms)) {
            return DEFAULT_LOW_STOCK;
        }
        int minSafety = Integer.MAX_VALUE;
        for (MaterialNorms normsItem : norms) {
            int safety = Convert.toInt(normsItem.getSafetyTock(), DEFAULT_LOW_STOCK);
            if (safety <= 0) {
                safety = DEFAULT_LOW_STOCK;
            }
            minSafety = Math.min(minSafety, safety);
        }
        return minSafety == Integer.MAX_VALUE ? DEFAULT_LOW_STOCK : minSafety;
    }

    @Override
    @IgnoreTenant
    public void queryPersonalStoreInventoryDiagnosis(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String storeId = pageInfo.getObjectId();
        assertPersonalStoreOwner(storeId);
        Map<String, Object> params = inputObject.getParams();
        String diagnoseFilter = MapUtil.getStr(params, "diagnoseType");

        List<ShopMaterialStore> relations = shopMaterialStoreService.selectByStoreId(storeId,
            WhetherEnum.ENABLE_USING.getKey(), WhetherEnum.ENABLE_USING.getKey(), pageInfo.getKeyword());
        List<ShopStoreDepot> enabledDepots = listEnabledByStoreId(storeId);
        List<String> materialIds = relations.stream().map(ShopMaterialStore::getMaterialId).distinct()
            .collect(Collectors.toList());
        Map<String, ShopMaterial> materialMap = CollectionUtil.isEmpty(materialIds)
            ? new HashMap<>() : shopMaterialService.queryShopMaterialByMaterialId(materialIds);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (ShopMaterialStore relation : relations) {
            ShopMaterial shopMaterial = materialMap.get(relation.getMaterialId());
            if (shopMaterial == null) {
                continue;
            }
            List<String> normsIds = normsIdsOfMaterial(relation.getMaterialId());
            int saleable = calcSaleableStock(storeId, relation, normsIds, enabledDepots);
            int safety = safetyOfMaterial(relation.getMaterialId());
            String type = diagnoseTypeOf(saleable, safety);
            if ("ok".equals(type)) {
                continue;
            }
            if (StrUtil.isNotBlank(diagnoseFilter) && !"all".equals(diagnoseFilter) && !diagnoseFilter.equals(type)) {
                continue;
            }
            rows.add(buildInventoryRow(shopMaterial, relation, saleable, type));
        }
        int page = pageInfo.getPage() <= 0 ? 1 : pageInfo.getPage();
        int limit = pageInfo.getLimit() <= 0 ? 10 : pageInfo.getLimit();
        int from = Math.min((page - 1) * limit, rows.size());
        int to = Math.min(from + limit, rows.size());
        outputObject.setBeans(rows.subList(from, to));
        outputObject.settotal(rows.size());
    }

    @Override
    @IgnoreTenant
    public void queryPersonalStoreInventoryHome(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = MapUtil.getStr(params, "storeId");
        assertPersonalStoreOwner(storeId);
        List<ShopMaterialStore> launched = shopMaterialStoreService.selectByStoreId(storeId,
            WhetherEnum.ENABLE_USING.getKey(), WhetherEnum.ENABLE_USING.getKey(), null);
        List<ShopStoreDepot> enabledDepots = listEnabledByStoreId(storeId);
        int onSale = launched.size();
        int oos = 0;
        int low = 0;
        for (ShopMaterialStore relation : launched) {
            List<String> normsIds = normsIdsOfMaterial(relation.getMaterialId());
            int saleable = calcSaleableStock(storeId, relation, normsIds, enabledDepots);
            int safety = safetyOfMaterial(relation.getMaterialId());
            String type = diagnoseTypeOf(saleable, safety);
            if ("oos".equals(type)) {
                oos++;
            } else if ("low".equals(type)) {
                low++;
            }
        }
        QueryWrapper<ShopStoreDepot> depotCount = new QueryWrapper<>();
        depotCount.eq(MybatisPlusUtil.toColumns(ShopStoreDepot::getStoreId), storeId);
        long depotTotal = count(depotCount);

        Map<String, Object> bean = new HashMap<>();
        bean.put("onSaleCount", onSale);
        bean.put("oosCount", oos);
        bean.put("lowStockCount", low);
        bean.put("depotCount", depotTotal);
        bean.put("enabledDepotCount", enabledDepots.size());
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    @IgnoreTenant
    public void queryPersonalStoreMaterialStockEdit(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String storeId = MapUtil.getStr(params, "storeId");
        assertPersonalStoreOwner(storeId);
        String relationId = MapUtil.getStr(params, "id");
        ShopMaterialStore relation = shopMaterialStoreService.selectById(relationId);
        if (relation == null || !storeId.equals(relation.getStoreId())) {
            throw new CustomException("商品不存在");
        }
        assertNotPlatformDropship(relation);
        ShopMaterial shopMaterial = shopMaterialService.queryShopMaterialByMaterialId(relation.getMaterialId());
        if (shopMaterial == null) {
            throw new CustomException("商品不存在");
        }
        // 规格名称来自 ERP 规格
        List<MaterialNorms> materialNorms = materialNormsService.queryNormsUnitListByMaterialId(relation.getMaterialId());
        Map<String, MaterialNorms> normsMap = materialNorms.stream()
            .collect(Collectors.toMap(MaterialNorms::getId, n -> n, (a, b) -> a));
        List<String> normsIds = normsIdsOfMaterial(relation.getMaterialId());
        Integer mode = resolveStockMode(relation);
        List<ShopStoreDepot> enabledDepots = listEnabledByStoreId(storeId);
        // 补仓库名称
        List<String> depotIds = enabledDepots.stream().map(ShopStoreDepot::getDepotId)
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        Map<String, Depot> depotEntityMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(depotIds)) {
            List<Depot> depots = erpDepotService.selectByIds(depotIds.toArray(new String[0]));
            if (CollectionUtil.isNotEmpty(depots)) {
                depotEntityMap = depots.stream().collect(Collectors.toMap(Depot::getId, d -> d, (a, b) -> a));
            }
        }
        Map<String, Depot> finalDepotMap = depotEntityMap;
        String defaultDepotId = enabledDepots.stream()
            .filter(d -> IsDefaultEnum.IS_DEFAULT.getKey().equals(d.getIsDefault()))
            .map(ShopStoreDepot::getDepotId)
            .findFirst()
            .orElse(CollectionUtil.isEmpty(enabledDepots) ? StrUtil.EMPTY : enabledDepots.get(0).getDepotId());

        Map<String, String> shopStockMap = ShopMaterialStockMode.NORMAL.getKey().equals(mode)
            ? shopStockService.queryNormsShopStock(storeId, normsIds)
            : new HashMap<>();

        // 关联仓：一次查出全部仓×规格库存，避免循环内逐条 SQL
        Map<String, Map<String, String>> depotNormsStockMap = ShopMaterialStockMode.DEPOT_LINK.getKey().equals(mode)
            ? materialNormsStockService.queryMaterialNormsStockByDepotIds(normsIds, depotIds)
            : new HashMap<>();
        Map<String, String> defaultDepotStockMap = StrUtil.isBlank(defaultDepotId)
            ? new HashMap<>()
            : depotNormsStockMap.getOrDefault(defaultDepotId, new HashMap<>());

        List<Map<String, Object>> skuRows = new ArrayList<>();
        int idx = 0;
        for (String normsId : normsIds) {
            MaterialNorms mn = normsMap.get(normsId);
            String name = mn != null && StrUtil.isNotBlank(mn.getName())
                ? mn.getName()
                : (normsIds.size() == 1 ? "默认规格" : "规格" + (++idx));
            int stock;
            if (ShopMaterialStockMode.DEPOT_LINK.getKey().equals(mode)) {
                stock = Convert.toInt(defaultDepotStockMap.get(normsId), 0);
            } else {
                stock = Convert.toInt(shopStockMap.get(normsId), 0);
            }
            Map<String, Object> row = new HashMap<>();
            row.put("normsId", normsId);
            row.put("normsName", name);
            row.put("logo", shopMaterial.getLogo());
            row.put("stock", stock);
            row.put("lockedStock", 0);
            row.put("depotId", defaultDepotId);
            skuRows.add(row);
        }

        Map<String, String> depotStockMap = new HashMap<>();
        if (ShopMaterialStockMode.DEPOT_LINK.getKey().equals(mode)) {
            for (ShopStoreDepot depot : enabledDepots) {
                Map<String, String> normsStock = depotNormsStockMap.getOrDefault(depot.getDepotId(), new HashMap<>());
                for (String normsId : normsIds) {
                    depotStockMap.put(depot.getDepotId() + "_" + normsId,
                        StrUtil.blankToDefault(normsStock.get(normsId), CommonNumConstants.NUM_ZERO.toString()));
                }
            }
        }

        int saleable = calcSaleableStock(storeId, relation, normsIds, enabledDepots);
        List<Map<String, Object>> depotOptions = new ArrayList<>();
        for (ShopStoreDepot depot : enabledDepots) {
            Depot d = finalDepotMap.get(depot.getDepotId());
            Map<String, Object> opt = new HashMap<>();
            opt.put("id", depot.getId());
            opt.put("depotId", depot.getDepotId());
            opt.put("depotCode", depot.getDepotCode());
            opt.put("aliasName", depot.getAliasName());
            opt.put("isDefault", depot.getIsDefault());
            opt.put("name", d != null ? d.getName() : StrUtil.blankToDefault(depot.getAliasName(), "商家仓"));
            depotOptions.add(opt);
        }

        Map<String, Object> bean = new HashMap<>();
        bean.put("id", relation.getId());
        bean.put("storeId", storeId);
        bean.put("materialId", relation.getMaterialId());
        bean.put("stockMode", mode);
        bean.put("sourceType", relation.getSourceType());
        bean.put("saleableStock", saleable);
        bean.put("shopMaterial", shopMaterial);
        bean.put("skuRows", skuRows);
        bean.put("depotOptions", depotOptions);
        bean.put("depotStockMap", depotStockMap);
        bean.put("defaultDepotId", defaultDepotId);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}
