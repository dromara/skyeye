/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionTaskState;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: EquipmentInspectionTask
 * @Description: 设备巡检任务实体类
 */
@Data
@RedisCacheField(name = "erp:equipmentInspectionTask", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "erp_equipment_inspection_task")
@ApiModel("设备巡检任务实体类")
public class EquipmentInspectionTask extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "任务编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "plan_id")
    @ApiModelProperty(value = "巡检方案ID", required = "required")
    private String planId;

    @TableField(exist = false)
    @Property(value = "方案信息")
    private EquipmentInspectionPlan planMation;

    @TableField(value = "equipment_id")
    @ApiModelProperty(value = "设备ID", required = "required")
    private String equipmentId;

    @TableField(exist = false)
    @Property(value = "设备信息")
    private Map<String, Object> equipmentMation;

    @TableField(value = "executor_id")
    @ApiModelProperty(value = "执行人ID（员工ID）")
    private String executorId;

    @TableField(exist = false)
    @Property(value = "执行人信息")
    private Map<String, Object> executorMation;

    @TableField(value = "planned_start_time")
    @ApiModelProperty(value = "计划开始执行时间，格式yyyy-MM-dd HH:mm:ss", required = "required")
    private String plannedStartTime;

    @TableField(value = "actual_start_time")
    @ApiModelProperty(value = "实际开始时间，格式yyyy-MM-dd HH:mm:ss")
    private String actualStartTime;

    @TableField(value = "actual_end_time")
    @ApiModelProperty(value = "实际结束时间，格式yyyy-MM-dd HH:mm:ss")
    private String actualEndTime;

    @TableField(value = "seq_in_day")
    @ApiModelProperty(value = "当日第几次巡检", required = "required,num")
    private Integer seqInDay;

    @TableField(value = "state")
    @Property(value = "任务状态", enumClass = EquipmentInspectionTaskState.class)
    private Integer state;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

}