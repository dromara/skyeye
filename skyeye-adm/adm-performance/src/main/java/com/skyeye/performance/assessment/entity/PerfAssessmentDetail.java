package com.skyeye.performance.assessment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.performance.classenum.PerfItemSourceType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@TableName(value = "perf_assessment_detail")
@ApiModel("员工绩效考核明细实体类")
public class PerfAssessmentDetail extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("assessment_id")
    @Property(value = "考核单id")
    private String assessmentId;

    @TableField("scheme_item_id")
    @ApiModelProperty(value = "方案指标id")
    private String schemeItemId;

    @TableField("name")
    @ApiModelProperty(value = "指标名称", required = "required")
    private String name;

    @TableField("source_type")
    @ApiModelProperty(value = "指标来源", enumClass = PerfItemSourceType.class)
    private String sourceType;

    @TableField(exist = false)
    @Property(value = "指标来源信息")
    private Map<String, Object> sourceTypeMation;

    @TableField("source_config")
    @ApiModelProperty(value = "来源参数（从方案指标带入）")
    private String sourceConfig;

    @TableField("weight")
    @ApiModelProperty(value = "权重(%)", required = "required,double")
    private BigDecimal weight;

    @TableField("score")
    @ApiModelProperty(value = "得分", required = "double")
    private BigDecimal score;

    @TableField("weighted_score")
    @ApiModelProperty(value = "加权得分", required = "double")
    private BigDecimal weightedScore;

    @TableField("remark")
    @ApiModelProperty(value = "评语")
    private String remark;

    @TableField("order_by")
    @ApiModelProperty(value = "排序", required = "num")
    private Integer orderBy;
}
