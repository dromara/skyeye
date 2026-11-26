package com.skyeye.performance.cycle.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.performance.classenum.PerfCycleState;
import com.skyeye.performance.scheme.entity.PerfScheme;
import lombok.Data;

import java.util.Map;

@Data
@RedisCacheField(name = "perf:cycle")
@TableName(value = "perf_cycle")
@ApiModel("绩效考核周期实体类")
public class PerfCycle extends BaseGeneralInfo {

    @TableField("scheme_id")
    @ApiModelProperty(value = "方案id", required = "required")
    private String schemeId;

    @TableField(exist = false)
    @Property(value = "方案信息")
    private PerfScheme schemeMation;

    @TableField("start_time")
    @ApiModelProperty(value = "开始日期", required = "required")
    private String startTime;

    @TableField("end_time")
    @ApiModelProperty(value = "结束日期", required = "required")
    private String endTime;

    @TableField("cycle_state")
    @ApiModelProperty(value = "周期状态", enumClass = PerfCycleState.class, required = "required")
    private String cycleState;

    @TableField(exist = false)
    @Property(value = "周期状态信息")
    private Map<String, Object> cycleStateMation;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;
}
