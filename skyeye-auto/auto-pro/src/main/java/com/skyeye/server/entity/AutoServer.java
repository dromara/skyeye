/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.entity;

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
import com.skyeye.server.classenum.AutoServerOnlineStatusEnum;
import com.skyeye.server.classenum.AutoServerProbeTypeEnum;
import lombok.Data;

/**
 * @ClassName: AutoServer
 * @Description: 服务器管理实体层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 9:00
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField({"objectId", "ip"})
@RedisCacheField(name = "auto:server", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "auto_server", autoResultMap = true)
@ApiModel(value = "服务器实体类")
public class AutoServer extends SkyeyeTeamAuth implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required")
    private String name;

    @TableField("remark")
    @ApiModelProperty("相关描述")
    private String remark;

    @TableField("ip")
    @ApiModelProperty(value = "服务器ip", required = "required")
    private String ip;

    @TableField("cpu")
    @ApiModelProperty(value = "服务器cpu")
    private String cpu;

    @TableField("disk")
    @ApiModelProperty(value = "服务器磁盘")
    private String disk;

    @TableField("mem")
    @ApiModelProperty(value = "服务器分配信息")
    private String mem;

    @TableField("environment_id")
    @ApiModelProperty(value = "环境id", required = "required")
    private String environmentId;

    @TableField("probe_type")
    @ApiModelProperty(value = "探测类型", enumClass = AutoServerProbeTypeEnum.class)
    private String probeType;

    @TableField("probe_port")
    @ApiModelProperty(value = "探测端口；ping 默认 22，http 默认 80，https 默认 443")
    private Integer probePort;

    @TableField("probe_path")
    @ApiModelProperty(value = "HTTP/HTTPS 探测路径，默认 /")
    private String probePath;

    @TableField("probe_timeout")
    @ApiModelProperty(value = "探测超时毫秒，默认 3000")
    private Integer probeTimeout;

    @TableField("online_status")
    @ApiModelProperty(value = "在线状态", enumClass = AutoServerOnlineStatusEnum.class)
    private String onlineStatus;

    @TableField("last_probe_time")
    @ApiModelProperty(value = "最近探测时间")
    private String lastProbeTime;

    @TableField("last_latency")
    @ApiModelProperty(value = "最近探测耗时毫秒")
    private Integer lastLatency;

    @TableField("last_probe_msg")
    @ApiModelProperty(value = "最近探测说明")
    private String lastProbeMsg;

    @TableField("ssh_enabled")
    @ApiModelProperty(value = "是否启用SSH资源采集，1启用0关闭")
    private Integer sshEnabled;

    @TableField("ssh_port")
    @ApiModelProperty(value = "SSH端口，默认22")
    private Integer sshPort;

    @TableField("ssh_user")
    @ApiModelProperty(value = "SSH登录用户名")
    private String sshUser;

    @TableField("ssh_password")
    @ApiModelProperty(value = "SSH登录密码；库内AES加密存储，编辑时留空表示不修改")
    private String sshPassword;

    @TableField("ssh_timeout")
    @ApiModelProperty(value = "SSH连接/执行超时毫秒，默认8000")
    private Integer sshTimeout;

    @TableField("cpu_usage")
    @ApiModelProperty(value = "最近CPU使用率%")
    private java.math.BigDecimal cpuUsage;

    @TableField("mem_usage")
    @ApiModelProperty(value = "最近内存使用率%")
    private java.math.BigDecimal memUsage;

    @TableField("disk_usage")
    @ApiModelProperty(value = "最近根分区磁盘使用率%")
    private java.math.BigDecimal diskUsage;

    @TableField("load_avg")
    @ApiModelProperty(value = "最近负载 load1/load5/load15")
    private String loadAvg;

    @TableField("metric_status")
    @ApiModelProperty(value = "资源采集状态：ok/fail/unknown")
    private String metricStatus;

    @TableField("last_metric_time")
    @ApiModelProperty(value = "最近资源采集时间")
    private String lastMetricTime;

    @TableField("last_metric_msg")
    @ApiModelProperty(value = "最近资源采集说明")
    private String lastMetricMsg;

    @TableField(exist = false)
    @Property(value = "环境信息")
    private AutoEnvironment environmentMation;

}
