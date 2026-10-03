package com.skyeye.finance.period.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.finance.period.classenum.AccountPeriodState;
import lombok.Data;

import java.util.Map;

@Data
@RedisCacheField(name = "ifs:accountPeriod")
@TableName(value = "ifs_account_period", autoResultMap = true)
@ApiModel("会计期间实体类")
public class AccountPeriod extends BaseGeneralInfo {

    @TableField("set_of_books_id")
    @ApiModelProperty(value = "账套id", required = "required")
    private String setOfBooksId;

    @TableField(exist = false)
    @Property("账套信息")
    private Map<String, Object> setOfBooksMation;

    @TableField("`year`")
    @ApiModelProperty(value = "会计年度", required = "required,num")
    private Integer year;

    @TableField("`period`")
    @ApiModelProperty(value = "会计期间1-12", required = "required,num")
    private Integer period;

    @TableField("period_code")
    @ApiModelProperty(value = "期间编码YYYY-MM", required = "required")
    private String periodCode;

    @TableField("state")
    @ApiModelProperty(value = "状态", required = "required,num", enumClass = AccountPeriodState.class)
    private Integer state;

    @TableField("start_date")
    @ApiModelProperty(value = "开始日期")
    private String startDate;

    @TableField("end_date")
    @ApiModelProperty(value = "结束日期")
    private String endDate;
}
