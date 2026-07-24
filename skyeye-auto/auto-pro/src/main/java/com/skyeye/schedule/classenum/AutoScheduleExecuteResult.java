/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Description: 定时任务最近执行结果
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AutoScheduleExecuteResult implements SkyeyeEnumClass {

    IN_PROGRESS(1, "执行中", true, false),
    SUCCESS(2, "执行成功", true, false),
    FAILED(3, "执行失败", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
