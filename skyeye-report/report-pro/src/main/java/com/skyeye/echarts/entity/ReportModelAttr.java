/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.echarts.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: ReportModelAttr
 * @Description: Echarts报表模型属性
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/20 16:22
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "report_model_attr", autoResultMap = true)
@ApiModel("Echarts报表模型属性实体类")
public class ReportModelAttr extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("report_model_id")
    @ApiModelProperty(value = "模型id")
    private String reportModelId;

    @TableField("attr_code")
    @ApiModelProperty(value = "属性id，也是属性code", required = "required")
    private String attrCode;

    @TableField("type_name")
    @ApiModelProperty(value = "属性分类名称")
    private String typeName;

    @TableField("name")
    @ApiModelProperty(value = "属性名称", required = "required")
    private String name;

    @TableField("remark")
    @ApiModelProperty(value = "介绍")
    private String remark;

    @TableField("default_value")
    @ApiModelProperty(value = "默认值")
    private String defaultValue;

    @TableField("edit")
    @ApiModelProperty(value = "是否可编辑", required = "required")
    private String edit;

    @TableField("editor_type")
    @ApiModelProperty(value = "编辑器")
    private String editorType;

    @TableField("optional_value")
    @ApiModelProperty(value = "可选值")
    private String optionalValue;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序", required = "required,num")
    private Integer orderBy;

}
