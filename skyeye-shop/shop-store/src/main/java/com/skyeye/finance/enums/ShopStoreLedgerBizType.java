/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.finance.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 个人门店资金流水业务类型
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ShopStoreLedgerBizType implements SkyeyeEnumClass {

    ORDER_IN(1, "订单入账", "green", true, true),
    REFUND_OUT(2, "退款出账", "red", true, false),
    WITHDRAW_FREEZE(3, "提现冻结", "blue", true, false),
    WITHDRAW_UNFREEZE(4, "提现解冻", "orange", true, false),
    WITHDRAW_DONE(5, "提现完成", "default", true, false),
    /** 确认收货后进入结算冻结，期满前不可提现 */
    ORDER_HOLD(6, "收货冻结", "blue", true, false),
    /** 结算期满：冻结转入可提现 */
    ORDER_SETTLE(7, "结算可提现", "green", true, false);

    private Integer key;
    private String value;
    private String color;
    private Boolean show;
    private Boolean isDefault;
}
