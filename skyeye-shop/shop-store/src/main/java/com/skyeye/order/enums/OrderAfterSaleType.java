package com.skyeye.order.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 商城商品售后类型
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum OrderAfterSaleType implements SkyeyeEnumClass {

    REFUND_ONLY(1, "仅退款", true, true),
    RETURN_REFUND(2, "退货退款", true, false),
    EXCHANGE(3, "换货", true, false);

    private Integer key;
    private String value;
    private Boolean show;
    private Boolean isDefault;
}
