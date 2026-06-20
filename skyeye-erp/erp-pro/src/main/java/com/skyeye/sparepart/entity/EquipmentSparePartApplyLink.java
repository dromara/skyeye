/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import lombok.Data;

import java.util.Map;

/**
 * 设备备件-申领明细
 */
@Data
@TableName(value = "erp_equipment_spare_part_apply_link")
@ApiModel("设备备件-申领明细")
public class EquipmentSparePartApplyLink extends SkyeyeLinkData {

    @TableField(value = "material_id")
    @ApiModelProperty(value = "商品id", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Map<String, Object> materialMation;

    @TableField(value = "norms_id")
    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private Map<String, Object> normsMation;

    @TableField("oper_number")
    @ApiModelProperty(value = "申领数量", required = "required,num")
    private String operNumber;

    @TableField("unit_price")
    @ApiModelProperty(value = "单价", defaultValue = "0")
    private String unitPrice;

    @TableField("all_price")
    @ApiModelProperty(value = "总金额", defaultValue = "0")
    private String allPrice;

    @TableField(value = "depot_id")
    @ApiModelProperty(value = "仓库id", required = "required")
    private String depotId;

    @TableField(exist = false)
    @Property(value = "仓库信息")
    private Map<String, Object> depotMation;

}
