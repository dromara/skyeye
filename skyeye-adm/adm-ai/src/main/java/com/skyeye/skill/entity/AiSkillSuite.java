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

import java.util.List;

/**
 * AI 技能套件：把请假申请、销假申请等同类技能归到一组，对话时一次命中整套。
 */
@Data
@UniqueField(value = {"oddNumber"})
@TableName(value = "skyeye_ai_skill_suite")
@ApiModel("AI技能套件")
public class AiSkillSuite extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "odd_number")
    @ApiModelProperty(value = "套件编码")
    private String oddNumber;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "套件名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "description")
    @ApiModelProperty(value = "何时使用该套件")
    private String description;

    @TableField(value = "keywords")
    @ApiModelProperty(value = "触发词，逗号分隔")
    private String keywords;

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
    @Property("套件下的技能")
    private List<AiSkill> skillList;
}
