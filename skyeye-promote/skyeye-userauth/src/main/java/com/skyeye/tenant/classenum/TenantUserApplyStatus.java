/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: TenantUserApplyStatus
 * @Description: 用户申请加入租户的状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TenantUserApplyStatus implements SkyeyeEnumClass {

    PENDING(0, "待审核", "blue", true, true),
    APPROVED(1, "已通过", "green", true, false),
    REJECTED(2, "已拒绝", "red", true, false),
    CANCELLED(3, "已取消", "gray", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
