/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.property.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import java.util.List;

import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: Property
 * @Description: 样式属性实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/26 20:02
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "report:property")
@TableName(value = "report_property", autoResultMap = true)
@ApiModel("样式属性实体类")
public class Property extends BaseGeneralInfo {

    @TableField(value = "attr_code")
    @ApiModelProperty(value = "样式属性", required = "required", fuzzyLike = true)
    private String attrCode;

    @TableField("editor_type")
    @ApiModelProperty(value = "展示类型，参考echarts的展示类型", required = "required,num")
    private Integer editorType;

    @TableField(value = "optional")
    @ApiModelProperty(value = "属性值是否可选", required = "required,num", enumClass = WhetherEnum.class)
    private Integer optional;

    @TableField(value = "default_value")
    @ApiModelProperty(value = "optional=0时，填写的默认值")
    private String defaultValue;

    @TableField(exist = false)
    @ApiModelProperty(value = "样式属性值", required = "json")
    private List<PropertyValue> propertyValueList;

}
