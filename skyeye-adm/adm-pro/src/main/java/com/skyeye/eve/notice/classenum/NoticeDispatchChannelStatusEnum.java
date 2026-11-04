/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 消息通知渠道明细状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum NoticeDispatchChannelStatusEnum implements SkyeyeEnumClass {

    SKIPPED(1, "未开启/跳过", "default", true, false),
    PROCESSING(2, "发送中", "blue", true, true),
    SUCCESS(3, "成功", "green", true, false),
    FAIL(4, "失败", "red", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
