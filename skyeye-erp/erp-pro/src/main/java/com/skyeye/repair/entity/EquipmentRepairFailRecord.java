/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

/**
 * 设备维修失败履历：仅记录未修复轮次的维修人及完成情况，供详情展示。
 */
@Data
@TableName(value = "erp_equipment_repair_fail_record")
@ApiModel("设备维修失败履历实体类")
public class EquipmentRepairFailRecord extends CommonInfo {

    @TableId("id")
    @Property(value = "主键id")
    private String id;

    @TableField("parent_id")
    @ApiModelProperty(value = "维修单id")
    private String parentId;

    @TableField("service_user_id")
    @ApiModelProperty(value = "维修人id")
    private String serviceUserId;

    @TableField(exist = false)
    @Property(value = "维修人信息")
    private Map<String, Object> serviceUserMation;

    @TableField("fail_time")
    @ApiModelProperty(value = "未修复时间")
    private String failTime;

    @TableField("repair_desc")
    @ApiModelProperty(value = "完成情况（维修情况说明）")
    private String repairDesc;

}
