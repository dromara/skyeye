/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.ScheduleFrequency;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: PatrolPlan
 * @Description: 巡检计划实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "seal:patrol:plan", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "crm_service_patrol_plan")
@ApiModel("巡检计划实体类")
public class PatrolPlan extends BaseGeneralInfo {

    @TableField(value = "odd_number")
    @Property(value = "计划编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "team_id")
    @ApiModelProperty(value = "巡检班组ID", required = "required")
    private String teamId;

    @TableField(exist = false)
    @Property(value = "班组信息")
    private PatrolTeam teamMation;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "计划开始时间", required = "required")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "计划结束时间")
    private String endTime;

    @TableField(value = "frequency")
    @ApiModelProperty(value = "巡检频次", enumClass = ScheduleFrequency.class, required = "required,num")
    private Integer frequency;

    @TableField(value = "patrol_time")
    @ApiModelProperty(value = "巡检时间（格式：HH:mm）")
    private String patrolTime;

    @TableField(value = "week_days")
    @ApiModelProperty(value = "每周巡检日期（1-7，多个用逗号分隔，如：1,3,5）")
    private String weekDays;

    @TableField(value = "month_days")
    @ApiModelProperty(value = "每月巡检日期（1-31，多个用逗号分隔，如：1,15,30）")
    private String monthDays;

    @TableField(value = "custom_cron")
    @ApiModelProperty(value = "自定义Cron表达式（当频次为自定义时使用）")
    private String customCron;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联的巡检点位ID列表")
    private List<String> pointId;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联的巡检点位信息列表")
    private List<PatrolPoint> pointMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联的巡检项目ID列表")
    private List<String> itemId;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联的巡检项目信息列表")
    private List<PatrolItem> itemMation;

}

