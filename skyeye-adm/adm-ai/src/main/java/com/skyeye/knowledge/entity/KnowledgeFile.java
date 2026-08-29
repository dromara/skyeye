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
import com.skyeye.knowledge.classenum.KnowledgeFileSyncStatusEnum;
import lombok.Data;

/**
 * AI 知识库上传文件。本地保存后随知识库同步到 S3/平台，删除时按 S3 对象 ID 清理。
 */
@Data
@TableName(value = "skyeye_ai_knowledge_file")
@ApiModel("AI知识库文件")
public class KnowledgeFile extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "knowledge_id")
    @ApiModelProperty(value = "知识库id", required = "required")
    private String knowledgeId;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "原始文件名", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "`path`")
    @ApiModelProperty(value = "本地访问路径", required = "required")
    private String path;

    @TableField(value = "file_ext")
    @ApiModelProperty(value = "文件扩展名")
    private String fileExt;

    @TableField(value = "file_size")
    @ApiModelProperty(value = "文件大小（字节）")
    private Long fileSize;

    @TableField(value = "sync_status")
    @ApiModelProperty(value = "同步状态", enumClass = KnowledgeFileSyncStatusEnum.class)
    private Integer syncStatus;

    @TableField(value = "s3_object_id")
    @Property("同步到 S3/TOS 的对象路径，删除时使用")
    private String s3ObjectId;

    @TableField(value = "storage_config_id")
    @Property("文件配置ID，删除 S3 对象时使用")
    private String storageConfigId;

    @TableField(value = "platform_doc_id")
    @Property("平台知识库文档ID")
    private String platformDocId;

    @TableField(value = "sync_time")
    @Property("最近同步时间")
    private String syncTime;

    @TableField(value = "error_msg")
    @Property("同步失败原因")
    private String errorMsg;

    @TableField(value = "tenant_id", updateStrategy = FieldStrategy.NEVER)
    @Property("租户id")
    private String tenantId;

}
