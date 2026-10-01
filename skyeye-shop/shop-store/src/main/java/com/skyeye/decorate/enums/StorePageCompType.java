/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.decorate.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 门店装修组件类型
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum StorePageCompType implements SkyeyeEnumClass {

    STORE_HEADER(1, "店头", true, true),
    BANNER(2, "轮播", true, false),
    NOTICE(3, "公告", true, false),
    COUPON(4, "优惠券", true, false),
    RECOMMEND(5, "店长推荐", true, false),
    GOODS(6, "商品区", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
