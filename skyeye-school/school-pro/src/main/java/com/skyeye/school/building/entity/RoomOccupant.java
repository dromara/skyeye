package com.skyeye.school.building.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.school.building.floorenum.RoomPersonTypeEnum;
import lombok.Data;

/**
 * 房间内的系统人员及职责
 */
@Data
@TableName("school_room_occupant")
@ApiModel(description = "房间人员")
public class RoomOccupant extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id，为空时新增，不为空时编辑")
    private String id;

    @TableField("floor_info_id")
    @ApiModelProperty(value = "房间id", required = "required")
    private String floorInfoId;

    @TableField("person_type")
    @ApiModelProperty(value = "人员类型，1教职工 2学生", required = "required", enumClass = RoomPersonTypeEnum.class)
    private Integer personType;

    @TableField("person_id")
    @ApiModelProperty(value = "人员id，教职工为用户id，学生为学生id", required = "required")
    private String personId;

    @TableField("duty")
    @ApiModelProperty(value = "职责，如办公、上课、值班", required = "required")
    private String duty;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "人员姓名")
    private String personName;
}
