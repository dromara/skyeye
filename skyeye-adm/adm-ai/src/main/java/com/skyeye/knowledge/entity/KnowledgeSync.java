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
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.knowledge.classenum.KnowledgeSyncTypeEnum;
import lombok.Data;

/**
 * 知识库同步表配置：一张业务表对应一条记录。
 */
@Data
@TableName(value = "skyeye_ai_knowledge_sync")
@ApiModel("AI知识库同步表配置")
public class KnowledgeSync extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "knowledge_id")
    @ApiModelProperty(value = "知识库id")
    private String knowledgeId;

    @TableField(value = "table_name")
    @ApiModelProperty(value = "同步表名", required = "required")
    private String tableName;

    @TableField(value = "id_field")
    @ApiModelProperty(value = "主键字段", required = "required")
    private String idField;

    @TableField(value = "title_field")
    @ApiModelProperty(value = "标题字段")
    private String titleField;

    @TableField(value = "content_fields")
    @ApiModelProperty(value = "内容字段，多个用逗号分隔", required = "required")
    private String contentFields;

    @TableField(value = "tenant_field")
    @ApiModelProperty(value = "源表租户字段名，默认 tenant_id")
    private String tenantField;

    @TableField(value = "tenant_isolation")
    @ApiModelProperty(value = "表数据隔离类型", enumClass = TenantEnum.class)
    private String tenantIsolation;

    @TableField(value = "sync_type")
    @ApiModelProperty(value = "同步类型", enumClass = KnowledgeSyncTypeEnum.class, required = "required,num")
    private Integer syncType;

    @TableField(value = "watermark_field")
    @ApiModelProperty(value = "增量水位字段，如 update_time")
    private String watermarkField;

    @TableField(value = "last_watermark")
    @Property("最近一次增量水位")
    private String lastWatermark;

    @TableField(value = "tenant_id", updateStrategy = FieldStrategy.NEVER)
    @Property("租户id")
    private String tenantId;

}
