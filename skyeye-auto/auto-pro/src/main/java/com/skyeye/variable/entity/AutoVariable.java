/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.variable.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeTeamAuth;
import lombok.Data;

import com.skyeye.variable.classenum.AutoVariableType;

/**
 * @ClassName: AutoVariable
 * @Description: 变量管理实体层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 9:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "auto:variable", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName("auto_variable")
@ApiModel(value = "变量实体类")
public class AutoVariable extends SkyeyeTeamAuth {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`key`")
    @ApiModelProperty(value = "键", required = "required")
    private String key;

    @TableField("`value`")
    @ApiModelProperty(value = "值", required = "required")
    private String value;

    @TableField("type")
    @ApiModelProperty(value = "类型", required = "required", enumClass = AutoVariableType.class)
    private String type;

    @TableField("remark")
    @ApiModelProperty("备注")
    private String remark;

    @TableField("environment_id")
    @ApiModelProperty(value = "环境id", required = "required")
    private String environmentId;
}