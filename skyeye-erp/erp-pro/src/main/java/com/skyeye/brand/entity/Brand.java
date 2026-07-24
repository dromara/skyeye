/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.brand.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

import com.skyeye.common.enumeration.EnableEnum;

/**
 * @ClassName: Brand
 * @Description: 品牌管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/17 21:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "erp:brand")
@TableName(value = "erp_brand")
@ApiModel("品牌表实体类")
public class Brand extends BaseGeneralInfo {

    @TableField(value = "img")
    @ApiModelProperty(value = "背景图")
    private String img;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;

    @TableField(value = "logo")
    @ApiModelProperty(value = "logo图片", required = "required")
    private String logo;
}
