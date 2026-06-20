/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.repair.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.repair.classenum.EquipmentRepairEquipmentStatus;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: EquipmentScrapOrder
 * @Description: 设备报废单实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/04/21
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_equipment_scrap_order", autoResultMap = true)
@ApiModel("设备报废单实体类")
public class EquipmentScrapOrder extends SkyeyeFlowable {

    @TableField(value = "application_date")
    @ApiModelProperty(value = "申请日期")
    private String applicationDate;

    @TableField(value = "user_id")
    @ApiModelProperty(value = "申请人用户ID")
    private String userId;

    @TableField(exist = false)
    @Property(value = "申请人信息")
    private Map<String, Object> userMation;

    @TableField(value = "equipment_id")
    @ApiModelProperty(value = "设备id", required = "required")
    private String equipmentId;

    @TableField(exist = false)
    @Property(value = "设备信息")
    private Map<String, Object> equipmentMation;

    @TableField(value = "equipment_status")
    @ApiModelProperty(value = "设备状态", enumClass = EquipmentRepairEquipmentStatus.class)
    private Integer equipmentStatus;

    @TableField(value = "staff_id")
    @ApiModelProperty(value = "设备负责人员工ID")
    private String staffId;

    @TableField(exist = false)
    @Property(value = "设备负责人信息")
    private Map<String, Object> staffMation;

    @TableField(value = "scrap_reason")
    @ApiModelProperty(value = "报废原因", fuzzyLike = true)
    private String scrapReason;


}
