/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: CouponJumpType
 * @Description: 优惠券去使用跳转类型
 * @author: skyeye云系列--卫志强
 * @date: 2026/7/16 11:40
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CouponJumpType implements SkyeyeEnumClass {

    PRODUCT_LIST(1, "商品列表", true, false),
    STORE_LIST(2, "门店列表", true, false),
    STORE_HOME(3, "进入门店", true, false),
    DETAIL(4, "商品详情", true, false),
    HOME(5, "商城首页", true, true);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
