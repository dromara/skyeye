/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 服务器中心探测类型。
 * ping：对目标 IP+端口做 TCP 连通性探测（比 ICMP 更通用）。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AutoServerProbeTypeEnum implements SkyeyeEnumClass {

    PING("ping", "端口探测", true, true),
    HTTP("http", "HTTP探测", true, false),
    HTTPS("https", "HTTPS探测", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
