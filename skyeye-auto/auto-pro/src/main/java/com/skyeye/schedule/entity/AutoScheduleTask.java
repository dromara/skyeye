/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeTeamAuth;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.module.entity.AutoModule;
import com.skyeye.schedule.classenum.AutoScheduleExecuteType;
import com.skyeye.schedule.classenum.AutoScheduleFrequency;
import com.skyeye.usercase.entity.AutoCase;
import lombok.Data;

import java.util.List;

/**
 * @Description: 自动化定时任务
 */
@Data
@RedisCacheField(name = "auto:schedule:task", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "auto_schedule_task")
@ApiModel("自动化定时任务")
public class AutoScheduleTask extends SkyeyeTeamAuth {

    @TableId("id")
    @ApiModelProperty("主键id")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField("remark")
    @ApiModelProperty("相关描述")
    private String remark;

    @TableField(value = "frequency")
    @ApiModelProperty(value = "执行频次", enumClass = AutoScheduleFrequency.class, required = "required,num")
    private Integer frequency;

    @TableField(value = "execute_time")
    @ApiModelProperty(value = "执行时间（格式：HH:mm）")
    private String executeTime;

    @TableField(value = "week_days")
    @ApiModelProperty(value = "每周执行日期（1-7，多个用逗号分隔，如：1,3,5）")
    private String weekDays;

    @TableField(value = "month_days")
    @ApiModelProperty(value = "每月执行日期（1-31，多个用逗号分隔，如：1,15,30）")
    private String monthDays;

    @TableField(value = "custom_cron")
    @ApiModelProperty(value = "自定义Cron表达式（当频次为自定义时使用）")
    private String customCron;

    @TableField(value = "execute_type")
    @ApiModelProperty(value = "执行范围", enumClass = AutoScheduleExecuteType.class, required = "required,num")
    private Integer executeType;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联的模块ID列表")
    private List<String> moduleIdList;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联的模块信息列表")
    private List<AutoModule> moduleMationList;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联的用例ID列表")
    private List<String> caseIdList;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联的用例信息列表")
    private List<AutoCase> caseMationList;

}
