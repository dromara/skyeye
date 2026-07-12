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
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.equipment.classenum.EquipmentState;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionResultType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionOrder
 * @Description: 设备巡检单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@RedisCacheField(name = "erp:equipmentInspectionOrder", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "erp_equipment_inspection_order", autoResultMap = true)
@ApiModel("设备巡检单实体类")
public class EquipmentInspectionOrder extends SkyeyeFlowable {

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "设备巡检单编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "equipment_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "设备id", required = "required")
    private String equipmentId;

    @TableField(value = "plan_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "巡检方案id", required = "required")
    private String planId;

    @TableField(value = "task_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "巡检任务id", required = "required")
    private String taskId;

    @TableField(exist = false)
    @Property(value = "任务信息")
    private EquipmentInspectionTask taskMation;

    @TableField("inspection_time")
    @ApiModelProperty(value = "巡检时间，格式yyyy-MM-dd HH:mm:ss", required = "required")
    private String inspectionTime;

    @TableField(value = "inspector_user_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "巡检员用户id")
    private String inspectorUserId;

    @TableField(exist = false)
    @Property(value = "巡检员信息")
    private Map<String, Object> inspectorUserMation;

    @TableField("overall_result")
    @ApiModelProperty(value = "巡检结果", enumClass = EquipmentInspectionResultType.class, required = "required,num")
    private Integer overallResult;

    @TableField("result_value")
    @ApiModelProperty(value = "数值型结果")
    private BigDecimal resultValue;

    @TableField(value = "equipment_run_status", insertStrategy = FieldStrategy.NOT_NULL, updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "设备运行状态", enumClass = EquipmentState.class, required = "num", defaultValue = "1")
    private Integer equipmentRunStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "设备运行状态(兼容旧参 equipmentState)", enumClass = EquipmentState.class)
    private Integer equipmentState;

    @TableField("summary_richtext")
    @ApiModelProperty(value = "巡检总结")
    private String summaryRichtext;

    @TableField("header_location_text")
    @ApiModelProperty(value = "定位文本")
    private String headerLocationText;

    @TableField("header_longitude")
    @ApiModelProperty(value = "定位经度")
    private String headerLongitude;

    @TableField("header_latitude")
    @ApiModelProperty(value = "定位纬度")
    private String headerLatitude;

    @TableField("header_address")
    @ApiModelProperty(value = "定位地址")
    private String headerAddress;

    @TableField("header_photo_urls")
    @ApiModelProperty(value = "拍照URL，逗号分隔")
    private String headerPhotoUrls;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

}