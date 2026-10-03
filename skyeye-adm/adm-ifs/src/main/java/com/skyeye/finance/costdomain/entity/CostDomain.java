package com.skyeye.finance.costdomain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.finance.costdomain.classenum.ValuationMethod;
import lombok.Data;

import java.util.Map;

@Data
@UniqueField
@RedisCacheField(name = "ifs:costDomain")
@TableName(value = "ifs_cost_domain", autoResultMap = true)
@ApiModel("成本域实体类")
public class CostDomain extends BaseGeneralInfo {

    @TableField("set_of_books_id")
    @ApiModelProperty(value = "账套id", required = "required")
    private String setOfBooksId;

    @TableField(exist = false)
    @Property("账套信息")
    private Map<String, Object> setOfBooksMation;

    @TableField("valuation_method")
    @ApiModelProperty(value = "计价方法", required = "required,num", enumClass = ValuationMethod.class)
    private Integer valuationMethod;

    @TableField("enabled")
    @ApiModelProperty(value = "状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;
}
