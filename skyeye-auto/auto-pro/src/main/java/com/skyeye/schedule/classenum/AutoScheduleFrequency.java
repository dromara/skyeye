/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Description: 自动化定时任务频次
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AutoScheduleFrequency implements SkyeyeEnumClass {

    DAILY(1, "每天", true, true),
    WEEKLY(2, "每周", true, false),
    MONTHLY(3, "每月", true, false),
    CUSTOM(99, "自定义", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
