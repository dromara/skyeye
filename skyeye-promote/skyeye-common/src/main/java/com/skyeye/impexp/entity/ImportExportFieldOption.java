/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: ImportExportFieldOption
 * @Description: 导入导出字段可选项
 * @author: skyeye云系列--卫志强
 * @date: 2026/4/8 22:05
 */
@Data
@ApiModel("导入导出字段可选项")
public class ImportExportFieldOption {

    @ApiModelProperty("字段key")
    private String attrKey;

    @ApiModelProperty("字段显示名称")
    private String name;

    @ApiModelProperty("字段类型（业务定义）")
    private String attrType;

    @ApiModelProperty("数据库字段类型")
    private String fieldType;

    @ApiModelProperty("属性模型结构类型：1简单类型 2对象 3集合")
    private Integer attrModelType;

    @ApiModelProperty("父属性key；对象/集合拆列时子字段为父key")
    private String parentAttrKey;

    @ApiModelProperty("父属性模型类型：2对象 3集合；子字段时有值")
    private Integer parentAttrModelType;

    @ApiModelProperty("展示层级，0为顶级，1为对象/集合子字段")
    private Integer depth;

    @ApiModelProperty("是否可作为入参")
    private Integer whetherInputParams;

    @ApiModelProperty("是否默认勾选导入")
    private Boolean defaultImportChecked;

    @ApiModelProperty("导入时是否必填且不可取消")
    private Boolean importRequiredFixed;

    @ApiModelProperty("是否默认勾选导出")
    private Boolean defaultExportChecked;

    @ApiModelProperty("属性描述")
    private String remark;
}

