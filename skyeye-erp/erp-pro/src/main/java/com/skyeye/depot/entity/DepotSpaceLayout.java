/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: DepotSpaceLayout
 * @Description: 仓库 3D 空间布局
 */
@Data
@UniqueField(value = {"depotId", "levelValId"})
@TableName(value = "erp_depot_space_layout", autoResultMap = true)
@ApiModel("仓库3D空间布局")
public class DepotSpaceLayout extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "depot_id")
    @ApiModelProperty(value = "仓库id", required = "required")
    private String depotId;

    @TableField(value = "level_val_id")
    @ApiModelProperty(value = "库位值id", required = "required")
    private String levelValId;

    @TableField(value = "floor_no")
    @ApiModelProperty(value = "第几层/楼层号", required = "num")
    private Integer floorNo;

    @TableField(value = "area")
    @ApiModelProperty(value = "面积(㎡)")
    private String area;

    @TableField(value = "pos_x")
    @ApiModelProperty(value = "X坐标")
    private String posX;

    @TableField(value = "pos_y")
    @ApiModelProperty(value = "Y坐标")
    private String posY;

    @TableField(value = "pos_z")
    @ApiModelProperty(value = "Z坐标")
    private String posZ;

    @TableField(value = "size_w")
    @ApiModelProperty(value = "宽度")
    private String sizeW;

    @TableField(value = "size_h")
    @ApiModelProperty(value = "高度")
    private String sizeH;

    @TableField(value = "size_d")
    @ApiModelProperty(value = "深度")
    private String sizeD;

    @TableField(value = "rotate_y")
    @ApiModelProperty(value = "绕Y轴旋转角度")
    private String rotateY;

    @TableField(value = "range_json")
    @ApiModelProperty(value = "分区范围JSON")
    private String rangeJson;

    @TableField(value = "display_color")
    @ApiModelProperty(value = "显示颜色(#RRGGBB)")
    private String displayColor;

    @TableField(value = "shelf_levels")
    @ApiModelProperty(value = "货架层数", required = "num")
    private Integer shelfLevels;

    @TableField(value = "shelf_height")
    @ApiModelProperty(value = "货架层高(米)")
    private String shelfHeight;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

}
