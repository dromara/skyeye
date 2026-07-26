/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: PropertyValue
 * @Description: 样式属性值实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/26 20:02
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "report_property_value", autoResultMap = true)
@ApiModel("样式属性值实体类")
public class PropertyValue extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("property_id")
    @com.skyeye.annotation.api.Property(value = "属性id")
    private String propertyId;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "属性值标题")
    private String name;

    @TableField(value = "value")
    @ApiModelProperty(value = "属性值", required = "required")
    private String value;

    @TableField("default_choose")
    @ApiModelProperty(value = "是否是默认值", required = "required,num", enumClass = WhetherEnum.class)
    private Integer defaultChoose;

    @TableField(value = "order_by")
    @com.skyeye.annotation.api.Property(value = "排序")
    private Integer orderBy;

}
