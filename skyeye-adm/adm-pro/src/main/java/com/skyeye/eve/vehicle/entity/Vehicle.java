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
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import java.util.Map;

import com.skyeye.eve.vehicle.classenum.VehicleState;

/**
 * @ClassName: Vehicle
 * @Description: 车辆实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/6 9:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"licensePlate"})
@RedisCacheField(name = "assistant:vehicle", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "vehicle")
@ApiModel("车辆实体类")
public class Vehicle extends BaseGeneralInfo {

    @TableField(value = "img")
    @ApiModelProperty(value = "车辆图片", required = "required")
    private String img;

    @TableField(value = "license_plate")
    @ApiModelProperty(value = "车牌", required = "required", fuzzyLike = true)
    private String licensePlate;

    @TableField(value = "specifications")
    @ApiModelProperty(value = "车辆规格(准载人数)", required = "required,num")
    private Integer specifications;

    @TableField(value = "oil_consumption")
    @ApiModelProperty(value = "油耗")
    private String oilConsumption;

    @TableField(value = "unit_price")
    @ApiModelProperty(value = "单价", required = "double", defaultValue = "0")
    private String unitPrice;

    @TableField(value = "color")
    @ApiModelProperty(value = "车辆颜色")
    private String color;

    @TableField(value = "manufacturer")
    @ApiModelProperty(value = "生产商")
    private String manufacturer;

    @TableField(value = "manufacture_time")
    @ApiModelProperty(value = "生产日期")
    private String manufactureTime;

    @TableField(value = "supplier")
    @ApiModelProperty(value = "供应商")
    private String supplier;

    @TableField(value = "purchase_time")
    @ApiModelProperty(value = "采购日期")
    private String purchaseTime;

    @TableField(value = "engine_number")
    @ApiModelProperty(value = "发动机号")
    private String engineNumber;

    @TableField(value = "frame_number")
    @ApiModelProperty(value = "车架号")
    private String frameNumber;

    @TableField(value = "storage_area")
    @ApiModelProperty(value = "存放区域")
    private String storageArea;

    @TableField(value = "next_inspection_time")
    @ApiModelProperty(value = "下次年检时间")
    private String nextInspectionTime;

    @TableField(value = "insurance_deadline")
    @ApiModelProperty(value = "保险截止日期")
    private String insuranceDeadline;

    @TableField(value = "prev_maintain_time")
    @ApiModelProperty(value = "上次保养日期")
    private String prevMaintainTime;

    @TableField(value = "vehicle_admin")
    @ApiModelProperty(value = "管理人")
    private String vehicleAdmin;

    @TableField(exist = false)
    @Property(value = "管理人")
    private Map<String, Object> vehicleAdminMation;

    @TableField(value = "state")
    @Property(value = "车辆状态", enumClass = VehicleState.class)
    private Integer state;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

}
