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
import com.google.common.base.Joiner;
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
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.coupon.dao.CouponDao;
import com.skyeye.coupon.entity.Coupon;
import com.skyeye.coupon.entity.CouponMaterial;
import com.skyeye.coupon.entity.CouponStore;
import com.skyeye.coupon.entity.CouponUse;
import com.skyeye.coupon.enums.CouponJumpType;
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
     * 获取优惠券「去使用」跳转信息。
     * 根据商品范围、门店范围及关联数量，返回 jumpType 及后续接口所需参数。
     * jumpType：1商品列表 2门店列表 3进入门店 4商品详情 5商城首页
     */
    @Override
    @IgnoreTenant
    public void queryCouponJumpInfo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String couponId = params.get("couponId").toString();
        String currentStoreId = params.get("storeId") == null ? StrUtil.EMPTY : params.get("storeId").toString();

        Coupon coupon = selectById(couponId);
        if (ObjectUtil.isEmpty(coupon)) {
            throw new CustomException("优惠券不存在");
        }
        if (!Objects.equals(coupon.getEnabled(), EnableEnum.ENABLE_USING.getKey())) {
            throw new CustomException("优惠券已失效");
        }
        // 缓存券对象可能缺少适用商品，补查一次
        if (CollectionUtil.isEmpty(coupon.getCouponMaterialList())) {
            coupon.setCouponMaterialList(couponMaterialService.queryListByCouponId(couponId));
        }

        List<String> materialIdList = resolveMaterialIdList(coupon);
        List<String> storeIdList = resolveStoreIdList(coupon);
        boolean specifyMaterial = Objects.equals(coupon.getProductScope(), PromotionMaterialScope.SPU.getKey());
        boolean specifyStore = Objects.equals(coupon.getStoreCoverage(), CouponStoreCoverage.SPECIFIED_STORE.getKey());

        Map<String, Object> jumpInfo;
        if (specifyMaterial && !specifyStore) {
            // 仅指定商品 → 商品列表
            if (CollectionUtil.isEmpty(materialIdList)) {
                jumpInfo = buildHomeJump("优惠券未配置适用商品");
            } else {
                jumpInfo = buildJumpInfo(CouponJumpType.PRODUCT_LIST, joinIds(materialIdList), StrUtil.EMPTY,
                    StrUtil.EMPTY, StrUtil.EMPTY, StrUtil.EMPTY);
            }
        } else if (!specifyMaterial && specifyStore) {
            // 仅指定门店
            if (CollectionUtil.isEmpty(storeIdList)) {
                jumpInfo = buildHomeJump("优惠券未配置适用门店");
            } else if (storeIdList.size() == CommonNumConstants.NUM_ONE) {
                jumpInfo = buildJumpInfo(CouponJumpType.STORE_HOME, StrUtil.EMPTY, joinIds(storeIdList),
                    StrUtil.EMPTY, storeIdList.get(CommonNumConstants.NUM_ZERO), StrUtil.EMPTY);
            } else {
                jumpInfo = buildJumpInfo(CouponJumpType.STORE_LIST, StrUtil.EMPTY, joinIds(storeIdList),
                    StrUtil.EMPTY, StrUtil.EMPTY, StrUtil.EMPTY);
            }
        } else if (specifyMaterial && specifyStore) {
            if (CollectionUtil.isEmpty(materialIdList) || CollectionUtil.isEmpty(storeIdList)) {
                jumpInfo = buildHomeJump("优惠券适用商品或门店配置不完整");
            } else if (materialIdList.size() == CommonNumConstants.NUM_ONE
                && storeIdList.size() == CommonNumConstants.NUM_ONE) {
                // 单商品 + 单门店 → 详情
                jumpInfo = buildDetailJump(materialIdList.get(CommonNumConstants.NUM_ZERO),
                    storeIdList.get(CommonNumConstants.NUM_ZERO), materialIdList, storeIdList);
            } else if (materialIdList.size() == CommonNumConstants.NUM_ONE
                && storeIdList.size() > CommonNumConstants.NUM_ONE
                && StrUtil.isNotBlank(currentStoreId)
                && storeIdList.contains(currentStoreId)) {
                // 单商品 + 多门店，且当前门店在适用范围内 → 优先详情
                jumpInfo = buildDetailJump(materialIdList.get(CommonNumConstants.NUM_ZERO),
                    currentStoreId, materialIdList, storeIdList);
            } else {
                // 多商品/多门店 → 列表
                jumpInfo = buildJumpInfo(CouponJumpType.PRODUCT_LIST, joinIds(materialIdList), joinIds(storeIdList),
                    StrUtil.EMPTY, StrUtil.EMPTY, StrUtil.EMPTY);
            }
        } else {
            // 全场券等兜底
            jumpInfo = buildHomeJump("该优惠券适用于全场，请前往商城首页");
        }

        outputObject.setBean(jumpInfo);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 解析单商品+单门店的详情跳转：查询 erp_shop_material_store.id。
     * 查不到或异常时降级为商品列表。
     *
     * @param materialId 商品id
     * @param storeId 门店id
     * @param materialIdList 适用商品id列表（回填到返回结果）
     * @param storeIdList 适用门店id列表（回填到返回结果）
     */
    private Map<String, Object> buildDetailJump(String materialId, String storeId,
                                                List<String> materialIdList, List<String> storeIdList) {
        try {
            Map<String, Object> shopMaterialStore =
                iShopMaterialNormsService.queryShopMaterialByMaterialIdAndStoreId(materialId, storeId);
            if (ObjectUtil.isNotEmpty(shopMaterialStore) && shopMaterialStore.get(CommonConstants.ID) != null
                && StrUtil.isNotBlank(shopMaterialStore.get(CommonConstants.ID).toString())) {
                return buildJumpInfo(CouponJumpType.DETAIL, joinIds(materialIdList), joinIds(storeIdList),
                    shopMaterialStore.get(CommonConstants.ID).toString(), storeId, StrUtil.EMPTY);
            }
        } catch (Exception e) {
            log.warn("解析门店商品关系失败, materialId={}, storeId={}", materialId, storeId, e);
        }
        // 未上架等降级为商品列表
        return buildJumpInfo(CouponJumpType.PRODUCT_LIST, joinIds(materialIdList), joinIds(storeIdList),
            StrUtil.EMPTY, StrUtil.EMPTY, "未找到可跳转的商品详情，已降级为商品列表");
    }

    /**
     * 构建跳转商城首页的结果
     *
     * @param message 提示信息
     */
    private Map<String, Object> buildHomeJump(String message) {
        return buildJumpInfo(CouponJumpType.HOME, StrUtil.EMPTY, StrUtil.EMPTY,
            StrUtil.EMPTY, StrUtil.EMPTY, message);
    }

    /**
     * 组装跳转结果
     *
     * @param jumpType 跳转类型，参考#CouponJumpType
     * @param materialIds 商品id，多个逗号隔开
     * @param storeIds 门店id，多个逗号隔开
     * @param shopMaterialStoreId 门店商品关系主键（详情跳转用）
     * @param storeId 单门店场景下的门店id
     * @param message 提示信息
     */
    private Map<String, Object> buildJumpInfo(CouponJumpType jumpType, String materialIds, String storeIds,
                                              String shopMaterialStoreId, String storeId, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("jumpType", jumpType.getKey());
        result.put("materialIds", materialIds);
        result.put("storeIds", storeIds);
        result.put("shopMaterialStoreId", shopMaterialStoreId);
        result.put("storeId", storeId);
        result.put("message", message);
        return result;
    }

    /**
     * 解析优惠券适用商品id列表（仅指定商品范围时返回）
     */
    private List<String> resolveMaterialIdList(Coupon coupon) {
        if (!Objects.equals(coupon.getProductScope(), PromotionMaterialScope.SPU.getKey())
            || CollectionUtil.isEmpty(coupon.getCouponMaterialList())) {
            return Collections.emptyList();
        }
        return coupon.getCouponMaterialList().stream()
            .map(CouponMaterial::getMaterialId)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * 解析优惠券适用门店id列表（仅指定门店范围时返回）
     */
    private List<String> resolveStoreIdList(Coupon coupon) {
        if (!Objects.equals(coupon.getStoreCoverage(), CouponStoreCoverage.SPECIFIED_STORE.getKey())
            || CollectionUtil.isEmpty(coupon.getStoreIdList())) {
            return Collections.emptyList();
        }
        return coupon.getStoreIdList().stream()
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * 将id列表拼接为逗号分隔字符串
     */
    private String joinIds(List<String> idList) {
        if (CollectionUtil.isEmpty(idList)) {
            return StrUtil.EMPTY;
        }
        return Joiner.on(CommonCharConstants.COMMA_MARK).join(idList);
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
