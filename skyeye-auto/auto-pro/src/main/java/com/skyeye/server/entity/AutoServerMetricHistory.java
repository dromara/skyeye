/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 服务器资源采集历史
 */
@Data
@TableName(value = "auto_server_metric_history")
@ApiModel("服务器资源采集历史")
public class AutoServerMetricHistory extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id")
    private String id;

    @TableField("server_id")
    @ApiModelProperty(value = "服务器id", required = "required")
    private String serverId;

    @TableField("object_id")
    @ApiModelProperty("项目id")
    private String objectId;

    @TableField("object_key")
    @ApiModelProperty("项目key")
    private String objectKey;

    @TableField("cpu_usage")
    @ApiModelProperty("CPU使用率%")
    private BigDecimal cpuUsage;

    @TableField("mem_usage")
    @ApiModelProperty("内存使用率%")
    private BigDecimal memUsage;

    @TableField("disk_usage")
    @ApiModelProperty("磁盘使用率%")
    private BigDecimal diskUsage;

    @TableField("load_avg")
    @ApiModelProperty("负载")
    private String loadAvg;

    @TableField("metric_status")
    @ApiModelProperty("采集状态 ok/fail")
    private String metricStatus;

    @TableField("metric_msg")
    @ApiModelProperty("采集说明")
    private String metricMsg;

    @TableField("collect_time")
    @ApiModelProperty("采集时间")
    private String collectTime;

    @TableField("create_time")
    @ApiModelProperty("创建时间")
    private String createTime;
}
