package com.skyeye.performance.assessment.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.performance.cycle.entity.PerfCycle;
import com.skyeye.performance.scheme.entity.PerfScheme;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@RedisCacheField(name = "perf:assessment", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "perf_assessment")
@ApiModel("员工绩效考核单实体类")
public class PerfAssessment extends SkyeyeFlowable {

    @TableField("name")
    @ApiModelProperty(value = "考核单名称", fuzzyLike = true)
    private String name;

    @TableField(value = "cycle_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "周期id", required = "required")
    private String cycleId;

    @TableField(exist = false)
    @Property(value = "周期信息")
    private PerfCycle cycleMation;

    @TableField(value = "scheme_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "方案id", required = "required")
    private String schemeId;

    @TableField(exist = false)
    @Property(value = "方案信息")
    private PerfScheme schemeMation;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "员工id", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "员工业务key")
    private String objectKey;

    @TableField(exist = false)
    @Property(value = "员工信息")
    private Map<String, Object> objectMation;

    @TableField("total_score")
    @ApiModelProperty(value = "总分", required = "double")
    private BigDecimal totalScore;

    @TableField("grade")
    @ApiModelProperty(value = "等级")
    private String grade;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "考核明细", required = "json")
    private List<PerfAssessmentDetail> assessmentDetailList;
}
