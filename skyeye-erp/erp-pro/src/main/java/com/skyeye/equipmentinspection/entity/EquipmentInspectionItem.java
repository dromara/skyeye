/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

/**
 * @ClassName: EquipmentInspectionItem
 * @Description: 设备巡检项目实体类（主数据，方案通过 itemId 引用）
 */
@Data
@RedisCacheField(name = "erp:equipmentInspectionItem", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "erp_equipment_inspection_item")
@ApiModel("设备巡检项目实体类")
public class EquipmentInspectionItem extends BaseGeneralInfo {

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "项目编号", fuzzyLike = true)
    private String oddNumber;

    @TableField("check_method")
    @ApiModelProperty(value = "检查方法")
    private String checkMethod;

    @TableField("result_type")
    @ApiModelProperty(value = "结果类型", defaultValue = "text")
    private String resultType;

    @TableField("unit")
    @ApiModelProperty(value = "结果单位")
    private String unit;

    @TableField("required_flag")
    @ApiModelProperty(value = "是否必检", enumClass = WhetherEnum.class, required = "required,num", defaultValue = "1")
    private Integer requiredFlag;

    @TableField("abnormal_rule")
    @ApiModelProperty(value = "异常判定规则")
    private String abnormalRule;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

}
