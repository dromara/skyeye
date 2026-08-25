/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.knowledge.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.ScheduleFrequency;
import lombok.Data;

import java.util.List;

/**
 * AI 知识库。按租户隔离，可供多个 AI 角色绑定，每个角色只从绑定库取数。
 */
@Data
@UniqueField
@TableName(value = "skyeye_ai_knowledge")
@ApiModel("AI知识库")
public class Knowledge extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "知识库名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "descr")
    @ApiModelProperty(value = "描述")
    private String descr;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(value = "jdbc_url")
    @ApiModelProperty(value = "同步数据库 JDBC 地址")
    private String jdbcUrl;

    @TableField(value = "jdbc_user")
    @ApiModelProperty(value = "同步数据库用户名")
    private String jdbcUser;

    @TableField(value = "jdbc_password")
    @ApiModelProperty(value = "同步数据库密码")
    private String jdbcPassword;

    @TableField(value = "driver_class")
    @ApiModelProperty(value = "JDBC 驱动类")
    private String driverClass;

    @TableField(value = "frequency")
    @ApiModelProperty(value = "同步频次", enumClass = ScheduleFrequency.class, required = "required,num")
    private Integer frequency;

    @TableField(value = "execute_time")
    @ApiModelProperty(value = "执行时间（格式：HH:mm）")
    private String executeTime;

    @TableField(value = "week_days")
    @ApiModelProperty(value = "每周执行日期（1-7，多个用逗号分隔）")
    private String weekDays;

    @TableField(value = "month_days")
    @ApiModelProperty(value = "每月执行日期（1-31，多个用逗号分隔）")
    private String monthDays;

    @TableField(value = "custom_cron")
    @ApiModelProperty(value = "自定义 Cron 表达式")
    private String customCron;

    @TableField(value = "last_sync_time")
    @Property("最近同步时间")
    private String lastSyncTime;

    @TableField(value = "tenant_id", updateStrategy = FieldStrategy.NEVER)
    @Property("租户id")
    private String tenantId;

    @TableField(exist = false)
    @ApiModelProperty(value = "同步表配置列表")
    private List<KnowledgeSync> syncList;

}
