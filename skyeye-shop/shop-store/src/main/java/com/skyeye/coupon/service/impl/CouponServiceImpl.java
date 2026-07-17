/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.service.impl;

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
     * 根据优惠券获取适用门店。入参：page、limit + customParamsMap.couponId
     */
    @Override
    @IgnoreTenant
    public void queryCouponApplicableStoreList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String couponId = commonPageInfo.getCustomParamsMapStr("couponId");
        Coupon coupon = loadEnabledCoupon(couponId);
        if (coupon == null) {
            return;
        }

        boolean allStore = Objects.equals(coupon.getStoreCoverage(), CouponStoreCoverage.ALL_STORE.getKey());
        List<String> storeIdList = resolveStoreIdList(coupon, allStore);
        if (!allStore && CollectionUtil.isEmpty(storeIdList)) {
            return;
        }

        List<ShopStore> stores = loadStores(storeIdList, allStore);
        if (CollectionUtil.isEmpty(stores)) {
            return;
        }
        List<ShopStore> pageStores = pageList(stores, commonPageInfo);
        outputObject.setBeans(pageStores);
        outputObject.settotal(stores.size());
    }

    /**
     * 根据优惠券和门店获取适用商品。入参：page、limit + customParamsMap.couponId、storeId
     */
    @Override
    @IgnoreTenant
    public void queryCouponApplicableMaterialList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String couponId = commonPageInfo.getCustomParamsMapStr("couponId");
        String storeId = commonPageInfo.getCustomParamsMapStr("storeId");
        if (StrUtil.isBlank(storeId)) {
            return;
        }
        Coupon coupon = loadEnabledCoupon(couponId);
        if (coupon == null) {
            return;
        }
        if (!Objects.equals(coupon.getProductScope(), PromotionMaterialScope.ALL.getKey())
            && CollectionUtil.isEmpty(coupon.getCouponMaterialList())) {
            coupon.setCouponMaterialList(couponMaterialService.queryListByCouponId(couponId));
        }

        boolean allMaterial = Objects.equals(coupon.getProductScope(), PromotionMaterialScope.ALL.getKey());
        boolean allStore = Objects.equals(coupon.getStoreCoverage(), CouponStoreCoverage.ALL_STORE.getKey());
        List<String> materialIdList = resolveMaterialIdList(coupon, allMaterial);
        List<String> storeIdList = resolveStoreIdList(coupon, allStore);

        if (!allMaterial && CollectionUtil.isEmpty(materialIdList)) {
            return;
        }
        // 指定门店时，入参 storeId 须在适用门店内
        if (!allStore) {
            if (CollectionUtil.isEmpty(storeIdList) || !storeIdList.contains(storeId)) {
                return;
            }
        }

        // 门店 + 商品范围取交集
        ResultEntity resultEntity = queryShopMaterialsByStore(commonPageInfo, storeId, materialIdList, allMaterial);
        if (resultEntity == null || CollectionUtil.isEmpty(resultEntity.getRows())) {
            return;
        }
        outputObject.setBeans(resultEntity.getRows());
        outputObject.settotal(resultEntity.getTotal());
    }

    /**
     * 查询场景：参数缺失/券不存在/已失效时返回 null，由调用方直接空返回。
     */
    private Coupon loadEnabledCoupon(String couponId) {
        if (StrUtil.isBlank(couponId)) {
            return null;
        }
        Coupon coupon = selectById(couponId);
        if (ObjectUtil.isEmpty(coupon)) {
            return null;
        }
        if (!Objects.equals(coupon.getEnabled(), EnableEnum.ENABLE_USING.getKey())) {
            return null;
        }
        return coupon;
    }

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

    private List<ShopStore> loadStores(List<String> storeIdList, boolean allStore) {
        if (allStore) {
            QueryWrapper<ShopStore> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStore::getEnabled), EnableEnum.ENABLE_USING.getKey());
            return shopStoreService.list(queryWrapper);
        }
        return shopStoreService.selectByIds(storeIdList.toArray(new String[0]));
    }

    private ResultEntity queryShopMaterialsByStore(CommonPageInfo commonPageInfo, String storeId,
                                                   List<String> materialIdList, boolean allMaterial) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", commonPageInfo.getPage());
        queryParams.put("limit", commonPageInfo.getLimit());
        queryParams.put("objectId", storeId);
        if (!allMaterial && CollectionUtil.isNotEmpty(materialIdList)) {
            queryParams.put("materialIds", String.join(CommonCharConstants.COMMA_MARK, materialIdList));
        }
        ResultEntity resultEntity = iShopMaterialNormsService.queryShopMaterialList(queryParams);
        if (resultEntity == null || CollectionUtil.isEmpty(resultEntity.getRows()) || allMaterial) {
            return resultEntity;
        }
        // 指定商品：与券适用商品取交集（防止 materialIds 未生效时仍返回全店商品）
        Set<String> materialIdSet = new HashSet<>(materialIdList);
        List<Map<String, Object>> filtered = resultEntity.getRows().stream()
            .filter(row -> {
                Object materialId = row.get("materialId");
                return materialId != null && materialIdSet.contains(materialId.toString());
            })
            .collect(Collectors.toList());
        resultEntity.setRows(filtered);
        resultEntity.setTotal(filtered.size());
        return resultEntity;
    }

    private <T> List<T> pageList(List<T> list, CommonPageInfo commonPageInfo) {
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        int page = commonPageInfo.getPage() == null ? CommonNumConstants.NUM_ONE : commonPageInfo.getPage();
        int limit = commonPageInfo.getLimit() == null ? list.size() : commonPageInfo.getLimit();
        int fromIndex = Math.max((page - CommonNumConstants.NUM_ONE) * limit, CommonNumConstants.NUM_ZERO);
        if (fromIndex >= list.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(fromIndex + limit, list.size());
        return list.subList(fromIndex, toIndex);
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
