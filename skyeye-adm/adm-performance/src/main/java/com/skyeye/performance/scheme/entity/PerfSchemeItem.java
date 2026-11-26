package com.skyeye.performance.scheme.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.performance.classenum.PerfItemSourceType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@TableName(value = "perf_scheme_item")
@ApiModel("绩效考核方案指标实体类")
public class PerfSchemeItem extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("scheme_id")
    @Property(value = "方案id")
    private String schemeId;

    @TableField("name")
    @ApiModelProperty(value = "指标名称", required = "required")
    private String name;

    @TableField("source_type")
    @ApiModelProperty(value = "指标来源", enumClass = PerfItemSourceType.class, required = "required")
    private String sourceType;

    @TableField(exist = false)
    @Property(value = "指标来源信息")
    private Map<String, Object> sourceTypeMation;

    @TableField("source_config")
    @ApiModelProperty(value = "来源参数，问卷填问卷ID；也可存JSON如 {\"surveyId\":\"xxx\"}")
    private String sourceConfig;

    @TableField("weight")
    @ApiModelProperty(value = "权重(%)", required = "required,double")
    private BigDecimal weight;

    @TableField("remark")
    @ApiModelProperty(value = "说明")
    private String remark;

    @TableField("order_by")
    @ApiModelProperty(value = "排序", required = "required,num")
    private Integer orderBy;
}
