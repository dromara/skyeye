package com.skyeye.performance.scheme.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.performance.classenum.PerfCycleType;
import com.skyeye.performance.classenum.PerfSchemeType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@UniqueField
@RedisCacheField(name = "perf:scheme")
@TableName(value = "perf_scheme")
@ApiModel("绩效考核方案实体类")
public class PerfScheme extends BaseGeneralInfo {

    @TableField("scheme_type")
    @ApiModelProperty(value = "方案类型", enumClass = PerfSchemeType.class, required = "required")
    private String schemeType;

    @TableField(exist = false)
    @Property(value = "方案类型信息")
    private Map<String, Object> schemeTypeMation;

    @TableField("cycle_type")
    @ApiModelProperty(value = "周期类型", enumClass = PerfCycleType.class, required = "required")
    private String cycleType;

    @TableField(exist = false)
    @Property(value = "周期类型信息")
    private Map<String, Object> cycleTypeMation;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField("full_score")
    @ApiModelProperty(value = "满分", required = "required,double")
    private BigDecimal fullScore;

    @TableField("grade_rule")
    @ApiModelProperty(value = "等级规则JSON，例：[{\"min\":90,\"max\":100,\"grade\":\"A\"}]")
    private String gradeRule;

    @TableField("objective")
    @ApiModelProperty(value = "OKR 目标描述（Objective）")
    private String objective;

    @TableField("rater_rule")
    @ApiModelProperty(value = "360评价人权重JSON，例：[{\"role\":\"self\",\"weight\":20},{\"role\":\"superior\",\"weight\":50}]")
    private String raterRule;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "适用对象", required = "json")
    private List<PerfSchemeApplicableObjects> applicableObjectsList;

    @TableField(exist = false)
    @ApiModelProperty(value = "考核指标", required = "required,json")
    private List<PerfSchemeItem> schemeItemList;
}
