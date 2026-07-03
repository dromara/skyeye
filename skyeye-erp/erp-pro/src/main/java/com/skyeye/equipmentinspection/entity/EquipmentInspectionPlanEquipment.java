/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: EquipmentInspectionPlanEquipment
 * @Description: 设备巡检方案设备关联实体类
 */
@Data
@TableName(value = "erp_equipment_inspection_plan_equipment")
@ApiModel("设备巡检方案设备关联实体类")
public class EquipmentInspectionPlanEquipment extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "plan_id")
    @ApiModelProperty(value = "方案ID", required = "required")
    private String planId;

    @TableField(value = "equipment_id")
    @ApiModelProperty(value = "设备ID", required = "required")
    private String equipmentId;

    @TableField(exist = false)
    @ApiModelProperty(value = "设备信息")
    private Map<String, Object> equipmentMation;

}
