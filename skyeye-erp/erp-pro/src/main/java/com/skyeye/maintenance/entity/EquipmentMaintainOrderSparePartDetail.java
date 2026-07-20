/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

@Data
@TableName(value = "erp_equipment_maintain_order_spare_part_detail")
@ApiModel("保养单备件使用明细实体类")
public class EquipmentMaintainOrderSparePartDetail extends CommonInfo {

    @TableId("id")
    @Property(value = "主键id")
    private String id;

    @TableField("parent_id")
    @ApiModelProperty(value = "父类id(设备保养单id)")
    private String parentId;

    @TableField(value = "material_id")
    @ApiModelProperty(value = "商品ID", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Map<String, Object> materialMation;

    @TableField("norms_id")
    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private Map<String, Object> normsMation;

    @TableField(value = "oper_number")
    @ApiModelProperty(value = "使用数量", required = "required,num")
    private String operNumber;

    @TableField(value = "usage_reason")
    @ApiModelProperty(value = "使用原因")
    private String usageReason;

    @TableField(value = "unit_price")
    @ApiModelProperty(value = "出库单价", required = "double", defaultValue = "0")
    private String unitPrice;

    @TableField(value = "amount")
    @ApiModelProperty(value = "总金额(元)", defaultValue = "0")
    private String allPrice;

    @TableField("create_id")
    @Property(value = "使用人ID")
    private String createId;

    @TableField("create_time")
    @Property(value = "创建时间")
    private String createTime;

    @TableField(exist = false)
    @Property(value = "当前登录人库存信息")
    private Map<String, Object> serviceUserStock;

}
