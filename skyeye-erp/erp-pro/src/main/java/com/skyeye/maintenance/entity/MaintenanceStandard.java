/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.maintenance.classenum.MaintenanceFrequencyEnum;
import com.skyeye.maintenance.classenum.MaintenanceLevelEnum;
import lombok.Data;

/**
 * @Description: 保养标准
 */
@Data
@TableName("erp_equipment_maintenance_standard")
@ApiModel("保养标准")
public class MaintenanceStandard extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "maintenance_level")
    @ApiModelProperty(value = "保养等级", enumClass = MaintenanceLevelEnum.class, required = "required,num")
    private Integer maintenanceLevel;

    @TableField(value = "maintenance_frequency")
    @ApiModelProperty(value = "保养频次", enumClass = MaintenanceFrequencyEnum.class, required = "required,num")
    private Integer maintenanceFrequency;
}
