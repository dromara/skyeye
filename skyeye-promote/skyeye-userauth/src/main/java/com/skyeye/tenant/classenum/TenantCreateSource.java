/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: TenantCreateSource
 * @Description: 租户组织创建来源
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TenantCreateSource implements SkyeyeEnumClass {

    PLATFORM(1, "后台创建", true, true),
    USER(2, "用户自助创建", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
