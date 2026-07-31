/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.attr.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.attr.classenum.AttrDefinitionAttrType;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.enumeration.AttrModelType;
import com.skyeye.common.enumeration.FieldType;
import com.skyeye.common.enumeration.ServiceBeanType;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

import java.util.List;


/**
 * @ClassName: AttrDefinition
 * @Description: 服务类属性实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/18 13:11
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "skyeye_attr_definition", autoResultMap = true)
@ApiModel("服务类属性实体类")
public class AttrDefinition extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id")
    private String id;

    @TableField(exist = false)
    @Property("自定义属性信息")
    private AttrDefinitionCustom attrDefinitionCustom;

    @TableField(exist = false)
    @Property("子属性列表")
    private List<AttrDefinition> childAttrDefinitions;

    @TableField("app_id")
    @ApiModelProperty(value = "应用的APPID")
    private String appId;

    @TableField("class_name")
    @ApiModelProperty(value = "服务类的className", required = "required")
    private String className;

    @TableField("attr_key")
    @ApiModelProperty(value = "字段名", required = "required")
    private String attrKey;

    @TableField("attr_type")
    @ApiModelProperty(value = "字段类型", enumClass = AttrDefinitionAttrType.class, required = "required")
    private String attrType;

    @TableField("attr_model_type")
    @ApiModelProperty(value = "属性模型结构类型：简单类型/对象/集合", enumClass = AttrModelType.class, required = "required,num", defaultValue = "1")
    private Integer attrModelType;

    @TableField("`name`")
    @ApiModelProperty(value = "属性名称", required = "required")
    private String name;

    @TableField("remark")
    @ApiModelProperty(value = "属性描述")
    private String remark;

    @TableField("whether_input_params")
    @ApiModelProperty(value = "是否可以作为入参", enumClass = WhetherEnum.class, required = "required")
    private Integer whetherInputParams;

    @TableField("enum_class_str")
    @ApiModelProperty(value = "属性对应的枚举类地址，例如：skyeye-pro#com.skyeye.app.enums.AppReleaseStatusEnum")
    private String enumClassStr;

    @TableField("is_unique_field")
    @ApiModelProperty(value = "是否唯一", enumClass = WhetherEnum.class, defaultValue = "0")
    private Integer isUniqueField;

    @TableField("is_fuzzy_like")
    @ApiModelProperty(value = "是否模糊匹配", enumClass = WhetherEnum.class, defaultValue = "0")
    private Integer isFuzzyLike;

    @TableField("required")
    @ApiModelProperty(value = "属性限制条件")
    private String required;

    @TableField("model_attribute")
    @ApiModelProperty(value = "是否是模型属性", enumClass = WhetherEnum.class, required = "required")
    private Integer modelAttribute;

    @TableField("db_field_name")
    @ApiModelProperty(value = "对应得数据库表得字段名")
    private String dbFieldName;

    @TableField("field_type")
    @ApiModelProperty(value = "对应得数据库表得字段类型", enumClass = FieldType.class)
    private String fieldType;

    @TableField("field_length")
    @ApiModelProperty(value = "对应得数据库表得字段长度")
    private String fieldLength;

    @TableField("decimal_places")
    @ApiModelProperty(value = "对应得数据库表得字段小数位数")
    private Integer decimalPlaces;

    @TableField("db_default_value")
    @ApiModelProperty(value = "对应得数据库表得字段默认值")
    private String dbDefaultValue;

    @TableField("is_primary_key")
    @ApiModelProperty(value = "是否是主键", enumClass = WhetherEnum.class)
    private Integer isPrimaryKey;

    @Property("创建时间")
    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    private String createTime;

    @Property("最后更新日期")
    @TableField(value = "last_update_time")
    private String lastUpdateTime;

    @TableField("`type`")
    @ApiModelProperty(value = "业务对象类型", enumClass = ServiceBeanType.class)
    private Integer type;

    @TableField(exist = false)
    @ApiModelProperty("服务名(中文名称)")
    private String applicationName;

    @TableField(exist = false)
    @ApiModelProperty(value = "表名称")
    private String tableName;

}
