/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
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
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionPlan
 * @Description: 设备巡检方案实体类
 */
@Data
@RedisCacheField(name = "erp:equipmentInspectionPlan", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "erp_equipment_inspection_plan")
@ApiModel("设备巡检方案实体类")
public class EquipmentInspectionPlan extends BaseGeneralInfo {

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "巡检方案编码", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "frequency")
    @ApiModelProperty(value = "巡检频率", enumClass = ScheduleFrequency.class, required = "required,num")
    private Integer frequency;

    @TableField(exist = false)
    @Property(value = "巡检频率信息")
    private Map<String, Object> frequencyMation;

    @TableField(value = "inspections_per_day")
    @ApiModelProperty(value = "当天规定巡检次数", required = "required,num", defaultValue = "1")
    private Integer inspectionsPerDay;

    @TableField(value = "inspection_item")
    @ApiModelProperty(value = "巡检项目", required = "required")
    private String inspectionItem;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "方案开始时间，格式yyyy-MM-dd HH:mm:ss", required = "required")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "方案结束时间，格式yyyy-MM-dd HH:mm:ss")
    private String endTime;

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
    @ApiModelProperty(value = "关联的设备ID列表", required = "required,json")
    private List<String> equipmentId;

    /**
     * 关联设备信息列表
     */
    @TableField(exist = false)
    @Property(value = "关联的设备信息列表")
    private List<Map<String, Object>> equipmentMation;

}
