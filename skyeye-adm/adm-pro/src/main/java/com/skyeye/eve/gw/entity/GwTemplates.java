/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.gw.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.eve.seal.entity.Seal;
import lombok.Data;

import com.skyeye.common.enumeration.EnableEnum;

/**
 * @ClassName: GwTemplates
 * @Description: 套红模板实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/25 10:44
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "gw:templates", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "gw_templates")
@ApiModel("套红模板实体类")
public class GwTemplates extends BaseGeneralInfo {

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;

    @TableField(value = "seal_id")
    @ApiModelProperty(value = "公章id", required = "required")
    private String sealId;

    @TableField(exist = false)
    @Property("公章信息")
    private Seal sealMation;

    @TableField(value = "red_head")
    @ApiModelProperty(value = "红头标题")
    private String redHead;

}
