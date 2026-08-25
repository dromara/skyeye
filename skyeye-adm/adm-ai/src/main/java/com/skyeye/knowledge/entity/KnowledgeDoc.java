/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * 知识库同步落库文档
 */
@Data
@TableName(value = "skyeye_ai_knowledge_doc")
@ApiModel("AI知识库文档")
public class KnowledgeDoc extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(value = "knowledge_id")
    @ApiModelProperty(value = "知识库id")
    private String knowledgeId;

    @TableField(value = "title")
    @ApiModelProperty(value = "标题")
    private String title;

    @TableField(value = "`type`")
    @ApiModelProperty(value = "文档类型，sync 表示库表同步")
    private String type;

    @TableField(value = "content")
    @ApiModelProperty(value = "正文")
    private String content;

    @TableField(value = "source_table")
    @ApiModelProperty(value = "来源表名")
    private String sourceTable;

    @TableField(value = "source_id")
    @ApiModelProperty(value = "来源主键")
    private String sourceId;

    @TableField(value = "metadata")
    @ApiModelProperty(value = "元数据 JSON")
    private String metadata;

    @TableField(value = "`status`")
    @ApiModelProperty(value = "状态")
    private String status;

    @TableField(value = "tenant_id", updateStrategy = FieldStrategy.NEVER)
    @Property("租户id")
    private String tenantId;

}
