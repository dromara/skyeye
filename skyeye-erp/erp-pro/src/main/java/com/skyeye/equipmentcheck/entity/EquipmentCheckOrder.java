package com.skyeye.equipmentcheck.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.equipmentcheck.classenum.EquipmentCheckResult;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentCheckOrder
 * @Description: 设备点检单实体类
 */
@Data
@RedisCacheField(name = "erp:equipment:check:order")
@TableName(value = "erp_equipment_check_order", autoResultMap = true)
@ApiModel("设备点检单实体类")
public class EquipmentCheckOrder extends SkyeyeFlowable {

    @TableField(value = "odd_number", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "点检单编号")
    private String oddNumber;

    @TableField(value = "equipment_id")
    @ApiModelProperty(value = "设备id", required = "required")
    private String equipmentId;

    @TableField(exist = false)
    @Property(value = "设备信息")
    private Map<String, Object> equipmentMation;

    @TableField(value = "equipment_code")
    @ApiModelProperty(value = "设备编码")
    private String equipmentCode;

    @TableField(value = "equipment_name")
    @ApiModelProperty(value = "设备名称")
    private String equipmentName;

    @TableField(value = "standard_id")
    @ApiModelProperty(value = "点检标准id")
    private String standardId;

    @TableField(value = "standard_code")
    @ApiModelProperty(value = "点检标准编码")
    private String standardCode;

    @TableField(value = "standard_name")
    @ApiModelProperty(value = "点检标准名称")
    private String standardName;

    @TableField(value = "check_time")
    @ApiModelProperty(value = "点检时间", required = "required")
    private String checkTime;

    @TableField(value = "checker_id")
    @ApiModelProperty(value = "点检员id", required = "required")
    private String checkerId;

    @TableField(exist = false)
    @Property(value = "点检员信息")
    private Map<String, Object> checkerMation;

    @TableField(value = "check_result")
    @ApiModelProperty(value = "点检结果", enumClass = EquipmentCheckResult.class, required = "required,num")
    private Integer checkResult;

    @TableField(value = "equipment_state")
    @ApiModelProperty(value = "设备状态", required = "required")
    private String equipmentState;

    @TableField(value = "position")
    @ApiModelProperty(value = "定位")
    private String position;

    @TableField(value = "images")
    @ApiModelProperty(value = "拍照附件，多个逗号分隔")
    private String images;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "project_id")
    @ApiModelProperty(value = "项目id")
    private String projectId;

    @TableField(exist = false)
    @ApiModelProperty(value = "点检项目明细", required = "required,json")
    private List<EquipmentCheckOrderItem> itemList;
}

