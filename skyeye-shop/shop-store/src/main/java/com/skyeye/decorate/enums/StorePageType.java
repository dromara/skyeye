/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.decorate.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 门店装修页面类型
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum StorePageType implements SkyeyeEnumClass {

    STORE_HOME(1, "门店首页", true, true);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
