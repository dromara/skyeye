/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.projectconfig.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeTeamAuth;
import com.skyeye.common.enumeration.IsDefaultEnum;
import lombok.Data;

/**
 * 自动化项目配置（按项目一条）。
 */
@Data
@UniqueField(value = {"objectId"})
@RedisCacheField(name = "auto:projectConfig", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "auto_project_config", autoResultMap = true)
@ApiModel("自动化项目配置")
public class AutoProjectConfig extends SkyeyeTeamAuth {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("enable_estimate_time")
    @ApiModelProperty(value = "是否可设置需求预计开始/结束时间", enumClass = IsDefaultEnum.class, required = "required,num")
    private Integer enableEstimateTime;

    @TableField("enable_score_allocate")
    @ApiModelProperty(value = "是否开启需求积分分配", enumClass = IsDefaultEnum.class, required = "required,num")
    private Integer enableScoreAllocate;

    @TableField("remark")
    @ApiModelProperty("备注")
    private String remark;

}
