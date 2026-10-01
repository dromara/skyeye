/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.decorate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

/**
 * 门店轮播（对标平台 Adsense，挂 storeId）
 */
@Data
@TableName(value = "shop_store_banner")
@ApiModel("门店轮播")
public class StoreBanner extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "store_id")
    @ApiModelProperty(value = "门店id", required = "required")
    private String storeId;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "标题", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "jump_url")
    @ApiModelProperty(value = "跳转链接")
    private String jumpUrl;

    @TableField(value = "pc_logo")
    @ApiModelProperty(value = "PC端图")
    private String pcLogo;

    @TableField(value = "app_logo")
    @ApiModelProperty(value = "移动端图")
    private String appLogo;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "序号", required = "required,num")
    private Integer orderBy;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;
}
