/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: TenantAppBuyOrderSource
 * @Description: 租户应用购买订单来源
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TenantAppBuyOrderSource implements SkyeyeEnumClass {

    PLATFORM(1, "后台添加", true, true),
    TENANT(2, "租户自购", true, false),
    TOKEN_BILL(3, "月结账单", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
