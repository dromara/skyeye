package com.skyeye.order.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 商城商品售后单状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum OrderAfterSaleStatus implements SkyeyeEnumClass {

    WAIT_MERCHANT(1, "待商家处理", true, true),
    WAIT_BUYER_RETURN(2, "待买家退货", true, false),
    WAIT_MERCHANT_RECEIVE(3, "待商家收货", true, false),
    REFUNDING(4, "退款中", true, false),
    DONE(5, "已完成", true, false),
    REJECTED(6, "已拒绝", true, false),
    CANCELLED(7, "已取消", true, false);

    private Integer key;
    private String value;
    private Boolean show;
    private Boolean isDefault;
}
