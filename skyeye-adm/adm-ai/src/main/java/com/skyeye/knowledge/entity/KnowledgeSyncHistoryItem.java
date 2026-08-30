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
import com.skyeye.knowledge.classenum.KnowledgeSyncItemTypeEnum;
import com.skyeye.knowledge.classenum.KnowledgeSyncResultEnum;
import lombok.Data;

/**
 * 一次同步任务里，单张表或单个文件的结果。
 */
@Data
@TableName(value = "skyeye_ai_knowledge_sync_history_item")
@ApiModel("AI知识库同步历史明细")
public class KnowledgeSyncHistoryItem extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(value = "history_id")
    @ApiModelProperty(value = "同步历史id")
    private String historyId;

    @TableField(value = "item_type")
    @ApiModelProperty(value = "明细类型", enumClass = KnowledgeSyncItemTypeEnum.class)
    private Integer itemType;

    @TableField(value = "item_id")
    @ApiModelProperty(value = "同步表配置id或文件id")
    private String itemId;

    @TableField(value = "item_name")
    @ApiModelProperty(value = "表名或文件名", fuzzyLike = true)
    private String itemName;

    @TableField(value = "sync_count")
    @ApiModelProperty(value = "本项处理条数")
    private Integer syncCount;

    @TableField(value = "`status`")
    @ApiModelProperty(value = "同步结果", enumClass = KnowledgeSyncResultEnum.class)
    private Integer status;

    @TableField(value = "error_msg")
    @ApiModelProperty(value = "失败原因")
    private String errorMsg;

    @TableField(value = "tenant_id", updateStrategy = FieldStrategy.NEVER)
    @Property("租户id")
    private String tenantId;

    public static KnowledgeSyncHistoryItem of(Integer itemType, String itemId, String itemName,
                                              int syncCount, Integer status, String errorMsg) {
        KnowledgeSyncHistoryItem item = new KnowledgeSyncHistoryItem();
        item.setItemType(itemType);
        item.setItemId(itemId == null ? "" : itemId);
        item.setItemName(itemName == null ? "" : itemName);
        item.setSyncCount(syncCount);
        item.setStatus(status);
        if (errorMsg != null && errorMsg.length() > 1000) {
            item.setErrorMsg(errorMsg.substring(0, 1000));
        } else {
            item.setErrorMsg(errorMsg);
        }
        return item;
    }

}
