package com.skyeye.scheduling.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "check_work_scheduling_time", autoResultMap = true)
@ApiModel("排班时间段表实体类")
public class SchedulingTime extends BaseGeneralInfo {

    @TableField(value = "start_time")
    @ApiModelProperty(value = "班次开始时间(格式 HH:mm:ss)")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "班次结束时间(格式 HH:mm:ss)")
    private String endTime;

    @TableField(value = "is_next_day")
    @ApiModelProperty(value = "是否跨天", enumClass = WhetherEnum.class)
    private Integer isNextDay;

    @TableField(value = "color")
    @ApiModelProperty(value = "时间段颜色")
    private String color;

    @TableField(value = "min_staff")
    @ApiModelProperty(value = "最小需求人数")
    private Integer minStaff;

    @TableField(value = "max_staff")
    @ApiModelProperty(value = "最大需求人数")
    private Integer maxStaff;

    @TableField(value = "scheduling_id")
    @ApiModelProperty(value = "排班id")
    private String schedulingId;

    @TableField(exist = false)
    @ApiModelProperty(value = "排班时间段下的工位信息")
    private List<SchedulingTimeWork> schedulingTimeWorkMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前员工在该时间段下的工位名称")
    private List<String> workStationNameList;

}
