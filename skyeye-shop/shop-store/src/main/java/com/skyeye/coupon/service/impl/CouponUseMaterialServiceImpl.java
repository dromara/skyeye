/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.coupon.dao.CouponUseMaterialDao;
import com.skyeye.coupon.entity.CouponUseMaterial;
import com.skyeye.coupon.service.CouponUseMaterialService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: CouponUseMaterialServiceImpl
 * @Description: 用户领取的优惠券适用商品对象管理--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用户领取的优惠券适用商品对象管理", groupName = "用户领取的优惠券适用商品对象管理", manageShow = false, tenant = TenantEnum.NO_ISOLATION)
public class CouponUseMaterialServiceImpl extends SkyeyeBusinessServiceImpl<CouponUseMaterialDao, CouponUseMaterial> implements CouponUseMaterialService {
    @Override
    public List<CouponUseMaterial> queryListByCouponIds(List<String> couponIds) {
        QueryWrapper<CouponUseMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(CouponUseMaterial::getCouponId), couponIds);
        return list(queryWrapper);
    }
}
