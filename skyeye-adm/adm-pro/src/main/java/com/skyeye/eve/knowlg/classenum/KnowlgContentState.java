/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.knowlg.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: KnowlgContentState
 * @Description: 知识库状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/10/14 11:10
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum KnowlgContentState implements SkyeyeEnumClass {

    IN_EXAMINE(1, "审核中", "blue", true, false),
    PASS(2, "审核通过", "green", true, true),
    NO_PASS(3, "审核不通过", "red", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
