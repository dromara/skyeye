/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.skyeye.eve.notice.classenum.NoticeRealLinesType;
import com.skyeye.eve.notice.classenum.NoticeState;
import com.skyeye.eve.notice.classenum.NoticeTimeSend;

/**
 * @ClassName: Notice
 * @Description: 公告实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/30 19:56
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@UniqueField
@RedisCacheField(name = "sys:notice")
@TableName(value = "sys_notice", autoResultMap = true)
@ApiModel("公告实体类")
public class Notice extends BaseGeneralInfo {

    @TableField(value = "content")
    @ApiModelProperty(value = "内容", required = "required")
    private String content;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态", required = "required,num", enumClass = NoticeState.class)
    private Integer state;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序，值越大越往前", required = "required,num")
    private Integer orderBy;

    @TableField(value = "whether_email")
    @ApiModelProperty(value = "是否发送邮件通知", required = "required,num", enumClass = WhetherEnum.class)
    private Integer whetherEmail;

    @TableField(value = "time_send")
    @ApiModelProperty(value = "是否设置定时发送", required = "required,num", enumClass = NoticeTimeSend.class)
    private Integer timeSend;

    @TableField(value = "delayed_time")
    @ApiModelProperty(value = "当time_send为2时的定时任务时间")
    private String delayedTime;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "所属分类，参考数据字典", required = "required")
    private String typeId;

    @TableField(value = "real_lines_type")
    @Property(value = "上线类型", enumClass = NoticeRealLinesType.class)
    private Integer realLinesType;

    @TableField(value = "real_lines_time")
    @Property(value = "真正的上线时间")
    private String realLinesTime;

    @TableField(value = "send_type")
    @ApiModelProperty(value = "是否群发所有人", enumClass = WhetherEnum.class, required = "required,num")
    private Integer sendType;

    @TableField(exist = false)
    @ApiModelProperty(value = "公告不是群发时，指定的人", required = "json")
    private List<String> receiver;

    @TableField(exist = false)
    @Property(value = "公告不是群发时，指定的人")
    private List<Map<String, Object>> receiverMation;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

}
