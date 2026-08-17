/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeTeamAuth;
import com.skyeye.environment.entity.AutoEnvironment;
import com.skyeye.microservice.entity.AutoMicroservice;
import com.skyeye.module.entity.AutoModule;
import com.skyeye.server.entity.AutoServer;
import lombok.Data;

/**
 * @ClassName: AutoApi
 * @Description: 接口管理实体层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 8:32
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "auto:api", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName("auto_api")
@ApiModel(value = "接口实体类")
public class AutoApi extends SkyeyeTeamAuth {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField("address")
    @ApiModelProperty(value = "接口地址", required = "required", fuzzyLike = true)
    private String address;

    @TableField("remark")
    @ApiModelProperty("相关描述")
    private String remark;

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

    @TableField("microservice_id")
    @ApiModelProperty(value = "微服务id", required = "required")
    private String microserviceId;

    @TableField(exist = false)
    @Property(value = "微服务信息")
    private AutoMicroservice microserviceMation;

    @TableField("request_way")
    @ApiModelProperty(value = "请求方式", required = "required")
    private String requestWay;

    @TableField("input_example")
    @ApiModelProperty(value = "入参示例")
    private String inputExample;

    @TableField("output_example")
    @ApiModelProperty(value = "出参示例")
    private String outputExample;

    @TableField("module_id")
    @ApiModelProperty(value = "所属模块id")
    private String moduleId;

    @TableField(exist = false)
    @Property(value = "模块信息")
    private AutoModule moduleMation;

}
