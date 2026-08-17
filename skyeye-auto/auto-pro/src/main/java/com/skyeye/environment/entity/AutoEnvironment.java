/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.environment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeTeamAuth;
import lombok.Data;

/**
 * @ClassName: AutoEnvironment
 * @Description: 环境管理实体层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 8:38
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"objectId", "name"})
@RedisCacheField(name = "auto:environment", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "auto_environment", autoResultMap = true)
@ApiModel("环境实体类")
public class AutoEnvironment extends SkyeyeTeamAuth implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField("remark")
    @ApiModelProperty("相关描述")
    private String remark;

    @TableField("type")
    @ApiModelProperty(value = "环境类型", required = "required")
    private String type;

}

