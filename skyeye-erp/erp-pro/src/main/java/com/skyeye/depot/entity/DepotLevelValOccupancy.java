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
 * 库位占用：供 3D 占用色 LIVE；与规格库存（仓+norms）并行，后续出入库挂库位时回写。
 */
@Data
@UniqueField(value = {"depotId", "levelValId"})
@TableName(value = "erp_depot_level_val_occupancy", autoResultMap = true)
@ApiModel("仓库库位占用")
public class DepotLevelValOccupancy extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "depot_id")
    @ApiModelProperty(value = "仓库id", required = "required")
    private String depotId;

    @TableField(value = "level_val_id")
    @ApiModelProperty(value = "库位值id", required = "required")
    private String levelValId;

    @TableField(value = "qty")
    @ApiModelProperty(value = "当前数量", required = "required")
    private String qty;

    @TableField(value = "capacity")
    @ApiModelProperty(value = "库位容量", required = "required")
    private String capacity;

    @TableField(value = "warn")
    @ApiModelProperty(value = "是否预警 0否 1是", required = "num")
    private Integer warn;

}
