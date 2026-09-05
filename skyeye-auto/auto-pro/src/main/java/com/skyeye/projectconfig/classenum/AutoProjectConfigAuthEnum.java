/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.projectconfig.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 项目功能配置权限。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AutoProjectConfigAuthEnum implements SkyeyeEnumClass {

    EDIT("edit", "编辑", true, true);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
