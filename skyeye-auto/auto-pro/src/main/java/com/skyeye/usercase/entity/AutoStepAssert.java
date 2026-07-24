/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.usercase.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import com.skyeye.attr.classenum.AttrSymbols;
import com.skyeye.usercase.classenum.AutoValueFromTypeEnum;

/**
 * @ClassName: AutoStepAssert
 * @Description: 用例步骤关联的断言实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/28 12:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "auto_step_assert")
@ApiModel(value = "用例步骤关联的断言实体类")
public class AutoStepAssert extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("step_id")
    @Property(value = "步骤id")
    private String stepId;

    /**
     * 断言实际值的 JsonPath 路径（相对用例执行结果集 result）。
     * 引擎读取方式：JsonPath.read(resultJson, "$.{key}")。
     * 示例：步骤resultKey.code、步骤resultKey.data.message、数据库步骤resultKey[0].userId。
     * 步骤 resultKey 可在前端中间步骤树点击复制。
     */
    @TableField("`key`")
    @ApiModelProperty(value = "断言实际值路径(JsonPath，相对结果集result)，如：步骤resultKey.code", required = "required")
    private String key;

    /**
     * 比较运算符，取值参考 AttrSymbols 的 key：
     * equalTo / notEqual / lessThan / greaterThan / lessThanOrEqual / greaterThanOrEqual / contain。
     * 执行时两侧均按字符串比较：'{实际值}' {symbols} '{期望值}'。
     */
    @TableField("operator")
    @ApiModelProperty(value = "比较运算符", required = "required", enumClass = AttrSymbols.class)
    private String operator;

    /**
     * 期望值来源，参考 AutoValueFromTypeEnum：
     * 1-自定义：value 为字面期望值；2-表达式：value 为另一条 JsonPath，从 result 再取期望值。
     */
    @TableField("value_from")
    @ApiModelProperty(value = "期望值数据来源", required = "required", enumClass = AutoValueFromTypeEnum.class)
    private Integer valueFrom;

    /**
     * 期望值：自定义时为字面量（如 200）；表达式时为 JsonPath 路径（如 另一步骤resultKey.userId）。
     */
    @TableField("value")
    @ApiModelProperty(value = "期望值：自定义填字面量；表达式填JsonPath路径")
    private String value;

    @TableField("order_by")
    @ApiModelProperty(value = "排序", required = "required")
    private Integer orderBy;

    @TableField("case_id")
    @Property(value = "所属用例")
    private String caseId;
}
