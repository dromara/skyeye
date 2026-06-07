/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipmentinspection.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName: EquipmentInspectionPlanItem
 * @Description: 设备巡检方案检查项实体类
 */
@Data
@TableName(value = "erp_equipment_inspection_plan_item_zcdemo")
@ApiModel("设备巡检方案子表明细实体类")
public class EquipmentInspectionPlanItem extends SkyeyeLinkData {

    @TableField("line_no")
    @ApiModelProperty(value = "行号", required = "required,num")
    private Integer lineNo;

    @TableField("item_name")
    @ApiModelProperty(value = "检查项名称", required = "required")
    private String itemName;

    @TableField("check_method")
    @ApiModelProperty(value = "检查方法")
    private String checkMethod;

    @TableField("result_type")
    @ApiModelProperty(value = "结果类型", defaultValue = "text")
    private String resultType;

    @TableField("min_value")
    @ApiModelProperty(value = "最小值")
    private BigDecimal minValue;

    @TableField("max_value")
    @ApiModelProperty(value = "最大值")
    private BigDecimal maxValue;

    @TableField("unit")
    @ApiModelProperty(value = "结果单位")
    private String unit;

    @TableField("required_flag")
    @ApiModelProperty(value = "是否必检 0否 1是", required = "required,num", defaultValue = "1")
    private Integer requiredFlag;

    @TableField("abnormal_rule")
    @ApiModelProperty(value = "异常判定规则")
    private String abnormalRule;

    @TableField("remark")
    @ApiModelProperty(value = "行备注")
    private String remark;

}
