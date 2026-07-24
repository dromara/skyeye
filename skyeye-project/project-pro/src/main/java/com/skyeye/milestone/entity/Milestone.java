/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.milestone.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.skyeye.milestone.classenum.MilestoneImported;

/**
 * @ClassName: Milestone
 * @Description: 里程碑管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/14 20:14
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"objectId", "name"})
@RedisCacheField(name = "pm:milestone", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "pro_milestone", autoResultMap = true)
@ApiModel("里程碑管理实体类")
public class Milestone extends SkyeyeFlowable {

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required")
    private String name;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key", required = "required")
    private String objectKey;

    @TableField("start_time")
    @ApiModelProperty(value = "开始时间", required = "required")
    private String startTime;

    @TableField("end_time")
    @ApiModelProperty(value = "结束时间", required = "required")
    private String endTime;

    @TableField(value = "remark")
    @ApiModelProperty(value = "相关描述")
    private String remark;

    @TableField(value = "responsible_id", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "负责人ID")
    private List<String> responsibleId;

    @TableField(exist = false)
    @Property(value = "负责人")
    private List<Map<String, Object>> responsibleMation;

    @TableField(value = "total_workload")
    @Property(value = "总工作量(小时)")
    private String totalWorkload;

    @TableField(value = "completed_workload")
    @Property(value = "已完成工作量(小时）")
    private String completedWorkload;

    @TableField("imported")
    @ApiModelProperty(value = "重要性", required = "required", enumClass = MilestoneImported.class)
    private String imported;

    @TableField(exist = false)
    private String pId;

}
