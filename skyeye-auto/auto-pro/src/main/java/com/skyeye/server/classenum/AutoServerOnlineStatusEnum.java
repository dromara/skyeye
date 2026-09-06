/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 服务器在线状态（中心探测结果）。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AutoServerOnlineStatusEnum implements SkyeyeEnumClass {

    ONLINE("online", "在线", true, false),
    OFFLINE("offline", "离线", true, false),
    UNKNOWN("unknown", "未探测", true, true);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
