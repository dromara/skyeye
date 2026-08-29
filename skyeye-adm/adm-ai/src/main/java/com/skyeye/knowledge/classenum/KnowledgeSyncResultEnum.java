/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 知识库同步结果
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum KnowledgeSyncResultEnum implements SkyeyeEnumClass {

    RUNNING(0, "同步中", "orange", true, false),
    SUCCESS(1, "成功", "green", true, true),
    FAIL(2, "失败", "red", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
