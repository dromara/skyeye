/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.vehicle.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import com.skyeye.eve.vehicle.classenum.MaintenanceType;

/**
 * @ClassName: Maintenance
 * @Description: 车辆维修实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/3 18:16
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "assistant:vehicle:maintenance", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "vehicle_maintenance")
@ApiModel("车辆维修实体类")
public class Maintenance extends BaseGeneralInfo {

    @TableField("vehicle_id")
    @ApiModelProperty(value = "车辆id", required = "required")
    private String vehicleId;

    @TableField(exist = false)
    @Property(value = "车辆信息")
    private Vehicle vehicleMation;

    @TableField("maintenance_type")
    @ApiModelProperty(value = "类型", required = "required", enumClass = MaintenanceType.class)
    private Integer maintenanceType;

    @TableField("maintenance_price")
    @ApiModelProperty(value = "费用", required = "required,double")
    private String maintenancePrice;

    @TableField("start_time")
    @ApiModelProperty(value = "开始时间", required = "required")
    private String startTime;

    @TableField("end_time")
    @ApiModelProperty(value = "结束时间", required = "required")
    private String endTime;

    @TableField("content")
    @ApiModelProperty(value = "具体内容", required = "required")
    private String content;

}
