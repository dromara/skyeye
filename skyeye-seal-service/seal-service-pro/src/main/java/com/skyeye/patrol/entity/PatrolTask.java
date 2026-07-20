/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.patrol.classenum.PatrolTaskState;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: PatrolTask
 * @Description: 巡检任务实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "seal:patrol:task", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "crm_service_patrol_task")
@ApiModel("巡检任务实体类")
public class PatrolTask extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "odd_number")
    @Property(value = "任务编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "plan_id")
    @ApiModelProperty(value = "巡检计划ID", required = "required")
    private String planId;

    @TableField(exist = false)
    @Property(value = "计划信息")
    private PatrolPlan planMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "巡检点位ID列表")
    private List<String> pointId;

    @TableField(exist = false)
    @Property(value = "点位信息列表")
    private List<PatrolPoint> pointMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "巡检项目ID列表")
    private List<String> itemId;

    @TableField(exist = false)
    @Property(value = "项目信息列表")
    private List<PatrolItem> itemMation;

    @TableField(value = "executor_id")
    @ApiModelProperty(value = "执行人ID（员工ID）")
    private String executorId;

    @TableField(exist = false)
    @Property(value = "执行人信息")
    private Map<String, Object> executorMation;

    @TableField(value = "planned_start_time")
    @ApiModelProperty(value = "计划开始执行时间（对应actualStartTime）", required = "required")
    private String plannedStartTime;

    @TableField(value = "actual_start_time")
    @ApiModelProperty(value = "实际开始时间")
    private String actualStartTime;

    @TableField(value = "actual_end_time")
    @ApiModelProperty(value = "实际结束时间")
    private String actualEndTime;

    @TableField(value = "state")
    @Property(value = "任务状态", enumClass = PatrolTaskState.class)
    private Integer state;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

}

