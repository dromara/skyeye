/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 消息通知派发批次状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum NoticeDispatchHistoryStatusEnum implements SkyeyeEnumClass {

    PROCESSING(1, "处理中", "blue", true, true),
    SUCCESS(2, "成功", "green", true, false),
    PARTIAL_SUCCESS(3, "部分成功", "orange", true, false),
    FAIL(4, "失败", "red", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
