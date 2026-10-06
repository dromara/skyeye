package com.skyeye.finance.report.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

@Data
@ApiModel("往来账龄")
public class CounterpartAging extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(exist = false)
    @ApiModelProperty(value = "往来单位id")
    private String partnerId;

    @TableField(exist = false)
    @ApiModelProperty(value = "往来单位")
    private String partnerName;

    @TableField(exist = false)
    @ApiModelProperty(value = "余额")
    private String balance;

    @TableField(exist = false)
    @ApiModelProperty(value = "最早发生日")
    private String oldestDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "账龄天数")
    private Long overdueDays;

    @TableField(exist = false)
    @ApiModelProperty(value = "0-30天")
    private String d0_30;

    @TableField(exist = false)
    @ApiModelProperty(value = "31-60天")
    private String d31_60;

    @TableField(exist = false)
    @ApiModelProperty(value = "61-90天")
    private String d61_90;

    @TableField(exist = false)
    @ApiModelProperty(value = "90天以上")
    private String d90p;
}
