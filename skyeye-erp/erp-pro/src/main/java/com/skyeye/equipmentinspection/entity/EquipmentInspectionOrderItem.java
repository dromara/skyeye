/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import com.skyeye.equipmentinspection.classenum.EquipmentInspectionResultType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @ClassName: EquipmentInspectionOrderItem
 * @Description: 设备巡检单子表明细实体类
 */
@Data
@TableName(value = "erp_equipment_inspection_order_item")
@ApiModel("设备巡检单子表明细实体类")
public class EquipmentInspectionOrderItem extends SkyeyeLinkData {

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序，对应方案itemId顺序", required = "required,num")
    private Integer orderBy;

    @TableField("item_result")
    @ApiModelProperty(value = "检查结果", enumClass = EquipmentInspectionResultType.class, required = "num")
    private Integer itemResult;

    @TableField(exist = false)
    @Property(value = "检查结果信息")
    private Map<String, Object> itemResultMation;

    @TableField("result_value")
    @ApiModelProperty(value = "数值型结果")
    private BigDecimal resultValue;

    @TableField("item_photo_urls")
    @ApiModelProperty(value = "拍照URL，逗号分隔")
    private String itemPhotoUrls;

    @TableField("item_longitude")
    @ApiModelProperty(value = "定位经度")
    private String itemLongitude;

    @TableField("item_latitude")
    @ApiModelProperty(value = "定位纬度")
    private String itemLatitude;

    @TableField("item_address")
    @ApiModelProperty(value = "定位地址")
    private String itemAddress;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @Property(value = "巡检项目信息")
    private EquipmentInspectionItem itemMation;

}
