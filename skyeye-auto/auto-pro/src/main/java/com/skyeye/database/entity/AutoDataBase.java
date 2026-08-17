/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.database.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.SkyeyeTeamAuth;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DataBase
 * @Description: 数据库实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/28 12:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "auto:database")
@TableName(value = "auto_database", autoResultMap = true)
@ApiModel("数据库实体类")
public class AutoDataBase extends SkyeyeTeamAuth {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField("remark")
    @ApiModelProperty("相关描述")
    private String remark;

    @TableField(value = "jdbc_url")
    @ApiModelProperty(value = "数据源连接字符串(JDBC)", required = "required")
    private String jdbcUrl;

    @TableField(value = "user")
    @ApiModelProperty(value = "数据源登录用户名", required = "required")
    private String user;

    @TableField(value = "password")
    @ApiModelProperty(value = "数据源登录密码")
    private String password;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据类型", required = "required")
    private String dataType;

    @TableField(value = "queryer_class")
    @Property(value = "获取报表引擎查询器类名")
    private String queryerClass;

    @TableField(value = "driver_class")
    @Property(value = "数据源驱动类")
    private String driverClass;

    @TableField(exist = false)
    @ApiModelProperty(value = "报表引擎查询器使用的数据源连接池类型", required = "required")
    private String poolClassType;

    @TableField(value = "pool_class")
    @Property(value = "报表引擎查询器使用的数据源连接池类名")
    private String poolClass;

    @TableField(exist = false)
    @Property(value = "报表引擎查询器使用的数据源连接池名称")
    private String poolClassName;

    @TableField(value = "options", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "数据源配置选项(JSON格式）")
    private List<Map<String, Object>> options;

}
