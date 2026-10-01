/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.decorate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.decorate.enums.StorePageCompType;
import lombok.Data;

import java.util.Map;

/**
 * 门店装修页组件
 */
@Data
@TableName(value = "shop_store_page_comp", autoResultMap = true)
@ApiModel("门店装修页组件")
public class StorePageComp extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "page_id")
    @ApiModelProperty(value = "装修页id")
    private String pageId;

    @TableField(value = "store_id")
    @ApiModelProperty(value = "门店id", required = "required")
    private String storeId;

    @TableField(value = "comp_type")
    @ApiModelProperty(value = "组件类型", enumClass = StorePageCompType.class, required = "required,num")
    private Integer compType;

    @TableField(value = "title")
    @ApiModelProperty(value = "组件标题")
    private String title;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "序号", required = "required,num")
    private Integer orderBy;

    @TableField(value = "show_pc")
    @ApiModelProperty(value = "PC端展示", enumClass = WhetherEnum.class, required = "num")
    private Integer showPc;

    @TableField(value = "show_app")
    @ApiModelProperty(value = "移动端展示", enumClass = WhetherEnum.class, required = "num")
    private Integer showApp;

    @TableField(value = "config_json", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "组件配置", required = "json")
    private Map<String, Object> configJson;
}
