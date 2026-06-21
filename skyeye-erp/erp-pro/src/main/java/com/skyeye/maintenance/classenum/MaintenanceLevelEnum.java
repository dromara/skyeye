/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Description: 保养等级
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MaintenanceLevelEnum implements SkyeyeEnumClass {

    DAILY(1, "日常保养", "green", true, true),
    LEVEL_TWO(2, "二级保养", "orange", true, false),
    LEVEL_THREE(3, "三级保养", "red", true, false),
    ANNUAL(4, "年度保养", "purple", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
