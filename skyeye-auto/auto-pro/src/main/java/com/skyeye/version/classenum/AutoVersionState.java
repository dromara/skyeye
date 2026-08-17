/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.version.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: AutoVersionState
 * @Description: 版本状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/20 19:42
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AutoVersionState implements SkyeyeEnumClass {

    GET("get", "新建", "blue", true, true),
    PROGRESS("progress", "进行中", "orange", true, false),
    END("end", "结束", "green", true, false);

    private String key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
