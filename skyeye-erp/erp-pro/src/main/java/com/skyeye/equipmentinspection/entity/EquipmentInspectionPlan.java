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
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionFrequencyType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionPlan
 * @Description: 设备巡检方案实体类
 */
@Data
@UniqueField(value = {"planCode"})
@RedisCacheField(name = "erp:equipmentInspectionPlan:zcdemo:v2", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "erp_equipment_inspection_plan_zcdemo")
@ApiModel("设备巡检方案实体类")
public class EquipmentInspectionPlan extends BaseGeneralInfo {

    @TableField(value = "plan_code", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "巡检方案编码", fuzzyLike = true)
    private String planCode;

    @TableField("frequency_type")
    @ApiModelProperty(value = "巡检频率", enumClass = EquipmentInspectionFrequencyType.class, required = "required,num", defaultValue = "1")
    private Integer frequencyType;

    @TableField(exist = false)
    @Property(value = "巡检频率信息")
    private Map<String, Object> frequencyTypeMation;

    @TableField("inspections_per_day")
    @ApiModelProperty(value = "每日巡检次数", required = "required,num", defaultValue = "1")
    private Integer inspectionsPerDay;

    @TableField(exist = false)
    @ApiModelProperty(value = "巡检方案明细", required = "required,json")
    private List<EquipmentInspectionPlanItem> equipmentInspectionPlanItemList;
}

