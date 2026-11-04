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
import com.skyeye.eve.notice.classenum.NoticeChannelEnum;
import com.skyeye.eve.notice.classenum.NoticeDispatchChannelStatusEnum;
import lombok.Data;

import java.util.Map;

/**
 * 消息通知派发渠道明细（接收人 × 渠道）
 */
@Data
@TableName(value = "sys_notice_dispatch_channel_log")
@ApiModel("消息通知派发渠道明细")
public class NoticeDispatchChannelLog extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("history_id")
    @ApiModelProperty(value = "派发历史id")
    private String historyId;

    @TableField("channel")
    @ApiModelProperty(value = "渠道", enumClass = NoticeChannelEnum.class)
    private Integer channel;

    @TableField("receive_user_id")
    @ApiModelProperty(value = "接收人用户id")
    private String receiveUserId;

    @TableField(exist = false)
    @Property("接收人信息")
    private Map<String, Object> receiveUserMation;

    @TableField("receive_account")
    @ApiModelProperty(value = "接收账号")
    private String receiveAccount;

    @TableField("title")
    @ApiModelProperty(value = "标题")
    private String title;

    @TableField("content")
    @ApiModelProperty(value = "内容")
    private String content;

    @TableField("job_mate_id")
    @ApiModelProperty(value = "MQ任务id")
    private String jobMateId;

    @TableField(value = "`status`")
    @ApiModelProperty(value = "渠道状态", enumClass = NoticeDispatchChannelStatusEnum.class)
    private Integer status;

    @TableField("error_msg")
    @ApiModelProperty(value = "跳过原因或失败信息")
    private String errorMsg;

    @TableField("send_time")
    @ApiModelProperty(value = "投递/开始时间")
    private String sendTime;

    @TableField("finish_time")
    @ApiModelProperty(value = "结束时间")
    private String finishTime;

    @TableField(value = "tenant_id", updateStrategy = FieldStrategy.NEVER)
    @Property("租户id")
    private String tenantId;

}
