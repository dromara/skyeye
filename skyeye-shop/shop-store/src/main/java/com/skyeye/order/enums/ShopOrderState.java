/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ShopOrderState
 * @Description: 订单支付状态
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:33
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ShopOrderState implements SkyeyeEnumClass {

    UNPAID(1, "待支付", true, true),
    FAIRPAID(2, "支付失败", true, false),
    PAY_SUCCESS(3, "支付成功", true, false),
    PARTIAL_PAID(4, "部分支付", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
