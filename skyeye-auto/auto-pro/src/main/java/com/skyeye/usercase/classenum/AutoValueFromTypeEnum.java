/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.usercase.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: AutoValueFromTypeEnum
 * @Description: 自动化用例「值」的数据来源枚举。
 * 用于步骤入参(AutoStepInput)、断言(AutoStepAssert)等：
 * CUSTOMIZE=直接使用填写的字面量；EXPRESSION=把填写内容当作 JsonPath，从用例执行结果集 result 中取值。
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/20 19:42
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AutoValueFromTypeEnum implements SkyeyeEnumClass {

    /** 自定义：value 为固定字面量，原样参与入参或断言期望值 */
    CUSTOMIZE(1, "自定义", true, true),
    /** 表达式：value 为 JsonPath 路径（相对结果集 result），如 步骤resultKey.data.id */
    EXPRESSION(2, "表达式", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}