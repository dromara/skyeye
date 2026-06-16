/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.AreaInfo;
import lombok.Data;

/**
 * @ClassName: CheckWorkTimePoint
 * @Description: 考勤班次线上打卡点位实体类
 */
@Data
@TableName(value = "check_work_time_point", autoResultMap = true)
@ApiModel("考勤班次线上打卡点位实体类")
public class CheckWorkTimePoint extends AreaInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(value = "time_id")
    @ApiModelProperty(value = "班次id", required = "required")
    private String timeId;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "点位名称")
    private String name;

    @TableField(value = "longitude")
    @ApiModelProperty(value = "经度", required = "required")
    private String longitude;

    @TableField(value = "latitude")
    @ApiModelProperty(value = "纬度", required = "required")
    private String latitude;

    @TableField(value = "radius")
    @ApiModelProperty(value = "打卡范围(米)", defaultValue = "500")
    private Integer radius;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序", defaultValue = "1")
    private Integer orderBy;

}
