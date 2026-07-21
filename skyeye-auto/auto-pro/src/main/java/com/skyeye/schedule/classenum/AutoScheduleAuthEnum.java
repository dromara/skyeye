/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Description: 定时任务权限枚举
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AutoScheduleAuthEnum implements SkyeyeEnumClass {

    ADD("add", "新增", true, false),
    EDIT("edit", "编辑", true, false),
    DELETE("delete", "删除", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
