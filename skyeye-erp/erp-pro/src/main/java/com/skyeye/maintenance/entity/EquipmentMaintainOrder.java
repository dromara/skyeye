/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.entity;

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
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.maintenance.classenum.EquipmentMaintainResult;
import com.skyeye.maintenance.classenum.EquipmentMaintainTaskState;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description: 设备保养任务（参考设备巡检任务，不走审批，无独立保养记录单）
 */
@Data
@RedisCacheField(name = "erp:equipment:maintainOrder", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "erp_equipment_maintain_order")
@ApiModel("设备保养任务")
public class EquipmentMaintainOrder extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "任务编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "plan_id")
    @ApiModelProperty(value = "保养计划id", required = "required")
    private String planId;

    @TableField(exist = false)
    @Property(value = "保养计划")
    private MaintenancePlan planMation;

    @TableField(value = "equipment_id")
    @ApiModelProperty(value = "设备id", required = "required")
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

    @TableField(value = "state")
    @Property(value = "任务状态", enumClass = EquipmentMaintainTaskState.class)
    private Integer state;

    @TableField(value = "maintain_result")
    @ApiModelProperty(value = "保养结果", enumClass = EquipmentMaintainResult.class, required = "num")
    private Integer maintainResult;

    @TableField(value = "is_to_repair")
    @ApiModelProperty(value = "是否转维修", enumClass = WhetherEnum.class, required = "num")
    private Integer isToRepair;

    @TableField(value = "maintain_photos")
    @ApiModelProperty(value = "保养拍照")
    private String maintainPhotos;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "保养明细", required = "json")
    private List<EquipmentMaintainOrderItem> maintainOrderItemList;

    @TableField(exist = false)
    @ApiModelProperty(value = "备件使用明细", required = "json")
    private List<EquipmentMaintainOrderSparePartDetail> sparePartDetailList;
}
