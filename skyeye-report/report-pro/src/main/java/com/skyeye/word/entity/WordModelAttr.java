/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.word.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: WordModelAttr
 * @Description: 文字模型属性实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/26 15:20
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@TableName(value = "report_word_model_attr", autoResultMap = true)
@ApiModel("文字模型属性实体类")
public class WordModelAttr extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("word_id")
    @Property(value = "文字模型id")
    private String wordId;

    @TableField("property_id")
    @ApiModelProperty(value = "属性id", required = "required")
    private String propertyId;

    @TableField(exist = false)
    @Property(value = "属性信息")
    private com.skyeye.property.entity.Property propertyMation;

    @TableField("editor")
    @ApiModelProperty(value = "是否可编辑", required = "required,num", enumClass = WhetherEnum.class)
    private Integer editor;

    @TableField("show_to_editor")
    @ApiModelProperty(value = "是否显示在编辑框", required = "required,num", enumClass = WhetherEnum.class)
    private Integer showToEditor;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序", required = "required,num")
    private Integer orderBy;

}
