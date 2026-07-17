/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.coupon.dao.CouponDao;
import com.skyeye.coupon.entity.Coupon;
import com.skyeye.coupon.entity.CouponMaterial;
import com.skyeye.coupon.entity.CouponStore;
import com.skyeye.coupon.entity.CouponUse;
import com.skyeye.coupon.enums.CouponStoreCoverage;
import com.skyeye.coupon.enums.CouponValidityType;
import com.skyeye.coupon.enums.PromotionDiscountType;
import com.skyeye.coupon.enums.PromotionMaterialScope;
import com.skyeye.coupon.service.CouponMaterialService;
import com.skyeye.coupon.service.CouponService;
import com.skyeye.coupon.service.CouponStoreService;
import com.skyeye.coupon.service.CouponUseService;
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.shopmaterialnorms.sevice.IShopMaterialNormsService;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.service.ShopStoreService;
import com.skyeye.xxljob.ShopXxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: CouponServiceImpl
 * @Description: 优惠券/模版信息管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/23 10:07
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "优惠券/模版信息管理", groupName = "优惠券/模版信息管理")
public class CouponServiceImpl extends SkyeyeBusinessServiceImpl<CouponDao, Coupon> implements CouponService {

    @Autowired
    private CouponMaterialService couponMaterialService;

    @Autowired
    private IShopMaterialNormsService iShopMaterialNormsService;

    @Autowired
    private CouponUseService couponUseService;

    @Autowired
    private IQuartzService iQuartzService;

    @Autowired
    private CouponStoreService couponStoreService;

    @Autowired
    private ShopStoreService shopStoreService;

    private static final int QUERY_LIMIT = 10000;

    private static Logger log = LoggerFactory.getLogger(ShopXxlJob.class);

    @Override
    public void validatorEntity(Coupon coupon) {
        // 模板新增
        if (StrUtil.isEmpty(coupon.getId()) && StrUtil.isEmpty(coupon.getTemplateId()) && // 主键和模板id为空时，即为模板
            coupon.getProductScope() != PromotionMaterialScope.ALL.getKey() && // 判断适用商品类型
            CollectionUtil.isEmpty(coupon.getCouponMaterialList()))  // 不适用全部商品时，适用对象不能为空。
        {
            throw new CustomException("需要指定优惠券适用的商品范围，适用全部商品时可为空");
        }
        if (Objects.equals(coupon.getValidityType(), CouponValidityType.DATE.getKey())) {
            if (StrUtil.isEmpty(coupon.getValidStartTime()) || StrUtil.isEmpty(coupon.getValidEndTime())) {
                throw new CustomException("固定日期类型优惠券，有效期不能为空");
            }
            if (!DateUtil.compare(coupon.getValidStartTime(), coupon.getValidEndTime())) {
                throw new CustomException("固定日期类型优惠券，开始时间不能晚于结束时间");
            }
        }
        if (Objects.equals(coupon.getValidityType(), CouponValidityType.TERM.getKey())) {
            if (coupon.getFixedStartTime() == null || coupon.getFixedEndTime() == null || coupon.getFixedEndTime() == 0) {
                throw new CustomException("领取之后类型优惠券，有效期不能为空或为零");
            }
        }
        if (Objects.equals(coupon.getDiscountType(), PromotionDiscountType.PRICE.getKey())) {
            if (coupon.getDiscountPrice() == null) {
                throw new CustomException("价格折扣类型优惠券，折扣金额不能为空");
            }
            if (Integer.parseInt(coupon.getDiscountPrice()) > Integer.parseInt(coupon.getDiscountLimitPrice())) {
                throw new CustomException("价格折扣类型优惠券，折扣金额不能大于等于优惠上限金额");
            }
            if (Integer.parseInt(coupon.getDiscountPrice()) > Integer.parseInt(coupon.getUsePrice())) {
                throw new CustomException("价格折扣类型优惠券，折扣金额不能大于等于使用金额");
            }
        } else {
            if (coupon.getDiscountPercent() == null) {
                throw new CustomException("折扣率类型优惠券，折扣率不能为空");
            }
        }
        if (coupon.getTotalCount() <= CommonNumConstants.NUM_ZERO && coupon.getTotalCount() != -1) {
            throw new CustomException("优惠券总量不能为空");
        }
        if (coupon.getUseCount() <= CommonNumConstants.NUM_ZERO) {
            throw new CustomException("优惠券总使用次数不能为零");
        }
    }

    @Override
    public void createPrepose(Coupon entity) {
        entity.setTakeCount(CommonNumConstants.NUM_ZERO);
    }

    @Override
    public void createPostpose(Coupon entity, String userId) {
        if (StrUtil.isNotEmpty(entity.getTemplateId())) {// 优惠券
            if (Objects.equals(entity.getValidityType(), CouponValidityType.DATE.getKey())) {
                log.info("优惠券id" + entity.getId() + "创建定时任务-- 开始");
                startUpTaskQuartz(entity.getId(), entity.getName(), entity.getValidEndTime());
                log.info("优惠券id" + entity.getId() + "创建定时任务-- 结束");
            }
        }
    }

    private void startUpTaskQuartz(String name, String title, String delayedTime) {
        SysQuartzMation sysQuartzMation = new SysQuartzMation();
        sysQuartzMation.setName(name);
        sysQuartzMation.setTitle(title);
        sysQuartzMation.setDelayedTime(delayedTime);
        sysQuartzMation.setGroupId(QuartzConstants.QuartzMateMationJobType.SHOP_COUPON.getTaskType());
        iQuartzService.startUpTaskQuartz(sysQuartzMation);
    }

    @Override
    public void updatePrepose(Coupon entity) {
        Coupon oldCoupon = selectById(entity.getId());
        entity.setTakeCount(oldCoupon.getTakeCount());
    }

    @Override
    public void writePostpose(Coupon coupon, String userId) {
        // 新增/编辑优惠券的适用商品对象
        if (coupon.getProductScope() == PromotionMaterialScope.ALL.getKey()) {
            // 适用全部商品
            List<Map<String, Object>> material = iShopMaterialNormsService.queryAllShopMaterialListForChoose();
            if (CollectionUtil.isNotEmpty(material)) {
                List<CouponMaterial> couponMaterialList = material.stream().map(bean -> {
                    CouponMaterial couponMaterial = new CouponMaterial();
                    couponMaterial.setMaterialId(bean.get("id").toString());
                    return couponMaterial;
                }).collect(Collectors.toList());
                couponMaterialService.insertCouponMaterial(coupon.getId(), couponMaterialList, userId);
            }
        } else if (coupon.getProductScope() == PromotionMaterialScope.SPU.getKey()) {
            // 适用指定商品
            if (CollectionUtil.isNotEmpty(coupon.getCouponMaterialList())) {
                couponMaterialService.insertCouponMaterial(coupon.getId(), coupon.getCouponMaterialList(), userId);
            }
        }
        if (coupon.getStoreCoverage() == CouponStoreCoverage.SPECIFIED_STORE.getKey()) {
            // 指定门店
            // 先删除原有关联门店
            couponStoreService.deleteByCouponIds(Collections.singletonList(coupon.getId()));
            if (CollectionUtil.isNotEmpty(coupon.getStoreIdList())) {// 优惠券关联门店
                couponStoreService.createEntity(coupon.getId(), coupon.getStoreIdList());
            }
        } else if (coupon.getStoreCoverage() == CouponStoreCoverage.ALL_STORE.getKey()) {
            // 全部门店
            couponStoreService.deleteByCouponIds(Collections.singletonList(coupon.getId()));
        }
    }

    @Override
    @IgnoreTenant
    public Coupon selectById(String id) {
        Coupon coupon = super.selectById(id);
        if (ObjectUtil.isNotEmpty(coupon)) {
            List<CouponStore> couponStores = couponStoreService.queryListByCouponId(id);
            coupon.setCouponStoreList(couponStores);
            if (CollectionUtil.isNotEmpty(couponStores)) {
                List<String> storeIds = couponStores.stream().map(CouponStore::getStoreId).distinct().collect(Collectors.toList());
                coupon.setStoreIdList(storeIds);
            }
        }
        return coupon;
    }

    @Override
    public QueryWrapper<Coupon> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Coupon> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String type = commonPageInfo.getType();
        if (StrUtil.isEmpty(type)) {
            throw new CustomException("暂不支持该类型查询");
        }
        String typeKey = MybatisPlusUtil.toColumns(Coupon::getTemplateId);
        if (type.equals(CommonNumConstants.NUM_ZERO.toString())) {
            queryWrapper.and(wra -> {
                wra.isNull(typeKey).or().eq(typeKey, StrUtil.EMPTY);
            });
        }
        if (type.equals(CommonNumConstants.NUM_ONE.toString())) {
            queryWrapper.and(wra -> {
                wra.isNotNull(typeKey).ne(typeKey, StrUtil.EMPTY);
            });
        }
        return queryWrapper;
    }

    @Override
    public void queryCouponListByState(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        String storeId = params.get("storeId").toString();
        String type = params.get("type").toString();
        /*
         * todo 优惠券是由厂商发布的，门店无法发放优惠券
         *  需要线判断type，再考虑storeId
         *  模板通用、查模板时不需要storeId，先判断type
         */
        String typeKey = MybatisPlusUtil.toColumns(Coupon::getTemplateId);
        if (StrUtil.equals(type, CommonNumConstants.NUM_ZERO.toString())) {
            queryWrapper.and(wrapper -> {
                wrapper.isNull(typeKey).or().eq(typeKey, StrUtil.EMPTY);
            });
        }
        if (StrUtil.equals(type, CommonNumConstants.NUM_ONE.toString())) {
            queryWrapper.and(Wrapper -> {
                Wrapper.isNotNull(typeKey).ne(typeKey, StrUtil.EMPTY);
            });
            String totalCountKey = MybatisPlusUtil.toColumns(Coupon::getTotalCount);
            String takeCountKey = MybatisPlusUtil.toColumns(Coupon::getTakeCount);
            queryWrapper.and(w -> w.eq(totalCountKey, -1).or().apply(takeCountKey + " < " + totalCountKey));
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(Coupon::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<Coupon> list = list(queryWrapper);
        setDrawState(list);// 设置是否可以领取状态
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    @IgnoreTenant
    public void updateTakeCount(String couponId, Integer takeCount) {
        UpdateWrapper<Coupon> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, couponId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Coupon::getTakeCount), takeCount);
        update(updateWrapper);
        refreshCache(couponId);
    }

    @Override
    public void deletePostpose(List<String> ids) {
        couponMaterialService.deleteByCouponId(ids);// 删除优惠券的适用对象
        couponStoreService.deleteByCouponIds(ids);// 删除优惠券与门店关联的信息
        // 删除定时任务
        deleteJobByCouponIdList(ids);
        couponUseService.deleteByCouponIds(ids);  // 删除已领取的但是未使用的优惠券
    }

    private void deleteJobByCouponIdList(List<String> couponIdList) {
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, couponIdList);
        List<Coupon> list = list(queryWrapper);
        // 固定日期类型的优惠券
        List<String> deleteObjectIds = new ArrayList<>();
        List<String> dateCouponIds = list.stream().filter(coupon -> Objects.equals(coupon.getValidityType(), CouponValidityType.DATE.getKey())).map(Coupon::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(dateCouponIds)) {
            deleteObjectIds.addAll(dateCouponIds);
        }
        // 领取之后类型的优惠券
        List<String> termCouponIds = list.stream().filter(coupon -> Objects.equals(coupon.getValidityType(), CouponValidityType.TERM.getKey())).map(Coupon::getId).collect(Collectors.toList());
        List<CouponUse> couponUseList = couponUseService.queryUnUseByCouponIdList(termCouponIds);
        if (CollectionUtil.isNotEmpty(couponUseList)) {
                deleteObjectIds.addAll(couponUseList.stream().map(CouponUse::getId).collect(Collectors.toList()));
        }
        // 删除定时任务
        log.info("批量删除优惠券：" + couponIdList.toString() + "-- 开始");
        iQuartzService.batchStopAndDeleteTaskQuartz(deleteObjectIds);
        log.info("批量删除优惠券：------- 结束");
    }
    @Override
    public Coupon getDataFromDb(String id) {
        Coupon coupon = super.getDataFromDb(id);
        coupon.setCouponMaterialList(couponMaterialService.queryListByCouponId(id));
        setDrawState(Collections.singletonList(coupon));// 设置是否可以领取状态
        return coupon;
    }

    @Override
    @IgnoreTenant
    public void queryCouponListByMaterialId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String materialId = params.get("materialId").toString();
        String storeId = params.get("storeId").toString();

        String typeKey = MybatisPlusUtil.toColumns(Coupon::getTemplateId);
        MPJLambdaWrapper<Coupon> wrapper = new MPJLambdaWrapper<Coupon>()
            .innerJoin(CouponMaterial.class, CouponMaterial::getCouponId, Coupon::getId)
            .eq(CouponMaterial::getMaterialId, materialId)
            .eq(MybatisPlusUtil.toColumns(Coupon::getEnabled), EnableEnum.ENABLE_USING.getKey())
            .isNotNull(typeKey).ne(typeKey, StrUtil.EMPTY)
            .leftJoin(CouponStore.class, CouponStore::getCouponId, Coupon::getId)
            .and(w -> w.eq(Coupon::getStoreCoverage, CouponStoreCoverage.ALL_STORE.getKey())
                .or(w2 -> w2.eq(Coupon::getStoreCoverage, CouponStoreCoverage.SPECIFIED_STORE.getKey())
                    .eq(CouponStore::getStoreId, storeId)))
            .groupBy(Coupon::getId);
        List<Coupon> list = skyeyeBaseMapper.selectJoinList(Coupon.class, wrapper);
        setDrawState(list);// 设置是否可以领取状态
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    /**
     * 查询优惠券适用的门店/商品列表。
     * 按「单门店单商品 / 单门店多商品 / 多门店单商品 / 多门店多商品」四种场景分支组装返回结构。
     */
    @Override
    @IgnoreTenant
    public void queryCouponApplicableMaterialList(InputObject inputObject, OutputObject outputObject) {
        String couponId = inputObject.getParams().get("couponId").toString();

        Coupon coupon = selectById(couponId);
        if (ObjectUtil.isEmpty(coupon)) {
            throw new CustomException("优惠券不存在");
        }
        if (!Objects.equals(coupon.getEnabled(), EnableEnum.ENABLE_USING.getKey())) {
            throw new CustomException("优惠券已失效");
        }
        if (!Objects.equals(coupon.getProductScope(), PromotionMaterialScope.ALL.getKey())
            && CollectionUtil.isEmpty(coupon.getCouponMaterialList())) {
            coupon.setCouponMaterialList(couponMaterialService.queryListByCouponId(couponId));
        }

        boolean allMaterial = Objects.equals(coupon.getProductScope(), PromotionMaterialScope.ALL.getKey());
        boolean allStore = Objects.equals(coupon.getStoreCoverage(), CouponStoreCoverage.ALL_STORE.getKey());
        List<String> materialIdList = resolveMaterialIdList(coupon, allMaterial);
        List<String> storeIdList = resolveStoreIdList(coupon, allStore);

        // 指定范围却无关联数据 → 空结果（全部商品+全部门店由前端处理，不会进本接口）
        if (!allMaterial && CollectionUtil.isEmpty(materialIdList)) {
            outputObject.setBeans(Collections.emptyList());
            outputObject.settotal(CommonNumConstants.NUM_ZERO);
            return;
        }
        if (!allStore && CollectionUtil.isEmpty(storeIdList)) {
            outputObject.setBeans(Collections.emptyList());
            outputObject.settotal(CommonNumConstants.NUM_ZERO);
            return;
        }

        boolean singleMaterial = !allMaterial && materialIdList.size() == CommonNumConstants.NUM_ONE;
        boolean singleStore = !allStore && storeIdList.size() == CommonNumConstants.NUM_ONE;

        if (singleMaterial && singleStore) {
            buildOneStoreOneMaterial(outputObject, storeIdList.get(0), materialIdList.get(0));
        } else if (singleStore) {
            buildOneStoreMultiMaterial(outputObject, storeIdList.get(0), materialIdList, allMaterial);
        } else if (singleMaterial) {
            buildStoreListWithMaterials(outputObject, storeIdList, allStore,
                loadMaterialsByHolder(materialIdList.get(0), storeIdList, allStore));
        } else {
            buildStoreListWithMaterials(outputObject, storeIdList, allStore,
                loadMaterialsForMulti(materialIdList, storeIdList, allMaterial, allStore));
        }
    }

    /**
     * 解析优惠券适用的商品 ID 列表；全部商品时返回空列表。
     */
    private List<String> resolveMaterialIdList(Coupon coupon, boolean allMaterial) {
        if (allMaterial) {
            return Collections.emptyList();
        }
        if (CollectionUtil.isEmpty(coupon.getCouponMaterialList())) {
            return Collections.emptyList();
        }
        return coupon.getCouponMaterialList().stream()
            .map(CouponMaterial::getMaterialId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * 解析优惠券适用的门店 ID 列表；全部门店时返回空列表。
     */
    private List<String> resolveStoreIdList(Coupon coupon, boolean allStore) {
        if (allStore) {
            return Collections.emptyList();
        }
        if (CollectionUtil.isEmpty(coupon.getStoreIdList())) {
            return Collections.emptyList();
        }
        return coupon.getStoreIdList().stream()
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * 单门店 + 单商品：直接返回该门店下该商品的规格数据。
     */
    private void buildOneStoreOneMaterial(OutputObject outputObject, String storeId, String materialId) {
        List<Map<String, Object>> rows = queryShopMaterials(storeId, materialId);
        if (CollectionUtil.isEmpty(rows)) {
            outputObject.setBeans(Collections.emptyList());
            outputObject.settotal(CommonNumConstants.NUM_ZERO);
            return;
        }
        outputObject.setBean(rows.get(0));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 单门店 + 多商品（或全部商品）：返回门店信息，并挂上该门店下的适用商品列表。
     */
    private void buildOneStoreMultiMaterial(OutputObject outputObject, String storeId,
                                            List<String> materialIdList, boolean allMaterial) {
        ShopStore shopStore = shopStoreService.selectById(storeId);
        if (ObjectUtil.isEmpty(shopStore)) {
            outputObject.setBeans(Collections.emptyList());
            outputObject.settotal(CommonNumConstants.NUM_ZERO);
            return;
        }
        List<Map<String, Object>> materials = queryShopMaterials(storeId, null);
        if (!allMaterial) {
            Set<String> materialIdSet = new HashSet<>(materialIdList);
            materials = materials.stream()
                .filter(row -> materialIdSet.contains(mapStr(row, "materialId")))
                .collect(Collectors.toList());
        }
        Map<String, Object> storeMap = BeanUtil.beanToMap(shopStore);
        storeMap.put("shopMaterialList", materials);
        outputObject.setBean(storeMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 多门店场景：把门店列表与商品列表按 storeId 组装，每个门店挂上各自的 shopMaterialList。
     */
    private void buildStoreListWithMaterials(OutputObject outputObject, List<String> storeIdList, boolean allStore,
                                             List<Map<String, Object>> materials) {
        List<ShopStore> stores = loadStores(storeIdList, allStore);
        if (CollectionUtil.isEmpty(stores)) {
            outputObject.setBeans(Collections.emptyList());
            outputObject.settotal(CommonNumConstants.NUM_ZERO);
            return;
        }
        Map<String, List<Map<String, Object>>> materialByStore = CollectionUtil.isEmpty(materials)
            ? Collections.emptyMap()
            : materials.stream()
            .filter(row -> StrUtil.isNotBlank(mapStr(row, "storeId")))
            .collect(Collectors.groupingBy(row -> mapStr(row, "storeId")));

        List<Map<String, Object>> result = new ArrayList<>();
        for (ShopStore store : stores) {
            List<Map<String, Object>> storeMaterials = materialByStore.get(store.getId());
            if (CollectionUtil.isEmpty(storeMaterials)) {
                continue;
            }
            Map<String, Object> storeMap = BeanUtil.beanToMap(store);
            storeMap.put("shopMaterialList", storeMaterials);
            result.add(storeMap);
        }
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    /**
     * 加载门店：全部门店查启用中的门店，否则按指定 storeIdList 查询。
     */
    private List<ShopStore> loadStores(List<String> storeIdList, boolean allStore) {
        if (allStore) {
            QueryWrapper<ShopStore> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStore::getEnabled), EnableEnum.ENABLE_USING.getKey());
            return shopStoreService.list(queryWrapper);
        }
        return shopStoreService.selectByIds(storeIdList.toArray(new String[0]));
    }

    /**
     * 按单个商品（holderId）查各门店上架数据；非全部门店时再按 storeIdList 过滤。
     */
    private List<Map<String, Object>> loadMaterialsByHolder(String materialId, List<String> storeIdList, boolean allStore) {
        List<Map<String, Object>> rows = queryShopMaterials(null, materialId);
        if (allStore || CollectionUtil.isEmpty(rows)) {
            return rows;
        }
        Set<String> storeIdSet = new HashSet<>(storeIdList);
        return rows.stream()
            .filter(row -> storeIdSet.contains(mapStr(row, "storeId")))
            .collect(Collectors.toList());
    }

    /**
     * 多商品场景加载商品数据：
     * 指定门店时按门店查询后再按商品过滤；全部门店时按商品逐个查询。
     */
    private List<Map<String, Object>> loadMaterialsForMulti(List<String> materialIdList, List<String> storeIdList,
                                                            boolean allMaterial, boolean allStore) {
        if (!allStore) {
            List<Map<String, Object>> result = new ArrayList<>();
            Set<String> materialIdSet = allMaterial ? null : new HashSet<>(materialIdList);
            for (String storeId : storeIdList) {
                List<Map<String, Object>> rows = queryShopMaterials(storeId, null);
                if (materialIdSet != null) {
                    rows = rows.stream()
                        .filter(row -> materialIdSet.contains(mapStr(row, "materialId")))
                        .collect(Collectors.toList());
                }
                result.addAll(rows);
            }
            return result;
        }
        // 全部门店 + 指定多商品
        List<Map<String, Object>> result = new ArrayList<>();
        for (String materialId : materialIdList) {
            result.addAll(queryShopMaterials(null, materialId));
        }
        return result;
    }

    /**
     * 远程查询门店商品：objectId 为门店 ID，holderId 为商品 ID，均可选。
     */
    private List<Map<String, Object>> queryShopMaterials(String objectId, String holderId) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", CommonNumConstants.NUM_ONE);
        queryParams.put("limit", QUERY_LIMIT);
        if (StrUtil.isNotBlank(objectId)) {
            queryParams.put("objectId", objectId);
        }
        if (StrUtil.isNotBlank(holderId)) {
            queryParams.put("holderId", holderId);
        }
        ResultEntity resultEntity = iShopMaterialNormsService.queryShopMaterialList(queryParams);
        if (resultEntity == null || CollectionUtil.isEmpty(resultEntity.getRows())) {
            return Collections.emptyList();
        }
        return resultEntity.getRows();
    }

    /**
     * 从 Map 中安全取字符串值，空则返回空串。
     */
    private String mapStr(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value == null ? StrUtil.EMPTY : value.toString();
    }

    private void setDrawState(List<Coupon> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<String> couponIdList = list.stream().map(Coupon::getId).collect(Collectors.toList());
        Map<String, Integer> map = couponUseService.queryIdTotalMapByCouponId(couponIdList);
        for (Coupon coupon : list) {
            Integer takeLimitCount = coupon.getTakeLimitCount();// 限制领取数量
            Integer takeCount = map.containsKey(coupon.getId()) ? map.get(coupon.getId()) : CommonNumConstants.NUM_ZERO;// 已经领的
            coupon.setCanDraw(takeLimitCount == -1 ? true : takeCount < takeLimitCount);
        }
    }

    @Override
    public void setStateByCoupon(String surveyId) {
        UpdateWrapper<Coupon> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, surveyId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Coupon::getEnabled), EnableEnum.DISABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    @IgnoreTenant
    public <M> void setDataMation(M bean, SFunction<M, ?> sFunction) {
        super.setDataMation(bean, sFunction);
    }

    @Override
    @IgnoreTenant
    public <M> void setDataMation(List<M> beans, SFunction<M, ?> sFunction) {
        super.setDataMation(beans, sFunction);
    }

    @Override
    @IgnoreTenant
    public void setMationForMap(List<Map<String, Object>> beans, String idKey, String nameKey) {
        super.setMationForMap(beans, idKey, nameKey);
    }
}
