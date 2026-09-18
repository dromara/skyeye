/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 会员实名认证状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MemberAuthStatus implements SkyeyeEnumClass {

    NOT_AUTH(0, "未认证", true, true),
    AUTHED(1, "已认证", true, false);

    private Integer key;
    private String value;
    private Boolean show;
    private Boolean isDefault;
}
