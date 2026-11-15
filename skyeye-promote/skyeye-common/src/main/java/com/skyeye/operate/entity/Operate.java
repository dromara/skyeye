/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.operate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.attr.classenum.ButtonColorType;
import com.skyeye.business.entity.BusinessApi;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.operate.classenum.EventType;
import com.skyeye.operate.classenum.OperatePosition;
import com.skyeye.operate.classenum.PageModalSize;
import com.skyeye.operate.classenum.PageOpenType;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: Operate
 * @Description: 操作管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/1/29 18:07
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "skyeye:operate")
@TableName(value = "skyeye_operate", autoResultMap = true)
@ApiModel("操作管理实体类")
public class Operate extends BaseGeneralInfo {

    @TableField("position")
    @ApiModelProperty(value = "展示位置", enumClass = OperatePosition.class, required = "required")
    private String position;

    @TableField("color")
    @ApiModelProperty(value = "操作按钮的颜色，展示位置为操作栏时必填", enumClass = ButtonColorType.class)
    private String color;

    @TableField("auth_point_num")
    @ApiModelProperty(value = "权限控制编号")
    private String authPointNum;

    @TableField("app_auth_point_num")
    @ApiModelProperty(value = "移动端权限控制编号")
    private String appAuthPointNum;

    @TableField("event_type")
    @ApiModelProperty(value = "事件类型", enumClass = EventType.class, required = "required")
    private String eventType;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序，值越大越往后", required = "required,num")
    private Integer orderBy;

    @TableField("class_name")
    @ApiModelProperty(value = "服务类的className", required = "required")
    private String className;

    @TableField("app_id")
    @ApiModelProperty(value = "应用的appId", required = "required")
    private String appId;

    @TableField(exist = false)
    @ApiModelProperty(value = "当事件类型为请求事件时，填写的接口信息")
    private BusinessApi businessApi;

    @TableField(value = "open_type")
    @ApiModelProperty(value = "页面布局打开方式", enumClass = PageOpenType.class)
    private Integer openType;

    @TableField(value = "modal_size")
    @ApiModelProperty(value = "弹窗尺寸，事件类型为新开页面且以弹窗打开时生效", enumClass = PageModalSize.class)
    private Integer modalSize;

    @TableField(exist = false)
    @ApiModelProperty(value = "当事件类型为新开页面时，填写的页面/布局信息")
    private OperateOpenPage operateOpenPage;

    @TableField(value = "show_condition", typeHandler = OperateListTypeHandler.class)
    @ApiModelProperty(value = "操作按钮显示的条件")
    private List<OperateShowCondition> showConditionList;

}
