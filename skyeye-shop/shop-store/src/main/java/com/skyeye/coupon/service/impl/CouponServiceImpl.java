/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.coupon.dao.CouponDao;
import com.skyeye.coupon.entity.Coupon;
import com.skyeye.coupon.entity.CouponMaterial;
import com.skyeye.coupon.entity.CouponStore;
import com.skyeye.coupon.entity.CouponUse;
import com.skyeye.coupon.enums.*;
import com.skyeye.coupon.service.CouponMaterialService;
import com.skyeye.coupon.service.CouponService;
import com.skyeye.coupon.service.CouponStoreService;
import com.skyeye.coupon.service.CouponUseService;
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.shopmaterialnorms.sevice.IShopMaterialNormsService;
import com.skyeye.store.classenum.StoreNature;
import com.skyeye.store.entity.ShopStore;
import com.skyeye.store.entity.ShopStoreStaff;
import com.skyeye.store.service.ShopStoreService;
import com.skyeye.store.service.ShopStoreStaffService;
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

    @Autowired
    private ShopStoreStaffService shopStoreStaffService;

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
        // 门店工作台：按 Coupon.storeId 归属
        if (StrUtil.isNotBlank(coupon.getStoreId())) {
            ShopStore store = assertStoreCouponAccess(coupon.getStoreId());
            coupon.setCouponSource(CouponSource.STORE.getKey());
            if (StrUtil.isBlank(coupon.getTemplateId())) {
                coupon.setTemplateId(StrUtil.EMPTY);
            } else {
                assertStoreOwnedTemplate(coupon.getTemplateId(), store);
            }
            if (Objects.equals(coupon.getProductScope(), PromotionMaterialScope.SPU.getKey())
                && CollectionUtil.isEmpty(coupon.getCouponMaterialList())) {
                throw new CustomException("请选择适用商品");
            }
            if (Objects.equals(coupon.getProductScope(), PromotionMaterialScope.SPU.getKey())
                && CollectionUtil.isNotEmpty(coupon.getCouponMaterialList())) {
                assertStoreCouponMaterialsAreSelfBuilt(coupon.getStoreId(), coupon.getCouponMaterialList());
            }
            applyStoreCoverageRule(coupon, store, coupon.getStoreId());
            if (StrUtil.isNotBlank(coupon.getId())) {
                assertStoreOwnedCoupon(coupon.getId(), store);
            }
        }
    }

    /**
     * 门店券指定商品只能选自建：平台货源（sourceType=平台货源 / 有供货门店）不可绑定，避免给供货方商品降价。
     */
    private void assertStoreCouponMaterialsAreSelfBuilt(String storeId, List<CouponMaterial> materials) {
        List<String> materialIds = materials.stream()
            .map(CouponMaterial::getMaterialId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(materialIds)) {
            return;
        }
        Map<String, Object> materialStoreIdMap = iShopMaterialNormsService
            .queryShopMaterialMapByMaterialIdsAndStoreIds(materialIds, Collections.singletonList(storeId));
        if (CollectionUtil.isEmpty(materialStoreIdMap)) {
            throw new CustomException("适用商品不在本店商品中");
        }
        List<String> materialStoreIds = materialStoreIdMap.values().stream()
            .map(Object::toString)
            .collect(Collectors.toList());
        List<Map<String, Object>> materialByIds = iShopMaterialNormsService.queryShopMaterialByIds(materialStoreIds);
        if (CollectionUtil.isEmpty(materialByIds)) {
            throw new CustomException("适用商品不在本店商品中");
        }
        // 平台货源 sourceType = 2（与 ShopMaterialStoreSourceType.PLATFORM 一致）
        final int platformSourceType = 2;
        for (Map<String, Object> map : materialByIds) {
            if (ObjectUtil.isEmpty(map.get("shopMaterialStore"))) {
                continue;
            }
            Map<String, Object> shopMaterialStore = JSONUtil.toBean(JSONUtil.toJsonStr(map.get("shopMaterialStore")), null);
            if (CollectionUtil.isEmpty(shopMaterialStore)) {
                continue;
            }
            Integer sourceType = MapUtil.getInt(shopMaterialStore, "sourceType");
            String sourceStoreId = MapUtil.getStr(shopMaterialStore, "sourceStoreId");
            if (Objects.equals(sourceType, platformSourceType) || StrUtil.isNotBlank(sourceStoreId)) {
                throw new CustomException("平台货源商品不能设置为本店优惠券适用商品");
            }
        }
    }

    @Override
    public void createPrepose(Coupon entity) {
        entity.setTakeCount(CommonNumConstants.NUM_ZERO);
        entity.setTenantId(null);
        if (StrUtil.isNotBlank(entity.getStoreId())) {
            entity.setCouponSource(CouponSource.STORE.getKey());
        } else if (entity.getCouponSource() == null) {
            entity.setCouponSource(CouponSource.PLATFORM.getKey());
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
        if (StrUtil.isNotBlank(entity.getStoreId())) {
            entity.setCouponSource(CouponSource.STORE.getKey());
            // 归属门店不可改
            entity.setStoreId(oldCoupon.getStoreId());
        } else if (entity.getCouponSource() == null) {
            entity.setCouponSource(oldCoupon.getCouponSource() == null
                ? CouponSource.PLATFORM.getKey() : oldCoupon.getCouponSource());
        }
    }

    @Override
    public void writePostpose(Coupon coupon, String userId) {
        // 新增/编辑优惠券的适用商品对象
        if (coupon.getProductScope() == PromotionMaterialScope.ALL.getKey()) {
            if (Objects.equals(coupon.getStoreCoverage(), CouponStoreCoverage.SPECIFIED_STORE.getKey())) {
                // 指定门店 + 全部商品：不绑全租户商品，下单按门店范围校验；商品页可领券走门店维度列表
                couponMaterialService.deleteByCouponId(coupon.getId());
            } else {
                // 全部门店 + 全部商品：绑定当前租户全部商城商品
                List<Map<String, Object>> material = iShopMaterialNormsService.queryAllShopMaterialListForChoose();
                if (CollectionUtil.isNotEmpty(material)) {
                    List<CouponMaterial> couponMaterialList = material.stream().map(bean -> {
                        CouponMaterial couponMaterial = new CouponMaterial();
                        couponMaterial.setMaterialId(bean.get("id").toString());
                        return couponMaterial;
                    }).collect(Collectors.toList());
                    couponMaterialService.insertCouponMaterial(coupon.getId(), couponMaterialList, userId);
                }
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
        // 优惠券：先删调度任务再按固定日期重建，避免编辑后任务未更新导致过期仍可用
        if (StrUtil.isNotEmpty(coupon.getTemplateId())) {
            log.info("优惠券id" + coupon.getId() + "删除定时任务-- 开始");
            iQuartzService.stopAndDeleteTaskQuartz(coupon.getId());
            log.info("优惠券id" + coupon.getId() + "删除定时任务-- 结束");
            if (Objects.equals(coupon.getValidityType(), CouponValidityType.DATE.getKey())
                && DateUtil.compare(DateUtil.getTimeAndToString(), coupon.getValidEndTime())) {
                // 结束时间晚于当前时间才创建定时任务，已过期则不再创建
                log.info("优惠券id" + coupon.getId() + "创建定时任务-- 开始");
                startUpTaskQuartz(coupon.getId(), coupon.getName(), coupon.getValidEndTime());
                log.info("优惠券id" + coupon.getId() + "创建定时任务-- 结束");
            }
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
    @IgnoreTenant
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {
        super.queryPageList(inputObject, outputObject);
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
            queryWrapper.and(wra -> wra.isNull(typeKey).or().eq(typeKey, StrUtil.EMPTY));
        }
        if (type.equals(CommonNumConstants.NUM_ONE.toString())) {
            queryWrapper.and(wra -> wra.isNotNull(typeKey).ne(typeKey, StrUtil.EMPTY));
        }
        String sourceKey = MybatisPlusUtil.toColumns(Coupon::getCouponSource);
        // 门店工作台：objectId = 归属门店 store_id
        if (StrUtil.isNotBlank(commonPageInfo.getObjectId())) {
            assertStoreCouponAccess(commonPageInfo.getObjectId());
            queryWrapper.eq(MybatisPlusUtil.toColumns(Coupon::getStoreId), commonPageInfo.getObjectId());
            queryWrapper.eq(sourceKey, CouponSource.STORE.getKey());
            return queryWrapper;
        }
        // 管理端：仅管理端来源 + 当前租户（本方法在 IgnoreTenant 下，需手工加租户）
        queryWrapper.and(wra -> wra.isNull(sourceKey).or().eq(sourceKey, CouponSource.PLATFORM.getKey()));
        String tenantId = TenantContext.getTenantId();
        if (StrUtil.isNotBlank(tenantId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Coupon::getTenantId), tenantId);
        }
        return queryWrapper;
    }

    @Override
    @IgnoreTenant
    public void queryCouponListByState(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        // 门店id
        String storeId = commonPageInfo.getCustomParamsMapStr("storeId");
        // 类型：优惠券：1，优惠券模板：0，全部：为空
        String type = commonPageInfo.getType();
        // 折扣类型（可选）：1 满减，2 折扣
        String discountType = commonPageInfo.getCustomParamsMapStr("discountType");

        String typeKey = MybatisPlusUtil.toColumns(Coupon::getTemplateId);
        Page pages = null;
        if (commonPageInfo.getIsPaging()) {
            pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        }

        MPJLambdaWrapper<Coupon> wrapper = new MPJLambdaWrapper<Coupon>()
            .eq(MybatisPlusUtil.toColumns(Coupon::getEnabled), EnableEnum.ENABLE_USING.getKey());
        if (StrUtil.equals(type, CommonNumConstants.NUM_ZERO.toString())) {
            // 模板：templateId 为空
            wrapper.and(w -> w.isNull(typeKey).or().eq(typeKey, StrUtil.EMPTY));
        }
        if (StrUtil.equals(type, CommonNumConstants.NUM_ONE.toString())) {
            // 优惠券：有模板，且仍有剩余可领数量
            wrapper.isNotNull(typeKey).ne(typeKey, StrUtil.EMPTY);
            String totalCountKey = MybatisPlusUtil.toColumns(Coupon::getTotalCount);
            String takeCountKey = MybatisPlusUtil.toColumns(Coupon::getTakeCount);
            wrapper.and(w -> w.eq(totalCountKey, -1).or().apply(takeCountKey + " < " + totalCountKey));
        }
        if (StrUtil.isNotEmpty(discountType)) {
            wrapper.eq(Coupon::getDiscountType, Integer.valueOf(discountType));
        }
        // 有效期过滤：固定日期类型且已过结束时间的不返回；领取后生效类型无固定截止时间，不过滤
        String now = DateUtil.getTimeAndToString();
        wrapper.and(w -> w.ne(Coupon::getValidityType, CouponValidityType.DATE.getKey())
            .or(w2 -> w2.eq(Coupon::getValidityType, CouponValidityType.DATE.getKey())
                .ge(Coupon::getValidEndTime, now)));
        // 按门店过滤：指定门店关联本店；全部门店仅同租户券可用（个人店/其他租户不可用）
        if (StrUtil.isNotEmpty(storeId)) {
            ShopStore store = shopStoreService.selectById(storeId);
            if (store == null || StrUtil.isBlank(store.getId())) {
                outputObject.setBeans(new ArrayList<>());
                outputObject.settotal(CommonNumConstants.NUM_ZERO);
                return;
            }
            wrapper.leftJoin(CouponStore.class, CouponStore::getCouponId, Coupon::getId);
            applyCEndStoreVisibility(wrapper, store, storeId);
            wrapper.groupBy(Coupon::getId);
        }
        wrapper.orderByDesc(Coupon::getCreateTime);

        List<Coupon> list = skyeyeBaseMapper.selectJoinList(Coupon.class, wrapper);
        setDrawState(list);
        outputObject.setBeans(list);
        if (commonPageInfo.getIsPaging()) {
            outputObject.settotal(pages.getTotal());
        } else {
            outputObject.settotal(list.size());
        }
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
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Map<String, Object> params = inputObject.getParams();
        String materialId = params.get("materialId").toString();
        String storeId = params.get("storeId").toString();
        String type = commonPageInfo.getType();

        String typeKey = MybatisPlusUtil.toColumns(Coupon::getTemplateId);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        ShopStore store = shopStoreService.selectById(storeId);
        MPJLambdaWrapper<Coupon> wrapper = new MPJLambdaWrapper<Coupon>()
            .innerJoin(CouponMaterial.class, CouponMaterial::getCouponId, Coupon::getId)
            .eq(CouponMaterial::getMaterialId, materialId)
            .eq(MybatisPlusUtil.toColumns(Coupon::getEnabled), EnableEnum.ENABLE_USING.getKey())
            .isNotNull(typeKey).ne(typeKey, StrUtil.EMPTY)
            .leftJoin(CouponStore.class, CouponStore::getCouponId, Coupon::getId);
        applyCEndStoreVisibility(wrapper, store, storeId);
        wrapper.groupBy(Coupon::getId);
        if (StrUtil.isNotEmpty(type)) {
            wrapper.eq(Coupon::getDiscountType, type);
        }
        List<Coupon> list = skyeyeBaseMapper.selectJoinList(Coupon.class, wrapper);
        setDrawState(list);// 设置是否可以领取状态
        outputObject.setBeans(list);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    @IgnoreTenant
    public void queryMaxCouponByMaterialId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String materialId = params.get("materialId").toString();
        String storeId = params.get("storeId").toString();

        String typeKey = MybatisPlusUtil.toColumns(Coupon::getTemplateId);
        ShopStore store = shopStoreService.selectById(storeId);
        MPJLambdaWrapper<Coupon> wrapper = new MPJLambdaWrapper<Coupon>()
            .innerJoin(CouponMaterial.class, CouponMaterial::getCouponId, Coupon::getId)
            .eq(CouponMaterial::getMaterialId, materialId)
            .eq(MybatisPlusUtil.toColumns(Coupon::getEnabled), EnableEnum.ENABLE_USING.getKey())
            .isNotNull(typeKey).ne(typeKey, StrUtil.EMPTY)
            .leftJoin(CouponStore.class, CouponStore::getCouponId, Coupon::getId);
        applyCEndStoreVisibility(wrapper, store, storeId);
        wrapper.groupBy(Coupon::getId);
        List<Coupon> allList = skyeyeBaseMapper.selectJoinList(Coupon.class, wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("maxDiscountPercentCoupon", null);
        result.put("maxDiscountPriceCoupon", null);
        if (CollectionUtil.isNotEmpty(allList)) {
            // 折扣：百分比越小力度越大
            allList.stream()
                .filter(coupon -> Objects.equals(coupon.getDiscountType(), PromotionDiscountType.PERCENT.getKey()))
                .filter(coupon -> ObjectUtil.isNotEmpty(coupon.getDiscountPercent()))
                .min(Comparator.comparing(Coupon::getDiscountPercent))
                .ifPresent(coupon -> result.put("maxDiscountPercentCoupon", coupon));
            // 满减：优惠金额越大力度越大
            allList.stream()
                .filter(coupon -> Objects.equals(coupon.getDiscountType(), PromotionDiscountType.PRICE.getKey()))
                .filter(coupon -> StrUtil.isNotEmpty(coupon.getDiscountPrice()))
                .max(Comparator.comparing(Coupon::getDiscountPrice))
                .ifPresent(coupon -> result.put("maxDiscountPriceCoupon", coupon));
            List<Coupon> maxCouponList = new ArrayList<>();
            if (ObjectUtil.isNotEmpty(result.get("maxDiscountPercentCoupon"))) {
                maxCouponList.add((Coupon) result.get("maxDiscountPercentCoupon"));
            }
            if (ObjectUtil.isNotEmpty(result.get("maxDiscountPriceCoupon"))) {
                maxCouponList.add((Coupon) result.get("maxDiscountPriceCoupon"));
            }
            setDrawState(maxCouponList);// 设置是否可以领取状态
        }
        outputObject.setBean(result);
    }

    /**
     * 分页查询优惠券适用门店。入参：page、limit + customParamsMap.couponId。
     * 全部门店：分页列出启用门店；指定门店：分页列出适用门店。不校验券启用状态。
     * 指定商品（全部门店/指定门店）：按 couponMaterialList 过滤掉没有任何适用商品（已上架）的门店。
     */
    @Override
    @IgnoreTenant
    public void queryCouponApplicableStoreList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String couponId = commonPageInfo.getCustomParamsMapStr("couponId");
        if (StrUtil.isBlank(couponId)) {
            return;
        }
        Coupon coupon = selectById(couponId);
        if (ObjectUtil.isEmpty(coupon)) {
            return;
        }

        boolean allStore = Objects.equals(coupon.getStoreCoverage(), CouponStoreCoverage.ALL_STORE.getKey());
        boolean specifiedMaterial = Objects.equals(coupon.getProductScope(), PromotionMaterialScope.SPU.getKey());
        List<String> storeIdList = null;
        if (specifiedMaterial) {
            // ---------- 指定商品：先定候选门店，再按是否有已上架适用商品过滤 ----------
            if (CollectionUtil.isEmpty(coupon.getCouponMaterialList())) {
                return;
            }
            List<String> materialIdList = coupon.getCouponMaterialList().stream()
                .map(CouponMaterial::getMaterialId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
            if (CollectionUtil.isEmpty(materialIdList)) {
                return;
            }
            // 候选门店：全部门店 = 启用门店；指定门店 = 券关联门店（不与全部门店比对）
            List<String> candidateStoreIdList;
            if (allStore) {
                QueryWrapper<ShopStore> enabledStoreQuery = new QueryWrapper<>();
                enabledStoreQuery.select(CommonConstants.ID)
                    .eq(MybatisPlusUtil.toColumns(ShopStore::getEnabled), EnableEnum.ENABLE_USING.getKey())
                    .eq(MybatisPlusUtil.toColumns(ShopStore::getTenantId),
                        StrUtil.blankToDefault(coupon.getTenantId(), StrUtil.EMPTY))
                    // 「全部门店」不含个人店
                    .ne(MybatisPlusUtil.toColumns(ShopStore::getStoreNature), StoreNature.PERSONAL.getKey());
                candidateStoreIdList = shopStoreService.list(enabledStoreQuery).stream()
                    .map(ShopStore::getId).filter(StrUtil::isNotBlank).collect(Collectors.toList());
            } else {
                candidateStoreIdList = CollectionUtil.isEmpty(coupon.getStoreIdList()) ? Collections.emptyList()
                    : coupon.getStoreIdList().stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
            }
            if (CollectionUtil.isEmpty(candidateStoreIdList)) {
                return;
            }
            // IN 查询商品-门店关系（替代一一对应笛卡尔积），上架判断仍在 shop
            Map<String, Object> materialStoreIdMap = iShopMaterialNormsService
                .queryShopMaterialMapByMaterialIdsAndStoreIds(materialIdList, candidateStoreIdList);
            if (CollectionUtil.isEmpty(materialStoreIdMap)) {
                return;
            }
            List<String> materialStoreIds = materialStoreIdMap.values().stream()
                .map(Object::toString).collect(Collectors.toList());
            List<Map<String, Object>> materialByIds = iShopMaterialNormsService.queryShopMaterialByIds(materialStoreIds);
            if (CollectionUtil.isEmpty(materialByIds)) {
                return;
            }
            storeIdList = materialByIds.stream().map(map -> {
                if (ObjectUtil.isEmpty(map.get("shopMaterialStore"))) {
                    return null;
                }
                // toJsonStr 再转 Map，避免 Map.toString 解析失败
                Map<String, Object> shopMaterialStore = JSONUtil.toBean(JSONUtil.toJsonStr(map.get("shopMaterialStore")), null);
                if (CollectionUtil.isEmpty(shopMaterialStore) || ObjectUtil.isEmpty(shopMaterialStore.get("storeId"))) {
                    return null;
                }
                Integer isLaunchStore = MapUtil.getInt(shopMaterialStore, "isLaunchStore");
                Integer isLaunchShop = MapUtil.getInt(shopMaterialStore, "isLaunchShop");
                Integer storeEnabled = MapUtil.getInt(shopMaterialStore, "storeEnabled");
                // 门店至少有一个适用商品满足：已添加 + 已上架 + 门店启用
                if (Objects.equals(isLaunchStore, WhetherEnum.ENABLE_USING.getKey())
                    && Objects.equals(isLaunchShop, WhetherEnum.ENABLE_USING.getKey())
                    && Objects.equals(storeEnabled, EnableEnum.ENABLE_USING.getKey())) {
                    return shopMaterialStore.get("storeId").toString();
                }
                return null;
            }).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
            if (CollectionUtil.isEmpty(storeIdList)) {
                return;
            }
        } else if (!allStore) {
            // 全部商品 + 指定门店：不按商品过滤，直接用券关联门店
            storeIdList = CollectionUtil.isEmpty(coupon.getStoreIdList()) ? Collections.emptyList()
                : coupon.getStoreIdList().stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
            if (CollectionUtil.isEmpty(storeIdList)) {
                return;
            }
        }

        // 过滤完成后再分页，保证 total 准确
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ShopStore> queryWrapper = new QueryWrapper<>();
        if (specifiedMaterial || !allStore) {
            // 指定商品（已过滤）或指定门店：按门店 id 分页
            queryWrapper.in(CommonConstants.ID, storeIdList);
        } else {
            // 全部商品 + 全部门店：仅本租户启用企业门店（不含个人店）
            queryWrapper.eq(MybatisPlusUtil.toColumns(ShopStore::getEnabled), EnableEnum.ENABLE_USING.getKey())
                .eq(MybatisPlusUtil.toColumns(ShopStore::getTenantId),
                    StrUtil.blankToDefault(coupon.getTenantId(), StrUtil.EMPTY))
                .ne(MybatisPlusUtil.toColumns(ShopStore::getStoreNature), StoreNature.PERSONAL.getKey());
        }
        List<ShopStore> stores = shopStoreService.list(queryWrapper);
        outputObject.setBeans(stores);
        outputObject.settotal(pages.getTotal());
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
    @IgnoreTenant
    public void deleteCouponById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        Object storeIdObj = params.get("storeId");
        String storeId = storeIdObj == null ? StrUtil.EMPTY : storeIdObj.toString();
        String ids = params.get("ids").toString();
        if (StrUtil.isNotBlank(storeId)) {
            ShopStore store = assertStoreCouponAccess(storeId);
            for (String id : ids.split(CommonCharConstants.COMMA_MARK)) {
                if (StrUtil.isNotBlank(id)) {
                    assertStoreOwnedCoupon(id.trim(), store);
                }
            }
        }
        deleteByIds(inputObject, outputObject);
    }

    @Override
    @IgnoreTenant
    public void changeCouponEnabled(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Integer enabled = Integer.valueOf(params.get("enabled").toString());
        Object storeIdObj = params.get("storeId");
        String storeId = storeIdObj == null ? StrUtil.EMPTY : storeIdObj.toString();
        if (StrUtil.isNotBlank(storeId)) {
            ShopStore store = assertStoreCouponAccess(storeId);
            assertStoreOwnedCoupon(id, store);
        } else {
            Coupon coupon = selectById(id);
            if (ObjectUtil.isEmpty(coupon) || StrUtil.isBlank(coupon.getId())) {
                throw new CustomException("优惠券不存在");
            }
            if (Objects.equals(coupon.getCouponSource(), CouponSource.STORE.getKey())) {
                throw new CustomException("不能操作门店优惠券");
            }
        }
        if (!Objects.equals(enabled, EnableEnum.ENABLE_USING.getKey())
            && !Objects.equals(enabled, EnableEnum.DISABLE_USING.getKey())) {
            throw new CustomException("状态不正确");
        }
        UpdateWrapper<Coupon> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Coupon::getEnabled), enabled);
        update(updateWrapper);
        refreshCache(id);
        outputObject.setBean(selectById(id));
    }

    /**
     * 个人店强制指定本店；企业店可全部门店（仅本租户）或指定门店。
     */
    private void applyStoreCoverageRule(Coupon coupon, ShopStore store, String storeId) {
        if (StoreNature.PERSONAL.getKey().equals(store.getStoreNature())) {
            coupon.setStoreCoverage(CouponStoreCoverage.SPECIFIED_STORE.getKey());
            coupon.setStoreIdList(Collections.singletonList(storeId));
            return;
        }
        if (coupon.getStoreCoverage() == null) {
            coupon.setStoreCoverage(CouponStoreCoverage.SPECIFIED_STORE.getKey());
        }
        if (Objects.equals(coupon.getStoreCoverage(), CouponStoreCoverage.ALL_STORE.getKey())) {
            coupon.setStoreIdList(null);
            return;
        }
        coupon.setStoreCoverage(CouponStoreCoverage.SPECIFIED_STORE.getKey());
        if (CollectionUtil.isEmpty(coupon.getStoreIdList())) {
            coupon.setStoreIdList(Collections.singletonList(storeId));
        }
    }

    /**
     * C 端按门店可见性：个人店仅本店指定券；企业店=本租户全店券 + 绑定本店的指定券。
     * 全店券不可被个人店或其他租户领取。
     * 指定门店同时认 CouponStore 与 Coupon.storeId（门店自建券）。
     */
    private void applyCEndStoreVisibility(MPJLambdaWrapper<Coupon> wrapper, ShopStore store, String storeId) {
        if (store == null || StrUtil.isBlank(store.getId())
            || StoreNature.PERSONAL.getKey().equals(store.getStoreNature())) {
            wrapper.eq(Coupon::getStoreCoverage, CouponStoreCoverage.SPECIFIED_STORE.getKey())
                .and(w -> w.eq(CouponStore::getStoreId, storeId).or().eq(Coupon::getStoreId, storeId));
            return;
        }
        String storeTenantId = StrUtil.blankToDefault(store.getTenantId(), StrUtil.EMPTY);
        wrapper.and(w -> w.and(wAll -> wAll.eq(Coupon::getStoreCoverage, CouponStoreCoverage.ALL_STORE.getKey())
                .eq(Coupon::getTenantId, storeTenantId))
            .or(w2 -> w2.eq(Coupon::getStoreCoverage, CouponStoreCoverage.SPECIFIED_STORE.getKey())
                .and(wBind -> wBind.eq(CouponStore::getStoreId, storeId).or().eq(Coupon::getStoreId, storeId))));
    }

    /**
     * 门店优惠券操作权限：店主 / 本店员工 / 加盟企业店（入口靠菜单）。对齐资金 assertStoreFundAccess。
     */
    private ShopStore assertStoreCouponAccess(String storeId) {
        if (StrUtil.isBlank(storeId)) {
            throw new CustomException("请选择门店");
        }
        ShopStore store = shopStoreService.selectById(storeId);
        if (store == null || StrUtil.isBlank(store.getId())) {
            throw new CustomException("门店不存在");
        }
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        if (userId.equals(store.getCreateId())) {
            return store;
        }
        Object staffIdObj = InputObject.getLogParamsStatic().get("staffId");
        String staffId = staffIdObj == null ? StrUtil.EMPTY : staffIdObj.toString();
        if (StrUtil.isNotBlank(staffId) && !"tmpUserStaffId".equals(staffId) && isStoreStaff(storeId, staffId)) {
            return store;
        }
        if (!StoreNature.PERSONAL.getKey().equals(store.getStoreNature())) {
            return store;
        }
        throw new CustomException("无权操作该门店");
    }

    private boolean isStoreStaff(String storeId, String staffId) {
        List<ShopStoreStaff> staffList = shopStoreStaffService.getShopStoresByStoreId(storeId);
        if (CollectionUtil.isEmpty(staffList)) {
            return false;
        }
        return staffList.stream().anyMatch(s -> staffId.equals(s.getStaffId()));
    }

    private void assertCouponBelongStore(String couponId, String storeId) {
        List<CouponStore> couponStoreList = couponStoreService.queryListByCouponId(couponId);
        boolean matched = CollectionUtil.isNotEmpty(couponStoreList)
            && couponStoreList.stream().anyMatch(item -> storeId.equals(item.getStoreId()));
        if (!matched) {
            throw new CustomException("优惠券不属于当前门店");
        }
    }

    /**
     * 校验为门店来源，且归属当前门店。
     */
    private void assertStoreOwnedCoupon(String couponId, ShopStore store) {
        Coupon coupon = selectById(couponId);
        if (ObjectUtil.isEmpty(coupon) || StrUtil.isBlank(coupon.getId())) {
            throw new CustomException("优惠券不存在");
        }
        if (!Objects.equals(coupon.getCouponSource(), CouponSource.STORE.getKey())) {
            throw new CustomException("不能操作管理端优惠券");
        }
        if (!StrUtil.equals(store.getId(), coupon.getStoreId())) {
            throw new CustomException("优惠券不属于当前门店");
        }
    }

    private void assertStoreOwnedTemplate(String templateId, ShopStore store) {
        Coupon template = selectById(templateId);
        if (ObjectUtil.isEmpty(template) || StrUtil.isBlank(template.getId())) {
            throw new CustomException("优惠券模板不存在");
        }
        if (StrUtil.isNotBlank(template.getTemplateId())) {
            throw new CustomException("请选择优惠券模板");
        }
        if (!Objects.equals(template.getCouponSource(), CouponSource.STORE.getKey())) {
            throw new CustomException("不能使用管理端模板，请先自建模板");
        }
        if (!StrUtil.equals(store.getId(), template.getStoreId())) {
            throw new CustomException("模板不属于当前门店");
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
