package com.skyeye.performance.scheme.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.enumeration.ApplicableObjectsType;
import lombok.Data;

import java.util.Map;

@Data
@TableName(value = "perf_scheme_applicable_objects")
@ApiModel("绩效考核方案适用对象实体类")
public class PerfSchemeApplicableObjects extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("scheme_id")
    @Property(value = "方案id")
    private String schemeId;

    @TableField("object_id")
    @ApiModelProperty(value = "适用对象id", required = "required")
    private String objectId;

    @TableField(exist = false)
    @Property(value = "适用对象信息")
    private Map<String, Object> objectMation;

    @TableField("object_type")
    @ApiModelProperty(value = "适用对象类型", required = "required,num", enumClass = ApplicableObjectsType.class)
    private Integer objectType;
}
