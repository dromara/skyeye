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
import lombok.Data;

/**
 * 小程序订阅消息授权状态（用户 × 模板）
 */
@Data
@TableName(value = "sys_notice_mini_subscribe")
@ApiModel("小程序订阅消息授权状态")
public class NoticeMiniSubscribe extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("user_id")
    @ApiModelProperty(value = "用户id")
    private String userId;

    @TableField("template_id")
    @ApiModelProperty(value = "微信小程序订阅消息模板ID")
    private String templateId;

    @TableField(value = "`status`")
    @ApiModelProperty(value = "授权状态：accept/reject/ban")
    private String status;

    @TableField("last_auth_time")
    @ApiModelProperty(value = "最近一次授权时间")
    private String lastAuthTime;

    @TableField(value = "tenant_id", updateStrategy = FieldStrategy.NEVER)
    @Property("租户id")
    private String tenantId;

}
