/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.microservice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeTeamAuth;
import com.skyeye.environment.entity.AutoEnvironment;
import com.skyeye.server.entity.AutoServer;
import lombok.Data;

/**
 * @ClassName: AutoMicroservice
 * @Description: 微服务实体层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 8:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "auto:microservice", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "auto_microservice", autoResultMap = true)
@ApiModel(value = "微服务实体类")
public class AutoMicroservice extends SkyeyeTeamAuth implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required")
    private String name;

    @TableField("remark")
    @ApiModelProperty("相关描述")
    private String remark;

    @TableField("protocol")
    @ApiModelProperty(value = "协议", required = "required")
    private String protocol;

    @TableField("port")
    @ApiModelProperty(value = "访问端口", required = "required")
    private String port;

    @TableField("path")
    @ApiModelProperty(value = "基础路径")
    private String path;

    @TableField("environment_id")
    @ApiModelProperty(value = "环境id", required = "required")
    private String environmentId;

    @TableField(exist = false)
    @Property(value = "环境信息")
    private AutoEnvironment environmentMation;

    @TableField("server_id")
    @ApiModelProperty(value = "服务id", required = "required")
    private String serverId;

    @TableField(exist = false)
    @Property(value = "服务信息")
    private AutoServer serverMation;
}
