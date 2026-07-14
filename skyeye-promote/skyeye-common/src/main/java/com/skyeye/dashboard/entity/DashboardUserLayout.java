/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dashboard.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.IsDefaultEnum;
import lombok.Data;

/**
 * @ClassName: DashboardUserLayout
 * @Description: 用户仪表盘布局
 */
@Data
@TableName(value = "dashboard_user_layout")
@ApiModel("用户仪表盘布局")
public class DashboardUserLayout extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("user_id")
    @Property(value = "用户id")
    private String userId;

    @TableField("`name`")
    @ApiModelProperty(value = "布局名称", required = "required")
    private String name;

    @TableField("content")
    @ApiModelProperty(value = "布局JSON", required = "required,json")
    private String content;

    @TableField("is_default")
    @ApiModelProperty(value = "是否默认布局", enumClass = IsDefaultEnum.class, required = "num")
    private Integer isDefault;

}
