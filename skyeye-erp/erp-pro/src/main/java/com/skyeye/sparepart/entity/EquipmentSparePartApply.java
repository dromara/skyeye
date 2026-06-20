/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sparepart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.sparepart.classenum.EquipmentSparePartRequisitionPurpose;
import lombok.Data;

import java.util.List;

/**
 * 设备备件-申领单
 */
@Data
@RedisCacheField(name = "erp:sparepart:apply", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "erp_equipment_spare_part_apply")
@ApiModel("设备备件-申领单")
public class EquipmentSparePartApply extends SkyeyeFlowable {

    @TableField(value = "repair_order_id")
    @ApiModelProperty(value = "来源单据ID（设备维修单/设备保养单）")
    private String repairOrderId;

    @TableField(value = "apply_purpose")
    @ApiModelProperty(value = "申领目的：1-设备维修、2-设备保养", enumClass = EquipmentSparePartRequisitionPurpose.class, required = "num")
    private Integer applyPurpose;

    @TableField(value = "apply_time")
    @ApiModelProperty(value = "申领日期", required = "required")
    private String applyTime;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField(value = "all_price")
    @ApiModelProperty(value = "总金额")
    private String allPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "备件申领明细", required = "required,json")
    private List<EquipmentSparePartApplyLink> applyLinkList;

    @TableField("other_state")
    @Property("出入库状态")
    private Integer otherState;

}
