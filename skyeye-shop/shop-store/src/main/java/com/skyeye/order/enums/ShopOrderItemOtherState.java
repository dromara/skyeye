package com.skyeye.order.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ShopOrderItemDeliverState
 * @Description: 订单子单据发货状态枚举
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ShopOrderItemOtherState implements SkyeyeEnumClass {

    WAIT_PAY(0, "待支付", true, true),
    FAIRPAID(1, "支付失败", true, false),
    CANCELED(2, "已取消", true, false),
    WAIT_DELIVER(3, "待发货", true, false),
    ALL_DELIVERED(4, "全部发货", true, false),
    TRANSPORTING(5, "运输中", true, false),
    SIGN(6, "已签收", true, false),
    COMPLETED(7, "已完成", true, false),
    UNEVALUATE(8, "待评价", true, false),
    EVALUATED(9, "已评价", true, false),
    REFUNDING(10, "退款中", true, false),
    REFUND(11, "已退款", true, false),
    SALESRETURNING(12, "退货中", true, false),
    SALESRETURNED(13, "已退货", true, false),
    EXCHANGEING(14, "换货中", true, false),
    EXCHANGED(15, "已换货", true, false),
    PARTIALLYDONE(16, "部分完成", true, false),
    PARTIALEVALUATION(17, "部分评价", true, false),
    PART_DELIVERED(18, "部分发货", true, false),
    PART_SIGN(19, "部分签收", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
