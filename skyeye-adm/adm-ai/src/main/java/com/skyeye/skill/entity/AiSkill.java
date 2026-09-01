/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

/**
 * AI 技能：绑定业务对象 appId + serviceClassName，说明书由积木编译。
 */
@Data
@UniqueField(value = {"oddNumber"})
@TableName(value = "skyeye_ai_skill")
@ApiModel("AI技能")
public class AiSkill extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "suite_id", updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "所属套件id")
    private String suiteId;

    @TableField(value = "odd_number")
    @ApiModelProperty(value = "技能编码")
    private String oddNumber;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "技能名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "description")
    @ApiModelProperty(value = "何时使用")
    private String description;

    @TableField(value = "keywords")
    @ApiModelProperty(value = "触发词，逗号分隔")
    private String keywords;

    @TableField(value = "instruction")
    @ApiModelProperty(value = "编译后的说明书")
    private String instruction;

    @TableField(value = "blocks")
    @ApiModelProperty(value = "结构化积木 JSON")
    private String blocks;

    @TableField(value = "app_id")
    @ApiModelProperty(value = "业务应用 appId", required = "required")
    private String appId;

    @TableField(value = "service_class_name")
    @ApiModelProperty(value = "业务对象 serviceClassName", required = "required")
    private String serviceClassName;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序，越小越优先", defaultValue = "100")
    private Integer orderBy;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "tenant_id", updateStrategy = FieldStrategy.NEVER)
    @Property("租户id")
    private String tenantId;

    @TableField(exist = false)
    @Property("所属套件")
    private AiSkillSuite suiteMation;
}
