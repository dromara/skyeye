/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 知识库同步明细类型：表 / 文件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum KnowledgeSyncItemTypeEnum implements SkyeyeEnumClass {

    TABLE(1, "同步表", "blue", true, true),
    FILE(2, "文件", "green", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
