package com.skyeye.scheduling.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.scheduling.classenum.SchedulePublishState;
import com.skyeye.scheduling.classenum.ScheduleType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("员工排班表")
@TableName(value = "check_work_scheduling", autoResultMap = true)
public class Scheduling extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "shift_id")
    @ApiModelProperty(value = "班次Id", required = "required")
    private String shiftId;

    @TableField(value = "schedule_type")
    @ApiModelProperty(value = "排班状态 1 自动 2 手动", enumClass = ScheduleType.class)
    private Integer scheduleType;

    @TableField(value = "publish_state")
    @ApiModelProperty(value = "发布状态", enumClass = SchedulePublishState.class)
    private Integer publishState;

    @TableField(value = "farm_id")
    @ApiModelProperty(value = "车间id", required = "required")
    private String farmId;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "排班开始时间（年月日）")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "排班结束时间（年月日）")
    private String endTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "排班时间段")
    private List<SchedulingTime> schedulingTimeMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "班次信息")
    private SchedulingShifts shiftMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "车间信息")
    private Map<String, Object> farmMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "排班下员工信息")
    private List<SchedulingTimeWorkPeople> sedulingTimeWorkPeopleMation;

}
