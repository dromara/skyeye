/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 知识库同步类型：全量 / 增量
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum KnowledgeSyncTypeEnum implements SkyeyeEnumClass {

    FULL(1, "全量同步", true, true),
    INCREMENTAL(2, "增量同步", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static KnowledgeSyncTypeEnum getByKey(Integer key) {
        if (key == null) {
            return null;
        }
        for (KnowledgeSyncTypeEnum bean : values()) {
            if (key.equals(bean.getKey())) {
                return bean;
            }
        }
        return null;
    }

}
