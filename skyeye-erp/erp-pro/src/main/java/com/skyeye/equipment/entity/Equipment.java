/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.equipment.classenum.EquipmentState;
import com.skyeye.farm.entity.Farm;
import com.skyeye.supplier.entity.Supplier;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: Equipment
 * @Description: 设备管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/17 21:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"name", "model", "serialNumber"})
@RedisCacheField(name = "mes:equipment")
@TableName(value = "erp_equipment")
@ApiModel("设备管理实体类")
public class Equipment extends BaseGeneralInfo {

    @TableField(value = "odd_number")
    @ApiModelProperty(value = "设备编码", required = "required")
    private String oddNumber;

    @TableField(value = "equipment_type_id")
    @ApiModelProperty(value = "设备类型id")
    private String equipmentTypeId;

    @TableField(value = "equipment_type_name")
    @ApiModelProperty(value = "设备类型名称")
    private String equipmentTypeName;

    @TableField(value = "equipment_state")
    @ApiModelProperty(value = "设备状态", enumClass = EquipmentState.class, required = "required,num")
    private Integer equipmentState;

    @TableField(value = "equipment_img")
    @ApiModelProperty(value = "设备图片")
    private String equipmentImg;

    @TableField(value = "technical_manual")
    @ApiModelProperty(value = "设备技术手册")
    private String technicalManual;

    @TableField(value = "model")
    @ApiModelProperty(value = "规格", required = "required", fuzzyLike = true)
    private String model;

    @TableField(value = "serial_number")
    @ApiModelProperty(value = "序列号", required = "required", fuzzyLike = true)
    private String serialNumber;

    @TableField(value = "manufacturer")
    @ApiModelProperty(value = "生产厂家", required = "required")
    private String manufacturer;

    @TableField(value = "buy_time")
    @ApiModelProperty(value = "购买日期", required = "required")
    private String buyTime;

    @TableField(value = "enable_time")
    @ApiModelProperty(value = "启用日期")
    private String enableTime;

    @TableField(value = "operating_hours")
    @ApiModelProperty(value = "每日运转时长")
    private String operatingHours;

    @TableField(value = "quota_capacity")
    @ApiModelProperty(value = "定额能力(h)")
    private String quotaCapacity;

    @TableField(value = "max_capacity")
    @ApiModelProperty(value = "最大能力(h)")
    private String maxCapacity;

    @TableField(value = "farm_id")
    @ApiModelProperty(value = "车间id", required = "required")
    private String farmId;

    @TableField(value = "responsible_id")
    @ApiModelProperty(value = "设备负责人id")
    private String responsibleId;

    @TableField(value = "unit_price")
    @ApiModelProperty(value = "单价",required = "required")
    private String unitPrice;

    @TableField(value = "project_id")
    @ApiModelProperty(value = "项目id")
    private String projectId;

    @TableField(value = "supplier_id")
    @ApiModelProperty(value = "供应商id")
    private String supplierId;

    @TableField(exist = false)
    @Property(value = "项目信息")
    private Map<String,Object> projectMation;

    @TableField(exist = false)
    @Property(value = "车间信息")
    private Farm farmMation;

    @TableField(exist = false)
    @Property(value = "设备负责人信息")
    private Map<String, Object> responsibleMation;

    @TableField(exist = false)
    @Property(value = "供应商信息")
    private Supplier supplierMation;

}
