/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.finance.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 个人门店资金流水方向
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ShopStoreLedgerDirection implements SkyeyeEnumClass {

    IN_AVAILABLE(1, "增加可用", true, true),
    OUT_AVAILABLE(2, "减少可用", true, false),
    FREEZE(3, "可用转冻结", true, false),
    UNFREEZE(4, "冻结转可用", true, false),
    FREEZE_TO_WITHDRAW(5, "冻结转已提现", true, false),
    /** 收货冻结：直接增加冻结余额 */
    IN_FROZEN(6, "增加冻结", true, false);

    private Integer key;
    private String value;
    private Boolean show;
    private Boolean isDefault;
}
