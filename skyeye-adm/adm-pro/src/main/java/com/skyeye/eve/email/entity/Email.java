/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.email.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.List;

import com.skyeye.eve.email.classenum.EmailState;
import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: Email
 * @Description: 邮件实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/8 8:57
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "email:mail")
@TableName(value = "email_mail", autoResultMap = true)
@ApiModel("邮件实体类")
public class Email extends CommonInfo {

    @TableId("id")
    @Property("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("title")
    @ApiModelProperty(value = "标题", required = "required")
    private String title;

    @TableField("content")
    @ApiModelProperty(value = "邮件内容", required = "required")
    private String content;

    @TableField("send_date")
    @Property(value = "发送时间")
    private String sendDate;

    @TableField(exist = false)
    @Property(value = "发送日期")
    private String sendDay;

    @TableField(exist = false)
    @Property(value = "具体的发送时间")
    private String sendTime;

    @TableField("replay_sign")
    @Property(value = "是否需要回复", enumClass = WhetherEnum.class)
    private Integer replaySign;

    @TableField("is_new")
    @Property(value = "是否已读", enumClass = WhetherEnum.class)
    private Integer isNew;

    @TableField("is_contain_attach")
    @Property(value = "是否包含附件", enumClass = WhetherEnum.class)
    private Integer isContainAttach;

    @TableField("from_people")
    @Property(value = "发件人")
    private String fromPeople;

    @TableField("to_people")
    @ApiModelProperty(value = "收件人", required = "required")
    private String toPeople;

    @TableField("to_cc")
    @ApiModelProperty(value = "抄送人")
    private String toCc;

    @TableField("to_bcc")
    @ApiModelProperty(value = "暗送人")
    private String toBcc;

    @TableField("message_id")
    @Property(value = "消息id")
    private String messageId;

    @TableField("create_time")
    @Property(value = "创建时间")
    private String createTime;

    @TableField("state")
    @Property(value = "状态", enumClass = EmailState.class)
    private Integer state;

    @TableField(exist = false)
    @Property(value = "邮件附件id串")
    private String emailEnclosure;

    @TableField(exist = false)
    @Property(value = "邮件附件信息")
    private List<EmailEnclosure> emailEnclosureList;

    @TableField(exist = false)
    @Property(value = "用户绑定的邮箱id")
    private String emailUserId;

    @TableField(exist = false)
    @Property(value = "消息任务类型")
    private Integer messageJobType;

}
