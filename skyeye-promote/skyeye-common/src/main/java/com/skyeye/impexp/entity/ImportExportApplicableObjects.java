/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.enumeration.ApplicableObjectsType;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: ImportExportApplicableObjects
 * @Description: 导入导出模板适用对象
 * @author: skyeye云系列--卫志强
 * @date: 2026/9/10
 */
@Data
@ApiModel("导入导出模板适用对象")
@TableName(value = "skyeye_import_export_applicable_objects")
public class ImportExportApplicableObjects extends CommonInfo {

    @TableId("id")
    @Property("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("config_id")
    @Property(value = "导入导出配置id")
    private String configId;

    @TableField("object_id")
    @ApiModelProperty(value = "适用对象id", required = "required")
    private String objectId;

    @TableField("object_type")
    @ApiModelProperty(value = "适用对象类型", required = "required,num", enumClass = ApplicableObjectsType.class)
    private Integer objectType;

    @TableField("object_name")
    @ApiModelProperty(value = "适用对象名称（展示缓存）")
    private String objectName;

    @TableField(exist = false)
    @Property(value = "适用对象信息")
    private Map<String, Object> objectMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "适用对象名称（前端提交兼容字段）")
    private String name;
}
