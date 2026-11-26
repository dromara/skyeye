package com.skyeye.performance.assessment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName(value = "perf_assessment_rater_score")
@ApiModel("360评价人指标得分")
public class PerfAssessmentRaterScore extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("assessment_id")
    @ApiModelProperty(value = "考核单id", required = "required")
    private String assessmentId;

    @TableField("rater_id")
    @ApiModelProperty(value = "评价人记录id", required = "required")
    private String raterId;

    @TableField("scheme_item_id")
    @ApiModelProperty(value = "方案指标id")
    private String schemeItemId;

    @TableField("item_name")
    @ApiModelProperty(value = "指标名称")
    private String itemName;

    @TableField("score")
    @ApiModelProperty(value = "得分", required = "double")
    private BigDecimal score;

    @TableField("remark")
    @ApiModelProperty(value = "评语")
    private String remark;
}
