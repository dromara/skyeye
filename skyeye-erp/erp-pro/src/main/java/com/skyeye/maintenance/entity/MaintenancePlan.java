/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.maintenance.classenum.MaintenancePlanFrequency;
import lombok.Data;

import java.util.List;

/**
 * @Description: 保养计划
 */
@Data
@TableName("erp_equipment_maintenance_plan")
@ApiModel("保养计划")
public class MaintenancePlan extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(value = "odd_number")
    @Property(value = "保养计划编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "equipment_id")
    @ApiModelProperty(value = "设备id", required = "required")
    private String equipmentId;

    @TableField(exist = false)
    @Property(value = "设备信息")
    private Equipment equipmentMation;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "计划开始时间", required = "required")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "计划结束时间")
    private String endTime;

    @TableField(value = "frequency")
    @ApiModelProperty(value = "保养频次", enumClass = MaintenancePlanFrequency.class, required = "required,num")
    private Integer frequency;

    @TableField(value = "maintain_time")
    @ApiModelProperty(value = "保养时间（格式：HH:mm）")
    private String maintainTime;

    @TableField(value = "week_days")
    @ApiModelProperty(value = "每周保养日期（1-7，多个用逗号分隔，如：1,3,5）")
    private String weekDays;

    @TableField(value = "month_days")
    @ApiModelProperty(value = "每月保养日期（1-31，多个用逗号分隔，如：1,15,30）")
    private String monthDays;

    @TableField(value = "custom_cron")
    @ApiModelProperty(value = "自定义Cron表达式（当频次为自定义时使用）")
    private String customCron;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(exist = false)
    @ApiModelProperty(value = "保养计划明细")
    private List<MaintenancePlanItem> maintenancePlanItemList;
}
