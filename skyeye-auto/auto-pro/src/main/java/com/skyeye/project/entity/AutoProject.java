/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.project.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.product.entity.AutoProduct;
import lombok.Data;

import com.skyeye.project.classenum.AutoProjectState;

/**
 * @ClassName: AutoProject
 * @Description: 项目管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/20 19:27
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "auto:project", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "auto_project", autoResultMap = true)
@ApiModel("项目管理实体类")
public class AutoProject extends BaseGeneralInfo {

    @TableField("product_id")
    @ApiModelProperty(value = "产品id", required = "required")
    private String productId;

    @TableField(exist = false)
    @Property("产品信息")
    private AutoProduct productMation;

    @TableField("state")
    @ApiModelProperty(value = "状态", required = "required", enumClass = AutoProjectState.class)
    private String state;

    @TableField(value = "team_template_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "团队模板id")
    private String teamTemplateId;

}
