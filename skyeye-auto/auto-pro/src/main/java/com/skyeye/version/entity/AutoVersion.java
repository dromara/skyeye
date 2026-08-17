/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.version.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeTeamAuth;
import lombok.Data;

import com.skyeye.version.classenum.AutoVersionState;

/**
 * @ClassName: AutoVersion
 * @Description: 版本管理实体层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 9:04
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = "no", message = "版本号不能重复。")
@RedisCacheField(name = "auto:version", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName("auto_version")
@ApiModel(value = "版本实体类")
public class AutoVersion extends SkyeyeTeamAuth {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required")
    private String name;

    @TableField("remark")
    @ApiModelProperty("相关描述")
    private String remark;

    @TableField("no")
    @ApiModelProperty(value = "版本号", required = "required")
    private String no;

    @TableField("state")
    @ApiModelProperty(value = "状态", required = "required", enumClass = AutoVersionState.class)
    private String state;

}
