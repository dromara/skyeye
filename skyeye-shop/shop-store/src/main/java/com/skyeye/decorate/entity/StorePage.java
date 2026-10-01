/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.decorate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.decorate.enums.StorePageType;
import lombok.Data;

import java.util.List;

/**
 * 门店装修页
 */
@Data
@TableName(value = "shop_store_page")
@ApiModel("门店装修页")
public class StorePage extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "store_id")
    @ApiModelProperty(value = "门店id", required = "required")
    private String storeId;

    @TableField(value = "page_type")
    @ApiModelProperty(value = "页面类型", enumClass = StorePageType.class, required = "required,num")
    private Integer pageType;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "页面名称")
    private String name;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(exist = false)
    @Property(value = "页面组件列表")
    private List<StorePageComp> compList;
}
