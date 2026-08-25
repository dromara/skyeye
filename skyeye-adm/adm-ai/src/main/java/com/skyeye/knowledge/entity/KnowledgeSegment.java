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
 * 知识库分段。向量化恢复前先存原文，供角色检索。
 */
@Data
@TableName(value = "skyeye_ai_knowledge_segment")
@ApiModel("AI知识库分段")
public class KnowledgeSegment extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(value = "knowledge_id")
    @ApiModelProperty(value = "知识库id")
    private String knowledgeId;

    @TableField(value = "doc_id")
    @ApiModelProperty(value = "文档id")
    private String docId;

    @TableField(value = "doc_name")
    @ApiModelProperty(value = "文档标题")
    private String docName;

    @TableField(value = "content")
    @ApiModelProperty(value = "分段文本")
    private String content;

    @TableField(value = "segment_index")
    @ApiModelProperty(value = "分段序号")
    private Integer segmentIndex;

    @TableField(value = "tenant_id", updateStrategy = FieldStrategy.NEVER)
    @Property("租户id")
    private String tenantId;

}
