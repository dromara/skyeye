/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.schedule.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: AutoScheduleTaskHistory
 * @Description: 定时任务执行记录实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/7/24
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "auto_schedule_task_history")
@ApiModel(value = "定时任务执行记录实体类")
public class AutoScheduleTaskHistory extends CommonInfo {

    @TableId("id")
    @Property(value = "主键id")
    private String id;

    @TableField("schedule_task_id")
    @Property(value = "定时任务id")
    private String scheduleTaskId;

    @TableField(value = "`name`")
    @Property(value = "任务名称快照")
    private String name;

    @TableField("object_id")
    @Property(value = "所属项目id")
    private String objectId;

    @TableField("object_key")
    @Property(value = "所属项目key")
    private String objectKey;

    @TableField("execute_type")
    @Property(value = "执行范围")
    private Integer executeType;

    @TableField("execute_result")
    @Property(value = "执行结果")
    private Integer executeResult;

    @TableField("total_num")
    @Property(value = "用例总数")
    private Integer totalNum;

    @TableField("success_num")
    @Property(value = "成功数")
    private Integer successNum;

    @TableField("fail_num")
    @Property(value = "失败数")
    private Integer failNum;

    @TableField("success_rate")
    @Property(value = "成功率（0-1，保留两位小数）")
    private Double successRate;

    @TableField("execute_start_time")
    @Property(value = "执行开始时间")
    private String executeStartTime;

    @TableField("execute_end_time")
    @Property(value = "执行结束时间")
    private String executeEndTime;

    @TableField("execute_time")
    @Property(value = "执行耗时毫秒")
    private String executeTime;

}
