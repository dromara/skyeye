/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 知识库上传文件同步状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum KnowledgeFileSyncStatusEnum implements SkyeyeEnumClass {

    WAIT(0, "待同步", true, true),
    SUCCESS(1, "已同步", true, false),
    FAIL(2, "同步失败", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
