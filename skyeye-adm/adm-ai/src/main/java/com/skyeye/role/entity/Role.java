/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.knowledge.entity.Knowledge;
import lombok.Data;

/**
 * @ClassName: ShopDeliveryCompanyController
 * @Description: ai角色实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/8 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@TableName(value = "skyeye_ai_role")
@ApiModel("AI角色")
public class Role extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "角色名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "`remark`")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "`logo`")
    @ApiModelProperty(value = "角色logo")
    private String logo;

    @TableField(value = "prompt")
    @ApiModelProperty(value = "提示词")
    private String prompt;

    @TableField(value = "knowledge_id")
    @ApiModelProperty(value = "绑定知识库id")
    private String knowledgeId;

    @TableField(exist = false)
    @Property("绑定的知识库")
    private Knowledge knowledgeMation;
}
