package com.skyeye.performance.assessment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.performance.classenum.PerfRaterRole;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "perf_assessment_rater")
@ApiModel("360评价人")
public class PerfAssessmentRater extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("assessment_id")
    @ApiModelProperty(value = "考核单id", required = "required")
    private String assessmentId;

    @TableField("rater_staff_id")
    @ApiModelProperty(value = "评价人员工id", required = "required")
    private String raterStaffId;

    @TableField(exist = false)
    @Property(value = "评价人信息")
    private Map<String, Object> raterStaffMation;

    @TableField("rater_role")
    @ApiModelProperty(value = "评价角色", enumClass = PerfRaterRole.class, required = "required")
    private String raterRole;

    @TableField(exist = false)
    @Property(value = "评价角色信息")
    private Map<String, Object> raterRoleMation;

    @TableField("weight")
    @ApiModelProperty(value = "权重(%)", required = "required,double")
    private BigDecimal weight;

    @TableField("state")
    @ApiModelProperty(value = "状态 draft/done")
    private String state;

    @TableField(exist = false)
    @ApiModelProperty(value = "该评价人的打分明细", required = "json")
    private List<PerfAssessmentRaterScore> scoreList;
}
