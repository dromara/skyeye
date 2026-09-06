/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 服务器 SSH 资源采集状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AutoServerMetricStatusEnum implements SkyeyeEnumClass {

    UNKNOWN("unknown", "未采集", true, true),
    OK("ok", "采集成功", true, false),
    FAIL("fail", "采集失败", true, false);

    private String key;
    private String value;
    private Boolean show;
    private Boolean isDefault;

}
