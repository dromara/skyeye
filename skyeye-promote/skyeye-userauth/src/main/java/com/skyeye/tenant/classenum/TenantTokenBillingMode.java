/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 租户 Token 计费方式
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TenantTokenBillingMode implements SkyeyeEnumClass {

    NONE(0, "未开通", true, true),
    PAYG(1, "按 Token 计费", true, false),
    PREPAID(2, "预付购买", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
