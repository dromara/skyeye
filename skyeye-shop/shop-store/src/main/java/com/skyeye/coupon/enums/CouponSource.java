/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 优惠券/模板来源：管理端 vs 门店工作台
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CouponSource implements SkyeyeEnumClass {

    PLATFORM(1, "管理端", true, true),
    STORE(2, "门店", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
