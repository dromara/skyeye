/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Description: 保养频次
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MaintenanceFrequencyEnum implements SkyeyeEnumClass {

    ONCE_DAILY(1, "每天一次", "green", true, false),
    ONCE_MONTHLY(2, "每月一次", "orange", true, false),
    ONCE_QUARTERLY(3, "每季度一次", "red", true, false),
    ONCE_HALF_YEARLY(4, "每半年一次", "purple", true, false),
    ONCE_YEARLY(5, "每年一次", "blue", true, true);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
