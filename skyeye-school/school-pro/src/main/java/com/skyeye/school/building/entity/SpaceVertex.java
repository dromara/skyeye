package com.skyeye.school.building.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.school.building.floorenum.SpaceOwnerTypeEnum;
import lombok.Data;

/**
 * 楼栋或房间的经纬度轮廓顶点
 */
@Data
@TableName("school_space_vertex")
@ApiModel(description = "空间轮廓顶点")
public class SpaceVertex extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id，为空时新增，不为空时编辑")
    private String id;

    @TableField("owner_type")
    @ApiModelProperty(value = "归属类型，1楼栋 2房间", required = "required", enumClass = SpaceOwnerTypeEnum.class)
    private Integer ownerType;

    @TableField("owner_id")
    @ApiModelProperty(value = "归属id，楼栋为地点id，房间为楼层房间id", required = "required")
    private String ownerId;

    @TableField("order_num")
    @ApiModelProperty(value = "顶点顺序，从1开始")
    private Integer orderNum;

    @TableField("longitude")
    @ApiModelProperty(value = "经度", required = "required")
    private String longitude;

    @TableField("latitude")
    @ApiModelProperty(value = "纬度", required = "required")
    private String latitude;
}
