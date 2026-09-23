/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: StoreNature
 * @Description: 门店性质（加盟门店 / 个人门店）
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum StoreNature implements SkyeyeEnumClass {

    FRANCHISE(1, "加盟门店", "#FF8833", true, true),
    PERSONAL(2, "个人门店", "#2176DD", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
