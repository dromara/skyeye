/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.finance.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 门店订单资金到账状态（对账/流水展示）
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ShopStoreSettleStatus implements SkyeyeEnumClass {

    NONE(0, "未入账", "default", true, true),
    HOLDING(1, "结算中", "orange", true, false),
    ARRIVED(2, "已到账", "green", true, false);

    private Integer key;
    private String value;
    private String color;
    private Boolean show;
    private Boolean isDefault;
}
