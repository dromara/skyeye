/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.constans.SysUserAuthConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.coupon.dao.CouponUseDao;
import com.skyeye.coupon.entity.Coupon;
import com.skyeye.coupon.entity.CouponMaterial;
import com.skyeye.coupon.entity.CouponUse;
import com.skyeye.coupon.entity.CouponUseMaterial;
import com.skyeye.coupon.enums.CouponTakeType;
import com.skyeye.coupon.enums.CouponUseState;
import com.skyeye.coupon.enums.CouponValidityType;
import com.skyeye.coupon.enums.PromotionDiscountType;
import com.skyeye.coupon.service.CouponService;
import com.skyeye.coupon.service.CouponUseMaterialService;
import com.skyeye.coupon.service.CouponUseService;
import com.skyeye.entity.Member;
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.exception.CustomException;
import com.skyeye.service.MemberService;
import com.skyeye.xxljob.ShopXxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: CouponUseServiceImpl
 * @Description: 优惠券领取信息管理服务层--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/23 10:43
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "优惠券领取信息管理", groupName = "优惠券领取信息管理", tenant = TenantEnum.NO_ISOLATION)
public class CouponUseServiceImpl extends SkyeyeBusinessServiceImpl<CouponUseDao, CouponUse> implements CouponUseService {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponUseMaterialService couponUseMaterialService;

    @Autowired
    private IQuartzService iQuartzService;

    @Autowired
    private MemberService memberService;

    private static Logger log = LoggerFactory.getLogger(ShopXxlJob.class);

    private void check(Coupon coupon) {
        if (ObjectUtil.isEmpty(coupon)) {
            throw new CustomException("优惠券不存在");
        }
        if (coupon.getTakeType() == CouponTakeType.REGISTER.getKey()) {
            String currentUserId = InputObject.getLogParamsStatic().get("id").toString();
            Member member = memberService.selectById(currentUserId);
            int distanceDay = DateUtil.getDistanceDay(member.getCreateTime(), DateUtil.getTimeAndToString());
            if (distanceDay > 30) {
                throw new CustomException("您已经不是新用户，无法领取新人券");
            }
        }
        if (Objects.equals(coupon.getEnabled(), WhetherEnum.DISABLE_USING.getKey())) {
            throw new CustomException("优惠券已过期");
        }
        if (coupon.getTotalCount() != -1) {
            // 优惠券数量限制, -1表示不限制, 其他正数表示数量限制
            if (coupon.getTakeCount() >= coupon.getTotalCount()) {
                throw new CustomException("优惠券已被领完.");
            }
        }
        // 领取限制, -1表示不限制
        if (coupon.getTakeLimitCount() == -1) {
            return;
        }
        // 个人领取该优惠券的数量限制查询
        QueryWrapper<CouponUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCouponId), coupon.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        if (count(queryWrapper) >= coupon.getTakeLimitCount()) {
            throw new CustomException("超出领取数量限制");
        }
    }

    @Override
    public void createPrepose(CouponUse couponUse) {
        Coupon coupon = couponService.selectById(couponUse.getCouponId());
        couponUse.setUsageCount(coupon.getUseCount());
        check(coupon);
        // 设置适用对象
        List<CouponUseMaterial> couponUseMaterialList = couponUse.getCouponUseMaterialList();
        for (CouponMaterial couponMaterial : coupon.getCouponMaterialList()) {
            CouponUseMaterial couponUseMaterial = new CouponUseMaterial();
            couponUseMaterial.setCouponId(coupon.getId());
            couponUseMaterial.setMaterialId(couponMaterial.getMaterialId());
            couponUseMaterialList.add(couponUseMaterial);
        }
        // 设置领取方式
        couponUse.setTaskType(coupon.getTakeType());
        // 状态
        couponUse.setState(CouponUseState.UNUSED.getKey());
        //满减
        couponUse.setUsePrice(coupon.getUsePrice());
        //使用范围
        couponUse.setProductScope(coupon.getProductScope());
        //生效时间
        if (Objects.equals(CouponValidityType.DATE.getKey(), coupon.getValidityType())) {
            couponUse.setValidStartTime(coupon.getValidStartTime());
            couponUse.setValidEndTime(coupon.getValidEndTime());
        } else {
            DateFormat df = new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS);
            // 计算开始生效时间
            Date validStartTime = DateUtil.getAfDate(DateUtil.getPointTime(DateUtil.getTimeAndToString(), DateUtil.YYYY_MM_DD_HH_MM_SS), coupon.getFixedStartTime(), "d");
            // 在开始生效时间基础上加上fixedEndTime天数，得到结束时间
            Date validEndTime = DateUtil.getAfDate(validStartTime, coupon.getFixedEndTime(), "d");
            // 设置优惠券的开始和结束时间
            couponUse.setValidStartTime(df.format(validStartTime));
            couponUse.setValidEndTime(df.format(validEndTime));
        }
        // 领取非固定类型优惠券时，借助couponMation成员变量存储优惠券信息，便于后置执行新增定时任务
        couponUse.setCouponMation(coupon);
        //折扣类型
        couponUse.setDiscountType(coupon.getDiscountType());
        //折扣值
        if (Objects.equals(PromotionDiscountType.PERCENT.getKey(), coupon.getDiscountType())) {
            couponUse.setDiscountPercent(coupon.getDiscountPercent());
        } else {
            couponUse.setDiscountPrice(coupon.getDiscountPrice());
        }
        //折扣上限
        couponUse.setDiscountLimitPrice(coupon.getDiscountLimitPrice());
    }

    @Override
    public void createPostpose(CouponUse couponUse, String userId) {
        // 更新优惠券领取数量
        couponService.updateTakeCount(couponUse.getCouponId(), couponUse.getCouponMation().getTakeCount() + 1);
        // 新增优惠券可使用的商品信息
        couponUseMaterialService.createEntity(couponUse.getCouponUseMaterialList(), userId);
        // 定时任务
        Coupon couponMation = couponUse.getCouponMation();
        if (Objects.equals(couponMation.getValidityType(), CouponValidityType.TERM.getKey())) {
            log.info("领取优惠券的id(couponUseId)" + couponUse.getId() + "创建定时任务--开始");
            startUpTaskQuartz(couponUse.getId(), couponMation.getName(), couponUse.getValidEndTime());
            log.info("领取优惠券的id(couponUseId)" + couponUse.getId() + "创建定时任务--结束");
        }
    }

    private void startUpTaskQuartz(String name, String title, String delayedTime) {
        SysQuartzMation sysQuartzMation = new SysQuartzMation();
        sysQuartzMation.setName(name);
        sysQuartzMation.setTitle(title);
        sysQuartzMation.setDelayedTime(delayedTime);
        sysQuartzMation.setGroupId(QuartzConstants.QuartzMateMationJobType.SHOP_COUPON_USE.getTaskType());
        iQuartzService.startUpTaskQuartz(sysQuartzMation);
    }

    @Override
    public void writePostpose(CouponUse couponUse, String userId) {
        if (ObjectUtil.isNotEmpty(couponUse.getCouponUseMaterialList())) {
            couponUse.getCouponUseMaterialList().forEach(couponMaterial -> couponMaterial.setCouponId(couponUse.getId()));
            couponUseMaterialService.createEntity(couponUse.getCouponUseMaterialList(), userId);
        }
    }

    @Override
    public void updatePrepose(CouponUse couponUse) {
        if (StrUtil.isNotEmpty(couponUse.getUseOrderId())) {
            couponUse.setUseTime(DateUtil.getTimeAndToString());
            couponUse.setState(CouponUseState.USED.getKey());
        }
    }

    @Override
    public void updatePostpose(CouponUse couponUse, String userId) {
        QueryWrapper<CouponUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCouponId), couponUse.getCouponId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getState), CouponUseState.USED.getKey());
        Coupon coupon = couponService.selectById(couponUse.getCouponId());
        if (ObjectUtil.isNotEmpty(coupon) && Objects.equals(coupon.getValidityType(), CouponValidityType.TERM.getKey())) {
            iQuartzService.stopAndDeleteTaskQuartz(couponUse.getId());// 删除任务
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> mapList = super.queryPageDataList(inputObject);
        couponService.setMationForMap(mapList, "couponId", "couponMation");
        return mapList;
    }

    @Override
    public void queryMyCouponUseByState(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String couponId = commonPageInfo.getCustomParamsMapStr("couponId");
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<CouponUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCreateId), inputObject.getLogParams().get("id").toString());
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getState), commonPageInfo.getState());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getType())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getDiscountType), commonPageInfo.getType());
        }
        if (StrUtil.isNotEmpty(couponId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCouponId), couponId);
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(CouponUse::getCreateTime));
        List<CouponUse> list = list(queryWrapper);
        couponService.setDataMation(list, CouponUse::getCouponId);
        outputObject.setBeans(list);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public Map<String, Integer> queryIdTotalMapByCouponId(List<String> couponIdList) {
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if (StrUtil.isEmpty(userToken)) {
            return new HashMap<>();
        }
        String userTokenUserId = GetUserToken.getUserTokenUserId(InputObject.getRequest());
        Boolean aBoolean = SysUserAuthConstants.exitUserLoginRedisCache(userTokenUserId);
        if (!aBoolean) {
            return new HashMap<>();
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<CouponUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(MybatisPlusUtil.toColumns(CouponUse::getCouponId), "count(id) as total");
        queryWrapper.in(MybatisPlusUtil.toColumns(CouponUse::getCouponId), couponIdList);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCreateId), userId);
        queryWrapper.groupBy(MybatisPlusUtil.toColumns(CouponUse::getCouponId));
        List<Map<String, Object>> mapList = listMaps(queryWrapper);
        return CollectionUtil.isEmpty(mapList) ? new HashMap<>()
            : mapList.stream().collect(Collectors.toMap(map -> map.get("coupon_id").toString(), map -> Integer.parseInt(map.get("total").toString())));
    }

    /**
     * xxlJob任务管理器定时修改过期优惠券的状态
     */
    @Override
    public void setCouponUseStateByDate(String couponId) {
        UpdateWrapper<CouponUse> updateWrapper = new UpdateWrapper<>();
        // 取优未使用的优惠券
        updateWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getState), CouponUseState.UNUSED.getKey());
        updateWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCouponId), couponId);
        // 更改状态为过期
        updateWrapper.set(MybatisPlusUtil.toColumns(CouponUse::getState), CouponUseState.EXPIRE.getKey());
        update(updateWrapper);
    }

    @Override
    public void setCouponUseStateByTerm(String userId, String couponUseId) {
        UpdateWrapper<CouponUse> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, couponUseId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCreateId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(CouponUse::getState), CouponUseState.EXPIRE.getKey());
        update(updateWrapper);
    }

    @Override
    public void updateState(String couponUseId) {
        CouponUse couponUse = selectById(couponUseId);
        if (ObjUtil.isEmpty(couponUse)) {
            throw new CustomException("优惠券使用记录不存在");
        }
        if (couponUse.getUsedCount() == couponUse.getUsageCount()) {
            UpdateWrapper<CouponUse> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, couponUseId);
            updateWrapper.set(MybatisPlusUtil.toColumns(CouponUse::getState), CouponUseState.USED.getKey());
            update(updateWrapper);
        }
    }

    @Override
    public void UpdateUsedCount(String couponUseId) {
        CouponUse couponUse = selectById(couponUseId);
        if (ObjUtil.isEmpty(couponUse)) {
            throw new CustomException("优惠券使用记录不存在");
        }
        Integer usedCount = couponUse.getUsedCount();
        if (couponUse.getUsedCount() < couponUse.getUsageCount()) {
            UpdateWrapper<CouponUse> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, couponUseId);
            updateWrapper.set(MybatisPlusUtil.toColumns(CouponUse::getUsedCount), usedCount + 1);
            update(updateWrapper);
        } else {
            throw new CustomException("优惠券使用次数已达到上限");
        }
    }

    @Override
    public void deleteByCouponIds(List<String> ids) {
        QueryWrapper<CouponUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(CouponUse::getCouponId), ids);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getState), CouponUseState.UNUSED.getKey());
        remove(queryWrapper);
    }

    @Override
    public List<CouponUse> queryUnUseByCouponIdList(List<String> termCouponIds) {
        if (CollectionUtil.isEmpty(termCouponIds)) {
            return new ArrayList<>();
        }
        QueryWrapper<CouponUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(CouponUse::getCouponId), termCouponIds)
                .eq(MybatisPlusUtil.toColumns(CouponUse::getState), CouponUseState.UNUSED.getKey());
        return list(queryWrapper);
    }
}