/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.notice.classenum.NoticeDispatchHistoryStatusEnum;
import lombok.Data;

import java.util.List;

/**
 * 消息通知派发历史（一次 dispatch 一条）
 */
@Data
@TableName(value = "sys_notice_dispatch_history")
@ApiModel("消息通知派发历史")
public class NoticeDispatchHistory extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("type_code")
    @ApiModelProperty(value = "通知类型编码", fuzzyLike = true)
    private String typeCode;

    @TableField("type_name")
    @ApiModelProperty(value = "通知类型名称", fuzzyLike = true)
    private String typeName;

    @TableField("title")
    @ApiModelProperty(value = "标题摘要", fuzzyLike = true)
    private String title;

    @TableField("object_data")
    @ApiModelProperty(value = "关联业务JSON")
    private String objectData;

    @TableField("receiver_count")
    @ApiModelProperty(value = "接收人数")
    private Integer receiverCount;

    @TableField("success_count")
    @ApiModelProperty(value = "成功渠道次数")
    private Integer successCount;

    @TableField("fail_count")
    @ApiModelProperty(value = "失败渠道次数")
    private Integer failCount;

    @TableField("skip_count")
    @ApiModelProperty(value = "跳过渠道次数")
    private Integer skipCount;

    @TableField(value = "`status`")
    @ApiModelProperty(value = "批次状态", enumClass = NoticeDispatchHistoryStatusEnum.class)
    private Integer status;

    @TableField(value = "tenant_id", updateStrategy = FieldStrategy.NEVER)
    @Property("租户id")
    private String tenantId;

    @TableField(exist = false)
    @Property("渠道明细")
    private List<NoticeDispatchChannelLog> channelLogList;

}
