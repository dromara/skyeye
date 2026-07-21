/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Description: 自动化定时任务执行范围
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AutoScheduleExecuteType implements SkyeyeEnumClass {

    FULL(1, "全量执行", true, true),
    MODULE(2, "按模块执行", true, false),
    CASE(3, "按用例执行", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
