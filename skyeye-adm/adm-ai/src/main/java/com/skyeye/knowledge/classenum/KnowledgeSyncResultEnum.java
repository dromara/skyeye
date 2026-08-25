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

    SUCCESS(1, "成功", true, true),
    FAIL(2, "失败", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
