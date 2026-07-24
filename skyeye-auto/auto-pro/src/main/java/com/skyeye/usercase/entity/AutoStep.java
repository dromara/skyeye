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

import java.util.List;

import com.skyeye.usercase.classenum.AutoStepTypeEnum;

/**
 * @ClassName: AutoStep
 * @Description: 用例步骤实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/28 12:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "auto_step")
@ApiModel(value = "用例步骤实体类")
public class AutoStep extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required")
    private String name;

    @TableField("result_key")
    @ApiModelProperty(value = "结果的key", required = "required")
    private String resultKey;

    @TableField("order_by")
    @ApiModelProperty(value = "顺序", required = "required")
    private Integer orderBy;

    @TableField("type")
    @ApiModelProperty(value = "步骤类型", required = "required", enumClass = AutoStepTypeEnum.class)
    private Integer type;

    @TableField("case_id")
    @Property(value = "所属用例")
    private String caseId;

    @TableField(exist = false)
    @ApiModelProperty(value = "API", required = "json")
    private AutoStepApi stepApi;

    @TableField(exist = false)
    @ApiModelProperty(value = "断言", required = "json")
    private List<AutoStepAssert> stepAssertList;

    @TableField(exist = false)
    @ApiModelProperty(value = "用例", required = "json")
    private AutoStepCase stepCase;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据库", required = "json")
    private AutoStepDatabase stepDatabase;

    @TableField(exist = false)
    @ApiModelProperty(value = "前置条件", required = "json")
    private List<AutoStepInput> stepInputList;

}
