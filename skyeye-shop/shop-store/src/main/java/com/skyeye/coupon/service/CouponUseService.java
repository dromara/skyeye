/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.coupon.entity.CouponUse;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CouponUseService
 * @Description: 优惠券领取信息管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/23 10:44
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CouponUseService extends SkyeyeBusinessService<CouponUse> {

    /**
     * 根据状态分页查询自己的优惠券领取信息
     */
    void queryMyCouponUseByState(InputObject inputObject, OutputObject outputObject);

    Map<String, Integer> queryIdTotalMapByCouponId(List<String> couponIdList);

    void setCouponUseStateByDate(String surveyId);

    void setCouponUseStateByTerm(String userId, String couponUseId);

    void updateState(String couponUseId);

    void UpdateUsedCount(String couponUseId);

    void deleteByCouponIds(List<String> ids);

    List<CouponUse> queryUnUseByCouponIdList(List<String> termCouponIds);
}
