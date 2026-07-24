/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.schedule.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.CheckDayType;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.eve.schedule.classenum.ScheduleImported;
import com.skyeye.eve.schedule.classenum.ScheduleRemindType;
import lombok.Data;

/**
 * @ClassName: ScheduleDay
 * @Description: 日程实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/26 18:59
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "schedule_day")
@ApiModel("日程实体类")
public class ScheduleDay extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "标题", required = "required")
    private String name;

    @TableField(value = "remark")
    @ApiModelProperty(value = "日程内容/备注")
    private String remark;

    @TableField(value = "all_day", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "是否全天", enumClass = WhetherEnum.class, required = "required,num")
    private Integer allDay;

    @TableField("start_time")
    @ApiModelProperty(value = "开始时间,格式为：yyyy-MM-dd HH:mm:ss", required = "required")
    private String startTime;

    @TableField("end_time")
    @ApiModelProperty(value = "结束时间,格式为：yyyy-MM-dd HH:mm:ss", required = "required")
    private String endTime;

    @TableField(value = "remind_type")
    @ApiModelProperty(value = "提醒时间所属类型", enumClass = ScheduleRemindType.class, required = "required,num")
    private Integer remindType;

    @TableField("remind_time")
    @Property("提醒时间，系统计算")
    private String remindTime;

    @TableField(value = "type", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "日程类型", enumClass = CheckDayType.class, required = "required,num")
    private Integer type;

    @TableField(exist = false)
    @Property("日程类型名称")
    private String typeName;

    @TableField("state")
    @Property("日程状态 0.新建日程 1.已提醒结束")
    private Integer state = 0;

    @TableField(exist = false)
    @Property("日程状态名称")
    private String stateName;

    @TableField(value = "imported")
    @ApiModelProperty(value = "日程重要性", enumClass = ScheduleImported.class, required = "required,num")
    private Integer imported;

    @TableField(exist = false)
    @Property("日程重要性名称")
    private String importedName;

    @TableField(exist = false)
    @Property("日程背景色")
    private String backgroundColor;

    @TableField(value = "is_remind")
    @Property(value = "是否需要提醒", enumClass = WhetherEnum.class)
    private Integer isRemind = 1;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "关联id")
    private String objectId;

    @TableField(value = "object_type", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "object类型：1.任务计划id，2.项目任务id", required = "num")
    private Integer objectType;

}
