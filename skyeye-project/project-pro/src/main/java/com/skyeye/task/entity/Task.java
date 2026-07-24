/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.task.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.base.handler.enclosure.bean.Enclosure;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.milestone.entity.Milestone;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.skyeye.task.classenum.TaskImported;

/**
 * @ClassName: Task
 * @Description: 任务实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/1 15:52
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@UniqueField(value = {"objectId", "name"})
@RedisCacheField(name = "pm:task", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "pro_task", autoResultMap = true)
@ApiModel("任务实体类")
public class Task extends SkyeyeFlowable {

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "type_id")
    @ApiModelProperty(value = "任务分类，数据来源数据字典", required = "required")
    private String typeId;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key", required = "required")
    private String objectKey;

    @TableField(value = "department_id")
    @ApiModelProperty(value = "所属部门id", required = "required")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "所属部门信息")
    private Map<String, Object> departmentMation;

    @TableField("start_time")
    @ApiModelProperty(value = "计划开始时间", required = "required")
    private String startTime;

    @TableField("end_time")
    @ApiModelProperty(value = "计划结束时间", required = "required")
    private String endTime;

    @TableField(value = "perform_id", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "执行人ID")
    private List<String> performId;

    @TableField(exist = false)
    @Property(value = "执行人")
    private List<Map<String, Object>> performMation;

    @TableField(value = "estimated_workload")
    @ApiModelProperty(value = "预估工作量")
    private String estimatedWorkload;

    @TableField(value = "actual_workload")
    @ApiModelProperty(value = "实际工作量")
    private String actualWorkload;

    @TableField(value = "task_instructions")
    @ApiModelProperty(value = "任务说明", required = "required")
    private String taskInstructions;

    @TableField(value = "execution_result")
    @ApiModelProperty(value = "执行结果")
    private String executionResult;

    @TableField(exist = false)
    @ApiModelProperty(value = "执行结果附件", required = "json")
    private Enclosure executionEnclosureInfo;

    @TableField("imported")
    @ApiModelProperty(value = "重要性", required = "required", enumClass = TaskImported.class)
    private String imported;

    @TableField(value = "milestone_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "里程碑id")
    private String milestoneId;

    @TableField(exist = false)
    @Property(value = "里程碑信息")
    private Milestone milestoneMation;

    @TableField(value = "parent_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "父节点id")
    private String parentId;

}
