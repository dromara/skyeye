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

/**
 * @ClassName: EquipmentInspectionOrderItem
 * @Description: 设备巡检单子表明细实体类
 */
@Data
@TableName(value = "erp_equipment_inspection_order_item_zcdemo")
@ApiModel("设备巡检单子表明细实体类")
public class EquipmentInspectionOrderItem extends SkyeyeLinkData {

    @TableField("order_id")
    @Property(value = "巡检单id")
    private String parentId;

    @TableField("line_no")
    @ApiModelProperty(value = "行号", required = "required,num")
    private Integer lineNo;

    @TableField("item_result")
    @ApiModelProperty(value = "检查结果", enumClass = EquipmentInspectionResultType.class, required = "num")
    private Integer itemResult;

    @TableField(exist = false)
    @Property(value = "检查结果名称")
    private String itemResultName;

    @TableField("result_value")
    @ApiModelProperty(value = "数值型结果")
    private BigDecimal resultValue;

    @TableField("item_photo_urls")
    @ApiModelProperty(value = "行级拍照URL，逗号分隔")
    private String itemPhotoUrls;

    @TableField("item_longitude")
    @ApiModelProperty(value = "行级定位经度")
    private String itemLongitude;

    @TableField("item_latitude")
    @ApiModelProperty(value = "行级定位纬度")
    private String itemLatitude;

    @TableField("item_address")
    @ApiModelProperty(value = "行级定位地址")
    private String itemAddress;

    @TableField("abnormal_flag")
    @ApiModelProperty(value = "是否异常 0否 1是", required = "required,num", defaultValue = "0")
    private Integer abnormalFlag;

    @TableField("remark")
    @ApiModelProperty(value = "行备注")
    private String remark;

    @TableField(exist = false)
    @Property(value = "方案检查项信息")
    private EquipmentInspectionPlanItem planItemMation;

}
