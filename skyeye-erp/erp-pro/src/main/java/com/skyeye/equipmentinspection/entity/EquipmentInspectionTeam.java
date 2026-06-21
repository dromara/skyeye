/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

/**
 * @ClassName: EquipmentInspectionTeam
 * @Description: 设备巡检班组实体类
 */
@Data
@RedisCacheField(name = "erp:equipmentInspectionTeam", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "erp_equipment_inspection_team")
@ApiModel("设备巡检班组实体类")
public class EquipmentInspectionTeam extends BaseGeneralInfo {

    @TableField(value = "team_code")
    @ApiModelProperty(value = "班组编码", required = "required")
    private String teamCode;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "班组排序", required = "required,num")
    private Integer orderBy;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

}
