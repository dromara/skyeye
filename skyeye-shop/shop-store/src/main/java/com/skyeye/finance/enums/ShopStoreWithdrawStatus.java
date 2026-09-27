/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.finance.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 门店提现状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ShopStoreWithdrawStatus implements SkyeyeEnumClass {

    PENDING(0, "待审核", "blue", true, true),
    APPROVED(1, "打款成功", "green", true, false),
    REJECTED(2, "已拒绝", "red", true, false),
    CANCELLED(3, "已取消", "gray", true, false),
    /** 审核通过，支付中心打款处理中 */
    TRANSFERRING(4, "打款中", "orange", true, false),
    /** 打款失败，冻结保留，可重试或驳回解冻 */
    TRANSFER_FAILED(5, "打款失败", "red", true, false);

    private Integer key;
    private String value;
    private String color;
    private Boolean show;
    private Boolean isDefault;
}
