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
import com.skyeye.knowledge.classenum.KnowledgeSyncResultEnum;
import com.skyeye.knowledge.classenum.KnowledgeSyncTriggerEnum;
import lombok.Data;

/**
 * 知识库同步执行历史（每次手动/定时同步一条记录）
 */
@Data
@TableName(value = "skyeye_ai_knowledge_sync_history")
@ApiModel("AI知识库同步历史")
public class KnowledgeSyncHistory extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(value = "knowledge_id")
    @ApiModelProperty(value = "知识库id")
    private String knowledgeId;

    @TableField(value = "trigger_type")
    @ApiModelProperty(value = "触发方式", enumClass = KnowledgeSyncTriggerEnum.class)
    private Integer triggerType;

    @TableField(value = "`status`")
    @ApiModelProperty(value = "同步结果", enumClass = KnowledgeSyncResultEnum.class)
    private Integer status;

    @TableField(value = "sync_count")
    @ApiModelProperty(value = "本次处理条数")
    private Integer syncCount;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @TableField(value = "error_msg")
    @ApiModelProperty(value = "失败原因", fuzzyLike = true)
    private String errorMsg;

    @TableField(value = "tenant_id", updateStrategy = FieldStrategy.NEVER)
    @Property("租户id")
    private String tenantId;

}
