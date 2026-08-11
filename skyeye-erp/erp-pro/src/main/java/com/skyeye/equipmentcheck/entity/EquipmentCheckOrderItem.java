package com.skyeye.equipmentcheck.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.equipmentcheck.classenum.EquipmentCheckItemResult;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: EquipmentCheckOrderItem
 * @Description: 设备点检单明细实体类
 */
@Data
@TableName(value = "erp_equipment_check_order_item")
@ApiModel("设备点检单明细实体类")
public class EquipmentCheckOrderItem extends CommonInfo {

    @TableId("id")
    @Property(value = "主键id")
    private String id;

    @TableField("parent_id")
    @Property(value = "点检单id")
    private String parentId;

    @TableField("check_item")
    @ApiModelProperty(value = "检查项", required = "required")
    private String checkItem;

    @TableField("check_method")
    @ApiModelProperty(value = "检查方法", required = "required")
    private String checkMethod;

    @TableField("min_value")
    @ApiModelProperty(value = "最小值")
    private String minValue;

    @TableField("max_value")
    @ApiModelProperty(value = "最大值")
    private String maxValue;

    @TableField("check_value")
    @ApiModelProperty(value = "检查值")
    private String checkValue;

    @TableField("item_result")
    @ApiModelProperty(value = "检查结果", enumClass = EquipmentCheckItemResult.class, required = "required")
    private String itemResult;

    @TableField(exist = false)
    @Property(value = "检查结果信息")
    private Map<String, Object> itemResultMation;

    @TableField("sort_no")
    @ApiModelProperty(value = "排序号", defaultValue = "1")
    private Integer sortNo;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;
}

