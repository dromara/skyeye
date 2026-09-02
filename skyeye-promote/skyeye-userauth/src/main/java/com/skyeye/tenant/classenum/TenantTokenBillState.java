/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 按量月结账单状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TenantTokenBillState implements SkyeyeEnumClass {

    SETTLED(0, "已出账", "blue", true, true),
    PAID(1, "已结清", "green", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
