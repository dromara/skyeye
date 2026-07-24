/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.team.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.constans.RedisConstants;
import lombok.Data;

import com.skyeye.common.enumeration.IsUsedEnum;

/**
 * @ClassName: TeamTemplate
 * @Description: 团队模板实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/7/30 0:21
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = CacheConstants.TEAM_TEMPLATE_CACHE_KEY, cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "team_template")
@ApiModel("团队模板实体类")
public class TeamTemplate extends AbstractTeam {

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField("`code`")
    @Property(value = "编码", fuzzyLike = true)
    private String code;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField("object_type")
    @ApiModelProperty(value = "该模板适用的对象类型，来源skyeye-pro#TeamObjectTypeEnum", required = "required,num")
    private Integer objectType;

    @TableField("enabled")
    @ApiModelProperty(value = "启用 1-启用  2-禁用", required = "required,num")
    private Integer enabled;

    @TableField("is_used")
    @Property(value = "是否使用中", enumClass = IsUsedEnum.class)
    private Integer isUsed;

}
