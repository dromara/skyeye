/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: SysEveUserMenuFavorite
 * @Description: 用户APP菜单收藏
 */
@Data
@TableName(value = "sys_eve_user_menu_favorite")
@ApiModel("用户APP菜单收藏")
public class SysEveUserMenuFavorite extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("user_id")
    @Property(value = "用户id")
    private String userId;

    @TableField("menu_id")
    @ApiModelProperty(value = "菜单id", required = "required")
    private String menuId;

    @TableField("tenant_id")
    @Property(value = "租户id")
    private String tenantId;

    @TableField("order_by")
    @ApiModelProperty(value = "排序，值越小越靠前", required = "required,num")
    private Integer orderBy;

}
