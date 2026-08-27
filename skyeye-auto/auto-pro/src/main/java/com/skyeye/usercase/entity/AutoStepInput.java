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

import com.skyeye.usercase.classenum.AutoValueFromTypeEnum;

/**
 * @ClassName: AutoStepInput
 * @Description: 用例步骤前置条件实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/28 12:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "auto_step_input")
@ApiModel(value = "用例步骤前置条件实体类")
public class AutoStepInput extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("step_id")
    @Property(value = "步骤id")
    private String stepId;

    /**
     * 入参参数名。
     * API 步骤：作为请求入参字段写入接口；
     * 数据库步骤：可在 SQL 中用 #{key} 占位，执行时替换为实际值。
     */
    @TableField("`key`")
    @ApiModelProperty(value = "入参参数名；数据库步骤可在SQL中用#{键}引用", required = "required")
    private String key;

    /**
     * 值来源，参考 AutoValueFromTypeEnum：1-自定义字面量；2-表达式（从结果集 JsonPath 取值）。
     */
    @TableField("value_from")
    @ApiModelProperty(value = "值的数据来源", required = "required", enumClass = AutoValueFromTypeEnum.class)
    private Integer valueFrom;

    /**
     * 入参值：自定义时为字面量；表达式时为 JsonPath（相对结果集 result），如 前序步骤resultKey.data.id。
     */
    @TableField("value")
    @ApiModelProperty(value = "入参值：自定义填字面量；表达式填前序步骤JsonPath路径")
    private String value;

    @TableField("random_category")
    @ApiModelProperty(value = "随机数类别：date/datetime/code6/code8")
    private String randomCategory;

    @TableField("random_position")
    @ApiModelProperty(value = "随机数位置：front/back")
    private String randomPosition;

    @TableField("case_id")
    @Property(value = "所属用例")
    private String caseId;

}
